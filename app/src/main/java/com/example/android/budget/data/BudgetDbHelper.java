package com.example.android.budget.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BudgetDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "budget.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public BudgetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold waitlist data
        final String SQL_CREATE_BUDGET_TABLE = "CREATE TABLE " + BudgetContract.BudgetEntry.TABLE_NAME + " (" +
                BudgetContract.BudgetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BudgetContract.BudgetEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BudgetContract.BudgetEntry.COLUMN_DOLLARS_SPENT + " INTEGER NOT NULL, " +
                BudgetContract.BudgetEntry.COLUMN_SPENT_MONEY_ON + " TEXT NOT NULL, " +
                BudgetContract.BudgetEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BUDGET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BudgetEntry.TABLE_NAME);
//        onCreate(sqLiteDatabase);
    }
}