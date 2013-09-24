package com.example.kaustubh.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatusData {

	public static final String TAG = "StatusData";

	public static final String _DATABASE = "timeline.db";
	public static final int _VERSION = 1;
	public static final String _TABLE = "timeline";
	public static final String C_ID = "_id";
	public static final String C_CREATED_AT = "created_at";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	
	public static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	public static final String[] MAX_CREATED_AT_COLUMNS = {	"max(" + StatusData.C_CREATED_AT + ")" };
	public static final String[] DB_TEXT_COLUMNS = { "C_TEXT" };
	
	class DbHelper extends SQLiteOpenHelper {
		/*
		 * Helps us deal with SQLite without using sql
		 */
		public DbHelper(Context context) {
			super(context, _DATABASE, null, _VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Creating database: " + _DATABASE);
			String sql = "create table " + _TABLE + "(" + C_ID + " int primary key, " +
			C_CREATED_AT + " int, " + C_USER + " text, " + C_TEXT + " text)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table " + _TABLE);
			this.onCreate(db);
		}
		
	}

	final DbHelper dbHelper;

	public StatusData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.d(TAG, "Initializing data");
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public void delete() {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		db.delete(_TABLE, null, null);
		db.close();
	}
	
	public void insertOrIgnore(ContentValues values) {
		Log.d(TAG, "Before getting db");
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		// Calls the onCreate methods in DbHelper if no database present
		Log.d(TAG, "After getting db");
		if (db == null) Log.d(TAG, "DB is null");
		try {
			// Inserts it and ignores it if there is a conflict (entry already exists)
			Log.d(TAG, "before insertWithoutConflict");
			db.insertWithOnConflict(_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
			Log.d(TAG, "before insertWithoutConflict");
		} finally {
			// Very important to close database or else other Activities wouldn't be able to use this shared resource
			db.close();
		}
	}
	
	public Cursor getStatusUpdates() {
		
		/*
		 * We are not closing the database in this method or else the cursor would become invalid.
		 * Might be better to save the values got from the cursor in some container and then return it,
		 * or make sure that StatusData.close() is called from the other Activity after using the cursor.
		 */
		
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(_TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	public long getLatestStatusCreatedAtTime() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(_TABLE, MAX_CREATED_AT_COLUMNS, null, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
				/*
				 * You get the cursor to just before your first entry. This advances the cursor and checks if it is valid:
				 * If true, then returns the _ID as a long of the top entry (the max)
				 */
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
		
	}
	
	public String getStatusTextByID(long id) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(_TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id, null, null, null, null);
			/*
			 * The second entry is always a String[] - the columns you want to have in your resultSet
			 */
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
}
