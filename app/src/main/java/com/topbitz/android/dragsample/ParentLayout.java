package com.topbitz.android.dragsample;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by yongsun on 10/10/16.
 */

public class ParentLayout extends RelativeLayout {

    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;

    private int draggingState = ViewDragHelper.STATE_IDLE;
    private int barTop;
    private ViewDragHelper viewDragHelper;
    private View dragBar;

    public ParentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragBar = findViewById(R.id.drag_bar);
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return (isBarTarget(ev) && viewDragHelper.shouldInterceptTouchEvent(ev)) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isBarTarget(event) || isMoving()) {
            viewDragHelper.processTouchEvent(event);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean isBarTarget(MotionEvent ev) {
        int[] barLocation = new int[2];
        dragBar.getLocationOnScreen(barLocation);
        int topLimit = barLocation[1];
        int bottomLimit = barLocation[1] + dragBar.getMeasuredHeight();

        int y = (int) ev.getRawY();
        return (y > topLimit && y < bottomLimit);
    }

    public boolean isMoving() {
        return (draggingState == ViewDragHelper.STATE_DRAGGING ||
                draggingState == ViewDragHelper.STATE_SETTLING);
    }

    public int getDragRangeVertical() {
        return getHeight() - dragBar.getHeight();
    }

    public class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return (child.getId() == R.id.top_layout);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getDragRangeVertical();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getDragRangeVertical();
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == draggingState) {
                return;
            }
            if (isMoving() && state == ViewDragHelper.STATE_IDLE) {
                //the view stopped moving.


            }

            if (state == ViewDragHelper.STATE_DRAGGING) {

            }

            draggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            barTop = dragBar.getTop();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            final float rangeToCheck = getDragRangeVertical();

            boolean settleToOpen = false;

            if(yvel > AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (barTop > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (barTop <= rangeToCheck / 2) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? getDragRangeVertical() : 0;

            if(viewDragHelper.settleCapturedViewAt(0, settleDestY)){
                ViewCompat.postInvalidateOnAnimation(ParentLayout.this);
            }
        }


    }
}
