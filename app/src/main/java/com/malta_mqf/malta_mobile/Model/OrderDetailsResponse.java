package com.malta_mqf.malta_mobile.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class OrderDetailsResponse {

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;


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

    @NonNull
    @Override
    public String toString() {
        return "OrderDetailsResponse{" +
                "action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
