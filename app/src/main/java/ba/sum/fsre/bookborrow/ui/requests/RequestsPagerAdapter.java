package ba.sum.fsre.bookborrow.ui.requests;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RequestsPagerAdapter extends FragmentStateAdapter {

    public RequestsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ReceivedRequestsFragment();
        } else {
            return new SentRequestsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
