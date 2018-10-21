package ir.irse.wear;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import ir.irse.wear.other.TinyDB;

public class MainActivity extends WearableActivity {


    String apiPerfix = "http://api.nemov.org/api/v1/";
    WearableRecyclerView mWearableRecyclerView;
    ListAdapter adapter;
    ImageButton addbtn;
    TinyDB tinydb;
    ArrayList<String> stockSyms = new ArrayList<String>();
    ArrayList<String> stockSymsData = new ArrayList<String>();
    ArrayList<String> Names = new ArrayList<>();
    ArrayList<String> Percentage = new ArrayList<>();
    ArrayList<String> Pricechange = new ArrayList<>();
    ArrayList<String> Marketcap = new ArrayList<>();
    ArrayList<String> Value = new ArrayList<>();
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWearableRecyclerView = (WearableRecyclerView) findViewById(R.id.recycler_launcher_view);
        addbtn = (ImageButton) findViewById(R.id.addbtn);

        tinydb = new TinyDB(MainActivity.this);

        preferences = getSharedPreferences("Pref", MODE_PRIVATE);
        if (preferences.getBoolean("firstTime", true)) {
            firstRun();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();

        setAdapter();

        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");


        // Enables Always-on
        setAmbientEnabled();

        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setCircularScrollingGestureEnabled(true);
        mWearableRecyclerView.setBezelFraction(0.5f);
        mWearableRecyclerView.setScrollDegreesPerScreen(90);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this , AddActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");
        getAllSymbols();
        for(String s : stockSyms)
            getSymData(s);

        ArrayList<String> oldSyms = adapter.getAllSyms();
        ArrayList<String> newSyms = new ArrayList<>();
        newSyms.addAll(stockSyms);
        ArrayList<String> deletedSyms = new ArrayList<>();
        deletedSyms.addAll(oldSyms);

        deletedSyms.removeAll(newSyms);
        for(String s : deletedSyms){
            adapter.remove(s);
        }

        String name="--",per="--",pric="--",mc="--",val="--";
        JSONObject j = null;
        newSyms.removeAll(oldSyms);

        for(String s : newSyms) {
            int index = stockSyms.indexOf(s);
            name=s;
            per="--";pric="--";mc="--";val="--";
            try {
                j = new JSONObject(stockSymsData.get(index));
                float closing = j.getInt("PClosing");
                float yesterday = j.getInt("PriceYesterday");
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(symbols);
                df.setGroupingSize(3);
                df.setMaximumFractionDigits(0);
                val = df.format(j.getLong("PDrCotVal"));
                per = String.format("%,.2f", (((closing - yesterday) / yesterday) * 100)) + "%";
                pric = (closing - yesterday) + "";

            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.add(name , per , pric , mc , val);
        }
        adapter.notifyDataSetChanged();
    }

    public void setAdapter(){
        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");

        for(String s : stockSymsData){
            JSONObject j = null;
            try {
                j = new JSONObject(s);
                float closing = j.getInt("PClosing");
                float yesterday = j.getInt("PriceYesterday");
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(symbols);
                df.setGroupingSize(3);
                df.setMaximumFractionDigits(0);
                Names.add(j.getString("LVal18AFC"));
                Value.add(df.format(j.getLong("PDrCotVal")));
                Percentage.add(String.format("%,.2f", (((closing - yesterday) / yesterday) * 100)) + "%");
                Pricechange.add((closing - yesterday) + "");
                Marketcap.add("--");

            } catch (JSONException e) {
                e.printStackTrace();
                Names.add(stockSyms.get(stockSymsData.indexOf(s)));       Percentage.add("--") ; Pricechange.add("--") ; Marketcap.add("--") ; Value.add("--");
            }
        }
        adapter = new ListAdapter(this, Names , Percentage , Pricechange , Marketcap , Value);

        adapter.setClickListener(new ListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(MainActivity.this , SymbolActivity.class);
                i.putExtra("position" , position );
                startActivity(i);
            }
        });

        mWearableRecyclerView.setAdapter(adapter);

    }

    public void getAllSymbols() {

        String market_url = apiPerfix + "Market/Symbol";

        ArrayList<String> tempTitles = new ArrayList<>();
        ArrayList<String> tempNames = tinydb.getListString("restNames");
        if (tempNames.isEmpty()) {
            String s = readFirst();

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    tempNames.add(jsonArray.getJSONObject(i).getString("Name"));
                    tempTitles.add(jsonArray.getJSONObject(i).getString("Title"));
                }
                tinydb.putListString("restNames", tempNames);
                tinydb.putListString("restTitles", tempTitles);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest myReq = new JsonArrayRequest(Request.Method.GET, market_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try {
                            ArrayList<String> tempNames = new ArrayList<>();
                            ArrayList<String> tempTitles = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                tempNames.add(jsonArray.getJSONObject(i).getString("Name"));
                                tempTitles.add(jsonArray.getJSONObject(i).getString("Title"));
                            }
                            tinydb.putListString("restNames", tempNames);
                            tinydb.putListString("restTitles", tempTitles);
                        } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "سرور دچار ایراد شده است. لطفا بعدا تلاش کنید", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

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

    public void getSymData(final String symbol) {

        String url = "http://api.nemov.org/api/v1/Market/Symbol";

        try {
            url = url + "/" + URLEncoder.encode(symbol, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        stockSyms = tinydb.getListString("SymsList");
                        int index = stockSyms.indexOf(symbol);
                        stockSymsData = tinydb.getListString("SymsDataList");
                        stockSymsData.set(index, s);
                        tinydb.putListString("SymsDataList", stockSymsData);

                        JSONObject j = null;

                            try {
                                j = new JSONObject(stockSymsData.get(index));
                                try {
                                    if (!j.getString("PClosing").equals("null") && !j.getString("PriceYesterday").equals("null")) {
                                        Float closing = Float.parseFloat(j.getString("PClosing"));
                                        Float yesterday = Float.parseFloat(j.getString("PriceYesterday"));

                                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                                        symbols.setGroupingSeparator(',');
                                        DecimalFormat df = new DecimalFormat();
                                        df.setDecimalFormatSymbols(symbols);
                                        df.setGroupingSize(3);
                                        df.setMaximumFractionDigits(0);

                                        adapter.setPercentage(index , String.format("%,.2f", (((closing - yesterday) / yesterday) * 100)) + "%");
                                        adapter.setPriceChange(index , (closing - yesterday) + "");
                                        adapter.setValue(index ,df.format(j.getLong("PDrCotVal")));

                                    } else {
                                        adapter.setPercentage(index , "--");
                                        adapter.setPriceChange(index , "--");
                                        adapter.setValue(index ,"--" );
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//                            adapter = new ListAdapter(MainActivity.this, Names , Percentage , Pricechange , Marketcap , Value);
////                            mWearableRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

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

    public String readFirst() {
        return getResources().getString(R.string.list);
    }

    public void firstRun() {

        stockSyms.add("ذوب");
        stockSyms.add("کگل");
        stockSyms.add("فبیرا");
        tinydb.putListString("SymsList", stockSyms);

        stockSymsData.add(getResources().getString(R.string.zob));
        stockSymsData.add(getResources().getString(R.string.kgol));
        stockSymsData.add(getResources().getString(R.string.fbira));
        tinydb.putListString("SymsDataList", stockSymsData);
    }

}



