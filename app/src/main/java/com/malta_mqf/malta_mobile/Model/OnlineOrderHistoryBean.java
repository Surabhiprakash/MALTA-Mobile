package com.malta_mqf.malta_mobile.Model;

import java.util.Objects;

public class OnlineOrderHistoryBean {

    String order_no;
    String agencyName;
    String ItemName;
    int qty;
    String approved_qty;

    public OnlineOrderHistoryBean(String orderID, String agencyName, String itemName, String qty, String approvedQty) {

        this.order_no = orderID;
        this.agencyName = agencyName;
        this.ItemName = itemName;
        this.qty = Integer.parseInt(qty);
        this.approved_qty = approvedQty;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getApproved_qty() {
        return approved_qty;
    }

    public void setApproved_qty(String approved_qty) {
        this.approved_qty = approved_qty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnlineOrderHistoryBean that = (OnlineOrderHistoryBean) o;
        return qty == that.qty && Objects.equals(order_no, that.order_no) && Objects.equals(agencyName, that.agencyName) && Objects.equals(ItemName, that.ItemName) && Objects.equals(approved_qty, that.approved_qty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order_no, agencyName, ItemName, qty, approved_qty);
    }

    @Override
    public String toString() {
        return "OnlineOrderHistoryBean{" +
                "order_no='" + order_no + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", qty=" + qty +
                ", approved_qty='" + approved_qty + '\'' +
                '}';
    }
}
