package ba.sum.fsre.bookborrow.ui.requests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import ba.sum.fsre.bookborrow.models.RequestBook;
import ba.sum.fsre.bookborrow.R;

public class SentRequestsAdapter extends RecyclerView.Adapter<SentRequestsAdapter.ViewHolder> {

    private final List<RequestBook> sentRequests;

    public SentRequestsAdapter(List<RequestBook> sentRequests) {
        this.sentRequests = sentRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sent_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestBook request = sentRequests.get(position);
        holder.tvSentRequestBook.setText(request.getBook().getName());
        holder.tvSentRequestAuthor.setText(request.getBook().getAuthor());
        holder.tvStatus.setText(request.getStatus());

        String imageUrl = request.getBook().getImageUrl();
        Glide.with(holder.itemView.getContext()).clear(holder.tvBookImage);
        holder.tvBookImage.setImageResource(R.drawable.ic_book);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(holder.tvBookImage);
        }
    }

    @Override
    public int getItemCount() {
        return sentRequests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSentRequestBook;
        TextView tvSentRequestAuthor;
        TextView tvStatus;
        ImageView tvBookImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSentRequestBook = itemView.findViewById(R.id.sentRequestBook);
            tvSentRequestAuthor = itemView.findViewById(R.id.sentRequestAuthor);
            tvStatus = itemView.findViewById(R.id.sentRequestStatus);
            tvBookImage = itemView.findViewById(R.id.sentRequestImage);
        }
    }
}
