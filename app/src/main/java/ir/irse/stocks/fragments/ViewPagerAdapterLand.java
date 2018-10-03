package ir.irse.stocks.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import ir.irse.stocks.other.TinyDB;

public class ViewPagerAdapterLand extends FragmentPagerAdapter {

    private static int NUM_ITEMS=1;
    private static Context context = null;


    public ViewPagerAdapterLand(FragmentManager fm,Context cnt,int lenght) {
        super(fm);
        context = cnt;
        NUM_ITEMS = lenght;
    }

    @Override
    public Fragment getItem(int position) {

        TinyDB tinydb = new TinyDB(context);
        ArrayList<String> Syms = tinydb.getListString("SymsList");
        if(NUM_ITEMS>0) {
            return LandFrag.newInstance(Syms.get(position), position);
        }
        else {
            return null;
        }

    }


    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
