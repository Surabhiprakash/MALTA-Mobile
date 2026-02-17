package com.malta_mqf.malta_mobile.Model;
import com.google.gson.annotations.SerializedName;

public class Agency {

    @SerializedName("agencyName")
    private String agencyName;

    @SerializedName("agencyCode")
    private String agencyCode;

    // Getters
    public String getAgencyName() {
        return agencyName;
    }

    public String getAgencyCode() {
        return agencyCode;
    }
}
