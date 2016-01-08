package com.sample.moneymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Database {

    public static final String TABLE_NAME_EXPENSES = "expenses";
    public static final class COLUMNS_EXPENSES {
        public static final String ID = "_id";
        public static final String DAY = "day";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String NAME = "expense_name";
        public static final String VALUE = "expense_value";
        public static final String CATEGORY = "expense_cat";
    }

    Context context;
    DBHelper dbHelper;
    SQLiteDatabase db = null;

    private String DB_NAME = "moneymanager.db";
    private int DB_VERSION = 1;

    public static final String DB_CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_NAME_EXPENSES + " ("
            + COLUMNS_EXPENSES.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMNS_EXPENSES.DAY + " INTEGER DEFAULT 0, "
            + COLUMNS_EXPENSES.MONTH + " INTEGER DEFAULT 0, "
            + COLUMNS_EXPENSES.YEAR + " INTEGER DEFAULT 0, "
            + COLUMNS_EXPENSES.NAME + " TEXT NOT NULL DEFAULT '', "
            + COLUMNS_EXPENSES.VALUE + " INTEGER DEFAULT 0, "
            + COLUMNS_EXPENSES.CATEGORY + " TEXT NOT NULL DEFAULT ''"
            + " );";
    public static final String DB_DROP_TABLE_EXPENSES = "DROP TABLE IF EXISTS " + TABLE_NAME_EXPENSES;

    public Database(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public long insertExpense(String name, int value, String category) {
        GregorianCalendar calendar = new GregorianCalendar();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMNS_EXPENSES.NAME, name);
        values.put(COLUMNS_EXPENSES.VALUE, value);
        values.put(COLUMNS_EXPENSES.CATEGORY, category);
        values.put(COLUMNS_EXPENSES.YEAR, year);
        values.put(COLUMNS_EXPENSES.DAY, day);
        values.put(COLUMNS_EXPENSES.MONTH, month);
        return db.insert(TABLE_NAME_EXPENSES, null, values);
    }

    public int getSingleExpense(String text) {
        db = dbHelper.getReadableDatabase();
        String[] columns = { COLUMNS_EXPENSES.NAME, COLUMNS_EXPENSES.VALUE  };
        String selection = "" + COLUMNS_EXPENSES.MONTH + "=? AND " + COLUMNS_EXPENSES.YEAR + "=? AND " + COLUMNS_EXPENSES.NAME + "=?";
        GregorianCalendar calendar = new GregorianCalendar();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        String[] args = {"" + month, "" + year, text};
        int total = 0;
        Cursor c = db.query(TABLE_NAME_EXPENSES, columns, selection, args, null, null, null);
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                int expense = c.getInt(c.getColumnIndex(COLUMNS_EXPENSES.VALUE));
                total += expense;
                c.moveToNext();
            }
        }
        return total;
    }

    public int getSingleExpense(String text, int month, int year) {
        db = dbHelper.getReadableDatabase();
        String[] columns = { COLUMNS_EXPENSES.NAME, COLUMNS_EXPENSES.VALUE  };
        String selection = "" + COLUMNS_EXPENSES.MONTH + "=? AND " + COLUMNS_EXPENSES.YEAR + "=? AND " + COLUMNS_EXPENSES.NAME + "=?";

        String[] args = {"" + month, "" + year, text};
        int total = 0;
        Cursor c = db.query(TABLE_NAME_EXPENSES, columns, selection, args, null, null, null);
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                int expense = c.getInt(c.getColumnIndex(COLUMNS_EXPENSES.VALUE));
                total += expense;
                c.moveToNext();
            }
        }
        return total;
    }

    public int getSingleMonthlyCategoryExpense(String text, int month, int year) {
        db = dbHelper.getReadableDatabase();
        String[] columns = { COLUMNS_EXPENSES.NAME, COLUMNS_EXPENSES.VALUE  };
        String selection = "" + COLUMNS_EXPENSES.MONTH + "=? AND " + COLUMNS_EXPENSES.YEAR + "=? AND " + COLUMNS_EXPENSES.CATEGORY + "=?";
        String[] args = {"" + month, "" + year, text};
        int total = 0;
        Cursor c = db.query(TABLE_NAME_EXPENSES, columns, selection, args, null, null, null);
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                int expense = c.getInt(c.getColumnIndex(COLUMNS_EXPENSES.VALUE));
                total += expense;
                c.moveToNext();
            }
        }
        return total;
    }

    public int getSingleYearlyCategoryExpense(String text, int year) {
        db = dbHelper.getReadableDatabase();
        String[] columns = { COLUMNS_EXPENSES.NAME, COLUMNS_EXPENSES.VALUE  };
        String selection = "" + COLUMNS_EXPENSES.YEAR + "=? AND " + COLUMNS_EXPENSES.CATEGORY + "=?";
        String[] args = {"" + year, text};
        int total = 0;
        Cursor c = db.query(TABLE_NAME_EXPENSES, columns, selection, args, null, null, null);
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                int expense = c.getInt(c.getColumnIndex(COLUMNS_EXPENSES.VALUE));
                total += expense;
                c.moveToNext();
            }
        }
        return total;
    }

    public void close() {
        try {
            if (db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TABLE_EXPENSES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DB_DROP_TABLE_EXPENSES);
            onCreate(db);
        }
    }
}
