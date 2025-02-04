package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

public class AllItemSellingPriceDetailsResponse {


        @SerializedName("itemName")
        String itemName;

        @SerializedName("sellingPrice")
        String sellingPrice;

        @SerializedName("itemCode")
        String itemCode;

        @SerializedName("customerCode")
        String customerCode;

        @SerializedName("id")
        String id;

        @SerializedName("customerName")
        String customerName;



    public void setItemName(String itemName) {
            this.itemName = itemName;
        }
        public String getItemName() {
            return itemName;
        }

        public void setSellingPrice(String sellingPrice) {
            this.sellingPrice = sellingPrice;
        }
        public String getSellingPrice() {
            return sellingPrice;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }
        public String getItemCode() {
            return itemCode;
        }

        public void setCustomerCode(String customerCode) {
            this.customerCode = customerCode;
        }
        public String getCustomerCode() {
            return customerCode;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
        public String getCustomerName() {
            return customerName;
        }

    @Override
    public String toString() {
        return "AllItemSellingPriceDetailsResponse{" +
                "itemName='" + itemName + '\'' +
                ", sellingPrice='" + sellingPrice + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", customerCode='" + customerCode + '\'' +
                ", id='" + id + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
