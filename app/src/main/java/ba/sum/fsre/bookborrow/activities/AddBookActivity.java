package ba.sum.fsre.bookborrow.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.api.ApiCallback;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClientService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddBookActivity extends BaseActivity {

    private static final String SUPABASE_URL = "https://gvqwwllhdvsjtpodlruk.supabase.co";
    private static final String SUPABASE_API_KEY = "TU_STAVI_SVOJ_ANON_PUBLIC_KEY"; // isti kao u ostalim requestovima

    private TextInputEditText etName, etAuthor, etDescription;
    private Button btnCreate, btnPickImage;

    private Uri selectedImageUri;
    private ImageView ivPreview;

    private String token;
    private String userId;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (ivPreview != null) ivPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book_activity);

        etName = findViewById(R.id.etBookName);
        etAuthor = findViewById(R.id.etBookAuthor);
        etDescription = findViewById(R.id.etBookDescription);

        btnCreate = findViewById(R.id.btnCreateBook);

        ivPreview = findViewById(R.id.ivPreview);
        btnPickImage = findViewById(R.id.btnPickImage);


        // token + userId uzmi odmah jednom
        AuthManager auth = new AuthManager(this);
        token = auth.getToken();
        userId = auth.getUserId();

        if (token == null || token.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Not logged in (missing token/userId).", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnPickImage.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String author = etAuthor.getText() != null ? etAuthor.getText().toString().trim() : "";
            String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

            if (name.isEmpty() || author.isEmpty()) {
                Toast.makeText(this, "Name and author are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) upload image (ako je odabrana) -> 2) insert knjige s image_url
            uploadImageThenInsertBook(name, author, description);
        });

        setupBottomNav(R.id.nav_all_books);
    }

    private void uploadImageThenInsertBook(String name, String author, String description) {
        if (selectedImageUri == null) {
            insertBook(name, author, description, null);
            return;
        }

        String fileName = "books/" + System.currentTimeMillis() + ".jpg";

        byte[] imageBytes;
        try {
            imageBytes = readBytesFromUri(selectedImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Cannot read image", Toast.LENGTH_SHORT).show();
            insertBook(name, author, description, null);
            return;
        }

        RequestBody body = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/storage/v1/object/book-images/" + fileName)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Content-Type", "image/jpeg")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddBookActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    insertBook(name, author, description, null);
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    String imageUrl = SUPABASE_URL + "/storage/v1/object/public/book-images/" + fileName;
                    runOnUiThread(() -> insertBook(name, author, description, imageUrl));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(AddBookActivity.this, "Upload error: " + response.code(), Toast.LENGTH_SHORT).show();
                        insertBook(name, author, description, null);
                    });
                }
            }
        });
    }

    private void insertBook(String name, String author, String description, String imageUrl) {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("author", author);
        body.addProperty("description", description);
        body.addProperty("user_id", userId);
        body.addProperty("available", true);
        if (imageUrl != null) body.addProperty("image_url", imageUrl);

        RetrofitClientService.getInstance()
                .getApi()
                .insertBook("Bearer " + token, body)
                .enqueue(new ApiCallback<List<JsonObject>>() {
                    @Override
                    public void onSuccess(List<JsonObject> response) {
                        Log.e("ADD_BOOK", "INSERT OK: " + response);
                        Toast.makeText(AddBookActivity.this, "Book created ✅", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("ADD_BOOK", "INSERT ERROR: " + errorMessage);
                        Toast.makeText(AddBookActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private byte[] readBytesFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) throw new IOException("InputStream null");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }
        inputStream.close();
        return buffer.toByteArray();
    }
}
