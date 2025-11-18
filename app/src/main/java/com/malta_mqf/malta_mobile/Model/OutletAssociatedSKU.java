package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class OutletAssociatedSKU {

    @SerializedName("item_id")
    private String itemId;  // Comma-separated string

    @SerializedName("ou_id")
    private String outletId;

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }
}

