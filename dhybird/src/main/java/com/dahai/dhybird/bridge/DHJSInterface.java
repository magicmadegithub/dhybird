package com.dahai.dhybird.bridge;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.dahai.dhybird.DHybird;

public class DHJSInterface {

    private DHBridge dhBridge;

    public DHJSInterface(DHBridge dhBridge) {
        this.dhBridge = dhBridge;
    }

    @JavascriptInterface
    public String invoke(String message) {
        Log.e(DHybird.TAG, message);
        return dhBridge.jsExec(message);
    }

}
