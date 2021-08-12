package com.dahai.dhybird.bridge;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class DHJSUtils {

    /**
     * 插件json文件所在位置
     */
    public static String PLUGIN_JSON_URL = "dhybird/dhplugin.json";

    /**
     * dhbridge.js文件所在位置
     */
    public static String DHBIRDGE_JS_URL = "dhybird/dhbridge.js";

    /**
     * dhbridge.js文件转String
     *
     * @param context 上下文对象
     * @return
     */
    public static String getDHJS(Context context) {

        String jsStr = "";
        try {
            InputStream in = context.getAssets().open(DHBIRDGE_JS_URL);
            byte buff[] = new byte[1024];
            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
            do {
                int numRead = in.read(buff);
                if (numRead <= 0) {
                    break;
                }
                fromFile.write(buff, 0, numRead);
            } while (true);
            jsStr = fromFile.toString();
            in.close();
            fromFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsStr;
    }

    /**
     * 创建js给native消息的包装类
     *
     * @param message js消息json结构
     * @return
     */
    public static DHJSRequest createJsRequest(String message) {
        DHJSRequest dhjsRequest = new DHJSRequest();
        try {
            JSONObject jsonObject = new JSONObject(message);
            dhjsRequest.setCallbackId(jsonObject.optString("callbackId"));
            dhjsRequest.setPlugin(jsonObject.optString("plugin"));
            dhjsRequest.setSdkVersion(jsonObject.optString("sdkVersion"));
            dhjsRequest.setData(jsonObject.optJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dhjsRequest;
    }

    /**
     * 创建native给js消息的包装类
     *
     * @param callbackId   回调id
     * @param isSuccess    是否调用成功
     * @param isComplete   是否连续回调完成
     * @param errorMessage 错误消息
     * @param data         业务数据
     * @return
     */
    public static DHJSResponse createJsResponse(String callbackId, boolean isSuccess, boolean isComplete, String errorMessage, JSONObject data) {
        DHJSResponse dhjsResponse = new DHJSResponse();
        dhjsResponse.setCallbackId(callbackId);
        dhjsResponse.setStatus(isSuccess ? DHJSConst.STATUS_SUCCESS : DHJSConst.STATUS_FAIL);
        dhjsResponse.setComplete(isComplete ? DHJSConst.CALLBACK_COMPLETE : DHJSConst.CALLBACK_INCOMPLETE);
        dhjsResponse.setErrorMessage(errorMessage);
        dhjsResponse.setData(data);
        return dhjsResponse;
    }

    /**
     * 插件文件转换成LinkedHashMap
     *
     * @param context 上下文对象
     * @return
     */
    public static LinkedHashMap<String, DHPluginEntry> parseJson(Context context) {
        LinkedHashMap<String, DHPluginEntry> pluginMap = new LinkedHashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(PLUGIN_JSON_URL);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonStr = stringBuilder.toString();
        if (TextUtils.isEmpty(jsonStr)) {
            // TODO: 2019/2/27 后续做统计插件初始化失败率
            return pluginMap;
        }
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonStr);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject valueObject = jsonObject.optJSONObject(key);
                String module = valueObject.optString("module");
                String action = valueObject.optString("action");
                String path = valueObject.optString("path");
                String version = valueObject.optString("version");
                String describe = valueObject.optString("describe");
                DHPluginEntry pluginEntry = new DHPluginEntry();
                pluginEntry.setFunction(key);
                pluginEntry.setModule(module);
                pluginEntry.setAction(action);
                pluginEntry.setPath(path);
                pluginEntry.setVersion(version);
                pluginEntry.setDescribe(describe);
                pluginMap.put(key, pluginEntry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pluginMap;
    }
}
