package com.example.android.budget;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.budget.data.BudgetContract;
import com.example.android.budget.R;


public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    // Holds on to the cursor to display the waitlist
    private Cursor mCursor;
    private Context mContext;
    private long mLastAddedGuestId;

    /**
     * Constructor using the context and the db cursor
     * @param context the calling context/activity
     * @param cursor the db cursor with waitlist data to display
     */
    public BudgetAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public BudgetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.budget_list_item, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BudgetViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        String name = mCursor.getString(mCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME));
        int dollarsSpent = mCursor.getInt(mCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_DOLLARS_SPENT));
        String spentOn = mCursor.getString(mCursor.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_SPENT_MONEY_ON));

        long id = mCursor.getLong(mCursor.getColumnIndex(BudgetContract.BudgetEntry._ID));

        // Display the guest name
        holder.nameTextView.setText(name);
        // Display the party count
        holder.dollarTextView.setText(String.valueOf(dollarsSpent));
        // Display spent on what
        holder.spentOnTextView.setText(spentOn);

//        holder.itemView.setTag(mLastAddedGuestId);
        holder.itemView.setTag(id);
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){//, long lastAddedGuestId){
//        mLastAddedGuestId = lastAddedGuestId;
        if(mCursor != null)
            mCursor.close();
        mCursor = newCursor;
        if(mCursor != null)
            this.notifyDataSetChanged();
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class BudgetViewHolder extends RecyclerView.ViewHolder {

        // Will display the guest name
        TextView nameTextView;
        // Will display the dollars spent
        TextView dollarTextView;
        // Will display what money was spent on
        TextView spentOnTextView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews
         *
         * @param itemView The View that you inflated in
         *                 {@link BudgetAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public BudgetViewHolder (View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            dollarTextView = (TextView) itemView.findViewById(R.id.dollars_text_view);
            spentOnTextView = (TextView) itemView.findViewById(R.id.spent_on_text_view);
        }

    }
}