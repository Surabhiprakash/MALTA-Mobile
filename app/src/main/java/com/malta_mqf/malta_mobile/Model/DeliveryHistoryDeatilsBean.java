package com.malta_mqf.malta_mobile.Model;

public class DeliveryHistoryDeatilsBean {

    String itemname, itemCode, delqty, price, disc, net, vat, vatpencet, vat_amt, gross, uom, barcode, plucode, trn, dateTime, deliveryDateTime;
    String outletName;

    public String getPlucode() {
        return plucode;
    }

    public void setPlucode(String plucode) {
        this.plucode = plucode;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
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

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDelqty() {
        return delqty;
    }

    public void setDelqty(String delqty) {
        this.delqty = delqty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getVatpencet() {
        return vatpencet;
    }

    public void setVatpencet(String vatpencet) {
        this.vatpencet = vatpencet;
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

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(String deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    @Override
    public String toString() {
        return "DeliveryHistoryDeatilsBean{" +
                "itemname='" + itemname + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", delqty='" + delqty + '\'' +
                ", price='" + price + '\'' +
                ", disc='" + disc + '\'' +
                ", net='" + net + '\'' +
                ", vat='" + vat + '\'' +
                ", vatpencet='" + vatpencet + '\'' +
                ", vat_amt='" + vat_amt + '\'' +
                ", gross='" + gross + '\'' +
                ", uom='" + uom + '\'' +
                ", barcode='" + barcode + '\'' +
                ", plucode='" + plucode + '\'' +
                ", trn='" + trn + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", deliveryDateTime='" + deliveryDateTime + '\'' +
                ", outletName='" + outletName + '\'' +
                '}';
    }
}
