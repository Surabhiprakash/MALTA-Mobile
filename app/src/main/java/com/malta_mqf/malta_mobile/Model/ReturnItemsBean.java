package com.malta_mqf.malta_mobile.Model;

import java.util.Objects;

public class ReturnItemsBean {
    private String order_id;
    private String invoice_no;
    private String outletid;
    private String date;
    private String status;
    private String outletname;
    private String customername;
    private String customercode;
    private String customeraddress;
    private String trn;

    public String getOutletid() {
        return outletid;
    }

    public void setOutletid(String outletid) {
        this.outletid = outletid;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getCustomercode() {
        return customercode;
    }

    public void setCustomercode(String customercode) {
        this.customercode = customercode;
    }

    public String getCustomeraddress() {
        return customeraddress;
    }

    public void setCustomeraddress(String customeraddress) {
        this.customeraddress = customeraddress;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutletname() {
        return outletname;
    }

    public void setOutletname(String outletname) {
        this.outletname = outletname;
    }

    public String getTrn() {
        return trn;
    }

    public void setTrn(String trn) {
        this.trn = trn;
    }

    @Override
    public String toString() {
        return "ReturnItemsBean{" +
                "order_id='" + order_id + '\'' +
                ", invoice_no='" + invoice_no + '\'' +
                ", outletid='" + outletid + '\'' +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", outletname='" + outletname + '\'' +
                ", customername='" + customername + '\'' +
                ", customercode='" + customercode + '\'' +
                ", customeraddress='" + customeraddress + '\'' +
                ", trn='" + trn + '\'' +
                '}';
    }
}
