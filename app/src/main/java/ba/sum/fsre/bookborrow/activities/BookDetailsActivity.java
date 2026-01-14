package ba.sum.fsre.bookborrow.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;


import com.google.android.material.appbar.MaterialToolbar;

import ba.sum.fsre.bookborrow.R;

public class BookDetailsActivity extends BaseActivity {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_AUTHOR = "extra_author";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_AVAILABLE = "extra_available";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);




        findViewById(R.id.btnBack).setOnClickListener(v -> finish());



        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvDescription = findViewById(R.id.tvDescription);
        ImageView ivBookImage = findViewById(R.id.ivBookImage);


        String name = getIntent().getStringExtra(EXTRA_NAME);
        String author = getIntent().getStringExtra(EXTRA_AUTHOR);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        boolean available = getIntent().getBooleanExtra(EXTRA_AVAILABLE, false);
        String imageUrl = getIntent().getStringExtra("image_url");


        tvTitle.setText(name != null ? name : "Unknow title");
        tvAuthor.setText(author != null ? author : "Unknow author");
        tvDescription.setText(description != null ? description : "NO description");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(ivBookImage);
        } else {
            ivBookImage.setImageResource(R.drawable.ic_book);
        }


        if (available) {
            tvStatus.setText("AVAILABLE");
            tvStatus.setBackgroundResource(R.drawable.bg_status_available);
        } else {
            tvStatus.setText("BORROWED");
            tvStatus.setBackgroundResource(R.drawable.bg_status_borrowed);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Book details");
        }

    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
