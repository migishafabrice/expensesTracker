package com.myapp.expensestracker;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {
    private EditText edtAmount, edtDescription, edtIncome;
    private Spinner spinnerCategory;
    private Button btnDate, btnAddExpense, btnAddIncome;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    public static final String[] CATEGORIES = {
            "Food", "Drinks", "Entertainment", "Travels", "Clothes", "Other"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        initializeViews(view);
        setupSpinner();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        edtAmount = view.findViewById(R.id.edtAmount);
        edtDescription = view.findViewById(R.id.edtDescription);
        edtIncome = view.findViewById(R.id.edtIncome);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnDate = view.findViewById(R.id.btnDate);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);
        btnAddIncome = view.findViewById(R.id.btnAddIncome);

        dbHelper = new DatabaseHelper(getContext());
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        btnDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                CATEGORIES
        );
        spinnerCategory.setAdapter(adapter);
    }

    private void setupListeners() {
        btnDate.setOnClickListener(v -> showDatePicker());
        btnAddExpense.setOnClickListener(v -> addExpense());
        btnAddIncome.setOnClickListener(v -> addIncome());
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            btnDate.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addExpense() {
        try {
            double amount = Double.parseDouble(edtAmount.getText().toString());
            String category = spinnerCategory.getSelectedItem().toString();
            String description = edtDescription.getText().toString();
            String date = btnDate.getText().toString();

            long result = dbHelper.addExpense(amount, category, description, date);
            if (result != -1) {
                Toast.makeText(getContext(), "Expense added successfully",
                        Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(getContext(), "Failed to add expense",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid amount",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addIncome() {
        try {
            double income = Double.parseDouble(edtIncome.getText().toString());
            String date = dateFormat.format(calendar.getTime());

            long result = dbHelper.addIncome(income, date);
            if (result != -1) {
                Toast.makeText(getContext(), "Income updated successfully",
                        Toast.LENGTH_SHORT).show();
                edtIncome.getText().clear();
            } else {
                Toast.makeText(getContext(), "Failed to update income",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid income amount",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        edtAmount.getText().clear();
        edtDescription.getText().clear();
        spinnerCategory.setSelection(0);
    }
}