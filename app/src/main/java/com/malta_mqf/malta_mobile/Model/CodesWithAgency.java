package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class CodesWithAgency {

    @SerializedName("agency_code")
    private String agency_code;

    @SerializedName("security_code")
    private String security_code;

    public String getAgencyCode() {
        return agency_code;
    }

    public String getSecretCode() {
        return security_code;
    }

    @Override
    public String toString() {
        return "CodesWithAgency{" +
                "agency_code='" + agency_code + '\'' +
                ", security_code='" + security_code + '\'' +
                '}';
    }
}




