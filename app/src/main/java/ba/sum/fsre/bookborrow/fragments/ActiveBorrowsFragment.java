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

public class ActiveBorrowsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BorrowsAdapter adapter;
    private List<JsonObject> activeBorrowsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_borrows, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewActive);
        adapter = new BorrowsAdapter(activeBorrowsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void setActiveBorrows(List<JsonObject> newBorrows) {
        if (adapter != null) {
            activeBorrowsList.clear();
            activeBorrowsList.addAll(newBorrows);
            adapter.notifyDataSetChanged();
        }
    }
}
