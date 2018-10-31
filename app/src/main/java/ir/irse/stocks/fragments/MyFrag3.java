package ir.irse.stocks.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

import ir.irse.stocks.MainActivity;
import ir.irse.stocks.R;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.TinyDB;

/*
----------------------------------------------------------------------------------------------------
////
////                            TABLE
////
----------------------------------------------------------------------------------------------------
 */

public class MyFrag3 extends Fragment {



    View inf = null;
    TextView name , open , max , min , vol , pe,cap , min52 , max52 , avg , eps;
    static RequestQueue queue;
    StringRequest myReq;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(queue!=null)
            queue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });

        TinyDB tinyDB = new TinyDB(getActivity());
        ArrayList<String> syms = tinyDB.getListString("SymsList");
        String sym = PersianDigitConverter.EnglishNumber(tinyDB.getString("selectedSym"));

        if(!sym.equals("") && !syms.isEmpty()){
            inf = inflater.inflate(R.layout.fragment_my_frag3, container, false);
            // Inflate the layout for this fragment
            int index = syms.indexOf(PersianDigitConverter.EnglishNumber(sym));

            name = (TextView) inf.findViewById(R.id.tbl_name);
            open = (TextView) inf.findViewById(R.id.open);
            max = (TextView) inf.findViewById(R.id.max);
            min = (TextView) inf.findViewById(R.id.min);
            vol = (TextView) inf.findViewById(R.id.vol);
            pe = (TextView) inf.findViewById(R.id.pe);
            cap = (TextView) inf.findViewById(R.id.cap);
            min52 = (TextView) inf.findViewById(R.id.min52);
            max52 = (TextView) inf.findViewById(R.id.max52);
            avg = (TextView) inf.findViewById(R.id.avg);
            eps = (TextView) inf.findViewById(R.id.eps);


            ArrayList<String> symsData = tinyDB.getListString("SymsDataList");
            String s = symsData.get(index);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(symbols);
                df.setGroupingSize(3);
                df.setMaximumFractionDigits(0);
                try {
                    if(!jsonObject.getString("QTotCap").isEmpty() && !jsonObject.getString("QTotCap").equals("null"))
                    cap.setText( PersianDigitConverter.PerisanNumber(df.format(jsonObject.getLong("QTotCap"))) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PriceFirst").isEmpty() && !jsonObject.getString("PriceFirst").equals("null"))
                    open.setText( PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("PriceFirst"))) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    name.setText( PersianDigitConverter.PerisanNumber(jsonObject.getString("LVal30")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PriceMax").isEmpty() && !jsonObject.getString("PriceMax").equals("null"))
                        max.setText(PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("PriceMax"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PriceMin").isEmpty() && !jsonObject.getString("PriceMin").equals("null"))
                        min.setText(PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("PriceMin"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("EPS").isEmpty() && !jsonObject.getString("EPS").equals("null"))
                        eps.setText(PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("EPS"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            getData(sym);
        }
        else
            inf = inflater.inflate(R.layout.none, container, false);

        return inf;
    }

    public void getData(String symbol){

        String market_url = "http://api.nemov.org/api/v1/Market/Symbol";
        try {
            market_url  = market_url + "/" + URLEncoder.encode(symbol, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        queue  = Volley.newRequestQueue(getActivity().getApplicationContext());

        myReq = new StringRequest(Request.Method.GET, market_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {

                            TinyDB tinyDB = new TinyDB(getActivity());
                            ArrayList<String> syms = tinyDB.getListString("SymsList");
                            String sym = PersianDigitConverter.EnglishNumber(tinyDB.getString("selectedSym"));
                            JSONObject jsonObject = new JSONObject(s);
                            ArrayList<String> symsData = tinyDB.getListString("SymsDataList");
                            symsData.set(syms.indexOf(sym) , s);
                            tinyDB.putListString("SymsDataList",symsData);
                            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                            symbols.setGroupingSeparator(',');
                            DecimalFormat df = new DecimalFormat();
                            df.setDecimalFormatSymbols(symbols);
                            df.setGroupingSize(3);
                            df.setMaximumFractionDigits(0);
                            try {
                                if(!jsonObject.getString("QTotCap").isEmpty() && !jsonObject.getString("QTotCap").equals("null"))
                                cap.setText( PersianDigitConverter.PerisanNumber(df.format(jsonObject.getLong("QTotCap"))) );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("PriceFirst").isEmpty() && !jsonObject.getString("PriceFirst").equals("null"))
                                open.setText( PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("PriceFirst")) ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("PriceMax").isEmpty() && !jsonObject.getString("PriceMax").equals("null"))
                                    max.setText(PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("PriceMax"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("PriceMin").isEmpty() && !jsonObject.getString("PriceMin").equals("null"))
                                    min.setText(PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("PriceMin"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("EPS").isEmpty() && !jsonObject.getString("EPS").equals("null"))
                                    eps.setText(PersianDigitConverter.PerisanNumber(df.format(jsonObject.getInt("EPS"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            getActivity().recreate();
                        }
                        catch (Exception e){
//                            Toast.makeText(getActivity(), "سرور دچار ایراد شده است. لطفا بعدا تلاش کنید", Toast.LENGTH_SHORT).show();
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

}
