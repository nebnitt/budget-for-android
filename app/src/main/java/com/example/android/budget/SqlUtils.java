package com.example.android.budget;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.android.budget.data.BudgetContract;
import com.example.android.budget.data.BudgetDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

final class SqlUtils {
    private static SqlUtils instance = null;

    private SQLiteDatabase mDb;
    private String mTableName = BudgetContract.BudgetEntry.TABLE_NAME;
    private String mNameColumn = BudgetContract.BudgetEntry.COLUMN_NAME;
    private String mDollarsColumn = BudgetContract.BudgetEntry.COLUMN_DOLLARS_SPENT;
    private String mSpentOnColumn = BudgetContract.BudgetEntry.COLUMN_SPENT_MONEY_ON;
    private String mTimestampColumn = BudgetContract.BudgetEntry.COLUMN_TIMESTAMP;

    protected SqlUtils(Context context){
        // Create a DB helper (this will create the DB if run for the first time)
        BudgetDbHelper dbHelper = new BudgetDbHelper(context);
        // Keep a reference to the mDb until paused or killed.
        mDb = dbHelper.getWritableDatabase();
    }

    public static SqlUtils getInstance(Context context){
        if(instance == null) {
            instance = new SqlUtils(context);
        }
        return instance;
    }


    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    Cursor getAllBudgetEntries() {
        return mDb.query(
                mTableName,
                null,
                null,
                null,
                null,
                null,
                BudgetContract.BudgetEntry.COLUMN_TIMESTAMP
        );
    }

    void addBudgetItem(String name, int dollarsSpent, String spentOn){
        String tableName = mTableName;
        String nameCol = mNameColumn;
        String dollarsCol = mDollarsColumn;
        String spentOnCol = mSpentOnColumn;

        String sql = "INSERT INTO " + tableName +
                "(" + nameCol + ", " + dollarsCol + ", " + spentOnCol + ")" +
                " VALUES ('" + name + "', " + dollarsSpent + ", '" + spentOn + "')";
        mDb.execSQL(sql);

//        String getLastRowId = "SELECT last_insert_rowid()";
//        Cursor cursor = mDb.rawQuery(getLastRowId, null);
//        cursor.moveToFirst();
//        long addedGuestId = cursor.getLong(0);
//        cursor.close();
//        return addedGuestId;

    }

    void removeBudgetItem(long idToRemove){
        String id = String.valueOf(idToRemove);
        String sql = "DELETE FROM " + mTableName + " WHERE _ID = " + id;
        mDb.execSQL(sql);
    }

    void updateBudgetItem(String idToUpdate, Bundle updateArgs){

        String name = updateArgs.getString("name");
        String spentOn = updateArgs.getString("spentOn");
        String dollars = updateArgs.getString("dollars").replace("$", "");

        String sql = "UPDATE " + mTableName +
                    " SET " + mNameColumn + " = '" + name + "' " +
                    ", " + mSpentOnColumn + " = '" + spentOn + "' " +
                    ", " + mDollarsColumn + " = " + dollars +
                " WHERE _ID = " + idToUpdate;
        mDb.execSQL(sql);
    }

    class Overview {
        String name;
        int dollars;
        String date;
    }

    ArrayList<Overview> getOverviewStatistics(){
        String sql = "SELECT " + mNameColumn +
                    ", SUM(" + mDollarsColumn + ")" +
                    ", strftime('%m-%d-%Y', " + mTimestampColumn + ")" +
                    " FROM " + mTableName +
                    " GROUP BY " + mNameColumn +
                    ", strftime('%m-%d-%Y', " + mTimestampColumn + ")" +
                    " ORDER BY 3 ASC";
        Cursor cursor = mDb.rawQuery(sql, null);
        ArrayList<Overview> results = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Overview result = new Overview();
                result.name = cursor.getString(0);
                result.dollars = cursor.getInt(1);
                result.date = cursor.getString(2);
                results.add(result);

            }
        } finally {
            cursor.close();
        }
        return results;
    }
}
