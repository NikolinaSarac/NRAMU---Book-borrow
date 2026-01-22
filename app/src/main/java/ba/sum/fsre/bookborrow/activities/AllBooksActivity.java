package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.adapters.BooksAdapter;
import ba.sum.fsre.bookborrow.api.ApiCallback;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClientService;

public class AllBooksActivity extends BaseActivity {

    private RecyclerView rvBooks;
    private TextView tvEmpty;
    private com.google.android.material.textfield.TextInputEditText etSearch;

    private BooksAdapter adapter;
    private final List<JsonObject> booksList = new ArrayList<>();

    private AuthManager authManager;

    private static final int PAGE_SIZE = 5;
    private int offset = 0;
    private boolean isLoading = false;
    private boolean reachedEnd = false;
    private String currentQuery = "";

    private android.widget.ProgressBar pbLoading;
    private TextView tvEnd;

    private final android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_MS = 350;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_books_activity);

        rvBooks = findViewById(R.id.rvBooks);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        pbLoading = findViewById(R.id.pbLoading);
        tvEnd = findViewById(R.id.tvEnd);

        if (pbLoading != null) pbLoading.setVisibility(View.GONE);
        if (tvEnd != null) tvEnd.setVisibility(View.GONE);

        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BooksAdapter(booksList, false, true);
        rvBooks.setAdapter(adapter);

        rvBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                updateEndFooterVisibility();

                if (dy <= 0) return;
                if (isLoading || reachedEnd) return;

                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm == null) return;

                int total = lm.getItemCount();
                int lastVisible = lm.findLastVisibleItemPosition();

                if (total > 0 && lastVisible >= total - 3) {
                    loadBooks(false);
                }
            }
        });

        if (etSearch != null) {
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(android.text.Editable s) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String q = s != null ? s.toString().trim() : "";
                    searchHandler.removeCallbacks(searchRunnable);

                    searchRunnable = () -> {
                        currentQuery = q;
                        loadBooks(true);
                    };

                    searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
                }
            });
        }

        setupBottomNav(R.id.nav_all_books);
        authManager = new AuthManager(this);

        loadBooks(true);
    }

    private void loadBooks(boolean reset) {
        if (authManager == null) authManager = new AuthManager(this);

        if (reset) {
            offset = 0;
            reachedEnd = false;
            booksList.clear();
            adapter.setData(booksList);
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            if (tvEnd != null) tvEnd.setVisibility(View.GONE);
        }

        if (isLoading || reachedEnd) {
            updateEndFooterVisibility();
            return;
        }

        String token = authManager.getToken();
        if (token == null || token.isEmpty()) {
            if (pbLoading != null) pbLoading.setVisibility(View.GONE);
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("No auth token.");
            }
            return;
        }

        String myUserId = authManager.getUserId();
        if (myUserId == null || myUserId.isEmpty()) {
            if (pbLoading != null) pbLoading.setVisibility(View.GONE);
            if (tvEmpty != null) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("No user id.");
            }
            return;
        }

        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        if (tvEnd != null) tvEnd.setVisibility(View.GONE);

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        isLoading = true;

        boolean hasQuery = currentQuery != null && !currentQuery.trim().isEmpty();
        String userFilter = "neq." + myUserId;

        if (!hasQuery) {
            RetrofitClientService.getInstance()
                    .getApi()
                    .getAllBooksWithAvailableStatusPaged(authHeader, "*", userFilter, PAGE_SIZE, offset)
                    .enqueue(new ApiCallback<List<JsonObject>>() {
                        @Override
                        public void onSuccess(List<JsonObject> response) {
                            handlePageSuccess(response, false);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            handlePageError(errorMessage);
                        }
                    });
        } else {
            String safe = currentQuery.replace("*", "").replace("(", "").replace(")", "").replace(",", "").trim();
            String orFilter = "(name.ilike.*" + safe + "*,author.ilike.*" + safe + "*)";

            RetrofitClientService.getInstance()
                    .getApi()
                    .searchBooksWithAvailableStatusPaged(authHeader, "*", userFilter, orFilter, PAGE_SIZE, offset)
                    .enqueue(new ApiCallback<List<JsonObject>>() {
                        @Override
                        public void onSuccess(List<JsonObject> response) {
                            handlePageSuccess(response, true);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            handlePageError(errorMessage);
                        }
                    });
        }
    }

    private void handlePageSuccess(List<JsonObject> response, boolean searchMode) {
        isLoading = false;
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);

        if (response == null || response.isEmpty()) {
            if (offset == 0) {
                if (tvEmpty != null) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText(searchMode ? "No results." : "No available books.");
                }
            } else {
                reachedEnd = true;
            }

            rvBooks.post(this::updateEndFooterVisibility);
            return;
        }

        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);

        booksList.addAll(response);
        adapter.setData(booksList);

        offset += response.size();
        if (response.size() < PAGE_SIZE) reachedEnd = true;

        rvBooks.post(this::updateEndFooterVisibility);
    }

    private void handlePageError(String errorMessage) {
        isLoading = false;
        if (pbLoading != null) pbLoading.setVisibility(View.GONE);

        if (offset == 0 && tvEmpty != null) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(errorMessage);
        }
    }

    private void clearSearchFocus() {
        if (etSearch == null) return;

        etSearch.clearFocus();

        android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
        if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof com.google.android.material.textfield.TextInputEditText) {
                int[] out = new int[2];
                v.getLocationOnScreen(out);
                float x = ev.getRawX() + v.getLeft() - out[0];
                float y = ev.getRawY() + v.getTop() - out[1];

                if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                    clearSearchFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateEndFooterVisibility() {
        if (tvEnd == null || rvBooks == null) return;

        if (!reachedEnd) {
            tvEnd.setVisibility(View.GONE);
            return;
        }

        RecyclerView.LayoutManager lm0 = rvBooks.getLayoutManager();
        if (!(lm0 instanceof LinearLayoutManager)) {
            tvEnd.setVisibility(View.VISIBLE);
            return;
        }

        LinearLayoutManager lm = (LinearLayoutManager) lm0;
        int total = lm.getItemCount();
        int lastVisible = lm.findLastVisibleItemPosition();

        if (total <= 0) {
            tvEnd.setVisibility(View.GONE);
            return;
        }

        if (lastVisible == RecyclerView.NO_POSITION) {
            tvEnd.setVisibility(View.VISIBLE);
            return;
        }

        tvEnd.setVisibility(lastVisible >= total - 1 ? View.VISIBLE : View.GONE);
    }
}
