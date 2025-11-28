package com.malta_mqf.malta_mobile.SewooPrinter;

import static com.malta_mqf.malta_mobile.ReturnHistoryDetails.returnHistoryDetailsList;
import static com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistorySamplePrint.newSaleBeanLists;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.DeliveryHistoryDeatilsBean;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.Return_History;
import com.malta_mqf.malta_mobile.StartDeliveryActivity;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReturnHistoryBluetoothActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 2;
    public static final String PREFS_NAME = "BluetoothPrefs";
    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp";
    private static final String fileName = dir + "/BTPrinter";
    private static final int BT_PRINTER = 1536;
    private static final String SAVED_BT_KEY = "savedBT";
    private static final String MAC_ADDRESS_KEY = "bluetoothAddressKey";
    public static ArrayList<Activity> activity_list = new ArrayList<Activity>();
    public static List<String> orderList = new ArrayList<>();
    public static String outletname, outletcode, customeraddress, outletAddress, emirate, customercode, customername, creditIdNo, refrenceno, Comments, trn_no;
    static byte[] billImageDataSewoo;
    static byte[] billImageData;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    ArrayAdapter<String> adapter;
    boolean searchflags;
    SubmitOrderDB submitOrderDB;
    StockDB stockDB;
    Toolbar toolbar;
    private EditText edit_input;
    private Button button_connect;
    private Button button_search, button_capture, finishButton;
    private ListView list_printer;
    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver connectDevice;
    private Vector<BluetoothDevice> remoteDevices;
    private BluetoothDevice btDev;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothPort bluetoothPort;
    private CheckTypesTask BTtask;
    private ExcuteDisconnectBT BTdiscon;
    private ImageView billImageView;
    private Thread btThread;
    private Bitmap billBitmap;
    private boolean disconnectflags;
    private String currentPhotoPath;
    private String str_SavedBT = "";

    private String loadSettingFromPrefs() {
        // Access SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve the saved value, provide a default value if not found
        String savedBT = settings.getString(SAVED_BT_KEY, "");  // Default value is empty string

        return savedBT;
    }


    // Key for storing the value

    private void saveSettingToPrefs() {
        try {
            // Access SharedPreferences in private mode
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            // Store the value of str_SavedBT in SharedPreferences
            editor.putString(SAVED_BT_KEY, str_SavedBT);
            editor.apply();  // Asynchronously save the changes

            // Optionally, show a message or log success
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetooth_layout);
        Intent intent = getIntent();
        outletname = getIntent().getStringExtra("outletname");
        outletcode = getIntent().getStringExtra("outletCode");
        customercode = getIntent().getStringExtra("customerCode");
        refrenceno = getIntent().getStringExtra("reference");
        Comments = getIntent().getStringExtra("comments");
        trn_no = getIntent().getStringExtra("returnTrn");
        outletAddress = getIntent().getStringExtra("outletAddress");
        emirate = getIntent().getStringExtra("emirate");
        customeraddress = getIntent().getStringExtra("customeraddress");
        creditIdNo = getIntent().getStringExtra("creditIdNo");
        customername = getIntent().getStringExtra("customername");
        System.out.println("outletname in return bluetooth is: " + outletname);
        System.out.println("outletcode in return bluetooth is: " + outletcode);
        System.out.println("refrenceno in return bluetooth is: " + refrenceno);
        System.out.println("comments in return bluetooth is: " + Comments);
        System.out.println("trn in return bluetooth is: " + trn_no);
        System.out.println("outletAddress in return bluetooth is: " + outletAddress);
        System.out.println("emirate in return bluetooth is: " + emirate);


        String newSaleBeanListJson = getIntent().getStringExtra("deliveryHistoryDetailsList");
        if (newSaleBeanListJson != null) {
            Type type = new TypeToken<ArrayList<DeliveryHistoryDeatilsBean>>() {
            }.getType();
            newSaleBeanLists = new Gson().fromJson(newSaleBeanListJson, type);
        } else {
            newSaleBeanLists = new ArrayList<>();
        }
        if (outletAddress == null) {
            outletAddress = "DUBAI DESIGN DISTRICT";
            if (emirate == null) {
                emirate = "DUBAI";
            }
        }
        activity_list.add(ReturnHistoryBluetoothActivity.this);

        loadSettingFromPrefs();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("GENERATE INVOICE");
        edit_input = findViewById(R.id.EditTextAddressBT);
        button_connect = findViewById(R.id.ButtonConnectBT);
        button_search = findViewById(R.id.ButtonSearchBT);
        list_printer = findViewById(R.id.ListView01);
        button_capture = findViewById(R.id.btn_capture_bill);
        button_capture.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        finishButton = findViewById(R.id.finishDelivery);
        finishButton.setEnabled(true);
        finishButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        billImageView = findViewById(R.id.billImageView);
        submitOrderDB = new SubmitOrderDB(this);
        stockDB = new StockDB(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String storedString = sharedPreferences.getString("stored_string", "");

        // Set the string to EditText
        if (!storedString.isEmpty()) {
            edit_input.setText(storedString);
        }


        findViewById(R.id.btn_capture_bill).setOnClickListener(v -> {
            String fileName = "billPhoto";
            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile(fileName, ".jpg", storageDirectory);
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri imagreUri = FileProvider.getUriForFile(this, "com.malta_mqf.malta_mobile.fileprovider", photoFile);
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, imagreUri);
                startActivityForResult(intent1, 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // 100 for maximum quality

       /* if(billBitmap== null){
            finishButton.setEnabled(false);
        }*/

        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (returnHistoryDetailsList.size() != 0) {


                    Intent intent = new Intent(ReturnHistoryBluetoothActivity.this, Return_History.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    returnHistoryDetailsList.clear();
                    finish();
                }
            }
        });





/*
        if(billBitmap== null){
            finishButton.setEnabled(false);
        }*/

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list_printer.setAdapter(adapter);


        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        searchflags = false;
        disconnectflags = false;

        edit_input.setText(str_SavedBT);


        Init_BluetoothSet();

        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);//not using mac address filtering

        addPairedDevices();

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTtask = new CheckTypesTask();
                BTtask.execute();
            }
        });


        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String input_ip = edit_input.getText().toString();

                    if (input_ip.equals("")) {
                        alert
                                .setTitle("Error")
                                .setMessage("Input value is Null")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else if (!isValidBluetoothAddress(input_ip)) {
                        alert
                                .setTitle("Error")
                                .setMessage("Invalid Bluetooth Address")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        btConn(mBluetoothAdapter.getRemoteDevice(input_ip));
                        showExitConfirmationDialog2();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        list_printer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btDev = remoteDevices.elementAt(i);

                try {
                    btConn(btDev);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedMac = settings.getString(MAC_ADDRESS_KEY, "");
        edit_input.setText(savedMac);

        // Enable EditText to allow user input
        edit_input.setEnabled(true);
        edit_input.setFocusable(true);
        edit_input.setFocusableInTouchMode(true);


        edit_input.addTextChangedListener(new TextWatcher() {
            private boolean isToastShown = false;  // To avoid repetitive toasts

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputMac = s.toString().trim();

                // MAC address pattern (XX:XX:XX:XX:XX:XX or XX-XX-XX-XX-XX-XX)
                String macAddressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

                // Validate the MAC address format
                if (inputMac.matches(macAddressPattern)) {
                    button_connect.setEnabled(true);
                    button_connect.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

                    // Save the MAC address to SharedPreferences
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(MAC_ADDRESS_KEY, inputMac);
                    editor.apply();

                    isToastShown = false;  // Reset toast shown state when valid input
                } else {
                    button_connect.setEnabled(false);
                    button_connect.setBackgroundColor(getResources().getColor(R.color.listitem_gray));

                    // Show the toast once for invalid MAC address
                    if (!inputMac.isEmpty() && !isToastShown) {
                        //Toast.makeText(getApplicationContext(), "Invalid MAC Address", Toast.LENGTH_SHORT).show();
                        isToastShown = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (!savedMac.isEmpty() && savedMac.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
            button_connect.setEnabled(true);
            button_connect.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        } else {
            button_connect.setEnabled(false);
            button_connect.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
            if (savedMac.isEmpty()) {
                Toast.makeText(this, "Please enter a valid MAC Address", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean isValidBluetoothAddress(String address) {
        return address.matches("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");
    }

    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    private void bluetoothSetup() {
        // Initialize
        clearBtDevData();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    public void ConnectionFailedDevice() {
        // Check if the activity is finishing or destroyed before showing the dialog
        if (!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert
                    .setTitle("Error")
                    .setMessage("The Bluetooth connection is lost.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public void Init_BluetoothSet() {
        bluetoothSetup();

        connectDevice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //Toast.makeText(getApplicationContext(), "BlueTooth Connect", Toast.LENGTH_SHORT).show();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    try {
                        if (bluetoothPort.isConnected())
                            bluetoothPort.disconnect();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if ((btThread != null) && (btThread.isAlive())) {
                        btThread.interrupt();
                        btThread = null;
                    }

                    ConnectionFailedDevice();

                    //Toast.makeText(getApplicationContext(), "BlueTooth Disconnect", Toast.LENGTH_SHORT).show();
                }
            }
        };

        discoveryResult = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String key;
                boolean bFlag = true;
                BluetoothDevice btDev;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (remoteDevice != null) {
                    if (ActivityCompat.checkSelfPermission(ReturnHistoryBluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    int devNum = remoteDevice.getBluetoothClass().getMajorDeviceClass();

                    if (devNum != BT_PRINTER)
                        return;

                    if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    } else {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.getAddress())) {
                        for (int i = 0; i < remoteDevices.size(); i++) {
                            btDev = remoteDevices.elementAt(i);
                            if (remoteDevice.getAddress().equals(btDev.getAddress())) {
                                bFlag = false;
                                break;
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice);
                            adapter.add(key);
                        }
                    }
                }
            }
        };

        registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        searchStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(mainView, "블루투스 기기 검색 시작", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));

        searchFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchflags = true;
            }
        };
        registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            ImageView imageView = findViewById(R.id.billImageView);
            imageView.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            billImageDataSewoo = stream.toByteArray();
            System.out.println("billImageByteArray :" + Arrays.toString(billImageDataSewoo));
            finishButton.setEnabled(true);
            finishButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        }
    }


    private void addPairedDevices() {
        BluetoothDevice pairedDevice;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();

        String key = "";

        while (iter.hasNext()) {
            pairedDevice = iter.next();
            if (bluetoothPort.isValidAddress(pairedDevice.getAddress())) {
                int deviceNum = pairedDevice.getBluetoothClass().getMajorDeviceClass();

                if (deviceNum == BT_PRINTER) {
                    remoteDevices.add(pairedDevice);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    key = pairedDevice.getName() + "\n[" + pairedDevice.getAddress() + "] [Paired]";
                    adapter.add(key);
                }
            }
        }
    }

    private void SearchingBTDevice() {
        adapter.clear();
        adapter.notifyDataSetChanged();

        clearBtDevData();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mBluetoothAdapter.startDiscovery();
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new connBT().execute(btDev);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showExitConfirmationDialog(); // Show the dialog when the home button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog(); // Show the dialog when the back button is pressed
    }

    private void showExitConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Hey!!! you haven't pressed finish button to finish return? Are you sure want to go back?")
                .setCancelable(false) // Prevents dialog from closing on outside touch
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Use executor to handle the 'Yes' action
                    executorService.execute(() -> finish());
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Use executor to handle the 'No' action
                    executorService.execute(dialog::dismiss);
                })
                .show();
    }

    private void showExitConfirmationDialog2() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Hey!!! Do not Forget to complete this return By Pressing Finish Button!!! ")
                .setCancelable(false) // Prevents dialog from closing on outside touch
                .setPositiveButton("OK", (dialog, which) -> {
                    // Use executor to handle the 'Yes' action
                    executorService.execute(dialog::dismiss);
                })

                .show();
    }

    public void DisconnectDevice() {
        try {
            bluetoothPort.disconnect();

            unregisterReceiver(connectDevice);

            if ((btThread != null) && (btThread.isAlive()))
                btThread.interrupt();

            disconnectflags = true;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ExcuteDisconnect() {
        BTdiscon = new ExcuteDisconnectBT();
        BTdiscon.execute();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        try {

            if (bluetoothPort.isConnected()) {
                bluetoothPort.disconnect();
                unregisterReceiver(connectDevice);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if ((btThread != null) && (btThread.isAlive())) {
            btThread.interrupt();
            btThread = null;
        }

        unregisterReceiver(searchFinish);
        unregisterReceiver(searchStart);
        unregisterReceiver(discoveryResult);
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(ReturnHistoryBluetoothActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Searching the Printer...");
            asyncDialog.setCancelable(false);
            asyncDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            searchflags = true;
                            if (ActivityCompat.checkSelfPermission(ReturnHistoryBluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    });
            asyncDialog.show();
            SearchingBTDevice();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                while (true) {
                    if (searchflags)
                        break;

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (asyncDialog.isShowing())
                asyncDialog.dismiss();

            searchflags = false;
            super.onPostExecute(result);
        }


    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(ReturnHistoryBluetoothActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(ReturnHistoryBluetoothActivity.this);

        String str_temp = "";

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;

            try {
                bluetoothPort.connect(params[0]);
                if (ActivityCompat.checkSelfPermission(ReturnHistoryBluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
// Assuming this is inside the method where you get str_temp
                str_temp = params[0].getAddress();
// Store in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("stored_string", str_temp);
                editor.apply();


                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (result.intValue() == 0)    // Connection success.
            {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();

                str_SavedBT = str_temp;
                edit_input.setText(str_SavedBT);
                saveSettingToPrefs();


                registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));


                ReturnHistorySamplePrint sp = new ReturnHistorySamplePrint();
                try {
                    sp.ReturnHistorySample_Print();
                 /*   Intent in = new Intent(Bluetooth_Activity.this, NewSaleInvoice.class);
                in.putExtra("Connection", "BlueTooth");
                startActivity(in)*/

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else    // Connection failed.
            {
                alert
                        .setTitle("Error")
                        .setMessage("please,try again.")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(result);

        }
    }

    private class ExcuteDisconnectBT extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(ReturnHistoryBluetoothActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Disconnecting Device...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                DisconnectDevice();

                while (true) {
                    if (disconnectflags)
                        break;

                    Thread.sleep(100);

                    Intent in = new Intent(ReturnHistoryBluetoothActivity.this, StartDeliveryActivity.class);
                    in.putExtra("Connection", "BlueTooth");
                    startActivity(in);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            disconnectflags = false;
            Intent in = new Intent(ReturnHistoryBluetoothActivity.this, StartDeliveryActivity.class);
            in.putExtra("Connection", "BlueTooth");
            startActivity(in);
            super.onPostExecute(result);
        }

    }

}
