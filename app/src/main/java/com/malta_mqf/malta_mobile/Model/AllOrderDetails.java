package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class AllOrderDetails {

    @SerializedName("van_id")
    private String vanId;

    @SerializedName("route_id")
    private String routeId;

    @SerializedName("outlet_id")
    private String outletId;

    @SerializedName("orderid")
    private String orderId;

    @SerializedName("vanName")
    private String vanName;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("id")
    private String id;

    @SerializedName("outlet_name")
    private String outletName;

    @SerializedName("routeName")
    private String routeName;

    @SerializedName("ordered_datetime")
    private String orderDateTime;

    public String getVanId() {
        return vanId;
    }
    public void setVanId(String vanId) {
        this.vanId = vanId;
    }
    public String getRouteId() {
        return routeId;
    }
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    public String getOutletId() {
        return outletId;
    }
    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }
    public String getOrderid() {
        return orderId;
    }
    public void setOrderid(String orderid) {
        this.orderId = orderid;
    }
    public String getVanName() {
        return vanName;
    }
    public void setVanName(String vanName) {
        this.vanName = vanName;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOutletName() {
        return outletName;
    }
    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }
    public String getRouteName() {
        return routeName;
    }
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public AllOrderDetails(  String orderid) {

        this.orderId = orderid;

    }



    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    @Override
    public String toString() {
        return "AllOrderDetails{" +
                "vanId='" + vanId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", outletId='" + outletId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", vanName='" + vanName + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", id='" + id + '\'' +
                ", outletName='" + outletName + '\'' +
                ", routeName='" + routeName + '\'' +
                ", orderDateTime='" + orderDateTime + '\'' +
                '}';
    }
}
