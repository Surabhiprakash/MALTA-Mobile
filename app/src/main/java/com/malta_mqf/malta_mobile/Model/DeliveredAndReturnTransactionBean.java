package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeliveredAndReturnTransactionBean {
    @SerializedName("DeliveredOrderLevelDetails")
    List<DeliveredOrderLevelDetails> DeliveredOrderLevelDetails;

    @SerializedName("ReturnOrderLevelDetails")
    List<ReturnOrderLevelDetails> ReturnOrderLevelDetails;

    @SerializedName("DeliveredOrderItemLevelDetails")
    List<DeliveredOrderItemLevelDetails> DeliveredOrderItemLevelDetails;

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("ReturnOrderItemLevelDetails")
    List<ReturnOrderItemLevelDetails> ReturnOrderItemLevelDetails;

    @SerializedName("ReturnWithoutInvoiceDetails")
    List<ReturnWithoutInvoiceDetails> ReturnWithoutInvoiceDetails;

    @SerializedName("status")
    String status;

    public List<DeliveredOrderLevelDetails> getDeliveredOrderLevelDetails() {
        return DeliveredOrderLevelDetails;
    }

    public void setDeliveredOrderLevelDetails(List<DeliveredOrderLevelDetails> DeliveredOrderLevelDetails) {
        this.DeliveredOrderLevelDetails = DeliveredOrderLevelDetails;
    }

    public List<ReturnOrderLevelDetails> getReturnOrderLevelDetails() {
        return ReturnOrderLevelDetails;
    }

    public void setReturnOrderLevelDetails(List<ReturnOrderLevelDetails> ReturnOrderLevelDetails) {
        this.ReturnOrderLevelDetails = ReturnOrderLevelDetails;
    }

    public List<DeliveredOrderItemLevelDetails> getDeliveredOrderItemLevelDetails() {
        return DeliveredOrderItemLevelDetails;
    }

    public void setDeliveredOrderItemLevelDetails(List<DeliveredOrderItemLevelDetails> DeliveredOrderItemLevelDetails) {
        this.DeliveredOrderItemLevelDetails = DeliveredOrderItemLevelDetails;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ReturnOrderItemLevelDetails> getReturnOrderItemLevelDetails() {
        return ReturnOrderItemLevelDetails;
    }

    public void setReturnOrderItemLevelDetails(List<ReturnOrderItemLevelDetails> ReturnOrderItemLevelDetails) {
        this.ReturnOrderItemLevelDetails = ReturnOrderItemLevelDetails;
    }

    public List<ReturnWithoutInvoiceDetails> getReturnWithoutInvoiceDetails() {
        return ReturnWithoutInvoiceDetails;
    }

    public void setReturnWithoutInvoiceDetails(List<ReturnWithoutInvoiceDetails> ReturnWithoutInvoiceDetails) {
        this.ReturnWithoutInvoiceDetails = ReturnWithoutInvoiceDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeliveredAndReturnTransactionBean{" +
                "DeliveredOrderLevelDetails=" + DeliveredOrderLevelDetails +
                ", ReturnOrderLevelDetails=" + ReturnOrderLevelDetails +
                ", DeliveredOrderItemLevelDetails=" + DeliveredOrderItemLevelDetails +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", ReturnOrderItemLevelDetails=" + ReturnOrderItemLevelDetails +
                ", ReturnWithoutInvoiceDetails=" + ReturnWithoutInvoiceDetails +
                ", status='" + status + '\'' +
                '}';
    }
}
