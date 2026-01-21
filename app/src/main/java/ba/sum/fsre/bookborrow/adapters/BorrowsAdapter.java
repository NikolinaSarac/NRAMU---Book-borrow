package ba.sum.fsre.bookborrow.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.fragments.BorrowRequestDetailsDialogFragment;
import ba.sum.fsre.bookborrow.models.Profile;
import ba.sum.fsre.bookborrow.repository.BookRepository;
import ba.sum.fsre.bookborrow.repository.UserRepository;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;

public class BorrowsAdapter extends RecyclerView.Adapter<BorrowsAdapter.BorrowViewHolder> {

    private List<JsonObject> borrows;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final Context context;

    public BorrowsAdapter(Context context,List<JsonObject> borrows) {
        this.context = context;
        this.borrows = borrows;
        this.userRepository = new UserRepository(context);
        this.bookRepository = new BookRepository(context);
    }

    @NonNull
    @Override
    public BorrowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_borrow, parent, false);
        return new BorrowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowViewHolder holder, int position) {
        JsonObject borrow = borrows.get(position);
        holder.tvStatus.setText(
                borrow.has("status") && !borrow.get("status").isJsonNull()
                        ? borrow.get("status").getAsString()
                        : "-"
        );

        if (borrow.has("book") && borrow.get("book").isJsonObject()) {
            JsonObject book = borrow.getAsJsonObject("book");

            holder.tvBook.setText(
                    book.has("name") && !book.get("name").isJsonNull()
                            ? book.get("name").getAsString()
                            : "-"
            );
        } else {
            holder.tvBook.setText("-");
        }

        if (borrow.has("owner_id") && !borrow.get("owner_id").isJsonNull()) {
            String ownerId = borrow.get("owner_id").getAsString();

            userRepository.getUserProfile(ownerId)
                    .enqueue(new Callback<List<Profile>>() {
                        @Override
                        public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                Profile profile = response.body().get(0);
                                holder.tvOwner.setText(profile.getUsername());
                            } else {
                                holder.tvOwner.setText("Owner");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Profile>> call, Throwable t) {
                            holder.tvOwner.setText("Owner");
                        }
                    });

        } else {
            holder.tvOwner.setText("-");
        }


        JsonObject book = borrow.getAsJsonObject("book");
        String imageUrl = book.has("image_url") && !book.get("image_url").isJsonNull()
                ? book.get("image_url").getAsString()
                : null;

        Glide.with(holder.itemView.getContext()).clear(holder.ivBookImage);
        holder.ivBookImage.setImageResource(R.drawable.ic_book);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(holder.ivBookImage);
        }
        String currentUserId = userRepository.getCurrentUserId();
        String ownerId = borrow.has("owner_id") && !borrow.get("owner_id").isJsonNull()
                ? borrow.get("owner_id").getAsString() : "";

        if (currentUserId.equals(ownerId)) {
            holder.btnEditBorrow.setVisibility(View.VISIBLE);
        } else {
            holder.btnEditBorrow.setVisibility(View.GONE);
        }

        // --- 2️⃣ Klik na edit → AlertDialog (novi kod)
        holder.btnEditBorrow.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Mark as Completed")
                    .setMessage("Do you want to mark this borrow as completed?")
                    .setPositiveButton("Yes", (dialog, which) -> markBorrowCompleted(borrow, position))
                    .setNegativeButton("No", null)
                    .show();
        });
        holder.itemView.setOnClickListener(v -> {
            FragmentActivity activity = null;
            Context ctx = v.getContext();

            if (ctx instanceof FragmentActivity) {
                activity = (FragmentActivity) ctx;
            } else if (ctx instanceof ContextWrapper) {
                Context base = ((ContextWrapper) ctx).getBaseContext();
                if (base instanceof FragmentActivity) activity = (FragmentActivity) base;
            }

            if (activity == null) return;

            BorrowRequestDetailsDialogFragment dialog =
                    BorrowRequestDetailsDialogFragment.newInstance(borrow);
            dialog.show(activity.getSupportFragmentManager(), "BorrowRequestDetails");
        });



    }

    private void markBorrowCompleted(JsonObject borrow, int position) {
        String borrowId = borrow.has("id") ? borrow.get("id").getAsString() : null;
        if (borrowId == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("status", "completed");

        bookRepository.updateBookRequestStatus(borrowId, body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            borrows.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Borrow marked as completed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return borrows.size();
    }

    static class BorrowViewHolder extends RecyclerView.ViewHolder {
        TextView tvBook, tvStatus, tvOwner;
        ImageView ivBookImage;
        ImageButton btnEditBorrow;
        public BorrowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBook = itemView.findViewById(R.id.tvBorrowBook);
            tvStatus = itemView.findViewById(R.id.tvBorrowStatus);
            tvOwner = itemView.findViewById(R.id.tvBorrowOwner);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            btnEditBorrow = itemView.findViewById(R.id.btnEditBorrow);
        }
    }
}
