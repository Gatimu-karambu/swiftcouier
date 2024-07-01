package com.swift.swiftcourier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "swift_courier.db";
    private static final int DATABASE_VERSION = 2; // Increment the version number

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_USER_ID = "user_id";
    public static final String COLUMN_ORDER_DESCRIPTION = "description";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_LAST_UPDATED = "last_updated";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT)";

    private static final String CREATE_TABLE_ORDERS =
            "CREATE TABLE " + TABLE_ORDERS + " (" +
                    COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_USER_ID + " INTEGER, " +
                    COLUMN_ORDER_DESCRIPTION + " TEXT, " +
                    COLUMN_ORDER_STATUS + " TEXT, " +
                    COLUMN_ORDER_LAST_UPDATED + " TEXT, " + // New column for last updated timestamp
                    "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_ORDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_ORDER_LAST_UPDATED + " TEXT");
        }
    }

    public long createOrder(int userId, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_USER_ID, userId);
        values.put(COLUMN_ORDER_DESCRIPTION, description);
        values.put(COLUMN_ORDER_STATUS, status);
        values.put(COLUMN_ORDER_LAST_UPDATED, getCurrentTimestamp());

        long orderId = db.insert(TABLE_ORDERS, null, values);
        db.close();
        return orderId;
    }

    public boolean updateOrder(long orderId, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_DESCRIPTION, description);
        values.put(COLUMN_ORDER_STATUS, status);
        values.put(COLUMN_ORDER_LAST_UPDATED, getCurrentTimestamp());

        int result = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + "=?", new String[]{String.valueOf(orderId)});
        db.close();
        return result > 0;
    }

    public boolean deleteOrder(long orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ORDERS, COLUMN_ORDER_ID + "=?", new String[]{String.valueOf(orderId)});
        db.close();
        return result > 0;
    }

    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ORDER_ID + " AS _id, " + COLUMN_ORDER_USER_ID + ", " + COLUMN_ORDER_DESCRIPTION + ", " + COLUMN_ORDER_STATUS + ", " + COLUMN_ORDER_LAST_UPDATED + " FROM " + TABLE_ORDERS;
        return db.rawQuery(query, null);
    }

    public Cursor getOrderById(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ORDERS, null, COLUMN_ORDER_ID + "=?", new String[]{String.valueOf(orderId)}, null, null, null);
    }

    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
}
