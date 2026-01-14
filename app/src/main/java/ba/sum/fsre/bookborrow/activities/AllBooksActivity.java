package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    private BooksAdapter adapter;
    private final List<JsonObject> booksList = new ArrayList<>();

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_books_activity);

        rvBooks = findViewById(R.id.rvBooks);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvBooks.setLayoutManager(new LinearLayoutManager(this));

        // u AllBooks sakrivamo edit/delete
        adapter = new BooksAdapter(booksList, false, true);
        rvBooks.setAdapter(adapter);

        setupBottomNav(R.id.nav_all_books);

        authManager = new AuthManager(this);

        loadBooks();
    }

    private void loadBooks() {
        String token = authManager.getToken();
        if (token == null || token.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No auth token.");
            return;
        }

        String myUserId = authManager.getUserId();
        if (myUserId == null || myUserId.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No user id.");
            return;
        }

        // Ako je token već spremljen sa "Bearer ", nemoj duplirati
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        RetrofitClientService.getInstance()
                .getApi()
                // filtriraj da ne dobiješ svoje knjige
                .getAllBooks(authHeader, "*", "neq." + myUserId)
                .enqueue(new ApiCallback<List<JsonObject>>() {
                    @Override
                    public void onSuccess(List<JsonObject> response) {
                        booksList.clear();
                        booksList.addAll(response);
                        adapter.notifyDataSetChanged();

                        tvEmpty.setVisibility(booksList.isEmpty() ? View.VISIBLE : View.GONE);
                        if (booksList.isEmpty()) tvEmpty.setText("No available books.");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("AllBooksActivity", "Error: " + errorMessage);
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText(errorMessage);
                    }
                });
    }
}
