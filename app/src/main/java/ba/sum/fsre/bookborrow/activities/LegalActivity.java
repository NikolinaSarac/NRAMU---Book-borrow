package ba.sum.fsre.bookborrow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ba.sum.fsre.bookborrow.R;

public class LegalActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "type";
    public static final String TYPE_PRIVACY = "privacy";
    public static final String TYPE_TERMS = "terms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        TextView tvTitle = findViewById(R.id.tvLegalTitle);
        TextView tvContent = findViewById(R.id.tvLegalContent);

        String type = getIntent().getStringExtra(EXTRA_TYPE);


        if (TYPE_PRIVACY.equals(type)) {
            tvTitle.setText("Privacy Policy");
            tvContent.setText(R.string.privacy_policy_text);
        } else if (TYPE_TERMS.equals(type)) {
            tvTitle.setText("Terms of Use");
            tvContent.setText(R.string.terms_of_use_text);
        } else {
            tvTitle.setText("Legal");
            tvContent.setText(R.string.terms_of_use_text);
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack == null) {
            throw new RuntimeException("btnBack NOT FOUND in activity_legal.xml");
        }

        btnBack.setOnClickListener(v -> {
            Toast.makeText(this, "Back clicked", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
