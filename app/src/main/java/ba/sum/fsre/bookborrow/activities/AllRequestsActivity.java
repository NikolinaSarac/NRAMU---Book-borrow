package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.ui.requests.RequestsFragment;

public class AllRequestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_requests_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new RequestsFragment())
                    .commit();
        }
    }
}
