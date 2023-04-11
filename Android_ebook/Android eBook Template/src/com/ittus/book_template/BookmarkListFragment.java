package com.ittus.book_template;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ittus.book_template.adapters.BookmarkListAdapter;
import com.ittus.book_template.model.Bookmark;
import com.ittus.book_template.swipelistview.SwipeListView;
import com.ittus.book_template.swipelistview.SwipeListViewListener;

public class BookmarkListFragment extends ListFragment {

	private ArrayList<Bookmark> mBookmarkList = new ArrayList<Bookmark>();

	public void setup(ArrayList<Bookmark> mBookmarkList) {
		this.mBookmarkList = mBookmarkList;
	}

	public void notifyDataChanged() {
		((BookmarkListAdapter) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BookmarkListAdapter listAdapter = new BookmarkListAdapter(
				getActivity(), mBookmarkList);
		setListAdapter(listAdapter);
		((SwipeListView) getListView())
				.setSwipeListViewListener(new SwipeListViewListener() {

					@Override
					public void onStartOpen(int position, int action,
							boolean right) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartClose(int position, boolean right) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onOpened(int position, boolean toRight) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMove(int position, float x) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onListChanged() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDismiss(int[] reverseSortedPositions) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onClosed(int position, boolean fromRight) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onClickFrontView(int position) {
						Bookmark bm = mBookmarkList.get(position);
						ReadBookActivity activity = ((ReadBookActivity) getActivity());
						activity.selectPart(bm.partId, bm.chapId);
						activity.getSlidingMenu().toggle();
					}

					@Override
					public void onClickBackView(int position) {
						((SwipeListView) getListView()).closeAnimate(position);
					}
				});
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.bookmark_list, null);
	}
}
