package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TotalItemsPerVanIdPoResponse {
    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    @SerializedName("ItemWiseOrdersBasedOnVanPowise")
    List<ItemWiseOrdersBasedOnVanPowiseDetails> ItemWiseOrdersBasedOnVanPowise;

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

    public List<ItemWiseOrdersBasedOnVanPowiseDetails> getItemWiseOrdersBasedOnVanPowise() {
        return ItemWiseOrdersBasedOnVanPowise;
    }

    public void setItemWiseOrdersBasedOnVanPowise(List<ItemWiseOrdersBasedOnVanPowiseDetails> ItemWiseOrdersBasedOnVanPowise) {
        this.ItemWiseOrdersBasedOnVanPowise = ItemWiseOrdersBasedOnVanPowise;
    }

    @Override
    public String toString() {
        return "TotalItemsPerVanIdPoResponse{" +
                "action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", ItemWiseOrdersBasedOnVanPowise=" + ItemWiseOrdersBasedOnVanPowise +
                '}';
    }
}
