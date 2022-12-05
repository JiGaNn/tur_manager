package com.example.tur_manager.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;

public class MyDBManager {
    private Context context;
    private MyDBHelper myDBHelper;
    private SQLiteDatabase db;

    public MyDBManager (Context context) {
        this.context = context;
        myDBHelper = new MyDBHelper(context);
    }

    public void openDb(){
        db = myDBHelper.getWritableDatabase();
    }

    public void insertToDb(String title, String disc){
        ContentValues cv = new ContentValues();
        cv.put(MyConstans.TITLE, title);
        cv.put(MyConstans.DISC, disc);
        db.insert(MyConstans.TABLE_NAME, null, cv);
    }

    public List<String> getFromDb(){
        List<String> tempList = new ArrayList<>();
        Cursor cursor = db.query(MyConstans.TABLE_NAME,null,null,
                null,null,null,null);
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(MyConstans.TITLE) + 1);
            tempList.add(title);
        }
        cursor.close();
        return tempList;
    }

    public void closeDb(){
        myDBHelper.close();
    }

    public void insertToDb(String toString) {
    }
}