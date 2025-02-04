package com.malta_mqf.malta_mobile.Model;

import java.util.List;

public class ReturnWithoutInvoiceBean {

    public String itemName;
    public String return_qty;
    public String reason;
    public List<String> reasonList;

    public ReturnWithoutInvoiceBean() {
    }

    public ReturnWithoutInvoiceBean(String itemName, String return_qty, String reason, List<String> reasonList) {
        this.itemName = itemName;
        this.return_qty = return_qty;
        this.reason = reason;
        this.reasonList = reasonList;
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getReturn_qty() {
        return return_qty;
    }

    public void setReturn_qty(String return_qty) {
        this.return_qty = return_qty;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<String> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<String> reasonList) {
        this.reasonList = reasonList;
    }

    @Override
    public String toString() {
        return "ReturnWithoutInvoiceBean{" +
                "itemName='" + itemName + '\'' +
                ", return_qty='" + return_qty + '\'' +
                ", reason='" + reason + '\'' +
                ", reasonList=" + reasonList +
                '}';
    }
}
