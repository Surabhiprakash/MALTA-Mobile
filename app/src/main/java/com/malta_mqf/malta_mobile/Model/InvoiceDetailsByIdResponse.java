package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InvoiceDetailsByIdResponse {
    @SerializedName("outletCode")
    String outletcode;


    @SerializedName("refno")
    String refno;

    @SerializedName("salesmanName")
    String salesmanName;

    @SerializedName("comments")
    String comments;

    @SerializedName("totalvatamount")
    String totalvatamount;

    @SerializedName("invoicetotal")
    String invoicetotal;

    @SerializedName("invoicewithoutrebate")
    String invoicewithoutrebate;

    @SerializedName("message")
    String message;

    @SerializedName("totalnetamount")
    String totalnetamount;

    @SerializedName("IndividualPoDetails")
    List<InvoiceDetailsByInvoiceNumber> IndividualPoDetails;

    @SerializedName("categoryName")
    String categoryName;

    @SerializedName("customerName")
    String customerName;

    @SerializedName("outletName")
    String outletName;

    @SerializedName("vanName")
    String vanName;

    @SerializedName("action")
    String action;

    @SerializedName("totalitems")
    String totalitems;

    @SerializedName("status")
    String status;

    public String getOutletcode() {
        return outletcode;
    }

    public void setOutletcode(String outletcode) {
        this.outletcode = outletcode;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTotalvatamount() {
        return totalvatamount;
    }

    public void setTotalvatamount(String totalvatamount) {
        this.totalvatamount = totalvatamount;
    }

    public String getInvoicetotal() {
        return invoicetotal;
    }

    public void setInvoicetotal(String invoicetotal) {
        this.invoicetotal = invoicetotal;
    }

    public String getInvoicewithoutrebate() {
        return invoicewithoutrebate;
    }

    public void setInvoicewithoutrebate(String invoicewithoutrebate) {
        this.invoicewithoutrebate = invoicewithoutrebate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotalnetamount() {
        return totalnetamount;
    }

    public void setTotalnetamount(String totalnetamount) {
        this.totalnetamount = totalnetamount;
    }

    public List<InvoiceDetailsByInvoiceNumber> getIndividualPoDetails() {
        return IndividualPoDetails;
    }

    public void setIndividualPoDetails(List<InvoiceDetailsByInvoiceNumber> IndividualPoDetails) {
        this.IndividualPoDetails = IndividualPoDetails;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getVanName() {
        return vanName;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTotalitems() {
        return totalitems;
    }

    public void setTotalitems(String totalitems) {
        this.totalitems = totalitems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InvoiceDetailsByIdResponse{" +
                "outletcode='" + outletcode + '\'' +
                ", refno='" + refno + '\'' +
                ", salesmanName='" + salesmanName + '\'' +
                ", comments='" + comments + '\'' +
                ", totalvatamount='" + totalvatamount + '\'' +
                ", invoicetotal='" + invoicetotal + '\'' +
                ", invoicewithoutrebate='" + invoicewithoutrebate + '\'' +
                ", message='" + message + '\'' +
                ", totalnetamount='" + totalnetamount + '\'' +
                ", IndividualPoDetails=" + IndividualPoDetails +
                ", categoryName='" + categoryName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", outletName='" + outletName + '\'' +
                ", vanName='" + vanName + '\'' +
                ", action='" + action + '\'' +
                ", totalitems='" + totalitems + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
