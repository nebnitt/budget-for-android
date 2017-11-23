package com.example.android.budget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private BudgetAdapter mAdapter;
    private SqlUtils mSqlUtils;

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

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSqlUtils = SqlUtils.getInstance(this);

        // Get all guest info from the database and save in a cursor
        Cursor cursor = mSqlUtils.getAllBudgetEntries();

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

    public static class EditBudgetItemDialogFragment extends DialogFragment {

        private SqlUtils mSqlUtils = SqlUtils.getInstance(getActivity());

        public EditBudgetItemDialogFragment(){}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Get the layout inflater and inflate the layout
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View editBudgetDialog = inflater.inflate(R.layout.dialog_edit, null);

            final EditText dialogNameEditText = (EditText)editBudgetDialog.findViewById(R.id.edit_name);
            final EditText dialogSpentOnEditText = (EditText)editBudgetDialog.findViewById(R.id.edit_spent_on);
            final EditText dialogDollarsEditText = (EditText)editBudgetDialog.findViewById(R.id.edit_dollars);

            final Bundle args = getArguments();

            String name = args.getString("name");
            String spentOn = args.getString("spentOn");
            String dollars = args.getString("dollars");

            dialogNameEditText.setText(name);
            dialogSpentOnEditText.setText(spentOn);
            dialogDollarsEditText.setText(dollars);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(editBudgetDialog)
                    .setTitle("Edit Budget Entry")
                    // Add action buttons
                    .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String row_id = args.getString("id");
                            String nameToUpdate = dialogNameEditText.getText().toString();
                            String spentOnToUpdate = dialogSpentOnEditText.getText().toString();
                            String dollarsToUpdate = dialogDollarsEditText.getText().toString();

                            Bundle updateArgs = new Bundle();

                            updateArgs.putString("name", nameToUpdate);
                            updateArgs.putString("spentOn", spentOnToUpdate);
                            updateArgs.putString("dollars", dollarsToUpdate);

                            //TODO: refresh the RecyclerView with the new data after update
                            mSqlUtils.updateBudgetItem(row_id, updateArgs);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditBudgetItemDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }

    public void editBudgetItem(View view){
        // view.getResources().getResourceName(view.getId()) to find which view we're in, to debug
        //need to cast each ViewParent as a View, then cast the result of findViewById as the appropriate type of view
        TextView nameTextView = (TextView)((View)view.getParent()).findViewById(R.id.name_text_view);
        TextView spentOnTextView = (TextView)((View)view.getParent()).findViewById(R.id.spent_on_text_view);
        TextView dollarsTextView = (TextView)((View)view.getParent()).findViewById(R.id.dollars_text_view);

        String nameText = nameTextView.getText().toString();
        String spentOnText = spentOnTextView.getText().toString();
        String dollarsText = dollarsTextView.getText().toString();

        String clickedId = String.valueOf(view.getTag());

        Bundle args = new Bundle();
        args.putString("id", clickedId);
        args.putString("name", nameText);
        args.putString("spentOn", spentOnText);
        args.putString("dollars", dollarsText);

        DialogFragment editDialogFragment = new EditBudgetItemDialogFragment();
        editDialogFragment.setArguments(args);
        editDialogFragment.show(getFragmentManager(), "Edit");
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
            mSqlUtils.addBudgetItem(name, dollarsSpent, spentOn);
            mAdapter.swapCursor(mSqlUtils.getAllBudgetEntries());//, lastAddedGuestId);
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