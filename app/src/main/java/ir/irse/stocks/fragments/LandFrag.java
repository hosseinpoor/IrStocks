package ir.irse.stocks.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ir.irse.stocks.R;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.TinyDB;

public class LandFrag extends Fragment {

    private int position;
    private String sym;
    private TextView stock , name , value , mrk_cap , per;
    private TextView b1 , b2 , b3 , b4 , b5 , b6 , b7 , b8 , b9;
    private LineChart chart;
    private BarChart chart2;
    int active = 1;
    ColorStateList oldColors;
    Random rand = new Random();
    static ArrayList<String> Syms = new ArrayList<>();
    static ArrayList<String> SymsData = new ArrayList<>();

    public static LandFrag newInstance(String sym, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("sym",sym);
        bundle.putInt("position",position);
        LandFrag fragment = new LandFrag();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            sym = bundle.getString("sym");
            position = bundle.getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.land_flag, container, false);

        final TinyDB tinydb = new TinyDB(getActivity());
        Syms = tinydb.getListString("SymsList");
        SymsData = tinydb.getListString("SymsDataList");

        stock =     (TextView) view.findViewById(R.id.txt_stock_land);
        name =      (TextView) view.findViewById(R.id.txt_name_land);
        value =     (TextView) view.findViewById(R.id.txt_value_land);
        mrk_cap =   (TextView) view.findViewById(R.id.txt_cat_land);
        per =       (TextView) view.findViewById(R.id.txt_per_land);
        chart =     (LineChart) view.findViewById(R.id.chart_land);
        chart2 =    (BarChart) view.findViewById(R.id.chart2_land);
        b1 =        (TextView) view.findViewById(R.id.one_day_btn) ;
        b2 =        (TextView) view.findViewById(R.id.one_week_btn) ;
        b3 =        (TextView) view.findViewById(R.id.one_month_btn) ;
        b4 =        (TextView) view.findViewById(R.id.three_month_btn) ;
        b5 =        (TextView) view.findViewById(R.id.six_month_btn) ;
        b6 =        (TextView) view.findViewById(R.id.one_year_btn) ;
        b7 =        (TextView) view.findViewById(R.id.two_year_btn) ;
        b8 =        (TextView) view.findViewById(R.id.five_year_btn) ;
        b9 =        (TextView) view.findViewById(R.id.ten_year_btn) ;

        readBundle(getArguments());
        int index =  Syms.indexOf(sym);
        stock.setText(PersianDigitConverter.PerisanNumber(Syms.get(index)));
        try {
            JSONObject j = new JSONObject(SymsData.get(index));
            name.setText(PersianDigitConverter.PerisanNumber(j.getString("LVal30")));

            if (!j.getString("PClosing").equals("null") && !j.getString("PriceYesterday").equals("null")) {
                Float closing = Float.parseFloat(j.getString("PClosing") + "");
                Float yesterday = Float.parseFloat(j.getString("PriceYesterday") + "");

                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(symbols);
                df.setGroupingSize(3);
                df.setMaximumFractionDigits(0);
                value.setText(PersianDigitConverter.PerisanNumber(df.format(j.getLong("PDrCotVal"))));
                per.setText(PersianDigitConverter.PerisanNumber(String.format("%,.2f", (((closing - yesterday) / yesterday) * 100))) + "%");
                mrk_cap.setText(PersianDigitConverter.PerisanNumber(String.format("%,.0f", (closing - yesterday)) + ""));
            }







        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mrk_cap.getText().toString().startsWith("-"))
            mrk_cap.setTextColor(getResources().getColor(R.color.colorRed));
        else
            mrk_cap.setTextColor(getResources().getColor(R.color.colorGreen));

        if(per.getText().toString().startsWith("-"))
            per.setTextColor(getResources().getColor(R.color.colorRed));
        else
            per.setTextColor(getResources().getColor(R.color.colorGreen));

        oldColors =  b2.getTextColors();
        setColors();
        chartPrepare();
        setChart();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =1;
                setColors();
                setChart();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =2;
                setColors();
                setChart();
            }
        });


        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =3;
                setColors();
                setChart();
            }
        });


        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =4;
                setColors();
                setChart();
            }
        });


        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =5;
                setColors();
                setChart();
            }
        });


        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =6;
                setColors();
                setChart();
            }
        });


        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =7;
                setColors();
                setChart();
            }
        });


        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =8;
                setColors();
                setChart();
            }
        });


        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active =9;
                setColors();
                setChart();
            }
        });



        return view;
    }

    public void chartPrepare(){
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.zoom(0,0,0,0);
        chart.setScaleEnabled(false);
        chart.setClickable(false);
        chart.setPinchZoom(false);
        chart.setTouchEnabled(false);
        chart.setNoDataTextColor(getResources().getColor(R.color.colorPrimary));
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setTextColor(Color.GRAY);
        chart.getAxisRight().setTextColor(Color.GRAY);
        chart.getAxisLeft().setTextColor(Color.GRAY);
        chart.setBackgroundColor(Color.BLACK);
        chart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);    // Hide the description
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setDrawLabels(false);
        chart2.setViewPortOffsets(0, 0, 0, 0);
        chart2.zoom(0,0,0,0);
        chart2.setScaleEnabled(false);
        chart2.setClickable(false);
        chart2.setPinchZoom(false);
        chart2.setTouchEnabled(false);
        chart2.setNoDataTextColor(getResources().getColor(R.color.colorPrimary));
        chart2.getAxisRight().setDrawGridLines(false);
        chart2.getAxisLeft().setDrawGridLines(false);
        chart2.getXAxis().setDrawGridLines(false);
        chart2.getXAxis().setDrawAxisLine(false);
        chart2.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        chart2.getLegend().setEnabled(false);
        chart2.setDescription(description);    // Hide the description
        chart2.getAxisLeft().setDrawLabels(false);
        chart2.getAxisRight().setDrawLabels(false);
        chart2.getXAxis().setDrawLabels(false);
        chart2.getAxisLeft().setEnabled(false);
        chart2.getAxisRight().setEnabled(false);
        chart.setNoDataText("");
        chart2.setNoDataText("");
    }

    public void setColors(){
        switch (active) {
            case 1:
                b1.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b3.setTextColor(oldColors); b4.setTextColor(oldColors); b5.setTextColor(oldColors);
                b6.setTextColor(oldColors); b7.setTextColor(oldColors); b8.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 2:
                b2.setTextColor(Color.WHITE);
                b1.setTextColor(oldColors); b3.setTextColor(oldColors); b4.setTextColor(oldColors); b5.setTextColor(oldColors);
                b6.setTextColor(oldColors); b7.setTextColor(oldColors); b8.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 3:
                b3.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b1.setTextColor(oldColors); b4.setTextColor(oldColors); b5.setTextColor(oldColors);
                b6.setTextColor(oldColors); b7.setTextColor(oldColors); b8.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 4:
                b4.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b3.setTextColor(oldColors); b1.setTextColor(oldColors); b5.setTextColor(oldColors);
                b6.setTextColor(oldColors); b7.setTextColor(oldColors); b8.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 5:
                b5.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b3.setTextColor(oldColors); b4.setTextColor(oldColors); b1.setTextColor(oldColors);
                b6.setTextColor(oldColors); b7.setTextColor(oldColors); b8.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 6:
                b6.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b3.setTextColor(oldColors); b4.setTextColor(oldColors); b5.setTextColor(oldColors);
                b1.setTextColor(oldColors); b7.setTextColor(oldColors); b8.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 7:
                b7.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b3.setTextColor(oldColors); b4.setTextColor(oldColors); b5.setTextColor(oldColors);
                b6.setTextColor(oldColors); b1.setTextColor(oldColors); b8.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 8:
                b8.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b3.setTextColor(oldColors); b4.setTextColor(oldColors); b5.setTextColor(oldColors);
                b6.setTextColor(oldColors); b7.setTextColor(oldColors); b1.setTextColor(oldColors); b9.setTextColor(oldColors);
                break;
            case 9:
                b9.setTextColor(Color.WHITE);
                b2.setTextColor(oldColors); b3.setTextColor(oldColors); b4.setTextColor(oldColors); b5.setTextColor(oldColors);
                b6.setTextColor(oldColors); b7.setTextColor(oldColors); b8.setTextColor(oldColors); b1.setTextColor(oldColors);
                break;
            default:
                break;
        }
    }

    public void getChart(String time){
        TinyDB tinyDB = new TinyDB(getActivity());
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        ArrayList<String> data = tinyDB.getListString("SymsDataList");
        int index = syms.indexOf(sym);
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
            if (xAX.size() > 0) {
                setLineChartData(xAX, cAX);
                setBarChartData(xAX, vAX);
            }
            else{
                chart.setData(null);
                chart.invalidate();
                chart2.setData(null);
                chart2.invalidate();
            }
        }
        catch (Exception e){
            chart.setData(null);
            chart.invalidate();
            chart2.setData(null);
            chart2.invalidate();
//                            Toast.makeText(getActivity() ,e.getMessage(),Toast.LENGTH_LONG ).show();
        }

        String market_url = "http://api.nemov.org/api/v1/Market/Chart/" + id + "/" + time;

        RequestQueue queue  = Volley.newRequestQueue(getActivity().getApplicationContext());
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

                            if (xAX.size() > 0) {
                                setLineChartData(xAX, cAX);
                                setBarChartData(xAX, vAX);
                            }
                            else{
                                chart.setData(null);
                                chart.invalidate();
                                chart2.setData(null);
                                chart2.invalidate();
                            }

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
        myReq.setRetryPolicy(new DefaultRetryPolicy( 5000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

            case 8:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(6, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("5Y");

                break;

            case 9:

                leftAxis = chart.getXAxis();
                leftAxis.setLabelCount(11, true);
                leftAxis.setGranularityEnabled(true);
                leftAxis.setGranularity(1);

                getChart("10Y");

                break;
            default:
                break;
        }
    }

    public String getSavedChart(){
        TinyDB tinyDB = new TinyDB(getActivity());
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        String s;
        ArrayList<String> symsChart;

        switch (active){
            case 1:
                symsChart = tinyDB.getListString("SymsDchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 2:
                symsChart = tinyDB.getListString("SymsWchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 3:
                symsChart = tinyDB.getListString("SymsMchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 4:
                symsChart = tinyDB.getListString("Syms3MchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 5:
                symsChart = tinyDB.getListString("Syms6MchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 6:
                symsChart = tinyDB.getListString("SymsYchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 7:
                symsChart = tinyDB.getListString("Syms2YchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 8:
                symsChart = tinyDB.getListString("Syms5YchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 9:
                symsChart = tinyDB.getListString("Syms10YchartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            default:
                s = "";
                break;
        }
        return s;

    }

    public void saveChart(String s){

        TinyDB tinyDB = new TinyDB(getActivity());
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        ArrayList<String> symsChart;
        switch (active){
            case 1:
                symsChart = tinyDB.getListString("SymsDchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsDchartList",symsChart);
                break;
            case 2:
                symsChart = tinyDB.getListString("SymsWchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsWchartList",symsChart);
                break;
            case 3:
                symsChart = tinyDB.getListString("SymsMchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsMchartList",symsChart);
                break;
            case 4:
                symsChart = tinyDB.getListString("Syms3MchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms3MchartList",symsChart);
                break;
            case 5:
                symsChart = tinyDB.getListString("Syms6MchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms6MchartList",symsChart);
                break;
            case 6:
                symsChart = tinyDB.getListString("SymsYchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsYchartList",symsChart);
                break;
            case 7:
                symsChart = tinyDB.getListString("Syms2YchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms2YchartList",symsChart);
                break;
            case 8:
                symsChart = tinyDB.getListString("Syms5YchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms5YchartList",symsChart);
                break;
            case 9:
                symsChart = tinyDB.getListString("Syms10YchartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms10YchartList",symsChart);
                break;
            default:
                s = "";
                break;
        }

    }



}
