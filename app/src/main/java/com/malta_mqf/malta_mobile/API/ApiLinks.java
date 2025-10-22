package com.malta_mqf.malta_mobile.API;

public class ApiLinks {
  //  public static final String urlBase = "http://64.227.151.183:8082/GFC/rest/";//GFC TEST SERVER
   //  public static final String urlBase = "http://47.91.121.165:8082/GFC/rest/";//MALTA url
  //public static final String urlBase = "http://10.1.2.153:8082/GFC/rest/";//MALTA url
public static final String urlBase = "http://sfa.mqftrading.com:8082/GFC/rest/";//MALTA url
//public static final String urlBase = "http://103.189.89.145:8082/GFC/rest/";//sfa
 // public static final String urlBase = "http://47.91.121.41:8082/GFC/rest/";
    public static final String loginurl = urlBase + "user/userMobileLogin";
    public static final String allCustomerDetails = urlBase + "customers/activeCustomerDetails";
    public static final String allAgencyDetails = urlBase + "agency/allActiveAgencyDetails";
    public static final String OutletDetailsById = urlBase + "outlets/outletDetailsBsdOnVanId";
    public static final String allItemDetailsById = urlBase + "items/activeItemsWithSp";
    public static final String get_outlet_associated_skus_for_agency = urlBase + "outlet_skus/get_outlet_associated_skus_for_agency";
    public static final String submitOrder = urlBase + "order/syncorder";
    public static final String approvedOrderDetailsBsdOnVanId = urlBase + "order/approvedOrderBsdOnVan";
   public static final String approvedOrderBsdOnVanWithApprovedDateTime = urlBase + "order/approvedOrderBsdOnVanWithApprovedDateTime";
    public static final String totalperItemapprovedDetailsBsdOnVanId = urlBase + "order/itemwiseQtyBsdOnVan";
   public static final String totalperItemapprovedDetailsBsdOnVanIdPowise = urlBase + "order/itemwiseQtyBsdOnVanPowise";
    public static final String allOrderDetails = urlBase + "order/allOrderDetails";
    public static final String itemDetailsBsdOnOrderId = urlBase + "order/approvedOrderDetailsBasedOnOrderId";
    public static final String allSellingPriceDetails = urlBase + "itemSellingPrice/allItemSellingPriceDetails";
    public static final String deliverysync = urlBase + "order/syncDeliveredQuantities";
    public static final String deliverysyncExtraItems = urlBase +"order/syncDeliveredQuantitiesWithExtraItems";
    public static final String loadsync = urlBase + "order/syncVanLoad";
    public static final String loadsyncPowise = urlBase +"order/syncVanLoadPowise";
    public static final String returnsync = urlBase + "order/syncMutipleReturnQuantitiesPerOrder";
    public static final String cancelOrdersSync=urlBase+"order/rejectOrderInBulkFromMobile";
    public static final String extraordersSync=urlBase+"order/syncExtraDeliveredOrders";
    public static final String returnWithoutInvoiceSync=urlBase+"order/syncReturnedQuantitiesWithoutInvoice";
    public static final String deliveredAndReturnTransactionSync=urlBase+"order/sevenDaysOrdersDataForMobile";
    public static final String syncVanStock=urlBase+"stock/syncVanStock";
    public static final String getPreviousVanStockByVan=urlBase+"stock/vanStockDetails";
    public static final String getPreviousInvoiceOutletsByVan=urlBase+"invoices/getPreviousInvoicesOutlets";
    public static final String getInvoiceDetailsByInvoiceNo=urlBase+"order/previousInvoiceDetails";
   public static final String getPreviousLoadsByVan=urlBase+"order/restoreVanLoadBsdOnVanid";
    public static final String onlineReturnDetails=urlBase+"returns/getPreviousReturnsOutlets";
    public static final String allOnlineReturnDetails=urlBase+"returns/getPreviousReturns";
    public static final String SalesAndReturns = urlBase + "reports/tabdashboardsalesreturns";
    public static final String TabDashboardDateWiseSalesReturns = urlBase + "reports/tabdashboarddatewisesalesreturns";

}
