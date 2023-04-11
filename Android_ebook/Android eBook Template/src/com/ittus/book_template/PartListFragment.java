package com.ittus.book_template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.ittus.book_template.adapters.SimpleExpandableListAdapter;
import com.ittus.book_template.model.Chapter;
import com.ittus.book_template.model.Part;
import com.ittus.book_template.utils.CommonUtils;

public class PartListFragment extends ExpandableListFragment {
	private static final String TITLE = "TITLE";
	private static final String DESCRIPTION = "DESCRIPTION";
	private ArrayList<Part> partList = new ArrayList<Part>();

	public void setup(ArrayList<Part> partList) {
		this.partList = partList;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.part_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
		for (int i = 0; i < partList.size(); i++) {
			Map<String, String> curGroupMap = new HashMap<String, String>();
			groupData.add(curGroupMap);
			Part part = partList.get(i);
			curGroupMap.put(TITLE,
					CommonUtils.convertPartId(getActivity(), part.id, false));
			curGroupMap.put(DESCRIPTION, part.title);

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for (int j = 0; j < partList.get(i).chapters.size(); j++) {
				Map<String, String> curChildMap = new HashMap<String, String>();
				children.add(curChildMap);
				Chapter chapter = partList.get(i).chapters.get(j);
				curChildMap.put(TITLE,
						CommonUtils.convertChapId(getActivity(), chapter.id));
				curChildMap.put(DESCRIPTION, chapter.title);
			}
			childData.add(children);
		}
		// Set up our adapter
		ExpandableListAdapter adapter = new SimpleExpandableListAdapter(
				getActivity(), groupData, R.layout.partlist_header,
				new String[] { TITLE, DESCRIPTION }, new int[] { R.id.text1,
						R.id.text2 }, childData, R.layout.partlist_chap_header,
				new String[] { TITLE, DESCRIPTION }, new int[] { R.id.text1,
						R.id.text2 });
		setListAdapter(adapter);
		getExpandableListView().setGroupIndicator(null);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View view,
			int parentPos, int pos, long id) {
		ReadBookActivity activity = ((ReadBookActivity) getActivity());
		activity.selectPart(parentPos, pos);
		activity.getSlidingMenu().toggle();
		return true;
	}

}
