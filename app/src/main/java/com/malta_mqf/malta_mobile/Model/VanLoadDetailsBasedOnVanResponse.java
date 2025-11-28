package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VanLoadDetailsBasedOnVanResponse {
    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("VanLoadDataForVan")
    List<VanLoadDataForVanDetails> VanLoadDataForVan;

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

    public List<VanLoadDataForVanDetails> getVanLoadDataForVan() {
        return VanLoadDataForVan;
    }

    public void setVanLoadDataForVan(List<VanLoadDataForVanDetails> VanLoadDataForVan) {
        this.VanLoadDataForVan = VanLoadDataForVan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VanLoadDetailsBasedOnVanResponse{" +
                "action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", VanLoadDataForVan=" + VanLoadDataForVan +
                ", status='" + status + '\'' +
                '}';
    }
}
