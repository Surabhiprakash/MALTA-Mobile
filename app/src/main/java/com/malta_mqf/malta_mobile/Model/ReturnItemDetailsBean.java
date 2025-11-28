package com.malta_mqf.malta_mobile.Model;

import java.util.List;

public class ReturnItemDetailsBean {

    private String itemName;
    private String delivered_qty;
    private String return_qty;
    private List<String> reasonList;
    private String reason;

    public ReturnItemDetailsBean() {

    }

    public ReturnItemDetailsBean(String itemName, String delivered_qty, String return_qty, List<String> reasonList) {
        this.itemName = itemName;
        this.delivered_qty = delivered_qty;
        this.return_qty = return_qty;
        this.reasonList = reasonList;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDelivered_qty() {
        return delivered_qty;
    }

    public void setDelivered_qty(String delivered_qty) {
        this.delivered_qty = delivered_qty;
    }

    public String getReturn_qty() {
        return return_qty;
    }

    public void setReturn_qty(String return_qty) {
        this.return_qty = return_qty;
    }

    public List<String> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<String> reasonList) {
        this.reasonList = reasonList;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "ReturnItemDetailsBean{" +
                "itemName='" + itemName + '\'' +
                ", delivered_qty='" + delivered_qty + '\'' +
                ", return_qty='" + return_qty + '\'' +
                ", reasonList=" + reasonList +
                ", reason='" + reason + '\'' +
                '}';
    }
}
