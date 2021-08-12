package com.dahai.dhybird.bridge;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

public class DHCallbackContext {

    private String callbackId;

    private DHPluginManager pluginManager;

    private Activity activity;


    public DHCallbackContext(String callbackId, DHPluginManager pluginManager, Activity activity) {
        this.callbackId = callbackId;
        this.pluginManager = pluginManager;
        this.activity = activity;
    }

    /**
     * 获取上下文对象
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * 获取pluginManager
     */
    public DHPluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * 执行成功
     *
     * @param data 业务数据
     */
    public String success(JSONObject data) {
        return handlePluginResult(true, true, "", data);
    }

    /**
     * 执行成功
     *
     * @param data       业务数据
     * @param isComplete 是否连续回调完成
     */
    public String success(JSONObject data, boolean isComplete) {
        return handlePluginResult(true, isComplete, "", data);
    }

    /**
     * 执行失败
     *
     * @param errorMessage 错误消息
     */
    public String fail(String errorMessage) {
        return handlePluginResult(false, true, errorMessage, null);
    }

    /**
     * 执行失败
     *
     * @param errorMessage 错误消息
     * @param isComplete   是否连续回调完成
     */
    public String fail(String errorMessage, boolean isComplete) {
        return handlePluginResult(false, isComplete, errorMessage, null);
    }

    /**
     * 处理插件执行结果
     *
     * @param isSuccess    是否成功
     * @param isComplete   是否连续回调完成
     * @param errorMessage 错误消息
     * @param data         业务数据
     */
    private String handlePluginResult(boolean isSuccess, boolean isComplete, String errorMessage, JSONObject data) {
        DHJSResponse jsResponse = DHJSUtils.createJsResponse(callbackId, isSuccess, isComplete, errorMessage, data);
        pluginManager.handleJsResponse(jsResponse);
        return jsResponse.toString();
    }
}
