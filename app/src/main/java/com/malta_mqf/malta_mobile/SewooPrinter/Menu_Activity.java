package com.malta_mqf.malta_mobile.SewooPrinter;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.R;
import com.sewoo.jpos.command.ESCPOSConst;

public class Menu_Activity extends BaseActivity implements Button.OnClickListener {


    Sample_Print sample;
    String con_type = "";
    private Button button_sample4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_layout);

        ResourceInstaller ri = new ResourceInstaller();
        ri.copyAssets(getAssets(), "temp");

        Intent in = getIntent();
        con_type = in.getStringExtra("Connection");

        if (con_type.equals("BlueTooth"))
            activity_list.add(Menu_Activity.this);


        button_sample4 = findViewById(R.id.ButtonSample_4);


        button_sample4.setOnClickListener(this);


        sample = new Sample_Print();
    }

    @Override
    public void onClick(View view) {
        int re_val = 0;

        try {
            if (view.getId() == R.id.ButtonSample_4) {
                re_val = sample.Print_Sample_4();
            }


            String statusMsg;
            if (re_val == ESCPOSConst.LK_STS_NORMAL) {
                statusMsg = "Printing success";
                Toast toast = Toast.makeText(Menu_Activity.this, statusMsg, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                statusMsg = "";
                if ((re_val & ESCPOSConst.LK_STS_COVER_OPEN) > 0)
                    statusMsg = statusMsg + "Cover Open\r\n";
                if ((re_val & ESCPOSConst.LK_STS_PAPER_EMPTY) > 0)
                    statusMsg = statusMsg + "Paper Empty\r\n";
                if ((re_val & ESCPOSConst.LK_STS_BATTERY_LOW) > 0)
                    statusMsg = statusMsg + "Battery Low\r\n";
                if ((re_val & ESCPOSConst.LK_STS_PRINTEROFF) > 0)
                    statusMsg = statusMsg + "Printer Power OFF\r\nReconnect and print\r\n";
                if ((re_val & ESCPOSConst.LK_STS_TIMEOUT) > 0)
                    statusMsg = statusMsg + "Timeout error";

                AlertDialog.Builder alert = new AlertDialog.Builder(Menu_Activity.this);
                alert.setTitle("Error")
                        .setMessage(statusMsg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            AlertDialog.Builder alert = new AlertDialog.Builder(Menu_Activity.this);
            alert.setTitle("Error")
                    .setMessage(e.toString())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (con_type.equals("BlueTooth")) {
            activity_list.remove(1);
            ((Bluetooth_Activity) activity_list.get(0)).ExcuteDisconnect();
        }


        super.onDestroy();
    }
}
