package com.ittus.book_template.provider;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ittus.book_template.model.Bookmark;

public class DataModel {

	public static ArrayList<Bookmark> getBookmarkList(Context context) {
		ArrayList<Bookmark> itemList = new ArrayList<Bookmark>();
		Cursor c = context.getContentResolver().query(Provider.BM_CONTENT_URI,
				null, null, null, null);
		try {
			while (c.moveToNext()) {
				int partId = c.getInt(c.getColumnIndex(DBHelper.mPart));
				int chapId = c.getInt(c.getColumnIndex(DBHelper.mChap));
				Bookmark item = new Bookmark(partId, chapId);
				itemList.add(item);
			}
		} catch (Exception e) {
			Log.e("DataModel", "Load item list failed " + e.getMessage());
		} finally {
			if (c != null)
				c.close();
		}
		return itemList;
	}

	public static void addBookmark(Context context, int partId, int chapId) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.mPart, partId);
		contentValues.put(DBHelper.mChap, chapId);
		context.getContentResolver().insert(Provider.BM_CONTENT_URI,
				contentValues);
	}

	public static void deleteBookmark(Context context, int partId, int chapId) {
		context.getContentResolver().delete(Provider.BM_CONTENT_URI,
				DBHelper.mPart + " =? AND " + DBHelper.mChap + " =? ",
				new String[] { "" + partId, "" + chapId });
	}
}
