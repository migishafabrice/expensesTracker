package com.myapp.expensestracker;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StatisticsFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private TextView txtDateRange, txtIncome, txtExpenses, txtBalance;
    private LinearLayout layoutCategories;
    private RadioGroup radioGroup;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        initializeViews(view);
        setupListeners();
        updateStats("daily"); // Default view

        return view;
    }

    private void initializeViews(View view) {
        txtDateRange = view.findViewById(R.id.txtDateRange);
        txtIncome = view.findViewById(R.id.txtIncome);
        txtExpenses = view.findViewById(R.id.txtExpenses);
        txtBalance = view.findViewById(R.id.txtBalance);
        layoutCategories = view.findViewById(R.id.layoutCategories);
        radioGroup = view.findViewById(R.id.radioGroup);

        dbHelper = new DatabaseHelper(getContext());
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    private void setupListeners() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDaily) {
                updateStats("daily");
            } else if (checkedId == R.id.radioWeekly) {
                updateStats("weekly");
            } else if (checkedId == R.id.radioMonthly) {
                updateStats("monthly");
            }
        });
    }

    private void updateStats(String period) {
        String startDate, endDate;
        Calendar cal = Calendar.getInstance();

        switch (period) {
            case "daily":
                startDate = dateFormat.format(cal.getTime());
                endDate = startDate;
                txtDateRange.setText("Statistics for " + startDate);
                break;

            case "weekly":
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                startDate = dateFormat.format(cal.getTime());
                cal.add(Calendar.DAY_OF_WEEK, 6);
                endDate = dateFormat.format(cal.getTime());
                txtDateRange.setText("Statistics from " + startDate + " to " + endDate);
                break;

            case "monthly":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = dateFormat.format(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH,
                        cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate = dateFormat.format(cal.getTime());
                txtDateRange.setText("Statistics for " +
                        new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                .format(cal.getTime()));
                break;

            default:
                return;
        }

        double totalIncome = dbHelper.getTotalIncome(startDate, endDate);
        double totalExpenses = dbHelper.getTotalExpenses(startDate, endDate);
        double balance = totalIncome - totalExpenses;

        txtIncome.setText(String.format("Total Income: $%.2f", totalIncome));
        txtExpenses.setText(String.format("Total Expenses: $%.2f", totalExpenses));
        txtBalance.setText(String.format("Balance: $%.2f", balance));

        updateCategoryStats(startDate, endDate);
    }

    private void updateCategoryStats(String startDate, String endDate) {
        layoutCategories.removeAllViews();

        for (String category : AddExpenseFragment.CATEGORIES) {
            double categoryTotal = 0;
            for (Expense expense : dbHelper.getExpensesByCategory(category,
                    startDate, endDate)) {
                categoryTotal += expense.getAmount();
            }

            if (categoryTotal > 0) {
                TextView txtCategory = new TextView(getContext());
                txtCategory.setText(String.format("%s: $%.2f", category, categoryTotal));
                txtCategory.setPadding(0, 8, 0, 8);
                layoutCategories.addView(txtCategory);
            }
        }
    }
}