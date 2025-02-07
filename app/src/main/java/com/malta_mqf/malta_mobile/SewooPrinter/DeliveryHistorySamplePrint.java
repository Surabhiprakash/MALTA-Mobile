package com.malta_mqf.malta_mobile.SewooPrinter;

import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.comments;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.deliveryHistoryDetailsList;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.invNoOrOrderId;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.reference;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.name;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.vehiclenum;
import static com.malta_mqf.malta_mobile.NewSaleActivity.invoiceNumber;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.route;

import static com.malta_mqf.malta_mobile.NewSaleInvoice.orderToInvoice;
import static com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity.customerDetailsDB;
import static com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity.customeraddress;
import static com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity.customercode;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.outletname;
//import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.barcode;

import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.returnTrn;
import static com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity.customername;
import static com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity.emirate;
import static com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity.outletaddress;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.Model.DeliveryHistoryDeatilsBean;
import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;
import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DeliveryHistorySamplePrint  extends AppCompatActivity {

    private ESCPOSPrinter escposPrinter;

    private final char ESC = ESCPOS.ESC;
    private final char LF = ESCPOS.LF;

    private CheckPrinterStatus check_status;
    private int sts;
    public  static List<DeliveryHistoryDeatilsBean> newSaleBeanLists = new LinkedList<>();
    ;
    String orderId, returnComments, returnrefrence,TRN_NO;
    String customerCode;
    public static double totalQty = 0;
    public static   BigDecimal totalNetAmount = BigDecimal.ZERO;
    public static   BigDecimal totalVatAmount = BigDecimal.ZERO;
    public static   BigDecimal totalGrossAmount = BigDecimal.ZERO;
    public static double  totalGrossAmt, NET, ITEM_VAT_AMT, ITEMS_GROSS,amountPayableAfterRebate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();





    }
    public DeliveryHistorySamplePrint()
    {
        escposPrinter = new ESCPOSPrinter();    //Default = English.
        //escposPrinter = new ESCPOSPrinter("EUC-KR"); // Korean.
        //escposPrinter = new ESCPOSPrinter("GB2312"); //Chinese.
        check_status = new CheckPrinterStatus();
    }

/*    public int Print_Sample_2() throws UnsupportedEncodingException
    {
        sts = check_status.PrinterStatus(escposPrinter);
        if(sts != ESCPOSConst.LK_SUCCESS) return sts;

        escposPrinter.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|2C" + "Receipt" + LF + LF);
        escposPrinter.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
        escposPrinter.printNormal(ESC+"|cAThank you!!!\n");
        escposPrinter.printNormal("Chicken                   $10.00\n");
        escposPrinter.printNormal("Hamburger                 $20.00\n");
        escposPrinter.printNormal("Pizza                     $30.00\n");
        escposPrinter.printNormal("Lemons                    $40.00\n");
        escposPrinter.printNormal("Drink                     $50.00\n");
        escposPrinter.printNormal("Excluded tax             $150.00\n");
        escposPrinter.printNormal(ESC+"|uCTax(5%)                    $7.50\n");
        escposPrinter.printNormal(ESC+"|bC"+ESC+"|2CTotal    $157.50\n\n");
        escposPrinter.printNormal("Payment                  $200.00\n");
        escposPrinter.printNormal("Change                    $42.50\n\n");
        escposPrinter.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode

        return 0;
    }*/



    public static String generateInvoiceNumber(String orderId) {
        // Create a random 3-digit number
        int randomNum = generateRandomNumber(100, 999);

        // Combine with the order ID
        String invoiceNumber = "INV" + orderId + "-" + randomNum;

        return invoiceNumber;
    }

    // Generate a random number within a specified range
    private static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    @SuppressLint("DefaultLocale")
    public int Print_Sample_4() throws IOException {
        ESCPOSPrinter escposPrinter = new ESCPOSPrinter();
        sts = check_status.PrinterStatus(escposPrinter);
        if (sts != ESCPOSConst.LK_SUCCESS) return sts;


        // Sample values from your existing data
        int itemCount = newSaleBeanLists.size();


        // Print header
        escposPrinter.printText(centerAlignText("Malta Quality Foodstuff Trading LLC") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Office 401-02,Eldorado Building Humaid Alhasm Al Rumaithi") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("65st,Al Danah") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Tell : +971 2 583 2166") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("PO Box No 105689, Abu Dhabi, United Arab Emirates") +"", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("TRN: 100014706400003") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Invoiced Date: " + convertDate( newSaleBeanLists.get(0).getDeliveryDateTime().substring(0,10))+"Invoiced Time: " + newSaleBeanLists.get(0).getDeliveryDateTime().substring(11,16)) + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Re-print Date: " + getCurrentDate() + " "+"Re-print Time: " + getCurrentTime()) + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("TAX INVOICE") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Invoice No: " + invNoOrOrderId) + "\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print customer details
        escposPrinter.printText("\r"+"CUSTOMER NAME: " + customername + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        System.out.println("customer code in deliveryhistorysampleprint is :"+customername);
        escposPrinter.printText("ADDRESS: "+customeraddress+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("BRANCH: " + outletname + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("TRN: " + returnTrn + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("EMIRATE: "+ emirate+  "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("VEHICLE NO: " + vehiclenum + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("ROUTE: " + route + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("SALESMAN: " + name + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("REF.NO: " + reference + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("COMMENTS: " + comments + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print item headers
        escposPrinter.printText("\n---------------------------------------------------------------------\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("ITEM   BARCODE  QTY   PRICE   DISC    NET VAT %    VAT AMT    GROSS\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("---------------------------------------------------------------------\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Initialize totals
        totalQty = 0;
        totalNetAmount = BigDecimal.ZERO;
        totalVatAmount = BigDecimal.ZERO;
        totalGrossAmount = BigDecimal.ZERO;
        int itemsCount=1;
        // Loop through items
        for (int i = 0; i < itemCount; i++) {
            if(!newSaleBeanLists.get(i).getDelqty().equals("0") || newSaleBeanLists.get(i).getDelqty().isEmpty()) {
               DeliveryHistoryDeatilsBean  item = newSaleBeanLists.get(i);
                String qty = item.getDelqty();
                String plucode="";
                if(newSaleBeanLists.get(i).getPlucode().equals(null)|| newSaleBeanLists.get(i).getPlucode().isEmpty()|| newSaleBeanLists.get(i).getPlucode()==null|| "NA".equals(newSaleBeanLists.get(i).getPlucode())){
                    plucode="";
                }else{
                    plucode=newSaleBeanLists.get(i).getPlucode();
                }
                BigDecimal qtyValue = BigDecimal.valueOf(Double.parseDouble(qty));
                String qtyWithUom = String.format("%.2f %s", qtyValue, item.getUom());
                BigDecimal priceValue = BigDecimal.valueOf(Double.parseDouble(item.getPrice())).setScale(2, RoundingMode.HALF_UP);
                BigDecimal netAmount = qtyValue.multiply( priceValue).setScale(2, RoundingMode.HALF_UP);
                BigDecimal vatAmount = netAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal grossAmount = netAmount.add( vatAmount);

                totalQty +=qtyValue.intValue();
                totalNetAmount =totalNetAmount.add( netAmount);
                totalVatAmount =totalVatAmount.add( vatAmount);
                totalGrossAmount =totalGrossAmount.add( grossAmount);

// Define a width for the item count, assuming maximum 99 items (2 digits)
                int itemCountWidth = 2;
                String format = "%-" + itemCountWidth + "d. %s\r\n";

// Assuming LKPrint.LK_ALIGNMENT_LEFT is a constant for left alignment
                escposPrinter.printText(
                        String.format(format, itemsCount++, item.getItemname() + " " + item.getItemCode() + " " + plucode),
                        LKPrint.LK_ALIGNMENT_LEFT,
                        LKPrint.LK_FNT_DEFAULT,
                        LKPrint.LK_TXT_1WIDTH
                );
                escposPrinter.printText(String.format("%-4s %-4s %-4s %-6.2f %-6s %-6.2f %-3s %-7.2f %-7.2f\n\r", " ", item.getBarcode(), qtyWithUom, priceValue, item.getDisc(), netAmount, "5%", vatAmount, grossAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
            }
        }
        // Print totals
        String rebateStr = getCustomerRebate(customercode);

        double rebate = 0.0;
        try {
            rebate = Double.parseDouble(rebateStr);
        } catch (NumberFormatException |NullPointerException e) {
            e.printStackTrace(); // Handle parsing exception if necessary
        }
        // Convert rebate percentage and total gross amount to BigDecimal
        BigDecimal rebatePercent = BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100.0));


// Calculate rebateAmount with proper precision
        BigDecimal rebateAmount = totalGrossAmount.multiply(rebatePercent);

// Optionally, if you need `rebatePercent` as a double for display purposes
        double rebatePercentDouble = rebatePercent.doubleValue();


        BigDecimal amountPayableAfterRebate = totalGrossAmount.subtract( rebateAmount);
        escposPrinter.printText("--------------------------------------------------------------------\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Items:              %d\r\n", itemsCount-1), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Qty:                %d\r\n",(int)totalQty), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total NET Amount:         AED %.2f\r\n", totalNetAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total VAT Amount:         AED %.2f\r\n", totalVatAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Gross Amount:       AED %.2f\r\n", totalGrossAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
       // escposPrinter.printText(String.format("Gross  Amount Payable:    AED %.2f\n",amountPayableAfterRebate), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Sales Person Name:        %s\r\n",name), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Invoice No:               %s\r\n",invNoOrOrderId), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print signatures
        escposPrinter.lineFeed(3);
        escposPrinter.printText("-----------------------------\t\t-----------------------------\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.lineFeed(1);
        escposPrinter.printText("Buyer's Signature\t\t\t\tSeller's Signature\t\t\t\t\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.lineFeed(4);

        return 0;
    }




    @SuppressLint("Range")
    private String getCustomerRebate(String customerCode) {
        System.out.println("heyeyeyeyeye customer code in sample print:"+customerCode);
        Cursor cursor = customerDetailsDB.getCustomerDetailsById(customerCode);
        String rebate = null;

        while (cursor.moveToNext()) {
            rebate = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_REBATE));
            if (rebate == null) {
                rebate = "0";

            }
        }

        return rebate;
    }


    private String centerAlignText(String text) {
        int maxLength = 70; // Maximum length of the line
        int paddingLength = (maxLength - text.length()) / 2;
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < paddingLength; i++) {
            padding.append(" ");
        }
        return padding.toString() + text + padding.toString();
    }
    private String convertDate(String inputDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MMM/yyyy");
        String formattedDate = "";

        try {
            Date date = originalFormat.parse(inputDate); // Parse the original date format
            formattedDate = targetFormat.format(date);   // Convert to target format
        } catch (ParseException e) {
            e.printStackTrace(); // Handle exception if input date format is incorrect
        }

        return formattedDate;
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        return dateFormat.format(new Date());
    }

    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        return timeFormat.format(new Date());
    }

}
