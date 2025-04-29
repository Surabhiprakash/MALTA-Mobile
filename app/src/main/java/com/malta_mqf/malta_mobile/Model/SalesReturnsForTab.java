package com.malta_mqf.malta_mobile.Model;


import com.google.gson.annotations.SerializedName;

public class SalesReturnsForTab {
        @SerializedName("return_amt")
        private String returnAmt;

        @SerializedName("total_Sales_Amt")
        private String totalSalesAmt;

        @SerializedName("reusable_Amt")
        private String reusableAmt;

        @SerializedName("netamount")
        private String netAmount;

        @SerializedName("total_Return_Amt")
        private String totalReturnAmt;

        @SerializedName("net_Sales_Amount")
        private String netSalesAmount;

        @SerializedName("return_percentage")
        private String returnPercentage;

        @SerializedName("delivered_date")
        private String deliveredDate;

        // Getters
        public String getReturnAmt() {
            return returnAmt;
        }

        public String getTotalSalesAmt() {
            return totalSalesAmt;
        }

        public String getReusableAmt() {
            return reusableAmt;
        }

        public String getNetAmount() {
            return netAmount;
        }

        public String getTotalReturnAmt() {
            return totalReturnAmt;
        }

        public String getNetSalesAmount() {
            return netSalesAmount;
        }

        public String getReturnPercentage() {
            return returnPercentage;
        }

        public String getDeliveredDate() {
            return deliveredDate;
        }
    }


