package com.malta_mqf.malta_mobile.Model;

public class deliveryhistorybean implements Comparable<deliveryhistorybean> {
    String invoiceOrOrderID;
    String Datetime;
    String status;
    String outletName;
    String customer;
    String referenceNo;
    String totalAmount;
    String outletcode;

    public String getInvoiceOrOrderID() {
        return invoiceOrOrderID;
    }

    public void setInvoiceOrOrderID(String invoiceOrOrderID) {
        this.invoiceOrOrderID = invoiceOrOrderID;
    }
    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }
    public String getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDatetime() {
        return Datetime;
    }

    public void setDatetime(String datetime) {
        Datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getOutletcode() {
        return outletcode;
    }

    public void setOutletcode(String outletcode) {
        this.outletcode = outletcode;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    @Override
    public String toString() {
        return "deliveryhistorybean{" +
                "invoiceOrOrderID='" + invoiceOrOrderID + '\'' +
                ", Datetime='" + Datetime + '\'' +
                ", status='" + status + '\'' +
                ", outletName='" + outletName + '\'' +
                ", customer='" + customer + '\'' +
                ", outletcode='" + outletcode + '\'' +
                '}';
    }

    @Override
    public int compareTo(deliveryhistorybean deliveryhistorybean) {
        String thisLastFour = this.invoiceOrOrderID.substring(this.invoiceOrOrderID.length() - 4);
        String otherLastFour = deliveryhistorybean.invoiceOrOrderID.substring(deliveryhistorybean.invoiceOrOrderID.length() - 4);
        return otherLastFour.compareTo(thisLastFour); // Descending order
    }
}
