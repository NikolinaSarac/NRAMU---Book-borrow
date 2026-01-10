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
    private List<JsonObject> books = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        adapter = new BooksAdapter(books);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void setBooks(List<JsonObject> newBooks) {
        if (adapter != null) {
            books.clear();
            books.addAll(newBooks);
            adapter.notifyDataSetChanged();
        }
    }
}
