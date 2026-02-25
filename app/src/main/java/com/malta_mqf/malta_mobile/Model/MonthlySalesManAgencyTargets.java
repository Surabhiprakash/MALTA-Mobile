package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class MonthlySalesManAgencyTargets {
    @SerializedName("user_id")
    String userId;

    @SerializedName("yr_mnth")
    String yrMnth;

    @SerializedName("a_id")
    String aId;

    @SerializedName("a_name")
    String aName;

    @SerializedName("amt")
    String amt;


    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setYrMnth(String yrMnth) {
        this.yrMnth = yrMnth;
    }
    public String getYrMnth() {
        return yrMnth;
    }

    public void setAId(String aId) {
        this.aId = aId;
    }
    public String getAId() {
        return aId;
    }

    public void setAName(String aName) {
        this.aName = aName;
    }
    public String getAName() {
        return aName;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
    public String getAmt() {
        return amt;
    }
}
