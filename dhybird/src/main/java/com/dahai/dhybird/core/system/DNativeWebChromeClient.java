package com.dahai.dhybird.core.system;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.dahai.dhybird.core.base.DWebChromeClient;

import java.util.Arrays;
import java.util.List;

public class DNativeWebChromeClient extends WebChromeClient implements DWebChromeClient {

    private View progressVideo;
    private IWebPageView webPageView;
    private View customView;
    private CustomViewCallback customViewCallback;

    public DNativeWebChromeClient(IWebPageView mIWebPageView) {
        this.webPageView = mIWebPageView;
    }

    /**
     * 网页加载进度
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (webPageView == null) {
            return;
        }
        webPageView.startProgress(newProgress);
    }

    @Override
    public boolean onShowFileChooser(WebView webView,ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        Log.e("magic","onShowFileChooser==============");
        List<String> acceptTypes = Arrays.asList(fileChooserParams.getAcceptTypes());
        if (acceptTypes.contains("image/*")) {
            if(webPageView!=null){
                webPageView.selectFile(valueCallback);
            }
        }
        return true;

    }

    /**
     * 播放网络视频时全屏会被调用的方法
     */
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (webPageView == null) {
            return;
        }
        webPageView.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        webPageView.hindWebView();
        // 如果一个视图已经存在，那么立刻终止并新建一个
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        webPageView.fullViewAddView(view);
        customView = view;
        customViewCallback = callback;
        webPageView.showVideoFullView();
    }

    /**
     * 视频播放退出全屏会被调用的
     */
    @Override
    public void onHideCustomView() {
        if (webPageView == null) {
            return;
        }
        // 不是全屏播放状态
        if (customView == null) {
            return;
        }
        webPageView.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        customView.setVisibility(View.GONE);
        if (webPageView.getVideoFullView() != null) {
            webPageView.getVideoFullView().removeView(customView);
        }
        customView = null;
        webPageView.hindVideoFullView();
        customViewCallback.onCustomViewHidden();
        webPageView.showWebView();
    }

    /**
     * 视频加载时loading
     */
    @Override
    public View getVideoLoadingProgressView() {
        if (progressVideo == null) {
            progressVideo = webPageView.getVideoLoadingProgressView();
        }
        return progressVideo;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        webPageView.webViewTitle(title);
    }


    /**
     * 判断是否是全屏
     */
    public boolean inCustomView() {
        return (customView != null);
    }

}
