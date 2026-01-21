package ba.sum.fsre.bookborrow.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.repository.BookRepository;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestBookDialogFragment extends DialogFragment {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_OWNER_ID = "owner_id";

    private String bookId;
    private String ownerId;

    private EditText etFirstName, etLastName, etAddress,
            etPostalCode, etCity, etShippingNote;

    private BookRepository repository;
    private AuthManager authManager;

    // ===== FACTORY METHOD =====
    public static RequestBookDialogFragment newInstance(String bookId, String ownerId) {
        RequestBookDialogFragment fragment = new RequestBookDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
        args.putString(ARG_OWNER_ID, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_request_book, container, false);

        if (getArguments() != null) {
            bookId = getArguments().getString(ARG_BOOK_ID);
            ownerId = getArguments().getString(ARG_OWNER_ID);
        }

        authManager = new AuthManager(requireContext());
        repository = new BookRepository(requireContext());

        bindViews(view);
        setupButtons(view);

        return view;
    }

    private void bindViews(View view) {
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etAddress = view.findViewById(R.id.etAddress);
        etPostalCode = view.findViewById(R.id.etPostalCode);
        etCity = view.findViewById(R.id.etCity);
        etShippingNote = view.findViewById(R.id.etShippingNote);
    }

    private void setupButtons(View view) {
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSend = view.findViewById(R.id.btnSendRequest);

        btnCancel.setOnClickListener(v -> dismiss());

        btnSend.setOnClickListener(v -> sendRequest());
    }

    private void sendRequest() {

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String postalCode = etPostalCode.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String shippingNote = etShippingNote.getText().toString().trim();

        // ===== VALIDACIJA =====
        if (TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(postalCode) ||
                TextUtils.isEmpty(city)) {

            Toast.makeText(getContext(),
                    "Please fill all required fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String requesterId = authManager.getUserId();

        repository.sendBookRequestWithShipping(
                        bookId,
                        ownerId,
                        requesterId,
                        firstName,
                        lastName,
                        address,
                        postalCode,
                        city,
                        shippingNote
                )
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Request successfully sent",
                                    Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to send request",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(),
                                t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
