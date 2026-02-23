package com.demo.androidtest.base;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class BaseTestActivity extends AppCompatActivity {

    // 定义参数key（统一管理，避免拼写错误）
    public static final String EXTRA_TEST_ITEM_TITLE = "TEST_ITEM_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 获取ActionBar并配置返回箭头
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // 主界面隐藏返回箭头
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // 2. 读取Intent传递的title，设置为Activity标题
        String testItemTitle = getIntent().getStringExtra(EXTRA_TEST_ITEM_TITLE);
        if (testItemTitle != null && !testItemTitle.isEmpty()) {
            setTitle(testItemTitle);
        } else {
            // 兜底：无标题时显示默认文字
            setTitle("测试项");
        }
    }

    // 处理返回箭头点击事件（返回主界面）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 可选：重写setTitle，确保ActionBar标题同步
    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
        super.setTitle(title);
    }
}