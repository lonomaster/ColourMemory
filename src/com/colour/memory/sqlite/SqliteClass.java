package com.colour.memory.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteClass extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ColourMemoryDB.db";
    
    private String sql = "CREATE TABLE 'people' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
    												    "'name' TEXT, " +
    												    "'score' REAL)";
    
    public SqliteClass(Context ctx){
    	super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS 'people'");
		onCreate(db);
	}
}