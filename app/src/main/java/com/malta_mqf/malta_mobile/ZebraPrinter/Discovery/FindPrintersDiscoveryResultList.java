

package com.malta_mqf.malta_mobile.ZebraPrinter.Discovery;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;

import com.malta_mqf.malta_mobile.ZebraPrinter.UIHelper;
import com.zebra.sdk.printer.discovery.DiscoveryException;
import com.zebra.sdk.printer.discovery.NetworkDiscoverer;

public class FindPrintersDiscoveryResultList extends DiscoveryResultList {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            MulticastLock lock = wifi.createMulticastLock("wifi_multicast_lock");
            lock.setReferenceCounted(true);
            lock.acquire();
            NetworkDiscoverer.findPrinters(this);
            lock.release();
        } catch (DiscoveryException e) {
            new UIHelper(this).showErrorDialog(e.getMessage());
        }
    }
}
