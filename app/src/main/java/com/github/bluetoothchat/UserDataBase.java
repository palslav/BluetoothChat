package com.github.bluetoothchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pallav.choudhary on 11-07-2017.
 */

public class UserDataBase extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "btChatDB.db";

    public static final String TABLE_USER = "user";
    //public static final String COLUMN_USER_HANDLE = "user_handle";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_STATUS = "user_status";
    public static final String COLUMN_USER_DEVICE_ID = "user_id";
    //public static final String COLUMN_USER_OTHER_DETAILS = "user_other_details";

    public UserDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_USER + "(" + COLUMN_USER_DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT, " + COLUMN_USER_STATUS + " TEXT " + ");" ;
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(sqLiteDatabase);
    }

    public void addUser(User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_NAME, user.getUserName());
        contentValues.put(COLUMN_USER_STATUS, user.getUserStatus());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_USER, null, contentValues);
        db.close();
    }

    public void deleteUser(String userID){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER + " WHERE " + COLUMN_USER_DEVICE_ID + "\"" + userID + "\";");
    }

    public String dbToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("user_name"))!=null) { // || c.getString(c.getColumnIndex("")))
                dbString += c.getString(c.getColumnIndex("user_id"));
                dbString += " ";
                dbString += c.getString((c.getColumnIndex("user_name")));
                dbString += " ";
                dbString += c.getString(c.getColumnIndex("user_status"));
                dbString += " ";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }
    /*public static final String TABLE_MSG = "msg";
    public static final String COLUMN_MSG_HANDLE = "msg_handle";
    public static final String COLUMN_MSG_DIRECTION = "msg_direction";
    public static final String COLUMN_MSG_CONTENT = "msg_content";
    public static final String COLUMN_MSG_USER_HANDLE = "msg_user_handle";
    public static final String COLUMN_MSG_OTHER_DETAILS = "msg_other_details";*/

}
