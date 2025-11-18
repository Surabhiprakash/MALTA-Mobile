package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;


public class OfflineOutletSkuAssosiatedResponse {

    @SerializedName("VanAssociatedOutletAssociatedSKUS")
    private List<OutletAssociatedSKU> offlineoutletAssociatedSKUS;

    @SerializedName("action")
    private String action;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    // Getters and Setters
    public List<OutletAssociatedSKU> getOutletAssociatedSKUS() {
        return offlineoutletAssociatedSKUS;
    }

    public void setOutletAssociatedSKUS(List<OutletAssociatedSKU> outletAssociatedSKUS) {
        this.offlineoutletAssociatedSKUS = outletAssociatedSKUS;
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
}
