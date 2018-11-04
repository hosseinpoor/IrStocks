package ir.irse.stocks.fragments;

/*
----------------------------------------------------------------------------------------------------
////
////                            NEWS
////
----------------------------------------------------------------------------------------------------
 */


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import ir.irse.stocks.R;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.TinyDB;

public class MyFrag2 extends Fragment {

    String apiPerfix = "http://api.nemov.org/api/v1/";
    ListView list;
    String sym;
    View inf = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TinyDB tinyDB = new TinyDB(getActivity());
        sym = tinyDB.getString("selectedSym" );
        if(!sym.equals("")){
            inf = inflater.inflate(R.layout.fragment_my_frag2, container, false);
            // Inflate the layout for this fragment

            list = (ListView) inf.findViewById(R.id.news_list);

            getSavedNews();
            getAllNews();

    }
        else
    inf = inflater.inflate(R.layout.none, container, false);

        return inf;
    }

    public void getAllNews(){

        String market_url = apiPerfix+ "Market/News/";
        try {
            market_url  = market_url + URLEncoder.encode("بورس", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());

        StringRequest myReq = new StringRequest(Request.Method.GET, market_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            SharedPreferences pref = getActivity().getSharedPreferences("pref" , Context.MODE_PRIVATE);
                            SharedPreferences.Editor e = pref.edit();
                            e.putString("news" , s);
                            e.apply();

                            JSONArray jsonArray = new JSONArray(s);

                            final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

                            for(int i=0 ; i<jsonArray.length();i++){

                                HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
                                hashMap.put("title", PersianDigitConverter.PerisanNumber(jsonArray.getJSONObject(i).getString("Title").trim()));
                                hashMap.put("text", PersianDigitConverter.PerisanNumber(jsonArray.getJSONObject(i).getString("Source").trim() + " - "+  jsonArray.getJSONObject(i).getString("IRDate").trim()));
                                hashMap.put("link", jsonArray.getJSONObject(i).getString("Link"));
                                arrayList.add(hashMap);//add the hashmap into arrayList

                            }

                            String[] from = {"title", "text"};
                            int[] to = {R.id.news_title, R.id.news_text};
                            SimpleAdapter adb = new SimpleAdapter(getContext(), arrayList, R.layout.news_item, from, to);
                            list.setAdapter(adb);

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(0).get("link")));
                                    startActivity(browserIntent);


                                }
                            });

                        }
                        catch (Exception e){
//                            Toast.makeText(getActivity().getBaseContext(), "سرور دچار ایراد شده است. لطفا بعدا تلاش کنید", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        myReq.setRetryPolicy(new DefaultRetryPolicy( 5000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(myReq);


    }

    public void getSavedNews(){
        SharedPreferences pref = getActivity().getSharedPreferences("pref" , Context.MODE_PRIVATE);
        String s = pref.getString("news" , "");
        if(!s.equals("")){
            try {
                JSONArray jsonArray = new JSONArray(s);
                final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                for(int i=0 ; i<jsonArray.length();i++){
                    HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
                    hashMap.put("title", PersianDigitConverter.PerisanNumber(jsonArray.getJSONObject(i).getString("Title")));
                    hashMap.put("text", PersianDigitConverter.PerisanNumber(jsonArray.getJSONObject(i).getString("Source") + " - "+  jsonArray.getJSONObject(i).getString("IRDate")));
                    hashMap.put("link", PersianDigitConverter.PerisanNumber(jsonArray.getJSONObject(i).getString("Link") + " - "));
                    arrayList.add(hashMap);//add the hashmap into arrayList
                }
                String[] from = {"title", "text"};
                int[] to = {R.id.news_title, R.id.news_text};
                SimpleAdapter adb = new SimpleAdapter(getContext(), arrayList, R.layout.news_item, from, to);
                list.setAdapter(adb);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList.get(0).get("link")));
                        startActivity(browserIntent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
