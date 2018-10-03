package ir.irse.stocks.other;

        import android.content.Context;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.UnsupportedEncodingException;
        import java.net.URLEncoder;
        import java.util.ArrayList;

        import co.ronash.pushe.PusheListenerService;
        import ir.irse.stocks.FullList;
        import ir.irse.stocks.other.TinyDB;


public class MyPushListener extends PusheListenerService {

    ArrayList<String> stockSyms = new ArrayList<String>();
    ArrayList<String> SymsData = new ArrayList<>();

    @Override
    public void onMessageReceived(JSONObject customContent, JSONObject pushMessage) {

        if (customContent == null || customContent.length() == 0)
            return; //json is empty
        android.util.Log.i("Pushe", "Custom json Message: " + customContent.toString()); //print json to logCat
        //Do something with json
        try {

            String sym = customContent.getString("sym");
            android.util.Log.i("Pushe", "Json Message\n sym: " + sym );

            TinyDB tinydb = new TinyDB(getBaseContext());
            stockSyms = tinydb.getListString("SymsList");
            SymsData = tinydb.getListString("SymsDataList");
            stockSyms.add(sym);
            tinydb.putListString("SymsList" , stockSyms);
            getSymData(sym);
        } catch (JSONException e) {
            android.util.Log.e("TAG", "Exception in parsing json", e);
        }
    }


    public void getSymData(String symbol){

        String url = "http://api.nemov.org/api/v1/Market/Symbol";
        try {
            url  = url + "/" + URLEncoder.encode(symbol, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestQueue queue  = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TinyDB tinydb = new TinyDB(getBaseContext());
                        SymsData = tinydb.getListString("SymsDataList");
                        SymsData.add(SymsData.size(),s);
                        tinydb.putListString("SymsDataList" , SymsData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(FullList.this, "خطا", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(myReq);

    }
}

