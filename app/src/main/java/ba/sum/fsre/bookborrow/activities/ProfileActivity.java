package ba.sum.fsre.bookborrow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.adapters.BooksAdapter;
import ba.sum.fsre.bookborrow.adapters.BorrowsAdapter;
import ba.sum.fsre.bookborrow.fragments.ActiveBorrowsFragment;
import ba.sum.fsre.bookborrow.fragments.BooksFragment;
import ba.sum.fsre.bookborrow.fragments.BorrowHistoryFragment;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClient;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail;
    private Button btnLogout;
    private AuthManager authManager;

    private BooksFragment booksFragment;
    private ActiveBorrowsFragment activeBorrowsFragment;
    private BorrowHistoryFragment borrowHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authManager = new AuthManager(this);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> logout());

        booksFragment = new BooksFragment();
        activeBorrowsFragment = new ActiveBorrowsFragment();
        borrowHistoryFragment = new BorrowHistoryFragment();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Vlastite knjige"));
        tabLayout.addTab(tabLayout.newTab().setText("Aktivne posudbe"));
        tabLayout.addTab(tabLayout.newTab().setText("Povijest posudbi"));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, booksFragment)
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = booksFragment; // default
                switch (tab.getPosition()) {
                    case 0: selected = booksFragment; break;
                    case 1: selected = activeBorrowsFragment; break;
                    case 2: selected = borrowHistoryFragment; break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadProfile();
    }

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

                Log.e("ProfileActivity", "HTTP CODE: " + response.code());
                Log.e("ProfileActivity", "MESSAGE: " + response.message());

                if (response.errorBody() != null) {
                    try {
                        Log.e("ProfileActivity", "ERROR BODY: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    JsonObject profile = response.body();

                    tvUsername.setText(profile.get("username").getAsString());
                    tvEmail.setText(profile.get("email").getAsString());

<<<<<<< Updated upstream
                    List<JsonObject> booksList = new ArrayList<>();
                    JsonArray booksArray = profile.getAsJsonArray("books");
                    for (int i = 0; i < booksArray.size(); i++) {
                        booksList.add(booksArray.get(i).getAsJsonObject());
                    }
                    booksFragment.setBooks(booksList);

                    List<JsonObject> activeList = new ArrayList<>();
                    JsonArray activeArray = profile.getAsJsonArray("active_borrows");
                    for (int i = 0; i < activeArray.size(); i++) {
                        activeList.add(activeArray.get(i).getAsJsonObject());
                    }
                    activeBorrowsFragment.setActiveBorrows(activeList);

                    List<JsonObject> historyList = new ArrayList<>();
                    JsonArray historyArray = profile.getAsJsonArray("borrow_history");
                    for (int i = 0; i < historyArray.size(); i++) {
                        historyList.add(historyArray.get(i).getAsJsonObject());
                    }
                    borrowHistoryFragment.setBorrowHistory(historyList);

=======
>>>>>>> Stashed changes
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
                Log.e("ProfileActivity", "TOKEN = " + authManager.getToken());
                Log.e("ProfileActivity", "USER ID = " + authManager.getUserId());
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
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
