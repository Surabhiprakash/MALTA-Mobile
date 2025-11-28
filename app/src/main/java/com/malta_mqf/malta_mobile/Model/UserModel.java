package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("role")
    String role;

    @SerializedName("route_id")
    String routeId;

    @SerializedName("emailid")
    String emailid;

    @SerializedName("mobileno")
    String mobileno;

    @SerializedName("message")
    String message;

    @SerializedName("routeName")
    String routeName;

    @SerializedName("van_id")
    String vanId;

    @SerializedName("vehicleno")
    String vehicleNo;

    @SerializedName("outletName")
    String outletName;

    @SerializedName("outlet_id")
    String outletId;

    @SerializedName("empCode")
    String empCode;

    @SerializedName("vanName")
    String vanName;

    @SerializedName("name")
    String name;

    @SerializedName("action")
    String action;

    @SerializedName("id")
    int id;

    @SerializedName("status")
    String status;
    @SerializedName("categoryName")
    String category;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getVanId() {
        return vanId;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
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

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getVanName() {
        return vanName;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "role='" + role + '\'' +
                ", routeId='" + routeId + '\'' +
                ", emailid='" + emailid + '\'' +
                ", mobileno='" + mobileno + '\'' +
                ", message='" + message + '\'' +
                ", routeName='" + routeName + '\'' +
                ", vanId='" + vanId + '\'' +
                ", vehicleNo='" + vehicleNo + '\'' +
                ", outletName='" + outletName + '\'' +
                ", outletId='" + outletId + '\'' +
                ", empCode='" + empCode + '\'' +
                ", vanName='" + vanName + '\'' +
                ", name='" + name + '\'' +
                ", action='" + action + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
