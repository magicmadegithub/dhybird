package com.dahai.dhybird.core.x5;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dahai.dhybird.DHybird;
import com.dahai.dhybird.bridge.DHJSUtils;
import com.dahai.dhybird.core.base.DWebViewClient;
import com.dahai.dhybird.core.system.IWebPageView;
import com.dahai.dhybird.utils.NetUtils;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;

public class DX5WebViewClient extends WebViewClient implements DWebViewClient {

    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;

    private IWebPageView webPageView;

    public DX5WebViewClient(IWebPageView mIWebPageView) {
        this.webPageView = mIWebPageView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {
        Log.e(DHybird.TAG, "native shouldOverrideUrlLoading: " + url);
        if (url.contains("tel:")) {
            handlePhone(view.getContext(), url);
            return true;
        } else if (url.endsWith(".pdf")) {
            return handlePDF(view, url);
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
        super.onPageStarted(webView, s, bitmap);
        Log.e(DHybird.TAG, "onPageStarted --");
    }

    @Override
    public void onPageFinished(final WebView webView, String url) {
        super.onPageFinished(webView, url);
        Log.e(DHybird.TAG, "onPageFinished --");
        String dhjs = DHJSUtils.getDHJS(webView.getContext());
        webView.evaluateJavascript(dhjs, null);
        webPageView.webViewTitle(webView.getTitle());
        Log.e(DHybird.TAG, "--dhbridge.js在onPageFinished注入成功--");
    }

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        Log.e(DHybird.TAG, "onReceivedSslError: ");
        sslErrorHandler.cancel();
    }

    @Override
    public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
        Log.e(DHybird.TAG, "onReceivedHttpError: ");
    }


    @Override
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        super.onReceivedError(webView, webResourceRequest, webResourceError);
        if(webResourceRequest.isForMainFrame()){
            webPageView.onLoadError();
        }
    }

    private void handlePhone(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    private boolean handlePDF(WebView view, String url) {
        if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
            return false;
        }
        lastClickTime = System.currentTimeMillis();
        final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("加载中");
        final String path = view.getContext().getFilesDir().getAbsolutePath() + File.separator + "temp.pdf";
        NetUtils.getInstance().download(url, path, new NetUtils.DownloadListener() {
            @Override
            public void onStart() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                });
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (webPageView != null) {
                            webPageView.docDownloadFinish(path);
                        }
                    }
                });
            }
        });
        return true;
    }
}
