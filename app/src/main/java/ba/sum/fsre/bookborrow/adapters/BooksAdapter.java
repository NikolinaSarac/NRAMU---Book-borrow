package ba.sum.fsre.bookborrow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import ba.sum.fsre.bookborrow.R;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private List<JsonObject> books;

    public BooksAdapter(List<JsonObject> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        JsonObject book = books.get(position);
        holder.tvTitle.setText(book.get("title").getAsString());
        holder.tvAuthor.setText(book.get("author").getAsString());
        String status = book.has("available") && book.get("available").getAsBoolean() ? "Available" : "Borrowed";
        holder.tvStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvStatus;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvStatus = itemView.findViewById(R.id.tvBookStatus);
        }
    }
}
