package com.malta_mqf.malta_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DeliveryActivity extends AppCompatActivity {

    CardView cardView_New_Order,cardView_New_sale,customerCardReturn;
    String outletname,outletlocation,outletid,customerCode,customername;
    TextView customerName,outletName;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        cardView_New_Order = findViewById(R.id.customerCardView1);
        cardView_New_sale = findViewById(R.id.customerCardView2);
        customerName = findViewById(R.id.customerName);
        outletName = findViewById(R.id.customerAddress1);
        customerCardReturn=findViewById(R.id.customerCardReturn);
        outletid = getIntent().getStringExtra("outletid");
        System.out.println("hi........"+outletid);
        outletname = getIntent().getStringExtra("outletname");
        customerCode = getIntent().getStringExtra("customerCode");
        customername = getIntent().getStringExtra("customerName");

        customerName.setText(customername);
        outletName.setText(outletname);
        cardView_New_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryActivity.this, AddQuantity.class);
                intent.putExtra("outletName",outletname);
                intent.putExtra("outletId",outletid);
                intent.putExtra("customerCode",customerCode);
                startActivity(intent);
            }
        });

        cardView_New_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeliveryActivity.this, NewSaleActivity.class);
                intent.putExtra("outletName",outletname);
                intent.putExtra("outletId",outletid);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customerName",customername);
                startActivity(intent);
            }
        });

        customerCardReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DeliveryActivity.this,CustomerReturn.class);
                intent.putExtra("outletId",outletid);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customerName",customername);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        outletname=null;
        outletlocation=null;
        outletid=null;
        customerCode=null;
        customername=null;
    }
}