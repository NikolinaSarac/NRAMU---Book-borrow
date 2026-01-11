package ba.sum.fsre.bookborrow.ui.requests;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ba.sum.fsre.bookborrow.models.RequestBook;
import ba.sum.fsre.bookborrow.repository.BookRepository;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ba.sum.fsre.bookborrow.R;

public class ReceivedRequestsFragment extends Fragment {


    private RecyclerView recyclerView;
    private ReceivedRequestsAdapter adapter;
    private BookRepository bookRepository;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_received_requests, container, false);

        recyclerView = view.findViewById(R.id.rvReceivedRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<RequestBook> requestList = new ArrayList<>();
        adapter = new ReceivedRequestsAdapter(getContext(), requestList, new ReceivedRequestsAdapter.OnRequestUpdatedListener() {
            @Override
            public void onRequestUpdated() {
                loadRequests();
            }
        });

        recyclerView.setAdapter(adapter);
        bookRepository = new BookRepository(getContext());

        loadRequests();

        return view;
    }

    private void loadRequests() {
        AuthManager authManager = new AuthManager(getContext());
        String currentUserId = authManager.getUserId();

        bookRepository.getMyReceivedRequest(
                currentUserId,
                "*,book:books(name,author)"
        ).enqueue(new Callback<List<RequestBook>>() {
            @Override
            public void onResponse(Call<List<RequestBook>> call, Response<List<RequestBook>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.getRequests().clear();
                    adapter.getRequests().addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("ReceivedRequests", "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<List<RequestBook>> call, Throwable t) {
                Log.e("ReceivedRequests", "API call failed", t);
            }
        });
    }
}
