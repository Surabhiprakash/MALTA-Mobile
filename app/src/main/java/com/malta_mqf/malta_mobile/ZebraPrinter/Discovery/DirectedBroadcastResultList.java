package com.malta_mqf.malta_mobile.ZebraPrinter.Discovery;

import android.os.Bundle;

import com.malta_mqf.malta_mobile.ZebraPrinter.UIHelper;
import com.zebra.sdk.printer.discovery.DiscoveryException;
import com.zebra.sdk.printer.discovery.NetworkDiscoverer;

public class DirectedBroadcastResultList extends DiscoveryResultList {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String directedBroadcastIp = getIntent().getExtras().getString(DirectedBroadcastParameters.DIRECTED_BROADCAST_IP);

        try {
            NetworkDiscoverer.directedBroadcast(this, directedBroadcastIp);
        } catch (DiscoveryException e) {
            new UIHelper(this).showErrorDialog(e.getMessage());
        }
    }
}
