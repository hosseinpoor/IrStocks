package ir.irse.stocks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.ronash.pushe.Pushe;
import ir.irse.stocks.fragments.ViewPagerAdapter;
import ir.irse.stocks.fragments.ViewPagerAdapterLand;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.StockItem;
import ir.irse.stocks.other.TinyDB;


public class MainActivity extends AppCompatActivity {

    String apiPerfix = "http://api.nemov.org/api/v1/";
    ArrayList<String> stockSyms = new ArrayList<String>();
    ArrayList<String> stockSymsData = new ArrayList<String>();
    ViewPager viewPager;
    ViewPagerAdapter adb;
    ListView list;
    StockItem item;
    List<StockItem> all_messages;
    ListAdapter adapter;
    TextView p1, p2, p3;
    TinyDB tinydb;
    int currentTab = 0;
    String pid = "";

    Handler handler = new Handler();
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                setMarketState();

            getAllSymbols();
            for (String i : stockSyms) {
                getSymData(i);
            }

            // Repeat this the same runnable code block again another 60 seconds
            handler.postDelayed(runnableCode, 60000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pushe.initialize(this, true);
        pid = Pushe.getPusheId(this);
        tinydb = new TinyDB(MainActivity.this);
        firstRun();
        getVersion();
        handler.post(runnableCode);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape

            final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerLandId);
            int l = stockSyms.size();
            if (l == 0)
                setContentView(R.layout.none);
            ViewPagerAdapterLand adb = new ViewPagerAdapterLand(getSupportFragmentManager(), MainActivity.this, l);
            viewPager.setAdapter(adb);
            String currentSym = tinydb.getString("selectedSym");
            int index = stockSyms.indexOf(PersianDigitConverter.EnglishNumber(currentSym));
            viewPager.setCurrentItem(index);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    currentTab = viewPager.getCurrentItem();
                    tinydb.putString("selectedSym", PersianDigitConverter.PerisanNumber(stockSyms.get(currentTab)));
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

        } else {
            // In portrait

            setDots();

            list = (ListView) findViewById(R.id.listViewId);
            if (Build.VERSION.SDK_INT < 21) {
                list.setSelector(R.color.colorPrimary);
            }
            all_messages = new ArrayList<StockItem>();

            stockSyms = tinydb.getListString("SymsList");
            for (String i : stockSyms) {

                int index = stockSyms.indexOf(i);
                JSONObject j = null;

                item = new StockItem(PersianDigitConverter.PerisanNumber(i), "", "", "", "", "", "");

                try {
                    stockSymsData = tinydb.getListString("SymsDataList");
                    j = new JSONObject(stockSymsData.get(index));

                    float closing = j.getInt("PClosing");
                    float yesterday = j.getInt("PriceYesterday");

                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setGroupingSeparator(',');
                    DecimalFormat df = new DecimalFormat();
                    df.setDecimalFormatSymbols(symbols);
                    df.setGroupingSize(3);
                    df.setMaximumFractionDigits(0);

                    item = new StockItem(PersianDigitConverter.PerisanNumber(i), "", "", "",
                            PersianDigitConverter.PerisanNumber(String.format("%,.2f", (((closing - yesterday) / yesterday) * 100))) + "%"
                            , PersianDigitConverter.PerisanNumber(df.format(j.getLong("PDrCotVal"))),
                            PersianDigitConverter.PerisanNumber((closing - yesterday) + ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                all_messages.add(item);
            }

            adapter = new ListAdapter(this, all_messages);

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String temp = all_messages.get(i).getSymbol();
                    tinydb.putString("selectedSym", PersianDigitConverter.PerisanNumber(temp));
                    setDots();
                    adapter.notifyDataSetChanged();
                    list.invalidateViews();
                }
            });

            String temp = tinydb.getString("selectedSym");
            list.setSelection(stockSyms.indexOf(PersianDigitConverter.EnglishNumber(temp)));

            ImageView options = (ImageView) findViewById(R.id.options);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, FullList.class);
                    startActivity(i);

                    handler.removeCallbacks(runnableCode);
                    finish();
                }
            });

            final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    handler.removeCallbacks(runnableCode);
                    handler.post(runnableCode);
                    pullToRefresh.setRefreshing(false);
                }
            });

        }

    }

    @Override
    public void recreate() {

        String temp = tinydb.getString("selectedSym");
        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");
        int index = stockSyms.indexOf(PersianDigitConverter.EnglishNumber(temp));
        JSONObject j = null;
        item = new StockItem(PersianDigitConverter.PerisanNumber(temp), "", "", "", "", "", "");
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
            item = new StockItem(PersianDigitConverter.PerisanNumber(temp), "", "", "",
                    PersianDigitConverter.PerisanNumber(String.format("%,.2f", (((closing - yesterday) / yesterday) * 100))) + "%"
                    , PersianDigitConverter.PerisanNumber(df.format(j.getLong("PDrCotVal"))),
                    PersianDigitConverter.PerisanNumber((closing - yesterday) + ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        all_messages.set(index, item);

        adapter.notifyDataSetChanged();
        list.invalidateViews();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnableCode);
    }

    public void setDots() {
        p1 = (TextView) findViewById(R.id.first_page_dot);
        p2 = (TextView) findViewById(R.id.second_page_dot);
        p3 = (TextView) findViewById(R.id.third_page_dot);

        viewPager = (ViewPager) findViewById(R.id.viewPagerId);

        adb = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adb);
        viewPager.setCurrentItem(currentTab);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                currentTab = viewPager.getCurrentItem();

                if (currentTab == 0) {
                    p1.setTextColor(Color.WHITE);
                    p2.setTextColor(getResources().getColor(R.color.colorGray));
                    p3.setTextColor(getResources().getColor(R.color.colorGray));
                } else if (currentTab == 1) {
                    p2.setTextColor(Color.WHITE);
                    p1.setTextColor(getResources().getColor(R.color.colorGray));
                    p3.setTextColor(getResources().getColor(R.color.colorGray));
                } else {
                    p3.setTextColor(Color.WHITE);
                    p2.setTextColor(getResources().getColor(R.color.colorGray));
                    p1.setTextColor(getResources().getColor(R.color.colorGray));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void firstRun() {
        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");
        ArrayList<String> chartD = tinydb.getListString("SymsDChartList");
        ArrayList<String> chartW = tinydb.getListString("SymsWChartList");
        ArrayList<String> chartM = tinydb.getListString("SymsMChartList");
        ArrayList<String> chart3M = tinydb.getListString("Syms3MChartList");
        ArrayList<String> chart6M = tinydb.getListString("Syms6MChartList");
        ArrayList<String> chartY = tinydb.getListString("SymsYChartList");
        ArrayList<String> chart2Y = tinydb.getListString("Syms2YChartList");
        ArrayList<String> chart5Y = tinydb.getListString("Syms5YChartList");
        ArrayList<String> chart10Y = tinydb.getListString("Syms10YChartList");

        SharedPreferences preferences = getSharedPreferences("Pref", MODE_PRIVATE);
        if (preferences.getBoolean("firstTime", true)) {
            stockSyms.add("ذوب");
            stockSymsData.add(getResources().getString(R.string.zob));
            chartD.add(" ");
            chartW.add(" ");
            chartM.add(" ");
            chart3M.add(" ");
            chart6M.add(" ");
            chartY.add(" ");
            chart2Y.add(" ");
            chart5Y.add(" ");
            chart10Y.add(" ");

            stockSyms.add("کگل");
            stockSymsData.add(getResources().getString(R.string.kgol));
            chartD.add(" ");
            chartW.add(" ");
            chartM.add(" ");
            chart3M.add(" ");
            chart6M.add(" ");
            chartY.add(" ");
            chart2Y.add(" ");
            chart5Y.add(" ");
            chart10Y.add(" ");

            stockSyms.add("فبیرا");
            stockSymsData.add(getResources().getString(R.string.fbira));
            chartD.add(" ");
            chartW.add(" ");
            chartM.add(" ");
            chart3M.add(" ");
            chart6M.add(" ");
            chartY.add(" ");
            chart2Y.add(" ");
            chart5Y.add(" ");
            chart10Y.add(" ");

            tinydb.putListString("SymsDChartList", chartD);
            tinydb.putListString("SymsWChartList", chartW);
            tinydb.putListString("SymsMChartList", chartM);
            tinydb.putListString("Syms3MChartList", chart3M);
            tinydb.putListString("Syms6MChartList", chart6M);
            tinydb.putListString("SymsYChartList", chartY);
            tinydb.putListString("Syms2YChartList", chart2Y);
            tinydb.putListString("Syms5YChartList", chart5Y);
            tinydb.putListString("Syms10YChartList", chart10Y);
            tinydb.putListString("SymsList", stockSyms);
            tinydb.putListString("SymsDataList", stockSymsData);
            tinydb.putString("selectedSym", "ذوب");

        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();
    }

    public void setMarketState() {


        String market_url = apiPerfix + "Market/Status";

        final TextView t = (TextView) findViewById(R.id.market_state);
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String update = pref.getString("lastupdate", "");
        if (!update.isEmpty()) {
            SimpleDateFormat dd = new SimpleDateFormat("dd");
            String currentDate = dd.format(new Date());
            if (currentDate.equals(update.substring(0, 2)))
                t.setText("آخرین به روز رسانی : " + PersianDigitConverter.PerisanNumber(update.substring(3)));
            else if (currentDate.equals((Integer.parseInt(update.substring(0, 2)) + 1) + ""))
                t.setText("آخرین به روز رسانی : دیروز");
            else
                t.setText("آخرین به روز رسانی : چند روز پیش");
        } else {
            t.setText("لطفا به روز رسانی کنید");
        }

        final JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, market_url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            t.setText(jsonObject.getString("Status"));
                            SimpleDateFormat dhms = new SimpleDateFormat("dd:HH:mm");
                            String currentDateandTime = dhms.format(new Date());
                            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("lastupdate", currentDateandTime);
                            edit.apply();
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

    public void getAllSymbols() {

        String market_url = apiPerfix + "Market/Symbol";

        ArrayList<String> tempTitles = new ArrayList<>();
        ArrayList<String> tempNames = tinydb.getListString("restNames");
        if (tempNames.isEmpty()) {
            String s = readFirst();
            try {
                JSONArray jsonArray = new JSONArray(s);
                Log.e("e", s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    tempNames.add(jsonArray.getJSONObject(i).getString("Name"));
                    tempTitles.add(jsonArray.getJSONObject(i).getString("Title"));
                }
                tinydb.putListString("restNames", tempNames);
                tinydb.putListString("restTitles", tempTitles);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonArrayRequest myReq = new JsonArrayRequest(Request.Method.GET, market_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
//                            Toast.makeText(MainActivity.this , "again" , Toast.LENGTH_LONG).show();
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
        final int index = stockSyms.indexOf(symbol);
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
                        stockSymsData = tinydb.getListString("SymsDataList");
                        stockSymsData.set(index, s);
                        tinydb.putListString("SymsDataList", stockSymsData);

                        int orientation = getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                            int index = stockSyms.indexOf(symbol);
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

                                        item = new StockItem(PersianDigitConverter.PerisanNumber(symbol), "", "", "",
                                                PersianDigitConverter.PerisanNumber(String.format("%,.2f", (((closing - yesterday) / yesterday) * 100))) + "%"
                                                , PersianDigitConverter.PerisanNumber(df.format(j.getLong("PDrCotVal"))),
                                                PersianDigitConverter.PerisanNumber((closing - yesterday) + ""));
                                    } else {
                                        item = new StockItem(PersianDigitConverter.PerisanNumber(symbol), "--", "", "",
                                                "--"
                                                , "--",
                                                "--");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            all_messages.set(index, item);

                            adapter.notifyDataSetChanged();
                            list.invalidateViews();
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

    public String readFirst() {

        return getResources().getString(R.string.list);
//        BufferedReader reader = null;
//        String json = "";
//        try {
//            reader = new BufferedReader(
//                    new InputStreamReader(getAssets().open("list.txt"), "UTF-8"));
//
//            // do reading, usually loop until end of file reading
//
//            String mLine;
//            while ((mLine = reader.readLine()) != null) {
//                //process line
//                json += mLine;
//            }
//        } catch (IOException e) {
//            //log the exception
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    //log the exception
//                }
//            }
//        }
//        return json;
    }

    public void getVersion() {

        String url = apiPerfix + "Market/Version";

        final SharedPreferences preferences = getSharedPreferences("Pref", MODE_PRIVATE);
        final SharedPreferences.Editor e = preferences.edit();
        Long current = preferences.getLong("Days", 0);
        final Long tsLong = System.currentTimeMillis() / 86400000;
        final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        if (tsLong - current > 6) {
            final JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                e.putLong("Days", tsLong);
                                e.apply();
                                String ver = jsonObject.getString("Version");
                                float newver = Float.parseFloat(ver);
                                float oldver = Float.parseFloat(preferences.getString("Ver", "0"));
                                if (newver > oldver && newver > 1) {
                                    e.putString("Ver", ver);
                                    e.apply();
                                    Toast.makeText(MainActivity.this, "نسخه جدید تری از برنامه موجود میباشد", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "سرور دچار ایراد شده است. لطفا بعدا تلاش کنید", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
//                    Toast.makeText(MainActivity.this, "err", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(myReq);

        }


    }


}

