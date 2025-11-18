package com.malta_mqf.malta_mobile.Utilities;

import com.malta_mqf.malta_mobile.Model.AllAgencyDetails;
import com.malta_mqf.malta_mobile.Model.AllCustomerDetails;
import com.malta_mqf.malta_mobile.Model.AllItemDeatilsById;
import com.malta_mqf.malta_mobile.Model.AllItemSellingPriceDetails;
import com.malta_mqf.malta_mobile.Model.AllOrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.AllReturnOrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.ApprovedOrdersBasedOnVanId;
import com.malta_mqf.malta_mobile.Model.CancelOrderResponse;
import com.malta_mqf.malta_mobile.Model.DashBoardResponse;
import com.malta_mqf.malta_mobile.Model.DeliveredAndReturnTransactionBean;
import com.malta_mqf.malta_mobile.Model.DeliveryOrderResponse;
import com.malta_mqf.malta_mobile.Model.ExtraOrderSyncResponse;
import com.malta_mqf.malta_mobile.Model.InvoiceDetailsByIdResponse;
import com.malta_mqf.malta_mobile.Model.LoadINSyncResponse;
import com.malta_mqf.malta_mobile.Model.OfflineOutletSkuAssosiatedResponse;
import com.malta_mqf.malta_mobile.Model.OnlineOutletSkuAssosiatedResponse;
import com.malta_mqf.malta_mobile.Model.OnlinePreviousInvoiceResponse;
import com.malta_mqf.malta_mobile.Model.OnlineReturnInfoResponse;
import com.malta_mqf.malta_mobile.Model.OrderDetailsBasedOnOrderIdResponse;
import com.malta_mqf.malta_mobile.Model.OrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.OutletAssociatedSKUAgencyResponse;
import com.malta_mqf.malta_mobile.Model.OutletSkuResponse;
import com.malta_mqf.malta_mobile.Model.OutletsById;
import com.malta_mqf.malta_mobile.Model.ReturnOrderWithoutInvoiceResponse;
import com.malta_mqf.malta_mobile.Model.SalesReturnsForTabList;
import com.malta_mqf.malta_mobile.Model.TotalItemsPerVanIdPoResponse;
import com.malta_mqf.malta_mobile.Model.UserModel;
import com.malta_mqf.malta_mobile.Model.VanLoadDetailsBasedOnVanResponse;
import com.malta_mqf.malta_mobile.Model.VanStockSyncResponse;
import com.malta_mqf.malta_mobile.Model.approvedorderCustomerNonReturnableSKUS;
import com.malta_mqf.malta_mobile.Model.returnOrderResponse;
import com.malta_mqf.malta_mobile.Model.vanStockTransactionResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterFace {
    @GET
    Call<UserModel> loginUser(@Url String url);

    @GET
    Call<AllCustomerDetails> allCustomerDetails(@Url String url);

    @GET
    Call<AllAgencyDetails> allAgencyDetails(@Url String url);

    @GET
    Call<AllItemDeatilsById> allItemDetailsById(@Url String url);

    @GET
    Call<OutletSkuResponse> outletskuassosiate(@Url String url);

    @GET
    Call<OutletAssociatedSKUAgencyResponse> OutletAssociatedSKUAgencyResponse(@Url String url);

    @GET
    Call<OnlineOutletSkuAssosiatedResponse> onlineOutletAssociatedSKUResponse (@Url String url);

    @GET
    Call<OfflineOutletSkuAssosiatedResponse> offlineOutletAssociatedSKUResponse (@Url String url);

    @GET
    Call<approvedorderCustomerNonReturnableSKUS> approveordercustomernonreturnableskus(@Url String url);
    @GET
    Call<OutletsById> outletsById(@Url String url);


    @POST
    @FormUrlEncoded
    Call<OrderDetailsResponse> submitOrder(@Url String url, @FieldMap Map<String, String> orderDetails);

    @GET
    Call<ApprovedOrdersBasedOnVanId> approvedOrderDetailsBsdOnVanId(@Url String url);

 /*   @GET
    Call<TotalPerItemsByVanId> totalperItemapprovedDetailsBsdOnVanId(@Url String url);*/

    @GET
    Call<AllOrderDetailsResponse> allOrderDetails(@Url String url);

    @GET
    Call<InvoiceDetailsByIdResponse> getInvoiceDetails(@Url String url);

    @GET
    Call<OnlinePreviousInvoiceResponse> getPreviousInvoiceByVanId(@Url String url);

    @GET
    Call<OrderDetailsBasedOnOrderIdResponse> orderDetailBasedOnOrderId(@Url String url);

    @GET
    Call<AllItemSellingPriceDetails> allSellingPriceDetails(@Url String url);


    @POST
    @FormUrlEncoded
    Call<DeliveryOrderResponse> DeliverOrderSubmit(@Url String url, @FieldMap Map<String, String> orderDetails);

    @POST
    @FormUrlEncoded
    Call<LoadINSyncResponse> LOADinSubmit(@Url String url, @FieldMap Map<String, String> orderDetails);


    @POST
    @FormUrlEncoded
    Call<returnOrderResponse> returnOrderSubmit(@Url String url, @FieldMap Map<String, String> orderDetails);

    @POST
    @FormUrlEncoded
    Call<CancelOrderResponse> cancelOrderSubmit(@Url String url, @FieldMap Map<String, String> cancelOrders);

    @POST
    @FormUrlEncoded
    Call<ExtraOrderSyncResponse> ExtraOrderSubmit(@Url String url, @FieldMap Map<String, String> cancelOrders);

    @POST
    @FormUrlEncoded
    Call<ReturnOrderWithoutInvoiceResponse> returnOrderWithoutInvoiceSubmit(@Url String url, @FieldMap Map<String, String> orderDetails);

    @GET
    Call<DeliveredAndReturnTransactionBean> allDeliveredAndReturnTransaction(@Url String url);

    @POST
    @FormUrlEncoded
    Call<VanStockSyncResponse> vanStockSync(@Url String url, @FieldMap Map<String, String> vanStock);

    @GET
    Call<vanStockTransactionResponse> allVanStockTransaction(@Url String url);

    @GET
    Call<VanLoadDetailsBasedOnVanResponse> allLoadInfoTransaction(@Url String url);

    @POST
    @FormUrlEncoded
    Call<LoadINSyncResponse> LOADinSyncPowise(@Url String url, @FieldMap Map<String, String> orderDetails);

    @GET
    Call<TotalItemsPerVanIdPoResponse> totalperItemapprovedDetailsBsdOnVanId(@Url String url);

    @GET
    Call<OnlineReturnInfoResponse> allReturnOrderDetailsByVanId(@Url String url);

    @GET
    Call<AllReturnOrderDetailsResponse> allReturnOrderDetails(@Url String url);

    @GET
    Call<DashBoardResponse> getDashBoardData(@Url String url);

    @GET
    Call<SalesReturnsForTabList> getDashBoardGraphData(@Url String url);

}
