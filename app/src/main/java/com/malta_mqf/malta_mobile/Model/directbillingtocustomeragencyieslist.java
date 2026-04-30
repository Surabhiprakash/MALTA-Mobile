package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class directbillingtocustomeragencyieslist {
    @SerializedName("action")
    private String action;
    @SerializedName("agency_billing_list")
    private List<AgencyDirectBillingCustomer> agency_billing_list;
    @SerializedName("AgencyBasedDirectBillingDetailsForAllCustomersWithSKUS")
    private List<agencyskuassosiation> AgencyBasedDirectBillingDetailsForAllCustomersWithSKUS;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<agencyskuassosiation> getAgencyBasedDirectBillingDetailsForAllCustomersWithSKUS() {
        return AgencyBasedDirectBillingDetailsForAllCustomersWithSKUS;
    }

    public void setAgencyBasedDirectBillingDetailsForAllCustomersWithSKUS(List<agencyskuassosiation> agencyBasedDirectBillingDetailsForAllCustomersWithSKUS) {
        AgencyBasedDirectBillingDetailsForAllCustomersWithSKUS = agencyBasedDirectBillingDetailsForAllCustomersWithSKUS;
    }

    public List<AgencyDirectBillingCustomer> getAgency_billing_list() {
        return agency_billing_list;
    }

    public void setAgency_billing_list(List<AgencyDirectBillingCustomer> agency_billing_list) {
        this.agency_billing_list = agency_billing_list;
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
