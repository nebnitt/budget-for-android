package com.example.android.budget;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.budget.data.BudgetDbHelper;
import com.example.android.budget.data.BudgetContract;

import java.util.HashMap;
import java.util.Map;


public class OverviewActivity extends AppCompatActivity {

    private BudgetAdapter mAdapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);

        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        mDb = dbHelper.getReadableDatabase();

        Map<String, Integer> results = getOverviewStatistics();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.overview_linear_layout);
        for (Map.Entry<String, Integer> entry : results.entrySet())
        {
            TextView overviewTextView = new TextView(this);
            String overviewText = entry.getKey() + " has spent: $" + String.valueOf(entry.getValue());
            overviewTextView.setText(overviewText);
            overviewTextView.setPadding(10, 10, 10, 10);
            overviewTextView.setTextSize(20);
            linearLayout.addView(overviewTextView);
        }
    }

    private Map<String, Integer> getOverviewStatistics(){
        String sql = "SELECT " + BudgetContract.BudgetEntry.COLUMN_NAME +
                ", SUM(" + BudgetContract.BudgetEntry.COLUMN_DOLLARS_SPENT + ")" +
                " FROM " + BudgetContract.BudgetEntry.TABLE_NAME +
                " GROUP BY " + BudgetContract.BudgetEntry.COLUMN_NAME;
        Cursor cursor = mDb.rawQuery(sql, null);
        Map<String, Integer> results = new HashMap<String, Integer>();
        try {
            while (cursor.moveToNext()) {
                results.put(cursor.getString(0), cursor.getInt(1));
            }
        } finally {
            cursor.close();
        }
        return results;
    }

}
