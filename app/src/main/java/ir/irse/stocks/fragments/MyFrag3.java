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
    TextView name , priceLast , pDrCotVal , priceFirst , priceYesterday , baseVol , priceInterval,zTotTran , qTotCap , qTotTran  , eps;
    static RequestQueue queue;
    StringRequest myReq;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    DecimalFormat df = new DecimalFormat();

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
            priceLast = (TextView) inf.findViewById(R.id.priceLast);
            pDrCotVal = (TextView) inf.findViewById(R.id.pDrCotVal);
            priceFirst = (TextView) inf.findViewById(R.id.priceFirst);
            priceYesterday = (TextView) inf.findViewById(R.id.priceYesterday);
            baseVol = (TextView) inf.findViewById(R.id.baseVol);
            priceInterval = (TextView) inf.findViewById(R.id.priceInterval);
            zTotTran = (TextView) inf.findViewById(R.id.zTotTran);
            qTotCap = (TextView) inf.findViewById(R.id.qTotCap);
            qTotTran = (TextView) inf.findViewById(R.id.qTotTran);
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
                    name.setText( PersianDigitConverter.PerisanNumber(jsonObject.getString("LVal30")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PriceLast").isEmpty() && !jsonObject.getString("PriceLast").equals("null"))
                        priceLast.setText( PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceLast"))) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PDrCotVal").isEmpty() && !jsonObject.getString("PDrCotVal").equals("null"))
                        pDrCotVal.setText( PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PDrCotVal"))) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PriceFirst").isEmpty() && !jsonObject.getString("PriceFirst").equals("null"))
                        priceFirst.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceFirst"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PriceYesterday").isEmpty() && !jsonObject.getString("PriceYesterday").equals("null"))
                        priceYesterday.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceYesterday"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("BaseVol").isEmpty() && !jsonObject.getString("BaseVol").equals("null"))
                        baseVol.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("BaseVol"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("PriceMax").isEmpty() && !jsonObject.getString("PriceMax").equals("null") && !jsonObject.getString("PriceMin").isEmpty() && !jsonObject.getString("PriceMin").equals("null"))
                        priceInterval.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceMax"))) + " - " + PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceMin"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("ZTotTran").isEmpty() && !jsonObject.getString("ZTotTran").equals("null"))
                        zTotTran.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("ZTotTran"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("QTotCap").isEmpty() && !jsonObject.getString("QTotCap").equals("null"))
                        qTotCap.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("QTotCap"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("QTotTran5J").isEmpty() && !jsonObject.getString("QTotTran5J").equals("null"))
                        qTotTran.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("QTotTran5J"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if(!jsonObject.getString("EPS").isEmpty() && !jsonObject.getString("EPS").equals("null"))
                        eps.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("EPS"))));
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

                            try {
                                if(!jsonObject.getString("PriceLast").isEmpty() && !jsonObject.getString("PriceLast").equals("null"))
                                    priceLast.setText( PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceLast"))) );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("PDrCotVal").isEmpty() && !jsonObject.getString("PDrCotVal").equals("null"))
                                    pDrCotVal.setText( PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PDrCotVal"))) );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("PriceFirst").isEmpty() && !jsonObject.getString("PriceFirst").equals("null"))
                                    priceFirst.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceFirst"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("PriceYesterday").isEmpty() && !jsonObject.getString("PriceYesterday").equals("null"))
                                    priceYesterday.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceYesterday"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("BaseVol").isEmpty() && !jsonObject.getString("BaseVol").equals("null"))
                                    baseVol.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("BaseVol"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("PriceMax").isEmpty() && !jsonObject.getString("PriceMax").equals("null") && !jsonObject.getString("PriceMin").isEmpty() && !jsonObject.getString("PriceMin").equals("null"))
                                    priceInterval.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceMax"))) + " - " + PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("PriceMin"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("ZTotTran").isEmpty() && !jsonObject.getString("ZTotTran").equals("null"))
                                    zTotTran.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("ZTotTran"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("QTotCap").isEmpty() && !jsonObject.getString("QTotCap").equals("null"))
                                    qTotCap.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("QTotCap"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("QTotTran5J").isEmpty() && !jsonObject.getString("QTotTran5J").equals("null"))
                                    qTotTran.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("QTotTran5J"))));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                if(!jsonObject.getString("EPS").isEmpty() && !jsonObject.getString("EPS").equals("null"))
                                    eps.setText(PersianDigitConverter.PerisanNumber(rond(""+jsonObject.getLong("EPS"))));
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

    public String rond(String s){
        if(s.length()>6 && s.length()<10){
            return s.substring(0 , s.length()-6) + "." + s.substring(s.length()-6,s.length()-3) + "M";
        }
        else if(s.length()>9){
            return s.substring(0 , s.length()-9) + "." + s.substring(s.length()-9,s.length()-6) + "B";
        }
        else
        {
            symbols.setGroupingSeparator(',');
            df.setDecimalFormatSymbols(symbols);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(0);
            return df.format(Integer.parseInt(s));
        }
    }

}
