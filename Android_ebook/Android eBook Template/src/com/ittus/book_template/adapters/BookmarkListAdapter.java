package com.ittus.book_template.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ittus.book_template.R;
import com.ittus.book_template.model.Bookmark;
import com.ittus.book_template.provider.DataModel;

public class BookmarkListAdapter extends BaseAdapter {

	private List<Bookmark> data;
	private Context context;

	public BookmarkListAdapter(Context context, List<Bookmark> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Bookmark getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Bookmark item = getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.swipe_item, parent, false);
			holder = new ViewHolder();
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			holder.bAction = (Button) convertView.findViewById(R.id.btn_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.text1.setText(context.getText(R.string.str_part) + " "
				+ (item.partId + 1));
		holder.text2.setText(context.getText(R.string.str_chap) + " "
				+ (item.chapId + 1));

		holder.bAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				data.remove(position);
				DataModel.deleteBookmark(context, item.partId, item.chapId);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	static class ViewHolder {
		TextView text1;
		TextView text2;
		Button bAction;
	}

}
