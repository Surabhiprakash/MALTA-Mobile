package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllItemSellingPriceDetails {


        @SerializedName("action")
        String action;

        @SerializedName("ItemSellingPriceDetails")
        List<AllItemSellingPriceDetailsResponse> ItemSellingPriceDetails;

        @SerializedName("message")
        String message;

        @SerializedName("status")
        String status;


        public void setAction(String action) {
            this.action = action;
        }
        public String getAction() {
            return action;
        }

        public void setItemSellingPriceDetails(List<AllItemSellingPriceDetailsResponse> ItemSellingPriceDetails) {
            this.ItemSellingPriceDetails = ItemSellingPriceDetails;
        }
        public List<AllItemSellingPriceDetailsResponse> getItemSellingPriceDetails() {
            return ItemSellingPriceDetails;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }



    @Override
    public String toString() {
        return "AllItemSellingPriceDetails{" +
                "action='" + action + '\'' +
                ", ItemSellingPriceDetails=" + ItemSellingPriceDetails +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

