package com.swift.swiftcourier;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserProfile.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "user_profile";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_COUNTY = "county";
    public static final String COLUMN_SUB_COUNTY = "sub_county";
    public static final String COLUMN_WARD = "ward";
    public static final String COLUMN_EMAIL_NOTIF = "email_notif";
    public static final String COLUMN_SMS_NOTIF = "sms_notif";
    public static final String COLUMN_PUSH_NOTIF = "push_notif";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_COUNTY + " TEXT, " +
                COLUMN_SUB_COUNTY + " TEXT, " +
                COLUMN_WARD + " TEXT, " +
                COLUMN_EMAIL_NOTIF + " INTEGER, " +
                COLUMN_SMS_NOTIF + " INTEGER, " +
                COLUMN_PUSH_NOTIF + " INTEGER, " +
                COLUMN_IMAGE_PATH + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}