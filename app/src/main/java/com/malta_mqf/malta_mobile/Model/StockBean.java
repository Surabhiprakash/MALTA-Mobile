package com.malta_mqf.malta_mobile.Model;

import java.util.Objects;

public class StockBean {
    String productName;
    String productID;
    String Qty;
    String delQty;

    public StockBean() {

    }

    public StockBean(String productName, String productID, String qty) {
        this.productName = productName;
        this.productID = productID;
        Qty = qty;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductID() {
        return productID;
    }

    public String getQty() {
        return Qty;
    }

    public String getDelQty() {
        return delQty;
    }

    public void setDelQty(String delQty) {
        this.delQty = delQty;
    }

    @Override
    public String toString() {
        return "StockBean{" +
                "productName='" + productName + '\'' +
                ", productID='" + productID + '\'' +
                ", Qty='" + Qty + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockBean stockBean = (StockBean) o;
        return Objects.equals(productName, stockBean.productName) && Objects.equals(productID, stockBean.productID) && Objects.equals(Qty, stockBean.Qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, productID, Qty);
    }
}
