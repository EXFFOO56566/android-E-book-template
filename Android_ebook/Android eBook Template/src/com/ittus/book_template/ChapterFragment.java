package com.ittus.book_template;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ittus.book_template.model.Constants;
import com.ittus.book_template.utils.PreferencesUtils;
import com.ittus.book_template.views.SimpleWebView;

public class ChapterFragment extends Fragment implements OnClickListener {

	private static final String KEY_CONTENT = "ChapterFragment:Content";

	private View mLayout;
	private SimpleWebView webView;
	private String mContent = null;

//	private View btnZoomIn;
//	private View btnZoomOut;

	public static ChapterFragment newInstance(String content) {
		ChapterFragment fragment = new ChapterFragment();
		StringBuilder builder = new StringBuilder();
		builder.append(content);
		fragment.mContent = builder.toString();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayout = inflater.inflate(R.layout.layout_detail, null);
		initData();
		return mLayout;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void initData() {
		if (mLayout != null) {
//			btnZoomIn = mLayout.findViewById(R.id.btn_zoom_in);
//			btnZoomIn.setOnClickListener(this);
//			btnZoomOut = mLayout.findViewById(R.id.btn_zoom_out);
//			btnZoomOut.setOnClickListener(this);
			webView = ((SimpleWebView) mLayout
					.findViewById(R.id.chapter_content));
			webView.getSettings().setDefaultTextEncodingName("UTF-8");
			webView.getSettings().setBuiltInZoomControls(true);
			if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			    webView.getSettings().setDisplayZoomControls(false);
	        }
			int bgColor = PreferencesUtils.getBgColor(getActivity());
			int bgColorResId = R.color.white;
			switch (bgColor) {
			case Constants.BG_COLOR_KHAKI:
				bgColorResId = R.color.khaki;
				break;
			case Constants.BG_COLOR_SEPIA:
				bgColorResId = R.color.sepia;
				break;
			default:
				bgColorResId = R.color.white;
				break;
			}
			webView.setBackgroundColor(getResources().getColor(bgColorResId));
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
				}
			});
			String[] tokens = mContent.split("@");
			webView.loadUrl("file:///android_asset/" + tokens[0] + "/"
					+ tokens[1] + ".html");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.btn_zoom_in:
//			if (!webView.zoomIn()) {
//				btnZoomIn.setEnabled(false);
//			} else {
//				if (!btnZoomOut.isEnabled()) {
//					btnZoomOut.setEnabled(true);
//				}
//			}
//			break;
//		case R.id.btn_zoom_out:
//			if (!webView.zoomOut()) {
//				btnZoomOut.setEnabled(false);
//			} else {
//				if (!btnZoomIn.isEnabled()) {
//					btnZoomIn.setEnabled(true);
//				}
//			}
//			break;
		default:
			break;
		}

	}

	public SimpleWebView getWebView() {
		return webView;
	}
}