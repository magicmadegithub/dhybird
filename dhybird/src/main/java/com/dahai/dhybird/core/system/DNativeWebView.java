package com.dahai.dhybird.core.system;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.dahai.dhybird.core.base.DDownloadListener;
import com.dahai.dhybird.core.base.DWebChromeClient;
import com.dahai.dhybird.core.base.DWebView;
import com.dahai.dhybird.core.base.DWebViewClient;
import com.tencent.smtt.sdk.ValueCallback;

import java.util.HashMap;

public class DNativeWebView extends WebView implements DWebView {

    public DNativeWebView(Context context) {
        super(context);
    }

    public DNativeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setDownloadListener(DDownloadListener listener) {
        super.setDownloadListener((DNativieDownloadListener) listener);
    }

    @Override
    public void setWebViewClient(DWebViewClient webViewClient) {
        super.setWebViewClient((DNativeWebViewClient) webViewClient);
    }

    @Override
    public void setWebChromeClient(DWebChromeClient webChromeClient) {
        super.setWebChromeClient((DNativeWebChromeClient) webChromeClient);
    }


    @Override
    public void dump() {
        clearHistory();
        clearCache(true);
        loadUrl("about:blank");
        freeMemory();
        loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        stopLoading();
        destroy();
    }

    @Override
    public void evaluateJavascript(String script, ValueCallback valueCallback) {
        super.evaluateJavascript(script, valueCallback);
    }

    @Override
    public void loadUrlWithHeader(String url, HashMap header) {
        super.loadUrl(url, header);
    }

}
