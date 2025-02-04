package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class VanLoadDataForVanDetails {
    @SerializedName("itemCode")
    String itemCode;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("approved_qty")
    String approvedQty;

    @SerializedName("ordered_qty")
    String orderedQty;

    @SerializedName("po_reference")
    String poReference;

    @SerializedName("loaded_qty")
    String loadedQty;

    @SerializedName("loaded_date")
    String loadedDate;

    @SerializedName("expectedDelivery")
    String expectedDelivery;

    @SerializedName("item_id")
    String itemId;

    @SerializedName("loadStatus")
    String loadStatus;



    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setApprovedQty(String approvedQty) {
        this.approvedQty = approvedQty;
    }
    public String getApprovedQty() {
        return approvedQty;
    }

    public void setOrderedQty(String orderedQty) {
        this.orderedQty = orderedQty;
    }
    public String getOrderedQty() {
        return orderedQty;
    }

    public void setPoReference(String poReference) {
        this.poReference = poReference;
    }
    public String getPoReference() {
        return poReference;
    }

    public void setLoadedQty(String loadedQty) {
        this.loadedQty = loadedQty;
    }
    public String getLoadedQty() {
        return loadedQty;
    }

    public void setLoadedDate(String loadedDate) {
        this.loadedDate = loadedDate;
    }
    public String getLoadedDate() {
        return loadedDate;
    }

    public void setExpectedDelivery(String expectedDelivery) {
        this.expectedDelivery = expectedDelivery;
    }
    public String getExpectedDelivery() {
        return expectedDelivery;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(String loadStatus) {
        this.loadStatus = loadStatus;
    }

    @Override
    public String toString() {
        return "VanLoadDataForVanDetails{" +
                "itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", approvedQty='" + approvedQty + '\'' +
                ", orderedQty='" + orderedQty + '\'' +
                ", poReference='" + poReference + '\'' +
                ", loadedQty='" + loadedQty + '\'' +
                ", loadedDate='" + loadedDate + '\'' +
                ", expectedDelivery='" + expectedDelivery + '\'' +
                ", itemId='" + itemId + '\'' +
                ", loadStatus='" + loadStatus + '\'' +
                '}';
    }
}
