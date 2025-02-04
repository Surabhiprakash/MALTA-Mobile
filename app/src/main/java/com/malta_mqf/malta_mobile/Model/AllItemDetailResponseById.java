package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class AllItemDetailResponseById {
    @SerializedName("status")
    String status;

    @SerializedName("id")
    String id;

    @SerializedName("itemCode")
    String itemCode;

    @SerializedName("customerName")
    String customerName;

    @SerializedName("customerCode")
    String customerCode;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("uom_id")
    String uomId;

    @SerializedName("itemCategory_id")
    String itemCategoryId;

    @SerializedName("agency_id")
    String agencyId;

    @SerializedName("purchasePrice")
    String purchasePrice;

    @SerializedName("productDescription")
    String productDescription;

    @SerializedName("shelflife")
    String shelflife;

    @SerializedName("uom")
    String uom;

    @SerializedName("itemCategory")
    String itemCategory;

    @SerializedName("subCategory_name")
    String subCategory;
    @SerializedName("agencyName")
    String agencyName;

    @SerializedName("agencyCode")
    String agencyCode;

    @SerializedName("sellingPrice")

    String sellingPrice;
    @SerializedName("barcode")
    String barcode;
    @SerializedName("lead_time")
    String lead_time;

    @SerializedName("itemcustomerplucode")
    String plu_code;


    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
    public String getCustomerCode() {
        return customerCode;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() {
        return itemName;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }
    public String getUomId() {
        return uomId;
    }

    public void setItemCategoryId(String itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }
    public String getItemCategoryId() {
        return itemCategoryId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }
    public String getAgencyId() {
        return agencyId;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    public String getProductDescription() {
        return productDescription;
    }

    public void setShelflife(String shelflife) {
        this.shelflife = shelflife;
    }
    public String getShelflife() {
        return shelflife;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
    public String getUom() {
        return uom;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }
    public String getItemCategory() {
        return itemCategory;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }
    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }
    public String getAgencyCode() {
        return agencyCode;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public String getSellingPrice() {
        return sellingPrice;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLead_time() {
        return lead_time;
    }

    public void setLead_time(String lead_time) {
        this.lead_time = lead_time;
    }

    public String getPlu_code() {
        return plu_code;
    }

    public void setPlu_code(String plu_code) {
        this.plu_code = plu_code;
    }

    @Override
    public String toString() {
        return "AllItemDetailResponseById{" +
                "status='" + status + '\'' +
                ", id='" + id + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerCode='" + customerCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", uomId='" + uomId + '\'' +
                ", itemCategoryId='" + itemCategoryId + '\'' +
                ", agencyId='" + agencyId + '\'' +
                ", purchasePrice='" + purchasePrice + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", shelflife='" + shelflife + '\'' +
                ", uom='" + uom + '\'' +
                ", itemCategory='" + itemCategory + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                ", sellingPrice='" + sellingPrice + '\'' +
                ", barcode='" + barcode + '\'' +
                ", lead_time='" + lead_time + '\'' +
                ", plu_code='" + plu_code + '\'' +
                '}';
    }
}
