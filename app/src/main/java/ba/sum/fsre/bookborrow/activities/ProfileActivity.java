package ba.sum.fsre.bookborrow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.api.ApiCallback;
import ba.sum.fsre.bookborrow.fragments.ActiveBorrowsFragment;
import ba.sum.fsre.bookborrow.fragments.BooksFragment;
import ba.sum.fsre.bookborrow.fragments.BorrowHistoryFragment;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClient;
import ba.sum.fsre.bookborrow.utils.RetrofitClientService;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";

    private static final String TAG_BOOKS = "TAB_BOOKS";
    private static final String TAG_ACTIVE = "TAB_ACTIVE";
    private static final String TAG_HISTORY = "TAB_HISTORY";

    private TextView tvUsername, tvEmail;
    private Button btnLogout;
    private AuthManager authManager;

    private BooksFragment booksFragment;
    private ActiveBorrowsFragment activeBorrowsFragment;
    private BorrowHistoryFragment borrowHistoryFragment;

    // tab "Vlastite knjige" (dolazi iz books tabele, ima image_url)
    private final List<JsonObject> myBooksList = new ArrayList<>();

    // ove dvije ostaju iz profila
    private final List<JsonObject> cachedActive = new ArrayList<>();
    private final List<JsonObject> cachedHistory = new ArrayList<>();

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authManager = new AuthManager(this);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> logout());

        // Kreiraj fragmente jednom
        booksFragment = new BooksFragment();
        activeBorrowsFragment = new ActiveBorrowsFragment();
        borrowHistoryFragment = new BorrowHistoryFragment();

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Vlastite knjige"));
        tabLayout.addTab(tabLayout.newTab().setText("Aktivne posudbe"));
        tabLayout.addTab(tabLayout.newTab().setText("Povijest posudbi"));

        // Default tab
        replaceFragment(booksFragment, TAG_BOOKS);
        booksFragment.setBooks(myBooksList);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab.getPosition());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                showTab(tab.getPosition());
            }
        });

        // username/email + active/history
        loadProfile();

        //povuci iz books tabele (ima image_url)
        loadMyBooks();

        setupBottomNav(R.id.nav_profile);
    }

    private void showTab(int position) {
        if (position == 0) {
            replaceFragment(booksFragment, TAG_BOOKS);


            loadMyBooks();

            // prikazi trenutno cache-anu listu (update će doći kad API vrati)
            booksFragment.setBooks(myBooksList);

        } else if (position == 1) {
            replaceFragment(activeBorrowsFragment, TAG_ACTIVE);
            activeBorrowsFragment.setActiveBorrows(cachedActive);

        } else if (position == 2) {
            replaceFragment(borrowHistoryFragment, TAG_HISTORY);
            borrowHistoryFragment.setBorrowHistory(cachedHistory);
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit();
    }

    //
    private void loadProfile() {
        SupabaseAuthService service =
                RetrofitClient.getClient().create(SupabaseAuthService.class);

        JsonObject body = new JsonObject();

        service.getUserProfile(
                "Bearer " + authManager.getToken(),
                body
        ).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.e(TAG, "HTTP CODE: " + response.code());
                Log.e(TAG, "MESSAGE: " + response.message());

                if (response.errorBody() != null) {
                    try {
                        Log.e(TAG, "ERROR BODY: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    JsonObject profile = response.body();

                    tvUsername.setText(profile.has("username") && !profile.get("username").isJsonNull()
                            ? profile.get("username").getAsString()
                            : "");

                    tvEmail.setText(profile.has("email") && !profile.get("email").isJsonNull()
                            ? profile.get("email").getAsString()
                            : "");

                    // --- ACTIVE BORROWS ---
                    List<JsonObject> activeList = new ArrayList<>();
                    if (profile.has("active_borrows") && !profile.get("active_borrows").isJsonNull()) {
                        JsonArray activeArray = profile.getAsJsonArray("active_borrows");
                        for (int i = 0; i < activeArray.size(); i++) {
                            activeList.add(activeArray.get(i).getAsJsonObject());
                        }
                    }

                    cachedActive.clear();
                    cachedActive.addAll(activeList);
                    activeBorrowsFragment.setActiveBorrows(cachedActive);

                    // --- BORROW HISTORY ---
                    List<JsonObject> historyList = new ArrayList<>();
                    if (profile.has("borrow_history") && !profile.get("borrow_history").isJsonNull()) {
                        JsonArray historyArray = profile.getAsJsonArray("borrow_history");
                        for (int i = 0; i < historyArray.size(); i++) {
                            historyList.add(historyArray.get(i).getAsJsonObject());
                        }
                    }

                    cachedHistory.clear();
                    cachedHistory.addAll(historyList);
                    borrowHistoryFragment.setBorrowHistory(cachedHistory);

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Failed to load profile", Toast.LENGTH_SHORT).show();
                }

                //
                Log.e(TAG, "USER ID = " + authManager.getUserId());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(ProfileActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // povlači knjige iz books tabele, gdje image_url postoji
    private void loadMyBooks() {
        String token = authManager.getToken();
        if (token == null || token.isEmpty()) return;

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        String userId = authManager.getUserId();
        if (userId == null || userId.isEmpty()) return;

        RetrofitClientService.getInstance()
                .getApi()
                .getMyBooks(authHeader, "*", "eq." + userId)
                .enqueue(new ApiCallback<List<JsonObject>>() {
                    @Override
                    public void onSuccess(List<JsonObject> response) {
                        myBooksList.clear();
                        myBooksList.addAll(response);

                        // Debug prvih par image_url
                        for (int i = 0; i < Math.min(5, myBooksList.size()); i++) {
                            JsonObject b = myBooksList.get(i);
                            String url = (b.has("image_url") && !b.get("image_url").isJsonNull())
                                    ? b.get("image_url").getAsString()
                                    : "null";
                            Log.e("MY_BOOKS_DEBUG", i + " image_url=" + url);
                        }

                        booksFragment.setBooks(myBooksList);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "loadMyBooks error: " + errorMessage);
                    }
                });
    }

    private void logout() {
        authManager.logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
