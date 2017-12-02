package com.example.android.budget;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.budget.data.BudgetDbHelper;
import com.example.android.budget.data.BudgetContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class OverviewActivity extends AppCompatActivity {

    private BudgetAdapter mAdapter;
    private SQLiteDatabase mDb;
    private SqlUtils mSqlUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);

        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        mDb = dbHelper.getReadableDatabase();

        mSqlUtils = SqlUtils.getInstance(this);

        ArrayList<SqlUtils.Overview> results = mSqlUtils.getOverviewStatistics();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.overview_linear_layout);
        for (SqlUtils.Overview result: results){
            // this code was an attempt at getting dates to work out
            /*
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date res = null;
            String this_date = "";
            try {
                Date dt = sdf.parse(date);
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
                String month = Integer.toString(c.get(Calendar.MONTH));
                String year = Integer.toString(c.get(Calendar.YEAR));
                this_date = day + month + year;
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/
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
            String overviewText = result.name + " has spent: $" + String.valueOf(result.dollars) + " ON: " + result.date + ", a " + this_date;
            overviewTextView.setText(overviewText);
            overviewTextView.setPadding(10, 10, 10, 10);
            overviewTextView.setTextSize(20);
            linearLayout.addView(overviewTextView);
        }
    }



}
