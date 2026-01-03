package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.SupabaseInstance;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private AuthManager authManager;

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AuthManager authManager = new AuthManager(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(SupabaseInstance.getAuthRegisterUrl())
                    .addHeader("apikey", SupabaseInstance.getAnonKey())
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            SupabaseInstance.getClient()
                    .newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() ->
                                    Toast.makeText(RegisterActivity.this,
                                            "Network error", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful() && response.body() != null) {
                                String respStr = response.body().string();
                                try {
                                    JSONObject json = new JSONObject(respStr);
                                    String accessToken = json.getString("access_token");

                                    runOnUiThread(() -> {
                                        authManager.saveToken(accessToken);
                                        Toast.makeText(RegisterActivity.this,
                                                "Registration successful", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(() ->
                                            Toast.makeText(RegisterActivity.this,
                                                    "Parsing error", Toast.LENGTH_SHORT).show());
                                }
                            } else {
                                runOnUiThread(() ->
                                        Toast.makeText(RegisterActivity.this,
                                                "Registration failed", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error", Toast.LENGTH_SHORT).show();
        }
    }
}
