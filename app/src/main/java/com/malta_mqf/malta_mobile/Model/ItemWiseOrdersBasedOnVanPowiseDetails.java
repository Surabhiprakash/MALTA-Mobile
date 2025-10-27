package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class ItemWiseOrdersBasedOnVanPowiseDetails {
    @SerializedName("itemCode")
    String itemCode;

    @SerializedName("vanName")
    String vanName;

    @SerializedName("categoryName")
    String categoryName;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("van_id")
    String vanId;

    @SerializedName("subCategory_name")
    String subCategoryName;

    @SerializedName("po_reference")
    String poReference;

    @SerializedName("item_id")
    String itemId;

    @SerializedName("total_orderedqty")
    String totalOrderedqty;

    @SerializedName("total_approvedqty")
    String totalApprovedqty;

    @SerializedName("agency_code")
    String agencyCode;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getVanName() {
        return vanName;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getVanId() {
        return vanId;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
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

    public String getTotalOrderedqty() {
        return totalOrderedqty;
    }

    public void setTotalOrderedqty(String totalOrderedqty) {
        this.totalOrderedqty = totalOrderedqty;
    }

    public String getTotalApprovedqty() {
        return totalApprovedqty;
    }

    public void setTotalApprovedqty(String totalApprovedqty) {
        this.totalApprovedqty = totalApprovedqty;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    @Override
    public String toString() {
        return "ItemWiseOrdersBasedOnVanPowiseDetails{" +
                "itemCode='" + itemCode + '\'' +
                ", vanName='" + vanName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", vanId='" + vanId + '\'' +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", poReference='" + poReference + '\'' +
                ", itemId='" + itemId + '\'' +
                ", totalOrderedqty='" + totalOrderedqty + '\'' +
                ", totalApprovedqty='" + totalApprovedqty + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                '}';
    }
}
