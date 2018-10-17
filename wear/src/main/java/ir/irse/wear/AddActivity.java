package ir.irse.wear;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.irse.wear.other.TinyDB;

public class AddActivity extends WearableActivity {

    ArrayList<String> Syms = new ArrayList<>();
    ArrayList<String> SymsData = new ArrayList<>();
    ArrayList<String> Names = new ArrayList<>();
    ArrayList<String> Titles = new ArrayList<>();
    TinyDB tinydb;
    SymsListAdapter adapter;
    WearableRecyclerView mWearableRecyclerView;
    TextView mc , pric , per;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        preferences = getSharedPreferences("Pref", MODE_PRIVATE);

        per = findViewById(R.id.permode);
        pric = findViewById(R.id.picmode);
        mc = findViewById(R.id.mcmode);

        tinydb = new TinyDB(AddActivity.this);
        Syms = tinydb.getListString("SymsList");
        SymsData = tinydb.getListString("SymsDataList");
        Names = tinydb.getListString("restNames");
        Titles = tinydb.getListString("restTitles");

        ArrayList<String> subNames = Names;
        subNames.removeAll(Syms);
        ArrayList<String> subTitles = Titles;
        ArrayList<String> tempTitles = new ArrayList<>();
        JSONObject j = null;
        for(String s : SymsData) {
            try {
                j = new JSONObject(s);
                tempTitles.add(j.getString("LVal30"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        subTitles.removeAll(tempTitles);

        mWearableRecyclerView = (WearableRecyclerView) findViewById(R.id.symslist);
        adapter = new SymsListAdapter(this, subNames , subTitles);
        mWearableRecyclerView.setAdapter(adapter);

        // Enables Always-on
        setAmbientEnabled();

        // To align the edge children (first and last) with the center of the screen
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        //...

        mWearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this));

        mWearableRecyclerView.setCircularScrollingGestureEnabled(true);
        mWearableRecyclerView.setBezelFraction(0.5f);
        mWearableRecyclerView.setScrollDegreesPerScreen(90);

        setmode();
        per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = preferences.edit();
                e.putInt("mode" , 1);
                e.apply();
                setmode();
            }
        });
        pric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = preferences.edit();
                e.putInt("mode" , 2);
                e.apply();
                setmode();
            }
        });
        mc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = preferences.edit();
                e.putInt("mode" , 3);
                e.apply();
                setmode();
            }
        });
    }

    public void setmode(){
        per.setTextColor(getResources().getColor(R.color.colorGray));
        pric.setTextColor(getResources().getColor(R.color.colorGray));
        mc.setTextColor(getResources().getColor(R.color.colorGray));
        per.setTypeface(null, Typeface.NORMAL);
        pric.setTypeface(null, Typeface.NORMAL);
        mc.setTypeface(null, Typeface.NORMAL);

        int mode = preferences.getInt("mode" , 1);
        switch (mode){
            case 1 :
                per.setTextColor(Color.WHITE);
                per.setTypeface(null, Typeface.BOLD);
                break;
            case 2 :
                pric.setTextColor(Color.WHITE);
                pric.setTypeface(null, Typeface.BOLD);
                break;
            case 3:
                mc.setTextColor(Color.WHITE);
                mc.setTypeface(null, Typeface.BOLD);
                break;
        }
    }

}
