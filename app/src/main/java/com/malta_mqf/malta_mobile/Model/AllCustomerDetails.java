package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllCustomerDetails {

    @SerializedName("ActiveCustomerDetails")
    List<AllCustomerDetailsResponse> ActiveCustomerDetails;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    public List<AllCustomerDetailsResponse> getActiveCustomerDetails() {
        return ActiveCustomerDetails;
    }

    public void setActiveCustomerDetails(List<AllCustomerDetailsResponse> ActiveCustomerDetails) {
        this.ActiveCustomerDetails = ActiveCustomerDetails;
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
        return "AllCustomerDetails{" +
                "ActiveCustomerDetails=" + ActiveCustomerDetails +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
