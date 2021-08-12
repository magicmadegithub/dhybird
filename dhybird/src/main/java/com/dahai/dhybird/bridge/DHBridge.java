package com.dahai.dhybird.bridge;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.dahai.dhybird.core.base.DWebView;

public class DHBridge {

    private DHPluginManager pluginManager;

    private DWebView webView;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public DHBridge(DHPluginManager pluginManager, DWebView webView) {
        this.pluginManager = pluginManager;
        this.webView = webView;
    }

    /**
     * 执行js消息逻辑
     *
     * @param message js传递的消息 json结构
     */
    public String jsExec(String message) {
        DHJSRequest dhjsRequest = DHJSUtils.createJsRequest(message);
        return pluginManager.exec(dhjsRequest, this);
    }

    /**
     * native给js发消息
     *
     * @param dhjsResponse native给js传递消息的包装类
     */
    public void handleJsResponse(DHJSResponse dhjsResponse) {
        safeEvaluateJavascript(String.format("window.handleMessageFromNative(%s)", dhjsResponse.toString()));
    }

    public void handleJSEventResponse(DHJSResponse dhjsResponse) {
        safeEvaluateJavascript(String.format("window.handleListenEventFromNative(%s)", dhjsResponse.toString()));
    }

    public void safeEvaluateJavascript(final String script) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                evaluateJavascript(script);
            }
        });
    }

    private void evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, null);
        } else {
            webView.loadUrl("javascript:" + script);
        }
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
            return;
        }
        mainHandler.post(runnable);
    }
}
