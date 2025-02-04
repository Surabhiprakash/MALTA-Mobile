package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApprovedOrdersBasedOnVanId {
    @SerializedName("ApprovedOrderDetailsBsdOnVanid")
    List<ApprovedOrdersDetailsBsdOnVanIdResponse> ApprovedOrderDetailsBsdOnVanid;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;


    public void setApprovedOrderDetailsBsdOnVanid(List<ApprovedOrdersDetailsBsdOnVanIdResponse> ApprovedOrderDetailsBsdOnVanid) {
        this.ApprovedOrderDetailsBsdOnVanid = ApprovedOrderDetailsBsdOnVanid;
    }
    public List<ApprovedOrdersDetailsBsdOnVanIdResponse> getApprovedOrderDetailsBsdOnVanid() {
        return ApprovedOrderDetailsBsdOnVanid;
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

    @Override
    public String toString() {
        return "ApprovedOrdersBasedOnVanId{" +
                "ApprovedOrderDetailsBsdOnVanid=" + ApprovedOrderDetailsBsdOnVanid +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
