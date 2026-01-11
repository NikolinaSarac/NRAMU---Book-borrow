package ba.sum.fsre.bookborrow.ui.requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ba.sum.fsre.bookborrow.models.RequestBook;
import ba.sum.fsre.bookborrow.repository.BookRepository;
import ba.sum.fsre.bookborrow.R;

public class ReceivedRequestsAdapter extends RecyclerView.Adapter<ReceivedRequestsAdapter.ViewHolder> {

    private final List<RequestBook> requests;
    private final Context context;

    public ReceivedRequestsAdapter(Context context,List<RequestBook> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_received_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestBook request = requests.get(position);
        if (request.getBook() != null) {
            holder.tvRequestBook.setText(request.getBook().getName());
            holder.tvRequestAuthor.setText(request.getBook().getAuthor());
        }

        holder.tvRequestUser.setText("Requester");
        holder.tvRequestStatus.setText(request.getStatus());

        holder.btnAccept.setOnClickListener(v -> {
            updateStatus(request, "approved", position);
        });

        holder.btnRejected.setOnClickListener(v -> {
            updateStatus(request, "rejected", position);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestUser;
        TextView tvRequestBook;
        TextView tvRequestAuthor;
        TextView tvRequestStatus;
        Button btnAccept;
        Button btnRejected;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestUser = itemView.findViewById(R.id.requestUser);
            tvRequestBook = itemView.findViewById(R.id.requestBook);
            tvRequestAuthor = itemView.findViewById(R.id.requestAuthor);
            tvRequestStatus = itemView.findViewById(R.id.requestStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnRejected = itemView.findViewById(R.id.btnRejected);
        }
    }

    private void updateStatus(RequestBook request, String status, int position) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        BookRepository bookRepository = new BookRepository(context);

        bookRepository.updateBookRequestStatus(request.getId(), body)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            request.setStatus(status);
                            notifyItemChanged(position);
                        } else {
                            android.util.Log.e("REQUEST", "Update failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        android.util.Log.e("REQUEST", "Network error", t);
                    }
                });
    }
}
