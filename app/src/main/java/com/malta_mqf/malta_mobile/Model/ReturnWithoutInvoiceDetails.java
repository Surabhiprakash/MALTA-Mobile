package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;


public class ReturnWithoutInvoiceDetails {
    @SerializedName("reason")
    String reason;
    @SerializedName("itemName")
    String itemName;
    @SerializedName("item_id")
    String item_id;
    @SerializedName("agencyCode")
    String agencyCode;
    @SerializedName("returned_datetime")
    String returnedDatetime;

    @SerializedName("refno")
    String refno;

    @SerializedName("comments")
    String comments;

    @SerializedName("customerName")
    String customerName;

    @SerializedName("returntotalnetamount")
    String returntotalnetamount;

    @SerializedName("returntotalvatamount")
    String returntotalvatamount;

    @SerializedName("sellingPrice")
    String sellingPrice;

    @SerializedName("customerCode")
    String customerCode;

    @SerializedName("van_id")
    String vanId;

    @SerializedName("outletCode")
    String outletCode;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("invoiceno")
    String invoiceno;

    @SerializedName("returned_date")
    String returnedDate;

    @SerializedName("item_code")
    String itemCode;

    @SerializedName("returned_qty")
    String returnedQty;

    @SerializedName("returnitemtotalprice")
    String returnitemtotalprice;

    @SerializedName("returnnetamount")
    String returnnetamount;

    @SerializedName("returnvatamount")
    String returnvatamount;

    @SerializedName("vat")
    String vat;

    @SerializedName("credit_noteid")
    String creditNoteid;

    @SerializedName("creditnotetotalamt")
    String creditnotetotalamount;

    @SerializedName("creditwithoutrebate")
    String creditwthoutrebate;

    @SerializedName("returncount")
    String returncount;

    @SerializedName("outlet_id")
    String outletId;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReturnedDatetime() {
        return returnedDatetime;
    }

    public void setReturnedDatetime(String returnedDatetime) {
        this.returnedDatetime = returnedDatetime;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getReturntotalnetamount() {
        return returntotalnetamount;
    }

    public void setReturntotalnetamount(String returntotalnetamount) {
        this.returntotalnetamount = returntotalnetamount;
    }

    public String getReturntotalvatamount() {
        return returntotalvatamount;
    }

    public void setReturntotalvatamount(String returntotalvatamount) {
        this.returntotalvatamount = returntotalvatamount;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getVanId() {
        return vanId;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
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

    public String getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getReturnedQty() {
        return returnedQty;
    }

    public void setReturnedQty(String returnedQty) {
        this.returnedQty = returnedQty;
    }

    public String getReturnitemtotalprice() {
        return returnitemtotalprice;
    }

    public void setReturnitemtotalprice(String returnitemtotalprice) {
        this.returnitemtotalprice = returnitemtotalprice;
    }

    public String getReturnnetamount() {
        return returnnetamount;
    }

    public void setReturnnetamount(String returnnetamount) {
        this.returnnetamount = returnnetamount;
    }

    public String getReturnvatamount() {
        return returnvatamount;
    }

    public void setReturnvatamount(String returnvatamount) {
        this.returnvatamount = returnvatamount;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getCreditNoteid() {
        return creditNoteid;
    }

    public void setCreditNoteid(String creditNoteid) {
        this.creditNoteid = creditNoteid;
    }

    public String getCreditnotetotalamount() {
        return creditnotetotalamount;
    }

    public void setCreditnotetotalamount(String creditnotetotalamount) {
        this.creditnotetotalamount = creditnotetotalamount;
    }

    public String getCreditwthoutrebate() {
        return creditwthoutrebate;
    }

    public void setCreditwthoutrebate(String creditwthoutrebate) {
        this.creditwthoutrebate = creditwthoutrebate;
    }

    public String getReturncount() {
        return returncount;
    }

    public void setReturncount(String returncount) {
        this.returncount = returncount;
    }

    @Override
    public String toString() {
        return "ReturnWithoutInvoiceDetails{" +
                "reason='" + reason + '\'' +
                ", itemName='" + itemName + '\'' +
                ", item_id='" + item_id + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                ", returnedDatetime='" + returnedDatetime + '\'' +
                ", refno='" + refno + '\'' +
                ", comments='" + comments + '\'' +
                ", customerName='" + customerName + '\'' +
                ", returntotalnetamount='" + returntotalnetamount + '\'' +
                ", returntotalvatamount='" + returntotalvatamount + '\'' +
                ", sellingPrice='" + sellingPrice + '\'' +
                ", customerCode='" + customerCode + '\'' +
                ", vanId='" + vanId + '\'' +
                ", outletCode='" + outletCode + '\'' +
                ", orderid='" + orderid + '\'' +
                ", invoiceno='" + invoiceno + '\'' +
                ", returnedDate='" + returnedDate + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", returnedQty='" + returnedQty + '\'' +
                ", returnitemtotalprice='" + returnitemtotalprice + '\'' +
                ", returnnetamount='" + returnnetamount + '\'' +
                ", returnvatamount='" + returnvatamount + '\'' +
                ", vat='" + vat + '\'' +
                ", creditNoteid='" + creditNoteid + '\'' +
                ", creditnotetotalamount='" + creditnotetotalamount + '\'' +
                ", creditwthoutrebate='" + creditwthoutrebate + '\'' +
                ", returncount='" + returncount + '\'' +
                ", outletId='" + outletId + '\'' +
                '}';
    }
}
