package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ba.sum.fsre.bookborrow.repository.BookRepository;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRequestActivity extends AppCompatActivity {
    AuthManager authManager;
    BookRepository repository;
    String bookId,ownerId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = new AuthManager(this);
        repository = new BookRepository(this);

        bookId = getIntent().getStringExtra("BOOK_ID");
        ownerId = getIntent().getStringExtra("OWNER_ID");

        Log.d("BookRequest", "Book ID: " + bookId);
        Log.d("BookRequest", "Owner ID: " + ownerId);

        sendRequest();
    }

    private void sendRequest()
    {
        String requesterId = authManager.getUserId();

        repository.sendBookRequest(bookId, ownerId, requesterId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(BookRequestActivity.this,
                                    "Request sent",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e("BookRequest", "Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(BookRequestActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
