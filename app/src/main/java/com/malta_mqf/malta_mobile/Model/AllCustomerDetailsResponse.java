package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class AllCustomerDetailsResponse {

    @SerializedName("creditPeriod_id")
    String creditPeriodId;

    @SerializedName("address")
    String address;

    @SerializedName("customerType_id")
    String customerTypeId;

    @SerializedName("rebate")
    String rebate;

    @SerializedName("latitude")
    String latitude;

    @SerializedName("customerCode")
    String customerCode;

    @SerializedName("contactPerson")
    String contactPerson;

    @SerializedName("emailid")
    String emailid;

    @SerializedName("mobileno")
    String mobileno;

    @SerializedName("creditPeriod")
    String creditPeriod;

    @SerializedName("customerName")
    String customerName;

    @SerializedName("district_name")
    String districtName;

    @SerializedName("customerType")
    String customerType;

    @SerializedName("creditLimit")
    String creditLimit;

    @SerializedName("id")
    String id;

    @SerializedName("district_id")
    String districtId;

    @SerializedName("longitude")
    String longitude;

    @SerializedName("status")
    String status;
    @SerializedName("trnno")
    String trn;


    public void setCreditPeriodId(String creditPeriodId) {
        this.creditPeriodId = creditPeriodId;
    }
    public String getCreditPeriodId() {
        return creditPeriodId;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }

    public void setCustomerTypeId(String customerTypeId) {
        this.customerTypeId = customerTypeId;
    }
    public String getCustomerTypeId() {
        return customerTypeId;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }
    public String getRebate() {
        return rebate;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
    public String getCustomerCode() {
        return customerCode;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    public String getContactPerson() {
        return contactPerson;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }
    public String getEmailid() {
        return emailid;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }
    public String getMobileno() {
        return mobileno;
    }

    public void setCreditPeriod(String creditPeriod) {
        this.creditPeriod = creditPeriod;
    }
    public String getCreditPeriod() {
        return creditPeriod;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
    public String getDistrictName() {
        return districtName;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    public String getCustomerType() {
        return customerType;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }
    public String getCreditLimit() {
        return creditLimit;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }
    public String getDistrictId() {
        return districtId;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public String getTrn() {
        return trn;
    }

    public void setTrn(String trn) {
        this.trn = trn;
    }

    @Override
    public String toString() {
        return "AllCustomerDetailsResponse{" +
                "creditPeriodId='" + creditPeriodId + '\'' +
                ", address='" + address + '\'' +
                ", customerTypeId='" + customerTypeId + '\'' +
                ", rebate='" + rebate + '\'' +
                ", latitude='" + latitude + '\'' +
                ", customerCode='" + customerCode + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", emailid='" + emailid + '\'' +
                ", mobileno='" + mobileno + '\'' +
                ", creditPeriod='" + creditPeriod + '\'' +
                ", customerName='" + customerName + '\'' +
                ", districtName='" + districtName + '\'' +
                ", customerType='" + customerType + '\'' +
                ", creditLimit='" + creditLimit + '\'' +
                ", id='" + id + '\'' +
                ", districtId='" + districtId + '\'' +
                ", longitude='" + longitude + '\'' +
                ", status='" + status + '\'' +
                ", trn='" + trn + '\'' +
                '}';
    }
}
