package com.example.demo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class GestureFrameLayout extends FrameLayout implements GestureDetector.OnGestureListener {
    private
    int verticalMinDistance = 50;

    private
    int minVelocity = 0;

    private static GestureFrameLayoutCallBack layoutCallBack;
    private GestureDetector gestureDetector;
    Context context;

    public void setCallBack(GestureFrameLayoutCallBack myLayoutCallBack) {
        layoutCallBack = myLayoutCallBack;
        Log.e("@@", "setCallBack--->" + layoutCallBack);
    }

    public GestureFrameLayout(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, this);
        this.context = context;
    }

    public GestureFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("@@", "onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("@@", "onScroll" + distanceX + "distanceY:" + distanceY);
//        if (layoutCallBack == null) {
//            return false;
//        }
        if (distanceX < -verticalMinDistance) {
            Log.d("@@", "向右手势" + layoutCallBack);
            layoutCallBack.scrollByX(50);
        } else if (distanceX > verticalMinDistance) {

            Log.d("@@", "向左手势");
            layoutCallBack.scrollByX(-50);
        } else if (distanceY < -verticalMinDistance) {
            Log.d("@@", "向下手势");
//            myLayoutCallBack.scrollByY(50);

        } else if (distanceY > verticalMinDistance) {

            Log.d("@@", "向上手势");
//            myLayoutCallBack.scrollByY(-50);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("@@", "onFling");
        if (layoutCallBack == null) {
            return false;
        }
        if (e1.getX()
                - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
            Log.d("@@", "向左手势");
            layoutCallBack.scrollByX(-50);

        } else if ((e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity)) {

            Log.d("@@", "向右手势");
            layoutCallBack.scrollByX(50);

        } else if (e1.getY()
                - e2.getY() > verticalMinDistance && Math.abs(velocityY) > minVelocity) {
            Log.d("@@", "向上手势");
//            myLayoutCallBack.scrollByY(-50);

        } else if ((e2.getY() - e1.getY() > verticalMinDistance && Math.abs(velocityY) > minVelocity)) {

            Log.d("@@", "向下手势");
//            myLayoutCallBack.scrollByY(50);

        }
        return false;
    }
}