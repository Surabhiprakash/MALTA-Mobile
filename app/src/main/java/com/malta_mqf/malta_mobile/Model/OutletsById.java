package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutletsById {


    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("Outlet_SKUs")
    List<OutletSKUs> OutletSKUs;

    @SerializedName("OutletDetailsBasOnVan")
    List<OutletsByIdResponse> OutletDetailsBasOnVan;

    @SerializedName("list_customer_nonreturnable_skus")
    List<ListCustomerNonreturnableSkus> listCustomerNonreturnableSkus;


    @SerializedName("status")
    String status;

    public List<OutletSKUs> getOutletSKUs() {
        return OutletSKUs;
    }

    public void setOutletSKUs(List<OutletSKUs> OutletSKUs) {
        this.OutletSKUs = OutletSKUs;
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

    public List<OutletsByIdResponse> getOutletDetailsBasOnVan() {
        return OutletDetailsBasOnVan;
    }

    public void setOutletDetailsBasOnVan(List<OutletsByIdResponse> OutletDetailsBasOnVan) {
        this.OutletDetailsBasOnVan = OutletDetailsBasOnVan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ListCustomerNonreturnableSkus> getListCustomerNonreturnableSkus() {
        return listCustomerNonreturnableSkus;
    }

    public void setListCustomerNonreturnableSkus(List<ListCustomerNonreturnableSkus> listCustomerNonreturnableSkus) {
        this.listCustomerNonreturnableSkus = listCustomerNonreturnableSkus;
    }
}
