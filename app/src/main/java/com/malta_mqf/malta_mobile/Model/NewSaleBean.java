package com.malta_mqf.malta_mobile.Model;

public class NewSaleBean {
    String orderID, productID, itemCode, quantity, ApprovedQty, productName, deliveryQty, sellingPrice, vanstock, uom, barcode, plucode;
    private String zeroReason;

    public NewSaleBean() {

    }

    public NewSaleBean(String productID, String itemCode, String barcode, String plucode, String approvedQty, String sellingPrice, String vanstcok, String uom) {
        this.productID = productID;
        this.itemCode = itemCode;
        this.barcode = barcode;
        this.ApprovedQty = approvedQty;
        this.sellingPrice = sellingPrice;
        this.vanstock = vanstcok;
        this.uom = uom;
        this.plucode = plucode;
    }

    public NewSaleBean(String productID, String productName, String itemCode, String barcode, String plucode, String approvedQty, String sellingPrice, String vanstock, String uom) {
        this.productID = productID;
        this.itemCode = itemCode;
        this.barcode = barcode;
        this.productName = productName;
        this.ApprovedQty = approvedQty;
        this.sellingPrice = sellingPrice;
        this.vanstock = vanstock;
        this.uom = uom;
        this.plucode = plucode;
    }

    public String getZeroReason() {
        return zeroReason;
    }

    public void setZeroReason(String zeroReason) {
        this.zeroReason = zeroReason;
    }

    public String getPlucode() {
        return plucode;
    }

    public void setPlucode(String plucode) {
        this.plucode = plucode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDeliveryQty() {
        return deliveryQty;
    }

    public void setDeliveryQty(String deliveryQty) {
        this.deliveryQty = deliveryQty;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

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

    public String getApprovedQty() {
        return ApprovedQty;
    }

    public void setApprovedQty(String approvedQty) {
        ApprovedQty = approvedQty;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getVanstock() {
        return vanstock;
    }

    public void setVanstock(String vanstock) {
        this.vanstock = vanstock;
    }

    @Override
    public String toString() {
        return "NewSaleBean{" +
                "orderID='" + orderID + '\'' +
                ", productID='" + productID + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", quantity='" + quantity + '\'' +
                ", ApprovedQty='" + ApprovedQty + '\'' +
                ", productName='" + productName + '\'' +
                ", deliveryQty='" + deliveryQty + '\'' +
                ", sellingPrice='" + sellingPrice + '\'' +
                ", vanstock='" + vanstock + '\'' +
                ", uom='" + uom + '\'' +
                ", barcode='" + barcode + '\'' +
                ", plucode='" + plucode + '\'' +
                ", zeroReason='" + zeroReason + '\'' +
                '}';
    }
}

