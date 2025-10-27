package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class vanStockTransactionResponse {
    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("VanStockDetails")
    List<VanStockDetails> VanStockDetails;

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

    public List<VanStockDetails> getVanStockDetails() {
        return VanStockDetails;
    }

    public void setVanStockDetails(List<VanStockDetails> VanStockDetails) {
        this.VanStockDetails = VanStockDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
