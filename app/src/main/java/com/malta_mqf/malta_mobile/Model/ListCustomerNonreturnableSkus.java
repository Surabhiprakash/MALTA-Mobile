package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class ListCustomerNonreturnableSkus {
    @SerializedName("customer_id")
    String customerId;

    @SerializedName("item_id")
    String itemId;
    @SerializedName("customer_code")
    String customer_code;

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

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }
    public String getCustomer_code() {
        return customer_code;
    }
}
