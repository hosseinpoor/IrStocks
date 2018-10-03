package ir.irse.stocks.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.irse.stocks.fragments.MyFrag1;
import ir.irse.stocks.fragments.MyFrag2;
import ir.irse.stocks.fragments.MyFrag3;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyFrag3 t3 = new MyFrag3();
                return t3;

            case 1:
                MyFrag1 t1 = new MyFrag1();
                return t1;
            case 2:
                MyFrag2 t2 = new MyFrag2();
                return t2;
            default:
                return null;
        }

    }


    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
