package com.malta_mqf.malta_mobile.Model;

public class OrderConfrimBean {
    String ProductName,ProductsQty;

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductsQty() {
        return ProductsQty;
    }

    public void setProductsQty(String productsQty) {
        ProductsQty = productsQty;
    }

    @Override
    public String toString() {
        return "OrderConfrimBean{" +
                "ProductName='" + ProductName + '\'' +
                ", ProductsQty='" + ProductsQty + '\'' +
                '}';
    }
}
