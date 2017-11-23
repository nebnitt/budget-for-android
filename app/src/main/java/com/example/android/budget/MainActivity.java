package com.example.android.budget;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.android.budget.data.BudgetContract;
import com.example.android.budget.data.BudgetDbHelper;


public class MainActivity extends AppCompatActivity {

    private BudgetAdapter mAdapter;
    private SQLiteDatabase mDb;

    private EditText mNewNameEditText;
    private EditText mNewDollarsSpentEditText;
    private EditText mNewSpentMoneyWhereEditText;

    // d (1) Create local EditText members for mNewGuestNameEditText and mNewPartySizeEditText

    // dODO (13) Create a constant string LOG_TAG that is equal to the class.getSimpleName()
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView budgetRecyclerView;

        // Set local attributes to corresponding views
        budgetRecyclerView = (RecyclerView) this.findViewById(R.id.all_budgets_list_view);

        mNewNameEditText = (EditText) findViewById(R.id.person_name_edit_text);
        mNewDollarsSpentEditText = (EditText) findViewById(R.id.dollars_spent_edit_text);
        mNewSpentMoneyWhereEditText = (EditText) findViewById(R.id.spent_money_where_edit_text);

        // d (2) Set the Edit texts to the corresponding views using findViewById

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Create a DB helper (this will create the DB if run for the first time)
        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // d (3) Remove this fake data call since we will be inserting our own data now
//        TestUtil.insertFakeData(mDb);

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllBudgetEntries();

        // Create an adapter for that cursor to display the data
        mAdapter = new BudgetAdapter(this, cursor);

        // Link the adapter to the RecyclerView
        budgetRecyclerView.setAdapter(mAdapter);

/*        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir){
                long guestId = (long)viewHolder.itemView.getTag();
                removeBudgetItem(guestId);
                mAdapter.swapCursor(getAllBudgetEntries());
            }
        }).attachToRecyclerView(budgetRecyclerView);*/

    }

    /**
     * This method is called when user clicks on the Add to waitlist menu item
     */
    public void addToBudgetList() {
        if(mNewNameEditText.getText().length() == 0 || mNewSpentMoneyWhereEditText.getText().length() == 0
                || mNewDollarsSpentEditText.getText().length() == 0){
            return;
        }

        try {
            int dollarsSpent = Integer.parseInt(mNewDollarsSpentEditText.getText().toString());
            String name = mNewNameEditText.getText().toString();
            String spentOn = mNewSpentMoneyWhereEditText.getText().toString();
            addBudgetItem(name, dollarsSpent, spentOn);
            mAdapter.swapCursor(getAllBudgetEntries());//, lastAddedGuestId);
        } catch (Exception e){
            e.printStackTrace();
        }

        mNewDollarsSpentEditText.getText().clear();
        mNewSpentMoneyWhereEditText.getText().clear();
        mNewNameEditText.getText().clear();
        mNewDollarsSpentEditText.clearFocus();
        mNewSpentMoneyWhereEditText.clearFocus();
        mNewNameEditText.clearFocus();

    }



    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllBudgetEntries() {
        return mDb.query(
                BudgetContract.BudgetEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BudgetContract.BudgetEntry.COLUMN_TIMESTAMP
        );
    }

    private void addBudgetItem(String name, int dollarsSpent, String spentOn){
        String tableName = BudgetContract.BudgetEntry.TABLE_NAME;
        String nameCol = BudgetContract.BudgetEntry.COLUMN_NAME;
        String dollarsCol = BudgetContract.BudgetEntry.COLUMN_DOLLARS_SPENT;
        String spentOnCol = BudgetContract.BudgetEntry.COLUMN_SPENT_MONEY_ON;

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

    private void removeBudgetItem(long idToRemove){
        String id = String.valueOf(idToRemove);
        String sql = "DELETE FROM " + BudgetContract.BudgetEntry.TABLE_NAME + " WHERE _ID = " + id;
        mDb.execSQL(sql);
    }

    public void goToOverview(){
        Intent intent = new Intent(this, OverviewActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int item_id = menuItem.getItemId();
        switch(item_id) {
            case R.id.add_budget_entry:
                addToBudgetList();
                return true;
            case R.id.see_overview:
                goToOverview();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}