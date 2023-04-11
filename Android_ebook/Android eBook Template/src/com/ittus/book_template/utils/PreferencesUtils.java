package com.ittus.book_template.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ittus.book_template.model.Constants;

public class PreferencesUtils {

	public static void saveBgColor(final Context context, final int bgColor) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Constants.SP_BACKGROUND_COLOR, bgColor);
		editor.commit();
	}

	public static int getBgColor(final Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		int bgColor = preferences.getInt(Constants.SP_BACKGROUND_COLOR,
				Constants.BG_COLOR_WHITE);
		return bgColor;
	}

	public static boolean getClingState(final Context context,
			final String clingDismissedKey) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		boolean clingState = preferences.getBoolean(clingDismissedKey, false);
		return clingState;
	}

	public static void setClingState(final Context context,
			final String clingDismissedKey, boolean value) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(clingDismissedKey, value);
		editor.commit();
	}

	public static void saveReadingPart(final Context context,
			final int readingPart) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Constants.SP_READING_PART, readingPart);
		editor.commit();
	}

	public static int getReadingPart(final Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		int readingPart = preferences.getInt(Constants.SP_READING_PART, 0);
		return readingPart;
	}
	
	public static void saveReadingChap(final Context context,
			final int readingChap) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Constants.SP_READING_CHAP, readingChap);
		editor.commit();
	}

	public static int getReadingChap(final Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE, 0);
		int readingChap = preferences.getInt(Constants.SP_READING_CHAP, 0);
		return readingChap;
	}
}
