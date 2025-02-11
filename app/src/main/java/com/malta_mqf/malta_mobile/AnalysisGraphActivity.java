package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";

    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;

    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, ReturnTextView, SalesTextView;
    private TextView totalOrderCountPlannedTextView, deliveredCountTextView, invoiceCountTextView, outOfRouteCountTextView, missedCallsTextView;

    private String fromDate, toDate;
    private ArrayList<String> dateLabels = new ArrayList<>();  // X-axis labels for dates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        initializeViews();
        setupListeners();
        setDefaultDateValues();
        loadTodaysOrderData();
    }

    private void initializeViews() {
        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this);

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
    }

    private void setupListeners() {
        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void setDefaultDateValues() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());

        fromDateButton.setText(todayDate);
        toDateButton.setText(todayDate);
    }

    private void loadTodaysOrderData() {
        String todayDate = getCurrentDateString();
        String todayFromDate = todayDate + " 00:00:00";
        String todayToDate = todayDate + " 23:59:59";

        int todayOrderCount = submitOrderDB.getOrderCountByDate(todayDate);
        int todayDeliveredCount = submitOrderDB.getDeliveredOrderCountByDate(todayDate);
        int todayInvoiceCount = submitOrderDB.getInvoiceCountForDeliveredOrders(todayDate);
        int todayOutOfRoute = submitOrderDB.getMExOrderCountByDate(todayFromDate, todayToDate);

        int missedCallsCount = todayOrderCount - todayDeliveredCount;

        Log.d(TAG, String.format("Today's Order Count: %d, Delivered Count: %d, Invoice Count: %d, Out Of Route Count: %d, Missed Calls Count: %d",
                todayOrderCount, todayDeliveredCount, todayInvoiceCount, todayOutOfRoute, missedCallsCount));

        displayTodaysData(todayOrderCount, todayDeliveredCount, todayInvoiceCount, todayOutOfRoute, missedCallsCount);
    }

    private String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void displayTodaysData(int orderCount, int deliveredCount, int invoiceCount, int outOfRouteCount, int missedCallsCount) {
        totalOrderCountPlannedTextView.setText(String.valueOf(orderCount));
        deliveredCountTextView.setText(String.valueOf(deliveredCount));
        invoiceCountTextView.setText(String.valueOf(invoiceCount));
        outOfRouteCountTextView.setText(String.valueOf(outOfRouteCount));
        missedCallsTextView.setText(String.valueOf(missedCallsCount));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    Log.d(TAG, "Selected date: " + selectedDate);

                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59";
                        toDateButton.setText(selectedDate);
                    }

                    if (fromDate != null && toDate != null) {
                        Log.d(TAG, "From date: " + fromDate + ", To date: " + toDate);
                        loadTotalGrossAmount(fromDate, toDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount(String from, String to) {
        Log.d(TAG, "Loading total gross amount for dates: " + from + " to " + to);
        new LoadTotalGrossAmountTask().execute(from, to);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double Sales = 0.0;
        private double Returns = 0.0;
        private double returnPercentage = 0.0;
        private double salesPercentage = 0.0;
        private int totalOrders = 0;
        private int totalDeliveredCount = 0;
        private int totalInvoiceCount = 0;
        private int totalMissedCalls = 0;

        private final float[] salesData = new float[31]; // Sales data for each day
        private final float[] returnsData = new float[31]; // Returns data for each day
        private final String[] dateData = new String[31]; // Date labels for X-axis

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);
            Log.d(TAG, "Dates between: " + dates);

            for (String date : dates) {
                processDateData(date, dateFormat, dayFormat);
            }

            return createLineData();
        }

        private void processDateData(String date, SimpleDateFormat dateFormat, SimpleDateFormat dayFormat) {
            try {
                int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1;

                // Sales Data
                double totalForSales = getSalesForDate(date);
                salesData[dayIndex] = (float) totalForSales;
                Sales += totalForSales;

                // Returns Data
                double totalForReturns = returnsDB.getTotalReturnAmountByDate(date);
                returnsData[dayIndex] = (float) totalForReturns;
                Returns += totalForReturns;

                // Store Date for X-axis
                dateData[dayIndex] = date;

                // Calculate percentages
                calculateReturnAndSalesPercentage();

                // Update totals
                updateTotalCounts(date);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + date, e);
            }
        }

        private double getSalesForDate(String date) {
            return submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE")
                    + submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERED");
        }

        private void calculateReturnAndSalesPercentage() {
            if (Sales != 0) {
                returnPercentage = (Returns / Sales) * 100.0;
            }

            if (Returns != 0) {
                salesPercentage =  100.0-returnPercentage;
            }
        }

        private void updateTotalCounts(String date) {
            totalOrders = submitOrderDB.getOrderCountByDate(date);
            totalDeliveredCount = submitOrderDB.getDeliveredOrderCountByDate(date);
            totalInvoiceCount = submitOrderDB.getInvoiceCountForDeliveredOrders(date);
            totalMissedCalls = totalOrders - totalDeliveredCount;
        }

        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = dateFormat.parse(fromDate);
                Date endDate = dateFormat.parse(toDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date range", e);
            }
            return dates;
        }

        private LineData createLineData() {
            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();

            for (int i = 0; i < dateData.length; i++) {
                if (dateData[i] != null) {
                    salesEntries.add(new Entry(i, salesData[i]));
                    returnsEntries.add(new Entry(i, returnsData[i]));
                }
            }

            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.GREEN);
            salesDataSet.setCircleColor(Color.GREEN);
            salesDataSet.setDrawValues(true); // Enable value display
            salesDataSet.setLineWidth(2f);
            salesDataSet.setValueTextSize(10f);
            salesDataSet.setValueTextColor(Color.BLACK);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);
            returnsDataSet.setCircleColor(Color.RED);
            returnsDataSet.setDrawValues(true); // Enable value display
            returnsDataSet.setLineWidth(2f);
            returnsDataSet.setValueTextSize(10f);
            returnsDataSet.setValueTextColor(Color.BLACK);

            return new LineData(salesDataSet, returnsDataSet);
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            super.onPostExecute(lineData);

            // Update the chart
            lineChart.setData(lineData);
            lineChart.invalidate();

            // Configure chart appearance
            setupChartAppearance();

            // Update text views
            updateTextViews();
        }

        private void setupChartAppearance() {
            Legend legend = lineChart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextColor(Color.BLACK);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(dateData));

            YAxis leftYAxis = lineChart.getAxisLeft();
            leftYAxis.setAxisMinimum(0f);

            YAxis rightYAxis = lineChart.getAxisRight();
            rightYAxis.setEnabled(false);
        }

        private void updateTextViews() {
            //SalesTextView.setText(String.format("Sales %: " , "%.2f%%", salesPercentage));
            //ReturnTextView.setText(String.format("Returns %: ","%.2f%%", returnPercentage));
            ReturnTextView.setText("Return %: " + String.format("%.2f", returnPercentage) + "%");
            SalesTextView.setText("Sales %: " + String.format("%.2f", salesPercentage) + "%");
            totalSalesTextView.setText(String.format("Sales: %.2f", Sales));
            totalReturnsTextView.setText(String.format("Returns: %.2f", Returns));

        }
    }
}




/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, totalOrderCountPlannedTextView, deliveredCountTextView, missedCallsTextView; // Added missedCallsTextView
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText);
        totalOrderCountPlannedTextView = findViewById(R.id.totalOrderCountPlannedText); // Initialize the new TextView for planned orders
        deliveredCountTextView = findViewById(R.id.completedOrdersText); // Initialize TextView for delivered count
        missedCallsTextView = findViewById(R.id.missedCallsText); // Initialize missedCallsTextView

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));

        // Set default date values as today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());
        fromDateButton.setText(todayDate);
        toDateButton.setText(todayDate);

        // Load today's order data
        loadTodaysOrderData();
    }

    private void loadTodaysOrderData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String todayDate = dateFormat.format(new Date());

        // Fetch order count and delivered count for today
        int todayOrderCount = submitOrderDB.getOrderCountByDate(todayDate);
        int todayDeliveredCount = submitOrderDB.getDeliveredOrderCountByDate(todayDate);

        // Calculate missed calls by subtracting delivered count from order count
        int missedCallsCount = todayOrderCount - todayDeliveredCount;

        Log.d(TAG, "Today's Order Count: " + todayOrderCount + ", Delivered Count: " + todayDeliveredCount + ", Missed Calls Count: " + missedCallsCount);

        // Display the fetched values in the UI
        totalOrderCountPlannedTextView.setText("Total Orders: " + todayOrderCount);
        deliveredCountTextView.setText("Total Delivered: " + todayDeliveredCount);
        missedCallsTextView.setText("Total Missed Calls: " + missedCallsCount); // Display missed calls count
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    Log.d(TAG, "Selected date: " + selectedDate); // Log the selected date
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59";
                        toDateButton.setText(selectedDate);
                    }

                    if (fromDate != null && toDate != null) {
                        Log.d(TAG, "From date: " + fromDate + ", To date: " + toDate); // Log the from and to dates
                        loadTotalGrossAmount(fromDate, toDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount(String from, String to) {
        Log.d(TAG, "Loading total gross amount for dates: " + from + " to " + to); // Log the date range for loading data
        new LoadTotalGrossAmountTask().execute(from, to);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private double totalReturns = 0.0;
        private int totalOrders = 0;
        private int totalDeliveredCount = 0;
        private int totalMissedCalls = 0;
        private final float[] salesData = new float[31]; // Array to store sales data for 1-31 days
        private final float[] returnsData = new float[31]; // Array to store returns data for 1-31 days

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);
            Log.d(TAG, "Dates between: " + dates);

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1;

                    double totalForSales = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE")
                            + submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERED");
                    salesData[dayIndex] = (float) totalForSales;
                    totalSales += totalForSales;
                    Log.d(TAG, "Sales for " + date + ": " + totalForSales);

                    double totalForReturns = returnsDB.getTotalReturnAmountByDate(fromDate, toDate);
                    returnsData[dayIndex] = (float) totalForReturns;
                    totalReturns += totalForReturns;
                    Log.d(TAG, "Returns for " + date + ": " + totalForReturns);

                    int orderCount = submitOrderDB.getOrderCountByDate(date);
                    totalOrders += orderCount;
                    Log.d(TAG, "Order count for " + date + ": " + orderCount);

                    int deliveredCount = submitOrderDB.getDeliveredOrderCountByDate(date);
                    totalDeliveredCount += deliveredCount;
                    Log.d(TAG, "Delivered count for " + date + ": " + deliveredCount);

                    int missedCalls = orderCount - deliveredCount;
                    totalMissedCalls += missedCalls;
                    Log.d(TAG, "Missed calls for " + date + ": " + missedCalls);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();

            for (int i = 0; i < 31; i++) {
                salesEntries.add(new Entry(i, salesData[i]));
                returnsEntries.add(new Entry(i, returnsData[i]));
            }

            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.BLUE);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);

            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            return lineData;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                lineChart.invalidate();

                Log.d(TAG, "Total Sales: " + totalSales + ", Total Returns: " + totalReturns + ", Total Orders: " + totalOrders + ", Total Missed Calls: " + totalMissedCalls);

                totalSalesTextView.setText("Total Sales: " + String.format("%.2f", totalSales));
                totalReturnsTextView.setText("Total Returns: " + String.format("%.2f", totalReturns));
                totalOrderCountPlannedTextView.setText("Total Orders: " + totalOrders);
                deliveredCountTextView.setText("Total Delivered: " + totalDeliveredCount);
                missedCallsTextView.setText("Total Missed Calls: " + totalMissedCalls);

                // Now configure the X-axis to display 1-31 (days of the month)
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        // Convert float to int (1-31)
                        int day = Math.round(value) + 1; // Convert index to day number (1-31)
                        return String.valueOf(day); // Return the day as a string (1-31)
                    }
                });

                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f); // Make sure each entry is evenly spaced (1 day per entry)
                xAxis.setLabelRotationAngle(45f); // Rotate the labels if necessary for readability
            });
        }

        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start = dateFormat.parse(fromDate);
                Date end = dateFormat.parse(toDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                while (!calendar.getTime().after(end)) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while getting dates between: " + fromDate + " and " + toDate, e);
            }
            return dates;
        }
    }
}



/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, totalOrderCountPlannedTextView, deliveredCountTextView; // Added for delivered count
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText);
        totalOrderCountPlannedTextView = findViewById(R.id.totalOrderCountPlannedText); // Initialize the new TextView for planned orders
        deliveredCountTextView = findViewById(R.id.completedOrdersText); // Initialize TextView for delivered count

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));

        // Set default date values as today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(new Date());
        fromDateButton.setText(todayDate);
        toDateButton.setText(todayDate);

        // Load today's order data
        loadTodaysOrderData();
    }

    private void loadTodaysOrderData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String todayDate = dateFormat.format(new Date());

        // Fetch order count and delivered count for today
        int todayOrderCount = submitOrderDB.getOrderCountByDate(todayDate);
        int todayDeliveredCount = submitOrderDB.getDeliveredOrderCountByDate(todayDate);

        Log.d(TAG, "Today's Order Count: " + todayOrderCount + ", Delivered Count: " + todayDeliveredCount);

        // Display the fetched values in the UI
        totalOrderCountPlannedTextView.setText("Total Orders: " + todayOrderCount);
        deliveredCountTextView.setText("Total Delivered: " + todayDeliveredCount);
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    Log.d(TAG, "Selected date: " + selectedDate); // Log the selected date
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59";
                        toDateButton.setText(selectedDate);
                    }

                    if (fromDate != null && toDate != null) {
                        Log.d(TAG, "From date: " + fromDate + ", To date: " + toDate); // Log the from and to dates
                        loadTotalGrossAmount(fromDate, toDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount(String from, String to) {
        Log.d(TAG, "Loading total gross amount for dates: " + from + " to " + to); // Log the date range for loading data
        new LoadTotalGrossAmountTask().execute(from, to);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private double totalReturns = 0.0;
        private int totalOrders = 0; // Store the total order count
        private int totalDeliveredCount = 0; // Store the total delivered count
        private final float[] salesData = new float[31];
        private final float[] returnsData = new float[31];

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);
            Log.d(TAG, "Dates between: " + dates); // Log the dates between the from and to dates

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1;

                    double totalForSales = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE")
                            + submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERED");
                    salesData[dayIndex] = (float) totalForSales;
                    totalSales += totalForSales;
                    Log.d(TAG, "Sales for " + date + ": " + totalForSales); // Log sales for each date

                    // Fetch returns data using the updated method with both fromDate and toDate
                    double totalForReturns = returnsDB.getTotalReturnAmountByDate(fromDate, toDate);
                    returnsData[dayIndex] = (float) totalForReturns;
                    totalReturns += totalForReturns;
                    Log.d(TAG, "Returns for " + date + ": " + totalForReturns); // Log returns for each date


                    // Fetch order count for the day
                    int orderCount = submitOrderDB.getOrderCountByDate(date);
                    totalOrders += orderCount; // Update total orders
                    Log.d(TAG, "Order count for " + date + ": " + orderCount); // Log the order count for each date

                    // Fetch the delivered count for the day
                    int deliveredCount = submitOrderDB.getDeliveredOrderCountByDate(date); // Fetch delivered count using updated method
                    totalDeliveredCount += deliveredCount; // Update total delivered count
                    Log.d(TAG, "Delivered count for " + date + ": " + deliveredCount); // Log the delivered count for each date

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();

            for (int i = 0; i < 31; i++) {
                salesEntries.add(new Entry(i + 1, salesData[i]));
                returnsEntries.add(new Entry(i + 1, returnsData[i]));
            }

            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.BLUE);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);

            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            return lineData;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                lineChart.invalidate();

                Log.d(TAG, "Total Sales: " + totalSales + ", Total Returns: " + totalReturns + ", Total Orders: " + totalOrders); // Log the final totals

                // Set total sales, returns, orders, and delivered count in the UI
                totalSalesTextView.setText("Total Sales: " + String.format("%.2f", totalSales));
                totalReturnsTextView.setText("Total Returns: " + String.format("%.2f", totalReturns));
                totalOrderCountPlannedTextView.setText("Total Orders: " + totalOrders); // Display the total order count
                deliveredCountTextView.setText("Total Delivered: " + totalDeliveredCount); // Display the total delivered count
            });
        }

        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start = dateFormat.parse(fromDate);
                Date end = dateFormat.parse(toDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                while (!calendar.getTime().after(end)) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while getting dates between: " + fromDate + " and " + toDate, e);
            }
            return dates;
        }
    }
}



/*
package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, totalOrderCountPlannedTextView; // Updated for planned
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText);
        totalOrderCountPlannedTextView = findViewById(R.id.totalOrderCountPlannedText); // Initialize the new TextView for planned orders

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    Log.d(TAG, "Selected date: " + selectedDate); // Log the selected date
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59";
                        toDateButton.setText(selectedDate);
                    }

                    if (fromDate != null && toDate != null) {
                        Log.d(TAG, "From date: " + fromDate + ", To date: " + toDate); // Log the from and to dates
                        loadTotalGrossAmount(fromDate, toDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount(String from, String to) {
        Log.d(TAG, "Loading total gross amount for dates: " + from + " to " + to); // Log the date range for loading data
        new LoadTotalGrossAmountTask().execute(from, to);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private double totalReturns = 0.0;
        private int totalOrders = 0; // Store the total order count
        private final float[] salesData = new float[31];
        private final float[] returnsData = new float[31];

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);
            Log.d(TAG, "Dates between: " + dates); // Log the dates between the from and to dates

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1;

                    double totalForSales = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE")
                            + submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERED");
                    salesData[dayIndex] = (float) totalForSales;
                    totalSales += totalForSales;
                    Log.d(TAG, "Sales for " + date + ": " + totalForSales); // Log sales for each date

                    double totalForReturns = returnsDB.getTotalReturnAmountByDate(fromDate, toDate);
                    returnsData[dayIndex] = (float) totalForReturns;
                    totalReturns += totalForReturns;
                    Log.d(TAG, "Returns for " + date + ": " + totalForReturns); // Log returns for each date

                    // Fetch order count for the day
                    int orderCount = submitOrderDB.getOrderCountByDate(date);
                    totalOrders += orderCount; // Update total orders
                    Log.d(TAG, "Order count for " + date + ": " + orderCount); // Log the order count for each date
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();

            for (int i = 0; i < 31; i++) {
                salesEntries.add(new Entry(i + 1, salesData[i]));
                returnsEntries.add(new Entry(i + 1, returnsData[i]));
            }

            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.BLUE);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);

            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            return lineData;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                lineChart.invalidate();

                Log.d(TAG, "Total Sales: " + totalSales + ", Total Returns: " + totalReturns + ", Total Orders: " + totalOrders); // Log the final totals

                // Set total sales, returns, and orders in the planned section
                totalSalesTextView.setText("Total Sales: " + totalSales);
                totalReturnsTextView.setText("Total Returns: " + totalReturns);
                totalOrderCountPlannedTextView.setText("Total Orders: " + totalOrders); // Display the total order count
                           });
        }

        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start = dateFormat.parse(fromDate);
                Date end = dateFormat.parse(toDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                while (!calendar.getTime().after(end)) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while getting dates between: " + fromDate + " and " + toDate, e);
            }
            return dates;
        }
    }
}

 */

/*
package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, totalOrderCountPlannedTextView; // Updated for planned
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText);
        totalOrderCountPlannedTextView = findViewById(R.id.totalOrderCountPlannedText); // Initialize the new TextView for planned orders

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59";
                        toDateButton.setText(selectedDate);
                    }

                    if (fromDate != null && toDate != null) {
                        loadTotalGrossAmount(fromDate, toDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount(String from, String to) {
        new LoadTotalGrossAmountTask().execute(from, to);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private double totalReturns = 0.0;
        private int totalOrders = 0; // Store the total order count
        private final float[] salesData = new float[31];
        private final float[] returnsData = new float[31];

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);
            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1;

                    double totalForSales = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE")
                            + submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERED");
                    salesData[dayIndex] = (float) totalForSales;
                    totalSales += totalForSales;

                    double totalForReturns = returnsDB.getTotalReturnAmountByDate(fromDate, toDate);
                    returnsData[dayIndex] = (float) totalForReturns;
                    totalReturns += totalForReturns;

                    // Fetch order count for the day
                    int orderCount = submitOrderDB.getOrderCountByDate(date);
                    totalOrders += orderCount; // Update total orders
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();

            for (int i = 0; i < 31; i++) {
                salesEntries.add(new Entry(i + 1, salesData[i]));
                returnsEntries.add(new Entry(i + 1, returnsData[i]));
            }

            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.BLUE);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);

            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            return lineData;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                lineChart.invalidate();

                // Set total sales, returns, and orders in the planned section
                totalSalesTextView.setText("Total Sales: " + totalSales);
                totalReturnsTextView.setText("Total Returns: " + totalReturns);
                totalOrderCountPlannedTextView.setText("Total Orders: " + totalOrders); // Display the total order count
               // totalOrderCountPlannedTextView.setText(totalOrders);
            });
        }

        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start = dateFormat.parse(fromDate);
                Date end = dateFormat.parse(toDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                while (!calendar.getTime().after(end)) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while getting dates between: " + fromDate + " and " + toDate, e);
            }
            return dates;
        }
    }
}
*/


/*

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, salesPercentageTextView;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this); // Initialize ReturnsDB
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText);  // Added this for total returns
       // salesPercentageTextView = findViewById(R.id.salesPercentageText);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate+ " 00:00:00";  // Store the date part only
                        fromDateButton.setText(selectedDate);
                        Log.d(TAG, "From date selected: " + fromDate); // Log the selected from date
                    } else {
                        toDate = selectedDate+" 23:59:59";  // Store the date part only
                        toDateButton.setText(selectedDate);
                        Log.d(TAG, "To date selected: " + toDate); // Log the selected to date
                    }

                    // Trigger data loading after both dates are selected
                    if (fromDate != null && toDate != null) {
                        Log.d(TAG, "Both dates selected. Loading data...");
                        loadTotalGrossAmount(fromDate,toDate);  // Update the data whenever both dates are set
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount(String from,String to) {
        Log.d(TAG, "Loading total gross amount for date range: " + fromDate + " to " + toDate);

        // Ensure to append the time component
         // End time at 23:59:59 for toDate

        new LoadTotalGrossAmountTask().execute(from, to);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private double totalReturns = 0.0;
        private final float[] salesData = new float[31]; // Store sales from day 1 to 31
        private final float[] returnsData = new float[31]; // Store returns from day 1 to 31

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            Log.d(TAG, "Fetching data for dates: " + fromDate + " to " + toDate);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault()); // Extract day only (1–31)

            // Initialize sales and returns data to 0
            for (int i = 0; i < 31; i++) {
                salesData[i] = 0;
                returnsData[i] = 0;
            }

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);
            Log.d(TAG, "Dates to process: " + dates);

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1; // Convert day to index

                    // Get sales data for the day for both "DELIVERY DONE" and "DELIVERED" statuses
                    double totalForSales = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE")
                            + submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERED");
                    salesData[dayIndex] = (float) totalForSales;
                    totalSales += totalForSales;
                    Log.d(TAG, "Sales for " + date + ": " + totalForSales);

                    // Get returns data for the day for both "RETURNED" and "RETURN DONE" statuses
                   /* String fromDateWithTime=date+" 00:00:00.0";*/
/*                    double totalForReturns = returnsDB.getTotalReturnAmountByDate(fromDate,toDate);
                    returnsData[dayIndex] = (float) totalForReturns;
                    totalReturns += totalForReturns;
                    Log.d(TAG, "Returns for " + date + ": " + totalForReturns);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            // Create chart entries for both sales and returns
            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                salesEntries.add(new Entry(i + 1, salesData[i])); // X = Day (1-31), Y = Sales Amount
                returnsEntries.add(new Entry(i + 1, returnsData[i])); // X = Day (1-31), Y = Return Amount
            }

            // Create datasets for both sales and returns
            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.BLUE);
            salesDataSet.setCircleColor(Color.BLUE);
            salesDataSet.setValueTextSize(12f);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);
            returnsDataSet.setCircleColor(Color.RED);
            returnsDataSet.setValueTextSize(12f);

            // Create the final LineData containing both sales and returns
            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            return lineData;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No data found.");
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                setupXAxis();
                setupYAxis();
                lineChart.invalidate();

                // Log total sales and returns before setting the UI components
                Log.d(TAG, "Total Sales: " + totalSales);
                Log.d(TAG, "Total Returns: " + totalReturns);

                totalSalesTextView.setText("Total Sales: " + totalSales);
                totalReturnsTextView.setText("Total Returns: " + totalReturns);  // Make sure this line is being executed
            });
        }

        // Method to get the list of dates between two dates
        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start = dateFormat.parse(fromDate);
                Date end = dateFormat.parse(toDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);
                while (!calendar.getTime().after(end)) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1); // Increment day by one
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while getting dates between: " + fromDate + " and " + toDate, e);
            }
            return dates;
        }

        // Method to setup the X-axis
        private void setupXAxis() {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(31);  // Show 31 labels for the 31 days of the month
        }

        // Method to setup the Y-axis
        private void setupYAxis() {
            YAxis leftYAxis = lineChart.getAxisLeft();
            leftYAxis.setGranularity(10f);  // Set Y-axis granularity
            lineChart.getAxisRight().setEnabled(false);  // Disable right Y-axis
        }
    }
}



/*
package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, salesPercentageTextView;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this); // Initialize ReturnsDB
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText);  // Added this for total returns
        salesPercentageTextView = findViewById(R.id.salesPercentageText);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00.0";
                        fromDateButton.setText(selectedDate);
                        Log.d(TAG, "From date selected: " + fromDate); // Log the selected from date
                    } else {
                        toDate = selectedDate + " 23:59:59.0";
                        toDateButton.setText(selectedDate);
                        Log.d(TAG, "To date selected: " + toDate); // Log the selected to date
                    }
                    if (fromDate != null && toDate != null) {
                        Log.d(TAG, "Both dates selected. Loading data...");
                        loadTotalGrossAmount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount() {
        Log.d(TAG, "Loading total gross amount for date range: " + fromDate + " to " + toDate);
        new LoadTotalGrossAmountTask().execute(fromDate, toDate);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private double totalReturns = 0.0;
        private final float[] salesData = new float[31]; // Store sales from day 1 to 31
        private final float[] returnsData = new float[31]; // Store returns from day 1 to 31

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            Log.d(TAG, "Fetching data for dates: " + fromDate + " to " + toDate);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault()); // Extract day only (1–31)

            // Initialize sales and returns data to 0
            for (int i = 0; i < 31; i++) {
                salesData[i] = 0;
                returnsData[i] = 0;
            }

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);
            Log.d(TAG, "Dates to process: " + dates);

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1; // Convert day to index

                    // Get sales data for the day for both "DELIVERY DONE" and "DELIVERED" statuses
                    double totalForSales = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE")
                            + submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERED");
                    salesData[dayIndex] = (float) totalForSales;
                    totalSales += totalForSales;
                    Log.d(TAG, "Sales for " + date + ": " + totalForSales);

                    // Get returns data for the day for both "RETURNED" and "RETURN DONE" statuses
                    double totalForReturns = returnsDB.getTotalReturnAmountByDate(date);
                    returnsData[dayIndex] = (float) totalForReturns;
                    totalReturns += totalForReturns;
                    Log.d(TAG, "Returns for " + date + ": " + totalForReturns);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            // Create chart entries for both sales and returns
            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                salesEntries.add(new Entry(i + 1, salesData[i])); // X = Day (1-31), Y = Sales Amount
                returnsEntries.add(new Entry(i + 1, returnsData[i])); // X = Day (1-31), Y = Return Amount
            }

            // Create datasets for both sales and returns
            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.BLUE);
            salesDataSet.setCircleColor(Color.BLUE);
            salesDataSet.setValueTextSize(12f);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);
            returnsDataSet.setCircleColor(Color.RED);
            returnsDataSet.setValueTextSize(12f);

            // Create the final LineData containing both sales and returns
            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            return lineData;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "No data found.");
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                setupXAxis();
                setupYAxis();
                lineChart.invalidate();

                totalSalesTextView.setText("Total Sales: " + totalSales);
                totalReturnsTextView.setText("Total Returns: " + totalReturns);  // Updated for total returns

            });
        }

        private void setupXAxis() {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(31); // Show 1 to 31
            xAxis.setAxisMinimum(1);
            xAxis.setAxisMaximum(31);

            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display 1 to 31
                }
            });
        }

        private void setupYAxis() {
            lineChart.getAxisLeft().setAxisMinimum(0f); // Start Y-axis from 0
            lineChart.getAxisRight().setEnabled(false);
        }

        // Helper method to get dates between two given dates
        private ArrayList<String> getDatesBetween(String startDate, String endDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar startCalendar = Calendar.getInstance();
                Calendar endCalendar = Calendar.getInstance();
                startCalendar.setTime(dateFormat.parse(startDate));
                endCalendar.setTime(dateFormat.parse(endDate));

                while (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
                    dates.add(dateFormat.format(startCalendar.getTime()));
                    startCalendar.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing dates", e);
            }
            return dates;
        }
    }
}




/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private ReturnDB returnsDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, totalReturnsTextView, salesPercentageTextView;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        returnsDB = new ReturnDB(this); // Initialize ReturnsDB
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        totalReturnsTextView = findViewById(R.id.totalReturnsText); // Fix: Add this line to reference the TextView
        salesPercentageTextView = findViewById(R.id.salesPercentageText);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00.0";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59.0";
                        toDateButton.setText(selectedDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadTotalGrossAmount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount() {
        new LoadTotalGrossAmountTask().execute(fromDate, toDate);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private double totalReturns = 0.0;
        private final float[] salesData = new float[31]; // Store sales from day 1 to 31
        private final float[] returnsData = new float[31]; // Store returns from day 1 to 31

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault()); // Extract day only (1–31)

            // Initialize sales and returns data to 0
            for (int i = 0; i < 31; i++) {
                salesData[i] = 0;
                returnsData[i] = 0;
            }

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1; // Convert day to index

                    // Get sales data for the day
                    double totalForSales = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE");
                    salesData[dayIndex] = (float) totalForSales;
                    totalSales += totalForSales;

                    // Get returns data for the day
                    double totalForReturns = returnsDB.getTotalReturnAmountByDate(date);
                    returnsData[dayIndex] = (float) totalForReturns;
                    totalReturns += totalForReturns;

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            // Create chart entries for both sales and returns
            ArrayList<Entry> salesEntries = new ArrayList<>();
            ArrayList<Entry> returnsEntries = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                salesEntries.add(new Entry(i + 1, salesData[i])); // X = Day (1-31), Y = Sales Amount
                returnsEntries.add(new Entry(i + 1, returnsData[i])); // X = Day (1-31), Y = Return Amount
            }

            // Create datasets for both sales and returns
            LineDataSet salesDataSet = new LineDataSet(salesEntries, "Sales");
            salesDataSet.setColor(Color.BLUE);
            salesDataSet.setCircleColor(Color.BLUE);
            salesDataSet.setValueTextSize(12f);

            LineDataSet returnsDataSet = new LineDataSet(returnsEntries, "Returns");
            returnsDataSet.setColor(Color.RED);
            returnsDataSet.setCircleColor(Color.RED);
            returnsDataSet.setValueTextSize(12f);

            // Create the final LineData containing both sales and returns
            LineData lineData = new LineData(salesDataSet, returnsDataSet);
            return lineData;
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                setupXAxis();
                setupYAxis();
                lineChart.invalidate();

                totalSalesTextView.setText("Total Sales: " + totalSales);
                totalReturnsTextView.setText("Total Returns: " + totalReturns);  // Added this line for total returns
            });
        }

        private void setupXAxis() {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(31); // Show 1 to 31
            xAxis.setAxisMinimum(1);
            xAxis.setAxisMaximum(31);

            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display 1 to 31
                }
            });
        }

        private void setupYAxis() {
            lineChart.getAxisLeft().setAxisMinimum(0f); // Start Y-axis from 0
            lineChart.getAxisRight().setEnabled(false);
        }

        // Method to get dates between fromDate and toDate
        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = dateFormat.parse(fromDate);
                Date endDate = dateFormat.parse(toDate);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                while (!calendar.getTime().after(endDate)) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error generating dates between " + fromDate + " and " + toDate, e);
            }
            return dates;
        }
    }
}


/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView, salesPercentageTextView;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);
        salesPercentageTextView = findViewById(R.id.salesPercentageText);  // Updated TextView for Sales Percentage

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00.0";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59.0";
                        toDateButton.setText(selectedDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadTotalGrossAmount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount() {
        new LoadTotalGrossAmountTask().execute(fromDate, toDate);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private final float[] salesData = new float[31]; // Store sales from day 1 to 31

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault()); // Extract day only (1–31)

            // Initialize sales data to 0
            for (int i = 0; i < 31; i++) {
                salesData[i] = 0;
            }

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1; // Convert day to index
                    double totalForDate = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE");

                    salesData[dayIndex] = (float) totalForDate;
                    totalSales += totalForDate;
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            // Generate chart entries for all 31 days
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                entries.add(new Entry(i + 1, salesData[i])); // X = Day (1-31), Y = Sales Amount
            }

            LineDataSet dataSet = new LineDataSet(entries, "Daily Sales");
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
            dataSet.setValueTextSize(12f);

            return new LineData(dataSet);
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                setupXAxis();
                setupYAxis();
                lineChart.invalidate();

                totalSalesTextView.setText("Total Sales: " + totalSales);

                // Calculate and display the percentage of sales
                displaySalesPercentage();
            });
        }

        private void setupXAxis() {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(31); // Show 1 to 31
            xAxis.setAxisMinimum(1);
            xAxis.setAxisMaximum(31);

            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display 1 to 31
                }
            });
        }

        private void setupYAxis() {
            lineChart.getAxisLeft().setAxisMinimum(0f); // Start Y-axis from 0
            lineChart.getAxisRight().setEnabled(false);
        }

        private void displaySalesPercentage() {
            double percentage = (totalSales / getMaxTotalSales()) * 100;  // Assuming max sales value is fetched or defined
            salesPercentageTextView.setText("Coverage %: " + String.format(Locale.getDefault(), "%.2f", percentage) + "%");
        }

        private double getMaxTotalSales() {
            // This method should calculate or fetch the max sales value (e.g., max sales of all possible days in a period)
            // For now, it's just a placeholder; you can adjust it as needed.
            return 10000.0;  // Example placeholder value, replace with actual max sales logic.
        }
    }

    private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
        ArrayList<String> dates = new ArrayList<>();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(sdf.parse(fromDate));

            Calendar endDate = Calendar.getInstance();
            endDate.setTime(sdf.parse(toDate));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            while (!startDate.after(endDate)) {
                dates.add(dateFormat.format(startDate.getTime()));
                startDate.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting date range", e);
        }

        return dates;
    }
}


/*
package com.malta_mqf.malta_mobile;


import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity";
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00.0";
                        fromDateButton.setText(selectedDate);
                    } else {
                        toDate = selectedDate + " 23:59:59.0";
                        toDateButton.setText(selectedDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadTotalGrossAmount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount() {
        new LoadTotalGrossAmountTask().execute(fromDate, toDate);
    }

    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, LineData> {
        private double totalSales = 0.0;
        private final float[] salesData = new float[31]; // Store sales from day 1 to 31

        @Override
        protected LineData doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault()); // Extract day only (1–31)

            // Initialize sales data to 0
            for (int i = 0; i < 31; i++) {
                salesData[i] = 0;
            }

            ArrayList<String> dates = getDatesBetween(fromDate, toDate);

            for (String date : dates) {
                try {
                    int dayIndex = Integer.parseInt(dayFormat.format(dateFormat.parse(date))) - 1; // Convert day to index
                    double totalForDate = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE");


                    salesData[dayIndex] = (float) totalForDate;
                    totalSales += totalForDate;
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing date: " + date, e);
                }
            }

            // Generate chart entries for all 31 days
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                entries.add(new Entry(i + 1, salesData[i])); // X = Day (1-31), Y = Sales Amount
            }

            LineDataSet dataSet = new LineDataSet(entries, "Daily Sales");
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
            dataSet.setValueTextSize(12f);

            return new LineData(dataSet);
        }

        @Override
        protected void onPostExecute(LineData lineData) {
            if (lineData.getEntryCount() == 0) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                lineChart.setData(lineData);
                setupXAxis();
                setupYAxis();
                lineChart.invalidate();

                totalSalesTextView.setText("Total Sales: " + totalSales);
            });
        }

        private void setupXAxis() {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(31); // Show 1 to 31
            xAxis.setAxisMinimum(1);
            xAxis.setAxisMaximum(31);

            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display 1 to 31
                }
            });
        }

        private void setupYAxis() {
            lineChart.getAxisLeft().setAxisMinimum(0f); // Start Y-axis from 0
            lineChart.getAxisRight().setEnabled(false);
        }
    }

    private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
        ArrayList<String> dates = new ArrayList<>();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(sdf.parse(fromDate));

            Calendar endDate = Calendar.getInstance();
            endDate.setTime(sdf.parse(toDate));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            while (!startDate.after(endDate)) {
                dates.add(dateFormat.format(startDate.getTime()));
                startDate.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting date range", e);
        }

        return dates;
    }
}





/*
package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity"; // Log tag
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private TextView totalSalesTextView; // To display the total gross amount
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        totalSalesTextView = findViewById(R.id.totalSalesText); // Initialize total sales text

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00.0"; // Set time to 00:00:00 for fromDate
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate + " 23:59:59.0"; // Set time to 23:59:59 for toDate
                        toDateButton.setText(toDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadTotalGrossAmount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadTotalGrossAmount() {
        new LoadTotalGrossAmountTask().execute(fromDate, toDate);
    }
    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, ArrayList<LineDataSet>> {
        private ArrayList<Double> dailySales = new ArrayList<>();
        private double totalSales = 0.0;

        @Override
        protected ArrayList<LineDataSet> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Get the dates between the fromDate and toDate
            ArrayList<String> dates = getDatesBetween(fromDate, toDate);

            // Fetch sales amounts for each date
            for (String date : dates) {
                double totalForDate = submitOrderDB.getTotalGrossAmountByStatusForDate(date, "DELIVERY DONE");
                dailySales.add(totalForDate);
                totalSales += totalForDate;
            }

            // Create an ArrayList to hold the entries for the graph
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 0; i < dailySales.size(); i++) {
                // Add each daily sales value to the chart (date is the x-axis)
                //entries.add(new Entry(i, (float) dailySales.get(i)));
                entries.add(new Entry(i, dailySales.get(i).floatValue())); // Cast Double to float

            }

            // Create LineDataSet for total sales
            LineDataSet dataSet = new LineDataSet(entries, "Daily Sales");
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
            dataSet.setValueTextSize(12f);

            // Prepare the data for the graph
            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            return dataSets;
        }

        @Override
        protected void onPostExecute(ArrayList<LineDataSet> dataSets) {
            if (dataSets.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                // Set the data to the chart
                LineData lineData = new LineData();
                for (LineDataSet dataSet : dataSets) {
                    lineData.addDataSet(dataSet);
                }
                lineChart.setData(lineData);
                lineChart.invalidate(); // Redraw the chart

                // Display total sales in the TextView
                totalSalesTextView.setText("Total Sales: " + totalSales);
            });
        }

        // Helper function to generate the list of dates between two dates
        private ArrayList<String> getDatesBetween(String fromDate, String toDate) {
            ArrayList<String> dates = new ArrayList<>();

            // Parse fromDate and toDate to extract the date parts
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(sdf.parse(fromDate));

                Calendar endDate = Calendar.getInstance();
                endDate.setTime(sdf.parse(toDate));

                while (!startDate.after(endDate)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dates.add(dateFormat.format(startDate.getTime()));
                    startDate.add(Calendar.DATE, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dates;
        }
    }
}

 */
/*
    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, ArrayList<LineDataSet>> {
        private double totalDeliveryDone = 0.0;
        private double totalDelivered = 0.0;

        @Override
        protected ArrayList<LineDataSet> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Fetch total amounts based on status
            totalDeliveryDone = submitOrderDB.getTotalGrossAmountByStatus(fromDate, toDate, "DELIVERY DONE");
            totalDelivered = submitOrderDB.getTotalGrossAmountByStatus(fromDate, toDate, "DELIVERED");

            Log.d(TAG, "Total DELIVERY DONE: " + totalDeliveryDone);
            Log.d(TAG, "Total DELIVERED: " + totalDelivered);

            // Combine both totals into one
            double totalSales = totalDeliveryDone + totalDelivered;

            ArrayList<Entry> totalSalesEntries = new ArrayList<>();
            totalSalesEntries.add(new Entry(0, (float) totalSales)); // Only one point representing the total sales

            // Create LineDataSet for combined total sales
            LineDataSet totalSalesDataSet = new LineDataSet(totalSalesEntries, "Total Sales");
            totalSalesDataSet.setColor(Color.BLUE);
            totalSalesDataSet.setCircleColor(Color.BLUE);
            totalSalesDataSet.setValueTextSize(12f);

            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(totalSalesDataSet);

            return dataSets;
        }

        @Override
        protected void onPostExecute(ArrayList<LineDataSet> dataSets) {
            if (dataSets.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                LineData lineData = new LineData();
                for (LineDataSet dataSet : dataSets) {
                    lineData.addDataSet(dataSet);
                }
                lineChart.setData(lineData);
                lineChart.invalidate(); // Redraw the chart

                // Display total sales amount in the text view
                double totalSales = totalDeliveryDone + totalDelivered;
                totalSalesTextView.setText("Sales: " + totalSales);
            });
        }
    }
}
    /*
    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, ArrayList<LineDataSet>> {
        private double totalDeliveryDone = 0.0;
        private double totalDelivered = 0.0;

        @Override
        protected ArrayList<LineDataSet> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Fetch total amounts based on status
            totalDeliveryDone = submitOrderDB.getTotalGrossAmountByStatus(fromDate, toDate, "DELIVERY DONE");
            totalDelivered = submitOrderDB.getTotalGrossAmountByStatus(fromDate, toDate, "DELIVERED");

            Log.d(TAG, "Total DELIVERY DONE: " + totalDeliveryDone);
            Log.d(TAG, "Total DELIVERED: " + totalDelivered);

            ArrayList<Entry> deliveryDoneEntries = new ArrayList<>();
            ArrayList<Entry> deliveredEntries = new ArrayList<>();

            // Add data points for both statuses
            deliveryDoneEntries.add(new Entry(0, (float) totalDeliveryDone));
            deliveredEntries.add(new Entry(1, (float) totalDelivered)); // Note index 1 to display side by side on the graph

            // Create LineDataSet for DELIVERY DONE
            LineDataSet deliveryDoneDataSet = new LineDataSet(deliveryDoneEntries, "DELIVERY DONE");
            deliveryDoneDataSet.setColor(Color.GREEN);
            deliveryDoneDataSet.setCircleColor(Color.GREEN);
            deliveryDoneDataSet.setValueTextSize(12f);

            // Create LineDataSet for DELIVERED
            LineDataSet deliveredDataSet = new LineDataSet(deliveredEntries, "DELIVERED");
            deliveredDataSet.setColor(Color.RED);
            deliveredDataSet.setCircleColor(Color.RED);
            deliveredDataSet.setValueTextSize(12f);

            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(deliveryDoneDataSet);
            dataSets.add(deliveredDataSet);

            return dataSets;
        }

        @Override
        protected void onPostExecute(ArrayList<LineDataSet> dataSets) {
            if (dataSets.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                LineData lineData = new LineData();
                for (LineDataSet dataSet : dataSets) {
                    lineData.addDataSet(dataSet);
                }
                lineChart.setData(lineData);
                lineChart.invalidate(); // Redraw the chart

                // Display total sales count below the graph
                //totalSalesTextView.setText("Total DELIVERY DONE: " + totalDeliveryDone + "\nTotal DELIVERED: " + totalDelivered);
                // Update the total sales text view to show all values
                double totalSales = totalDeliveryDone + totalDelivered;
                totalSalesTextView.setText("Sales: " + totalSales);
            });
        }
    }

}

    /*private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, ArrayList<LineDataSet>> {
        private double totalDeliveryDone = 0.0;
        private double totalDelivered = 0.0;

        @Override
        protected ArrayList<LineDataSet> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Fetch total amounts based on status
            totalDeliveryDone = submitOrderDB.getTotalGrossAmountByStatus(fromDate, toDate, "DELIVERY DONE");
            totalDelivered = submitOrderDB.getTotalGrossAmountByStatus(fromDate, toDate, "DELIVERED");

            Log.d(TAG, "Total DELIVERY DONE: " + totalDeliveryDone);
            Log.d(TAG, "Total DELIVERED: " + totalDelivered);

            ArrayList<Entry> deliveryDoneEntries = new ArrayList<>();
            ArrayList<Entry> pendingDeliveryEntries = new ArrayList<>();

            deliveryDoneEntries.add(new Entry(0, (float) totalDeliveryDone));
            pendingDeliveryEntries.add(new Entry(0, (float) totalDelivered));

            LineDataSet deliveryDoneDataSet = new LineDataSet(deliveryDoneEntries, "DELIVERY DONE");
            deliveryDoneDataSet.setColor(Color.GREEN);
            deliveryDoneDataSet.setCircleColor(Color.GREEN);
            deliveryDoneDataSet.setValueTextSize(12f);

            LineDataSet pendingDeliveryDataSet = new LineDataSet(pendingDeliveryEntries, "DELIVERED");
            pendingDeliveryDataSet.setColor(Color.RED);
            pendingDeliveryDataSet.setCircleColor(Color.RED);
            pendingDeliveryDataSet.setValueTextSize(12f);

            ArrayList<LineDataSet> dataSets = new ArrayList<>();
            dataSets.add(deliveryDoneDataSet);
            dataSets.add(pendingDeliveryDataSet);

            return dataSets;
        }

        @Override
        protected void onPostExecute(ArrayList<LineDataSet> dataSets) {
            if (dataSets.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            runOnUiThread(() -> {
                LineData lineData = new LineData();
                for (LineDataSet dataSet : dataSets) {
                    lineData.addDataSet(dataSet);
                }
                lineChart.setData(lineData);
                lineChart.invalidate();
            });
        }
    }
}

/*
    private class LoadTotalGrossAmountTask extends AsyncTask<String, Void, ArrayList<Entry>> {

        private double totalGrossAmount = 0.0; // Store total amount

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Log the selected date range
            Log.d(TAG, "Date Range: From " + fromDate + " To " + toDate);

            // Get the total gross amount from the database
            totalGrossAmount = submitOrderDB.getTotalGrossAmountByDateRange(fromDate, toDate);

            // Log the retrieved total amount
            Log.d(TAG, "Total Gross Amount: " + totalGrossAmount);

            // Prepare data for the line chart
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, (float) totalGrossAmount)); // Add the total amount as a data point

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            // Ensure this runs on the main UI thread
            if (entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No data found for the selected range.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create LineDataSet with total gross amount
            LineDataSet lineDataSet = new LineDataSet(entries, "Total Gross Amount");

            // Custom ValueFormatter to show amount in proper format
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format("%.2f", value); // Display amount with 2 decimal places
                }
            });

            // Customize the line data set appearance
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple));
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent)); // Text color for amount labels
            lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
            lineDataSet.setDrawCircles(true); // Draw circles at data points
            lineDataSet.setValueTextSize(12f); // Size of the amount text

            // Create LineData and set it to the chart on the main thread
            runOnUiThread(() -> {
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // Redraw the chart to apply changes
            });

            // Update total sales below the graph
            runOnUiThread(() -> {
                totalSalesTextView.setText("Total Sales: " + String.format("%.2f", totalGrossAmount));
            });
        }
    }
}





/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.util.ArrayList;
import java.util.Calendar;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity"; // Log tag
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00.0"; // Set time to 00:00:00 for fromDate
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate + " 23:59:59.0"; // Set time to 23:59:59 for toDate
                        toDateButton.setText(toDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadOrderCount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadOrderCount() {
        new LoadOrderCountTask().execute(fromDate, toDate);
    }

    private class LoadOrderCountTask extends AsyncTask<String, Void, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Log the selected date range
            Log.d(TAG, "Date Range: From " + fromDate + " To " + toDate);

            // Get the order count from the database based on orderID
            Cursor cursor = submitOrderDB.getOrderCountByDateRange(fromDate, toDate); // Assuming delivery date is same as toDate

            int orderCount = 0;
            if (cursor != null && cursor.moveToFirst()) {
                orderCount = cursor.getInt(0); // Get count from the first column (order count)
                cursor.close();
            }

            // Log the retrieved order count
            Log.d(TAG, "Order count: " + orderCount);

            // Prepare data for the line chart
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, orderCount)); // Add the order count as a data point

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            // Ensure this runs on the main UI thread
            if (entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No orders found for the selected range.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create LineDataSet with order counts
            LineDataSet lineDataSet = new LineDataSet(entries, "Order Count");

            // Custom ValueFormatter to show order count as integer
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display the sales count as an integer
                }
            });

            // Customize the line data set appearance
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple));
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent)); // Text color for the count labels
            lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
            lineDataSet.setDrawCircles(true); // Draw circles at data points
            lineDataSet.setValueTextSize(12f); // Size of the count text

            // Create LineData and set it to the chart on the main thread
            runOnUiThread(() -> {
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // Redraw the chart to apply changes
            });

            // Update total sales below the graph
            runOnUiThread(() -> {
                int totalSales = 0;
                for (Entry entry : entries) {
                    totalSales += (int) entry.getY(); // Sum up the sales values (order counts in this case)
                }

                // Assuming you have a TextView to display the total sales below the chart
                TextView totalSalesTextView = findViewById(R.id.totalSalesText);
                totalSalesTextView.setText("Sales: " + totalSales);
            });
        }
    }
}



/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity"; // Log tag
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    if (isFromDate) {
                        fromDate = selectedDate + " 00:00:00.0"; // Set time to 00:00:00 for fromDate
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate + " 23:59:59.0"; // Set time to 23:59:59 for toDate
                        toDateButton.setText(toDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadOrderCount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadOrderCount() {
        new LoadOrderCountTask().execute(fromDate, toDate);
    }

    private class LoadOrderCountTask extends AsyncTask<String, Void, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Log the selected date range
            Log.d(TAG, "Selected Date Range: From " + fromDate + " To " + toDate);

            // Set default value for del_dateTime (assumed to be same as toDate)
            String delDateTime = toDate;

            // Get the order count from the database
            Cursor cursor = submitOrderDB.getOrderCountByDateRange(fromDate, toDate, delDateTime);

            int orderCount = 0;
            if (cursor != null && cursor.moveToFirst()) {
                orderCount = cursor.getInt(0); // Get count from the first column
                cursor.close();
            }

            // Log the retrieved order count
            Log.d(TAG, "Order count for the selected range: " + orderCount);

            // Prepare data for the line chart
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, orderCount)); // Add the order count as a data point

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            // Ensure this runs on the main UI thread
            if (entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No orders found for the selected range.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create LineDataSet with order counts
            LineDataSet lineDataSet = new LineDataSet(entries, "ordercount");

            // Custom ValueFormatter to show order count as integer
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display the sales count as an integer
                }
            });

            // Customize the line data set appearance
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple));
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent)); // Text color for the count labels
            lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
            lineDataSet.setDrawCircles(true); // Draw circles at data points
            lineDataSet.setValueTextSize(12f); // Size of the count text

            // Create LineData and set it to the chart on the main thread
            runOnUiThread(() -> {
                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // Redraw the chart to apply changes
            });

            // Update total sales below the graph
            runOnUiThread(() -> {
                int totalSales = 0;
                for (Entry entry : entries) {
                    totalSales += (int) entry.getY(); // Sum up the sales values (order counts in this case)
                }

                // Assuming you have a TextView to display the total sales below the chart
                TextView totalSalesTextView = findViewById(R.id.totalSalesText);
                totalSalesTextView.setText("Sales: " + totalSales);
            });
        }
    }
}


/*
public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity"; // Log tag
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private String fromDate, toDate, expectedDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));

        // Initially, expectedDelivery will be set to toDate, if toDate is not null
        expectedDelivery = toDate; // Use toDate as the default expectedDelivery
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    if (isFromDate) {
                        fromDate = selectedDate;
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate;
                        toDateButton.setText(toDate);

                        // Update expectedDelivery to the selected toDate when the user picks a "to" date
                        expectedDelivery = toDate;
                    }
                    if (fromDate != null && toDate != null) {
                        loadOrderCount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadOrderCount() {
        // Pass all three parameters: fromDate, toDate, and expectedDelivery
        new LoadOrderCountTask().execute(fromDate, toDate, expectedDelivery);
    }

    private class LoadOrderCountTask extends AsyncTask<String, Void, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];
            String expectedDelivery = params[2];  // Get the third parameter

            // Log the selected date range
            Log.d(TAG, "Selected Date Range: From " + fromDate + " To " + toDate + " Expected Delivery: " + expectedDelivery);

            // Get the order count from the database
            Cursor cursor = submitOrderDB.getOrderCountByDateRange(fromDate, toDate, expectedDelivery);

            // Check if the cursor is valid
            if (cursor != null && cursor.moveToFirst()) {
                // Get the count from the first column (the result of COUNT(*))
                int orderCount = cursor.getInt(0);
                cursor.close();

                // Log the retrieved order count
                Log.d(TAG, "Order count for the selected range: " + orderCount);

                // Prepare data for the line chart
                ArrayList<Entry> entries = new ArrayList<>();
                entries.add(new Entry(0, orderCount)); // Add the order count as a data point

                return entries;
            } else {
                // Return an empty list if no data is found
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            // Ensure this runs on the main UI thread
            if (entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No orders found for the selected range.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create LineDataSet with order counts
            LineDataSet lineDataSet = new LineDataSet(entries, "Order Count");

            // Custom ValueFormatter to show order count as integer
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display the order count as an integer
                }
            });

            // Customize the line data set appearance
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple));
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent)); // Text color for the count labels
            lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
            lineDataSet.setDrawCircles(true); // Draw circles at data points
            lineDataSet.setValueTextSize(12f); // Size of the count text

            // Create LineData and set it to the chart on the main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LineData lineData = new LineData(lineDataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate(); // Redraw the chart to apply changes
                }
            });

            // Update total sales below the graph
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int totalSales = 0;
                    for (Entry entry : entries) {
                        totalSales += (int) entry.getY(); // Sum up the sales values (order counts in this case)
                    }

                    // Assuming you have a TextView to display the total sales below the chart
                    TextView totalSalesTextView = findViewById(R.id.totalSalesText);
                    totalSalesTextView.setText("Sales: " + totalSales);
                }
            });
        }
    }
}


/*
public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity"; // Log tag
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    if (isFromDate) {
                        fromDate = selectedDate;
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate;
                        toDateButton.setText(toDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadOrderCount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadOrderCount() {
        new LoadOrderCountTask().execute(fromDate, toDate);
    }

    private class LoadOrderCountTask extends AsyncTask<String, Void, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Log the selected date range
            Log.d(TAG, "Selected Date Range: From " + fromDate + " To " + toDate);

            // Get the order count from the database
            int orderCount = submitOrderDB.getOrderCountByDateRange(fromDate, toDate);

            // Log the retrieved order count
            Log.d(TAG, "Order count for the selected range: " + orderCount);

            // Prepare data for the line chart
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, orderCount)); // Add the order count as a data point

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            // Ensure this runs on the main UI thread
            if (entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No orders found for the selected range.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create LineDataSet with order counts
            LineDataSet lineDataSet = new LineDataSet(entries, "Sales Over Time");

            // Custom ValueFormatter to show order count as integer
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Display the sales count as an integer
                }
            });

            // Customize the line data set appearance
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple));
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent)); // Text color for the count labels
            lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
            lineDataSet.setDrawCircles(true); // Draw circles at data points
            lineDataSet.setValueTextSize(12f); // Size of the count text

            // Create LineData and set it to the chart on the main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LineData lineData = new LineData(lineDataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate(); // Redraw the chart to apply changes
                }
            });

            // Update total sales below the graph
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int totalSales = 0;
                    for (Entry entry : entries) {
                        totalSales += (int) entry.getY(); // Sum up the sales values (order counts in this case)
                    }

                    // Assuming you have a TextView to display the total sales below the chart
                    TextView totalSalesTextView = findViewById(R.id.totalSalesText);
                    totalSalesTextView.setText("Total Sales: " + totalSales);
                }
            });
        }
    }
}




/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.util.ArrayList;
import java.util.Calendar;

public class AnalysisGraphActivity extends AppCompatActivity {

    private static final String TAG = "AnalysisGraphActivity"; // Log tag
    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    if (isFromDate) {
                        fromDate = selectedDate;
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate;
                        toDateButton.setText(toDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadOrderCount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadOrderCount() {
        new LoadOrderCountTask().execute(fromDate, toDate);
    }

    private class LoadOrderCountTask extends AsyncTask<String, Void, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            // Log the selected date range
            Log.d(TAG, "Selected Date Range: From " + fromDate + " To " + toDate);

            // Get the order count from the database
            int orderCount = submitOrderDB.getOrderCountByDateRange(fromDate, toDate);

            // Log the retrieved order count
            Log.d(TAG, "Order count for the selected range: " + orderCount);

            // Prepare data for the line chart
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, orderCount)); // Add the order count as a data point

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            if (entries == null || entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No data available for the selected range", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create dataset and set styling
            LineDataSet lineDataSet = new LineDataSet(entries, "Orders Count");
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple));
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));

            // Create LineData and set it to the chart
            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);
            lineChart.invalidate(); // Refresh the chart

            // Log that the graph was updated
            Log.d(TAG, "Graph updated with order count data");
        }
    }
}





/*package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.util.ArrayList;
import java.util.Calendar;

public class AnalysisGraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private String fromDate, toDate;
    private int fixedOrderID = 1001; // Fixed ORDERID for the entire range

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    if (isFromDate) {
                        fromDate = selectedDate;
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate;
                        toDateButton.setText(toDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadOrderCount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadOrderCount() {
        new LoadOrderCountTask().execute(fromDate, toDate);
    }

    private class LoadOrderCountTask extends AsyncTask<String, Void, ArrayList<Entry>> {
        private int orderCount = 0;

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            ArrayList<Entry> entries = new ArrayList<>();
            Cursor cursor = null;
            try {
                // Get all orders data from the database between the selected date range
                cursor = submitOrderDB.getOrdersByDateRange(fromDate, toDate);
                if (cursor != null && cursor.moveToFirst()) {
                    int index = 0;

                    // Ensure column indices are valid before accessing them
                    int orderIDColumnIndex = cursor.getColumnIndex("ORDERID");
                    if (orderIDColumnIndex == -1) {
                        Log.e("LoadOrderCountTask", "ORDERID column not found!");
                        return entries;  // Return empty list if the column is not found
                    }

                    do {
                        // Check if the ORDERID matches the fixed value
                        if (cursor.getInt(orderIDColumnIndex) == fixedOrderID) {
                            orderCount++;  // Count each order
                            Log.d("OrderCount", "Order found, current count: " + orderCount);
                            // Plot the order count for each date in the range
                            entries.add(new Entry(index++, orderCount)); // Example: X is index, Y is the order count
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            // Display the total order count in a Toast
            Toast.makeText(AnalysisGraphActivity.this,
                    "Total Orders: " + orderCount,
                    Toast.LENGTH_LONG).show();

            // Check if entries are available for the graph
            if (entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No orders found for the selected range.", Toast.LENGTH_SHORT).show();
            }

            // Create a LineDataSet for the graph
            LineDataSet lineDataSet = new LineDataSet(entries, "Orders Over Time");
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple)); // Set line color
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent)); // Set value text color

            // Set up the graph data
            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);
            lineChart.invalidate();  // Refresh the chart to show the updated data

            // Debug log to ensure we are updating the chart
            Log.d("GraphUpdate", "Graph updated with order count data");
        }
    }
}



/*
package com.malta_mqf.malta_mobile;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;

import java.util.ArrayList;
import java.util.Calendar;

public class AnalysisGraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private String fromDate, toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        lineChart = findViewById(R.id.lineChart);
        submitOrderDB = new SubmitOrderDB(this);
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);

        fromDateButton.setOnClickListener(view -> showDatePickerDialog(true));
        toDateButton.setOnClickListener(view -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    if (isFromDate) {
                        fromDate = selectedDate;
                        fromDateButton.setText(fromDate);
                    } else {
                        toDate = selectedDate;
                        toDateButton.setText(toDate);
                    }
                    if (fromDate != null && toDate != null) {
                        loadOrderCount();
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadOrderCount() {
        new LoadOrderCountTask().execute(fromDate, toDate);
    }

    private class LoadOrderCountTask extends AsyncTask<String, Void, ArrayList<Entry>> {
        private int orderCount = 0;

        @Override
        protected ArrayList<Entry> doInBackground(String... params) {
            String fromDate = params[0];
            String toDate = params[1];

            ArrayList<Entry> entries = new ArrayList<>();
            Cursor cursor = null;
            try {
                // Get all orders data from the database between the selected date range
                cursor = submitOrderDB.getOrdersByDateRange(fromDate, toDate);
                if (cursor != null && cursor.moveToFirst()) {
                    int index = 0;
                    do {
                        orderCount++;  // Count each order
                        Log.d("OrderCount", "Order found, current count: " + orderCount);
                        // Example: You can plot any other data on the graph such as order amount
                        // For this example, I'm plotting an entry using the index (just for illustration)
                        entries.add(new Entry(index++, orderCount));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            return entries;
        }

        @Override
        protected void onPostExecute(ArrayList<Entry> entries) {
            // Display the total order count in a Toast
            Toast.makeText(AnalysisGraphActivity.this,
                    "Total Orders: " + orderCount,
                    Toast.LENGTH_LONG).show();

            // Check if entries are available for the graph
            if (entries.isEmpty()) {
                Toast.makeText(AnalysisGraphActivity.this, "No orders found for the selected range.", Toast.LENGTH_SHORT).show();
            }

            // Create a LineDataSet for the graph
            LineDataSet lineDataSet = new LineDataSet(entries, "Orders Over Time");
            lineDataSet.setColor(getResources().getColor(R.color.appColorpurple));
            lineDataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));

            // Set up the graph data
            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);
            lineChart.invalidate();  // Refresh the chart to show the updated data

            // Debug log to ensure we are updating the chart
            Log.d("GraphUpdate", "Graph updated with order count data");
        }
    }
}





/*
package com.malta_mqf.malta_mobile;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AnalysisGraphActivity extends AppCompatActivity {

    private SubmitOrderDB submitOrderDB;
    private Button fromDateButton, toDateButton;
    private Calendar calendar;
    private int year, month, day;

    // Variables to store selected dates
    private String fromDate, toDate;
    private LineChart lineChart; // LineChart for displaying the graph

    // TextViews to display the count and percentage
    private TextView totalSalesText, totalReturnsText, salesPercentageText, returnPercentageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_graph);

        // Initialize database helper
        submitOrderDB = new SubmitOrderDB(this);

        // Initialize calendar
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Initialize buttons for selecting From and To dates
        fromDateButton = findViewById(R.id.fromDateButton);
        toDateButton = findViewById(R.id.toDateButton);
        lineChart = findViewById(R.id.lineChart);

        // Initialize TextViews for displaying totals and percentages
        totalSalesText = findViewById(R.id.totalSalesText);
        totalReturnsText = findViewById(R.id.totalReturnsText);
        salesPercentageText = findViewById(R.id.salesPercentageText);
        returnPercentageText = findViewById(R.id.returnPercentageText);

        // Set up the 'From' date picker dialog
        fromDateButton.setOnClickListener(v -> {
            DatePickerDialog fromDatePicker = new DatePickerDialog(
                    AnalysisGraphActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Format and display the selected 'From' date
                        fromDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        fromDateButton.setText(fromDate);
                    },
                    year, month, day);
            fromDatePicker.show();
        });

        // Set up the 'To' date picker dialog
        toDateButton.setOnClickListener(v -> {
            DatePickerDialog toDatePicker = new DatePickerDialog(
                    AnalysisGraphActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Format and display the selected 'To' date
                        toDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        toDateButton.setText(toDate);
                    },
                    year, month, day);
            toDatePicker.show();
        });

        // Show graph and update totals and percentages after selecting dates
        Button showGraphButton = findViewById(R.id.showGraphButton);
        showGraphButton.setOnClickListener(v -> {
            if (fromDate != null && toDate != null) {
                // Fetch data from the database based on the selected date range
                List<Entry> salesData = fetchSalesData(fromDate, toDate);
                List<Entry> returnData = fetchReturnData(fromDate, toDate);

                // Create datasets for sales and returns
                LineDataSet salesDataSet = new LineDataSet(salesData, "Sales");
                salesDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
                salesDataSet.setValueTextColor(ColorTemplate.COLORFUL_COLORS[0]);

                LineDataSet returnDataSet = new LineDataSet(returnData, "Returns");
                returnDataSet.setColor(ColorTemplate.COLORFUL_COLORS[1]);
                returnDataSet.setValueTextColor(ColorTemplate.COLORFUL_COLORS[1]);

                // Create the line data and set it to the chart
                LineData lineData = new LineData(salesDataSet, returnDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // Refresh the chart

                // Calculate and display sales and return totals and percentages
                float totalSales = calculateTotalSales(salesData);
                float totalReturns = calculateTotalReturns(returnData);

                totalSalesText.setText("Sales: " + totalSales);
                totalReturnsText.setText("Returns: " + totalReturns);

                // Calculate and display sales and returns percentages
                float salesPercentage = (totalSales / (totalSales + totalReturns)) * 100;
                float returnPercentage = (totalReturns / (totalSales + totalReturns)) * 100;

                salesPercentageText.setText("Coverage %: " + String.format("%.2f", salesPercentage) + "%");
                returnPercentageText.setText("Returns %: " + String.format("%.2f", returnPercentage) + "%");

                Toast.makeText(this, "Graph updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please select both From and To dates.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch sales data from the database for the selected date range
    private List<Entry> fetchSalesData(String fromDate, String toDate) {
        List<Entry> salesData = new ArrayList<>();

        // Query the database for sales data based on the date range
        salesData.add(new Entry(1, 500)); // Date 1, Sales 500
        salesData.add(new Entry(2, 300)); // Date 2, Sales 300
        salesData.add(new Entry(3, 800)); // Date 3, Sales 800

        return salesData;
    }

    // Fetch return data from the database for the selected date range
    private List<Entry> fetchReturnData(String fromDate, String toDate) {
        List<Entry> returnData = new ArrayList<>();

        // Query the database for return data based on the date range
        returnData.add(new Entry(1, 100)); // Date 1, Returns 100
        returnData.add(new Entry(2, 150)); // Date 2, Returns 150
        returnData.add(new Entry(3, 200)); // Date 3, Returns 200

        return returnData;
    }

    // Calculate total sales
    private float calculateTotalSales(List<Entry> salesData) {
        float total = 0;
        for (Entry entry : salesData) {
            total += entry.getY();
        }
        return total;
    }

    // Calculate total returns
    private float calculateTotalReturns(List<Entry> returnData) {
        float total = 0;
        for (Entry entry : returnData) {
            total += entry.getY();
        }
        return total;
    }
}

 */
