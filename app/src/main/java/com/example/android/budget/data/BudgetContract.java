package com.example.android.budget.data;

import android.provider.BaseColumns;

public class BudgetContract {

    public static final class BudgetEntry implements BaseColumns {
        public static final String TABLE_NAME = "budget";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DOLLARS_SPENT = "dollars_spent";
        public static final String COLUMN_SPENT_MONEY_ON = "spent_dollars_on";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
