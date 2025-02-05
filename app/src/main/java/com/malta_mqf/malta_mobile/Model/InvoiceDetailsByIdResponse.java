package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InvoiceDetailsByIdResponse {
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


    public void setRefno(String refno) {
        this.refno = refno;
    }
    public String getRefno() {
        return refno;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }
    public String getSalesmanName() {
        return salesmanName;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getComments() {
        return comments;
    }

    public void setTotalvatamount(String totalvatamount) {
        this.totalvatamount = totalvatamount;
    }
    public String getTotalvatamount() {
        return totalvatamount;
    }

    public void setInvoicetotal(String invoicetotal) {
        this.invoicetotal = invoicetotal;
    }
    public String getInvoicetotal() {
        return invoicetotal;
    }

    public void setInvoicewithoutrebate(String invoicewithoutrebate) {
        this.invoicewithoutrebate = invoicewithoutrebate;
    }
    public String getInvoicewithoutrebate() {
        return invoicewithoutrebate;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setTotalnetamount(String totalnetamount) {
        this.totalnetamount = totalnetamount;
    }
    public String getTotalnetamount() {
        return totalnetamount;
    }

    public void setIndividualPoDetails(List<InvoiceDetailsByInvoiceNumber> IndividualPoDetails) {
        this.IndividualPoDetails = IndividualPoDetails;
    }
    public List<InvoiceDetailsByInvoiceNumber> getIndividualPoDetails() {
        return IndividualPoDetails;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }
    public String getOutletName() {
        return outletName;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }
    public String getVanName() {
        return vanName;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }

    public void setTotalitems(String totalitems) {
        this.totalitems = totalitems;
    }
    public String getTotalitems() {
        return totalitems;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
