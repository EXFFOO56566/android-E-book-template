package com.ittus.book_template.adapters;

import android.support.v4.app.FragmentManager;

public class TitleFragmentAdapter extends FragmentAdapter {
	public TitleFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return FragmentAdapter.CONTENT.get(position);
	}
}