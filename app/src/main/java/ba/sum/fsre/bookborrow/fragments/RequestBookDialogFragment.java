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

import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.models.Profile;
import ba.sum.fsre.bookborrow.models.ProfileShippingModel;
import ba.sum.fsre.bookborrow.repository.BookRepository;
import ba.sum.fsre.bookborrow.repository.UserRepository;
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
    private UserRepository userRepository;
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
        userRepository = new UserRepository(requireContext());

        bindViews(view);
        prefillFromProfile();
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
                            userRepository.updateUserProfileShipping(requesterId,
                                            firstName,
                                            lastName,
                                            address,
                                            postalCode,
                                            city,
                                            shippingNote)
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> r) {
                                            // možeš logirati, ali nije obavezno
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            // možeš logirati, ali nije obavezno
                                        }
                                    });

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

    private void prefillFromProfile() {
        String userId = authManager.getUserId();
        if (userId == null) return;

        userRepository.getUserProfileShipping(
                userId
        ).enqueue(new Callback<List<ProfileShippingModel>>() {
            @Override
            public void onResponse(Call<List<ProfileShippingModel>> call, Response<List<ProfileShippingModel>> response) {

                if (!isAdded()) return;

                if (response.isSuccessful()
                        && response.body() != null
                        && !response.body().isEmpty()) {

                    ProfileShippingModel p = response.body().get(0);
                    if (TextUtils.isEmpty(etFirstName.getText()) && p.getFirstName() != null)
                        etFirstName.setText(p.getFirstName());

                    if (TextUtils.isEmpty(etLastName.getText()) && p.getLastName() != null)
                        etLastName.setText(p.getLastName());

                    if (TextUtils.isEmpty(etAddress.getText()) && p.getAddress() != null)
                        etAddress.setText(p.getAddress());

                    if (TextUtils.isEmpty(etPostalCode.getText()) && p.getPostalCode() != null)
                        etPostalCode.setText(p.getPostalCode());

                    if (TextUtils.isEmpty(etCity.getText()) && p.getCity() != null)
                        etCity.setText(p.getCity());

                    if (TextUtils.isEmpty(etShippingNote.getText()) && p.getShippingNote() != null)
                        etShippingNote.setText(p.getShippingNote());
                }
            }

            @Override
            public void onFailure(Call<List<ProfileShippingModel>> call, Throwable t) {
                // nije kritično - možeš samo logirati
            }
        });
    }
}
