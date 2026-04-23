package com.malta_mqf.malta_mobile.Model;

import java.util.List;

public class directbillingtocustomeragencyieslist {

    private String action;
    private List<AgencyDirectBillingCustomer> AgencyDirectBillingCustomersForAllAgencies;
    private String message;
    private String status;

    // Getters and Setters

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<AgencyDirectBillingCustomer> getAgencyDirectBillingCustomersForAllAgencies() {
        return AgencyDirectBillingCustomersForAllAgencies;
    }

    public void setAgencyDirectBillingCustomersForAllAgencies(List<AgencyDirectBillingCustomer> agencyDirectBillingCustomersForAllAgencies) {
        AgencyDirectBillingCustomersForAllAgencies = agencyDirectBillingCustomersForAllAgencies;
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
