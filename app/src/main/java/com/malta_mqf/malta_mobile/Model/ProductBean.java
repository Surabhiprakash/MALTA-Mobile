package com.malta_mqf.malta_mobile.Model;

public class ProductBean {
    private String productName;
    private String productId;
    private String quantity;
    private String approvedqty;
    private String deliveryQty;
    private String purchase_price;
    private String itemcode;
    public ProductBean() {
    }

    public ProductBean(String productId, String quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public ProductBean(String productId,String ProductName, String quantity,String approvedqty) {
        this.productId = productId;
        this.productName = ProductName;
        this.quantity = quantity;
        this.approvedqty = approvedqty;
    }

    public ProductBean(String productId,String itemcode,String pruchaseprice,String ProductName, String quantity,String approvedqty,String deliveryQty) {
        this.productId = productId;
        this.purchase_price=pruchaseprice;
        this.productName = ProductName;
        this.quantity = quantity;
        this.approvedqty = approvedqty;
        this.itemcode=itemcode;
        this.deliveryQty = deliveryQty;
    }

    public ProductBean(String productId,String ProductName, String availableQty) {
        this.productId = productId;
        this.productName = ProductName;
        this.deliveryQty = availableQty;
    }

    public String getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(String purchase_price) {
        this.purchase_price = purchase_price;
    }

    public String getProductId() {
        return productId;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getApprovedqty() {
        return approvedqty;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setApprovedqty(String approvedqty) {
        this.approvedqty = approvedqty;
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

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "productName='" + productName + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity='" + quantity + '\'' +
                ", approvedqty='" + approvedqty + '\'' +
                ", deliveryQty='" + deliveryQty + '\'' +
                ", purchase_price='" + purchase_price + '\'' +
                ", itemcode='" + itemcode + '\'' +
                '}';
    }
}

