package com.malta_mqf.malta_mobile.Model;

public class Item {

    private String itemname;
    private String req_qty;
    private String approved_qty;

    public Item(String itemname, String req_qty, String approved_qty) {
        this.itemname = itemname;
        this.req_qty = req_qty;
        this.approved_qty = approved_qty;
    }

    public String getItemname() {
        return itemname;
    }

    public String getReq_qty() {
        return req_qty;
    }

    public String getApproved_qty() {
        return approved_qty;
    }
}
