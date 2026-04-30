package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class agencyskuassosiation {
    @SerializedName("agency_id")
    private String agency_id;
    @SerializedName("customer_id")
    private String customer_id;
    @SerializedName("sku_ids")
    private String sku_ids;

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getSku_ids() {
        return sku_ids;
    }

    public void setSku_ids(String sku_ids) {
        this.sku_ids = sku_ids;
    }
}
