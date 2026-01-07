package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import com.google.gson.JsonObject;
import ba.sum.fsre.bookborrow.utils.RetrofitClient;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = new AuthManager(this);


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

        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        SupabaseAuthService service =
                RetrofitClient.getClient().create(SupabaseAuthService.class);

        Call<JsonObject> call = service.register(body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {

                    String accessToken = response.body().get("access_token").getAsString();
                    authManager.saveToken(accessToken);
                    authManager.saveEmail(email);

                    Toast.makeText(RegisterActivity.this,
                            "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
