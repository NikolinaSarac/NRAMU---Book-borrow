package ba.sum.fsre.bookborrow.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.models.IsDeletedRequest;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClient;
import ba.sum.fsre.bookborrow.utils.RetrofitClientService;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = new AuthManager(this);

        if (authManager.isLoggedIn()) {
            Intent intent = new Intent(this, AllRequestsActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        tvRegister = findViewById(R.id.tvRegisterLink);
        tvRegister.setPaintFlags(tvRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }



    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseAuthService authService =
                RetrofitClient.getClient().create(SupabaseAuthService.class);

        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        authService.login(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this,
                            "Invalid credentials", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObject res = response.body();
                String token = res.get("access_token").getAsString();
                String userId = res.getAsJsonObject("user").get("id").getAsString();

                String authHeader = "Bearer " + token;

                RetrofitClientService.getInstance()
                        .getApi().
                        checkProfileDeleted(
                        authHeader,
                        "eq." + userId,
                        "is_deleted"
                ).enqueue(new Callback<List<IsDeletedRequest>>() {
                    @Override
                    public void onResponse(Call<List<IsDeletedRequest>> call,
                                           Response<List<IsDeletedRequest>> profileResp) {

                        Log.e("LOGIN", "profileResp code=" + profileResp.code());

                        try {
                            if (profileResp.errorBody() != null) {
                                Log.e("LOGIN", "profileResp error=" + profileResp.errorBody().string());
                            }
                        } catch (Exception ignored) {}

                        Log.e("LOGIN", "profileResp body=" + profileResp.body());

                        if (!profileResp.isSuccessful()
                                || profileResp.body() == null
                                || profileResp.body().isEmpty()) {

                            Toast.makeText(LoginActivity.this,
                                    "Profile not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean isDeleted = profileResp.body().get(0).isDeleted();

                        if (isDeleted) {
                            Toast.makeText(LoginActivity.this,
                                    "This account has been deleted",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        authManager.saveToken(token);
                        authManager.saveUserId(userId);
                        authManager.saveEmail(email);

                        Toast.makeText(LoginActivity.this,
                                "Login successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, AllRequestsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<List<IsDeletedRequest>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,
                                "Profile check failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
