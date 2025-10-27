package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllItemDeatilsById {
    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("ActiveItemDetailsWithSellingPrice")
    List<AllItemDetailResponseById> ActiveItemDetailsWithSellingPrice;

    @SerializedName("status")
    String status;

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

    public List<AllItemDetailResponseById> getActiveItemDetailsWithSellingPrice() {
        return ActiveItemDetailsWithSellingPrice;
    }

    public void setActiveItemDetailsWithSellingPrice(List<AllItemDetailResponseById> ActiveItemDetailsWithSellingPrice) {
        this.ActiveItemDetailsWithSellingPrice = ActiveItemDetailsWithSellingPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AllItemDeatilsById{" +
                "action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", ActiveItemDetailsWithSellingPrice=" + ActiveItemDetailsWithSellingPrice +
                ", status='" + status + '\'' +
                '}';
    }
}
