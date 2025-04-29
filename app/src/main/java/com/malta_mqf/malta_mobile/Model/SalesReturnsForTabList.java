package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesReturnsForTabList {

    @SerializedName("SalesReturnsForDateForTab")
    private List<SalesReturnsForTab> salesReturnsForDateForTab;
    @SerializedName("sales_total")
    String salesTotal;

    @SerializedName("total_returns")
    String totalReturns;

    @SerializedName("return_percentage")
    String returnPercentage;

    @SerializedName("reusable_amt")
    String reusableAmt;

    @SerializedName("actual_sales")
    String actualSales;

    @SerializedName("actual_returns")
    String actualReturns;

    @SerializedName("sales_percentage")
    String salesPercentage;


    public void setSalesTotal(String salesTotal) {
        this.salesTotal = salesTotal;
    }
    public String getSalesTotal() {
        return salesTotal;
    }

    public void setTotalReturns(String totalReturns) {
        this.totalReturns = totalReturns;
    }
    public String getTotalReturns() {
        return totalReturns;
    }

    public void setReturnPercentage(String returnPercentage) {
        this.returnPercentage = returnPercentage;
    }
    public String getReturnPercentage() {
        return returnPercentage;
    }

    public void setReusableAmt(String reusableAmt) {
        this.reusableAmt = reusableAmt;
    }
    public String getReusableAmt() {
        return reusableAmt;
    }

    public void setActualSales(String actualSales) {
        this.actualSales = actualSales;
    }
    public String getActualSales() {
        return actualSales;
    }

    public void setActualReturns(String actualReturns) {
        this.actualReturns = actualReturns;
    }
    public String getActualReturns() {
        return actualReturns;
    }

    public void setSalesPercentage(String salesPercentage) {
        this.salesPercentage = salesPercentage;
    }
    public String getSalesPercentage() {
        return salesPercentage;
    }

    public List<SalesReturnsForTab> getSalesReturnsForDateForTab() {
        return salesReturnsForDateForTab;
    }
}
