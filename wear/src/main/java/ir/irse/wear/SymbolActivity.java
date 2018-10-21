package ir.irse.wear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import ir.irse.wear.other.PersianDigitConverter;
import ir.irse.wear.other.TinyDB;

public class SymbolActivity extends WearableActivity {

    TextView name , sym , remove , data;
    ArrayList<String> stockSyms = new ArrayList<String>();
    ArrayList<String> stockSymsData = new ArrayList<String>();
    TinyDB tinydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        name = findViewById(R.id.name);
        sym = findViewById(R.id.symbol);
        remove = findViewById(R.id.removebtn);
        data = findViewById(R.id.datatxt);
        tinydb = new TinyDB(SymbolActivity.this);
        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");
        Bundle b = getIntent().getExtras();
        final int index = b.getInt("position");
        final String symbol = stockSyms.get(index);
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

    }

    public void getSymData(final String symbol)
    {

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
}
