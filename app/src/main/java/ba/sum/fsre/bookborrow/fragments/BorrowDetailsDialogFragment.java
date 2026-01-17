package ba.sum.fsre.bookborrow.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.List;

import ba.sum.fsre.bookborrow.R;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClient;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BorrowDetailsDialogFragment extends DialogFragment {

    private EditText etDate, etTime, etLocation, etDescription;
    private Button btnSave, btnCancel;

    private String borrowId;
    private AuthManager authManager;

    public static BorrowDetailsDialogFragment newInstance(String borrowId) {
        BorrowDetailsDialogFragment fragment = new BorrowDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString("borrowId", borrowId);
        fragment.setArguments(args);
        return fragment;
    }

    public BorrowDetailsDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_borrow_details_dialog, container, false);

        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        etLocation = view.findViewById(R.id.etLocation);
        etDescription = view.findViewById(R.id.etDescription);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        authManager = new AuthManager(requireContext());

        if (getArguments() != null) {
            borrowId = getArguments().getString("borrowId");
        }

        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());
        // Time picker
        etTime.setOnClickListener(v -> showTimePicker());

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> saveDetails());

        return view;
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, y, m, d) -> etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d)),
                year, month, day);
        dialog.show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(requireContext(),
                (view, h, m) -> etTime.setText(String.format("%02d:%02d", h, m)),
                hour, minute, true);
        dialog.show();
    }

    private void saveDetails() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || location.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in the date, time and location.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("pickup_date", date);
        body.addProperty("pickup_time", time);
        body.addProperty("location", location);
        body.addProperty("description", description);

        SupabaseAuthService service = RetrofitClient.getClient().create(SupabaseAuthService.class);

        service.updateBorrowDetails(
                        "Bearer " + authManager.getToken(),
                        "eq." + borrowId,
                        body)
                .enqueue(new Callback<List<JsonObject>>() {

                    @Override
                    public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Details saved!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Error: " + response.code() + " " + response.message(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                        Toast.makeText(
                                requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
