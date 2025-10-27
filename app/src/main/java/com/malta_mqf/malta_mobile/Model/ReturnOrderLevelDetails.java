package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class ReturnOrderLevelDetails {
    @SerializedName("customerCode")
    String customerCode;
    @SerializedName("customerName")
    String customerName;
    @SerializedName("outlet_id")
    String outletId;

    @SerializedName("returned_datetime")
    String returnedDatetime;

    @SerializedName("refno")
    String refno;

    @SerializedName("comments")
    String comments;

    @SerializedName("returntotalnetamount")
    String returntotalnetamount;

    @SerializedName("returntotalvatamount")
    String returntotalvatamount;

    @SerializedName("orderid")
    String orderid;

    @SerializedName("invoiceno")
    String invoiceno;

    @SerializedName("returned_date")
    String returnedDate;

    @SerializedName("credit_noteid")
    String creditNoteid;

    @SerializedName("creditnotetotalamt")
    String creditnotetotalamount;

    @SerializedName("creditwithoutrebate")
    String creditwthoutrebate;

    @SerializedName("returncount")
    String returncount;

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setReturnedDatetime(String returnedDatetime) {
        this.returnedDatetime = returnedDatetime;
    }
    public String getReturnedDatetime() {
        return returnedDatetime;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }
    public String getRefno() {
        return refno;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getComments() {
        return comments;
    }

    public void setReturntotalnetamount(String returntotalnetamount) {
        this.returntotalnetamount = returntotalnetamount;
    }
    public String getReturntotalnetamount() {
        return returntotalnetamount;
    }

    public void setReturntotalvatamount(String returntotalvatamount) {
        this.returntotalvatamount = returntotalvatamount;
    }
    public String getReturntotalvatamount() {
        return returntotalvatamount;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
    public String getOrderid() {
        return orderid;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }
    public String getInvoiceno() {
        return invoiceno;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }
    public String getReturnedDate() {
        return returnedDate;
    }

    public void setCreditNoteid(String creditNoteid) {
        this.creditNoteid = creditNoteid;
    }
    public String getCreditNoteid() {
        return creditNoteid;
    }

    public void setCreditnotetotalamount(String creditnotetotalamount) {
        this.creditnotetotalamount = creditnotetotalamount;
    }
    public String getCreditnotetotalamount() {
        return creditnotetotalamount;
    }

    public void setCreditwthoutrebate(String creditwthoutrebate) {
        this.creditwthoutrebate = creditwthoutrebate;
    }
    public String getCreditwthoutrebate() {
        return creditwthoutrebate;
    }

    public void setReturncount(String returncount) {
        this.returncount = returncount;
    }
    public String getReturncount() {
        return returncount;
    }

    @Override
    public String toString() {
        return "ReturnOrderLevelDetails{" +
                "customerCode='" + customerCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", outletId='" + outletId + '\'' +
                ", returnedDatetime='" + returnedDatetime + '\'' +
                ", refno='" + refno + '\'' +
                ", comments='" + comments + '\'' +
                ", returntotalnetamount='" + returntotalnetamount + '\'' +
                ", returntotalvatamount='" + returntotalvatamount + '\'' +
                ", orderid='" + orderid + '\'' +
                ", invoiceno='" + invoiceno + '\'' +
                ", returnedDate='" + returnedDate + '\'' +
                ", creditNoteid='" + creditNoteid + '\'' +
                ", creditnotetotalamount='" + creditnotetotalamount + '\'' +
                ", creditwthoutrebate='" + creditwthoutrebate + '\'' +
                ", returncount='" + returncount + '\'' +
                '}';
    }
}
