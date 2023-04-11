package com.ittus.book_template.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.ittus.book_template.ChapterFragment;
import com.ittus.book_template.model.Part;
import com.ittus.book_template.utils.CommonUtils;

public class FragmentAdapter extends FragmentStatePagerAdapter {
	protected static ArrayList<String> CONTENT;

	private int mCount;
	private Part mPart;

	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		return ChapterFragment.newInstance(mPart.id + "@"
				+ mPart.chapters.get(position).id);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	public void setupContent(Context context, Part part) {
		mPart = part;
		mCount = part.chapters.size();
		CONTENT = new ArrayList<String>();
		for (int i = 0; i < mCount; i++) {
			String strPart = CommonUtils.convertPartId(context, part.id, true);
			String strChap = CommonUtils.convertChapId(context,
					part.chapters.get(i).id);
			CONTENT.add(strPart + "_" + strChap);
		}
		notifyDataSetChanged();
	}
}