package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesReturnsForTabList {

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
    @SerializedName("SalesReturnsForDateForTab")
    private List<SalesReturnsForTab> salesReturnsForDateForTab;

    public String getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(String salesTotal) {
        this.salesTotal = salesTotal;
    }

    public String getTotalReturns() {
        return totalReturns;
    }

    public void setTotalReturns(String totalReturns) {
        this.totalReturns = totalReturns;
    }

    public String getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(String returnPercentage) {
        this.returnPercentage = returnPercentage;
    }

    public String getReusableAmt() {
        return reusableAmt;
    }

    public void setReusableAmt(String reusableAmt) {
        this.reusableAmt = reusableAmt;
    }

    public String getActualSales() {
        return actualSales;
    }

    public void setActualSales(String actualSales) {
        this.actualSales = actualSales;
    }

    public String getActualReturns() {
        return actualReturns;
    }

    public void setActualReturns(String actualReturns) {
        this.actualReturns = actualReturns;
    }

    public String getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(String salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public List<SalesReturnsForTab> getSalesReturnsForDateForTab() {
        return salesReturnsForDateForTab;
    }
}
