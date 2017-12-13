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

        mLinearLayout = (LinearLayout) findViewById(R.id.overview_wrapper_linear_layout);

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
        // TODO: This isn't strictly a time selector now that "Categories" has been added;
        // either create new Spinner or change variable naming
        String timeLayer = parent.getItemAtPosition(position).toString();
        mLinearLayout.removeAllViews();
        getResults(timeLayer);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // idk what this is for
    }

    void getResults(String timeLayer){
        ArrayList<SqlUtils.BudgetRecord> results;
        if(timeLayer.equals("All")) {
            results = mSqlUtils.getTotalAggregateStatistics();
        } else if (timeLayer.equals("Categories")) {
            results = mSqlUtils.getCategoryStatistics();
        } else {
            results = mSqlUtils.getTimeLayeredStatistics(timeLayer);
        }
        displayResults(results, timeLayer);
    }

    void displayResults(ArrayList<SqlUtils.BudgetRecord> results, String timeLayer) {
        if(!timeLayer.equals("Categories")) {
            String date_string = "";
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            for (SqlUtils.BudgetRecord result : results) {
                String date = result.date;
                Date dt = null;
                try {
                    dt = sdf.parse(date);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //TODO: Figure out why this isn't working in all cases; day of week is often wrong
                String this_date = android.text.format.DateFormat.format("EEEE", dt).toString();
                String qualifier = timeLayer.equals("All") ? " since " : " on: ";
                date_string = qualifier + result.date + ", a " + this_date;

                String overviewText = result.name + " has spent: $" + String.valueOf(result.dollars) + date_string;
                createTextView(overviewText);
            }
        }
        else  {
            for (SqlUtils.BudgetRecord result : results) {
                String overviewText = result.name + " has spent: $" + String.valueOf(result.dollars) + " on " + result.spentOn;
                createTextView(overviewText);
            }
        }
    }

    void createTextView(String text){
        //TODO: This should probably be done in a RecyclerView
        TextView overviewTextView = new TextView(this);
        overviewTextView.setText(text);
        overviewTextView.setPadding(10, 10, 10, 10);
        overviewTextView.setTextSize(20);
        mLinearLayout.addView(overviewTextView);
    }

}
