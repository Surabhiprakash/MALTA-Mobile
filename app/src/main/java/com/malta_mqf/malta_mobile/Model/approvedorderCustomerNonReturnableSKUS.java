package com.malta_mqf.malta_mobile.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class approvedorderCustomerNonReturnableSKUS {

    @SerializedName("action")
    String action;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    String status;

    @SerializedName("CustomerNonReturnableSKUS")
    List<ListCustomerNonreturnableSkus> approvedorderlistCustomerNonreturnableSkus;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<ListCustomerNonreturnableSkus> getApprovedorderlistCustomerNonreturnableSkus() {
        return approvedorderlistCustomerNonreturnableSkus;
    }

    public void setApprovedorderlistCustomerNonreturnableSkus(List<ListCustomerNonreturnableSkus> approvedorderlistCustomerNonreturnableSkus) {
        this.approvedorderlistCustomerNonreturnableSkus = approvedorderlistCustomerNonreturnableSkus;
    }


}
