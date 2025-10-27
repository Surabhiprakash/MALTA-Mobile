package com.malta_mqf.malta_mobile.Model;

public class OutletBean {
    String outletids;
    String outletnames;
    String customername;

    public OutletBean() {

    }

    public OutletBean(String outletids, String outletnames) {
        this.outletids = outletids;
        this.outletnames = outletnames;
    }

    public OutletBean(String outletids, String outletnames, String customername) {
        this.outletids = outletids;
        this.outletnames = outletnames;
        this.customername = customername;
    }

    public String getOutletids() {
        return outletids;
    }

    public void setOutletids(String outletids) {
        this.outletids = outletids;
    }

    public String getOutletnames() {
        return outletnames;
    }

    public void setOutletnames(String outletnames) {
        this.outletnames = outletnames;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    @Override
    public String toString() {
        return outletnames
                ;
    }
}
