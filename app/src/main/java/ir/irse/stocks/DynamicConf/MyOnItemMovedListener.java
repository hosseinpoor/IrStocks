package ir.irse.stocks.DynamicConf;


import android.content.Context;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import java.util.ArrayList;

import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.StockItem;
import ir.irse.stocks.other.TinyDB;

public class MyOnItemMovedListener implements OnItemMovedListener {

    private final ArrayAdapter<StockItem> mAdapter;
    private Context contex;


    public MyOnItemMovedListener(final ArrayAdapter<StockItem> adapter , Context con) {
        mAdapter = adapter;
        contex = con;
    }

    @Override
    public void onItemMoved(final int originalPosition, final int newPosition) {

        ArrayList<String> newSyms = new ArrayList<>();
        ArrayList<String> newSymsData = new ArrayList<>();
        TinyDB tinydb = new TinyDB(contex);
        ArrayList<String> oldSyms = tinydb.getListString("SymsList");
        ArrayList<String> oldSymsData = tinydb.getListString("SymsDataList");

        int index;
        for(int i=0;i<mAdapter.getCount();i++){
            index = oldSyms.indexOf(PersianDigitConverter.EnglishNumber(mAdapter.getItem(i).getSymbol()));
            newSyms.add(oldSyms.get(index));
            newSymsData.add(oldSymsData.get(index));
        }

        tinydb.putListString("SymsList" , newSyms);
        tinydb.putListString("SymsDataList" , newSymsData);

    }
}
