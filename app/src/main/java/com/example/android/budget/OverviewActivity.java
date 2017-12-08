package com.example.android.budget;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.budget.data.BudgetDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class OverviewActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private BudgetAdapter mAdapter;
    private SQLiteDatabase mDb;
    private SqlUtils mSqlUtils;
    private Spinner mSelectTimeSpinner;
    private LinearLayout mLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);

        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        mDb = dbHelper.getReadableDatabase();

        mLinearLayout = (LinearLayout) findViewById(R.id.wrapper_linear_layout);

        mSqlUtils = SqlUtils.getInstance(this);

        mSelectTimeSpinner = (Spinner) findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.times_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectTimeSpinner.setAdapter(adapter);
        mSelectTimeSpinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String timeLayer = parent.getItemAtPosition(position).toString();
        mLinearLayout.removeAllViews();
        displayResults(timeLayer);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // idk what this is for
    }

    void displayResults(String timeLayer){
        ArrayList<SqlUtils.BudgetRecord> results;
        if(timeLayer.equals("All")) {
            results = mSqlUtils.getTotalAggregateStatistics();
        } else {
            results = mSqlUtils.getTimeLayeredStatistics(timeLayer);
        }

        String date_string = "";

        for (SqlUtils.BudgetRecord result: results){
            if(timeLayer.equals("Day")) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                String date = result.date;
                Date dt = null;
                try {
                    dt = sdf.parse(date);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String this_date = android.text.format.DateFormat.format("EEEE", dt).toString();
                date_string = " ON: " + result.date + ", a " + this_date;
            }


            TextView overviewTextView = new TextView(this);
            String overviewText = result.name + " has spent: $" + String.valueOf(result.dollars) + date_string;
            overviewTextView.setText(overviewText);
            overviewTextView.setPadding(10, 10, 10, 10);
            overviewTextView.setTextSize(20);
            mLinearLayout.addView(overviewTextView);
        }
    }

}
