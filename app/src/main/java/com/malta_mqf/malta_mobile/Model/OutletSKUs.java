package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class OutletSKUs {

    @SerializedName("outlet_id")
    String outletId;

    @SerializedName("item_id")
    String itemId;


    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }
    public String getOutletId() {
        return outletId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return itemId;
    }

}
