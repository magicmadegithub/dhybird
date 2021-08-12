package com.dahai.dhybird.bridge;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import java.util.LinkedHashMap;

public class DHPluginManager {

    // 上下文对象
    private Activity activity;

    // native bridge
    private DHBridge dhBridge;

    // 插件集合
    private LinkedHashMap<String, DHPluginEntry> pluginEntryMap = new LinkedHashMap<>();

    public DHPluginManager(Activity activity) {
        this.activity = activity;
        init();
    }

    /**
     * 初始化，从json文件读取插件列表并存入集合
     */
    public void init() {
        LinkedHashMap<String, DHPluginEntry> parseMap = DHJSUtils.parseJson(activity);
        if (parseMap.size() > 0) {
            pluginEntryMap.clear();
            pluginEntryMap.putAll(parseMap);
        }
    }

    /**
     * 执行js message逻辑
     *
     * @param dhjsRequest js message包装类
     * @param dhBridge    native bridge
     */
    public String exec(DHJSRequest dhjsRequest, DHBridge dhBridge) {
        this.dhBridge = dhBridge;
        DHCallbackContext dhCallbackContext = new DHCallbackContext(dhjsRequest.getCallbackId(), this, activity);
        String plugin = dhjsRequest.getPlugin();
        if (pluginEntryMap.containsKey(plugin)) {
            DHPluginEntry pluginEntry = pluginEntryMap.get(plugin);
            DHPlugin dhPlugin = instantiatePlugin(pluginEntry.getPath());
            if (dhPlugin != null) {
                return dhPlugin.exec(dhjsRequest, dhCallbackContext);
            } else {
                DHJSResponse dhjsResponse = DHJSUtils.createJsResponse(dhjsRequest.getCallbackId(), false, true
                        , "not find plugin entry!", null);
                handleJsResponse(dhjsResponse);
                return dhjsResponse.toString();
            }
        } else {
            DHJSResponse dhjsResponse = DHJSUtils.createJsResponse(dhjsRequest.getCallbackId(), false, true
                    , "not find plugin!", null);
            handleJsResponse(dhjsResponse);
            return dhjsResponse.toString();
        }
    }

    /**
     * 处理native的返回消息给native bridge
     *
     * @param dhjsResponse native回传给js的消息包装类
     */
    public void handleJsResponse(DHJSResponse dhjsResponse) {
        dhBridge.handleJsResponse(dhjsResponse);
    }

    /**
     * 通过类名查找插件实现类
     */
    private DHPlugin instantiatePlugin(String className) {
        DHPlugin ret = null;
        try {
            Class<?> c = null;
            if ((className != null) && !("".equals(className))) {
                c = Class.forName(className);
            }
            if (c != null & DHPlugin.class.isAssignableFrom(c)) {
                ret = (DHPlugin) c.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error adding plugin " + className + ".");
        }
        return ret;
    }

    /**
     * 增加插件
     *
     * @param dhPluginEntry 插件实体类
     */

    public void addPlugin(DHPluginEntry dhPluginEntry) {
        pluginEntryMap.put(dhPluginEntry.getFunction(), dhPluginEntry);
    }

    /**
     * 移除插件
     *
     * @param module 业务模块
     * @param action 动作
     */
    public void removePlugin(String module, String action) {
        if (TextUtils.isEmpty(module) && TextUtils.isEmpty(action)) {
            String plugin = module + action;
            pluginEntryMap.remove(plugin);
        }
    }

    /**
     * 替换插件
     *
     * @param dhPluginEntry 插件实体类
     */
    public void replacePulgin(DHPluginEntry dhPluginEntry) {
        pluginEntryMap.put(dhPluginEntry.getFunction(), dhPluginEntry);
    }

    /**
     * 查找插件是否存在
     */
    public boolean checkPlugin(String pluginName) {
        if (!TextUtils.isEmpty(pluginName)) {
            return pluginEntryMap.get(pluginName) != null;
        }
        return false;
    }

}
