package ba.sum.fsre.bookborrow.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;



import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.activities.BookDetailsActivity;
import ba.sum.fsre.bookborrow.activities.EditBookActivity;
import androidx.fragment.app.FragmentActivity;
import ba.sum.fsre.bookborrow.fragments.RequestBookDialogFragment;


public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private final List<JsonObject> books;
    private final boolean showActions; // edit/delete
    private final boolean showRequest; // request button

    private final List<JsonObject> allBooks = new ArrayList<>();


    public BooksAdapter(List<JsonObject> books, boolean showActions, boolean showRequest) {
        this.books = books;
        this.showActions = showActions;
        this.showRequest = showRequest;
        allBooks.clear();
        allBooks.addAll(this.books);

        setHasStableIds(true);
    }

    public void setData(List<JsonObject> newBooks) {
        List<JsonObject> copy = new ArrayList<>();
        if (newBooks != null) copy.addAll(newBooks);

        books.clear();
        allBooks.clear();

        books.addAll(copy);
        allBooks.addAll(copy);

        notifyDataSetChanged();
    }
    //za serach
    public void filter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        books.clear();

        if (q.isEmpty()) {
            books.addAll(allBooks);
        } else {
            for (JsonObject book : allBooks) {
                String name = (book.has("name") && !book.get("name").isJsonNull())
                        ? book.get("name").getAsString()
                        : "";

                String author = (book.has("author") && !book.get("author").isJsonNull())
                        ? book.get("author").getAsString()
                        : "";

                if (name.toLowerCase(Locale.ROOT).contains(q) || author.toLowerCase(Locale.ROOT).contains(q)) {
                    books.add(book);
                }
            }
        }

        notifyDataSetChanged();
    }



    // ===== DELETE CALLBACK =====
    public interface OnDeleteClickListener {
        void onDelete(String bookId);
    }

    private OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener l) {
        this.onDeleteClickListener = l;
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

        String bookId = (book.has("id") && !book.get("id").isJsonNull())
                ? book.get("id").getAsString()
                : null;

        String ownerId = (book.has("user_id") && !book.get("user_id").isJsonNull())
                ? book.get("user_id").getAsString()
                : null;

        String name = book.has("name") && !book.get("name").isJsonNull()
                ? book.get("name").getAsString()
                : "Unknown title";

        String author = book.has("author") && !book.get("author").isJsonNull()
                ? book.get("author").getAsString()
                : "Unknown author";

        String description = book.has("description") && !book.get("description").isJsonNull()
                ? book.get("description").getAsString()
                : "";

        String availabilityStatus = null;
        boolean isAvailable;

        if (book.has("availability_status") && !book.get("availability_status").isJsonNull()) {
            availabilityStatus = book.get("availability_status").getAsString();
        }

        String imageUrl = book.has("image_url") && !book.get("image_url").isJsonNull()
                ? book.get("image_url").getAsString()
                : null;

        holder.tvTitle.setText(name);
        holder.tvAuthor.setText(author);

        if ("available".equals(availabilityStatus)) {
            holder.tvStatus.setText("Available");
            isAvailable = true;

            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_available);

        } else {
            holder.tvStatus.setText("Borrowed");
            isAvailable = false;
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#C62828"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_borrowed);

            holder.root.setBackgroundResource(R.drawable.card_book_borrowed);
        }

        // ===== IMAGE (sprječava krive slike) =====
        Glide.with(holder.itemView.getContext()).clear(holder.ivBookImage);
        holder.ivBookImage.setImageResource(R.drawable.ic_book);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(holder.ivBookImage);
        }

        // ===== ACTIONS (Edit/Delete) – samo MyBooks =====
        if (holder.actionsContainer != null) {
            holder.actionsContainer.setVisibility(showActions ? View.VISIBLE : View.GONE);
        }

        if (showActions && bookId != null) {

            if (holder.btnEdit != null) {
                holder.btnEdit.setOnClickListener(v -> {
                    Intent i = new Intent(v.getContext(), EditBookActivity.class);
                    i.putExtra(EditBookActivity.EXTRA_BOOK_ID, bookId);
                    i.putExtra(EditBookActivity.EXTRA_NAME, name);
                    i.putExtra(EditBookActivity.EXTRA_AUTHOR, author);
                    i.putExtra(EditBookActivity.EXTRA_DESCRIPTION, description);
                    i.putExtra(EditBookActivity.EXTRA_IMAGE_URL, imageUrl);

                    v.getContext().startActivity(i);
                });

                holder.btnEdit.setClickable(true);
            }

            if (holder.btnDelete != null) {
                holder.btnDelete.setOnClickListener(v -> {
                    if (onDeleteClickListener != null) onDeleteClickListener.onDelete(bookId);
                });

                holder.btnDelete.setClickable(true);
            }

        } else {
            // bitno zbog reciklaže view-a
            if (holder.btnEdit != null) holder.btnEdit.setOnClickListener(null);
            if (holder.btnDelete != null) holder.btnDelete.setOnClickListener(null);
        }

        // ===== REQUEST BUTTON – samo AllBooks =====
        if (holder.btnRequest != null) {
            // request ima smisla samo ako je showRequest i knjiga je available
            boolean shouldShowRequest = showRequest && isAvailable;

            holder.btnRequest.setVisibility(shouldShowRequest ? View.VISIBLE : View.GONE);

            if (shouldShowRequest && bookId != null && ownerId != null) {
                holder.btnRequest.setOnClickListener(v -> {
                    Context context = v.getContext();
                    FragmentActivity activity = null;
                    Log.d("BooksAdapter", "Request button clicked for bookId=" + bookId);


                    if (context instanceof FragmentActivity) {
                        activity = (FragmentActivity) context;
                    } else if (context instanceof ContextWrapper) {
                        Context base = ((ContextWrapper) context).getBaseContext();
                        if (base instanceof FragmentActivity) activity = (FragmentActivity) base;
                    }

                    if (activity == null) {
                        Log.e("BooksAdapter", "Cannot find FragmentActivity to show dialog");
                        return;
                    }

                    RequestBookDialogFragment dialog = RequestBookDialogFragment.newInstance(bookId, ownerId);

                    // Postavi dijalog u full width da se vidi
                    dialog.setStyle(DialogFragment.STYLE_NORMAL, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                    dialog.show(activity.getSupportFragmentManager(), "RequestBookDialog");
                });


            } else {
                holder.btnRequest.setOnClickListener(null);
            }
        }

        // ===== ITEM CLICK – otvori details =====
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookDetailsActivity.class);
            intent.putExtra(BookDetailsActivity.EXTRA_NAME, name);
            intent.putExtra(BookDetailsActivity.EXTRA_AUTHOR, author);
            intent.putExtra(BookDetailsActivity.EXTRA_DESCRIPTION, description);
            intent.putExtra(BookDetailsActivity.EXTRA_AVAILABLE, isAvailable);
            intent.putExtra("image_url", imageUrl);

            // korisno za details/request kasnije:
            intent.putExtra("BOOK_ID", bookId);
            intent.putExtra("OWNER_ID", ownerId);

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : 0;
    }

    // ✅ stabilni ID-i i za UUID (String)
    @Override
    public long getItemId(int position) {
        JsonObject book = books.get(position);
        if (book.has("id") && !book.get("id").isJsonNull()) {
            String idStr = book.get("id").getAsString();
            return idStr.hashCode();
        }
        return position;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAuthor, tvStatus;
        ImageView ivBookImage;

        ImageView btnEdit, btnDelete;
        View actionsContainer;

        Button btnRequest;

        LinearLayout root;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvStatus = itemView.findViewById(R.id.tvBookStatus);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);

            actionsContainer = itemView.findViewById(R.id.actionsContainer);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnRequest = itemView.findViewById(R.id.btnRequest);
            root = itemView.findViewById(R.id.bookCardRoot);
        }
    }
}
