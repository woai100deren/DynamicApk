package com.dj.pluginlib.model;

import android.app.Activity;

import com.dj.pluginlib.ProxyActivity;

/**
 * Description：已经打开的插件的对象
 */
public class PluginActivityItem {

    private Activity activity; //打开的插件

    private int launchModel; //插件Activity的启动模式

    private String activityReallyName;  //插件Activity实际启动名称（包名+类型）

    public String getActivityReallyName() {
        return activityReallyName;
    }

    public void setActivityReallyName(String activityReallyName) {
        this.activityReallyName = activityReallyName;
    }

    public PluginActivityItem(Activity activity, int launchModel, String activityReallyName) {
        this.activity = activity;
        this.launchModel = launchModel;
        this.activityReallyName = activityReallyName;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(ProxyActivity activity) {
        this.activity = activity;
    }

    public int getLaunchModel() {
        return launchModel;
    }

    public void setLaunchModel(int launchModel) {
        this.launchModel = launchModel;
    }
}
