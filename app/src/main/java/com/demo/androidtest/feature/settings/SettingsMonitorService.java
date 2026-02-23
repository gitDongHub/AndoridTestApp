package com.demo.androidtest.feature.settings;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.IBinder;
import android.provider.Settings;
import android.content.pm.ActivityInfo;
import android.content.Context;
import android.app.ActivityManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台监听Settings字段修改的Service（可独立运行）
 */
public class SettingsMonitorService extends Service {
    public static final String TAG = "SettingsMonitor";
    // 监听的系统字段（可扩展）
    private static final Map<String, String> MONITOR_FIELDS = new HashMap<>();
    static {
        MONITOR_FIELDS.put(Settings.System.SCREEN_BRIGHTNESS, "屏幕亮度");
        MONITOR_FIELDS.put(Settings.System.FONT_SCALE, "字体缩放");
        MONITOR_FIELDS.put(Settings.System.SCREEN_OFF_TIMEOUT, "屏幕超时");
    }

    // 修改记录（全局共享）
    public static final List<SettingsChangeRecord> CHANGE_RECORDS = new ArrayList<>();
    // 内容观察者
    private SettingsContentObserver mContentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册多字段监听
        mContentObserver = new SettingsContentObserver(new Handler(Looper.getMainLooper()));
        for (String field : MONITOR_FIELDS.keySet()) {
            getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(field),
                    false,
                    mContentObserver
            );
        }
        // 启动悬浮窗
//        FloatWindowManager.getInstance().showFloatWindow(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销观察者
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        // 关闭悬浮窗
        FloatWindowManager.getInstance().removeFloatWindow();
        CHANGE_RECORDS.clear();
    }

    /**
     * 内容观察者：监听所有目标字段修改
     */
    private class SettingsContentObserver extends ContentObserver {
        public SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            // 解析修改的字段名
            String fieldName = uri.getLastPathSegment();
            if (!MONITOR_FIELDS.containsKey(fieldName)) return;

            // 1. 获取字段新值
            String fieldValue = getFieldValue(fieldName);
            // 2. 获取修改者包名（前台应用）
            String modifierPkg = getForegroundPackageName();
            // 3. 生成修改记录
            SettingsChangeRecord record = new SettingsChangeRecord(
                    MONITOR_FIELDS.get(fieldName), // 中文名称
                    fieldName, // 原始字段名
                    fieldValue, // 新值
                    modifierPkg, // 修改者包名
                    System.currentTimeMillis() // 修改时间
            );
            // 4. 添加到全局记录
            CHANGE_RECORDS.add(0, record); // 最新记录放最前面
            Log.d(TAG, "Settings修改：" + record.toString());

            // 5. 更新悬浮窗
            FloatWindowManager.getInstance().updateFloatWindowContent(record);
            // 6. 通知Activity刷新UI
            SettingsMonitorActivity.notifyRefresh();
        }
    }

    /**
     * 获取字段当前值
     */
    private String getFieldValue(String field) {
        try {
            if (field.equals(Settings.System.SCREEN_BRIGHTNESS)) {
                int value = Settings.System.getInt(getContentResolver(), field, -1);
                return value + " (0-255)";
            } else if (field.equals(Settings.System.FONT_SCALE)) {
                float value = Settings.System.getFloat(getContentResolver(), field, 1.0f);
                return String.format("%.2f", value);
            } else if (field.equals(Settings.System.SCREEN_OFF_TIMEOUT)) {
                int value = Settings.System.getInt(getContentResolver(), field, -1);
                return value + " ms";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "获取失败";
    }

    /**
     * 获取前台应用包名（修改者）
     */
    private String getForegroundPackageName() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (ActivityManager.AppTask task : am.getAppTasks()) {
                    ComponentName info = task.getTaskInfo().topActivity;
                    if (info != null) return info.getPackageName();
                }
            }
            // 兼容低版本（需GET_TASKS权限）
            ActivityManager.RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
            return taskInfo.topActivity.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
            return "未知包名";
        }
    }

    /**
     * Settings修改记录实体类
     */
    public static class SettingsChangeRecord {
        public String chineseName; // 字段中文名称
        public String fieldName; // 原始字段名
        public String value; // 新值
        public String modifierPkg; // 修改者包名
        public long time; // 修改时间

        public SettingsChangeRecord(String chineseName, String fieldName, String value, String modifierPkg, long time) {
            this.chineseName = chineseName;
            this.fieldName = fieldName;
            this.value = value;
            this.modifierPkg = modifierPkg;
            this.time = time;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return String.format("[%s] %s = %s (修改者：%s)",
                    sdf.format(new Date(time)),
                    chineseName,
                    value,
                    modifierPkg);
        }
    }
}