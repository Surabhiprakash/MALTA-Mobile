package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AgencyResponse {


        @SerializedName("action")
        private String action;

        @SerializedName("message")
        private String message;

        @SerializedName("AgenciesForCustomerCode")
        private List<Agency> agenciesForCustomerCode;

        @SerializedName("status")
        private String status;

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

    public List<Agency> getAgenciesForCustomerCode() {
        return agenciesForCustomerCode;
    }

    public void setAgenciesForCustomerCode(List<Agency> agenciesForCustomerCode) {
        this.agenciesForCustomerCode = agenciesForCustomerCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
