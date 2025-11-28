package com.malta_mqf.malta_mobile.ZebraPrinter.Discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.malta_mqf.malta_mobile.R;


public class DirectedBroadcastParameters extends Activity {

    public static final String DIRECTED_BROADCAST_IP = "DIRECTED_BROADCAST_IP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direct_broadcast_discovery_parameters);

        Button discoverButton = this.findViewById(R.id.do_directed_broadcast);

        discoverButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(DirectedBroadcastParameters.this, DirectedBroadcastResultList.class);
                Bundle directedBroadcastBundle = new Bundle();
                EditText directedBroadcastIp = findViewById(R.id.directed_broadcast_ip);
                String ip = directedBroadcastIp.getText().toString();
                directedBroadcastBundle.putString(DIRECTED_BROADCAST_IP, ip);
                intent.putExtras(directedBroadcastBundle);
                startActivity(intent);
            }
        });

    }

}
