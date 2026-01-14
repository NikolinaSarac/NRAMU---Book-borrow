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
import ba.sum.fsre.bookborrow.adapters.BooksAdapter;

public class BooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private BooksAdapter adapter;
    private final List<JsonObject> books = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BooksAdapter(books, true, false);

        recyclerView.setAdapter(adapter);

        // ako su knjige već došle prije nego se view napravio
        adapter.notifyDataSetChanged();

        return view;
    }

    public void setBooks(List<JsonObject> newBooks) {
        books.clear();
        if (newBooks != null) books.addAll(newBooks);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
