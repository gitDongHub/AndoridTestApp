package com.demo.androidtest.base;

import android.content.Context;
import com.demo.androidtest.model.TestItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestConfigManager {
    // 从assets加载测试项配置
    public static List<TestItem> loadTestItems(Context context) {
        List<TestItem> testItems = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("test_items.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            is.close();

            // 解析JSON
            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                testItems.add(new TestItem(
                        obj.getString("title"),
                        obj.getString("activityClass")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            testItems.add(new TestItem("配置加载失败", "com.demo.androidtest.MainActivity"));
        }
        return testItems;
    }
}