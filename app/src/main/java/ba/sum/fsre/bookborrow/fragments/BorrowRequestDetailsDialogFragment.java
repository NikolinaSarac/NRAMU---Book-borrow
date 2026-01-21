package ba.sum.fsre.bookborrow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;

import ba.sum.fsre.bookborrow.R;

public class BorrowRequestDetailsDialogFragment extends DialogFragment {

    private static final String ARG_BORROW = "borrow";

    private JsonObject borrow;

    private TextView tvFirstName, tvLastName, tvAddress, tvPostalCode, tvCity, tvShippingNote;

    public static BorrowRequestDetailsDialogFragment newInstance(JsonObject borrow) {
        BorrowRequestDetailsDialogFragment fragment = new BorrowRequestDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BORROW, borrow.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_borrow_request_details, container, false);

        if (getArguments() != null) {
            String borrowStr = getArguments().getString(ARG_BORROW);
            borrow = borrowStr != null ? new com.google.gson.JsonParser().parse(borrowStr).getAsJsonObject() : null;
        }

        bindViews(view);
        populateData();
        setupCloseButton(view);

        return view;
    }

    private void bindViews(View view) {
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvLastName = view.findViewById(R.id.tvLastName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvPostalCode = view.findViewById(R.id.tvPostalCode);
        tvCity = view.findViewById(R.id.tvCity);
        tvShippingNote = view.findViewById(R.id.tvShippingNote);
    }

    private void populateData() {
        if (borrow == null) return;

        tvFirstName.setText(getStringSafe(borrow, "first_name"));
        tvLastName.setText(getStringSafe(borrow, "last_name"));
        tvAddress.setText(getStringSafe(borrow, "address"));
        tvPostalCode.setText(getStringSafe(borrow, "postal_code"));
        tvCity.setText(getStringSafe(borrow, "city"));
        tvShippingNote.setText(getStringSafe(borrow, "shipping_note"));
    }

    private void setupCloseButton(View view) {
        Button btnClose = view.findViewById(R.id.btnCloseDialog);
        btnClose.setOnClickListener(v -> dismiss());
    }

    private String getStringSafe(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return "-";
    }
}
