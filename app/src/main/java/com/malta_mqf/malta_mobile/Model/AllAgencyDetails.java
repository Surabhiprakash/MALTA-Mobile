package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllAgencyDetails {

    @SerializedName("action")
    String action;

    @SerializedName("ActiveAgencyDetails")
    List<AllAgencyDetailsResponse> ActiveAgencyDetails;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<AllAgencyDetailsResponse> getActiveAgencyDetails() {
        return ActiveAgencyDetails;
    }

    public void setActiveAgencyDetails(List<AllAgencyDetailsResponse> ActiveAgencyDetails) {
        this.ActiveAgencyDetails = ActiveAgencyDetails;
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
        return "AllAgencyDetails{" +
                "action='" + action + '\'' +
                ", ActiveAgencyDetails=" + ActiveAgencyDetails +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
