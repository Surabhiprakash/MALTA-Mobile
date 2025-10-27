package com.malta_mqf.malta_mobile.SewooPrinter;

import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.trn;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.Comments;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.credID;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.customerDetailsDB;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.customeraddress;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.customercode;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.customername;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.emirate;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.name;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.outletname;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.refrenceno;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.route;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity.vehiclenum;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ReturnWithoutInvoiceSamplePrint extends AppCompatActivity {

    private ESCPOSPrinter escposPrinter;

    // public static List<ShowOrderForInvoiceBean> newSaleBeanListsss = new LinkedList<>(orderToInvoice);

    private final char ESC = ESCPOS.ESC;
    private final char LF = ESCPOS.LF;

    private CheckPrinterStatus check_status;
    private int sts;
    private String customerCode;

    public static BigDecimal totalNetAmount,  totalVatAmount, totalGrossAmount,amountPayableAfterRebate;
    public static  int totalQty=0;


  public   static List<NewSaleBean> newSaleBeanLists2 = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    public ReturnWithoutInvoiceSamplePrint() {
        escposPrinter = new ESCPOSPrinter();    //Default = English.
        //escposPrinter = new ESCPOSPrinter("EUC-KR"); // Korean.
        //escposPrinter = new ESCPOSPrinter("GB2312"); //Chinese.
        check_status = new CheckPrinterStatus();
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

    @SuppressLint("DefaultLocale")
    public int ReturnPrint_Sample_4() throws IOException {
        System.out.println("the return list in return sample: "+newSaleBeanLists2);
        ESCPOSPrinter escposPrinter = new ESCPOSPrinter();
        sts = check_status.PrinterStatus(escposPrinter);
        if (sts != ESCPOSConst.LK_SUCCESS) return sts;


        // Sample values from your existing data
        int itemCount = newSaleBeanLists2.size();


        // Print header
        escposPrinter.printText(centerAlignText("Malta Quality Foodstuff Trading LLC") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Office 401-02,Eldorado Building Humaid Alhasm Al Rumaithi") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("65st,Al Danah") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Tell : +971 2 583 2166") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("PO Box No 105689, Abu Dhabi, United Arab Emirates") +"", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("TRN: 100014706400003") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Date: " + getCurrentDate() + " "+"Time: " + getCurrentTime()) + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("TAX CREDIT NOTE") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Credit Note No: " + credID) + "\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print customer details
        escposPrinter.printText("\r"+"CUSTOMER NAME: " + customername + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("ADDRESS:"+ customeraddress+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("BRANCH: " + outletname + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("TRN: " + trn + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("EMIRATE:"+ emirate  +"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("VEHICLE NO: " + vehiclenum +  "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("ROUTE: " + route + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("SALESMAN: " + name + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("REF.NO: " + refrenceno + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("COMMENTS: " + Comments + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

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

            NewSaleBean item = newSaleBeanLists2.get(i);
            String qty = item.getApprovedQty();
            String plucode="";
            if(newSaleBeanLists2.get(i).getPlucode().equals(null)|| newSaleBeanLists2.get(i).getPlucode().isEmpty()|| newSaleBeanLists2.get(i).getPlucode()==null){
                plucode="";
            }else{
                plucode=newSaleBeanLists2.get(i).getPlucode();
            }
            BigDecimal qtyValue = BigDecimal.valueOf(Double.parseDouble(qty));
            String qtyWithUom = String.format("%.2f %s", qtyValue, item.getUom());
            BigDecimal priceValue = BigDecimal.valueOf(Double.parseDouble(item.getSellingPrice())).setScale(2, RoundingMode.HALF_UP);
            BigDecimal netAmount = qtyValue.multiply( priceValue);
            BigDecimal vatAmount = netAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal grossAmount = netAmount.add( vatAmount);

            totalQty +=qtyValue.intValue();
            totalNetAmount =totalNetAmount.add( netAmount).setScale(2, RoundingMode.HALF_UP);
            totalVatAmount =totalVatAmount.add( vatAmount).setScale(2, RoundingMode.HALF_UP);
            totalGrossAmount =totalGrossAmount.add( grossAmount).setScale(2, RoundingMode.HALF_UP);

// Define a width for the item count, assuming maximum 99 items (2 digits)
            int itemCountWidth = 2;
            String format = "%-" + itemCountWidth + "d. %s\r\n";

// Assuming LKPrint.LK_ALIGNMENT_LEFT is a constant for left alignment
            escposPrinter.printText(
                    String.format(format, itemsCount++, item.getProductName() + " " + item.getItemCode() + " " + plucode),
                    LKPrint.LK_ALIGNMENT_LEFT,
                    LKPrint.LK_FNT_DEFAULT,
                    LKPrint.LK_TXT_1WIDTH
            );
            escposPrinter.printText(String.format("%-4s %-4s %-4s %-6.2f %-6s %-6.2f %-3s %-7.2f %-7.2f\n\r", " ", item.getBarcode(), qtyWithUom, priceValue, "0.00", netAmount, "5%", vatAmount, grossAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        }

        // Print totals
        String rebateStr = getCustomerRebate(customercode);

        double rebate = 0.0;
        try {
            rebate = Double.parseDouble(rebateStr);
        } catch (NumberFormatException  | NullPointerException e) {
            e.printStackTrace(); // Handle parsing exception if necessary
        }
        // Convert rebate percentage and total gross amount to BigDecimal
        BigDecimal rebatePercent = BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100.0));


// Calculate rebateAmount with proper precision
        BigDecimal rebateAmount = totalGrossAmount.multiply(rebatePercent);

// Optionally, if you need `rebatePercent` as a double for display purposes
        double rebatePercentDouble = rebatePercent.doubleValue();


        amountPayableAfterRebate = totalGrossAmount.subtract(rebateAmount);
        escposPrinter.printText("--------------------------------------------------------------------\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Items:              %d\r\n", itemsCount-1), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Qty:                %d\r\n", totalQty), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total NET Amount:         AED %.2f\r\n", totalNetAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total VAT Amount:         AED %.2f\r\n", totalVatAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Gross Amount:       AED %.2f\r\n", totalGrossAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        //   escposPrinter.printText(String.format("Gross  Amount Payable:    AED %.2f\n",amountPayableAfterRebate), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Sales Person Name:        %s\r\n",name), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Credit Note No:           %s\r\n", credID), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print signatures
        escposPrinter.lineFeed(3);
        escposPrinter.printText("-----------------------------\t\t-----------------------------\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.lineFeed(1);
        escposPrinter.printText("Buyer's Signature\t\t\t\tSeller's Signature\t\t\t\t\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.lineFeed(4);

        return 0;
    }



    @SuppressLint("DefaultLocale")
    public int ReturnProformaPrint_Sample_4() throws IOException {
        System.out.println("the return list in return sample: "+newSaleBeanLists2);
        ESCPOSPrinter escposPrinter = new ESCPOSPrinter();
        sts = check_status.PrinterStatus(escposPrinter);
        if (sts != ESCPOSConst.LK_SUCCESS) return sts;


        // Sample values from your existing data
        int itemCount = newSaleBeanLists2.size();


        // Print header
        escposPrinter.printText(centerAlignText("Malta Quality Foodstuff Trading LLC") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Office 401-02,Eldorado Building Humaid Alhasm Al Rumaithi") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("65st,Al Danah") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Tell : +971 2 583 2166") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("PO Box No 105689, Abu Dhabi, United Arab Emirates") +"", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("TRN: 100014706400003") + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Date: " + getCurrentDate() + " "+"Time: " + getCurrentTime()) + "", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("PROFORMA CREDIT NOTE") + "\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print customer details
        escposPrinter.printText("\r"+"CUSTOMER NAME: " + customername + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("ADDRESS:"+ customeraddress+"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("BRANCH: " + outletname + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("TRN: " + trn + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("EMIRATE:"+ emirate  +"\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("VEHICLE NO: " + vehiclenum +  "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("ROUTE: " + route + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("SALESMAN: " + name + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("REF.NO: " + refrenceno + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("COMMENTS: " + Comments + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

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

            NewSaleBean item = newSaleBeanLists2.get(i);
            String qty = item.getApprovedQty();
            String plucode="";
            if(newSaleBeanLists2.get(i).getPlucode().equals(null)|| newSaleBeanLists2.get(i).getPlucode().isEmpty()|| newSaleBeanLists2.get(i).getPlucode()==null){
                plucode="";
            }else{
                plucode=newSaleBeanLists2.get(i).getPlucode();
            }
            BigDecimal qtyValue = BigDecimal.valueOf(Double.parseDouble(qty));
            String qtyWithUom = String.format("%.2f %s", qtyValue, item.getUom());
            BigDecimal priceValue = BigDecimal.valueOf(Double.parseDouble(item.getSellingPrice())).setScale(2, RoundingMode.HALF_UP);
            BigDecimal netAmount = qtyValue.multiply( priceValue);
            BigDecimal vatAmount = netAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal grossAmount = netAmount.add( vatAmount);

            totalQty +=qtyValue.intValue();
            totalNetAmount =totalNetAmount.add( netAmount).setScale(2, RoundingMode.HALF_UP);
            totalVatAmount =totalVatAmount.add( vatAmount).setScale(2, RoundingMode.HALF_UP);
            totalGrossAmount =totalGrossAmount.add( grossAmount).setScale(2, RoundingMode.HALF_UP);

// Define a width for the item count, assuming maximum 99 items (2 digits)
            int itemCountWidth = 2;
            String format = "%-" + itemCountWidth + "d. %s\r\n";

// Assuming LKPrint.LK_ALIGNMENT_LEFT is a constant for left alignment
            escposPrinter.printText(
                    String.format(format, itemsCount++, item.getProductName() + " " + item.getItemCode() + " " + plucode),
                    LKPrint.LK_ALIGNMENT_LEFT,
                    LKPrint.LK_FNT_DEFAULT,
                    LKPrint.LK_TXT_1WIDTH
            );
            escposPrinter.printText(String.format("%-4s %-4s %-4s %-6.2f %-6s %-6.2f %-3s %-7.2f %-7.2f\n\r", " ", item.getBarcode(), qtyWithUom, priceValue, "0.00", netAmount, "5%", vatAmount, grossAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        }

        // Print totals
        String rebateStr = getCustomerRebate(customercode);

        double rebate = 0.0;
        try {
            rebate = Double.parseDouble(rebateStr);
        } catch (NumberFormatException  | NullPointerException e) {
            e.printStackTrace(); // Handle parsing exception if necessary
        }
        // Convert rebate percentage and total gross amount to BigDecimal
        BigDecimal rebatePercent = BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100.0));


// Calculate rebateAmount with proper precision
        BigDecimal rebateAmount = totalGrossAmount.multiply(rebatePercent);

// Optionally, if you need `rebatePercent` as a double for display purposes
        double rebatePercentDouble = rebatePercent.doubleValue();


        amountPayableAfterRebate = totalGrossAmount.subtract(rebateAmount);
        escposPrinter.printText("--------------------------------------------------------------------\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Items:              %d\r\n", itemsCount-1), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Qty:                %d\r\n", totalQty), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total NET Amount:         AED %.2f\r\n", totalNetAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total VAT Amount:         AED %.2f\r\n", totalVatAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Gross Amount:       AED %.2f\r\n", totalGrossAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        //   escposPrinter.printText(String.format("Gross  Amount Payable:    AED %.2f\n",amountPayableAfterRebate), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Sales Person Name:        %s\r\n",name), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

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
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        // Check if current time is after 10:30 PM but before midnight
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 21 && calendar.get(Calendar.MINUTE) > 30) ||
                (calendar.get(Calendar.HOUR_OF_DAY) >= 22)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        return dateFormat.format(calendar.getTime());
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();

        // Check if current time is after 10:30 PM but before midnight
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 21 && calendar.get(Calendar.MINUTE) > 30) ||
                (calendar.get(Calendar.HOUR_OF_DAY) >= 22)) {
            return "12:00 AM"; // Set time to "12:00 AM"
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a"); // Format in 12-hour format with AM/PM
        return formatter.format(calendar.getTime());
    }

}
