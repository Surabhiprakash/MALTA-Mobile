package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TotalPerItemsByVanId {
    @SerializedName("ItemWiseOrdersBasedOnVan")
    List<TotalPerItemsByVanIdResponse> ItemWiseOrdersBasedOnVan;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    public List<TotalPerItemsByVanIdResponse> getItemWiseOrdersBasedOnVan() {
        return ItemWiseOrdersBasedOnVan;
    }

    public void setItemWiseOrdersBasedOnVan(List<TotalPerItemsByVanIdResponse> ItemWiseOrdersBasedOnVan) {
        this.ItemWiseOrdersBasedOnVan = ItemWiseOrdersBasedOnVan;
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
        return "TotalPerItemsByVanId{" +
                "ItemWiseOrdersBasedOnVan=" + ItemWiseOrdersBasedOnVan +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
