package com.trioangle.gofer.placesearch;

/**
 * @package com.trioangle.gofer
 * @subpackage placesearch
 * @category Recycler view Item click listener
 * @author Trioangle Product Team
 * @version 1.1
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
/* ************************************************************
Place search list click listener
*************************************************************** */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    public GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        DebuggableLogI("Gofer", "Recyclview");
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        DebuggableLogI("Gofer", "Recyclview");
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}