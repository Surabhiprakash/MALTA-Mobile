package com.malta_mqf.malta_mobile.ZebraPrinter;

import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.Model.DeliveryHistoryDeatilsBean;
import com.malta_mqf.malta_mobile.NewOrderInvoice;
import com.malta_mqf.malta_mobile.NewSaleActivity;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditbeanList;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.comments;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.deliveryHistoryDetailsList;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.customername;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.invNoOrOrderId;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.reference;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.returnTrn;
import static com.malta_mqf.malta_mobile.MainActivity.name;
import static com.malta_mqf.malta_mobile.MainActivity.vehiclenum;
import static com.malta_mqf.malta_mobile.NewSaleActivity.invoiceNumber;
import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.route;
import static com.malta_mqf.malta_mobile.NewSaleInvoice.trn_no;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.credId;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.customerName;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.returnComments;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.returnrefrence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.ExpandableListView;


import androidx.annotation.NonNull;

import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.R;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class ReceiptDemo2 extends ConnectionScreenDeliveryHistory implements DiscoveryHandler {


    private UIHelper helper = new UIHelper(this);
    private boolean sendData = true;
    String orderId, returnComments, returnrefrence,TRN_NO,outletname,customerCode,customeraddress,outletaddress,emirate,customername;
    public static BigDecimal totalNetAmount, totalVatAmount, totalGrossAmt, NET, ITEM_VAT_AMT, ITEMS_GROSS;
    public static int totalQty;
    public static List<String> listNET = new LinkedList<>();
    public static List<String> listVAT = new LinkedList<>();
    public static List<String> listVatAmnt = new LinkedList<>();
    public static List<String> listGROSS = new LinkedList<>();
    public static List<String> listDISC = new LinkedList<>();

    SubmitOrderDB submitOrderDB;
    AllCustomerDetailsDB customerDetailsDB;
    Connection printerConnection = null;
    int itemcount=0;
    static List<DeliveryHistoryDeatilsBean> newSaleBeanListsss = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testButton = (Button) this.findViewById(R.id.testButton);
        mExpListView = (ExpandableListView) findViewById(android.R.id.list);
        customerDetailsDB=new AllCustomerDetailsDB(this);
        Intent intent = getIntent();
        outletname=getIntent().getStringExtra("outletname");
        customerCode=getIntent().getStringExtra("customerCode");
        customeraddress=getIntent().getStringExtra("customeraddress");
        customername=getIntent().getStringExtra("customername");
        outletaddress=getIntent().getStringExtra("address");
        emirate=getIntent().getStringExtra("emirate");
        vehiclenum=getIntent().getStringExtra("vehiclenum");
        name=getIntent().getStringExtra("name");
       /* reference = intent.getStringExtra("referenceNo");
        comments = intent.getStringExtra("comments");
        returnrefrence = intent.getStringExtra("refrence");
        returnComments = intent.getStringExtra("comment");*/
        if(outletaddress==null  || outletaddress.isEmpty()){
            outletaddress="DUBAI DESIGN DISTRICT";
        }
        if(emirate==null){
            emirate="DUBAI";
        }
        if (returnrefrence == null) {
            returnrefrence = reference;
        }
        if (returnComments == null) {
            returnComments = comments;
        }



        testButton.setText("Print Invoice");

      /*  Button printAsManyJobsButton = new Button(this);
       // printAsManyJobsButton.setText("Print Using Multiple Jobs");

        LinearLayout connection_screen_layout = (LinearLayout) findViewById(R.id.connection_screen_layout);
        int index = connection_screen_layout.indexOfChild(testButton);

        connection_screen_layout.addView(printAsManyJobsButton, index + 1);

        printAsManyJobsButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                sendData = false;
                performTestWithManyJobs();
            }
        });*/
        newSaleBeanListsss = new LinkedList<>(deliveryHistoryDetailsList);


        if (customername == null) {
            customername = customerName;
        }
        if (invoiceNumber == null) {
            invoiceNumber = invNoOrOrderId;
        }

        if(returnTrn==null){
            TRN_NO="000000000000000";
        }else {
            TRN_NO=returnTrn;
        }
        if (customeraddress.length() > 30) {
            // Find the last space within the first 30 characters
            int lastSpace = customeraddress.substring(0, 30).lastIndexOf(' ');

            // If there's a space within the first 30 characters, break there
            if (lastSpace != -1) {
                customeraddress = customeraddress.substring(0, lastSpace) + "\r\n"+" " + customeraddress.substring(lastSpace + 1);
            } else {
                // If there's no space, break at 30 characters
                customeraddress = customeraddress.substring(0, 30) + "\r\n" + customeraddress.substring(30);
            }
        }
    }

    @Override
    public void performTest() {
        executeTest(false);
    }

    @Override
    public void performTestPerforma() {

    }

    public void performTestWithManyJobs() {
        executeTest(true);
    }

    public void executeTest(final boolean withManyJobs) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                connectAndSendLabel(withManyJobs);
                Looper.loop();
                Looper.myLooper().quit();
                sendData = true;
            }
        }).start();

    }

    private void connectAndSendLabel(final boolean withManyJobs) {
        if (isBluetoothSelected() == false) {
            printerConnection = new BluetoothConnection(getMacAddressFieldText());


        } else {
            printerConnection = new BluetoothConnection(getMacAddressFieldText());
        }
        try {
            helper.showLoadingDialog("Connecting...");
            printerConnection.open();

            ZebraPrinter printer = null;

            if (printerConnection.isConnected()) {
                try {
                    printer = ZebraPrinterFactory.getInstance(printerConnection);
                } catch (ZebraPrinterLanguageUnknownException e) {
                    // Handle unknown printer language by assuming ZPL
                    helper.showErrorDialogOnGuiThread("Could not detect printer language, assuming ZPL");
                    printer = null;
                }

                if (printer != null) {
                    if (withManyJobs) {
                        sendTestLabelWithManyJobs(printerConnection);
                    } else {
                        sendTestLabel();
                    }
                } else {
                    // Handle case when printer is null
                    helper.showErrorDialogOnGuiThread("Printer is not available");
                }
                printerConnection.close();
                saveSettings();
            }

        } catch (ConnectionException e) {
            helper.showErrorDialogOnGuiThread("Connection error: " + e.getMessage());
        } finally {
            helper.dismissLoadingDialog();
        }
    }


    private void sendTestLabel() {
        try {
            byte[] configLabel = createZplReceipt().getBytes();
            printerConnection.write(configLabel);
            DemoSleeper.sleep(1500);
            if (printerConnection instanceof BluetoothConnection) {
                DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        }
    }

    private void sendTestLabelWithManyJobs(Connection printerConnection) {
        try {
            sendZplReceipt(printerConnection);
        } catch (ConnectionException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        }

    }

    private void saveSettings() {
        SettingsHelper.saveBluetoothAddress(ReceiptDemo2.this, getMacAddressFieldText());

    }

    @SuppressLint("Range")
    private String getCustomerRebate(String customerCode) {
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

    private String createZplReceipt() {
        listDISC.clear();
        listGROSS.clear();
        listNET.clear();
        listVAT.clear();
        listVatAmnt.clear();
        totalNetAmount = BigDecimal.ZERO;
        totalVatAmount = BigDecimal.ZERO;
        totalGrossAmt = BigDecimal.ZERO;
        totalQty = 0;
        itemcount=0;
        // Sample values
        int itemCount = newSaleBeanListsss.size();  // Set the number of items

        StringBuilder body = new StringBuilder();
        String header1 = centerAlignText("Malta Quality Foodstuff Trading LLC") + "\r\n"
                + centerAlignText("Office 401-02,Eldorado Building Humaid Alhasm Al Rumaithi")
                + centerAlignText("65st,Al Danah")
                + centerAlignText("Tell : +971 2 583 2166")
                + centerAlignText("PO Box No 105689,Abu Dhabi,United Arab Emirates")
                + centerAlignText("TRN: 100014706400003")
                + centerAlignText("Invoiced Date: " + convertDate(newSaleBeanListsss.get(0).getDeliveryDateTime().substring(0,10))+ "  " + "Invoiced Time: " + newSaleBeanListsss.get(0).getDeliveryDateTime().substring(11,16))
                + centerAlignText("Re-print Date: " + getCurrentDate() + "  " + "Re-print Time: " + getCurrentTime())
                + centerAlignText("TAX INVOICE")
                + centerAlignText("Invoice No: " + invNoOrOrderId) + "\n";



        int referenceLength = reference.length();
        int spaceToAdd = Math.max(0, 10 - referenceLength); // Calculate the number of spaces to add to make the reference length 10
        StringBuilder spacesBuilder = new StringBuilder();
        for (int i = 0; i < spaceToAdd; i++) {
            spacesBuilder.append(" ");
        }
        String spaces = spacesBuilder.toString();
        // Create a string with the required number of spaces

        String header2 = "\r"
                + " CUSTOMER NAME:"+ customername+"\r\n"
                + " ADDRESS: " +customeraddress+"\r\n"
                + " BRANCH: " +outletname+"\r\n"
                + " TRN: "+ TRN_NO + "\r\n"
                + " EMIRATE: "+ emirate +  "\r\n"
                + " VEHICLE NO: "+ vehiclenum + "\r\n"
                + " ROUTE: "+ route + "\r\n"
                + " SALESMAN:" + name+"\r\n"
                + " REF.NO: " + returnrefrence + spaces + "\r\n"
                + " COMMENTS: " + returnComments + "\r\n";

        body.append(header1).append(header2);
// Add column headings
        body.append("---------------------------------------------------------------------\r\n");
        body.append(" ITEM   \t\t BARCODE \t\t   QTY \t\t  PRICE  \t\t  DISC \t\t   NET VAT % \t  VAT AMT \t GROSS").append("\r\n");
        body.append("---------------------------------------------------------------------\r");
// Auto-incrementing Sl.no and adding values
        for (int i = 0; i < itemCount; i++) {
            if (!Objects.equals(newSaleBeanListsss.get(i).getDelqty(), "0") || newSaleBeanListsss.get(i).getDelqty().isEmpty()) {
                itemcount++;
                String plucode = "";
                if (newSaleBeanListsss.get(i).getPlucode().equals(null) || newSaleBeanListsss.get(i).getPlucode().isEmpty() || newSaleBeanListsss.get(i).getPlucode() == null || "NA".equals(newSaleBeanListsss.get(i).getPlucode())) {
                    plucode = "";
                } else {
                    plucode = newSaleBeanListsss.get(i).getPlucode();
                }
                body.append("\r").append(itemcount).append(". ").append(newSaleBeanListsss.get(i).getItemname()).append(" \t").append(newSaleBeanListsss.get(i).getItemCode()).append(" \t").append(plucode).append("\r\n");
                body.append("      " + newSaleBeanListsss.get(i).getBarcode()).append(" \t");

                // Check if deliveryQty is null or "0", if yes, use approvedQty, else use deliveryQty
                String qty = newSaleBeanListsss.get(i).getDelqty();
                String uom = newSaleBeanListsss.get(i).getUom();
                int aValue = Integer.parseInt(qty);
                if (aValue <= 9) {
                    body.append("  " + qty).append(" " + uom + "\t");
                } else {
                    body.append(" " + qty).append(" " + uom + "\t");
                }


                // Check if sellingPrice is not null before using it
                String sellingPrice = newSaleBeanListsss.get(i).getPrice() != null ? newSaleBeanListsss.get(i).getPrice() : "0";

                String discount = newSaleBeanListsss.get(i).getDisc();
                double discountValue = 0.0;

                try {
                    discountValue = Double.parseDouble(discount);
                } catch (NumberFormatException e) {
                    // Handle parsing exception if necessary
                    e.printStackTrace();
                }
                BigDecimal doubleValue = BigDecimal.valueOf(Double.parseDouble(sellingPrice)).setScale(2, RoundingMode.HALF_UP);
                int valuePrice = doubleValue.intValue();
                if (valuePrice >= 1000) {
                    body.append(sellingPrice).append("  \t");
                    body.append(discountValue).append("  \t");
                    listDISC.add(String.valueOf(discountValue));
                } else if (valuePrice >= 100 && valuePrice <= 999) {
                    body.append(" " + sellingPrice).append("  \t");
                    body.append(discountValue).append("  \t");
                    listDISC.add(String.valueOf(discountValue));
                } else if (valuePrice >= 10 && valuePrice <= 99) {
                    body.append("  " + sellingPrice).append("  \t");
                    body.append(discountValue).append("  \t");
                    listDISC.add(String.valueOf(discountValue));
                } else {
                    body.append("   " + sellingPrice).append("  \t");
                    body.append(discountValue).append("  \t");
                    listDISC.add(String.valueOf(discountValue));
                }
            /*if(valuePrice>=10){
                body.append(" "+sellingPrice).append("  \t");
                body.append(" 0.00  \t");

            }else if(valuePrice>=100){
                body.append(sellingPrice).append(" ");
                body.append("0.00  \t");


            }else {
                body.append("  " +sellingPrice).append("  \t");
                body.append("0.00  \t");

            }*/


                if (newSaleBeanListsss.get(i).getDelqty() == null) {
                    System.out.println(newSaleBeanListsss.get(i).getDelqty());
                    BigDecimal formattedNET = BigDecimal.valueOf(Float.parseFloat(newSaleBeanListsss.get(i).getDelqty()) * (Float.parseFloat(sellingPrice))).setScale(2, RoundingMode.HALF_UP);//here approvedqty means returnqty
                    NET = formattedNET;
                    listNET.add(String.valueOf(NET));
                } else {
                    BigDecimal formattedNET = BigDecimal.valueOf(Float.parseFloat(newSaleBeanListsss.get(i).getDelqty()) * (Float.parseFloat(sellingPrice))).setScale(2, RoundingMode.HALF_UP);
                    NET = formattedNET;
                    listNET.add(String.valueOf(NET));
                }


                BigDecimal doubleValueNet = BigDecimal.valueOf(Double.parseDouble(String.valueOf(NET))).setScale(2, RoundingMode.HALF_UP);
                String decimalStr = String.valueOf(NET);
                int decimalValue = decimalStr.indexOf(".");
                String decimalStr1 = decimalStr.substring(decimalValue + 1);
                int valuePriceNet = doubleValueNet.intValue();
                if (valuePriceNet >= 1000) {
                    body.append(NET).append("\t");
                    if (decimalStr1.length() > 1) {
                        body.append(" 5%  \t");
                    } else {
                        body.append("  5%  \t");
                    }
                } else if (valuePriceNet >= 100 && valuePriceNet <= 999) {
                    body.append(" " + NET).append("\t");
                    if (decimalStr1.length() > 1) {
                        body.append(" 5%  \t");
                    } else {
                        body.append("  5%  \t");
                    }
                } else if (valuePriceNet >= 10 && valuePriceNet <= 99) {
                    body.append("  " + NET).append("\t");
                    if (decimalStr1.length() > 1) {
                        body.append(" 5%  \t");
                    } else {
                        body.append("  5%  \t");
                    }
                } else {
                    body.append("   " + NET).append("\t");
                    if (decimalStr1.length() > 1) {
                        body.append(" 5%  \t");
                    } else {
                        body.append("  5%  \t");
                    }
                }

            /*if(valuePriceNet>=100 && valuePrice >10){
                body.append(NET).append("\t");
                body.append(" 5%  \t");
            }
            else if(valuePriceNet>=10  && valuePriceNet <= 99  && valuePrice<=9 ){
                body.append("  "+NET).append("\t");
                body.append(" 5%  \t");
            }
            else{
                body.append(NET).append(" \t");
                body.append(" 5%  \t");
            }*/


                // body.append(NET).append("   \t\t");


                // body.append("5%   \t\t");
                listVAT.add("5");
                ITEM_VAT_AMT = NET.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
                listVatAmnt.add(String.format("%.2f", ITEM_VAT_AMT));
                int itemVatAmount = ITEM_VAT_AMT.intValue();
                String itemVatAmountStr = String.format("%.2f", ITEM_VAT_AMT);
                if (itemVatAmount >= 100) {
                    body.append(itemVatAmountStr).append("  \t");
                } else if (itemVatAmount >= 10 && itemVatAmount <= 99) {
                    body.append(" " + itemVatAmountStr).append("  \t");
                } else {
                    body.append("  " + itemVatAmountStr).append("  \t");
                }
           /* if(valuePrice>10 && valuePriceNet>10){
                body.append(String.format("%.2f", ITEM_VAT_AMT)).append("  \t");
            }else{
                body.append(String.format("%.2f", ITEM_VAT_AMT)).append("   \t");

            }*/
                ITEMS_GROSS = ITEM_VAT_AMT.add(NET);

                int grossValue = ITEMS_GROSS.intValue();
                String str = String.format("%.2f", ITEMS_GROSS);

                if (grossValue >= 1000) {
                    body.append(str).append(" \t");
                } else if (grossValue >= 100 && grossValue <= 999) {
                    body.append(" " + str).append(" \t");

                } else if (grossValue >= 10 && grossValue <= 99) {
                    body.append("  " + str).append(" \t");
                } else {
                    body.append("   " + str).append(" \t");
                }
                listGROSS.add(String.format("%.2f", ITEMS_GROSS));
            }

            }
            body.append("\r\n");

            body.append(" --------------------------------------------------------------------\r\n");


// Calculate and append total quantity
            //totalQty = String.valueOf(0);
            for (int i = 0; i < itemCount; i++) {
                String qty = newSaleBeanListsss.get(i).getDelqty();
                totalQty += Double.parseDouble(qty);
            }
            System.out.println("totalqty:" + totalQty);
            body.append("\r\n").append(" Total Items:\t\t             ").append(itemcount);
            body.append("\r\n").append(" Total Qty:\t\t\t               ").append((int) totalQty).append("\r\n");

// Calculate and append total net amount
            String rebateStr = getCustomerRebate(customerCode);

            double rebate = 0.0;
            try {
                rebate = Double.parseDouble(rebateStr);
            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace(); // Handle parsing exception if necessary
            }

            for (int i = 0; i < itemCount; i++) {
                String qty = newSaleBeanListsss.get(i).getDelqty();
                BigDecimal qtyValue = BigDecimal.valueOf(Double.parseDouble(qty));
                BigDecimal price = BigDecimal.valueOf(Double.parseDouble(newSaleBeanListsss.get(i).getPrice() != null ? newSaleBeanListsss.get(i).getPrice() : "0")).setScale(2, RoundingMode.HALF_UP);

                // Net amount for this item
                BigDecimal netAmount = qtyValue.multiply(price);

                // VAT amount for this item (5% of net amount)
                BigDecimal vatAmount = netAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);

                // Gross amount for this item
                BigDecimal grossAmount = netAmount.add(vatAmount);

                // Accumulate total amounts
                totalNetAmount = totalNetAmount.add(netAmount).setScale(2, RoundingMode.HALF_UP);
                totalVatAmount = totalVatAmount.add(vatAmount).setScale(2, RoundingMode.HALF_UP);
                totalGrossAmt = totalGrossAmt.add(grossAmount).setScale(2, RoundingMode.HALF_UP);
            }


            // Convert rebate percentage and total gross amount to BigDecimal
            BigDecimal rebatePercent = BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100.0));


// Calculate rebateAmount with proper precision
            BigDecimal rebateAmount = totalGrossAmt.multiply(rebatePercent).setScale(2, RoundingMode.HALF_UP);

// Optionally, if you need `rebatePercent` as a double for display purposes
            double rebatePercentDouble = rebatePercent.doubleValue();


            BigDecimal amountPayableAfterRebate = totalGrossAmt.subtract(rebateAmount);

            body.append(" Total NET Amount:        ").append("AED ").append(totalNetAmount.setScale(2, RoundingMode.HALF_UP)).append("\r\n");
            body.append(" Total VAT Amount:        ").append("AED ").append(totalVatAmount.setScale(2, RoundingMode.HALF_UP)).append("\r\n");
            body.append(" Total Gross Amount:      ").append("AED ").append(totalGrossAmt.setScale(2, RoundingMode.HALF_UP)).append("\r\n");
            //   body.append(" Gross Amount Payable:    ").append("AED ").append(String.format("%.2f", amountPayableAfterRebate)).append("\r\n");
            body.append(" Sales Person Name:       ").append(name).append("\r\n");
            body.append(" Invoice No:              ").append(invNoOrOrderId).append("\r\n");
            body.append("\r\n");
            body.append("\r\n");
            body.append("\r\n");
            body.append("\r\n");

            body.append(" --------------------------------------------------------------------\r\n");
            body.append("\r\n");
            body.append("\r\n");
            body.append("\r\n");

            body.append(" Buyer's Signature                          \t\t").append("Seller's Signature\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");

            body.append("\r\n");
            body.append("\r\n");
            body.append("\r\n");
            body.append("\r\n");

            return body.toString();

    }


    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        // Check if current time is after 10:30 PM but before midnight
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 22 && calendar.get(Calendar.MINUTE) > 30) ||
                (calendar.get(Calendar.HOUR_OF_DAY) == 23)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        return dateFormat.format(calendar.getTime());
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();

        // Check if current time is after 10:30 PM but before midnight
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 22 && calendar.get(Calendar.MINUTE) > 30) ||
                (calendar.get(Calendar.HOUR_OF_DAY) == 23)) {
            return "12:00 AM"; // Set time to "12:00 AM"
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a"); // Format in 12-hour format with AM/PM
        return formatter.format(calendar.getTime());
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
    private String centerAlignText(String text) {
        int maxLength = 70; // Maximum length of the line
        int paddingLength = (maxLength - text.length()) / 2;
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < paddingLength; i++) {
            padding.append(" ");
        }
        return padding.toString() + text + padding.toString();
    }

    private void sendZplReceipt(Connection printerConnection) throws ConnectionException {
        String zplReceipt = createZplReceipt();
        printerConnection.write(zplReceipt.getBytes());
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

}
