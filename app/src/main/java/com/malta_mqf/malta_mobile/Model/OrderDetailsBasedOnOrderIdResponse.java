package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailsBasedOnOrderIdResponse {
    @SerializedName("ApprovedOrderDetailsBsdOnOrderid")
    List<OrderDetailBasedOnOrderId> ApprovedOrderDetailsBsdOnOrderid;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    public List<OrderDetailBasedOnOrderId> getApprovedOrderDetailsBsdOnOrderid() {
        return ApprovedOrderDetailsBsdOnOrderid;
    }

    public void setApprovedOrderDetailsBsdOnOrderid(List<OrderDetailBasedOnOrderId> ApprovedOrderDetailsBsdOnOrderid) {
        this.ApprovedOrderDetailsBsdOnOrderid = ApprovedOrderDetailsBsdOnOrderid;
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

    @Override
    public String toString() {
        return "OrderDetailsBasedOnOrderIdResponse{" +
                "ApprovedOrderDetailsBsdOnOrderid=" + ApprovedOrderDetailsBsdOnOrderid +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
