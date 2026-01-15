package ba.sum.fsre.bookborrow.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import ba.sum.fsre.bookborrow.R;

public class BaseActivity extends AppCompatActivity {

    protected void setupBottomNav(int selectedItemId) {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == selectedItemId) return true;

            if (item.getItemId() == R.id.nav_profile)
                startActivity(new Intent(this, ProfileActivity.class));

            if (item.getItemId() == R.id.nav_requests)
                startActivity(new Intent(this, AllRequestsActivity.class));

            if (item.getItemId() == R.id.nav_all_books)
                startActivity(new Intent(this, AllBooksActivity.class));

            if (item.getItemId() == R.id.nav_add_book)
                startActivity(new Intent(this, AddBookActivity.class));
            return true;
        });
    }
}
