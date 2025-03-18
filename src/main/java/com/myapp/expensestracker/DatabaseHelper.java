package com.myapp.expensestracker;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExpenseTracker";
    private static final int DATABASE_VERSION = 1;

    // Tables
    private static final String TABLE_INCOME = "income";
    private static final String TABLE_EXPENSES = "expenses";

    // Common columns
    private static final String KEY_ID = "id";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";

    // Expense table columns
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Income table
        String CREATE_INCOME_TABLE = "CREATE TABLE " + TABLE_INCOME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_DATE + " TEXT"
                + ")";

        // Create Expenses table
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT"
                + ")";

        db.execSQL(CREATE_INCOME_TABLE);
        db.execSQL(CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // Add new income
    public long addIncome(double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, amount);
        values.put(KEY_DATE, date);
        return db.insert(TABLE_INCOME, null, values);
    }

    // Add new expense
    public long addExpense(double amount, String category, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, amount);
        values.put(KEY_CATEGORY, category);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_DATE, date);
        return db.insert(TABLE_EXPENSES, null, values);
    }

    // Get total income for a date range
    public double getTotalIncome(String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_INCOME +
                " WHERE " + KEY_DATE + " BETWEEN ? AND ?", new String[]{startDate, endDate});

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // Get expenses by category for a date range
    @SuppressLint("Range")
    public List<Expense> getExpensesByCategory(String category, String startDate, String endDate) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXPENSES, null,
                KEY_CATEGORY + "=? AND " + KEY_DATE + " BETWEEN ? AND ?",
                new String[]{category, startDate, endDate},
                null, null, KEY_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                expense.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                expense.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenses;
    }

    // Get total expenses for a date range
    public double getTotalExpenses(String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + KEY_DATE + " BETWEEN ? AND ?", new String[]{startDate, endDate});

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }
}