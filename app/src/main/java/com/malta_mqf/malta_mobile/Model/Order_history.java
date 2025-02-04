package com.malta_mqf.malta_mobile.Model;

import java.util.Objects;

public class Order_history {

    private String sno;
    private String order_no;
    private String date;


    private String agencyName;
    private String ItemName;
    private String  qty;
    private String approved_qty;
    private String productID;
    private String Orderstatus;

    private String Outletname;

    public String getOutletname() {
        return Outletname;
    }

    public void setOutletname(String outletname) {
        Outletname = outletname;
    }

    public String getOrderstatus() {
        return Orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        Orderstatus = orderstatus;
    }

    private String deliveryQty;
    public String getProductID() {
        return productID;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getApproved_qty() {
        return approved_qty;
    }

    public String getDeliveryQty() {
        return deliveryQty;
    }

    public void setDeliveryQty(String deliveryQty) {
        this.deliveryQty = deliveryQty;
    }

    public void setApproved_qty(String approved_qty) {
        this.approved_qty = approved_qty;
    }

    public Order_history() {

    }
    public Order_history(String order_no, String date) {
        this.order_no = order_no;
        this.date = date;

    }
    public Order_history(String orderID,String agencyName, String itemName, String qty, String  approvedQty) {

        this.order_no = orderID;
        this.agencyName = agencyName;
        this.ItemName = itemName;
        this.qty = qty;
        this.approved_qty =approvedQty;
    }

    public Order_history(String agencyName, String productName, String deletedItemQuantity) {

        this.agencyName = agencyName;
        this.ItemName = productName;
        this.qty = deletedItemQuantity;
    }
    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Order_history{" +
                "sno='" + sno + '\'' +
                ", order_no='" + order_no + '\'' +
                ", date='" + date + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", qty=" + qty +
                ", approved_qty='" + approved_qty + '\'' +
                ", productID='" + productID + '\'' +
                ", Orderstatus='" + Orderstatus + '\'' +
                ", Outletname='" + Outletname + '\'' +
                ", deliveryQty='" + deliveryQty + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order_history that = (Order_history) o;
        return Objects.equals(order_no, that.order_no) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash( order_no, date);
    }

    public String getOrderID() {
        return order_no;
    }
}
