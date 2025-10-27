package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class OutletAssociatedSKUAgency {

    @SerializedName("agency_id")
    private String agencyId;

    @SerializedName("agencyName")
    private String agencyName;

    @SerializedName("agencyCode")
    private String agencyCode;

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
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

