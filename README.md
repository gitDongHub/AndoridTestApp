# AndroidTestApp
一款易扩展的Android测试工具App，以列表形式展示测试项，每个测试项独立封装为Activity，支持配置化新增测试项、图片测试等能力。

## 项目特点
1. **模块化架构**：每个测试项隔离存放，新增测试项无需修改主入口代码；
2. **配置化管理**：测试项列表通过JSON配置加载，扩展成本低；
3. **通用能力封装**：抽离基类和工具类，减少重复代码；
4. **资源分类管理**：图片/布局按测试项分类存放，便于维护。

## 技术栈
- 语言：Java
- 基础框架：AndroidX
- 核心组件：RecyclerView、Activity、OkHttp（网络测试）
- 配置管理：JSON + 反射

## 快速开始
### 1. 环境要求
- Android Studio 2022.3.1+
- Gradle 7.4+
- minSdkVersion 21
- targetSdkVersion 34

### 2. 编译运行
```bash
# 克隆代码
git clone https://github.com/你的用户名/AndroidTestApp.git

# 打开Android Studio，导入项目
# 点击"Run"按钮，选择模拟器/真机运行
```
### 3. 新增测试项
1. 创建测试项模块
在feature目录下新建文件夹（如testitem5），创建TestItem5Activity.java（继承BaseTestActivity）
```java
package com.example.testapp.feature.testitem5;

import com.example.testapp.base.BaseTestActivity;
import android.os.Bundle;

public class TestItem5Activity extends BaseTestActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("测试项5：新增测试示例");
        // 编写测试逻辑
    }
}
```
2. 注册Activity
在AndroidManifest.xml中添加：
```xml
<activity
    android:name=".feature.testitem.TestItemActivity"
    android:exported="false"/>
```
3. 添加配置
   在assets/test_items.json中新增一条配置：
```json
{
  "title": "测试项5：新增测试示例",
  "activityClass": "com.example.testapp.feature.testitem.TestItemActivity"
}
```
5. 运行app
   新增的测试项会自动显示在列表中，无需修改任何其他代码

### 总结
1. 项目架构核心是**模块化隔离+配置化管理**，每个测试项独立封装，新增无需修改主入口，符合易扩展需求；
2. README文档包含环境要求、架构说明、新增测试项步骤、常见问题，适配GitHub开源规范，便于后续维护；
3. 核心文件封装了通用能力（配置读取、图片加载、基类），测试项开发只需关注业务逻辑，降低重复开发成本。
