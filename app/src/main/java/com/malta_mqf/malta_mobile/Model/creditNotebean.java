package com.malta_mqf.malta_mobile.Model;

import java.io.Serializable;
import java.util.Objects;

public class creditNotebean implements Serializable {
    String itemName,itemCode,Delqty, returnQty, sellingprice,retrunreason, disc, net, vat_percent, vat_amt, gross,uom,barcode,trn,plucode;

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

    public String getTrn() {
        return trn;
    }

    public void setTrn(String trn) {
        this.trn = trn;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getVat_percent() {
        return vat_percent;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    @Override
    public String toString() {
        return "creditNotebean{" +
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
                ", barcode='" + barcode + '\'' +
                ", trn='" + trn + '\'' +
                ", plucode='" + plucode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        creditNotebean that = (creditNotebean) o;
        return Objects.equals(itemName, that.itemName) && Objects.equals(itemCode, that.itemCode) && Objects.equals(Delqty, that.Delqty) && Objects.equals(returnQty, that.returnQty) && Objects.equals(sellingprice, that.sellingprice) && Objects.equals(retrunreason, that.retrunreason) && Objects.equals(disc, that.disc) && Objects.equals(net, that.net) && Objects.equals(vat_percent, that.vat_percent) && Objects.equals(vat_amt, that.vat_amt) && Objects.equals(gross, that.gross) && Objects.equals(uom, that.uom) && Objects.equals(barcode, that.barcode) && Objects.equals(trn, that.trn) && Objects.equals(plucode, that.plucode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, itemCode, Delqty, returnQty, sellingprice, retrunreason, disc, net, vat_percent, vat_amt, gross, uom, barcode, trn, plucode);
    }
}
