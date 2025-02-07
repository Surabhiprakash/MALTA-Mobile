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


    public void setReturnsOutletsInfo(List<String> ReturnsOutletsInfo) {
        this.ReturnsOutletsInfo = ReturnsOutletsInfo;
    }
    public List<String> getReturnsOutletsInfo() {
        return ReturnsOutletsInfo;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

}
