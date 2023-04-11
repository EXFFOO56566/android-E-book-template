package com.ittus.book_template.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ittus.book_template.R;

public class CommonUtils {

	public static String convertPartId(Context context, String id,
			boolean isShort) {
		String part = id.substring("part".length(), id.length());
		return (isShort ? "P" : context.getText(R.string.str_part).toString()
				+ " ")
				+ Integer.parseInt(part);
	}

	public static String convertChapId(Context context, String id) {
		String chap = id.substring("chapter".length(), id.length());
		return context.getText(R.string.str_chap).toString() + " "
				+ Integer.parseInt(chap);
	}

	public static boolean isNetworkConnected(final Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}
		return true;
	}
}
