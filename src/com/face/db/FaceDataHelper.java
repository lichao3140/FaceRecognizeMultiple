package com.face.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FaceDataHelper extends SQLiteOpenHelper{
    public static String TAG = FaceDataHelper.class.getSimpleName();
    public static boolean DEBUG = true;
    
    private final static String CREATE_USER_TABLE = "create table tUser(id INTEGER primary key autoincrement,userId INTEGER, userName TEXT, userType INTEGER, userImage TEXT, createTime TEXT);";   
    private final static String CREATE_RECORD_TABLE = "create table tRecord(id INTEGER primary key autoincrement,userId INTEGER, userName TEXT, userType INTEGER, userImage TEXT, recResult INTEGER, recImage TEXT, createTime TEXT);";

    public FaceDataHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DEBUG) Log.d(TAG, "onCreate");
        // 第一次使用数据库时自动建表
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_RECORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DEBUG) Log.d(TAG, "onUpgrade(oldVersion=" + oldVersion + ",newVersion=" + newVersion + ")");
        if (oldVersion < newVersion)
        {
            if (DEBUG) Log.d(TAG, "oldVersion < newVersion");

            if (DEBUG) Log.e(TAG, "delete all data and recreate!");
            db.execSQL("DROP TABLE IF EXISTS tUser;");
            db.execSQL("DROP TABLE IF EXISTS tRecord;");
            
            onCreate(db);
            return;
        }
    }
}
