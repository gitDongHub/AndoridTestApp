package com.demo.androidtest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.demo.androidtest.base.TestConfigManager;
import com.demo.androidtest.model.TestItem;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 配置主界面标题和返回箭头
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("AndroidTest"); // 主界面标题
        }

        RecyclerView rvTestList = findViewById(R.id.rv_test_list);
        rvTestList.setLayoutManager(new LinearLayoutManager(this));

        // 加载配置文件中的测试项
        List<TestItem> testItems = TestConfigManager.loadTestItems(this);
        rvTestList.setAdapter(new TestItemAdapter(testItems));
    }

    // 测试项列表适配器
    class TestItemAdapter extends RecyclerView.Adapter<TestItemAdapter.ViewHolder> {
        private List<TestItem> mTestItems;

        public TestItemAdapter(List<TestItem> testItems) {
            mTestItems = testItems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        // MainActivity.java 中 TestItemAdapter 的 onBindViewHolder 方法
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TestItem item = mTestItems.get(position);
            holder.tvTitle.setText(item.getTitle());
            // 跳转对应测试项Activity，传递title参数
            holder.itemView.setOnClickListener(v -> {
                try {
                    Class<?> clazz = Class.forName(item.getActivityClass());
                    Intent intent = new Intent(MainActivity.this, clazz);
                    // 核心：传递JSON中的title字段
                    intent.putExtra("TEST_ITEM_TITLE", item.getTitle());
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTestItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}