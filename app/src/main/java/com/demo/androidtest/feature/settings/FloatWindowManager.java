package com.demo.androidtest.feature.settings;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.demo.androidtest.R;

/**
 * 悬浮窗管理类：展示最新Settings修改记录
 */
public class FloatWindowManager {
    private static FloatWindowManager sInstance;
    private WindowManager mWindowManager;
    private View mFloatView;
    private TextView mContentTv;
    private WindowManager.LayoutParams mParams;
    // 悬浮窗触摸偏移
    private int mLastX, mLastY;
    private int mStartX, mStartY;

    private FloatWindowManager() {}

    public static FloatWindowManager getInstance() {
        if (sInstance == null) {
            synchronized (FloatWindowManager.class) {
                if (sInstance == null) {
                    sInstance = new FloatWindowManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 显示悬浮窗
     */
    public void showFloatWindow(Context context) {
        if (mWindowManager != null) return;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mFloatView = LayoutInflater.from(context).inflate(R.layout.float_window_layout, null);
        mContentTv = mFloatView.findViewById(R.id.tv_float_content);

        // 悬浮窗参数
        mParams = new WindowManager.LayoutParams();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mParams.format = PixelFormat.RGBA_8888;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = 100;
        mParams.y = 200;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        // 触摸拖动
        mFloatView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = (int) event.getRawX();
                    mLastY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mParams.x = (int) event.getRawX() - mStartX;
                    mParams.y = (int) event.getRawY() - mStartY;
                    mWindowManager.updateViewLayout(mFloatView, mParams);
                    break;
            }
            return true;
        });

        mWindowManager.addView(mFloatView, mParams);
        mContentTv.setText("监听中...");
    }

    /**
     * 更新悬浮窗内容（最新修改记录）
     */
    public void updateFloatWindowContent(SettingsMonitorService.SettingsChangeRecord record) {
        if (mContentTv != null) {
            mContentTv.setText(record.toString());
        }
    }

    /**
     * 移除悬浮窗
     */
    public void removeFloatWindow() {
        if (mWindowManager != null && mFloatView != null) {
            mWindowManager.removeView(mFloatView);
            mWindowManager = null;
            mFloatView = null;
        }
    }

    /**
     * 判断悬浮窗是否显示
     */
    public boolean isFloatWindowShowing() {
        return mFloatView != null;
    }
}