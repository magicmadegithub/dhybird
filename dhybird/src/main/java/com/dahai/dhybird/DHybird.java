package com.dahai.dhybird;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.dahai.dhybird.bridge.DHBridge;
import com.dahai.dhybird.bridge.DHJSInterface;
import com.dahai.dhybird.bridge.DHJSResponse;
import com.dahai.dhybird.bridge.DHJSUtils;
import com.dahai.dhybird.bridge.DHPluginManager;
import com.dahai.dhybird.core.base.DDownloadListener;
import com.dahai.dhybird.core.base.DWebChromeClient;
import com.dahai.dhybird.core.base.DWebView;
import com.dahai.dhybird.core.base.DWebViewClient;
import com.dahai.dhybird.core.system.DNativeWebChromeClient;
import com.dahai.dhybird.core.system.DNativeWebView;
import com.dahai.dhybird.core.system.DNativeWebViewClient;
import com.dahai.dhybird.core.system.DNativieDownloadListener;
import com.dahai.dhybird.core.system.FullscreenHolder;
import com.dahai.dhybird.core.system.IWebPageView;
import com.dahai.dhybird.core.x5.DX5DownloadListener;
import com.dahai.dhybird.core.x5.DX5WebChromeClient;
import com.dahai.dhybird.core.x5.DX5WebView;
import com.dahai.dhybird.core.x5.DX5WebViewClient;
import com.dahai.dhybird.utils.FileDisplayActivity;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class DHybird implements IWebPageView {

    //tag
    public static final String TAG = "DHybird";
    //x5内核
    public static final String CORE_X5 = "x5";
    //原生内核
    public static final String CORE_NATIVE = "native";
    // 上下文对象 x5内核要传Activity
    private Activity activity;
    // webView内核类型
    private String coreType;
    // 地址url
    private String url;
    // 承载webView的容器
    private ViewGroup containerView;
    private DWebView webView;
    private DWebChromeClient webChromeClient;
    private DWebViewClient webViewClient;
    private DDownloadListener downloadListener;
    private WebSettings nativeWebSettings;
    private com.tencent.smtt.sdk.WebSettings x5WebSettings;
    // 是否debug模式
    private boolean isDebug;
    // jsInterface的命名空间
    private static final String BRIDGE_NAME = "android";
    // jsInterface的实现类
    private DHJSInterface dhjsInterface;
    // 插件管理工具类
    private DHPluginManager pluginManager;
    // native bridge
    private DHBridge dhBridge;
    private String agent;
    // dhybird接口
    private IDHybird IDHybird;
    // js注入判断
    private boolean isInjectedJS;
    // dsbridge
    private String dhjs = "";
    // 全屏时视频加载view
    private FrameLayout videoFullView;
    //cookie
    private String cookieKey;
    private String cookieValue;
    private Map<String, String> cookiesMap;

    private DHybird(Builder builder) {
        this.activity = builder.activity;
        this.coreType = builder.coreType;
        this.url = builder.url;
        this.containerView = builder.containerView;
        this.isDebug = builder.isDebug;
        this.agent = builder.agent;
        this.IDHybird = builder.IDHybird;
        this.cookieKey = builder.cookieKey;
        this.cookieValue = builder.cookieValue;
        this.cookiesMap = builder.cookiesMap;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        pluginManager = new DHPluginManager(activity);
        initWebView();
        setWebView();
    }

    private void initWebView() {
        dhjs = DHJSUtils.getDHJS(activity);
        if (CORE_X5.equals(coreType)) {
            com.tencent.smtt.sdk.WebView.setWebContentsDebuggingEnabled(true);
            webView = new DX5WebView(activity);
            webChromeClient = new DX5WebChromeClient(this);
            webViewClient = new DX5WebViewClient(this);
            downloadListener = new DX5DownloadListener() {
                @Override
                public void onDownloadStart(String s, String s1, String s2, String s3, long l) {

                }
            };
            x5WebSettings = (com.tencent.smtt.sdk.WebSettings) webView.getSettings();
        } else {
            WebView.setWebContentsDebuggingEnabled(true);
            webView = new DNativeWebView(activity);
            webChromeClient = new DNativeWebChromeClient(this);
            webViewClient = new DNativeWebViewClient(this);
            downloadListener = new DNativieDownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                }
            };
            nativeWebSettings = (WebSettings) webView.getSettings();
        }
        setCookie();
        setCookies();
    }

    private void setCookie() {
        if (TextUtils.isEmpty(url) ||
                TextUtils.isEmpty(cookieKey) ||
                TextUtils.isEmpty(cookieValue)) {
            return;
        }
        String cookie = cookieKey + "=" + cookieValue;
        if (CORE_X5.equals(coreType)) {
            com.tencent.smtt.sdk.CookieManager.getInstance().setCookie(url, cookie);
            if (Build.VERSION.SDK_INT < 21) {
                com.tencent.smtt.sdk.CookieSyncManager.getInstance().sync();
            } else {
                com.tencent.smtt.sdk.CookieManager.getInstance().flush();
            }
        } else {
            CookieManager.getInstance().setCookie(url, cookie);
            if (Build.VERSION.SDK_INT < 21) {
                CookieSyncManager.getInstance().sync();
            } else {
                CookieManager.getInstance().flush();
            }
        }

    }

    private void setCookies() {
        if (TextUtils.isEmpty(url) ||
                cookiesMap == null || cookiesMap.size() < 1) {
            return;
        }
        String cookie = "";
        if (CORE_X5.equals(coreType)) {
            Iterator iterator = cookiesMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = cookiesMap.get(key);
                cookie = key + "=" + value;
                com.tencent.smtt.sdk.CookieManager.getInstance().setCookie(url, cookie);
            }
            if (Build.VERSION.SDK_INT < 21) {
                com.tencent.smtt.sdk.CookieSyncManager.getInstance().sync();
            } else {
                com.tencent.smtt.sdk.CookieManager.getInstance().flush();
            }
        } else {
            Iterator iterator = cookiesMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = cookiesMap.get(key);
                cookie = key + "=" + value;
                CookieManager.getInstance().setCookie(url, cookie);
            }
            if (Build.VERSION.SDK_INT < 21) {
                CookieSyncManager.getInstance().sync();
            } else {
                CookieManager.getInstance().flush();
            }
        }

    }


    private void setWebView() {
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.setDownloadListener(downloadListener);
        setWebSettings();
        containerView.addView((View) webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        dhBridge = new DHBridge(pluginManager, webView);
        dhjsInterface = new DHJSInterface(dhBridge);
        webView.addJavascriptInterface(dhjsInterface, BRIDGE_NAME);
        webView.loadUrl(url);
    }

    private void setWebSettings() {
        switch (coreType) {
            case CORE_X5:
                setX5WebSettings();
                break;
            case CORE_NATIVE:
                setNativeSettings();
                break;
            default:
                break;
        }
    }

    /**
     * x5 webView setting
     */
    private void setX5WebSettings() {
        x5WebSettings.setJavaScriptEnabled(true);
        x5WebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        String cacheDirPath = activity.getDir("cache", Context.MODE_PRIVATE).getPath();
        x5WebSettings.setAppCachePath(cacheDirPath);
        x5WebSettings.setAppCacheEnabled(true);
        x5WebSettings.setAllowFileAccess(true);
        x5WebSettings.setDomStorageEnabled(true);
        x5WebSettings.setBuiltInZoomControls(true);
        StringBuilder sb = new StringBuilder(x5WebSettings.getUserAgentString());
        x5WebSettings.setUserAgentString(sb.append(agent).toString());
        Log.e(TAG, x5WebSettings.getUserAgentString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            x5WebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /**
     * native webView setting
     */
    private void setNativeSettings() {
        nativeWebSettings.setJavaScriptEnabled(true);
        nativeWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        String cacheDirPath = activity.getDir("cache", Context.MODE_PRIVATE).getPath();
        nativeWebSettings.setAppCachePath(cacheDirPath);
        nativeWebSettings.setAppCacheEnabled(true);
        nativeWebSettings.setAllowFileAccess(true);
        nativeWebSettings.setDomStorageEnabled(true);
        nativeWebSettings.setBuiltInZoomControls(true);
        StringBuilder sb = new StringBuilder(nativeWebSettings.getUserAgentString());
        nativeWebSettings.setUserAgentString(sb.append(agent).toString());
        Log.e(TAG, nativeWebSettings.getUserAgentString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nativeWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public DWebView getWebView() {
        return webView;
    }

    public DHPluginManager getPluginManager() {
        return pluginManager;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void go(String url) {
        if (webView != null) {
            webView.loadUrl(url);
        }
    }

    public void dump() {
        if (webView != null) {
            webView.dump();
            webView = null;
        }
        webChromeClient = null;
        webViewClient = null;
    }

    public boolean canGoBack() {
        if (webView != null) {
            return webView.canGoBack();
        }
        return false;
    }

    public void goBack() {
        if (webView != null) {
            webView.goBack();
        }
    }

    @Override
    public void startProgress(int newProgress) {
        Log.e(TAG, "onProgressChanged --" + newProgress);
        if (IDHybird != null) {
            IDHybird.pageLoadProgress(newProgress);
        }
    }

    @Override
    public void showWebView() {
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindWebView() {
        webView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void fullViewAddView(View view) {
        FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
        videoFullView = new FullscreenHolder(activity);
        videoFullView.addView(view);
        decor.addView(videoFullView);
    }

    @Override
    public void showVideoFullView() {
        videoFullView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindVideoFullView() {
        videoFullView.setVisibility(View.GONE);
    }

    @Override
    public void setRequestedOrientation(int screenOrientationPortrait) {
        activity.setRequestedOrientation(screenOrientationPortrait);
    }

    @Override
    public FrameLayout getVideoFullView() {
        return videoFullView;
    }

    @Override
    public View getVideoLoadingProgressView() {
        return LayoutInflater.from(activity).inflate(R.layout.video_loading_progress, null);
    }

    @Override
    public void docDownloadFinish(String path) {
        //如果没有实现IDHybird接口，则由Dhybird内部打开，否则把下完完的路径传出
        if (IDHybird == null) {
            FileDisplayActivity.show(activity, path);
        } else {
            IDHybird.docDownloadFinish(path);
        }
    }

    @Override
    public void onLoadError() {
        IDHybird.onLoadError();
    }

    @Override
    public void selectFile(ValueCallback<Uri[]> valueCallback) {
        IDHybird.selectFile(valueCallback);
    }

    @Override
    public void webViewTitle(String title) {
        IDHybird.webViewTitle(title);
    }

    public interface IDHybird {
        //页面加载进度
        void pageLoadProgress(int progress);

        //文档下载完成
        void docDownloadFinish(String path);

        //h5加载错误
        void onLoadError();

        void selectFile(ValueCallback<Uri[]> valueCallback);

        // 获取到网页标题
        void webViewTitle(String title);

    }

    public static class Builder {
        private Activity activity;
        private String coreType;
        private String url;
        private ViewGroup containerView;
        private boolean isDebug;
        private String agent;
        private IDHybird IDHybird;
        private String cookieKey;
        private String cookieValue;
        private Map<String, String> cookiesMap;

        public Builder with(Activity activity) {
            this.activity = activity;
            return this;
        }

        /**
         * 设置webView内核
         */
        public Builder setCore(String coreType) {
            this.coreType = coreType;
            return this;
        }

        /**
         * 设置webView承载容器
         */
        public Builder setContainer(ViewGroup containerView) {
            this.containerView = containerView;
            return this;
        }

        /**
         * 设置是否debug模式
         */
        public Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        /**
         * 设置userAgent
         */
        public Builder setAgent(String agent) {
            this.agent = agent;
            return this;
        }

        /**
         * 设置cookie
         */
        public Builder setCookie(String cookieKey, String cookieValue) {
            this.cookieKey = cookieKey;
            this.cookieValue = cookieValue;
            return this;
        }

        /****
         * 设置cookies
         */

        public Builder setCookies(Map<String, String> cookiesMap) {
            this.cookiesMap = cookiesMap;
            return this;
        }

        /**
         * 设置浏览器进度监听
         */
        public Builder setIDHybird(IDHybird IDHybird) {
            this.IDHybird = IDHybird;
            return this;
        }

        /**
         * 跳转
         */
        public Builder go(String url) {
            this.url = url;
            return this;
        }

        public DHybird build() {
            return new DHybird(this);
        }
    }

    /**
     * native主动给js发消息
     */
    public void sendEventMessageToJS(String eventName, JSONObject params) {
        DHJSResponse jsResponse = DHJSUtils.createJsResponse(eventName,
                true,
                false,
                "",
                params);
        dhBridge.handleJSEventResponse(jsResponse);
    }
}
