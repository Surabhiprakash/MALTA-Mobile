package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class TotalPerItemsByVanIdResponse {
    @SerializedName("van_id")
    String vanId;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("item_id")
    String itemId;

    @SerializedName("total_orderedqty")
    String totalOrderedqty;

    @SerializedName("total_approvedqty")
    String totalApprovedqty;

    @SerializedName("agency_code")
    String agency_code;

    @SerializedName("itemCode")
    String itemCode;

    @SerializedName("categoryName")
    String itemcategory;

    @SerializedName("subCategory_name")
    String itemsubcategory;


    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }
    public String getVanId() {
        return vanId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() {
        return itemName;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return itemId;
    }

    public void setTotalOrderedqty(String totalOrderedqty) {
        this.totalOrderedqty = totalOrderedqty;
    }
    public String getTotalOrderedqty() {
        return totalOrderedqty;
    }

    public void setTotalApprovedqty(String totalApprovedqty) {
        this.totalApprovedqty = totalApprovedqty;
    }
    public String getTotalApprovedqty() {
        return totalApprovedqty;
    }

    public String getAgency_code() {
        return agency_code;
    }

    public void setAgency_code(String agency_code) {
        this.agency_code = agency_code;
    }

    public String getItemcategory() {
        return itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        this.itemcategory = itemcategory;
    }

    public String getItemsubcategory() {
        return itemsubcategory;
    }

    public void setItemsubcategory(String itemsubcategory) {
        this.itemsubcategory = itemsubcategory;
    }

    @Override
    public String toString() {
        return "TotalPerItemsByVanIdResponse{" +
                "vanId='" + vanId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemId='" + itemId + '\'' +
                ", totalOrderedqty='" + totalOrderedqty + '\'' +
                ", totalApprovedqty='" + totalApprovedqty + '\'' +
                ", agency_code='" + agency_code + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", itemcategory='" + itemcategory + '\'' +
                ", itemsubcategory='" + itemsubcategory + '\'' +
                '}';
    }
}
