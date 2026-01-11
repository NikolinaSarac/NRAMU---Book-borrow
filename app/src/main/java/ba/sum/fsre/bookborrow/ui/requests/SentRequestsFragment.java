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

public class SentRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SentRequestsAdapter adapter;
    private BookRepository bookRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sent_requests, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rvSentRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<RequestBook> requestList = new ArrayList<>();
        adapter = new SentRequestsAdapter(requestList);
        recyclerView.setAdapter(adapter);

        bookRepository = new BookRepository(getContext());
        AuthManager authManager = new AuthManager(getContext());
        String currentUserId = authManager.getUserId();

        bookRepository.getMySentRequests(
                currentUserId,
                "*,book:books(name,author)"
        ).enqueue(new Callback<List<RequestBook>>() {
            @Override
            public void onResponse(Call<List<RequestBook>> call, Response<List<RequestBook>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    requestList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("SentRequests", "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<List<RequestBook>> call, Throwable t) {
                Log.e("SentRequests", "API call failed", t);
            }
        });

        return view;
    }
}
