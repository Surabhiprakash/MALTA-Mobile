package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class ListCustomerNonreturnableSkus {
    @SerializedName("customer_id")
    String customerId;

    @SerializedName("item_id")
    String itemId;


    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return itemId;
    }

}
