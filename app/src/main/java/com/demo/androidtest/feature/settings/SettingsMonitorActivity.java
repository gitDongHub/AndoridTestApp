package com.demo.androidtest.feature.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.demo.androidtest.base.BaseTestActivity;
import com.demo.androidtest.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.List;

/**
 * Settings监控页面：展示所有修改记录，控制监听服务启停
 */
public class SettingsMonitorActivity extends BaseTestActivity {
    private LinearLayout mRecordsLayout;
    // 用于通知UI刷新的回调
    private static OnRefreshListener sOnRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        // 注册刷新回调
        sOnRefreshListener = this::refreshRecords;
        // 启动监听服务
        startService(new Intent(this, SettingsMonitorService.class));
        // 初始刷新记录
        refreshRecords();
    }

    /**
     * 初始化UI：启停按钮 + 修改记录列表
     */
    private void initUI() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(32, 32, 32, 32);

        // 1. 标题
        TextView titleTv = new TextView(this);
        titleTv.setText("Settings字段修改监控");
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleTv.setTextColor(Color.BLACK);
        titleTv.setPadding(0, 0, 0, 24);
        titleTv.setTypeface(Typeface.DEFAULT_BOLD);
        rootLayout.addView(titleTv);

        // 2. 控制按钮
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnLayout.setPadding(0, 0, 0, 24);

        Button startBtn = new Button(this);
        startBtn.setText("启动监听");
        startBtn.setOnClickListener(v -> startService(new Intent(this, SettingsMonitorService.class)));
        btnLayout.addView(startBtn);

        Button stopBtn = new Button(this);
        stopBtn.setText("停止监听");
//        stopBtn.setMarginStart(16);
        stopBtn.setOnClickListener(v -> stopService(new Intent(this, SettingsMonitorService.class)));
        btnLayout.addView(stopBtn);
        rootLayout.addView(btnLayout);

        // 3. 修改记录标题
        TextView recordsTitle = new TextView(this);
        recordsTitle.setText("修改记录（最新在前）");
        recordsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        recordsTitle.setTextColor(Color.BLACK);
        recordsTitle.setPadding(0, 0, 0, 16);
        recordsTitle.setTypeface(Typeface.DEFAULT_BOLD);
        rootLayout.addView(recordsTitle);

        // 4. 修改记录列表
        mRecordsLayout = new LinearLayout(this);
        mRecordsLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.addView(mRecordsLayout);

        scrollView.addView(rootLayout);
        setContentView(scrollView);
    }

    /**
     * 刷新修改记录列表
     */
    private void refreshRecords() {
        runOnUiThread(() -> {
            mRecordsLayout.removeAllViews();
            List<SettingsMonitorService.SettingsChangeRecord> records = SettingsMonitorService.CHANGE_RECORDS;
            if (records.isEmpty()) {
                TextView emptyTv = new TextView(this);
                emptyTv.setText("暂无修改记录，可修改屏幕亮度/字体大小测试");
                emptyTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                emptyTv.setTextColor(Color.GRAY);
                mRecordsLayout.addView(emptyTv);
                return;
            }

            // 遍历展示所有记录
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            for (SettingsMonitorService.SettingsChangeRecord record : records) {
                LinearLayout recordItem = new LinearLayout(this);
                recordItem.setOrientation(LinearLayout.VERTICAL);
                recordItem.setPadding(0, 16, 0, 16);
                recordItem.setBackgroundColor(Color.parseColor("#F5F5F5"));
//                recordItem.setMarginBottom(8);

                // 时间
                TextView timeTv = new TextView(this);
                timeTv.setText(String.format("时间：%s", sdf.format(record.time)));
                timeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                timeTv.setTextColor(Color.parseColor("#666666"));
                recordItem.addView(timeTv);

                // 字段+值
                TextView fieldTv = new TextView(this);
                fieldTv.setText(String.format("字段：%s = %s", record.chineseName, record.value));
                fieldTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                fieldTv.setTextColor(Color.BLACK);
                recordItem.addView(fieldTv);

                // 修改者
                TextView pkgTv = new TextView(this);
                pkgTv.setText(String.format("修改者：%s", record.modifierPkg));
                pkgTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                pkgTv.setTextColor(Color.parseColor("#FF6600"));
                recordItem.addView(pkgTv);

                mRecordsLayout.addView(recordItem);
            }
        });
    }

    /**
     * 通知Activity刷新（由Service调用）
     */
    public static void notifyRefresh() {
        if (sOnRefreshListener != null) {
            sOnRefreshListener.onRefresh();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sOnRefreshListener = null;
    }

    /**
     * 刷新回调接口
     */
    public interface OnRefreshListener {
        void onRefresh();
    }
}