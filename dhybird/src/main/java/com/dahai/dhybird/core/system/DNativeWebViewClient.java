package com.dahai.dhybird.core.system;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dahai.dhybird.DHybird;
import com.dahai.dhybird.bridge.DHJSUtils;
import com.dahai.dhybird.core.base.DWebViewClient;
import com.dahai.dhybird.utils.NetUtils;

import java.io.File;

public class DNativeWebViewClient extends WebViewClient implements DWebViewClient {

    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;

    private IWebPageView webPageView;

    public DNativeWebViewClient(IWebPageView mIWebPageView) {
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
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.e(DHybird.TAG, "onPageStarted: " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.e(DHybird.TAG, "onPageFinished: " + url);
        String dhjs = DHJSUtils.getDHJS(view.getContext());
        view.evaluateJavascript(dhjs, null);
        webPageView.webViewTitle(view.getTitle());
        Log.e(DHybird.TAG, "--dhbridge.js在onPageFinished注入成功--");
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.cancel();
//        webPageView.onLoadError();
    }

    private void handlePhone(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
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

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if(request.isForMainFrame()){
            webPageView.onLoadError();
        }
    }
}
