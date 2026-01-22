package ba.sum.fsre.bookborrow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.adapters.BorrowsAdapter;

public class BorrowHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BorrowsAdapter adapter;
    private final List<JsonObject> borrowHistoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_borrow_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHistory);


        adapter = new BorrowsAdapter(requireContext(), borrowHistoryList, false);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void setBorrowHistory(List<JsonObject> newHistory) {
        borrowHistoryList.clear();
        if (newHistory != null) {
            borrowHistoryList.addAll(newHistory);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
