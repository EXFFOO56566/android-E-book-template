package com.ittus.book_template.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Provider extends ContentProvider {
	private DBHelper dbHelper;
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.ittus.book_template.provider.Provider");
	public static final Uri BM_CONTENT_URI = Uri
			.parse("content://com.ittus.book_template.provider.Provider/"
					+ DBHelper.BM_TABLE);

	private SQLiteDatabase mDB;

	@Override
	public boolean onCreate() {
		// Create Database
		dbHelper = new DBHelper(getContext());
		return (mDB != null);
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		mDB = dbHelper.getWritableDatabase();
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		String table = "";
		if (uri.toString().contains(DBHelper.BM_TABLE)) {
			table = DBHelper.BM_TABLE;
		}
		long id = mDB.insert(table, null, values);
		if (id > 0) {
			Uri newUri = null;
			if (uri.toString().contains(DBHelper.BM_TABLE)) {
				newUri = ContentUris.withAppendedId(BM_CONTENT_URI, id);
			}
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		throw new SQLException("Failed to insert id into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {
		mDB = dbHelper.getReadableDatabase();
		String table = "";
		if (uri.toString().contains(DBHelper.BM_TABLE)) {
			table = DBHelper.BM_TABLE;
		}
		return mDB.query(table, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		mDB = dbHelper.getWritableDatabase();
		String table = "";
		if (uri.toString().contains(DBHelper.BM_TABLE)) {
			table = DBHelper.BM_TABLE;
		}
		int num = mDB.update(table, values, selection, selectionArgs);
		if (num == 0) {
			mDB.insert(table, null, values);
		}
		return 1;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mDB = dbHelper.getWritableDatabase();
		String table = "";
		if (uri.toString().contains(DBHelper.BM_TABLE)) {
			table = DBHelper.BM_TABLE;
		}
		int num = mDB.delete(table, selection, selectionArgs);
		if (num == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}
}
