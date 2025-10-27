package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class DashBoardResponse {
    @SerializedName("SalesReturnsForTabList")
    SalesReturnsForTabList SalesReturnsForTabList;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    public SalesReturnsForTabList getSalesReturnsForTabList() {
        return SalesReturnsForTabList;
    }

    public void setSalesReturnsForTabList(SalesReturnsForTabList SalesReturnsForTabList) {
        this.SalesReturnsForTabList = SalesReturnsForTabList;
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
        return "DashBoardResponse{" +
                "SalesReturnsForTabList=" + SalesReturnsForTabList +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

