package com.dahai.dhybird.core.system;

import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;


public interface IWebPageView {

    /**
     * 进度条变化时调用
     *
     * @param newProgress 进度0-100
     */
    void startProgress(int newProgress);

    /**
     * 显示webview
     */
    void showWebView();

    /**
     * 隐藏webview
     */
    void hindWebView();

    /**
     * 添加视频全屏view
     */
    void fullViewAddView(View view);

    /**
     * 显示全屏view
     */
    void showVideoFullView();

    /**
     * 隐藏全屏view
     */
    void hindVideoFullView();

    /**
     * 设置横竖屏
     */
    void setRequestedOrientation(int screenOrientationPortrait);

    /**
     * 得到全屏view
     */
    FrameLayout getVideoFullView();

    /**
     * 加载视频进度条
     */
    View getVideoLoadingProgressView();

    /**
     * 文档文件下载完成
     */
    void docDownloadFinish(String path);


    void onLoadError();

    void selectFile(ValueCallback<Uri[]> valueCallback);

    void webViewTitle(String title);

}
