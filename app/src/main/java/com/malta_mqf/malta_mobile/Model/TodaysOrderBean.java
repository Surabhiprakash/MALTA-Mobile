package com.malta_mqf.malta_mobile.Model;

import java.util.Objects;

public class TodaysOrderBean {

    String outletName,OutletAddress,outletid,orderid,outletCode,orderStatus,CustomerCode,InvoiceOrOrderID;
    private boolean isDelivered;
    public TodaysOrderBean() {
    }

    public String getInvoiceOrOrderID() {
        return InvoiceOrOrderID;
    }

    public void setInvoiceOrOrderID(String invoiceOrOrderID) {
        InvoiceOrOrderID = invoiceOrOrderID;
    }

    public TodaysOrderBean(String outletName, String outletAddress, String outletid, String outletCode, String status, String customerCode) {
        this.outletName = outletName;
        this.OutletAddress = outletAddress;
        this.outletid=outletid;
        this.outletCode=outletCode;
        this.orderStatus=status;
        this.CustomerCode=customerCode;
    }

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCode) {
        CustomerCode = customerCode;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getOutletAddress() {
        return OutletAddress;
    }

    public void setOutletAddress(String outletAddress) {
        OutletAddress = outletAddress;
    }

    public String getOutletid() {
        return outletid;
    }

    public void setOutletid(String outletid) {
        this.outletid = outletid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    @Override
    public String toString() {
        return "TodaysOrderBean{" +
                "outletName='" + outletName + '\'' +
                ", OutletAddress='" + OutletAddress + '\'' +
                ", outletid='" + outletid + '\'' +
                ", orderid='" + orderid + '\'' +
                ", outletCode='" + outletCode + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", CustomerCode='" + CustomerCode + '\'' +
                ", InvoiceOrOrderID='" + InvoiceOrOrderID + '\'' +
                ", isDelivered=" + isDelivered +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodaysOrderBean that = (TodaysOrderBean) o;
        return isDelivered == that.isDelivered && Objects.equals(outletName, that.outletName) && Objects.equals(OutletAddress, that.OutletAddress) && Objects.equals(outletid, that.outletid) && Objects.equals(orderid, that.orderid) && Objects.equals(outletCode, that.outletCode) && Objects.equals(orderStatus, that.orderStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outletName, OutletAddress, outletid, orderid, outletCode, orderStatus, isDelivered);
    }
}
