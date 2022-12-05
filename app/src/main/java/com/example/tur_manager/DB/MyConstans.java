package com.example.tur_manager.DB;

public class MyConstans {
    public static final String TABLE_NAME = "my_table";
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DISC = "disc";
    public static final String DB_NAME = "my_db.db";
    public static final int DB_VERSION = 4;
    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT," +
            DISC + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}