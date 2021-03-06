package com.dj.pluginlib.consts;

public interface PluginConst {
    String DEX_PATH = "dex_path";
    String LAUNCH_MODEL = "launch_model";
    String REALLY_ACTIVITY_NAME = "reallyActivityName";
    String isPlugin = "isPlugin";

    /**
     * 四种启动模式
     */
    interface LaunchModel {
        int STANDARD = 0;
        int SINGLE_TOP = 1;
        int SINGLE_TASK = 2;
        int SINGLE_INSTANCE = 3;
    }
}
