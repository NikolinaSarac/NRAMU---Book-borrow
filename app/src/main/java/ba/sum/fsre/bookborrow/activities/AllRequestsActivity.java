package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.ui.requests.ReceivedRequestsFragment;
import ba.sum.fsre.bookborrow.ui.requests.SentRequestsFragment;

public class AllRequestsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_requests_activity);

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ReceivedRequestsFragment receivedFragment = new ReceivedRequestsFragment();
        SentRequestsFragment sentFragment = new SentRequestsFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, receivedFragment)
                    .commit();
        }

        tabLayout.addTab(tabLayout.newTab().setText("Primljeni"));
        tabLayout.addTab(tabLayout.newTab().setText("Poslani"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = receivedFragment;
                if (tab.getPosition() == 1) selected = sentFragment;

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupBottomNav(R.id.nav_requests);
    }
}
