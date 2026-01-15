package ba.sum.fsre.bookborrow.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClient;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class EditBookActivity extends BaseActivity {

    public static final String EXTRA_BOOK_ID = "BOOK_ID";
    public static final String EXTRA_NAME = "BOOK_NAME";
    public static final String EXTRA_AUTHOR = "BOOK_AUTHOR";
    public static final String EXTRA_DESCRIPTION = "BOOK_DESCRIPTION";
    public static final String EXTRA_IMAGE_URL = "BOOK_IMAGE_URL"; // ✅ dodajemo

    // ⚠️ prilagodi ako ti je drugačije
    private static final String SUPABASE_URL = "https://gvqwwllhdvsjtpodlruk.supabase.co";
    private static final String SUPABASE_API_KEY = "sb_publishable_QayxGZsh6CBuXJ1DFXsTXA_OzEMBI0e";
    private static final String BUCKET = "book-images";
    private static final String FOLDER = "books";

    private EditText etName, etAuthor, etDescription;
    private Button btnSave, btnChangeImage;
    private ImageView ivPreview;

    private AuthManager authManager;
    private String bookId;

    private String currentImageUrl;     // url koji je bio prije
    private Uri newImageUri = null;     // ako user odabere novu sliku

    private final OkHttpClient http = new OkHttpClient();

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    newImageUri = uri;
                    // pokaži preview odmah
                    Glide.with(this).load(uri).into(ivPreview);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        authManager = new AuthManager(this);

        ivPreview = findViewById(R.id.ivPreview);
        btnChangeImage = findViewById(R.id.btnChangeImage);

        etName = findViewById(R.id.etName);
        etAuthor = findViewById(R.id.etAuthor);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);

        bookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        etName.setText(getIntent().getStringExtra(EXTRA_NAME));
        etAuthor.setText(getIntent().getStringExtra(EXTRA_AUTHOR));
        etDescription.setText(getIntent().getStringExtra(EXTRA_DESCRIPTION));

        currentImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        // Preview trenutne slike
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(currentImageUrl)
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(ivPreview);
        } else {
            ivPreview.setImageResource(R.drawable.ic_book);
        }

        btnChangeImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        if (bookId == null || bookId.isEmpty()) return;

        String token = authManager.getToken();
        if (token == null || token.isEmpty()) return;

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        // 1) Ako nema nove slike -> samo update text fields
        if (newImageUri == null) {
            updateBookRow(authHeader, currentImageUrl);
            return;
        }

        // 2) Ako ima novu sliku -> upload pa update
        new Thread(() -> {
            try {
                String uploadedUrl = uploadImageToSupabase(authHeader, newImageUri);

                runOnUiThread(() -> {
                    if (uploadedUrl == null) {
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_LONG).show();
                        return;
                    }
                    updateBookRow(authHeader, uploadedUrl);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Upload error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    /**
     * Upload slike u Supabase Storage i vraća public URL.
     */
    private String uploadImageToSupabase(String authHeader, Uri uri) throws Exception {
        byte[] bytes = readBytesFromUri(uri);
        if (bytes == null || bytes.length == 0) return null;

        String ext = getFileExtension(uri);
        if (ext == null) ext = "jpg";

        String fileName = System.currentTimeMillis() + "." + ext;
        String path = FOLDER + "/" + fileName;

        // Storage upload endpoint:
        // POST /storage/v1/object/<bucket>/<path>
        String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET + "/" + path;

        String mime = getContentResolver().getType(uri);
        if (mime == null) mime = "image/jpeg";

        RequestBody body = RequestBody.create(bytes, MediaType.parse(mime));

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(body)
                .addHeader("Authorization", authHeader)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Content-Type", mime)
                .build();

        try (Response resp = http.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                // za debug: vidi status kod
                return null;
            }
        }

        // public URL format:
        return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET + "/" + path;
    }

    private void updateBookRow(String authHeader, String imageUrlToSet) {
        JsonObject body = new JsonObject();
        body.addProperty("name", etName.getText().toString().trim());
        body.addProperty("author", etAuthor.getText().toString().trim());
        body.addProperty("description", etDescription.getText().toString().trim());

        // samo ako imamo url (može biti null ako je ostalo staro)
        if (imageUrlToSet != null) body.addProperty("image_url", imageUrlToSet);

        SupabaseAuthService service = RetrofitClient.getClient().create(SupabaseAuthService.class);

        // moraš imati updateBook u SupabaseAuthService:
        // @PATCH("rest/v1/books") Call<Void> updateBook(@Header("Authorization") String token, @Query(value="id", encoded=true) String idFilter, @Body JsonObject body);
        service.updateBook(authHeader, "eq." + bookId, body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditBookActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(EditBookActivity.this, "Update failed: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(EditBookActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private byte[] readBytesFromUri(Uri uri) throws Exception {
        try (InputStream is = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            if (is == null) return null;

            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }

    private String getFileExtension(Uri uri) {
        String mime = getContentResolver().getType(uri);
        if (mime == null) return null;
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);
    }
}
