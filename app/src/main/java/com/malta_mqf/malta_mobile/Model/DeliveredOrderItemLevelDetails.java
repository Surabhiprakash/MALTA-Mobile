package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class DeliveredOrderItemLevelDetails {
    @SerializedName("itemtotal")
    String itemtotal;

    @SerializedName("rebateamount")
    String rebateamount;

    @SerializedName("vatamount")
    String vatamount;

    @SerializedName("po_reference")
    String poReference;

    @SerializedName("poRefName")
    String poRefName;

    @SerializedName("pocreated_datetime")
    String pocreatedDatetime;

    @SerializedName("ordered_qty")
    String orderedQty;

    @SerializedName("approved_qty")
    String approvedQty;

    @SerializedName("netamount")
    String netamount;

    @SerializedName("item_id")
    String itemId;

    @SerializedName("agency_id")
    String agencyId;

    @SerializedName("sellingPrice")
    String sellingPrice;

    @SerializedName("rebate")
    String rebate;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("invoiceno")
    String invoiceno;

    @SerializedName("item_code")
    String itemCode;

    @SerializedName("delivered_qty")
    String deliveredQty;

    @SerializedName("vat")
    String vat;

    public String getItemtotal() {
        return itemtotal;
    }

    public void setItemtotal(String itemtotal) {
        this.itemtotal = itemtotal;
    }

    public String getRebateamount() {
        return rebateamount;
    }

    public void setRebateamount(String rebateamount) {
        this.rebateamount = rebateamount;
    }

    public String getVatamount() {
        return vatamount;
    }

    public void setVatamount(String vatamount) {
        this.vatamount = vatamount;
    }

    public String getPoReference() {
        return poReference;
    }

    public void setPoReference(String poReference) {
        this.poReference = poReference;
    }

    public String getPoRefName() {
        return poRefName;
    }

    public void setPoRefName(String poRefName) {
        this.poRefName = poRefName;
    }

    public String getPocreatedDatetime() {
        return pocreatedDatetime;
    }

    public void setPocreatedDatetime(String pocreatedDatetime) {
        this.pocreatedDatetime = pocreatedDatetime;
    }

    public String getOrderedQty() {
        return orderedQty;
    }

    public void setOrderedQty(String orderedQty) {
        this.orderedQty = orderedQty;
    }

    public String getApprovedQty() {
        return approvedQty;
    }

    public void setApprovedQty(String approvedQty) {
        this.approvedQty = approvedQty;
    }

    public String getNetamount() {
        return netamount;
    }

    public void setNetamount(String netamount) {
        this.netamount = netamount;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDeliveredQty() {
        return deliveredQty;
    }

    public void setDeliveredQty(String deliveredQty) {
        this.deliveredQty = deliveredQty;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    @Override
    public String toString() {
        return "DeliveredOrderItemLevelDetails{" +
                "itemtotal='" + itemtotal + '\'' +
                ", rebateamount='" + rebateamount + '\'' +
                ", vatamount='" + vatamount + '\'' +
                ", poReference='" + poReference + '\'' +
                ", poRefName='" + poRefName + '\'' +
                ", pocreatedDatetime=" + pocreatedDatetime +
                ", orderedQty='" + orderedQty + '\'' +
                ", approvedQty='" + approvedQty + '\'' +
                ", netamount='" + netamount + '\'' +
                ", itemId='" + itemId + '\'' +
                ", agencyId='" + agencyId + '\'' +
                ", sellingPrice='" + sellingPrice + '\'' +
                ", rebate='" + rebate + '\'' +
                ", orderid='" + orderid + '\'' +
                ", invoiceno='" + invoiceno + '\'' +
                ", itemCode=" + itemCode +
                ", deliveredQty='" + deliveredQty + '\'' +
                ", vat='" + vat + '\'' +
                '}';
    }
}
