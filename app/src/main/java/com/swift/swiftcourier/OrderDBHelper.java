package com.swift.swiftcourier;



import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SwiftCourier.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ORDER_ID = "order_id";

    public OrderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_ORDER_ID + " TEXT UNIQUE)";
        db.execSQL(CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    public void addOrderId(String orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderId);
        db.insert(TABLE_ORDERS, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public List<String> getAllOrderIds() {
        List<String> orderIds = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                orderIds.add(cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_ID)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderIds;
    }

    public boolean isOrderIdPresent(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDER_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{orderId});
        boolean isPresent = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isPresent;
    }
}