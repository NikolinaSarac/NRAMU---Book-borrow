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

import ba.sum.fsre.bookborrow.models.Profile;
import ba.sum.fsre.bookborrow.models.RequestBook;
import ba.sum.fsre.bookborrow.repository.BookRepository;
import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceivedRequestsAdapter extends RecyclerView.Adapter<ReceivedRequestsAdapter.ViewHolder> {

    private final List<RequestBook> requests;
    private final Context context;

    private final UserRepository userRepository;

    public interface OnRequestUpdatedListener {
        void onRequestUpdated();
    }

    private final OnRequestUpdatedListener listener;

    public ReceivedRequestsAdapter(Context context,List<RequestBook> requests, OnRequestUpdatedListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
        this.userRepository = new UserRepository(context);
    }

    public List<RequestBook> getRequests() {
        return requests;
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

        userRepository.getUserProfile(request.getRequesterId())
                .enqueue(new Callback<List<Profile>>() {
                    @Override
                    public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Profile profile = response.body().get(0);
                            holder.tvRequestUser.setText(profile.getUsername());
                        } else {
                            holder.tvRequestUser.setText("Requester");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Profile>> call, Throwable t) {
                        holder.tvRequestUser.setText("Requester");
                    }
                });

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

                            if(listener != null){
                                listener.onRequestUpdated();
                            }

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
