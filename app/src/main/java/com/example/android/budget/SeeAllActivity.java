package com.example.android.budget;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.budget.data.BudgetDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SeeAllActivity extends AppCompatActivity {


    private BudgetAdapter mAdapter;
    private SQLiteDatabase mDb;
    private SqlUtils mSqlUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_all_records);

        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        mDb = dbHelper.getReadableDatabase();

        mSqlUtils = SqlUtils.getInstance(this);

        ArrayList<SqlUtils.BudgetRecord> results = mSqlUtils.getAllRecords();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.all_records_linear_layout);
        for (SqlUtils.BudgetRecord result: results){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            String date = result.date;
            Date dt = null;
            try {
                dt = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String this_date = android.text.format.DateFormat.format("EEEE", dt).toString();

            TextView overviewTextView = new TextView(this);
            String overviewText = String.valueOf(result.id) + ": " + result.name +
                                " has spent: $" + String.valueOf(result.dollars) +
                                " ON: " + result.date + ", a " + this_date +
                                " FOR: " + result.spentOn;
            overviewTextView.setText(overviewText);
            overviewTextView.setPadding(10, 10, 10, 10);
            overviewTextView.setTextSize(20);
            linearLayout.addView(overviewTextView);
        }
    }


}
