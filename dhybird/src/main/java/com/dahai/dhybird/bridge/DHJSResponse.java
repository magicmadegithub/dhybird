package com.dahai.dhybird.bridge;

import org.json.JSONException;
import org.json.JSONObject;

public class DHJSResponse {

    private String callbackId;

    private int status = DHJSConst.STATUS_SUCCESS;

    private int complete = DHJSConst.CALLBACK_COMPLETE;

    private String errorMessage;

    private JSONObject data;

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("callbackId", callbackId);
            jo.put("status", status);
            jo.put("complete", complete);
            jo.put("errorMessage", errorMessage);
            jo.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
    }
}
