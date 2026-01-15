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

    private static final String ARG_SHOW_ACTIONS = "ARG_SHOW_ACTIONS";
    private static final String ARG_SHOW_REQUEST = "ARG_SHOW_REQUEST";

    private RecyclerView recyclerView;
    private BooksAdapter adapter;
    private final List<JsonObject> books = new ArrayList<>();

    private boolean showActions = false;
    private boolean showRequest = false;

    public BooksFragment() { }

    // koristi ovo kad kreiraš fragment
    public static BooksFragment newInstance(boolean showActions, boolean showRequest) {
        BooksFragment f = new BooksFragment();
        Bundle b = new Bundle();
        b.putBoolean(ARG_SHOW_ACTIONS, showActions);
        b.putBoolean(ARG_SHOW_REQUEST, showRequest);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            showActions = getArguments().getBoolean(ARG_SHOW_ACTIONS, false);
            showRequest = getArguments().getBoolean(ARG_SHOW_REQUEST, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_books, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // adapter koristi flagove iz fragmenta
        adapter = new BooksAdapter(books, showActions, showRequest);
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

    public BooksAdapter getAdapter() {
        return adapter;
    }
}
