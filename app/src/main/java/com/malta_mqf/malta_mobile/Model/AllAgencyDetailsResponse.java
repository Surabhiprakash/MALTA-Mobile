package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class AllAgencyDetailsResponse {
    @SerializedName("address")
    String address;

    @SerializedName("contactPerson")
    String contactPerson;

    @SerializedName("emailid")
    String emailid;

    @SerializedName("id")
    String id;

    @SerializedName("agencyCode")
    String agencyCode;

    @SerializedName("mobileno")
    String mobileno;

    @SerializedName("agencyName")
    String agencyName;

    @SerializedName("status")
    String status;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AllAgencyDetailsResponse{" +
                "address='" + address + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", emailid='" + emailid + '\'' +
                ", id='" + id + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                ", mobileno='" + mobileno + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
