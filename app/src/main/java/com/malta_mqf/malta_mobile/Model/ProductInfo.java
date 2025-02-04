package com.malta_mqf.malta_mobile.Model;

import java.util.Objects;

public class ProductInfo {
    private String orderID;
    private String productID;
    private String itemCode;
    private String quantity;

    private String ApprovedQty;
   // private String productName;
    private String agencyCode;
    private String poREFRENCE;

    private String poRefName;
    private String poRefdate;
    private String agencyID;
    private String itemcategory;
    private String itemsubcategory;
    public ProductInfo() {

    }
    public ProductInfo(String productID,String agencyCode, String itemCode, String quantity) {
        this.productID = productID;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.agencyCode = agencyCode;
    }
   /* public ProductInfo(String productID, String itemCode, String quantity,String ApprovedQty) {
        this.productID = productID;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.ApprovedQty = ApprovedQty;
    }*/

    public ProductInfo(String productID,String approvedQty) {
        this.productID = productID;
        this.ApprovedQty = approvedQty;
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

    public String getPoRefName() {
        return poRefName;
    }

    public void setPoRefName(String poRefName) {
        this.poRefName = poRefName;
    }

    public String getPoRefdate() {
        return poRefdate;
    }

    public void setPoRefdate(String poRefdate) {
        this.poRefdate = poRefdate;
    }

    public String getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(String agencyID) {
        this.agencyID = agencyID;
    }

    // Getters and setters
    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getApprovedQty() {
        return ApprovedQty;
    }

    public void setApprovedQty(String approvedQty) {
        ApprovedQty = approvedQty;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getPoREFRENCE() {
        return poREFRENCE;
    }

    public void setPoREFRENCE(String poREFRENCE) {
        this.poREFRENCE = poREFRENCE;
    }

    @Override
    public String toString() {
        return "ProductInfo{" +
                "orderID='" + orderID + '\'' +
                ", productID='" + productID + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", quantity='" + quantity + '\'' +
                ", ApprovedQty='" + ApprovedQty + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                ", poREFRENCE='" + poREFRENCE + '\'' +
                ", poRefName='" + poRefName + '\'' +
                ", poRefdate='" + poRefdate + '\'' +
                ", agencyID='" + agencyID + '\'' +
                ", itemcategory='" + itemcategory + '\'' +
                ", itemsubcategory='" + itemsubcategory + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductInfo that = (ProductInfo) o;
        return Objects.equals(productID, that.productID) &&
                Objects.equals(poREFRENCE, that.poREFRENCE) &&
                Objects.equals(itemCode, that.itemCode); // Update this line to include all relevant fields
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID, poREFRENCE, itemCode); // Update this line to include all relevant fields
    }

}

