package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class OrderDetailBasedOnOrderId {
    @SerializedName("id")
    String id;

    @SerializedName("user_id")
    String userId;

    @SerializedName("agencyName")
    String agencyName;

    @SerializedName("outletName")
    String outletName;

    @SerializedName("outlet_id")
    String outletId;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("uom")
    String uom;

    @SerializedName("route_id")
    String routeId;

    @SerializedName("van_id")
    String vanId;

    @SerializedName("item_id")
    String itemId;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("ordered_datetime")
    String orderedDatetime;

    @SerializedName("orderStatus")
    String orderStatus;

    @SerializedName("approved_datetime")
    String approvedDatetime;

    @SerializedName("uomId")
    String uomId;

    @SerializedName("ordered_qty")
    String orderedQty;

    @SerializedName("po_reference")
    String poReference;

    @SerializedName("approved_qty")
    String approvedQty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getVanId() {
        return vanId;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderedDatetime() {
        return orderedDatetime;
    }

    public void setOrderedDatetime(String orderedDatetime) {
        this.orderedDatetime = orderedDatetime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getApprovedDatetime() {
        return approvedDatetime;
    }

    public void setApprovedDatetime(String approvedDatetime) {
        this.approvedDatetime = approvedDatetime;
    }

    public String getUomId() {
        return uomId;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }

    public String getOrderedQty() {
        return orderedQty;
    }

    public void setOrderedQty(String orderedQty) {
        this.orderedQty = orderedQty;
    }

    public String getPoReference() {
        return poReference;
    }

    public void setPoReference(String poReference) {
        this.poReference = poReference;
    }

    public String getApprovedQty() {
        return approvedQty;
    }

    public void setApprovedQty(String approvedQty) {
        this.approvedQty = approvedQty;
    }

    @Override
    public String toString() {
        return "OrderDetailBasedOnOrderId{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", outletName='" + outletName + '\'' +
                ", outletId='" + outletId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", uom='" + uom + '\'' +
                ", routeId='" + routeId + '\'' +
                ", vanId='" + vanId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", orderid='" + orderid + '\'' +
                ", orderedDatetime='" + orderedDatetime + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", approvedDatetime='" + approvedDatetime + '\'' +
                ", uomId='" + uomId + '\'' +
                ", orderedQty='" + orderedQty + '\'' +
                ", poReference='" + poReference + '\'' +
                ", approvedQty='" + approvedQty + '\'' +
                '}';
    }
}
