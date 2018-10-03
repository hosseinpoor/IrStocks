package ir.irse.stocks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import ir.irse.stocks.DynamicConf.*;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.StockItem;
import ir.irse.stocks.other.TinyDB;


public class FullList extends AppCompatActivity {


    private static final int INITIAL_DELAY_MILLIS = 300;
    TextView per,price,market,add,done;
    int active =1;
    static ArrayList<String> Syms = new ArrayList<>();
    static ArrayList<String> SymsData = new ArrayList<>();
    static ArrayList<String> Names = new ArrayList<>();
    static ArrayList<String> Titles = new ArrayList<>();
    DynamicListView listView ;
    ArrayAdapter<StockItem> adapter;
    SimpleSwipeUndoAdapter simpleSwipeUndoAdapter;
    AlphaInAnimationAdapter animAdapter;
    AlertDialog dialog;
    AutoCompleteTextView autocomplete;
    SharedPreferences pref;
    String alert_OK = "افزودن";
    String alert_CANCEL = "بیخیال";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_list);
        final TinyDB tinydb = new TinyDB(FullList.this);
        Syms = tinydb.getListString("SymsList");
        SymsData = tinydb.getListString("SymsDataList");
        Names = tinydb.getListString("restNames");
        Titles = tinydb.getListString("restTitles");
        pref = getSharedPreferences("pref" , MODE_PRIVATE);
        active = pref.getInt("mode" , 1);

        per = (TextView) findViewById(R.id.btn_per) ;
        price = (TextView) findViewById(R.id.btn_price) ;
        market = (TextView) findViewById(R.id.btn_market) ;
        add = (TextView) findViewById(R.id.txt_add) ;
        done = (TextView) findViewById(R.id.txt_done);
        listView = (DynamicListView) findViewById(R.id.dynamiclistview);

        btnColor();
        setupAdapter();

        per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active=1;
                btnColor();
                SharedPreferences.Editor e = pref.edit();
                e.putInt("mode" , active);
                e.apply();
            }
        });

        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active=2;
                btnColor();
                SharedPreferences.Editor e = pref.edit();
                e.putInt("mode" , active);
                e.apply();
            }
        });

        market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active=3;
                btnColor();
                SharedPreferences.Editor e = pref.edit();
                e.putInt("mode" , active);
                e.apply();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                @SuppressLint("RestrictedApi")
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(new ContextThemeWrapper(FullList.this , R.style.AppTheme_dialog));
                myBuilder.setView(R.layout.addalertlayout);
                dialog = myBuilder.create();
                dialog.show();

                Syms = tinydb.getListString("SymsList");
                SymsData = tinydb.getListString("SymsDataList");

                autocomplete = (AutoCompleteTextView)
                dialog.findViewById(R.id.search_txt);
                final TextView b = (TextView) dialog.findViewById(R.id.add_btn);
                final List<String> list = new ArrayList<>();
                list.addAll(Names);
                list.addAll(Titles);
                android.widget.ArrayAdapter<String> searchadapter = new android.widget.ArrayAdapter<String> (FullList.this,R.layout.autocomplete_layout, R.id.autoCompleteItem,list );
                autocomplete.setThreshold(1);
                autocomplete.setAdapter(searchadapter);
                autocomplete.setValidator(new Validator());
                autocomplete.addTextChangedListener(new TextWatcher(){

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(autocomplete.getValidator().isValid(s))
                            b.setText(alert_OK);
                        else
                            b.setText(alert_CANCEL);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                });

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(b.getText()==alert_OK)
                        {
                            Syms = tinydb.getListString("SymsList");
                            SymsData = tinydb.getListString("SymsDataList");
                            int i = Titles.indexOf(autocomplete.getText().toString());
                            if(i<0) i = Names.indexOf(autocomplete.getText().toString());
                            StockItem newItem = new StockItem(PersianDigitConverter.PerisanNumber(Names.get(i)),PersianDigitConverter.PerisanNumber(Titles.get(i)),"",null,null,null,null);
                            Syms.add(adapter.getCount(), Names.get(i));
                            getSymData(Names.get(i));
                            TinyDB tinydb = new TinyDB(FullList.this);
                            tinydb.putListString("SymsList" , Syms);
                            adapter.add(adapter.getCount(),newItem);
                            adapter.notifyDataSetChanged();
                            String sel = tinydb.getString("selectedSym");
                            if(sel.equals("")) {
                                tinydb.putString("selectedSym", PersianDigitConverter.PerisanNumber(Syms.get(0)));
                            }
                        }

                        dialog.dismiss();

                    }
                });

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FullList.this , MainActivity.class);
                startActivity(i);
//                overridePendingTransition(R.anim.slide_down, 0);
                finish();
            }
        });

        ImageView ebbroker = (ImageView) findViewById(R.id.img_ebbroker);
        ebbroker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ebbroker.com/"));
                startActivity(browserIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(FullList.this , MainActivity.class);
        startActivity(i);
//        overridePendingTransition(R.anim.slide_down, 0);
        finish();
    }

    public void btnColor(){
        switch (active){
            case 1:
                per.setTextColor(Color.BLACK);
                per.setBackground(getResources().getDrawable(R.drawable.btn_l_pressed));

                price.setTextColor(getResources().getColor(R.color.colorBlue));
                price.setBackground(getResources().getDrawable(R.drawable.btn_m_normal));

                market.setTextColor(getResources().getColor(R.color.colorBlue));
                market.setBackground(getResources().getDrawable(R.drawable.btn_r_normal));
                break;
            case 2:
                per.setTextColor(getResources().getColor(R.color.colorBlue));
                per.setBackground(getResources().getDrawable(R.drawable.btn_l_normal));

                price.setTextColor(Color.BLACK);
                price.setBackground(getResources().getDrawable(R.drawable.btn_m_pressed));

                market.setTextColor(getResources().getColor(R.color.colorBlue));
                market.setBackground(getResources().getDrawable(R.drawable.btn_r_normal));
                break;
            case 3:
                per.setTextColor(getResources().getColor(R.color.colorBlue));
                per.setBackground(getResources().getDrawable(R.drawable.btn_l_normal));

                price.setTextColor(getResources().getColor(R.color.colorBlue));
                price.setBackground(getResources().getDrawable(R.drawable.btn_m_normal));

                market.setTextColor(Color.BLACK);
                market.setBackground(getResources().getDrawable(R.drawable.btn_r_pressed));
                break;
            default:
                break;

        }
    }

    public void setupAdapter(){
        /* Setup the adapter */
        try {
            adapter = new MyListAdapter(FullList.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        simpleSwipeUndoAdapter = new SimpleSwipeUndoAdapter(adapter, FullList.this, new MyOnDismissCallback(adapter, FullList.this));
        animAdapter = new AlphaInAnimationAdapter(simpleSwipeUndoAdapter);
        animAdapter.setAbsListView(listView);
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(animAdapter);

        /* Enable drag and drop functionality */
        listView.enableDragAndDrop();
        listView.setDraggableManager(new TouchViewDraggableManager(R.id.list_row_draganddrop_touchview));
        listView.setOnItemMovedListener(new MyOnItemMovedListener(adapter , FullList.this));
        listView.setOnItemLongClickListener(new MyOnItemLongClickListener(listView));

        /* Enable swipe to dismiss */
        listView.enableSimpleSwipeUndo();
    }

    class Validator implements AutoCompleteTextView.Validator {

        @Override
        public boolean isValid(CharSequence text) {

            for (String s : SymsData){
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String name = jsonObject.getString("LVal30");
                    if (text.toString().equals(name)) {
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for(String s : Syms)
                if (text.toString().equals(s)) {
                    return false;
                }

            Log.e("Test", "Checking if valid: "+ text);
            List<String> list = new ArrayList<>();
            list.addAll(Names);
            list.addAll(Titles);
            for (int i = 0; i <= list.toArray().length - 1; i++) {

                if (text.toString().equals(list.get(i))) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public CharSequence fixText(CharSequence invalidText) {
            Log.e("Test", "Returning fixed text");
            /* I'm just returning an empty string here, so the field will be blanked,
             * but you could put any kind of action here, like popping up a dialog?
             *
             * Whatever value you return here must be in the list of valid words.
             */
            return "";
        }
    }

    public void getSymData(final String symbol){
        String url = "http://api.nemov.org/api/v1/Market/Symbol";
        try {
            url  = url + "/" + URLEncoder.encode(symbol, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        TinyDB tinydb = new TinyDB(FullList.this);
        int index = Names.indexOf(symbol);
        String s = "{\n" +
                "    \"InsCode\": null,\n" +
                "    \"CIsin\": null,\n" +
                "    \"LVal18AFC\": \"" +symbol+ "\",\n" +
                "    \"LVal30\": \""+Titles.get(index)+"\",\n" +
                "    \"PriceFirst\": null,\n" +
                "    \"PDrCotVal\": null,\n" +
                "    \"PClosing\": null,\n" +
                "    \"ZTotTran\": null,\n" +
                "    \"QTotTran5J\": null,\n" +
                "    \"QTotCap\": null,\n" +
                "    \"PriceMin\": null,\n" +
                "    \"PriceMax\": null,\n" +
                "    \"PriceYesterday\": null,\n" +
                "    \"BaseVol\": null,\n" +
                "    \"EPS\": null,\n" +
                "    \"YVal\": null\n" +
                "}";
        SymsData = tinydb.getListString("SymsDataList");
        SymsData.add(s);
        tinydb.putListString("SymsDataList" , SymsData);

        RequestQueue queue  = Volley.newRequestQueue(this);
        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TinyDB tinydb = new TinyDB(FullList.this);
                        SymsData = tinydb.getListString("SymsDataList");
                        SymsData.set(SymsData.size()-1 , s);
                        tinydb.putListString("SymsDataList" , SymsData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(myReq);

    }



}


