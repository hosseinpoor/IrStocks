package ir.irse.wear;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class MainActivity extends WearableActivity {

    WearableRecyclerView mWearableRecyclerView;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWearableRecyclerView = (WearableRecyclerView) findViewById(R.id.recycler_launcher_view);

        ArrayList<String> Names = new ArrayList<>();
        Names.add("ذوب");
        Names.add("کگل");
        Names.add("سکه");
        Names.add("فبیرا");
        Names.add("وتجارت");
        Names.add("ذوب");
        Names.add("کگل");
        Names.add("سکه");
        Names.add("فبیرا");
        Names.add("وتجارت");
        Names.add("ذوب");
        Names.add("کگل");
        Names.add("سکه");
        Names.add("فبیرا");
        Names.add("وتجارت");

        adapter = new ListAdapter(this, Names);
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

    }
}



