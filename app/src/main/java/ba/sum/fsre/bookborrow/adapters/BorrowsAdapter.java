package ba.sum.fsre.bookborrow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.fragments.BorrowDetailsDialogFragment;
import ba.sum.fsre.bookborrow.models.Profile;
import ba.sum.fsre.bookborrow.repository.UserRepository;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;

public class BorrowsAdapter extends RecyclerView.Adapter<BorrowsAdapter.BorrowViewHolder> {

    private List<JsonObject> borrows;
    private final UserRepository userRepository;
    private final Context context;

    public BorrowsAdapter(Context context,List<JsonObject> borrows) {
        this.context = context;
        this.borrows = borrows;
        this.userRepository = new UserRepository(context);
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
        holder.tvPickupDate.setText(borrow.has("pickup_date") && !borrow.get("pickup_date").isJsonNull()
                ? "Datum: " + borrow.get("pickup_date").getAsString() : "Datum: -");

        holder.tvPickupTime.setText(borrow.has("pickup_time") && !borrow.get("pickup_time").isJsonNull()
                ? "Vrijeme: " + borrow.get("pickup_time").getAsString() : "Vrijeme: -");

        holder.tvLocation.setText(borrow.has("location") && !borrow.get("location").isJsonNull()
                ? "Lokacija: " + borrow.get("location").getAsString() : "Lokacija: -");

        holder.tvDescription.setText(borrow.has("description") && !borrow.get("description").isJsonNull()
                ? "Opis: " + borrow.get("description").getAsString() : "Opis: -");

        holder.borrowDetailsContainer.setVisibility(View.GONE);

        boolean hasDetails =
                (borrow.has("pickup_date") && !borrow.get("pickup_date").isJsonNull()) ||
                        (borrow.has("pickup_time") && !borrow.get("pickup_time").isJsonNull()) ||
                        (borrow.has("location") && !borrow.get("location").isJsonNull()) ||
                        (borrow.has("description") && !borrow.get("description").isJsonNull());

        holder.tvToggleDetails.setVisibility(hasDetails ? View.VISIBLE : View.GONE);

        holder.tvToggleDetails.setOnClickListener(v -> {
            if (holder.borrowDetailsContainer.getVisibility() == View.VISIBLE) {
                holder.borrowDetailsContainer.setVisibility(View.GONE);
            } else {
                holder.borrowDetailsContainer.setVisibility(View.VISIBLE);
                String currentUserId = new AuthManager(context).getUserId();
                String ownerId = borrow.has("owner_id") && !borrow.get("owner_id").isJsonNull()
                        ? borrow.get("owner_id").getAsString() : "";
                holder.btnAddDetails.setVisibility(currentUserId.equals(ownerId) ? View.VISIBLE : View.GONE);
            }
        });

        String currentUserId = new AuthManager(context).getUserId();
        String ownerId = borrow.has("owner_id") && !borrow.get("owner_id").isJsonNull() ? borrow.get("owner_id").getAsString() : "";
        holder.btnAddDetails.setVisibility(currentUserId.equals(ownerId) ? View.VISIBLE : View.GONE);

        holder.btnAddDetails.setOnClickListener(v -> {
            BorrowDetailsDialogFragment dialog = BorrowDetailsDialogFragment.newInstance(
                    borrow.get("id").getAsString()
            );
            dialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "BorrowDetailsDialog");
        });

    }

    @Override
    public int getItemCount() {
        return borrows.size();
    }

    static class BorrowViewHolder extends RecyclerView.ViewHolder {
        TextView tvBook, tvStatus, tvOwner;
        ImageView ivBookImage;
        TextView tvPickupDate, tvPickupTime, tvLocation, tvDescription;
        LinearLayout borrowDetailsContainer;
        ImageView btnAddDetails;
        TextView tvToggleDetails;

        public BorrowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBook = itemView.findViewById(R.id.tvBorrowBook);
            tvStatus = itemView.findViewById(R.id.tvBorrowStatus);
            tvOwner = itemView.findViewById(R.id.tvBorrowOwner);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            tvPickupDate = itemView.findViewById(R.id.tvPickupDate);
            tvPickupTime = itemView.findViewById(R.id.tvPickupTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            borrowDetailsContainer = itemView.findViewById(R.id.borrowDetailsContainer);
            btnAddDetails = itemView.findViewById(R.id.btnAddDetails);
            tvToggleDetails = itemView.findViewById(R.id.tvToggleDetails);

        }
    }
}
