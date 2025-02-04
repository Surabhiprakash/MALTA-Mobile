package com.malta_mqf.malta_mobile.Model;

public class OnlineProductBean {
    String productName;
    String productId;
    String itemCode;
    String agencydid;
    String ReqQty;
    public OnlineProductBean() {

    }
    public OnlineProductBean(String productName, String productId, String itemCode, String agencydid) {
        this.productName = productName;
        this.productId = productId;
        this.itemCode = itemCode;
        this.agencydid = agencydid;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductId() {
        return productId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getAgencydid() {
        return agencydid;
    }

    public void setAgencydid(String agencydid) {
        this.agencydid = agencydid;
    }

    public String getReqQty() {
        return ReqQty;
    }

    public void setReqQty(String reqQty) {
        ReqQty = reqQty;
    }

    @Override
    public String toString() {
        return "OnlineProductBean{" +
                "productName='" + productName + '\'' +
                ", productId='" + productId + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", agencydid='" + agencydid + '\'' +
                ", ReqQty='" + ReqQty + '\'' +
                '}';
    }
}
