package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class DeliveredOrderLevelDetails {
    @SerializedName("customerCode")
    String customerCode;
    @SerializedName("customerName")
    String customerName;
    @SerializedName("totalvatamount")
    String totalvatanount;

    @SerializedName("commets")
    String commets;

    @SerializedName("totalnetamount")
    String totalnetamount;

    @SerializedName("invoicetotal")
    String invoicetotal;

    @SerializedName("invoicewithoutrebate")
    String invoicewithoutrebate;
    @SerializedName("refno")
    String refno;

    @SerializedName("outletCode")
    String outletCode;

    @SerializedName("outlet_id")
    String outletId;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("invoiceno")
    String invoiceno;

    @SerializedName("ordered_datetime")
    String orderedDatetime;

    @SerializedName("delivered_datetime")
    String deliveredDatetime;

    public String getInvoicewithoutrebate() {
        return invoicewithoutrebate;
    }

    public void setInvoicewithoutrebate(String invoicewithoutrebate) {
        this.invoicewithoutrebate = invoicewithoutrebate;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setTotalvatanount(String totalvatanount) {
        this.totalvatanount = totalvatanount;
    }
    public String getTotalvatanount() {
        return totalvatanount;
    }

    public void setCommets(String commets) {
        this.commets = commets;
    }
    public String getCommets() {
        return commets;
    }

    public void setTotalnetamount(String totalnetamount) {
        this.totalnetamount = totalnetamount;
    }
    public String getTotalnetamount() {
        return totalnetamount;
    }

    public void setInvoicetotal(String invoicetotal) {
        this.invoicetotal = invoicetotal;
    }
    public String getInvoicetotal() {
        return invoicetotal;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }
    public String getRefno() {
        return refno;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }
    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }
    public String getOutletId() {
        return outletId;
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

    public void setOrderedDatetime(String orderedDatetime) {
        this.orderedDatetime = orderedDatetime;
    }
    public String getOrderedDatetime() {
        return orderedDatetime;
    }

    public void setDeliveredDatetime(String deliveredDatetime) {
        this.deliveredDatetime = deliveredDatetime;
    }
    public String getDeliveredDatetime() {
        return deliveredDatetime;
    }

    @Override
    public String toString() {
        return "DeliveredOrderLevelDetails{" +
                "customerCode='" + customerCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalvatanount='" + totalvatanount + '\'' +
                ", commets='" + commets + '\'' +
                ", totalnetamount='" + totalnetamount + '\'' +
                ", invoicetotal='" + invoicetotal + '\'' +
                ", invoicewithoutrebate='" + invoicewithoutrebate + '\'' +
                ", refno='" + refno + '\'' +
                ", outletCode='" + outletCode + '\'' +
                ", outletId='" + outletId + '\'' +
                ", orderid='" + orderid + '\'' +
                ", invoiceno='" + invoiceno + '\'' +
                ", orderedDatetime='" + orderedDatetime + '\'' +
                ", deliveredDatetime='" + deliveredDatetime + '\'' +
                '}';
    }
}
