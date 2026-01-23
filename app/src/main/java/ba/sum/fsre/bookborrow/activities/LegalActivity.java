package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;
import android.widget.TextView;

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
            // fallback ako intent nije poslan
            tvTitle.setText("Legal");
            tvContent.setText(R.string.terms_of_use_text);
        }
    }
}
