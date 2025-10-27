package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OutletAssociatedSKUAgencyResponse {

    @SerializedName("OutletAssociatedSKUAgency")
    private List<OutletAssociatedSKUAgency> outletAssociatedSKUAgencyList;

    @SerializedName("action")
    private String action;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public List<OutletAssociatedSKUAgency> getOutletAssociatedSKUAgencyList() {
        return outletAssociatedSKUAgencyList;
    }

    public void setOutletAssociatedSKUAgencyList(List<OutletAssociatedSKUAgency> outletAssociatedSKUAgencyList) {
        this.outletAssociatedSKUAgencyList = outletAssociatedSKUAgencyList;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}