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

public class BorrowsAdapter extends RecyclerView.Adapter<BorrowsAdapter.BorrowViewHolder> {

    private List<JsonObject> borrows;

    public BorrowsAdapter(List<JsonObject> borrows) {
        this.borrows = borrows;
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
        holder.tvBook.setText(borrow.get("title").getAsString());
        holder.tvOwner.setText(borrow.get("owner").getAsString());
        holder.tvStatus.setText(borrow.get("status").getAsString());
    }

    @Override
    public int getItemCount() {
        return borrows.size();
    }

    static class BorrowViewHolder extends RecyclerView.ViewHolder {
        TextView tvBook, tvStatus, tvOwner;

        public BorrowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBook = itemView.findViewById(R.id.tvBorrowBook);
            tvStatus = itemView.findViewById(R.id.tvBorrowStatus);
            tvOwner = itemView.findViewById(R.id.tvBorrowOwner);
        }
    }
}
