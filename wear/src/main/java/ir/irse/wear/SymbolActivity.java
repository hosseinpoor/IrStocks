package ir.irse.wear;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import ir.irse.wear.other.PersianDigitConverter;
import ir.irse.wear.other.TinyDB;

public class SymbolActivity extends WearableActivity {

    TextView name , sym , remove , data , chartdur;
    ArrayList<String> stockSyms = new ArrayList<String>();
    ArrayList<String> stockSymsData = new ArrayList<String>();
    TinyDB tinydb;
    LineChart chart;
    BarChart chart2;
    String symbol;
    LinearLayout chartslay;
    int active = 1;
    String[] chartDuration = {"۱روز","۱هفته","۱ماه","۳ماه","۶ماه","۱سال","۲سال"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        name = findViewById(R.id.name);
        sym = findViewById(R.id.symbol);
        remove = findViewById(R.id.removebtn);
        data = findViewById(R.id.datatxt);
        chartdur = findViewById(R.id.chartduration);
        tinydb = new TinyDB(SymbolActivity.this);
        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");
        Bundle b = getIntent().getExtras();
        final int index = b.getInt("position");
        symbol = stockSyms.get(index);
        sym.setText(PersianDigitConverter.PerisanNumber(symbol));
        getSymData(symbol);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockSyms.remove(index);
                stockSymsData.remove(index);
                tinydb.putListString("SymsList" , stockSyms);
                tinydb.putListString("SymsDataList" , stockSymsData);
                Toast.makeText(SymbolActivity.this , PersianDigitConverter.PerisanNumber(symbol) + " حذف شد" , Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        chartPrepare();
        setChart();
        chartslay = findViewById(R.id.chartslay);
        chartslay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active = (active%7) +1;
                chartdur.setText(chartDuration[active-1]);
                setChart();
            }
        });

    }

    public void getSymData(final String symbol) {

        String url = "http://api.nemov.org/api/v1/Market/Symbol";
        stockSyms = tinydb.getListString("SymsList");
        int index = stockSyms.indexOf(symbol);
        try {
            url = url + "/" + URLEncoder.encode(symbol, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            JSONObject j = new JSONObject(stockSymsData.get(index));
            data.setText("");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat();
            df.setDecimalFormatSymbols(symbols);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(0);
            try {
                name.setText( PersianDigitConverter.PerisanNumber(j.getString("LVal30")) );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if(!j.getString("QTotCap").isEmpty() && !j.getString("QTotCap").equals("null"))
                    data.setText(data.getText().toString() + "ارزش بازار : " + PersianDigitConverter.PerisanNumber(df.format(j.getLong("QTotCap"))) ) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if(!j.getString("PriceFirst").isEmpty() && !j.getString("PriceFirst").equals("null")) {
                    data.setText(data.getText().toString() + " | باز : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("PriceFirst"))));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if(!j.getString("PriceMax").isEmpty() && !j.getString("PriceMax").equals("null"))
                    data.setText(data.getText().toString() + " | حد بالا : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("PriceMax"))) ) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if(!j.getString("PriceMin").isEmpty() && !j.getString("PriceMin").equals("null"))
                    data.setText(data.getText().toString() + " | حد پایین : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("PriceMin"))) ) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if(!j.getString("EPS").isEmpty() && !j.getString("EPS").equals("null"))
                    data.setText(data.getText().toString() + " | EPS : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("EPS"))) ) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        stockSyms = tinydb.getListString("SymsList");
                        stockSymsData = tinydb.getListString("SymsDataList");
                        int index = stockSyms.indexOf(symbol);
                        if(index>0) {
                            stockSymsData.set(index, s);
                            tinydb.putListString("SymsDataList", stockSymsData);

                            JSONObject j = null;
                            try {
                                j = new JSONObject(stockSymsData.get(index));
                                data.setText("");
                                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                                symbols.setGroupingSeparator(',');
                                DecimalFormat df = new DecimalFormat();
                                df.setDecimalFormatSymbols(symbols);
                                df.setGroupingSize(3);
                                df.setMaximumFractionDigits(0);
                                try {
                                    name.setText(PersianDigitConverter.PerisanNumber(j.getString("LVal30")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (!j.getString("QTotCap").isEmpty() && !j.getString("QTotCap").equals("null"))
                                        data.setText(data.getText().toString() + "ارزش بازار : " + PersianDigitConverter.PerisanNumber(df.format(j.getLong("QTotCap"))));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (!j.getString("PriceFirst").isEmpty() && !j.getString("PriceFirst").equals("null")) {
                                        data.setText(data.getText().toString() + " | باز : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("PriceFirst"))));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (!j.getString("PriceMax").isEmpty() && !j.getString("PriceMax").equals("null"))
                                        data.setText(data.getText().toString() + " | حد بالا : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("PriceMax"))));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (!j.getString("PriceMin").isEmpty() && !j.getString("PriceMin").equals("null"))
                                        data.setText(data.getText().toString() + " | حد پایین : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("PriceMin"))));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (!j.getString("EPS").isEmpty() && !j.getString("EPS").equals("null"))
                                        data.setText(data.getText().toString() + " | EPS : " + PersianDigitConverter.PerisanNumber(df.format(j.getInt("EPS"))));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(FullList.this, "خطا", Toast.LENGTH_SHORT).show();
            }
        });


        myReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });


        queue.add(myReq);

    }

    public void chartPrepare(){
        chart = (LineChart) findViewById(R.id.chart);
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.zoom(0,0,0,0);
        chart.setScaleEnabled(false);
        chart.setClickable(false);
        chart.setPinchZoom(false);
        chart.setTouchEnabled(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setTextColor(Color.GRAY);
        chart.getAxisRight().setTextColor(Color.GRAY);
        chart.getAxisLeft().setTextColor(Color.GRAY);
//        chart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        chart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);    // Hide the description
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setDrawLabels(false);
        chart.setNoDataText("");
        chart2 = (BarChart) findViewById(R.id.chart2);
        chart2.setViewPortOffsets(0, 0, 0, 0);
        chart2.zoom(0,0,0,0);
        chart2.setScaleEnabled(false);
        chart2.setClickable(false);
        chart2.setPinchZoom(false);
        chart2.setTouchEnabled(false);
        chart2.getAxisRight().setDrawGridLines(false);
        chart2.getAxisLeft().setDrawGridLines(false);
        chart2.getXAxis().setDrawGridLines(false);
        chart2.getXAxis().setDrawAxisLine(false);
        chart2.getXAxis().setTextColor(Color.GRAY);
        chart2.getAxisRight().setTextColor(Color.GRAY);
        chart2.getAxisLeft().setTextColor(Color.GRAY);
        chart2.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        chart2.getLegend().setEnabled(false);
        chart2.setDescription(description);    // Hide the description
        chart2.getAxisLeft().setDrawLabels(false);
        chart2.getAxisRight().setDrawLabels(false);
        chart2.getXAxis().setDrawLabels(false);
        chart2.setNoDataText("");
    }

    public void setChart(){

        XAxis leftAxis;
        switch (active) {
            case 1:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(25, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("D");

                break;
            case 2:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(8, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("W");

                break;
            case 3:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(5,true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("M");

                break;
            case 4:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(4, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("3M");

                break;
            case 5:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(7, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("6M");

                break;
            case 6:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(13, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("Y");

                break;
            case 7:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(25, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("2Y");

                break;
            default:
                break;
        }
    }

    public void getChart(String time){
        TinyDB tinyDB = new TinyDB(SymbolActivity.this);
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        ArrayList<String> data = tinyDB.getListString("SymsDataList");
        int index = syms.indexOf(PersianDigitConverter.EnglishNumber(symbol));
        try {
            JSONObject jsonObject = new JSONObject(data.get(index));
            String id = jsonObject.getString("InsCode");
            getData(id , time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String id , String time){

        ArrayList<String> xAX = new ArrayList<>() , cAX =  new ArrayList<>() , vAX =  new ArrayList<>();
        String s = getSavedChart();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArrayT = jsonObject.getJSONArray("t");
            for(int i = 0 ; i<jsonArrayT.length() ; i++){
                xAX.add(jsonArrayT.getString(i));
            }
            JSONArray jsonArrayC = jsonObject.getJSONArray("c");
            for(int i = 0 ; i<jsonArrayC.length() ; i++){
                cAX.add(jsonArrayC.getString(i));
            }
            JSONArray jsonArrayV = jsonObject.getJSONArray("v");
            for(int i = 0 ; i<jsonArrayV.length() ; i++){
                vAX.add(jsonArrayV.getString(i));
            }
            setLineChartData(xAX,cAX);
            setBarChartData(xAX,vAX);
        }
        catch (Exception e){
//                            Toast.makeText(getActivity() ,e.getMessage(),Toast.LENGTH_LONG ).show();
        }

        String market_url = "http://api.nemov.org/api/v1/Market/Chart/" + id + "/" + time;

        RequestQueue queue  = Volley.newRequestQueue(SymbolActivity.this);
        StringRequest myReq = new StringRequest(Request.Method.GET, market_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        ArrayList<String> xAX = new ArrayList<>() , cAX =  new ArrayList<>() , vAX =  new ArrayList<>();
                        try {

                            JSONObject jsonObject = new JSONObject(s);
                            saveChart(s);
                            JSONArray jsonArrayT = jsonObject.getJSONArray("t");
                            for(int i = 0 ; i<jsonArrayT.length() ; i++){
                                xAX.add(jsonArrayT.getString(i));
                            }
                            JSONArray jsonArrayC = jsonObject.getJSONArray("c");
                            for(int i = 0 ; i<jsonArrayC.length() ; i++){
                                cAX.add(jsonArrayC.getString(i));
                            }
                            JSONArray jsonArrayV = jsonObject.getJSONArray("v");
                            for(int i = 0 ; i<jsonArrayV.length() ; i++){
                                vAX.add(jsonArrayV.getString(i));
                            }

                            setLineChartData(xAX,cAX);
                            setBarChartData(xAX,vAX);

                        }
                        catch (Exception e){
//                            Toast.makeText(getActivity() ,e.getMessage(),Toast.LENGTH_LONG ).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(getActivity(), "خطا در برقرای اتصال به اینترنت", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(myReq);
    }

    public void setLineChartData(ArrayList<String> x , ArrayList<String> y) {

        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < x.size(); i++) {
            // turn your data into Entry objects
            entries.add(new BarEntry(i + 1, Long.parseLong(y.get(i))));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(getResources().getColor(R.color.colorGray));
        dataSet.setDrawValues(false);
        dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setDrawFilled(true);
        dataSet.setColor(Color.WHITE);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        if (Build.VERSION.SDK_INT > 17) {
            dataSet.setFillDrawable(getResources().getDrawable(R.drawable.chart_fill));
        }
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.invalidate(); // refresh
    }

    public void setBarChartData(ArrayList<String> x , ArrayList<String> y) {

        List<BarEntry> entries = new ArrayList<BarEntry>();
        for (int i = 0; i < x.size(); i++) {
            // turn your data into Entry objects
            entries.add(new BarEntry(i + 1, Long.parseLong(y.get(i))));
        }

        BarDataSet dataSet2 = new BarDataSet(entries, "Label"); // add entries to dataset
        dataSet2.setColor(getResources().getColor(R.color.colorGray));
        dataSet2.setDrawValues(false);

        BarData barData = new BarData(dataSet2);
        chart2.setData(barData);

        chart2.invalidate(); // refresh
    }

    public String getSavedChart(){
        TinyDB tinyDB = new TinyDB(SymbolActivity.this);
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        String s;
        ArrayList<String> symsChart;

        switch (active){
            case 1:
                symsChart = tinyDB.getListString("SymsDChartList");
                while (symsChart.size()<syms.size()) {
                    symsChart.add("");
                    tinyDB.putListString("SymsDChartList",symsChart);
                }
                s = symsChart.get(syms.indexOf(symbol));
                break;
            case 2:
                symsChart = tinyDB.getListString("SymsWChartList");
                while (symsChart.size()<syms.size()) {
                    symsChart.add("");
                    tinyDB.putListString("SymsWChartList",symsChart);
                }
                s = symsChart.get(syms.indexOf(symbol));
                break;
            case 3:
                symsChart = tinyDB.getListString("SymsMChartList");
                while (symsChart.size()<syms.size()) {
                    symsChart.add("");
                    tinyDB.putListString("SymsMChartList",symsChart);
                }
                s = symsChart.get(syms.indexOf(symbol));
                break;
            case 4:
                symsChart = tinyDB.getListString("Syms3MChartList");
                while (symsChart.size()<syms.size()) {
                    symsChart.add("");
                    tinyDB.putListString("Syms3MChartList",symsChart);
                }
                s = symsChart.get(syms.indexOf(symbol));
                break;
            case 5:
                symsChart = tinyDB.getListString("Syms6MChartList");
                while (symsChart.size()<syms.size()) {
                    symsChart.add("");
                    tinyDB.putListString("Syms6MChartList",symsChart);
                }
                s = symsChart.get(syms.indexOf(symbol));
                break;
            case 6:
                symsChart = tinyDB.getListString("SymsYChartList");
                while (symsChart.size()<syms.size()) {
                    symsChart.add("");
                    tinyDB.putListString("SymsYChartList",symsChart);
                }
                s = symsChart.get(syms.indexOf(symbol));
                break;
            case 7:
                symsChart = tinyDB.getListString("Syms2YChartList");
                while (symsChart.size()<syms.size()) {
                    symsChart.add("");
                    tinyDB.putListString("Syms2YChartList",symsChart);
                }
                s = symsChart.get(syms.indexOf(symbol));
                break;
            default:
                s = "";
                break;
        }
        return s;

    }

    public void saveChart(String s){
        TinyDB tinyDB = new TinyDB(SymbolActivity.this);
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        ArrayList<String> symsChart;

        switch (active){
            case 1:
                symsChart = tinyDB.getListString("SymsDChartList");
                symsChart.set(syms.indexOf(symbol) , s);
                tinyDB.putListString("SymsDChartList",symsChart);
                break;
            case 2:
                symsChart = tinyDB.getListString("SymsWChartList");
                symsChart.set(syms.indexOf(symbol) , s);
                tinyDB.putListString("SymsWChartList",symsChart);
                break;
            case 3:
                symsChart = tinyDB.getListString("SymsMChartList");
                symsChart.set(syms.indexOf(symbol) , s);
                tinyDB.putListString("SymsMChartList",symsChart);
                break;
            case 4:
                symsChart = tinyDB.getListString("Syms3MChartList");
                symsChart.set(syms.indexOf(symbol) , s);
                tinyDB.putListString("Syms3MChartList",symsChart);
                break;
            case 5:
                symsChart = tinyDB.getListString("Syms6MChartList");
                symsChart.set(syms.indexOf(symbol) , s);
                tinyDB.putListString("Syms6MChartList",symsChart);
                break;
            case 6:
                symsChart = tinyDB.getListString("SymsYChartList");
                symsChart.set(syms.indexOf(symbol) , s);
                tinyDB.putListString("SymsYChartList",symsChart);
                break;
            case 7:
                symsChart = tinyDB.getListString("Syms2YChartList");
                symsChart.set(syms.indexOf(symbol) , s);
                tinyDB.putListString("Syms2YChartList",symsChart);
                break;
            default:
                s = "";
                break;
        }

    }
}
