package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class OutletsByIdResponse {
    @SerializedName("route_id")
    String routeId;

    @SerializedName("address")
    String address;

    @SerializedName("district")
    String district;
    @SerializedName("contactPerson")
    String contactPerson;

    @SerializedName("customerCode")
    String customerCode;

    @SerializedName("emailid")
    String emailid;

    @SerializedName("mobileno")
    String mobileno;

    @SerializedName("customerName")
    String customerName;

    @SerializedName("routeName")
    String routeName;

    @SerializedName("vehicleNo")
    String vehicleNo;

    @SerializedName("outletName")
    String outletName;

    @SerializedName("outlet_id")
    String outletId;

    @SerializedName("vanName")
    String vanName;

    @SerializedName("id")
    String id;


    @SerializedName("outletCode")
    String outletCode;

    public OutletsByIdResponse() {
    }

    public OutletsByIdResponse(String customerName, String outletname) {
        this.customerName = customerName;
        this.outletName = outletname;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

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

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getVanName() {
        return vanName;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return "OutletsByIdResponse{" +
                "routeId='" + routeId + '\'' +
                ", address='" + address + '\'' +
                ", district='" + district + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", customerCode='" + customerCode + '\'' +
                ", emailid='" + emailid + '\'' +
                ", mobileno='" + mobileno + '\'' +
                ", customerName='" + customerName + '\'' +
                ", routeName='" + routeName + '\'' +
                ", vehicleNo='" + vehicleNo + '\'' +
                ", outletName='" + outletName + '\'' +
                ", outletId='" + outletId + '\'' +
                ", vanName='" + vanName + '\'' +
                ", id='" + id + '\'' +
                ", outletCode='" + outletCode + '\'' +
                '}';
    }
}

