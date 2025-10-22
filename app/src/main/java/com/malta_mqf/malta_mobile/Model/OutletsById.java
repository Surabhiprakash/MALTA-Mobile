package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutletsById {
    @SerializedName("Outlet_SKUs")
    List<OutletSKUs> OutletSKUs;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("OutletDetailsBasOnVan")
    List<OutletsByIdResponse> OutletDetailsBasOnVan;

    @SerializedName("status")
    String status;

    @SerializedName("list_customer_nonreturnable_skus")
    List<ListCustomerNonreturnableSkus> listCustomerNonreturnableSkus;


    public void setOutletSKUs(List<OutletSKUs> OutletSKUs) {
        this.OutletSKUs = OutletSKUs;
    }
    public List<OutletSKUs> getOutletSKUs() {
        return OutletSKUs;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setOutletDetailsBasOnVan(List<OutletsByIdResponse> OutletDetailsBasOnVan) {
        this.OutletDetailsBasOnVan = OutletDetailsBasOnVan;
    }
    public List<OutletsByIdResponse> getOutletDetailsBasOnVan() {
        return OutletDetailsBasOnVan;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setListCustomerNonreturnableSkus(List<ListCustomerNonreturnableSkus> listCustomerNonreturnableSkus) {
        this.listCustomerNonreturnableSkus = listCustomerNonreturnableSkus;
    }
    public List<ListCustomerNonreturnableSkus> getListCustomerNonreturnableSkus() {
        return listCustomerNonreturnableSkus;
    }
}
