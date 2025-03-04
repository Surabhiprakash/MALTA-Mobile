package com.malta_mqf.malta_mobile.Model;

public class VanStockUnloadModel {
    private int id;
    private String vanId,from_vanid,agencyCode,agencyName, productName, productId, itemCategory, itemSubcategory, itemCode, unloadDate, unloadReason, status;
    private int availableQty;
    private String unloadQty;

    public VanStockUnloadModel() {
    }

    public VanStockUnloadModel(String productName,String unloadQty) {
        this.productName = productName;
        this.unloadQty = unloadQty;
    }

    public VanStockUnloadModel(String vanId,String from_id, String productName, String productId, String itemCategory, String itemSubcategory, String itemCode, String unloadDate, String unloadReason, String status, int availableQty) {
        this.vanId = vanId;
        this.from_vanid=from_id;
        this.productName = productName;
        this.productId = productId;
        this.itemCategory = itemCategory;
        this.itemSubcategory = itemSubcategory;
        this.itemCode = itemCode;
        this.unloadDate = unloadDate;
        this.unloadReason = unloadReason;
        this.status = status;
        this.availableQty = availableQty;
    }

    public String getFrom_vanid() {
        return from_vanid;
    }

    public void setFrom_vanid(String from_vanid) {
        this.from_vanid = from_vanid;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVanId() {
        return vanId;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }

    public String getProductName() {
        return productName;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUnloadQty() {
        return unloadQty;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemSubcategory() {
        return itemSubcategory;
    }

    public void setItemSubcategory(String itemSubcategory) {
        this.itemSubcategory = itemSubcategory;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public String getUnloadDate() {
        return unloadDate;
    }

    public void setUnloadQty(String unloadQty) {
        this.unloadQty = unloadQty;
    }

    public void setUnloadDate(String unloadDate) {
        this.unloadDate = unloadDate;
    }

    public String getUnloadReason() {
        return unloadReason;
    }

    public void setUnloadReason(String unloadReason) {
        this.unloadReason = unloadReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VanStockUnloadModel{" +
                "id=" + id +
                ", vanId='" + vanId + '\'' +
                ", from_vanid='" + from_vanid + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", productName='" + productName + '\'' +
                ", productId='" + productId + '\'' +
                ", itemCategory='" + itemCategory + '\'' +
                ", itemSubcategory='" + itemSubcategory + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", unloadDate='" + unloadDate + '\'' +
                ", unloadReason='" + unloadReason + '\'' +
                ", status='" + status + '\'' +
                ", availableQty=" + availableQty +
                ", unloadQty='" + unloadQty + '\'' +
                '}';
    }
}


