package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OnlineReturnInfoResponse {
    @SerializedName("ReturnsOutletsInfo")
    List<String> ReturnsOutletsInfo;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    public List<String> getReturnsOutletsInfo() {
        return ReturnsOutletsInfo;
    }

    public void setReturnsOutletsInfo(List<String> ReturnsOutletsInfo) {
        this.ReturnsOutletsInfo = ReturnsOutletsInfo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
