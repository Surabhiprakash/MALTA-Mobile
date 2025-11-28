package com.malta_mqf.malta_mobile.Model;

public class ShowLoadinInvoiceBean {
    String productname, productcode, availableQty, purchasePrice, disc, itemNet, itemVat, itemGross, itemVatPercent;

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(String availableQty) {
        this.availableQty = availableQty;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getItemNet() {
        return itemNet;
    }

    public void setItemNet(String itemNet) {
        this.itemNet = itemNet;
    }

    public String getItemVat() {
        return itemVat;
    }

    public void setItemVat(String itemVat) {
        this.itemVat = itemVat;
    }

    public String getItemGross() {
        return itemGross;
    }

    public void setItemGross(String itemGross) {
        this.itemGross = itemGross;
    }

    public String getItemVatPercent() {
        return itemVatPercent;
    }

    public void setItemVatPercent(String itemVatPercent) {
        this.itemVatPercent = itemVatPercent;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    @Override
    public String toString() {
        return "ShowLoadinInvoiceBean{" +
                "productname='" + productname + '\'' +
                ", productcode='" + productcode + '\'' +
                ", availableQty='" + availableQty + '\'' +
                ", purchasePrice='" + purchasePrice + '\'' +
                ", disc='" + disc + '\'' +
                ", itemNet='" + itemNet + '\'' +
                ", itemVat='" + itemVat + '\'' +
                ", itemGross='" + itemGross + '\'' +
                ", itemVatPercent='" + itemVatPercent + '\'' +
                '}';
    }
}
