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
import java.util.ArrayList;
import java.util.List;
import ir.irse.stocks.R;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.TinyDB;

/*
----------------------------------------------------------------------------------------------------
////
////                            CHART
////
----------------------------------------------------------------------------------------------------
 */

public class MyFrag1 extends Fragment {

    TextView t1,t2,t3,t4,t5,t6,t7;
    int active=1;
    ColorStateList oldColors;
    LineChart chart;
    String sym;
    View inf = null;
    BarChart chart2;
    ArrayList<String> syms = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TinyDB tinyDB = new TinyDB(getActivity());
        syms = tinyDB.getListString("SymsList");
        sym = PersianDigitConverter.EnglishNumber(tinyDB.getString("selectedSym"));

        if(!sym.equals("")  && !syms.isEmpty() ) {
            inf = inflater.inflate(R.layout.fragment_my_frag1, container, false);
            // Inflate the layout for this fragment

            chartPrepare();

            setChart();

            t1 = (TextView) inf.findViewById(R.id.one_day_btn);
            t2 = (TextView) inf.findViewById(R.id.one_week_btn);
            t3 = (TextView) inf.findViewById(R.id.one_month_btn);
            t4 = (TextView) inf.findViewById(R.id.three_month_btn);
            t5 = (TextView) inf.findViewById(R.id.six_month_btn);
            t6 = (TextView) inf.findViewById(R.id.one_year_btn);
            t7 = (TextView) inf.findViewById(R.id.two_year_btn);

            oldColors = t2.getTextColors();
            setColors();

            t1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    active = 1;
                    setColors();
                    setChart();
                }
            });

            t2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    active = 2;
                    setColors();
                    setChart();
                }
            });

            t3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    active = 3;
                    setColors();
                    setChart();
                }
            });

            t4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    active = 4;
                    setColors();
                    setChart();
                }
            });

            t5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    active = 5;
                    setColors();
                    setChart();
                }
            });

            t6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    active = 6;
                    setColors();
                    setChart();
                }
            });

            t7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    active = 7;
                    setColors();
                    setChart();
                }
            });
        }
        else
            inf = inflater.inflate(R.layout.none, container, false);
        return inf;
    }

    public void chartPrepare(){
        chart = (LineChart) inf.findViewById(R.id.chart);
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
        chart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        chart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);    // Hide the description
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setDrawLabels(false);
        chart.setNoDataText("");
        chart2 = (BarChart) inf.findViewById(R.id.chart2);
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
        TinyDB tinyDB = new TinyDB(getActivity());
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        ArrayList<String> data = tinyDB.getListString("SymsDataList");
        int index = syms.indexOf(PersianDigitConverter.EnglishNumber(sym));
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
        TinyDB tinyDB = new TinyDB(getActivity());
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        String s;
        ArrayList<String> symsChart;

        switch (active){
            case 1:
                symsChart = tinyDB.getListString("SymsDChartList");
                s = symsChart.get(syms.indexOf(PersianDigitConverter.EnglishNumber(sym)));
                break;
            case 2:
                symsChart = tinyDB.getListString("SymsWChartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 3:
                symsChart = tinyDB.getListString("SymsMChartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 4:
                symsChart = tinyDB.getListString("Syms3MChartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 5:
                symsChart = tinyDB.getListString("Syms6MChartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 6:
                symsChart = tinyDB.getListString("SymsYChartList");
                s = symsChart.get(syms.indexOf(sym));
                break;
            case 7:
                symsChart = tinyDB.getListString("Syms2YChartList");
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
                symsChart = tinyDB.getListString("SymsDChartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsDChartList",symsChart);
                break;
            case 2:
                symsChart = tinyDB.getListString("SymsWChartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsWChartList",symsChart);
                break;
            case 3:
                symsChart = tinyDB.getListString("SymsMChartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsMChartList",symsChart);
                break;
            case 4:
                symsChart = tinyDB.getListString("Syms3MChartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms3MChartList",symsChart);
                break;
            case 5:
                symsChart = tinyDB.getListString("Syms6MChartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms6MChartList",symsChart);
                break;
            case 6:
                symsChart = tinyDB.getListString("SymsYChartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("SymsYChartList",symsChart);
                break;
            case 7:
                symsChart = tinyDB.getListString("Syms2YChartList");
                symsChart.set(syms.indexOf(sym) , s);
                tinyDB.putListString("Syms2YChartList",symsChart);
                break;
            default:
                s = "";
                break;
        }

    }

    public void setColors(){
        switch (active) {
            case 1:
                t1.setTextColor(Color.WHITE);
                t2.setTextColor(oldColors); t3.setTextColor(oldColors); t4.setTextColor(oldColors); t5.setTextColor(oldColors); t6.setTextColor(oldColors); t7.setTextColor(oldColors);
                break;
            case 2:
                t2.setTextColor(Color.WHITE);
                t1.setTextColor(oldColors); t3.setTextColor(oldColors); t4.setTextColor(oldColors); t5.setTextColor(oldColors); t6.setTextColor(oldColors); t7.setTextColor(oldColors);
                break;
            case 3:
                t3.setTextColor(Color.WHITE);
                t1.setTextColor(oldColors); t2.setTextColor(oldColors); t4.setTextColor(oldColors); t5.setTextColor(oldColors); t6.setTextColor(oldColors); t7.setTextColor(oldColors);
                break;
            case 4:
                t4.setTextColor(Color.WHITE);
                t1.setTextColor(oldColors); t2.setTextColor(oldColors); t3.setTextColor(oldColors); t5.setTextColor(oldColors); t6.setTextColor(oldColors); t7.setTextColor(oldColors);
                break;
            case 5:
                t5.setTextColor(Color.WHITE);
                t1.setTextColor(oldColors); t3.setTextColor(oldColors); t4.setTextColor(oldColors); t2.setTextColor(oldColors); t6.setTextColor(oldColors); t7.setTextColor(oldColors);
                break;
            case 6:
                t6.setTextColor(Color.WHITE);
                t2.setTextColor(oldColors); t3.setTextColor(oldColors); t4.setTextColor(oldColors); t5.setTextColor(oldColors); t1.setTextColor(oldColors); t7.setTextColor(oldColors);
                break;
            case 7:
                t7.setTextColor(Color.WHITE);
                t2.setTextColor(oldColors); t3.setTextColor(oldColors); t4.setTextColor(oldColors); t5.setTextColor(oldColors); t6.setTextColor(oldColors); t1.setTextColor(oldColors);
                break;
            default:
                break;
        }
    }


}
