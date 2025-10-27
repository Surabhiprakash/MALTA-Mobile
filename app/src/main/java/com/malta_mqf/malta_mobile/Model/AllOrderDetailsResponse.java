package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllOrderDetailsResponse {
    @SerializedName("action")
    private String action;

    @SerializedName("AllOrderDetails")
    private List<AllOrderDetails> allOrderDetails;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public AllOrderDetailsResponse(String message, String status, String action, List<AllOrderDetails> AllOrderDetails) {
        this.message = message;
        this.status = status;
        this.action = action;
        this.allOrderDetails = AllOrderDetails;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<AllOrderDetails> getAllOrderDetails() {
        return allOrderDetails;
    }

    public void setAllOrderDetails(List<AllOrderDetails> allOrderDetails) {
        this.allOrderDetails = allOrderDetails;
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

    @Override
    public String toString() {
        return "AllOrderDetailsResponse{" +
                "message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
