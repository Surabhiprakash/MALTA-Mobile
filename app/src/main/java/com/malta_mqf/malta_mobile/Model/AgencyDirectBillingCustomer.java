package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class AgencyDirectBillingCustomer {
    @SerializedName("billing_address")
    private String billing_address;
    @SerializedName("id")
    private String id;
    @SerializedName("agencyName")
    private String agencyName;
    @SerializedName("agencyCode")
    private String agencyCode;

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }
}
