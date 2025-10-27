package com.malta_mqf.malta_mobile.ZebraPrinter;

import static com.malta_mqf.malta_mobile.NewSaleInvoice.Comments;
import static com.malta_mqf.malta_mobile.NewSaleInvoice.orderToInvoice;
import static com.malta_mqf.malta_mobile.NewSaleInvoice.refrenceno;
import static com.malta_mqf.malta_mobile.NewSaleInvoice.trn_no;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.customerName;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;

import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class NewSaleReceiptDemo extends ConnectionScreen implements DiscoveryHandler {


    private UIHelper helper = new UIHelper(this);
    private boolean sendData = true;

    private ALodingDialog aLodingDialog;
    String  reference, comments, returnComments, returnrefrence,TRN_NO,outletname,outletaddress,emirate,vehiclenum,name,route;
    public static BigDecimal totalNetAmount, totalVatAmount, totalGrossAmt, NET, ITEM_VAT_AMT, ITEMS_GROSS,amountPayableAfterRebate=BigDecimal.ZERO;
    public static int totalQty;
    public static String userID,vanID;
    public static String orderId,newsaleoutletid,invoiceNumber,newsalecustomerCode;
    private String customername,customeraddress;
    public static List<String> listNET = new LinkedList<>();
    public static List<String> listVAT = new LinkedList<>();
    public static List<String> listVatAmnt = new LinkedList<>();
    public static List<String> listGROSS = new LinkedList<>();
    public static List<String> listDISC = new LinkedList<>();

    SubmitOrderDB submitOrderDB;
    AllCustomerDetailsDB customerDetailsDB;
    Connection printerConnection = null;
   // List<String> customerNamearr= Arrays.asList("Bandidos Retial LLC","Careem Network General Trading LLC","Delivery Hero Stores DB LLC");

    static List<ShowOrderForInvoiceBean> newSaleBeanListsss = new LinkedList<>();
    int itemcount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testButton = (Button) this.findViewById(R.id.testButton);
        mExpListView = (ExpandableListView) findViewById(android.R.id.list);
        aLodingDialog=new ALodingDialog(this);
        customerDetailsDB=new AllCustomerDetailsDB(this);
        Intent intent = getIntent();
       /* reference = intent.getStringExtra("referenceNo");
        comments = intent.getStringExtra("comments");
        returnrefrence = intent.getStringExtra("refrence");
        returnComments = intent.getStringExtra("comment");*/
        outletname=intent.getStringExtra("outletname");
        newsalecustomerCode=intent.getStringExtra("customerCode");
        customeraddress=intent.getStringExtra("customeraddress");
        customername=intent.getStringExtra("customername");
        outletaddress=intent.getStringExtra("address");
        emirate=intent.getStringExtra("emirate");
        orderId=intent.getStringExtra("orderid");
        newsaleoutletid=intent.getStringExtra("outletid");
        invoiceNumber=intent.getStringExtra("invoiceNo");
        vehiclenum=intent.getStringExtra("vehiclenum");
        name=intent.getStringExtra("name");
        route=intent.getStringExtra("route");
        userID=getIntent().getStringExtra("userid");
        vanID=getIntent().getStringExtra("vanid");
        reference=getIntent().getStringExtra("referenceNo");
        comments=getIntent().getStringExtra("Comments");
        if(outletaddress==null || outletaddress.isEmpty()){
            outletaddress="DUBAI DESIGN DISTRICT";
        }
        if(emirate==null){
            emirate="DUBAI";
        }
        if (reference == null) {
            reference = refrenceno;
        }
        if (comments == null) {
            comments = Comments;
        }
        if(trn_no==null){
            TRN_NO="000000000000000";
        }else {
            TRN_NO=trn_no;
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
        newSaleBeanListsss = new LinkedList<>(orderToInvoice);


        if (customername == null) {
            customername = customerName;
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
        executeTestPerforma(false);
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
    public void executeTestPerforma(final boolean withManyJobs) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                connectAndSendLabelPerforma(withManyJobs);
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

    private void connectAndSendLabelPerforma(final boolean withManyJobs) {
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
                        sendTestLabelWithManyJobsPerforma(printerConnection);
                    } else {
                        sendTestLabelPerforma();
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
    private void sendTestLabelPerforma() {
        try {
            byte[] configLabel = createZplProformaReceipt().getBytes();
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
    private void sendTestLabelWithManyJobsPerforma(Connection printerConnection) {
        try {
            sendZplReceiptPerforma(printerConnection);
        } catch (ConnectionException e) {
            helper.showErrorDialogOnGuiThread(e.getMessage());
        }

    }
    private void saveSettings() {
        SettingsHelper.saveBluetoothAddress(NewSaleReceiptDemo.this, getMacAddressFieldText());

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


    @SuppressLint("DefaultLocale")
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
                + centerAlignText("Date: " + getCurrentDate() + "  " + "Time: " + getCurrentTime())
                + centerAlignText("TAX INVOICE")
                + centerAlignText("Invoice No: " + invoiceNumber) + "\n";


        int referenceLength = reference.length();
        int spaceToAdd = Math.max(0, 10 - referenceLength); // Calculate the number of spaces to add to make the reference length 10
        StringBuilder spacesBuilder = new StringBuilder();
        for (int i = 0; i < spaceToAdd; i++) {
            spacesBuilder.append(" ");
        }
        String spaces = spacesBuilder.toString();
        // Create a string with the required number of spaces

        String header2 = "\r"
                + " CUSTOMER NAME:" + customername + "\r\n"
                + " ADDRESS:" + customeraddress+"\r\n"
                + " BRANCH:" + outletname + "\r\n"
                + " TRN:" + TRN_NO + "\r\n"
                + " EMIRATE: " + emirate+ "\r\n"
                + " VEHICLE NO: "+ vehiclenum+  "\r\n"
                + " ROUTE: "+ route+"\r\n"
                + " SALESMAN: " + name+"\r\n"
                + " REF.NO: " + reference + spaces + "\r\n"
                + " COMMENTS: " + comments + "\r\n";

        body.append(header1).append(header2);
// Add column headings
        body.append("---------------------------------------------------------------------\r\n");
        body.append(" ITEM   \t\t BARCODE \t\t   QTY \t\t  PRICE  \t\t  DISC \t\t   NET VAT % \t  VAT AMT \t GROSS").append("\r\n");
        body.append("---------------------------------------------------------------------\r");
// Auto-incrementing Sl.no and adding values
        for (int i = 0; i < itemCount; i++) {
            if (!Objects.equals(newSaleBeanListsss.get(i).getDelqty(), "0") || newSaleBeanListsss.get(i).getDelqty().isEmpty()  ) {
                itemcount++;
                String plucode="";
             //   if(customerNamearr.contains(customername)) {
                    if ((newSaleBeanListsss.get(i).getPlucode().equals(null) || newSaleBeanListsss.get(i).getPlucode().isEmpty() || newSaleBeanListsss.get(i).getPlucode() == null)) {
                        plucode = "";
                    } else {
                        plucode = newSaleBeanListsss.get(i).getPlucode();
                    }
                //}
            body.append("\r").append(itemcount).append(". ").append(newSaleBeanListsss.get(i).getItemName()).append(" \t").append(newSaleBeanListsss.get(i).getItemCode()).append(" \t").append(plucode).append("\r\n");
            body.append("      "+newSaleBeanListsss.get(i).getBarcode()).append(" \t");

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
            String sellingPrice = newSaleBeanListsss.get(i).getSellingprice() != null ? newSaleBeanListsss.get(i).getSellingprice() : "0";


                BigDecimal doubleValue = BigDecimal.valueOf(Double.parseDouble(sellingPrice)).setScale(2, RoundingMode.HALF_UP);

// Convert to double and then cast to int
                int valuePrice = doubleValue.intValue();

                String disc = newSaleBeanListsss.get(i).getDisc();
            if (valuePrice >= 1000) {
                body.append(sellingPrice).append("  \t");
                body.append(disc).append("\t");
                listDISC.add(disc);
            } else if (valuePrice >= 100 && valuePrice <= 999) {
                body.append(" " + sellingPrice).append("  \t");
                body.append(disc).append("\t");
                listDISC.add(disc);
            } else if (valuePrice >= 10 && valuePrice <= 99) {
                body.append("  " + sellingPrice).append("  \t");
                body.append(disc).append("\t");
                listDISC.add(disc);
            } else {
                body.append("   " + sellingPrice).append("  \t");
                body.append(disc).append("\t");
                listDISC.add(disc);
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
                BigDecimal formattedNET = BigDecimal.valueOf(Float.parseFloat(newSaleBeanListsss.get(i).getDelqty()) * (Float.parseFloat(sellingPrice))).setScale(2, RoundingMode.HALF_UP);;//here approvedqty means returnqty
                NET = formattedNET;
                listNET.add(String.valueOf(NET));
            } else {
                BigDecimal formattedNET = BigDecimal.valueOf(Float.parseFloat(newSaleBeanListsss.get(i).getDelqty()) * (Float.parseFloat(sellingPrice))).setScale(2, RoundingMode.HALF_UP);;
                NET = formattedNET;
                listNET.add(String.valueOf(NET));
            }


            BigDecimal doubleValueNet = NET;
            String decimalStr = String.valueOf(NET);
            int decimalValue = decimalStr.indexOf(".");
            String decimalStr1 = decimalStr.substring(decimalValue + 1);
            int valuePriceNet =  doubleValueNet.intValue();
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
            ITEM_VAT_AMT = NET.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);;//here ;
            listVatAmnt.add(String.format("%.2f", ITEM_VAT_AMT));
            int itemVatAmount =  ITEM_VAT_AMT.intValue();
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
            ITEMS_GROSS = ITEM_VAT_AMT.add( NET);

            int grossValue =  ITEMS_GROSS.intValue();
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
            System.out.println("totalqty:" + (int)totalQty);
            body.append("\r\n").append(" Total Items:\t\t             ").append(itemcount);
            body.append("\r\n").append(" Total Qty:\t\t\t               ").append((int)totalQty).append("\r\n");

// Calculate and append total net amount


            // Initialize variables to accumulate totals


// Get rebate from database (assuming it's retrieved as a String)
            String rebateStr = getCustomerRebate(newsalecustomerCode);

            double rebate = 0.0;
            try {
                rebate = Double.parseDouble(rebateStr);
            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace(); // Handle parsing exception if necessary
            }

// Iterate through items to calculate total amounts
            for (int i = 0; i < itemCount; i++) {
                String qty = newSaleBeanListsss.get(i).getDelqty();
                BigDecimal qtyValue = BigDecimal.valueOf(Double.parseDouble(qty));
                BigDecimal price = BigDecimal.valueOf(Double.parseDouble(newSaleBeanListsss.get(i).getSellingprice() != null ? newSaleBeanListsss.get(i).getSellingprice() : "0")).setScale(2, RoundingMode.HALF_UP);;

                // Net amount for this item
                BigDecimal netAmount = qtyValue.multiply( price);

                // VAT amount for this item (5% of net amount)
                BigDecimal vatAmount = netAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);;//here ;

                BigDecimal grossAmount = netAmount .add( vatAmount);


                totalNetAmount =totalNetAmount.add( netAmount);
                totalVatAmount =totalVatAmount.add( vatAmount);
                totalGrossAmt =totalGrossAmt.add( grossAmount);
            }

        // Convert rebate percentage and total gross amount to BigDecimal
        BigDecimal rebatePercent = BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100.0));


// Calculate rebateAmount with proper precision
        BigDecimal rebateAmount = totalGrossAmt.multiply(rebatePercent).setScale(2, RoundingMode.HALF_UP);;//here ;

// Optionally, if you need `rebatePercent` as a double for

        amountPayableAfterRebate = totalGrossAmt.subtract( rebateAmount);


            body.append(" Total NET Amount:        ").append("AED ").append( totalNetAmount.setScale(2, RoundingMode.HALF_UP) ).append("\r\n");
            body.append(" Total VAT Amount:        ").append("AED ").append(totalVatAmount.setScale(2, RoundingMode.HALF_UP) ).append("\r\n");
            body.append(" Total Gross Amount:      ").append("AED ").append( totalGrossAmt.setScale(2, RoundingMode.HALF_UP)).append("\r\n");
         //   body.append(" Gross Amount Payable:    ").append("AED ").append(String.format("%.2f", amountPayableAfterRebate)).append("\r\n");
            body.append(" Sales Person Name:       ").append(name).append("\r\n");
            body.append(" Invoice No:              ").append(invoiceNumber).append("\r\n");


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
            return body.toString();

        }


    @SuppressLint("DefaultLocale")
    private String createZplProformaReceipt() {
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
                + centerAlignText("Date: " + getCurrentDate() + "  " + "Time: " + getCurrentTime())
                + centerAlignText("PROFORMA ORDER") + "\n";



        int referenceLength = reference.length();
        int spaceToAdd = Math.max(0, 10 - referenceLength); // Calculate the number of spaces to add to make the reference length 10
        StringBuilder spacesBuilder = new StringBuilder();
        for (int i = 0; i < spaceToAdd; i++) {
            spacesBuilder.append(" ");
        }
        String spaces = spacesBuilder.toString();
        // Create a string with the required number of spaces

        String header2 = "\r"
                + " CUSTOMER NAME:" + customername + "\r\n"
                + " ADDRESS:" + customeraddress+"\r\n"
                + " BRANCH:" + outletname + "\r\n"
                + " TRN:" + TRN_NO + "\r\n"
                + " EMIRATE: " + emirate+ "\r\n"
                + " VEHICLE NO: "+ vehiclenum+  "\r\n"
                + " ROUTE: "+ route+"\r\n"
                + " SALESMAN: " + name+"\r\n"
                + " REF.NO: " + reference + spaces + "\r\n"
                + " COMMENTS: " + comments + "\r\n";

        body.append(header1).append(header2);
// Add column headings
        body.append("---------------------------------------------------------------------\r\n");
        body.append(" ITEM   \t\t BARCODE \t\t   QTY \t\t  PRICE  \t\t  DISC \t\t   NET VAT % \t  VAT AMT \t GROSS").append("\r\n");
        body.append("---------------------------------------------------------------------\r");
// Auto-incrementing Sl.no and adding values
        for (int i = 0; i < itemCount; i++) {
            if (!Objects.equals(newSaleBeanListsss.get(i).getDelqty(), "0") || newSaleBeanListsss.get(i).getDelqty().isEmpty()  ) {
                itemcount++;
                String plucode="";
                //   if(customerNamearr.contains(customername)) {
                if ((newSaleBeanListsss.get(i).getPlucode().equals(null) || newSaleBeanListsss.get(i).getPlucode().isEmpty() || newSaleBeanListsss.get(i).getPlucode() == null)) {
                    plucode = "";
                } else {
                    plucode = newSaleBeanListsss.get(i).getPlucode();
                }
                //}
                body.append("\r").append(itemcount).append(". ").append(newSaleBeanListsss.get(i).getItemName()).append(" \t").append(newSaleBeanListsss.get(i).getItemCode()).append(" \t").append(plucode).append("\r\n");
                body.append("      "+newSaleBeanListsss.get(i).getBarcode()).append(" \t");

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
                String sellingPrice = newSaleBeanListsss.get(i).getSellingprice() != null ? newSaleBeanListsss.get(i).getSellingprice() : "0";


                BigDecimal doubleValue = BigDecimal.valueOf(Double.parseDouble(sellingPrice)).setScale(2, RoundingMode.HALF_UP);

// Convert to double and then cast to int
                int valuePrice = doubleValue.intValue();

                String disc = newSaleBeanListsss.get(i).getDisc();
                if (valuePrice >= 1000) {
                    body.append(sellingPrice).append("  \t");
                    body.append(disc).append("\t");
                    listDISC.add(disc);
                } else if (valuePrice >= 100 && valuePrice <= 999) {
                    body.append(" " + sellingPrice).append("  \t");
                    body.append(disc).append("\t");
                    listDISC.add(disc);
                } else if (valuePrice >= 10 && valuePrice <= 99) {
                    body.append("  " + sellingPrice).append("  \t");
                    body.append(disc).append("\t");
                    listDISC.add(disc);
                } else {
                    body.append("   " + sellingPrice).append("  \t");
                    body.append(disc).append("\t");
                    listDISC.add(disc);
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
                    BigDecimal formattedNET = BigDecimal.valueOf(Float.parseFloat(newSaleBeanListsss.get(i).getDelqty()) * (Float.parseFloat(sellingPrice))).setScale(2, RoundingMode.HALF_UP);;//here approvedqty means returnqty
                    NET = formattedNET;
                    listNET.add(String.valueOf(NET));
                } else {
                    BigDecimal formattedNET = BigDecimal.valueOf(Float.parseFloat(newSaleBeanListsss.get(i).getDelqty()) * (Float.parseFloat(sellingPrice))).setScale(2, RoundingMode.HALF_UP);;
                    NET = formattedNET;
                    listNET.add(String.valueOf(NET));
                }


                BigDecimal doubleValueNet = NET;
                String decimalStr = String.valueOf(NET);
                int decimalValue = decimalStr.indexOf(".");
                String decimalStr1 = decimalStr.substring(decimalValue + 1);
                int valuePriceNet =  doubleValueNet.intValue();
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
                ITEM_VAT_AMT = NET.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);;//here ;
                listVatAmnt.add(String.format("%.2f", ITEM_VAT_AMT));
                int itemVatAmount =  ITEM_VAT_AMT.intValue();
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
                ITEMS_GROSS = ITEM_VAT_AMT.add( NET);

                int grossValue =  ITEMS_GROSS.intValue();
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
        System.out.println("totalqty:" + (int)totalQty);
        body.append("\r\n").append(" Total Items:\t\t             ").append(itemcount);
        body.append("\r\n").append(" Total Qty:\t\t\t               ").append((int)totalQty).append("\r\n");

// Calculate and append total net amount


        // Initialize variables to accumulate totals


// Get rebate from database (assuming it's retrieved as a String)
        String rebateStr = getCustomerRebate(newsalecustomerCode);

        double rebate = 0.0;
        try {
            rebate = Double.parseDouble(rebateStr);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace(); // Handle parsing exception if necessary
        }

// Iterate through items to calculate total amounts
        for (int i = 0; i < itemCount; i++) {
            String qty = newSaleBeanListsss.get(i).getDelqty();
            BigDecimal qtyValue = BigDecimal.valueOf(Double.parseDouble(qty));
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(newSaleBeanListsss.get(i).getSellingprice() != null ? newSaleBeanListsss.get(i).getSellingprice() : "0")).setScale(2, RoundingMode.HALF_UP);;

            // Net amount for this item
            BigDecimal netAmount = qtyValue.multiply( price);

            // VAT amount for this item (5% of net amount)
            BigDecimal vatAmount = netAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);;//here ;

            BigDecimal grossAmount = netAmount .add( vatAmount);


            totalNetAmount =totalNetAmount.add( netAmount);
            totalVatAmount =totalVatAmount.add( vatAmount);
            totalGrossAmt =totalGrossAmt.add( grossAmount);
        }

        // Convert rebate percentage and total gross amount to BigDecimal
        BigDecimal rebatePercent = BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100.0));


// Calculate rebateAmount with proper precision
        BigDecimal rebateAmount = totalGrossAmt.multiply(rebatePercent).setScale(2, RoundingMode.HALF_UP);;//here ;

// Optionally, if you need `rebatePercent` as a double for

        amountPayableAfterRebate = totalGrossAmt.subtract( rebateAmount);


        body.append(" Total NET Amount:        ").append("AED ").append( totalNetAmount.setScale(2, RoundingMode.HALF_UP) ).append("\r\n");
        body.append(" Total VAT Amount:        ").append("AED ").append(totalVatAmount.setScale(2, RoundingMode.HALF_UP) ).append("\r\n");
        body.append(" Total Gross Amount:      ").append("AED ").append( totalGrossAmt.setScale(2, RoundingMode.HALF_UP)).append("\r\n");
        //   body.append(" Gross Amount Payable:    ").append("AED ").append(String.format("%.2f", amountPayableAfterRebate)).append("\r\n");
        body.append(" Sales Person Name:       ").append(name).append("\r\n");


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
        return body.toString();

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
    @Override
    protected void onPause() {
        if (helper != null) {
            helper.onDestroy();  // Ensure the dialog is dismissed properly
        }
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        if (helper != null) {
            helper.onDestroy();  // Ensure the dialog is dismissed properly
        }
        super.onDestroy();
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
    private void sendZplReceiptPerforma(Connection printerConnection) throws ConnectionException {
        String zplReceiptPerforma = createZplProformaReceipt();
        printerConnection.write(zplReceiptPerforma.getBytes());
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

}
