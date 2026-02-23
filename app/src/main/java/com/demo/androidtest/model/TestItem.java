package com.demo.androidtest.model;

public class TestItem {
    private String title;       // 测试项标题
    private String activityClass; // 对应Activity类名

    public TestItem(String title, String activityClass) {
        this.title = title;
        this.activityClass = activityClass;
    }

    // Getter & Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getActivityClass() { return activityClass; }
    public void setActivityClass(String activityClass) { this.activityClass = activityClass; }
}