package com.malta_mqf.malta_mobile.SewooPrinter;


import static com.malta_mqf.malta_mobile.MainActivity.name;
import static com.malta_mqf.malta_mobile.MainActivity.vehiclenum;
import static com.malta_mqf.malta_mobile.ReturnHistoryDetails.returnTrn;
import static com.malta_mqf.malta_mobile.ReturnHistoryDetails.route;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity.Comments;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity.creditIdNo;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity.customeraddress;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity.customername;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity.emirate;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity.outletname;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity.refrenceno;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.DeliveryHistoryDeatilsBean;
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

public class ReturnHistorySamplePrint extends AppCompatActivity {
    public static List<DeliveryHistoryDeatilsBean> newSaleBeanLists = new LinkedList<>();
    public static BigDecimal totalNetAmount, totalVatAmount, totalGrossAmount;
    public static int totalQty = 0;
    private final char ESC = ESCPOS.ESC;
    private final char LF = ESCPOS.LF;
    SubmitOrderDB submitOrderDB;
    AllCustomerDetailsDB customerDetailsDB;
    private final ESCPOSPrinter escposPrinter;
    private final CheckPrinterStatus check_status;
    private int sts;

    public ReturnHistorySamplePrint() {
        escposPrinter = new ESCPOSPrinter();    //Default = English.
        check_status = new CheckPrinterStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private String centerAlignText(String text) {
        int maxLength = 70; // Maximum length of the line
        int paddingLength = (maxLength - text.length()) / 2;
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < paddingLength; i++) {
            padding.append(" ");
        }
        return padding + text + padding;
    }

    @SuppressLint("DefaultLocale")
    public int ReturnHistorySample_Print() throws IOException {
        ESCPOSPrinter escposPrinter = new ESCPOSPrinter();
        sts = check_status.PrinterStatus(escposPrinter);
        if (sts != ESCPOSConst.LK_SUCCESS) return sts;


        // Sample values from your existing data
        int itemCount = newSaleBeanLists.size();


        // Print header
        escposPrinter.printText(centerAlignText("Malta Quality Foodstuff Trading LLC") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Office 401-02,Eldorado Building Humaid Alhasm Al Rumaithi") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("65st,Al Danah") + "\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Tell : +971 2 583 2166"), LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("PO Box No 105689, Abu Dhabi, United Arab Emirates"), LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("TRN: 100014706400003"), LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Returned Date: " + convertDate(newSaleBeanLists.get(0).getDeliveryDateTime().substring(0, 10)) + " " + "Returned Time: " + newSaleBeanLists.get(0).getDeliveryDateTime().substring(11, 16)), LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Re-print Date: " + getCurrentDate() + " " + "Re-print Time: " + getCurrentTime()), LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("TAX CREDIT NOTE"), LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(centerAlignText("Credit Note No: " + creditIdNo) + "\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print customer details
        escposPrinter.printText("\r" + "CUSTOMER NAME: " + customername + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("ADDRESS: " + customeraddress + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("BRANCH: " + outletname + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("TRN: " + returnTrn + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("EMIRATE: " + emirate + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText("VEHICLE NO: " + vehiclenum + "\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
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
        int itemsCount = 1;
        // Loop through items
        for (int i = 0; i < itemCount; i++) {
            if (!newSaleBeanLists.get(i).getDelqty().equals("0") || newSaleBeanLists.get(i).getDelqty().isEmpty()) {
                DeliveryHistoryDeatilsBean item = newSaleBeanLists.get(i);
                String qty = item.getDelqty();
                String plucode = "";
                if (newSaleBeanLists.get(i).getPlucode().equals(null) || newSaleBeanLists.get(i).getPlucode().isEmpty() || newSaleBeanLists.get(i).getPlucode() == null) {
                    plucode = "";
                } else {
                    plucode = newSaleBeanLists.get(i).getPlucode();
                }
                BigDecimal qtyValue = BigDecimal.valueOf(Double.parseDouble(qty));
                String qtyWithUom = String.format("%.2f %s", qtyValue, item.getUom());
                BigDecimal priceValue = BigDecimal.valueOf(Double.parseDouble(item.getPrice())).setScale(2, RoundingMode.HALF_UP);
                BigDecimal netAmount = qtyValue.multiply(priceValue).setScale(2, RoundingMode.HALF_UP);
                BigDecimal vatAmount = netAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal grossAmount = netAmount.add(vatAmount).setScale(2, RoundingMode.HALF_UP);

                totalQty += qtyValue.intValue();
                totalNetAmount = totalNetAmount.add(netAmount);
                totalVatAmount = totalVatAmount.add(vatAmount);
                totalGrossAmount = totalGrossAmount.add(grossAmount);

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
       /* String rebateStr = getCustomerRebate(customercode);

        double rebate = 0.0;
        try {
            rebate = Double.parseDouble(rebateStr);
        } catch (NumberFormatException |NullPointerException e) {
            e.printStackTrace(); // Handle parsing exception if necessary
        }
        double rebateAmount = (rebate / 100.0) * totalGrossAmount;
        double rebatePercent = rebate;

        double amountPayableAfterRebate = totalGrossAmount - rebateAmount;*/
        escposPrinter.printText("--------------------------------------------------------------------\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Items:              %d\r\n", itemsCount - 1), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Qty:                %d\r\n", totalQty), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total NET Amount:         AED %.2f\r\n", totalNetAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total VAT Amount:         AED %.2f\r\n", totalVatAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Total Gross Amount:       AED %.2f\r\n", totalGrossAmount), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        // escposPrinter.printText(String.format("Gross  Amount Payable:    AED %.2f\n",amountPayableAfterRebate), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Sales Person Name:        %s\r\n", name), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.printText(String.format("Credit Note No:           %s\r\n", creditIdNo), LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);

        // Print signatures
        escposPrinter.lineFeed(3);
        escposPrinter.printText("-----------------------------\t\t-----------------------------\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.lineFeed(1);
        escposPrinter.printText("Buyer's Signature\t\t\t\tSeller's Signature\t\t\t\t\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
        escposPrinter.lineFeed(4);

        return 0;
    }
  /*  @SuppressLint("Range")
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
    }*/

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        return dateFormat.format(new Date());
    }

    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        return timeFormat.format(new Date());
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
}
