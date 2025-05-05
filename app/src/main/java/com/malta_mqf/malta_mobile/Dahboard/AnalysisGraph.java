package com.malta_mqf.malta_mobile.Dahboard;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.BaseActivity;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.TotalApprovedOrderBsdOnItem;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.MainActivity;
import com.malta_mqf.malta_mobile.Model.DashBoardResponse;
import com.malta_mqf.malta_mobile.Model.SalesReturnsForTab;
import com.malta_mqf.malta_mobile.Model.SalesReturnsForTabList;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisGraph extends BaseActivity {
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private StockDB stockDB;
    TotalApprovedOrderBsdOnItem totalApprovedOrderBsdOnItem;
    ALodingDialog aLodingDialog;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, ReturnTextView, SalesTextView;
    private TextView totalOrderCountPlannedTextView, deliveredCountTextView, invoiceCountTextView, outOfRouteCountTextView, missedCallsTextView;
    TextView ytdSalesTextView,mtdTextView,targetTextView,btgTextView;
    private String fromDate, toDate,outletId,vanId;
    Button getBtn;
    Toolbar toolbar;
    OutletByIdDB outletByIdDB;
    UserDetailsDb userDetailsDb;
    AutoCompleteTextView actvOutlet;
    private static final String TAG = "AnalysisGraphActivity";

    private List<String> dateList = new ArrayList<>(); // Store all dates
    private List<Double> salesDataList = new ArrayList<>(); // Store sales data
    private List<Double> returnsDataList = new ArrayList<>(); // Store return data
    private int totalOrders = 0;
    private int totalDeliveredCount = 0;
    private int totalInvoiceCount = 0;
    private int totalMissedCalls = 0;
    double salesTotalNetMonthly, salesTotalNetYearly;
    String expectedDelivery;
    ImageView syncImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        expectedDelivery=getCurrentDatetime();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SELECT DATE RANGE");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        getUserDetails();
        loadOutletNamesOnline();

        if(isOnline()){
            loadYearlyAndMonthlyDateRanges();
            //loadOutletNamesOnline();
        }else{
            loadYearlyAndMonthlyDateRangesOffline();
          //  loadOutletNames();
        }
//        if (isEodSyncDone()) {
//            syncImage.setColorFilter(ContextCompat.getColor(AnalysisGraph.this, android.R.color.holo_green_light));
//        } else {
//            syncImage.setColorFilter(ContextCompat.getColor(AnalysisGraph.this, android.R.color.holo_red_dark));
//        }
        setupListeners();
        setDefaultDateValues();
        loadTodaysOrderData();
        updateTotalCounts(expectedDelivery);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("Range")
    private void getUserDetails() {
        Cursor cursor = userDetailsDb.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No User Data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {

                vanId = cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_VAN_ID));
            }
        }
    }

    private void initializeViews() {
        ytdSalesTextView=findViewById(R.id.ytdSalesText);
        mtdTextView=findViewById(R.id.mtdSalesText);
        targetTextView=findViewById(R.id.targetSalesText);
        btgTextView=findViewById(R.id.btgText);
        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this);
        totalApprovedOrderBsdOnItem=new TotalApprovedOrderBsdOnItem(this);
        actvOutlet=findViewById(R.id.outletSearch);
        getBtn=findViewById(R.id.getButton);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText);
        SalesTextView = findViewById(R.id.salesPercentageText);
        ReturnTextView = findViewById(R.id.returnPercentageText);
        totalOrderCountPlannedTextView = findViewById(R.id.totalOrderCountPlannedText);
        deliveredCountTextView = findViewById(R.id.completedOrdersText);
        invoiceCountTextView = findViewById(R.id.invoiceCountText);
        outOfRouteCountTextView = findViewById(R.id.outOfRouteCountText);
        missedCallsTextView = findViewById(R.id.missedCallsText);
        aLodingDialog=new ALodingDialog(this);
        outletByIdDB=new OutletByIdDB(this);
        stockDB=new StockDB(this);
        userDetailsDb=new UserDetailsDb(this);
        syncImage=findViewById(R.id.syncImage);
    }

    private void setupListeners() {
        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if both dates are selected
                if (fromDate == null || fromDate.isEmpty()) {
                    Toast.makeText(AnalysisGraph.this, "Please select a From Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (toDate == null || toDate.isEmpty()) {
                    Toast.makeText(AnalysisGraph.this, "Please select a To Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isOnline()) {
                    System.out.println("Online API is called");
                    loadTotalGrossAmountOnline(fromDate, toDate, vanId, outletId);
                } else {
                    System.out.println("Offline is called");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        loadTotalGrossAmountForDateRangeOffline(fromDate, toDate, outletId);
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadTotalGrossAmountForDateRangeOffline(String fromDate, String toDate, String outletId) {
        dateList.clear();
        salesDataList.clear();
        returnsDataList.clear();

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start, end;
        try {
            start = LocalDate.parse(fromDate, inputFormatter);
            end = LocalDate.parse(toDate, inputFormatter);
        } catch (DateTimeParseException e) {
            Log.e("DateParsing", "Error parsing dates: " + e.getMessage());
            return;
        }

        while (!start.isAfter(end)) {
            String currentDate = start.format(outputFormatter);
            dateList.add(currentDate);
            start = start.plusDays(1);
        }

        // Fetch data
        Map<String, Double> salesMap = submitOrderDB.getSalesByDateRangeAndOutlet(fromDate + " 00:00:00", toDate + " 23:59:59", outletId);
        Map<String, Double> returnsMap = returnsDB.getReturnsByDateRangeAndOutlet(fromDate + " 00:00:00", toDate + " 23:59:59", outletId);
        Map<String, Double> reusableReturnsMap = getReusableReturnsByDate(fromDate+" 00:00:00", toDate+" 23:59:59", outletId);

        // Adjusted maps for plotting

        System.out.println("Sales Map: " + salesMap);
        System.out.println("Returns Map: " + returnsMap);
        System.out.println("reusableReturnsMap : " + reusableReturnsMap);


        Map<String, Double> adjustedSalesMap = new HashMap<>();
        Map<String, Double> adjustedReturnsMap = new HashMap<>();

        for (String date : dateList) {
            double salesValue = salesMap.getOrDefault(date, 0.0);
            double returnsValue = returnsMap.getOrDefault(date, 0.0);
            double reusableReturns = reusableReturnsMap.getOrDefault(date, 0.0);

            // Subtract reusable returns
            double adjustedSales = salesValue - reusableReturns;
            double adjustedReturns = returnsValue - reusableReturns;

            System.out.println("Date: " + date);
            System.out.println("Sales Value: " + salesValue);
            System.out.println("Returns Value: " + returnsValue);
            System.out.println("reusableReturns : " + reusableReturns);
            System.out.println("Adjusted Sales: " + adjustedSales);
            System.out.println("Adjusted Returns: " + adjustedReturns);
            // Prevent negative values
            adjustedSales = Math.max(adjustedSales, 0.0);
            adjustedReturns = Math.max(adjustedReturns, 0.0);

            // Save adjusted values
            adjustedSalesMap.put(date, adjustedSales);
            adjustedReturnsMap.put(date, adjustedReturns);

            salesDataList.add(adjustedSales);
            returnsDataList.add(adjustedReturns);
        }

        Log.d("Adjusted Sales", "Sales Data List: " + salesDataList);
        Log.d("Adjusted Returns", "Returns Data List: " + returnsDataList);

        // Calculate totals
        /*double totalSales = salesDataList.stream().mapToDouble(Double::doubleValue).sum();
        double totalReturns = returnsDataList.stream().mapToDouble(Double::doubleValue).sum();*/


        Cursor cursor = returnsDB.getReturnsTotalByReusable(fromDate, toDate, outletId);
        System.out.println("cusor count is:"+cursor.getCount());
        double reusableAmount = calculateTotalReusableAmount(cursor); // Only call once
        cursor.close(); // Close after you're done

        System.out.println("calculateTotalReusableAmount : " + reusableAmount);


         System.out.println("outletId is :"+outletId);
        double totalGross = submitOrderDB.getTotalGrossAmountByStatusForDateRangeAndOutletId(fromDate , toDate, outletId);
        System.out.println("getTotalGrossAmountByStatusForDateRangeAndOutletId : " + totalGross);

        double totalSales = totalGross - reusableAmount;
        System.out.println("totalSales : " + totalSales);

        double totalReturns = returnsDB.getTotalReturnAmountByDate(fromDate + " 00:00:00", toDate + " 23:59:59", outletId) - reusableAmount;
        System.out.println("totalReturns : " + totalReturns);


        // Plot adjusted graph
        System.out.println("adjustedSalesMap: "+adjustedSalesMap);
        System.out.println("adjustedReturnsMap: "+adjustedReturnsMap);
        plotGraph(adjustedSalesMap, adjustedReturnsMap);

        // Calculate percentages
        double salesPercent = calculateSalesPercentage(totalSales, totalReturns);
        double returnPercent = calculateReturnPercentage(totalSales, totalReturns);

        System.out.println("totalSales is going to the updatetextview method is :"+totalSales);

        updateTextViews(totalSales, totalReturns, salesPercent, returnPercent);
    }



   /* @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadTotalGrossAmountForDateRangeOffline(String fromDate, String toDate, String outletId) {
        dateList.clear();
        salesDataList.clear();
        returnsDataList.clear();

        // Define the date formatter
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d"); // Allows single-digit months & days
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Ensures YYYY-MM-DD format

        LocalDate start, end;
        try {
            start = LocalDate.parse(fromDate, inputFormatter);
            end = LocalDate.parse(toDate, inputFormatter);
        } catch (DateTimeParseException e) {
            Log.e("DateParsing", "Error parsing dates: " + e.getMessage());
            return;
        }

        // Generate date list
        while (!start.isAfter(end)) {
            String currentDate = start.format(outputFormatter);
            if (currentDate != null) {
                dateList.add(currentDate);
            }
            start = start.plusDays(1);
        }

        // Fetch sales, returns, and reusable returns data
        Map<String, Double> salesMap = submitOrderDB.getSalesByDateRangeAndOutlet(fromDate + " 00:00:00", toDate + " 23:59:59", outletId);
        Map<String, Double> returnsMap = returnsDB.getReturnsByDateRangeAndOutlet(fromDate + " 00:00:00", toDate + " 23:59:59", outletId);
        Map<String, Double> reusableReturnsMap = getReusableReturnsByDate(fromDate+" 00:00:00", toDate+" 23:59:59", outletId); // Fetch reusable returns by date

        Log.d("Sales Map", "Sales Data: " + salesMap);
        Log.d("Returns Map", "Returns Data: " + returnsMap);
        Log.d("Reusable Returns Map", "Reusable Returns Data: " + reusableReturnsMap);

        // Loop through the dateList and subtract reusable returns from sales and returns data
        for (String date : dateList) {
            double salesValue = salesMap.getOrDefault(date, 0.0);
            double returnsValue = returnsMap.getOrDefault(date, 0.0);
            double reusableReturnsValue = reusableReturnsMap.getOrDefault(date, 0.0);

            // Subtract reusable returns from both sales and returns for the specific date
            double adjustedSales = salesValue - reusableReturnsValue;
            double adjustedReturns = returnsValue - reusableReturnsValue;

            salesDataList.add(adjustedSales);
            returnsDataList.add(adjustedReturns);
        }

        // Get total sales and returns (subtract reusable returns as well)
        double totalSales = submitOrderDB.getTotalGrossAmountByStatusForDateRangeAndOutletId(fromDate, toDate, outletId) - getTotalReusableReturns(fromDate, toDate, outletId);
        double totalReturns = returnsDB.getTotalReturnAmountByDate(fromDate + " 00:00:00", toDate + " 23:59:59", outletId) - getTotalReusableReturns(fromDate, toDate, outletId);

        // Plot the graph including zero values for missing dates
        plotGraph(salesDataList, returnsDataList);

        // Calculate sales & return percentages
        double salesPercent = calculateSalesPercentage(totalSales, totalReturns);
        double returnPercent = calculateReturnPercentage(totalSales, totalReturns);

        // Update text views with calculated values
        updateTextViews(totalSales, totalReturns, salesPercent, returnPercent);
    }*/




    public Map<String, Double> getReusableReturnsByDate(String fromDate, String toDate, String outletId) {
        Map<String, Double> reusableReturnsByDate = new HashMap<>();

        // Query to get reusable returns data (with CSV fields) from the database
        Cursor cursor = returnsDB.getReusableReturnsByDateRange(fromDate + " 00:00:00", toDate + " 23:59:59", outletId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String returnDateTime = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DATE_TIME));
                @SuppressLint("Range")
                String returnReasonCSV = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_RETURN_REASON));
                @SuppressLint("Range")
                String netCSV = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_NET));

                // Extract just the date (yyyy-MM-dd)
                String returnDate = returnDateTime.length() >= 10 ? returnDateTime.substring(0, 10) : returnDateTime;

                String[] reasons = returnReasonCSV.split(",");
                String[] netValues = netCSV.split(",");

                double reusableTotal = 0.0;

                for (int i = 0; i < reasons.length; i++) {
                    if (reasons[i].trim().equalsIgnoreCase("Re-usable")) {
                        try {
                            reusableTotal += Double.parseDouble(netValues[i].trim());
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace(); // optionally log
                        }
                    }
                }

                // Add to the map
                if (reusableReturnsByDate.containsKey(returnDate)) {
                    reusableReturnsByDate.put(returnDate, reusableReturnsByDate.get(returnDate) + reusableTotal);
                } else {
                    reusableReturnsByDate.put(returnDate, reusableTotal);
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        return reusableReturnsByDate;
    }





    private void loadTotalGrossAmountOnline(String fromDate, String toDate, String vanId, String outletId) {
        String url = ApiLinks.SalesAndReturns + "?from_date=" + fromDate + "&to_date=" + toDate + "&van_id=" + vanId;
        String url2 = ApiLinks.TabDashboardDateWiseSalesReturns + "?from_date=" + fromDate + "&to_date=" + toDate + "&van_id=" + vanId;

        if (outletId != null && !outletId.isEmpty()) {
            url += "&outlet_id=" + outletId;
            url2 += "&outlet_id=" + outletId;
        }

        System.out.println("Request URL: " + url);
        System.out.println("Request URL date wise: " + url2);

        Call<DashBoardResponse> getDetails = apiInterface.getDashBoardData(url);
        Call<SalesReturnsForTabList> getDetailsDateWise = apiInterface.getDashBoardGraphData(url2);

        // First API call for dashboard data
        getDetails.enqueue(new Callback<DashBoardResponse>() {
            @Override
            public void onResponse(Call<DashBoardResponse> call, Response<DashBoardResponse> response) {
                System.out.println("onResponse called for first API");

                if (response.isSuccessful() && response.body() != null) {
                    DashBoardResponse dashBoardData = response.body();
                    SalesReturnsForTabList salesData = dashBoardData.getSalesReturnsForTabList();

                    if (salesData != null) {
                        try {
                            double salesTotalNet = isValidDouble(salesData.getActualSales()) ? Double.parseDouble(salesData.getActualSales()) : 0.0;
                            double returnsTotalNet = isValidDouble(salesData.getActualReturns()) ? Double.parseDouble(salesData.getActualReturns()) : 0.0;
                            double salesPercent = isValidDouble(salesData.getSalesPercentage()) ? Double.parseDouble(salesData.getSalesPercentage()) : 0.0;
                            double returnPercent = isValidDouble(salesData.getReturnPercentage()) ? Double.parseDouble(salesData.getReturnPercentage()) : 0.0;

                            System.out.println("salesTotalNet Online: " + salesTotalNet);
                            System.out.println("returnsTotalNet Online: " + returnsTotalNet);

                            salesDataList.add(salesTotalNet);
                            returnsDataList.add(returnsTotalNet);

                            updateTextViews(salesTotalNet, returnsTotalNet, salesPercent, returnPercent);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            salesDataList.add(0.0);
                            returnsDataList.add(0.0);
                            updateTextViews(0.0, 0.0, 0.0, 0.0);
                        }
                    } else {
                        salesDataList.add(0.0);
                        returnsDataList.add(0.0);
                        updateTextViews(0.0, 0.0, 0.0, 0.0);
                    }
                } else {
                    System.out.println("Response not successful or empty body: " + response.code());
                    salesDataList.add(0.0);
                    returnsDataList.add(0.0);
                    updateTextViews(0.0, 0.0, 0.0, 0.0);
                }
            }

            @Override
            public void onFailure(Call<DashBoardResponse> call, Throwable t) {
                System.out.println("onFailure called for first API: " + t.getMessage());
                t.printStackTrace();

                salesDataList.add(0.0);
                returnsDataList.add(0.0);
                updateTextViews(0.0, 0.0, 0.0, 0.0);
            }
        });

        // Second API call for dashboard graph data
        getDetailsDateWise.enqueue(new Callback<SalesReturnsForTabList>() {
            @Override
            public void onResponse(Call<SalesReturnsForTabList> call, Response<SalesReturnsForTabList> response) {
                System.out.println("onResponse called for second API");

                if (response.isSuccessful() && response.body() != null) {
                    SalesReturnsForTabList salesReturnsForTabList = response.body();
                    if (salesReturnsForTabList != null) {
                        parseAndPlotGraph(salesReturnsForTabList);
                    } else {
                        System.out.println("SalesReturnsForTabList is empty or null.");
                    }
                } else {
                    System.out.println("Second API response not successful or empty body: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SalesReturnsForTabList> call, Throwable t) {
                System.out.println("onFailure called for second API: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void parseAndPlotGraph(SalesReturnsForTabList salesReturnsForTabList) {
        LineChart lineChart = findViewById(R.id.lineChart);
        Map<String, Double> salesMap = new HashMap<>();
        Map<String, Double> returnMap = new HashMap<>();

        try {
            for (SalesReturnsForTab salesReturn : salesReturnsForTabList.getSalesReturnsForDateForTab()) {
                String date = salesReturn.getDeliveredDate();
                double sales = isValidDouble(salesReturn.getNetAmount()) ? Double.parseDouble(salesReturn.getNetAmount()) : 0.0;
                double returns = isValidDouble(salesReturn.getReturnAmt()) ? Double.parseDouble(salesReturn.getReturnAmt()) : 0.0;

                salesMap.put(date, sales);
                returnMap.put(date, returns);
            }

            List<Entry> salesEntries = new ArrayList<>();
            List<Entry> returnsEntries = new ArrayList<>();
            List<String> dateLabels = new ArrayList<>();

            List<String> sortedDates = new ArrayList<>(salesMap.keySet());
            Collections.sort(sortedDates);

            for (int i = 0; i < sortedDates.size(); i++) {
                String date = sortedDates.get(i);
                double salesValue = salesMap.getOrDefault(date, 0.0);
                double returnValue = returnMap.getOrDefault(date, 0.0);

                salesEntries.add(new Entry(i, (float) salesValue));
                returnsEntries.add(new Entry(i, (float) returnValue));
                dateLabels.add(date);
            }

            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.GREEN);
            salesDataSet.setCircleColor(Color.GREEN);
            salesDataSet.setValueTextColor(Color.BLACK);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);
            returnsDataSet.setCircleColor(Color.RED);
            returnsDataSet.setValueTextColor(Color.BLACK);

            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            lineChart.setData(lineData);

            // Configure X-axis
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelRotationAngle(-45);

            // Configure Y-axis
            YAxis leftYAxis = lineChart.getAxisLeft();
            leftYAxis.setAxisMinimum(0f);
            lineChart.getAxisRight().setEnabled(false);

            // Configure legend
            Legend legend = lineChart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextColor(Color.BLACK);

            // Refresh chart
            lineChart.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isValidDouble(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void plotGraph(Map<String, Double> salesData, Map<String, Double> returnsData) {
        LineChart lineChart = findViewById(R.id.lineChart);
        List<Entry> salesEntries = new ArrayList<>();
        List<Entry> returnsEntries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>(); // Stores date labels for X-axis

        // Combine and sort the keys (dates)
        Set<String> allDates = new TreeSet<>(salesData.keySet());
        allDates.addAll(returnsData.keySet());

        int index = 0;
        for (String date : allDates) {
            Double salesValue = salesData.get(date);
            Double returnValue = returnsData.get(date);

            salesEntries.add(new Entry(index, salesValue != null ? salesValue.floatValue() : 0f));
            returnsEntries.add(new Entry(index, returnValue != null ? returnValue.floatValue() : 0f));
            dateLabels.add(date);
            index++;
        }

        // Create datasets
        LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
        salesDataSet.setColor(Color.GREEN);
        salesDataSet.setCircleColor(Color.GREEN);
        salesDataSet.setValueTextColor(Color.BLACK);

        LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
        returnsDataSet.setColor(Color.RED);
        returnsDataSet.setCircleColor(Color.RED);
        returnsDataSet.setValueTextColor(Color.BLACK);

        // Combine datasets
        LineData lineData = new LineData(salesDataSet, returnsDataSet);
        lineChart.setData(lineData);

        // Configure X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        // Configure Y-axis
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setAxisMinimum(0f);
        lineChart.getAxisRight().setEnabled(false);

        // Configure legend
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLACK);

        // Refresh chart
        lineChart.invalidate();
    }



    public double calculateTotalReusableAmount(Cursor cursor) {
        double totalReusableAmount = 0.0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String netCsv = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_NET));
                String reasonCsv = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_RETURN_REASON));

                String[] netValues = netCsv.split(",");
                String[] reasonValues = reasonCsv.split(",");

                for (int i = 0; i < reasonValues.length; i++) {
                    String reason = reasonValues[i].trim();
                    if (reason.equalsIgnoreCase("Re-usable")) {
                        try {
                            totalReusableAmount += Double.parseDouble(netValues[i].trim());
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } while (cursor.moveToNext());
        }

        return totalReusableAmount;
    }








    private void setDefaultDateValues() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        fromDateButton.setText(todayDate);
        toDateButton.setText(todayDate);
    }
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


    private void loadTodaysOrderData() {
        String todayDate = getCurrentDateInDubaiZone();
        String todayFromDate = todayDate + " 00:00:00";
        String todayToDate = todayDate + " 23:59:59";
        System.out.println("todayDate in dubai : "+todayDate);

        int todayOrderCount = submitOrderDB.getOrderCountByDate(todayDate);
        System.out.println("todayOrderCount is :"+todayOrderCount);
        int todayDeliveredCount = submitOrderDB.getDeliveredOrderCountByDate(todayDate+"%");
        System.out.println("todayDeliveredCount is :"+todayDeliveredCount);
        int todayInvoiceCount = submitOrderDB.getInvoiceCountForDeliveredOrders(todayDate+"%");
        System.out.println("todayInvoiceCount is :"+todayInvoiceCount);
        int todayOutOfRoute = submitOrderDB.getMExOrderCountByDate(todayFromDate, todayToDate);
        System.out.println("todayOutOfRoute is :"+todayOutOfRoute);

        int missedCallsCount = Math.abs(todayOrderCount - todayDeliveredCount);

        Log.d(TAG, String.format("Today's Order Count: %d, Delivered Count: %d, Invoice Count: %d, Out Of Route Count: %d, Missed Calls Count: %d",
                todayOrderCount, todayDeliveredCount, todayInvoiceCount, todayOutOfRoute, missedCallsCount));

        displayTodaysData(todayOrderCount, todayDeliveredCount, todayInvoiceCount, todayOutOfRoute, missedCallsCount);
    }

    private void displayTodaysData(int orderCount, int deliveredCount, int invoiceCount, int outOfRouteCount, int missedCallsCount) {
        totalOrderCountPlannedTextView.setText(String.valueOf(orderCount));
        deliveredCountTextView.setText(String.valueOf(deliveredCount));
        invoiceCountTextView.setText(String.valueOf(invoiceCount));
        outOfRouteCountTextView.setText(String.valueOf(outOfRouteCount));
        missedCallsTextView.setText(String.valueOf(missedCallsCount));
    }


    private void loadOutletNames() {
        // Step 1: Get unique outlet IDs
        List<String> uniqueOutletIds = submitOrderDB.getUniqueOutletIds();

        // Step 2: Build a map of outletName → outletId
        Map<String, String> outletNameToIdMap = new HashMap<>();
        List<String> outletNames = new ArrayList<>();

        for (String id : uniqueOutletIds) {
            String name = outletByIdDB.getOutletNameById2(id);
            if (name != null) {
                outletNames.add(name);
                outletNameToIdMap.put(name, id);
            }
        }

        // Step 3: Display names in AutoCompleteTextView
        if (!outletNames.isEmpty()) {
            System.out.println("outletNames: " + outletNames);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_analysis_graph, outletNames);
            actvOutlet.setAdapter(adapter);
            actvOutlet.setOnClickListener(view -> {
                actvOutlet.showDropDown();
                actvOutlet.setDropDownHeight(500); // Height in pixels, adjust as needed
                actvOutlet.setDropDownWidth(450);  // Width in pixels, adjust as needed
            });

            actvOutlet.setOnItemClickListener((adapterView, view, i, l) -> {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                outletId = outletNameToIdMap.get(selectedName); // Correct mapping
                System.out.println("Selected Outlet Name: " + selectedName);
                System.out.println("OutletId is: " + outletId);
            });
        }
    }

    private void loadOutletNamesOnline() {
        List<String> uniqueOutletIds = outletByIdDB.getAllUniqueOutletIds();

        // Map to hold outletName -> outletId
        Map<String, String> outletNameToIdMap = new HashMap<>();
        List<String> outletNames = new ArrayList<>();

        for (String outletId : uniqueOutletIds) {
            String outletName = outletByIdDB.getOutletNameById2(outletId);
            if (outletName != null) {
                outletNames.add(outletName);
                outletNameToIdMap.put(outletName, outletId);
            }
        }

        if (!outletNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_analysis_graph, outletNames);
            actvOutlet.setAdapter(adapter);

            actvOutlet.setOnClickListener(view -> {
                actvOutlet.showDropDown();
                actvOutlet.setDropDownHeight(500);
                actvOutlet.setDropDownWidth(450);
            });

            actvOutlet.setOnItemClickListener((adapterView, view, i, l) -> {
                String selectedName = adapterView.getItemAtPosition(i).toString();
                outletId = outletNameToIdMap.get(selectedName);
                System.out.println("Selected Outlet ID: " + outletId);
            });
        }
    }

    private void showDatePickerDialog(boolean isFromDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedMonth = String.format("%02d", selectedMonth + 1);
            String formattedDay = String.format("%02d", selectedDay);
            String selectedDateStr = selectedYear + "-" + formattedMonth + "-" + formattedDay;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            try {
                Date selectedDate = sdf.parse(selectedDateStr);

                if (isFromDate) {
                    if (toDate != null) {
                        Date to = sdf.parse(toDate);
                        if (Math.abs(to.getTime() - selectedDate.getTime()) > 30L * 24 * 60 * 60 * 1000) {
                            Toast.makeText(this, "From and To date should not exceed 1 month.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    fromDate = selectedDateStr;
                    fromDateButton.setText(selectedDateStr);
                } else {
                    if (fromDate != null) {
                        Date from = sdf.parse(fromDate);
                        if (Math.abs(selectedDate.getTime() - from.getTime()) > 30L * 24 * 60 * 60 * 1000) {
                            Toast.makeText(this, "From and To date should not exceed 1 month.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    toDate = selectedDateStr;
                    toDateButton.setText(selectedDateStr);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }, year, month, day);

        datePickerDialog.show();
    }



    // Method to calculate return percentage
    public double calculateReturnPercentage(double totalSales, double totalReturns) {
        if (totalSales != 0) {
            return (totalReturns / totalSales) * 100.0;
        }
        return 0.0; // If totalSales is 0, return percentage should be 0
    }

    // Method to calculate sales percentage
    public double calculateSalesPercentage(double totalSales, double totalReturns) {
        if (totalReturns == 0) {
            return 100.0;  // If no returns, sales should be 100%
        }
        return 100.0 - calculateReturnPercentage(totalSales, totalReturns);
    }

    private void currentMonthAndYearToDate(String monthStart, String monthEnd, String yearStart, String yearEnd, String vanId) {
        // Monthly Sales
        String monthUrl = ApiLinks.SalesAndReturns + "?from_date=" + monthStart + "&to_date=" + monthEnd + "&van_id=" + vanId;
        Call<DashBoardResponse> monthlyCall = apiInterface.getDashBoardData(monthUrl);

        monthlyCall.enqueue(new Callback<DashBoardResponse>() {
            @Override
            public void onResponse(Call<DashBoardResponse> call, Response<DashBoardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SalesReturnsForTabList salesData = response.body().getSalesReturnsForTabList();
                    if (salesData != null) {
                        salesTotalNetMonthly = isValidDouble(salesData.getActualSales())
                                ? Double.parseDouble(salesData.getActualSales()) : 0.0;
                        mtdTextView.setText(String.format(Locale.US, "%.2f", salesTotalNetMonthly));

                        // Save only if yearly is already available
                        if (salesTotalNetYearly != -1) {
                            userDetailsDb.updateSalesYtdAndSalesMtd(
                                    String.valueOf(salesTotalNetYearly),
                                    String.valueOf(salesTotalNetMonthly));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DashBoardResponse> call, Throwable t) {
                System.out.println("Monthly API call failed: " + t.getMessage());
            }
        });

        // Yearly Sales
        String yearUrl = ApiLinks.SalesAndReturns + "?from_date=" + yearStart + "&to_date=" + yearEnd + "&van_id=" + vanId;
        Call<DashBoardResponse> yearlyCall = apiInterface.getDashBoardData(yearUrl);

        yearlyCall.enqueue(new Callback<DashBoardResponse>() {
            @Override
            public void onResponse(Call<DashBoardResponse> call, Response<DashBoardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SalesReturnsForTabList salesData = response.body().getSalesReturnsForTabList();
                    if (salesData != null) {
                        salesTotalNetYearly = isValidDouble(salesData.getActualSales())
                                ? Double.parseDouble(salesData.getActualSales()) : 0.0;
                        ytdSalesTextView.setText(String.format(Locale.US, "%.2f", salesTotalNetYearly));

                        // Save only if monthly is already available
                        if (salesTotalNetMonthly != -1) {
                            userDetailsDb.updateSalesYtdAndSalesMtd(
                                    String.valueOf(salesTotalNetYearly),
                                    String.valueOf(salesTotalNetMonthly));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DashBoardResponse> call, Throwable t) {
                System.out.println("Yearly API call failed: " + t.getMessage());
            }
        });
    }


    @SuppressLint("Range")
    private void loadYearlyAndMonthlyDateRangesOffline() {
        Cursor cursor = userDetailsDb.readSalesYtdAndSalesMtd();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String ytdSales = cursor.getString(cursor.getColumnIndex(UserDetailsDb.SALES_YTD));
                String mtdSales = cursor.getString(cursor.getColumnIndex(UserDetailsDb.SALES_MTD));

                ytdSalesTextView.setText(ytdSales != null ? ytdSales : "0");
                mtdTextView.setText(mtdSales != null ? mtdSales : "0");
            }
            cursor.close(); // Always close the cursor
        }
    }


    private void loadYearlyAndMonthlyDateRanges() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Current date as endDate
        String endDate = sdf.format(calendar.getTime());

        // Start of the year
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startOfYear = sdf.format(calendar.getTime());

        // Reset calendar to now
        calendar = Calendar.getInstance();

        // Start of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startOfMonth = sdf.format(calendar.getTime());

        // Output for debugging
        Log.d("DATE_RANGE", "Year Start: " + startOfYear + ", End: " + endDate);
        Log.d("DATE_RANGE", "Month Start: " + startOfMonth + ", End: " + endDate);

        // Example: Call your API loading methods here
        currentMonthAndYearToDate(startOfMonth,endDate,startOfYear, endDate, vanId); // for yearly
        // loadTotalGrossAmountOnline(startOfMonth, endDate, vanId, outletId); // for monthly
    }


    private void updateTotalCounts(String date) {
        totalOrders = submitOrderDB.getOrderCountByDate(date);
        totalDeliveredCount = submitOrderDB.getDeliveredOrderCountByDate(date+"%");
        totalInvoiceCount = submitOrderDB.getInvoiceCountForDeliveredOrders(date+"%");
        totalMissedCalls = totalOrders - totalDeliveredCount;
    }


    private void updateTextViews(double totalSales,double totalReturns,double salesPercent,double returnPercent) {
        ReturnTextView.setText("Return %: " + String.format("%.2f", returnPercent) + "%");
        SalesTextView.setText("Sales %: " + String.format("%.2f", salesPercent) + "%");
        totalSalesTextView.setText(String.format("Sales: %.2f", totalSales));
        totalReturnsTextView.setText(String.format("Returns: %.2f", totalReturns));

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AnalysisGraph.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
    }
    public static String getCurrentDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

//    public boolean isEodSyncDone() {
//        boolean delivered = submitOrderDB.checkSyncStatus();
//        boolean returned = returnsDB.checkSyncStatus();
//        boolean unloaded = stockDB.checkUnloadSync();
//        boolean loadFromWarehouse = stockDB.checkLoadFromWarehouseSync();
//
//        // EOD sync is considered done only if all statuses are false (i.e., nothing pending)
//        return !(delivered || returned || unloaded || loadFromWarehouse);
//    }


}


