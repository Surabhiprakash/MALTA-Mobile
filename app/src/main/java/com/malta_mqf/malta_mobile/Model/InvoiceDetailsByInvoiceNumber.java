package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class InvoiceDetailsByInvoiceNumber {
    @SerializedName("invoicetotal")
    String invoicetotal;

    @SerializedName("customerName")
    String customerName;

    @SerializedName("outletName")
    String outletName;

    @SerializedName("rebate")
    String rebate;

    @SerializedName("itemCode")
    String itemCode;

    @SerializedName("outletCode")
    String outletCode;

    @SerializedName("customerCode")
    String customerCode;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("agencyName")
    String agencyName;

    @SerializedName("sellingPrice")
    String sellingPrice;

    @SerializedName("itemtotal")
    String itemtotal;

    @SerializedName("netamount")
    String netamount;

    @SerializedName("rebateamount")
    String rebateamount;

    @SerializedName("vatamount")
    String vatamount;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("invoiceno")
    String invoiceno;

    @SerializedName("delivered_qty")
    String deliveredQty;

    @SerializedName("vat")
    String vat;

    @SerializedName("delivered_datetime")
    String deliveredDatetime;


    public void setInvoicetotal(String invoicetotal) {
        this.invoicetotal = invoicetotal;
    }
    public String getInvoicetotal() {
        return invoicetotal;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }
    public String getOutletName() {
        return outletName;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }
    public String getRebate() {
        return rebate;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }
    public String getOutletCode() {
        return outletCode;
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

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }
    public String getAgencyName() {
        return agencyName;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setItemtotal(String itemtotal) {
        this.itemtotal = itemtotal;
    }
    public String getItemtotal() {
        return itemtotal;
    }

    public void setNetamount(String netamount) {
        this.netamount = netamount;
    }
    public String getNetamount() {
        return netamount;
    }

    public void setRebateamount(String rebateamount) {
        this.rebateamount = rebateamount;
    }
    public String getRebateamount() {
        return rebateamount;
    }

    public void setVatamount(String vatamount) {
        this.vatamount = vatamount;
    }
    public String getVatamount() {
        return vatamount;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
    public String getOrderid() {
        return orderid;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }
    public String getInvoiceno() {
        return invoiceno;
    }

    public void setDeliveredQty(String deliveredQty) {
        this.deliveredQty = deliveredQty;
    }
    public String getDeliveredQty() {
        return deliveredQty;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }
    public String getVat() {
        return vat;
    }

    public void setDeliveredDatetime(String deliveredDatetime) {
        this.deliveredDatetime = deliveredDatetime;
    }
    public String getDeliveredDatetime() {
        return deliveredDatetime;
    }
}
