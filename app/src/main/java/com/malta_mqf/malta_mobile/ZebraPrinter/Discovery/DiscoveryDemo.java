package com.malta_mqf.malta_mobile.ZebraPrinter.Discovery;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.malta_mqf.malta_mobile.R;
import com.zebra.sdk.printer.discovery.internal.FindPrinters;


public class DiscoveryDemo extends ListActivity {

    private static final int BLUETOOTH = 0;
    private static final int LOCAL_BROADCAST = 1;
    private static final int DIRECTED_BROADCAST = 2;
    private static final int MULTICAST = 3;
    private static final int SUBNET_SEARCH = 4;
    private static final int FIND_PRINTERS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discovery_methods);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent;
        switch (position) {
            case BLUETOOTH:
                intent = new Intent(this, BluetoothDiscovery.class);
                break;

            case FIND_PRINTERS:
                intent = new Intent(this, FindPrinters.class);
                break;
            default:
                return;// not possible
        }
        startActivity(intent);
    }

}
