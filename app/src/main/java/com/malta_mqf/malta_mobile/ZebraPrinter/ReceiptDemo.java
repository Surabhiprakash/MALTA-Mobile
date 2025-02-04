package com.malta_mqf.malta_mobile.ZebraPrinter;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditbeanList;
import static com.malta_mqf.malta_mobile.DeliveryHistoryDetails.invNoOrOrderId;
import static com.malta_mqf.malta_mobile.NewSaleActivity.customername;
import static com.malta_mqf.malta_mobile.NewSaleActivity.invoiceNumber;
import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.credId;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.customerName;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.returnComments;
import static com.malta_mqf.malta_mobile.ReturnCreditNote.returnrefrence;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.ExpandableListView;


import androidx.annotation.NonNull;

import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DeliveryHistoryDetails;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.R;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

public class ReceiptDemo extends ConnectionScreen implements DiscoveryHandler {

    private UIHelper helper = new UIHelper(this);
    private boolean sendData = true;
    String orderId, reference, comments, returnComments, returnrefrence;
    public static double totalNetAmount, totalVatAmount, totalGrossAmt, NET, ITEM_VAT_AMT, ITEMS_GROSS;
    public static int totalQty;
    public static List<String> listNET = new LinkedList<>();
    public static List<String> listVAT = new LinkedList<>();
    public static List<String> listVatAmnt = new LinkedList<>();
    public static List<String> listGROSS = new LinkedList<>();
    public static List<String> listDISC = new LinkedList<>();

    SubmitOrderDB submitOrderDB;
    Connection printerConnection = null;

    static List<NewSaleBean> newSaleBeanListsss = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testButton = (Button) this.findViewById(R.id.testButton);
        mExpListView = (ExpandableListView) findViewById(android.R.id.list);
        Intent intent = getIntent();
        reference = intent.getStringExtra("referenceNo");
        comments = intent.getStringExtra("comments");
        returnrefrence = intent.getStringExtra("refrence");
        returnComments = intent.getStringExtra("comment");


        if (reference == null) {
            reference = returnrefrence;
        }
        if (comments == null) {
            comments = returnComments;
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
        if (newSaleBeanListss.size() == 0) {
            System.out.println("inside ifffff");
            newSaleBeanListsss = new LinkedList<>(creditbeanList);
            System.out.println(newSaleBeanListsss);
        } else {
            newSaleBeanListsss = new LinkedList<>(newSaleBeanListss);
        }

        if (customername == null) {
            customername = customerName;
        }
        if (invoiceNumber == null) {
            invoiceNumber = credId;
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
        SettingsHelper.saveBluetoothAddress(ReceiptDemo.this, getMacAddressFieldText());

    }

    private String createZplReceipt() {
        listDISC.clear();
        listGROSS.clear();
        listNET.clear();
        listVAT.clear();
        listVatAmnt.clear();
        totalQty=0;
        totalGrossAmt=0;
        totalVatAmount=0;
        totalNetAmount=0;
        // Sample values
        int itemCount = newSaleBeanListsss.size();  // Set the number of items
       /* String[] itemName = {"APPLE JUICE 330ML", "AVOCADO JUICE 330ML", "ORANGE JUICE 330ML", "CARROT JUICE 330ML", "PINEAPPLE JUICE 330ML",
                "POMEGRANATE JUICE 330ML", "WATERMELON JUICE 330ML", "BEETROOT & ORANGE JUICE 330ML", "GREEN JUICE 330ML", "APPLE CELERY GINGER JUICE 330ML",
                "MY D HPP ORANGE JUICE 120ML", "ANOTHER JUICE 330ML"};  // Adjust the number of items in this array
        String[] itemCode = {"101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112"};  // Adjust the item codes accordingly
        String[] qty = {"2.00", "1.00", "3.00", "4.00", "5.00", "6.00", "7.00", "8.00", "9.00", "10.00", "11.00", "12.00"};
        String[] price = {"9.19", "20.00", "15.00", "10.00", "15.00", "18.00", "20.00", "13.00", "27.00", "40.00", "19.00", "10.00"};
*/
        StringBuilder body = new StringBuilder();

        String header1 = centerAlignText("GFC GENERAL TRADING LLC") + "\n"
                + centerAlignText("OFFICE NO.713, ATRIUM CENTER, BUR DUBAI") + "\n"
                + centerAlignText("TEL: (04) 3515766") + "\n"
                + centerAlignText("PO BOX 27253, DUBAI, UAE") + "\n"
                + centerAlignText("TRN-100390755500003") + "\n"
                +centerAlignText("Date/Time: "+ getCurrentDate() +" " +getCurrentTime())
                + centerAlignText("TAX INVOICE") + "\n\n"
                + centerAlignText("Invoice No: " + invoiceNumber) + "\n";


        String header2 = "\n\n"
                +"CUSTOMER NAME:"+customername+"\r\n"
                + "ADDRESS: DUBAI DESIGN DISTRICT\n"
                + "                                       TRN: 10046769400003\n"
                + "                                                  EMIRATE: DUBAI\n"
                + "                                                       REF.NO: "+returnrefrence+"\n"
                + "                                                   COMMENTS: "+returnComments+"\n\n";
        body.append(header1).append(header2).append("\r\n");
// Add column headings
// Add column headings
        body.append("---------------------------------------------------------------------\r\n");
        body.append(" ITEM      \t\t    QTY \t\t  PRICE   \t\t  DISC \t\t   NET VAT % \t  VAT AMT \t    GROSS").append("\r\n");
        body.append("---------------------------------------------------------------------\r\n");
// Auto-incrementing Sl.no and adding values
        for (int i = 0; i < itemCount; i++) {
            body.append(" ").append(i + 1).append(". ").append(newSaleBeanListsss.get(i).getProductName()).append("\r\n");
            body.append("    \t\t").append(newSaleBeanListsss.get(i).getItemCode()).append("    \t\t");

            // Check if deliveryQty is null or "0", if yes, use approvedQty, else use deliveryQty
            String qty = (newSaleBeanListsss.get(i).getDeliveryQty() == null || newSaleBeanListsss.get(i).getDeliveryQty().equals("0"))
                    ? newSaleBeanListsss.get(i).getApprovedQty()
                    : newSaleBeanListsss.get(i).getDeliveryQty();
            body.append(qty).append(" BTL    \t");

            // Check if sellingPrice is not null before using it
            String sellingPrice = newSaleBeanListsss.get(i).getSellingPrice() != null ? newSaleBeanListsss.get(i).getSellingPrice() : "0";
            body.append(sellingPrice).append("   \t");

            body.append("0.00   \t");

            listDISC.add("0.00");
            if(newSaleBeanListsss.get(i).getDeliveryQty()==null){
                System.out.println(newSaleBeanListsss.get(i).getApprovedQty());
                double formattedNET = Float.parseFloat(newSaleBeanListsss.get(i).getApprovedQty()) * (Float.parseFloat(sellingPrice));//here approvedqty means returnqty
                NET = Double.parseDouble(String.format("%.2f",formattedNET));
                listNET.add(String.valueOf(NET));
            }else{
                double formattedNET = Float.parseFloat(newSaleBeanListsss.get(i).getDeliveryQty()) * (Float.parseFloat(sellingPrice));
                NET = Double.parseDouble(String.format("%.2f",formattedNET));
                listNET.add(String.valueOf(NET));
            }


            body.append(NET).append("     \t");


            body.append("5%   \t");
            listVAT.add("5");
            ITEM_VAT_AMT=NET * 0.05;
            listVatAmnt.add(String.format("%.2f",ITEM_VAT_AMT));

            body.append(String.format("%.2f",ITEM_VAT_AMT)).append("       \t");
            ITEMS_GROSS= ITEM_VAT_AMT + NET;
            listGROSS.add(String.format("%.2f",ITEMS_GROSS));
            body.append(String.format("%.2f",ITEMS_GROSS)).append(" \t");
        }

// Calculate and append total quantity
        //totalQty = String.valueOf(0);
        for (int i = 0; i < itemCount; i++) {
            String qty = (newSaleBeanListsss.get(i).getDeliveryQty() == null || newSaleBeanListsss.get(i).getDeliveryQty().equals("0"))
                    ? newSaleBeanListsss.get(i).getApprovedQty()
                    : newSaleBeanListsss.get(i).getDeliveryQty();
            totalQty += Double.parseDouble(qty);
        }
        System.out.println("totalqty:"+totalQty);
        body.append("  Total Qty:\t\t\t").append(totalQty).append("\r\n");

// Calculate and append total net amount
        totalNetAmount = 0;
        for (int i = 0; i < itemCount; i++) {
            String qty = (newSaleBeanListsss.get(i).getDeliveryQty() == null || newSaleBeanListsss.get(i).getDeliveryQty().equals("0"))
                    ? newSaleBeanListsss.get(i).getApprovedQty()
                    : newSaleBeanListsss.get(i).getDeliveryQty();
            double qtyValue = Double.parseDouble(qty);
            double price = Double.parseDouble(newSaleBeanListsss.get(i).getSellingPrice() != null ? newSaleBeanListsss.get(i).getSellingPrice() : "0");

            // Net amount for this item
            double netAmount = qtyValue * price;

            // VAT amount for this item (5% of net amount)
            double vatAmount = netAmount * 0.05;

            // Gross amount for this item
            double grossAmount = netAmount + vatAmount;

            // Accumulate total amounts
            totalNetAmount += netAmount;
            totalVatAmount += vatAmount;
            totalGrossAmt += grossAmount;
        }
        body.append(" Total NET Amount:\t\t").append(String.format("%.2f",totalNetAmount)).append("\r\n");
        body.append(" Total VAT Amount:\t\t").append(String.format("%.2f",totalVatAmount)).append("\r\n");

// Calculate and append gross amount payable
        totalGrossAmt = totalNetAmount + totalVatAmount;
        body.append(" Gross Amount Payable:\t").append(String.format("%.2f",totalGrossAmt)).append("\r\n");
        body.append("\r\n");
        body.append("\r\n");
        body.append("\r\n");
        body.append("\r\n");

        body.append(" ----------------------------    \t\t").append("------------------------------------\r\n");
        ;

        body.append(" Buyer's Signature                          \t\t").append("Seller's Signature\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
        ;

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
