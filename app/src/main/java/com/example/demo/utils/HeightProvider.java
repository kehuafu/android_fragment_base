package com.example.demo.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;

public class HeightProvider extends PopupWindow implements ViewTreeObserver.OnGlobalLayoutListener {
    private Activity mActivity;
    private View rootView;
    private HeightListener listener;
    private int heightMax; // 记录popup内容区的最大高度
    private int mKeyboardHeight = 0;

    public HeightProvider(Activity activity) {
        super(activity);
        this.mActivity = activity;

        // 基础配置
        rootView = new View(activity);
        setContentView(rootView);

        // 监听全局Layout变化
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        setBackgroundDrawable(new ColorDrawable(0));

        // 设置宽度为0，高度为全屏
        setWidth(0);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);

        // 设置键盘弹出方式
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
    }

    public HeightProvider init() {
        if (!isShowing()) {
            final View view = mActivity.getWindow().getDecorView();
            // 延迟加载popupwindow，如果不加延迟就会报错
            view.post(new Runnable() {
                @Override
                public void run() {
                    showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
                }
            });
        }
        return this;
    }

    public HeightProvider setHeightListener(HeightListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        if (rect.bottom > heightMax) {
            heightMax = rect.bottom;
        }

        // 两者的差值就是键盘的高度
        int keyboardHeight = heightMax - rect.bottom;
        Log.e("@@", "heightMax-->" + heightMax);
        Log.e("@@", "bottom-->" + rect.bottom);
        Log.e("@@", "keyboardHeight-->" + keyboardHeight);
        if (listener != null) {
            if (BarUtils.getNavBarHeight() == keyboardHeight) {
                heightMax = heightMax - BarUtils.getNavBarHeight();
                return;
            } else if (keyboardHeight > rect.bottom + 300) {
                return;
            }
            listener.onHeightChanged(keyboardHeight);
        }
        mKeyboardHeight = keyboardHeight;
    }

    public Boolean isSoftInputVisible() {
        return mKeyboardHeight != 0;
    }

    public interface HeightListener {
        void onHeightChanged(int height);
    }
}
