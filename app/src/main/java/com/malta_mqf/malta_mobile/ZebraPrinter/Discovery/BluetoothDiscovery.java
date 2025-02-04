package com.malta_mqf.malta_mobile.ZebraPrinter.Discovery;


import android.os.Bundle;
import android.os.Looper;

import com.malta_mqf.malta_mobile.ZebraPrinter.UIHelper;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;

public class BluetoothDiscovery extends DiscoveryResultList {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    BluetoothDiscoverer.findPrinters(BluetoothDiscovery.this, BluetoothDiscovery.this);
                } catch (ConnectionException e) {
                    new UIHelper(BluetoothDiscovery.this).showErrorDialogOnGuiThread(e.getMessage());
                } finally {
                    Looper.myLooper().quit();
                }
            }
        }).start();
    }

}
