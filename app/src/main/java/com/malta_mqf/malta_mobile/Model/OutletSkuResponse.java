package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutletSkuResponse {

    @SerializedName("action")
    private String action;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    @SerializedName("OutletAssociatedSKUAgencyItems")
    private List<OutletSkuItem> items;

    // Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OutletSkuItem> getItems() { return items; }
    public void setItems(List<OutletSkuItem> items) { this.items = items; }
}
