package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class ApprovedOrdersDetailsBsdOnVanIdResponse {
    @SerializedName("id")
    String id;

    @SerializedName("van_id")
    String vanId;

    @SerializedName("uom")
    String uom;

    @SerializedName("vanName")
    String vanName;

    @SerializedName("user_id")
    String userId;

    @SerializedName("outletName")
    String outletName;

    @SerializedName("outlet_id")
    String outletId;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("po_reference")
    String poReference;

    @SerializedName("item_id")
    String itemId;

    @SerializedName("approved_qty")
    String approvedQty;

    @SerializedName("approvedBy")
    String approvedBy;

    @SerializedName("approved_datetime")
    String approvedDatetime;

    @SerializedName("ordered_datetime")
    String orderedDatetime;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("orderStatus")
    String orderStatus;

    @SerializedName("uomId")
    String uomId;

    @SerializedName("ordered_qty")
    String orderedQty;

    @SerializedName("poRefName")
    String porefname;

    @SerializedName("pocreated_datetime")
    String pocreatedDate;

    @SerializedName("categoryName")
    String categoryName;

    @SerializedName("subCategory_name")
    String subCategoryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVanId() {
        return vanId;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getVanName() {
        return vanName;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPoReference() {
        return poReference;
    }

    public void setPoReference(String poReference) {
        this.poReference = poReference;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getApprovedQty() {
        return approvedQty;
    }

    public void setApprovedQty(String approvedQty) {
        this.approvedQty = approvedQty;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovedDatetime() {
        return approvedDatetime;
    }

    public void setApprovedDatetime(String approvedDatetime) {
        this.approvedDatetime = approvedDatetime;
    }

    public String getOrderedDatetime() {
        return orderedDatetime;
    }

    public void setOrderedDatetime(String orderedDatetime) {
        this.orderedDatetime = orderedDatetime;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public String getPorefname() {
        return porefname;
    }

    public void setPorefname(String porefname) {
        this.porefname = porefname;
    }

    public String getPocreatedDate() {
        return pocreatedDate;
    }

    public void setPocreatedDate(String pocreatedDate) {
        this.pocreatedDate = pocreatedDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    @Override
    public String toString() {
        return "ApprovedOrdersDetailsBsdOnVanIdResponse{" +
                "id='" + id + '\'' +
                ", vanId='" + vanId + '\'' +
                ", uom='" + uom + '\'' +
                ", vanName='" + vanName + '\'' +
                ", userId='" + userId + '\'' +
                ", outletName='" + outletName + '\'' +
                ", outletId='" + outletId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", poReference='" + poReference + '\'' +
                ", itemId='" + itemId + '\'' +
                ", approvedQty='" + approvedQty + '\'' +
                ", approvedBy='" + approvedBy + '\'' +
                ", approvedDatetime='" + approvedDatetime + '\'' +
                ", orderedDatetime='" + orderedDatetime + '\'' +
                ", orderid='" + orderid + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", uomId='" + uomId + '\'' +
                ", orderedQty='" + orderedQty + '\'' +
                ", porefname='" + porefname + '\'' +
                ", pocreatedDate='" + pocreatedDate + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", subCategoryName='" + subCategoryName + '\'' +
                '}';
    }
}

