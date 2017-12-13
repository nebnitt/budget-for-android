package com.example.android.budget;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.android.budget.data.BudgetContract;
import com.example.android.budget.data.BudgetDbHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

final class SqlUtils {
    private static SqlUtils instance = null;

    private SQLiteDatabase mDb;
    private String mTableName = BudgetContract.BudgetEntry.TABLE_NAME;
    private String mIdColumn = "_ID";
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

    class BudgetRecord {
        int id;
        int dollars;
        String name;
        String spentOn;
        String date;
    }

    ArrayList<BudgetRecord> getAllRecords(){
        String sql = "SELECT " + mIdColumn +
                    ", " + mNameColumn  +
                    ", " + mDollarsColumn +
                    ", " + mSpentOnColumn +
                    ", datetime(" + mTimestampColumn + ", 'localtime') " +
                    " FROM " + mTableName;

        Cursor cursor = mDb.rawQuery(sql, null);
        ArrayList<BudgetRecord> results = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                BudgetRecord result = new BudgetRecord();
                result.id = cursor.getInt(0);
                result.name = cursor.getString(1);
                result.dollars = cursor.getInt(2);
                result.spentOn = cursor.getString(3);
                result.date = cursor.getString(4);
                results.add(result);
            }
        } finally {
            cursor.close();
        }
        return results;
    }

    private ArrayList<BudgetRecord> executeSqlForBudgetStatisticsAndReturnResults(String sql, String selectedColumn, int selectedColumnIndex){
        Cursor cursor = mDb.rawQuery(sql, null);
        ArrayList<BudgetRecord> results = new ArrayList<>();
        try {
            Field field = BudgetRecord.class.getDeclaredField(selectedColumn);
            try {
                while (cursor.moveToNext()) {
                    BudgetRecord result = new BudgetRecord();
                    result.name = cursor.getString(0);
                    result.dollars = cursor.getInt(1);
                    try {
                        field.set(result, cursor.getString(selectedColumnIndex));
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                    results.add(result);
                }
            } finally {
                cursor.close();
            }
            return results;
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    ArrayList<BudgetRecord> getTotalAggregateStatistics(){
        String sqlTime = "'%m-%d-%Y'";
        String selectedColumn = "date";
        int selectedColumnIndex = 2;
        String sql = "SELECT " + mNameColumn + ", SUM(" + mDollarsColumn + ")" +
                ", MIN(strftime("+sqlTime+", " + "datetime(" + mTimestampColumn + ", 'localtime')))" +
                " FROM " + mTableName +
                " GROUP BY " + mNameColumn;

        return executeSqlForBudgetStatisticsAndReturnResults(sql, selectedColumn, selectedColumnIndex);
    }

    ArrayList<BudgetRecord> getCategoryStatistics(){
        String selectedColumn = "spentOn";
        int selectedColumnIndex = 2;
        String sql = "SELECT " + mNameColumn + ", SUM(" + mDollarsColumn + ")" +
                ", " + mSpentOnColumn +
                " FROM " + mTableName +
                " GROUP BY " + mNameColumn + ", " + mSpentOnColumn;

        return executeSqlForBudgetStatisticsAndReturnResults(sql, selectedColumn, selectedColumnIndex);
    }

    ArrayList<BudgetRecord> getTimeLayeredStatistics(String timeLayer){
        String sqlTime = "'%m-%d-%Y'";
        String selectedColumn = "date";
        String additionalWeekSelect = "";
        if (timeLayer.equals("Week")){
            sqlTime = "'%W-%Y'";
            additionalWeekSelect = ", max(date(" + mTimestampColumn + ", 'weekday 0', '-7 day'))";
        }
        String sql = "SELECT " + mNameColumn + ", SUM(" + mDollarsColumn + ")" +
                ", strftime("+sqlTime+", " + "datetime(" + mTimestampColumn + ", 'localtime'))" +
                additionalWeekSelect +
                " FROM " + mTableName +
                " GROUP BY " + mNameColumn +
                ", strftime("+sqlTime+", " + "datetime(" + mTimestampColumn + ", 'localtime'))" +
                " ORDER BY 3 ASC";
        int selectedColumnIndex = 2;
        if (timeLayer.equals("Week")){
            selectedColumnIndex = 3;
        }
        return executeSqlForBudgetStatisticsAndReturnResults(sql, selectedColumn, selectedColumnIndex);
    }
}
