package com.dahai.dhybird.core.x5;

import android.net.Uri;
import android.util.Log;

import com.dahai.dhybird.core.base.DWebChromeClient;
import com.dahai.dhybird.core.system.IWebPageView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DX5WebChromeClient extends WebChromeClient implements DWebChromeClient {

    private IWebPageView webPageView;

    public DX5WebChromeClient(IWebPageView mIWebPageView) {
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
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
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
     * 时候全屏播放视频，x5内核不用处理，直接返回false
     */
    @Override
    public boolean inCustomView() {
        return false;
    }
}
