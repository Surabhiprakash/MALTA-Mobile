package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class ReturnOrderItemLevelDetails {
    @SerializedName("reason")
    String reason;

    @SerializedName("itemName")
    String itemName;
    @SerializedName("item_id")
    String item_id;
    @SerializedName("returned_datetime")
    String returnedDatetime;

    @SerializedName("returnitemtotalprice")
    String returnitmetotalprice;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("invoiceno")
    String invoiceno;

    @SerializedName("item_code")
    String itemCode;

    @SerializedName("returned_qty")
    String returnedQty;

    @SerializedName("returnnetamount")
    String returnnetamount;

    @SerializedName("returnvatamount")
    String returnvatamount;

    @SerializedName("sellingPrice")
    String sellingPrice;

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

    public String getReturnitmetotalprice() {
        return returnitmetotalprice;
    }

    public void setReturnitmetotalprice(String returnitmetotalprice) {
        this.returnitmetotalprice = returnitmetotalprice;
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

    public String getReturnedQty() {
        return returnedQty;
    }

    public void setReturnedQty(String returnedQty) {
        this.returnedQty = returnedQty;
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

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    @Override
    public String toString() {
        return "ReturnOrderItemLevelDetails{" +
                "reason='" + reason + '\'' +
                ", itemName='" + itemName + '\'' +
                ", item_id='" + item_id + '\'' +
                ", returnedDatetime='" + returnedDatetime + '\'' +
                ", returnitmetotalprice='" + returnitmetotalprice + '\'' +
                ", orderid='" + orderid + '\'' +
                ", invoiceno='" + invoiceno + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", returnedQty='" + returnedQty + '\'' +
                ", returnnetamount='" + returnnetamount + '\'' +
                ", returnvatamount='" + returnvatamount + '\'' +
                ", sellingPrice='" + sellingPrice + '\'' +
                '}';
    }
}
