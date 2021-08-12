package com.dahai.dhybird.core.base;

import com.tencent.smtt.sdk.ValueCallback;

import java.util.HashMap;

public interface DWebView<T> {

    void setDownloadListener(DDownloadListener listener);

    void setWebViewClient(DWebViewClient webViewClient);

    void setWebChromeClient(DWebChromeClient webChromeClient);

    T getSettings();

    void loadUrl(String url);

    boolean canGoBack();

    void goBack();

    void dump();

    void addJavascriptInterface(Object var1, String var2);

    void evaluateJavascript(String script, ValueCallback valueCallback);

    void loadUrlWithHeader(String url, HashMap<String, String> header);

    void setVisibility(int visibility);

    void reload();
}
