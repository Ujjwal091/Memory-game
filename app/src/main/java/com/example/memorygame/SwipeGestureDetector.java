package com.example.memorygame;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private View mView;
    private OnSwipeListener mListener;

    public SwipeGestureDetector(Context context, OnSwipeListener listener) {
        mListener = listener;
        mView = new View(context);

        // Create a GestureDetector to handle gesture detection
        GestureDetector gestureDetector = new GestureDetector(context, this);

        // Set the OnTouchListener to the view
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Let the GestureDetector handle touch events
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffX = e2.getX() - e1.getX();
            if (-diffX > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                // Trigger the onSwipeRight() method when a right swipe is detected
                mListener.onSwipeRight();
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public View getView() {
        return mView;
    }

    // Listener interface for swipe events
    public interface OnSwipeListener {
        void onSwipeRight();
    }
}
