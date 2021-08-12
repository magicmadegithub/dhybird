package com.dahai.dhybird.plugin;

import android.content.Context;
import android.widget.Toast;

import com.dahai.dhybird.bridge.DHCallbackContext;
import com.dahai.dhybird.bridge.DHJSRequest;
import com.dahai.dhybird.bridge.DHPlugin;

import org.json.JSONException;
import org.json.JSONObject;

public class ToastPlugin implements DHPlugin {

    @Override
    public String exec(DHJSRequest dhjsRequest, DHCallbackContext dhCallbackContext) {
        Context context = dhCallbackContext.getActivity();
        JSONObject data = dhjsRequest.getData();
        String title = data.optString("title");
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
        JSONObject result = new JSONObject();
        try {
            result.put("title", "弹出成功");
            result.put("message", "ojbk");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dhCallbackContext.success(result);
        return title;
    }
}
