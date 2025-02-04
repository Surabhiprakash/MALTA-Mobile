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


    public void setDeliveredOrderLevelDetails(List<DeliveredOrderLevelDetails> DeliveredOrderLevelDetails) {
        this.DeliveredOrderLevelDetails = DeliveredOrderLevelDetails;
    }
    public List<DeliveredOrderLevelDetails> getDeliveredOrderLevelDetails() {
        return DeliveredOrderLevelDetails;
    }

    public void setReturnOrderLevelDetails(List<ReturnOrderLevelDetails> ReturnOrderLevelDetails) {
        this.ReturnOrderLevelDetails = ReturnOrderLevelDetails;
    }
    public List<ReturnOrderLevelDetails> getReturnOrderLevelDetails() {
        return ReturnOrderLevelDetails;
    }

    public void setDeliveredOrderItemLevelDetails(List<DeliveredOrderItemLevelDetails> DeliveredOrderItemLevelDetails) {
        this.DeliveredOrderItemLevelDetails = DeliveredOrderItemLevelDetails;
    }
    public List<DeliveredOrderItemLevelDetails> getDeliveredOrderItemLevelDetails() {
        return DeliveredOrderItemLevelDetails;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setReturnOrderItemLevelDetails(List<ReturnOrderItemLevelDetails> ReturnOrderItemLevelDetails) {
        this.ReturnOrderItemLevelDetails = ReturnOrderItemLevelDetails;
    }
    public List<ReturnOrderItemLevelDetails> getReturnOrderItemLevelDetails() {
        return ReturnOrderItemLevelDetails;
    }

    public void setReturnWithoutInvoiceDetails(List<ReturnWithoutInvoiceDetails> ReturnWithoutInvoiceDetails) {
        this.ReturnWithoutInvoiceDetails = ReturnWithoutInvoiceDetails;
    }
    public List<ReturnWithoutInvoiceDetails> getReturnWithoutInvoiceDetails() {
        return ReturnWithoutInvoiceDetails;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
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
