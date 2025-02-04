package com.malta_mqf.malta_mobile.Model;

public class ReturnHistoryBean implements Comparable<ReturnHistoryBean> {

    String invoiceOrOrderID;
    String creditNoteID;
    String Datetime;
    String status;
    String outletName;
    String customer;

    public ReturnHistoryBean(String invoiceOrOrderID, String creditNoteID, String datetime, String status, String outletName, String customer) {
        this.invoiceOrOrderID = invoiceOrOrderID;
        this.creditNoteID = creditNoteID;
        Datetime = datetime;
        this.status = status;
        this.outletName = outletName;
        this.customer = customer;
    }
    public ReturnHistoryBean(){

    }

    public String getInvoiceOrOrderID() {
        return invoiceOrOrderID;
    }

    public void setInvoiceOrOrderID(String invoiceOrOrderID) {
        this.invoiceOrOrderID = invoiceOrOrderID;
    }

    public String getCreditNoteID() {
        return creditNoteID;
    }

    public void setCreditNoteID(String creditNoteID) {
        this.creditNoteID = creditNoteID;
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

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "ReturnHistoryBean{" +
                "invoiceOrOrderID='" + invoiceOrOrderID + '\'' +
                ", creditNoteID='" + creditNoteID + '\'' +
                ", Datetime='" + Datetime + '\'' +
                ", status='" + status + '\'' +
                ", outletName='" + outletName + '\'' +
                ", customer='" + customer + '\'' +
                '}';
    }

    @Override
    public int compareTo(ReturnHistoryBean returnHistoryBean) {
        String thisLastFour = this.creditNoteID.substring(this.creditNoteID.length() - 4);
        String otherLastFour = returnHistoryBean.creditNoteID.substring(returnHistoryBean.creditNoteID.length() - 4);
        return otherLastFour.compareTo(thisLastFour); // Descending order
    }

}
