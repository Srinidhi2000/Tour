package com.example.android.project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//To create tables
public class display_data extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="Showplaces.db";
    public static final int DATABASE_VERSION=1;

    public display_data(Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
String table="CREATE TABLE " + contract.loginEntry.TABLE_NAME + " ("
        + contract.loginEntry.rowid +" INTEGER PRIMARY KEY AUTOINCREMENT, "
        + contract.loginEntry.c1username + " TEXT, "
        + contract.loginEntry.c2password +" TEXT);";
db.execSQL(table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
db.execSQL("DROP TABLE IF EXISTS " + contract.loginEntry.TABLE_NAME);
    }
}
