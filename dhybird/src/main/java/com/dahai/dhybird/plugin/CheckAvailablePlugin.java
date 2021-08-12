package com.dahai.dhybird.plugin;

import android.content.Context;
import android.util.Log;

import com.dahai.dhybird.bridge.DHCallbackContext;
import com.dahai.dhybird.bridge.DHJSRequest;
import com.dahai.dhybird.bridge.DHPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckAvailablePlugin implements DHPlugin {

    @Override
    public String exec(DHJSRequest dhjsRequest, DHCallbackContext dhCallbackContext) {
        JSONObject data = dhjsRequest.getData();
        List<String> checkList = new ArrayList<>();
        JSONObject result = new JSONObject();
        try {
            JSONArray dataJSONArray = (JSONArray) data.get("available");
            int length = dataJSONArray.length();
            for (int i = 0; i < length; i++) {
                String checkName = (String) dataJSONArray.get(i);
                boolean checkResult = dhCallbackContext.getPluginManager().checkPlugin(checkName);
                checkList.add(checkResult ? "true" : "false");
            }
            result.put("available", checkList);
        } catch (JSONException e) {
            e.printStackTrace();
            return dhCallbackContext.fail("异常");
        }
        return dhCallbackContext.success(result);
    }
}
