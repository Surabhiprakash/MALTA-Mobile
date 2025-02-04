package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutletsById {
    @SerializedName("action")
    String action;

    @SerializedName("OutletDetailsBasOnVan")
    List<OutletsByIdResponse> OutletDetailsBasOnVan;

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

    public void setOutletDetailsBasOnVan(List<OutletsByIdResponse> OutletDetailsBasOnVan) {
        this.OutletDetailsBasOnVan = OutletDetailsBasOnVan;
    }
    public List<OutletsByIdResponse> getOutletDetailsBasOnVan() {
        return OutletDetailsBasOnVan;
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
        return "OutletsById{" +
                "action='" + action + '\'' +
                ", OutletDetailsBasOnVan=" + OutletDetailsBasOnVan +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
