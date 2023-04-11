package com.ittus.book_template;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ittus.book_template.adapters.SimpleExpandableListAdapter;
import com.ittus.book_template.adapters.TitleFragmentAdapter;
import com.ittus.book_template.model.Bookmark;
import com.ittus.book_template.model.Constants;
import com.ittus.book_template.model.Part;
import com.ittus.book_template.provider.DataModel;
import com.ittus.book_template.utils.CommonUtils;
import com.ittus.book_template.utils.PreferencesUtils;
import com.ittus.book_template.views.Cling;
import com.ittus.book_template.views.TitlePageIndicator;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class ReadBookActivity extends SlidingFragmentActivity implements
		OnClickListener {

	private static final int DISMISS_CLING_DURATION = 250;
	protected PartListFragment mFrag;
	protected BookmarkListFragment mSecondFrag;
	private TitleFragmentAdapter mAdapter;
	private ViewPager mPager;
	private TitlePageIndicator mIndicator;
	private ArrayList<Part> mPartList = new ArrayList<Part>();

	private boolean isFullScreen;
	private boolean isShowBgSelect;
	private int screenWidth;
	private InterstitialAd interstitial;
	// private CanvasTransformer mTransformer;
	private int mCurrentPart;

	public int getCurrentPart() {
		return mCurrentPart;
	}

	private int mCurrentChap;

	public int getCurrentChap() {
		return mCurrentChap;
	}

	private ArrayList<Bookmark> mBookmarkList = new ArrayList<Bookmark>();

	private RelativeLayout mControllerLayout;
	private ImageButton mFullScreenButton;
	private LinearLayout mBgSelectLayout;
	private LinearLayout mBtnBgSelect;
	private ObjectAnimator mControllerAnimator;
	private AnimatorSet mBgSelectAnimator;
	private ImageView mBtnBookmarkView;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Display display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();

		mControllerLayout = (RelativeLayout) findViewById(R.id.controller);
		mBgSelectLayout = (LinearLayout) findViewById(R.id.layout_background);
		mFullScreenButton = (ImageButton) findViewById(R.id.btn_fullscreen);
		mBtnBgSelect = (LinearLayout) findViewById(R.id.btn_background);
		mBtnBookmarkView = (ImageView) findViewById(R.id.btn_bookmark_view);

		mBookmarkList = DataModel.getBookmarkList(this);

		ReadBookApplication app = (ReadBookApplication) getApplication();
		mPartList = app.getModel().getPartList();

		// mTransformer = new CanvasTransformer() {
		// @Override
		// public void transformCanvas(Canvas canvas, float percentOpen) {
		// canvas.scale(percentOpen, 1, 0, 0);
		// }
		// };
		setupSlidingMenu();
		mCurrentPart = PreferencesUtils.getReadingPart(this);
		mCurrentChap = PreferencesUtils.getReadingChap(this);
		loadPart(mCurrentPart, mCurrentChap);

		showFirstRunMainCling();
		hideBgSelector(false);
		hideControllers();

		// ads
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getResources().getString(
				R.string.full_ad_unit_id));

		// Create ad request.
		AdRequest adRequestFull = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequestFull);
	}

	// Invoke displayInterstitial() when you are ready to display an
	// interstitial.
	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}else{
			// Create ad request.
			AdRequest adRequestFull = new AdRequest.Builder().build();

			// Begin loading your interstitial.
			interstitial.loadAd(adRequestFull);
		}
	}

	public void loadPart(int parentPos, int pos) {
		mCurrentPart = parentPos;
		mCurrentChap = pos;
		mAdapter = new TitleFragmentAdapter(getSupportFragmentManager());
		mAdapter.setupContent(this, mPartList.get(parentPos));

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setCurrentItem(Math.min(pos, mAdapter.getCount() - 1));
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {
				setupSMTouchMode(pos);
				mCurrentChap = pos;
				updateExpandableListViewSelection();
				boolean isBookmarked = false;
				for (Bookmark bm : mBookmarkList) {
					if (bm.partId == mCurrentPart && bm.chapId == mCurrentChap) {
						isBookmarked = true;
						break;
					}
				}
				if (isBookmarked) {
					mBtnBookmarkView.setImageLevel(1);
				} else {
					mBtnBookmarkView.setImageLevel(0);
				}
				// checkInternetConnection();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		setupSMTouchMode(pos);
		updateExpandableListViewSelection();
		boolean isBookmarked = false;
		for (Bookmark bm : mBookmarkList) {
			if (bm.partId == mCurrentPart && bm.chapId == mCurrentChap) {
				isBookmarked = true;
				break;
			}
		}
		if (isBookmarked) {
			mBtnBookmarkView.setImageLevel(1);
		} else {
			mBtnBookmarkView.setImageLevel(0);
		}
	}

	private void showController() {
		mControllerLayout.setVisibility(View.VISIBLE);
		mControllerAnimator = ObjectAnimator.ofFloat(mControllerLayout,
				"translationX", 0);
		mControllerAnimator.setDuration(300);
		mControllerAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mFullScreenButton.setEnabled(true);
				mFullScreenButton.setImageLevel(0);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		mControllerAnimator.start();
	}

	private void hideController() {
		mControllerAnimator = ObjectAnimator.ofFloat(mControllerLayout,
				"translationX", -screenWidth);
		mControllerAnimator.setDuration(300);
		mControllerAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mControllerLayout.setVisibility(View.GONE);
				mFullScreenButton.setEnabled(true);
				mFullScreenButton.setImageLevel(1);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		mControllerAnimator.start();
	}

	private void showBgSelector() {
		mBgSelectAnimator = new AnimatorSet();
		mBgSelectLayout.setVisibility(View.VISIBLE);
		ObjectAnimator transY = ObjectAnimator.ofFloat(mBgSelectLayout,
				"translationY", 0);
		transY.setDuration(500);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mBgSelectLayout,
				"alpha", 0f, 1f);
		fadeIn.setDuration(500);
		mBgSelectAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mBtnBgSelect.setEnabled(true);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		mBgSelectAnimator.playTogether(transY, fadeIn);
		mBgSelectAnimator.start();
	}

	private void hideBgSelector(boolean animate) {
		if (animate) {
			mBgSelectAnimator = new AnimatorSet();
			int height = getResources().getDimensionPixelSize(
					R.dimen.button_size) * 3;
			ObjectAnimator transY = ObjectAnimator.ofFloat(mBgSelectLayout,
					"translationY", -height);
			transY.setDuration(500);
			ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mBgSelectLayout,
					"alpha", 1f, 0f);
			fadeOut.setDuration(500);
			mBgSelectAnimator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mBgSelectLayout.setVisibility(View.GONE);
					mBtnBgSelect.setEnabled(true);
					mBtnBgSelect.setSelected(false);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub

				}
			});
			mBgSelectAnimator.playTogether(transY, fadeOut);
			mBgSelectAnimator.start();
		} else {
			mBgSelectLayout.setVisibility(View.GONE);
			mBtnBgSelect.setEnabled(true);
			mBtnBgSelect.setSelected(false);
		}
	}

	private void updateExpandableListViewSelection() {
		SimpleExpandableListAdapter adapter = (SimpleExpandableListAdapter) mFrag
				.getExpandableListAdapter();
		if (adapter != null) {
			adapter.setSelected(mCurrentPart, mCurrentChap);
			adapter.notifyDataSetChanged();
		}
	}

	private void setupSMTouchMode(int pos) {
		switch (pos) {
		default:
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			break;
		}
	}

	private void setupSlidingMenu() {
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction fragTrans = this.getSupportFragmentManager()
				.beginTransaction();
		mFrag = new PartListFragment();
		mFrag.setup(mPartList);
		fragTrans.replace(R.id.menu_frame, mFrag);
		fragTrans.commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT_RIGHT);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setBehindScrollScale(0.0f);
		// sm.setBehindCanvasTransformer(mTransformer);
		sm.setBackgroundColor(Color.DKGRAY);
		sm.setSecondaryMenu(R.layout.secondary_menu_frame);
		FragmentTransaction secondFragTrans = this.getSupportFragmentManager()
				.beginTransaction();
		mSecondFrag = new BookmarkListFragment();
		mSecondFrag.setup(mBookmarkList);
		secondFragTrans.replace(R.id.secondary_menu_frame, mSecondFrag);
		secondFragTrans.commit();
		sm.setSecondaryShadowDrawable(R.drawable.secondary_shadow);

		setSlidingActionBarEnabled(false);
	}

	@Override
	public void onClick(View v) {
		int currentBgColor = PreferencesUtils.getBgColor(this);
		switch (v.getId()) {
		case R.id.btn_home:
			if (isShowBgSelect) {
				hideBgSelector(false);
				isShowBgSelect = false;
			}
			dismissMainCling(v);
			toggle();
			break;
		case R.id.btn_bookmark:
			if (isShowBgSelect) {
				hideBgSelector(false);
				isShowBgSelect = false;
			}
			boolean isBookmarked = false;
			for (Bookmark bm : mBookmarkList) {
				if (bm.partId == mCurrentPart && bm.chapId == mCurrentChap) {
					isBookmarked = true;
					break;
				}
			}
			if (isBookmarked) {
				showUnBookmarkDialog(mCurrentPart, mCurrentChap);
			} else {
				showSaveBookmarkDialog(mCurrentPart, mCurrentChap);
			}
			break;
		case R.id.btn_bm_list:
			if (isShowBgSelect) {
				hideBgSelector(false);
				isShowBgSelect = false;
			}
			// toggleSecondary();
			break;
		case R.id.btn_background:
			if (!isShowBgSelect) {
				showBgSelector();
				isShowBgSelect = true;
			} else {
				hideBgSelector(true);
				isShowBgSelect = false;
			}
			mBtnBgSelect.setEnabled(false);
			mBtnBgSelect.setSelected(true);
			break;
		case R.id.btn_fullscreen:
			if (isShowBgSelect) {
				hideBgSelector(false);
				isShowBgSelect = false;
			}
			if (isFullScreen) {
				showController();
				mFullScreenButton.setEnabled(false);
				isFullScreen = false;
			} else {
				hideController();
				isFullScreen = true;
			}
			break;
		case R.id.btn_home_slidingmenu_left:
			toggle();
			break;
		case R.id.btn_home_slidingmenu_right:
			// toggleSecondary();
			break;
		case R.id.btn_bg_white:
			if (isShowBgSelect) {
				hideBgSelector(false);
				isShowBgSelect = false;
			}
			if (currentBgColor != Constants.BG_COLOR_WHITE) {
				PreferencesUtils.saveBgColor(this, Constants.BG_COLOR_WHITE);
				loadPart(mCurrentPart, mCurrentChap);
			}
			break;
		case R.id.btn_bg_khaki:
			if (isShowBgSelect) {
				hideBgSelector(false);
				isShowBgSelect = false;
			}
			if (currentBgColor != Constants.BG_COLOR_KHAKI) {
				PreferencesUtils.saveBgColor(this, Constants.BG_COLOR_KHAKI);
				loadPart(mCurrentPart, mCurrentChap);
			}
			break;
		case R.id.btn_bg_sepia:
			if (isShowBgSelect) {
				hideBgSelector(false);
				isShowBgSelect = false;
			}
			if (currentBgColor != Constants.BG_COLOR_SEPIA) {
				PreferencesUtils.saveBgColor(this, Constants.BG_COLOR_SEPIA);
				loadPart(mCurrentPart, mCurrentChap);
			}
			break;
		case R.id.btn_about:
			showAboutDialog();
			break;
		case R.id.tv_contact_us:
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources()
					.getString(R.string.dialog_about_email) });
			email.putExtra(Intent.EXTRA_SUBJECT,
					getText(R.string.contact_prefix) + " "
							+ getText(R.string.app_name));
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email,
					getText(R.string.contact_chooser)));
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int currentBgColor = PreferencesUtils.getBgColor(this);
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.menu_bookmark:
			boolean isBookmarked = false;
			for (Bookmark bm : mBookmarkList) {
				if (bm.partId == mCurrentPart && bm.chapId == mCurrentChap) {
					isBookmarked = true;
					break;
				}
			}
			if (isBookmarked) {
				showUnBookmarkDialog(mCurrentPart, mCurrentChap);
			} else {
				showSaveBookmarkDialog(mCurrentPart, mCurrentChap);
			}
			return true;
		case R.id.menu_bookmarked_chap:
			// toggleSecondary();
			return true;
		case R.id.menu_bg_white:
			if (currentBgColor != Constants.BG_COLOR_WHITE) {
				PreferencesUtils.saveBgColor(this, Constants.BG_COLOR_WHITE);
				loadPart(mCurrentPart, mCurrentChap);
			}
			return true;
		case R.id.menu_bg_khaki:
			if (currentBgColor != Constants.BG_COLOR_KHAKI) {
				PreferencesUtils.saveBgColor(this, Constants.BG_COLOR_KHAKI);
				loadPart(mCurrentPart, mCurrentChap);
			}
			return true;
		case R.id.menu_bg_sepia:
			if (currentBgColor != Constants.BG_COLOR_SEPIA) {
				PreferencesUtils.saveBgColor(this, Constants.BG_COLOR_SEPIA);
				loadPart(mCurrentPart, mCurrentChap);
			}
			return true;
		case R.id.menu_about:
			showAboutDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (isShowBgSelect) {
			hideBgSelector(false);
			isShowBgSelect = false;
		}
		return super.onPrepareOptionsMenu(menu);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private void checkInternetConnection() {
		if (!CommonUtils.isNetworkConnected(this)) {
			showNoConnectionDialog();
		}
	}

	private boolean isClickTry = false;

	private void showNoConnectionDialog() {
		if (isShowBgSelect) {
			hideBgSelector(false);
			isShowBgSelect = false;
		}
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(getText(R.string.dialog_no_connection_title))
				.setIcon(R.drawable.ic_del)
				.setMessage(getText(R.string.dialog_no_connection_msg))
				.setPositiveButton(getText(R.string.dialog_exit_positive),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						})
				.setNegativeButton(getText(R.string.dialog_no_connection_try),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								isClickTry = true;
								checkInternetConnection();
							}
						}).create();
		alertDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (!isClickTry) {
					checkInternetConnection();
				} else {
					isClickTry = false;
				}
			}
		});
		alertDialog.show();
	}

	public void showSaveBookmarkDialog(final int partId, final int chapId) {
		if (isShowBgSelect) {
			hideBgSelector(false);
			isShowBgSelect = false;
		}
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(getText(R.string.dialog_bm_title))
				.setIcon(R.drawable.bookmark_folder)
				.setMessage(getText(R.string.dialog_bm_msg))
				.setPositiveButton(getText(R.string.dialog_bm_pos),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								DataModel.addBookmark(ReadBookActivity.this,
										partId, chapId);
								Bookmark bm = new Bookmark(partId, chapId);
								mBookmarkList.add(bm);
								mBtnBookmarkView.setImageLevel(1);
								mSecondFrag.notifyDataChanged();
								PreferencesUtils.saveReadingPart(
										ReadBookActivity.this, mCurrentPart);
								PreferencesUtils.saveReadingChap(
										ReadBookActivity.this, mCurrentChap);
							}
						})
				.setNegativeButton(getText(R.string.dialog_bm_neg),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create();
		alertDialog.show();
	}

	public void showUnBookmarkDialog(final int partId, final int chapId) {
		if (isShowBgSelect) {
			hideBgSelector(false);
			isShowBgSelect = false;
		}
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(getText(R.string.dialog_un_bm_title))
				.setIcon(R.drawable.bookmark_folder)
				.setMessage(getText(R.string.dialog_un_bm_msg))
				.setPositiveButton(getText(R.string.dialog_bm_pos),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								DataModel.deleteBookmark(ReadBookActivity.this,
										partId, chapId);
								for (Bookmark bm : mBookmarkList) {
									if (bm.partId == partId
											&& bm.chapId == chapId) {
										mBookmarkList.remove(bm);
										break;
									}
								}
								mBtnBookmarkView.setImageLevel(0);
								mSecondFrag.notifyDataChanged();
							}
						})
				.setNegativeButton(getText(R.string.dialog_bm_neg),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create();
		alertDialog.show();
	}

	public void showAboutDialog() {
		if (isShowBgSelect) {
			hideBgSelector(false);
			isShowBgSelect = false;
		}
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_about, null, false);
		TextView tvContactUs = (TextView) view.findViewById(R.id.tv_contact_us);
		tvContactUs.setClickable(true);
		tvContactUs.setOnClickListener(this);
		String email = getResources().getString(R.string.dialog_about_email);
		final String dev_name = getResources().getString(
				R.string.developer_name);
		String text = getText(R.string.dialog_about_contact) + " "
				+ "<a href='" + email + "'>" + email + "</a>";
		tvContactUs.setText(Html.fromHtml(text));
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(getText(R.string.menu_about))
				.setIcon(R.drawable.ic_launcher)
				.setView(view)
				.setPositiveButton(getText(R.string.dialog_bm_pos),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						})
				.setNegativeButton(getText(R.string.dialog_about_neg),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri
										.parse("market://search?q=pub:"
												+ dev_name));
								startActivity(intent);
							}
						})
				.setNeutralButton(getText(R.string.dialog_about_neu),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse("market://details?id="
										+ getPackageName()));
								startActivity(intent);
							}
						}).create();
		alertDialog.show();
	}

	public void showFirstRunMainCling() {
		// Enable the clings only if they have not been dismissed before
		if (!PreferencesUtils.getClingState(this, Cling.CLING_DISMISSED_KEY)) {
			int pos = getResources().getDimensionPixelSize(R.dimen.button_size);
			initCling(R.id.main_cling, new int[] { pos / 2, pos / 2 }, false, 0);
		} else {
			removeCling(R.id.main_cling);
		}
	}

	private Cling initCling(int clingId, int[] positionData, boolean animate,
			int delay) {
		final Cling cling = (Cling) findViewById(clingId);
		if (cling != null) {
			cling.init(this, positionData);
			cling.setVisibility(View.VISIBLE);
			cling.setFocusableInTouchMode(true);
			cling.post(new Runnable() {
				public void run() {
					cling.setFocusable(true);
					cling.requestFocus();
				}
			});
		}
		return cling;
	}

	private void dismissCling(final Cling cling, final String flag, int duration) {
		if (cling != null && cling.getVisibility() == View.VISIBLE) {
			cling.setVisibility(View.GONE);
			cling.cleanup();
			// We should update the shared preferences on a background
			// thread
			new Thread("dismissClingThread") {
				public void run() {
					PreferencesUtils.setClingState(ReadBookActivity.this,
							Cling.CLING_DISMISSED_KEY, true);
				}
			}.start();
		}
	}

	private void removeCling(int id) {
		final View cling = findViewById(id);
		if (cling != null) {
			final ViewGroup parent = (ViewGroup) cling.getParent();
			parent.post(new Runnable() {
				@Override
				public void run() {
					parent.removeView(cling);
				}
			});
		}
	}

	public void dismissMainCling(View v) {
		Cling cling = (Cling) findViewById(R.id.main_cling);
		dismissCling(cling, Cling.CLING_DISMISSED_KEY, DISMISS_CLING_DURATION);
	}

	public void selectPart(int parentPos, int pos) {
		if (mCurrentPart == parentPos && mCurrentChap == pos) {
			Toast.makeText(this, R.string.msg_alert_2, Toast.LENGTH_SHORT)
					.show();
		} else {
			if (mCurrentPart == parentPos) {
				mIndicator
						.setCurrentItem(Math.min(pos, mAdapter.getCount() - 1));
				mCurrentChap = pos;
			} else {
				loadPart(parentPos, pos);
			}
		}
	}

	@Override
	public void onBackPressed() {
		displayInterstitial();
		
//		if (true)
//			return;
		if (isShowBgSelect) {
			hideBgSelector(false);
			isShowBgSelect = false;
		}
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(getText(R.string.dialog_exit_title))
				.setIcon(R.drawable.ic_launcher)
				.setMessage(getText(R.string.dialog_exit_msg))
				.setPositiveButton(getText(R.string.dialog_exit_positive),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								PreferencesUtils.saveReadingPart(
										ReadBookActivity.this, 0);
								PreferencesUtils.saveReadingChap(
										ReadBookActivity.this, 0);
								finish();
							}
						})
				.setNegativeButton(getText(R.string.dialog_exit_negative),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setNeutralButton(getText(R.string.dialog_exit_neutral),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								PreferencesUtils.saveReadingPart(
										ReadBookActivity.this, mCurrentPart);
								PreferencesUtils.saveReadingChap(
										ReadBookActivity.this, mCurrentChap);
								finish();
							}
						}).create();
		alertDialog.show();
		
	}

	Handler hideHandler = new Handler();

	private Runnable hideThread = new Runnable() {

		public void run() {
			mFullScreenButton.setVisibility(View.GONE);
		}
	};

	public void hideControllers() {
		hideHandler.postDelayed(hideThread, 3000);
	}

	public void showControllers() {
		mFullScreenButton.setVisibility(View.VISIBLE);
		hideHandler.removeCallbacks(hideThread);
		hideControllers();
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		if (mFullScreenButton.getVisibility() == View.VISIBLE) {
			hideHandler.removeCallbacks(hideThread);
			hideControllers();
		} else {
			showControllers();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
