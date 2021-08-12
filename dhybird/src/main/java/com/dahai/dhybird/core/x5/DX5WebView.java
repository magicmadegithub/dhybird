package com.dahai.dhybird.core.x5;

import android.content.Context;
import android.util.AttributeSet;

import com.dahai.dhybird.core.base.DDownloadListener;
import com.dahai.dhybird.core.base.DWebChromeClient;
import com.dahai.dhybird.core.base.DWebView;
import com.dahai.dhybird.core.base.DWebViewClient;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import java.util.HashMap;

public class DX5WebView extends WebView implements DWebView {

    public DX5WebView(Context context) {
        super(context);
    }

    public DX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        IX5WebViewExtension ix5 = this.getX5WebViewExtension();
    }

    @Override
    public void setDownloadListener(DDownloadListener listener) {
        super.setDownloadListener((DX5DownloadListener) listener);
    }

    @Override
    public void setWebViewClient(DWebViewClient webViewClient) {
        super.setWebViewClient((DX5WebViewClient) webViewClient);
    }

    @Override
    public void setWebChromeClient(DWebChromeClient webChromeClient) {
        super.setWebChromeClient((DX5WebChromeClient) webChromeClient);
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
