package com.demo.androidtest.feature.systeminfo;

import android.os.Build;
import android.os.Bundle;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.demo.androidtest.base.BaseTestActivity;
import com.demo.androidtest.R;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Locale;

public class SystemInfoActivity extends BaseTestActivity {

    // 改用LinkedHashMap保证插入顺序
    private final Map<String, String> mSystemInfoMap = new LinkedHashMap<>();

    // ========== 核心：封装分类映射关系（统一管理分类） ==========
    private static final Map<String, String> CATEGORY_MAPPING = new HashMap<>();
    static {
        // 键：分类的首个key | 值：分类标题
        CATEGORY_MAPPING.put("Android Version", "Android System Info");
        CATEGORY_MAPPING.put("Device Brand", "Device Hardware Info");
        CATEGORY_MAPPING.put("Screen Resolution (Pixel)", "Screen Info");
        CATEGORY_MAPPING.put("App Version Info", "App Version Info");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collectSystemInfo(); // 采集信息
        initUI();            // 初始化UI（动态分类）
    }

    /**
     * 采集核心系统/设备信息（固定顺序，分类插入）
     */
    private void collectSystemInfo() {
        // ========== 第一类：Android系统核心信息 ==========
        mSystemInfoMap.put("Android Version", Build.VERSION.RELEASE);
        mSystemInfoMap.put("API Level", String.valueOf(Build.VERSION.SDK_INT));
        mSystemInfoMap.put("Codename", Build.VERSION.CODENAME);
        mSystemInfoMap.put("Build ID", Build.ID);
        mSystemInfoMap.put("Build Type", Build.TYPE);
        mSystemInfoMap.put("Build Number", Build.DISPLAY);
        // mSystemInfoMap.put("Build Fingerprint", Build.FINGERPRINT);
        mSystemInfoMap.put("System Build Time", getSystemBuildTime());

        // ========== 第二类：设备硬件信息 ==========
        mSystemInfoMap.put("Device Brand", Build.BRAND);
        mSystemInfoMap.put("Device Manufacturer", Build.MANUFACTURER);
        mSystemInfoMap.put("Device Model", Build.MODEL);
        mSystemInfoMap.put("Product Name", Build.PRODUCT);

        // ========== 第三类：屏幕信息（同安兔兔） ==========
        WindowManager windowManager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);

        int screenWidthPx = metrics.widthPixels;
        int screenHeightPx = metrics.heightPixels;
        String screenResolution = screenWidthPx + " x " + screenHeightPx;
        mSystemInfoMap.put("Screen Resolution (Pixel)", screenResolution);
        mSystemInfoMap.put("Screen DPI", String.valueOf(metrics.densityDpi));
        mSystemInfoMap.put("Screen Density", String.format("%.2f", metrics.density));

        double screenDiagonalPx = Math.sqrt(Math.pow(screenWidthPx, 2) + Math.pow(screenHeightPx, 2));
        double screenSizeInch = screenDiagonalPx / metrics.densityDpi;
        mSystemInfoMap.put("Screen Size (Inch)", String.format("%.1f\"", screenSizeInch));

        // ========== 第四类：App版本信息 ==========
        try {
            PackageInfo packageInfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            String appVersion = packageInfo.versionName + " (Code: " + packageInfo.versionCode + ")";
            mSystemInfoMap.put("App Version Info", appVersion);

            // APK构建时间（格式化）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String apkBuildTime = sdf.format(new Date(Build.TIME));
            mSystemInfoMap.put("APK Build Time", apkBuildTime);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mSystemInfoMap.put("App Version Info", "Unknown");
            mSystemInfoMap.put("APK Build Time", "Unknown");
        }
    }

    /**
     * 获取Android系统编译时间（通过反射读取系统属性ro.build.date）
     */
    private String getSystemBuildTime() {
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getMethod = systemPropertiesClass.getMethod("get", String.class);
            String systemBuildTime = (String) getMethod.invoke(null, "ro.build.date");
            return systemBuildTime != null ? systemBuildTime : "Unknown";
        } catch (Exception e) {
            e.printStackTrace();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(Build.TIME)); // 降级方案
        }
    }

    /**
     * 初始化UI：通过分类映射表动态插入标题（无if-else，无硬编码）
     */
    private void initUI() {
        // 外层ScrollView：解决内容超出屏幕
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.WHITE);

        // 内层LinearLayout：内容容器
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(32, 32, 32, 32);

        // 遍历信息（通过分类映射表动态判断标题，极简逻辑）
        for (Map.Entry<String, String> entry : mSystemInfoMap.entrySet()) {
            String currentKey = entry.getKey();

            // ========== 核心：通过映射表动态插入分类标题 ==========
            String categoryTitle = CATEGORY_MAPPING.get(currentKey);
            if (categoryTitle != null) {
                addSectionTitle(rootLayout, categoryTitle);
            }

            // 标题行：优化换行
            TextView tvKey = new TextView(this);
            tvKey.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvKey.setTextColor(Color.parseColor("#333333"));
            tvKey.setText(currentKey + ":");
            tvKey.setPadding(0, 16, 0, 4);
            tvKey.setSingleLine(false);
            tvKey.setMaxLines(2);
            rootLayout.addView(tvKey);

            // 内容行：优化换行
            TextView tvValue = new TextView(this);
            tvValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvValue.setTextColor(Color.parseColor("#666666"));
            tvValue.setText(entry.getValue());
            tvValue.setPadding(0, 0, 0, 12);
            tvValue.setSingleLine(false);
            tvValue.setMaxLines(3);
            rootLayout.addView(tvValue);
        }

        scrollView.addView(rootLayout);
        setContentView(scrollView);
    }

    /**
     * 加粗的分类标题
     */
    private void addSectionTitle(LinearLayout root, String title) {
        TextView tvSection = new TextView(this);
        tvSection.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvSection.setTextColor(Color.parseColor("#000000"));
        tvSection.setPadding(0, 24, 0, 8);
        tvSection.setText(title);
        tvSection.setTypeface(Typeface.DEFAULT_BOLD); // 加粗
        tvSection.setSingleLine(true);
        root.addView(tvSection);
    }
}