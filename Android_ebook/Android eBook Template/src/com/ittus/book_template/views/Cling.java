/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ittus.book_template.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.FocusFinder;
import android.view.View;
import android.widget.FrameLayout;

import com.ittus.book_template.R;
import com.ittus.book_template.ReadBookActivity;

public class Cling extends FrameLayout {

    public static final String CLING_DISMISSED_KEY = "cling.dismissed";

    private ReadBookActivity mLauncher;
    private boolean mIsInitialized;
    private String mDrawIdentifier;
    private Drawable mBackground;
    private Drawable mPunchThroughGraphic;
    private Drawable mHandTouchGraphic;
    private int mPunchThroughGraphicCenterRadius;
    // private int mButtonBarHeight;
    private float mRevealRadius;
    private int[] mPositionData;

    private static String MAIN_ACTIVITY = "main_activity";
    private static String SLIDINGMENU_LEFT = "slidingmenu_left";
    private static String SLIDINGMENU_RIGHT = "slidingmenu_right";

    private Paint mErasePaint;

    public Cling(Context context) {
        this(context, null, 0);
    }

    public Cling(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cling(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Cling,
                defStyle, 0);
        mDrawIdentifier = a.getString(R.styleable.Cling_drawIdentifier);
        a.recycle();

        setClickable(true);
    }

    public void init(ReadBookActivity l, int[] positionData) {
        if (!mIsInitialized) {
            mLauncher = l;
            mPositionData = positionData;

            Resources r = getContext().getResources();

            mPunchThroughGraphic = r.getDrawable(R.drawable.cling);
            mPunchThroughGraphicCenterRadius = r
                    .getDimensionPixelSize(R.dimen.clingPunchThroughGraphicCenterRadius);
            mRevealRadius = r.getDimensionPixelSize(R.dimen.reveal_radius) * 1f;
            // mButtonBarHeight = r
            // .getDimensionPixelSize(R.dimen.button_bar_height);

            mErasePaint = new Paint();
            mErasePaint.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.MULTIPLY));
            mErasePaint.setColor(0xFFFFFF);
            mErasePaint.setAlpha(0);

            mIsInitialized = true;
        }
    }

    public void cleanup() {
        mBackground = null;
        mPunchThroughGraphic = null;
        mHandTouchGraphic = null;
        mIsInitialized = false;
    }

    public String getDrawIdentifier() {
        return mDrawIdentifier;
    }

    private int[] getPunchThroughPositions() {
        if (mDrawIdentifier.equals(MAIN_ACTIVITY)) {
            return mPositionData;
        } else if (mDrawIdentifier.equals(SLIDINGMENU_LEFT)) {
        	
        } else if (mDrawIdentifier.equals(SLIDINGMENU_RIGHT)) {
        	
        }
        return new int[] { -1, -1 };
    }

    @Override
    public View focusSearch(int direction) {
        return this.focusSearch(this, direction);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        return FocusFinder.getInstance()
                .findNextFocus(this, focused, direction);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (mDrawIdentifier.equals(MAIN_ACTIVITY)) {
            int[] positions = getPunchThroughPositions();
            for (int i = 0; i < positions.length; i += 2) {
                double diff = Math
                        .sqrt(Math.pow(event.getX() - positions[i], 2)
                                + Math.pow(event.getY() - positions[i + 1], 2));
                if (diff < mRevealRadius) {
                    return false;
                }
            }
        } else if (mDrawIdentifier.equals(SLIDINGMENU_LEFT)) {
        	
        } else if (mDrawIdentifier.equals(SLIDINGMENU_RIGHT)) {
        	
        }
        return true;
    };

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mIsInitialized) {
            DisplayMetrics metrics = new DisplayMetrics();
            mLauncher.getWindowManager().getDefaultDisplay()
                    .getMetrics(metrics);

            // Initialize the draw buffer (to allow punching through)
            Bitmap b = Bitmap.createBitmap(getMeasuredWidth(),
                    getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            // Draw the background
            if (mBackground == null) {
                if (mDrawIdentifier.equals(MAIN_ACTIVITY)) {
                    mBackground = getResources().getDrawable(
                            R.drawable.bg_cling);
                } else if (mDrawIdentifier.equals(SLIDINGMENU_LEFT)) {
                	
                } else if (mDrawIdentifier.equals(SLIDINGMENU_RIGHT)) {
                	
                }
            }
            if (mBackground != null) {
                mBackground.setBounds(0, 0, getMeasuredWidth(),
                        getMeasuredHeight());
                mBackground.draw(c);
            } else {
                c.drawColor(0x99000000);
            }

            int cx = -1;
            int cy = -1;
            float scale = mRevealRadius / mPunchThroughGraphicCenterRadius;
            int dw = (int) (scale * mPunchThroughGraphic.getIntrinsicWidth());
            int dh = (int) (scale * mPunchThroughGraphic.getIntrinsicHeight());

            // Determine where to draw the punch through graphic
            int[] positions = getPunchThroughPositions();
            for (int i = 0; i < positions.length; i += 2) {
                cx = positions[i];
                cy = positions[i + 1];
                if (cx > -1 && cy > -1) {
                    c.drawCircle(cx, cy, mRevealRadius, mErasePaint);
                    mPunchThroughGraphic.setBounds(cx - dw / 2, cy - dh / 2, cx
                            + dw / 2, cy + dh / 2);
                    mPunchThroughGraphic.draw(c);
                }
            }

            if (mDrawIdentifier.equals(MAIN_ACTIVITY)) {
                if (mHandTouchGraphic == null) {
                    mHandTouchGraphic = getResources().getDrawable(
                            R.drawable.hand);
                }
                int offset = 0;
                mHandTouchGraphic.setBounds(cx + offset, cy + offset, cx
                        + mHandTouchGraphic.getIntrinsicWidth() + offset, cy
                        + mHandTouchGraphic.getIntrinsicHeight() + offset);
                mHandTouchGraphic.draw(c);
            } else if (mDrawIdentifier.equals(SLIDINGMENU_LEFT)) {
            	
            } else if (mDrawIdentifier.equals(SLIDINGMENU_RIGHT)) {
            	
            }

            canvas.drawBitmap(b, 0, 0, null);
            c.setBitmap(null);
            b = null;
        }

        // Draw the rest of the cling
        super.dispatchDraw(canvas);
    };
}
