package com.example.emojimokoko.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SqliteManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "emojidb";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "myemoji";


    private static final String ID_COL = "id";

    private static final String RESOURCE_COL = "emojiId";

    private static final String NAME_COL = "emojiName";

    private static final String DATETIME_COL = "dateTimeAdded";

    private static final String TABLE_NAME2 = "allemoji";
    private static final String RESOURCE_COL2 = "emojiId";
    private static final String NAME_COL2 = "emojiName";



    public SqliteManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + RESOURCE_COL + " TEXT,"
                + DATETIME_COL + " TEXT)";
        db.execSQL(query1);

        String query2 = "CREATE TABLE " + TABLE_NAME2 + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL2 + " TEXT,"
                + RESOURCE_COL2 + " TEXT)";
        db.execSQL(query2);
    }
    public void addEmoji(String emojiId,String emojiName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(new Date());
        values.put(RESOURCE_COL, emojiId);
        values.put(NAME_COL, emojiName);
        values.put(DATETIME_COL, currentTime);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void addAllEmoji(String emojiId,String emojiName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RESOURCE_COL2, emojiId);
        values.put(NAME_COL2, emojiName);
        db.insert(TABLE_NAME2, null, values);
    }

//    public ArrayList<Integer> searchEmojiByCharacter(String character) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<Integer> result = new ArrayList<>();
//
//        // Define the query to search for records containing the specified character
//        String query = "SELECT " + RESOURCE_COL2 +
//                " FROM " + TABLE_NAME2 +
//                " WHERE " + NAME_COL2 + " LIKE '%" + character + "%'";
//
//        Cursor cursor = db.rawQuery(query, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                result.add(Integer.parseInt(cursor.getString(0)));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return result;
//    }

    public ArrayList<Integer> readEmoji() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Modify the SQL query to include the ORDER BY clause
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + DATETIME_COL + " DESC", null);

        ArrayList<Integer> arr = new ArrayList<>();
        if (cursorCourses.moveToFirst()) {
            do {
                if (!arr.contains(Integer.parseInt(cursorCourses.getString(2)))) {
                    arr.add(Integer.parseInt(cursorCourses.getString(2)));
                }
            } while (cursorCourses.moveToNext());
        }
        cursorCourses.close();
        return arr;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }
}