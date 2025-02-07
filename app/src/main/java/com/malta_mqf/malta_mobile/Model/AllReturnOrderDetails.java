package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class AllReturnOrderDetails {
    @SerializedName("customerCode")
    String customerCode;

    @SerializedName("rebate")
    String rebate;

    @SerializedName("returned_date")
    String returnedDate;

    @SerializedName("item_code")
    String itemCode;

    @SerializedName("outletCode")
    String outletCode;

    @SerializedName("sellingPrice")
    String sellingPrice;

    @SerializedName("creditnotetotalamt")
    String creditnotetotalamt;

    @SerializedName("returntotalnetamount")
    String returntotalnetamount;

    @SerializedName("returntotalvatamount")
    String returntotalvatamount;

    @SerializedName("creditwithoutrebate")
    String creditwithoutrebate;

    @SerializedName("returned_datetime")
    String returnedDatetime;

    @SerializedName("refno")
    String refno;

    @SerializedName("comments")
    String comments;

    @SerializedName("vanName")
    String vanName;

    @SerializedName("customerName")
    String customerName;

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

    @SerializedName("reason")
    String reason;

    @SerializedName("barcode")
    String Barcode;

    @SerializedName("pluCode")
    String Plucode;

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getPlucode() {
        return Plucode;
    }

    public void setPlucode(String plucode) {
        Plucode = plucode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
    public String getCustomerCode() {
        return customerCode;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }
    public String getRebate() {
        return rebate;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }
    public String getReturnedDate() {
        return returnedDate;
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

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setCreditnotetotalamt(String creditnotetotalamt) {
        this.creditnotetotalamt = creditnotetotalamt;
    }
    public String getCreditnotetotalamt() {
        return creditnotetotalamt;
    }

    public void setReturntotalnetamount(String returntotalnetamount) {
        this.returntotalnetamount = returntotalnetamount;
    }
    public String getReturntotalnetamount() {
        return returntotalnetamount;
    }

    public void setReturntotalvatamount(String returntotalvatamount) {
        this.returntotalvatamount = returntotalvatamount;
    }
    public String getReturntotalvatamount() {
        return returntotalvatamount;
    }

    public void setCreditwithoutrebate(String creditwithoutrebate) {
        this.creditwithoutrebate = creditwithoutrebate;
    }
    public String getCreditwithoutrebate() {
        return creditwithoutrebate;
    }

    public void setReturnedDatetime(String returnedDatetime) {
        this.returnedDatetime = returnedDatetime;
    }
    public String getReturnedDatetime() {
        return returnedDatetime;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }
    public String getRefno() {
        return refno;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getComments() {
        return comments;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }
    public String getVanName() {
        return vanName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setReturnedQty(String returnedQty) {
        this.returnedQty = returnedQty;
    }
    public String getReturnedQty() {
        return returnedQty;
    }

    public void setReturnitemtotalprice(String returnitemtotalprice) {
        this.returnitemtotalprice = returnitemtotalprice;
    }
    public String getReturnitemtotalprice() {
        return returnitemtotalprice;
    }

    public void setReturnnetamount(String returnnetamount) {
        this.returnnetamount = returnnetamount;
    }
    public String getReturnnetamount() {
        return returnnetamount;
    }

    public void setReturnvatamount(String returnvatamount) {
        this.returnvatamount = returnvatamount;
    }
    public String getReturnvatamount() {
        return returnvatamount;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }
    public String getVat() {
        return vat;
    }

    public void setCreditNoteid(String creditNoteid) {
        this.creditNoteid = creditNoteid;
    }
    public String getCreditNoteid() {
        return creditNoteid;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getReason() {
        return reason;
    }
}
