package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class VanStockDetails {

    @SerializedName("itemCode")
    String itemCode;

    @SerializedName("vanName")
    String vanName;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("item_id")
    String item_id;

    @SerializedName("van_id")
    String vanId;

    @SerializedName("delivered_date")
    String deliveredDate;

    @SerializedName("quantities")
    String quantities;

    @SerializedName("categoryName")
    String itemcategory;

    @SerializedName("subCategory_name")
    String itemsubcategory;
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setVanName(String vanName) {
        this.vanName = vanName;
    }
    public String getVanName() {
        return vanName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() {
        return itemName;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }
    public String getVanId() {
        return vanId;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }
    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setQuantities(String quantities) {
        this.quantities = quantities;
    }
    public String getQuantities() {
        return quantities;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItemcategory() {
        return itemcategory;
    }

    public void setItemcategory(String itemcategory) {
        this.itemcategory = itemcategory;
    }

    public String getItemsubcategory() {
        return itemsubcategory;
    }

    public void setItemsubcategory(String itemsubcategory) {
        this.itemsubcategory = itemsubcategory;
    }

    @Override
    public String toString() {
        return "VanStockDetails{" +
                "itemCode='" + itemCode + '\'' +
                ", vanName='" + vanName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", item_id='" + item_id + '\'' +
                ", vanId='" + vanId + '\'' +
                ", deliveredDate='" + deliveredDate + '\'' +
                ", quantities='" + quantities + '\'' +
                ", itemcategory='" + itemcategory + '\'' +
                ", itemsubcategory='" + itemsubcategory + '\'' +
                '}';
    }
}
