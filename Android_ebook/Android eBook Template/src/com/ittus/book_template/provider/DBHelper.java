package com.ittus.book_template.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "nhungphongvienvuinhon.db";
	private static final int DB_VERSION = 1;

	/* Bookmark Table */
	public static final String BM_TABLE = "bookmark";
	public static final String _ID = "_id";
	public static final String mPart = "part";
	public static final String mChap = "chap";

	// Constructors
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	// Create Databases
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + BM_TABLE
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT," + "part INTERGER,"
				+ "chap INTERGER)");
	}

	// Upgrade Database
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + BM_TABLE);
		onCreate(db);
	}
}
