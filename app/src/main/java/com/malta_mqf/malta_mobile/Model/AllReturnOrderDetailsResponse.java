package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllReturnOrderDetailsResponse {
    @SerializedName("ReturnsInfo")
    List<AllReturnOrderDetails> ReturnsInfo;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    public List<AllReturnOrderDetails> getReturnsInfo() {
        return ReturnsInfo;
    }

    public void setReturnsInfo(List<AllReturnOrderDetails> ReturnsInfo) {
        this.ReturnsInfo = ReturnsInfo;
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
