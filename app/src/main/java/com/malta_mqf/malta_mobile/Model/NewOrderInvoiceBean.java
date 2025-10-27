package com.malta_mqf.malta_mobile.Model;

import java.io.Serializable;
import java.util.List;

public class NewOrderInvoiceBean implements Serializable {
    String itemName,itemCode,Delqty, returnQty, sellingprice,retrunreason, disc, net, vat_percent, vat_amt, gross,uom,agency_code,itemId,barcode,plucode,trn;


    //po,porefname,pocreateddate

    List<String> po;
    List<String> porefname;
    List<String> pocreateddate;

    public String getPlucode() {
        return plucode;
    }

    public void setPlucode(String plucode) {
        this.plucode = plucode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(String returnQty) {
        this.returnQty = returnQty;
    }

    public String getSellingprice() {
        return sellingprice;
    }

    public void setSellingprice(String sellingprice) {
        this.sellingprice = sellingprice;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTrn() {
        return trn;
    }

    public void setTrn(String trn) {
        this.trn = trn;
    }

    public String getVat_percent() {
        return vat_percent;
    }

    public void setVat_percent(String vat_percent) {
        this.vat_percent = vat_percent;
    }

    public String getVat_amt() {
        return vat_amt;
    }

    public void setVat_amt(String vat_amt) {
        this.vat_amt = vat_amt;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getRetrunreason() {
        return retrunreason;
    }

    public void setRetrunreason(String retrunreason) {
        this.retrunreason = retrunreason;
    }

    public String getDelqty() {
        return Delqty;
    }

    public void setDelqty(String delqty) {
        Delqty = delqty;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }




    public void setPorefname(List<String> porefname) {
        this.porefname = porefname;
    }




    public String getAgency_code() {
        return agency_code;
    }

    public void setAgency_code(String agency_code) {
        this.agency_code = agency_code;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


    public void setPo(List<String> po) {
        this.po = po;
    }

    public void setPocreateddate(List<String> pocreateddate) {
        this.pocreateddate = pocreateddate;
    }

    public List<String> getPo() {
        return po;
    }

    public List<String> getPorefname() {
        return porefname;
    }

    public List<String> getPocreateddate() {
        return pocreateddate;
    }


    @Override
    public String toString() {
        return "NewOrderInvoiceBean{" +
                "itemName='" + itemName + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", Delqty='" + Delqty + '\'' +
                ", returnQty='" + returnQty + '\'' +
                ", sellingprice='" + sellingprice + '\'' +
                ", retrunreason='" + retrunreason + '\'' +
                ", disc='" + disc + '\'' +
                ", net='" + net + '\'' +
                ", vat_percent='" + vat_percent + '\'' +
                ", vat_amt='" + vat_amt + '\'' +
                ", gross='" + gross + '\'' +
                ", uom='" + uom + '\'' +
                ", agency_code='" + agency_code + '\'' +
                ", itemId='" + itemId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", plucode='" + plucode + '\'' +
                ", trn='" + trn + '\'' +
                ", po=" + po +
                ", porefname=" + porefname +
                ", pocreateddate=" + pocreateddate +
                '}';
    }
}
