package ir.irse.stocks.DynamicConf;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import java.util.ArrayList;

import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.StockItem;
import ir.irse.stocks.other.TinyDB;

public class MyOnDismissCallback implements OnDismissCallback {

    private final ArrayAdapter<StockItem> mAdapter;
    private Context context;
    private Boolean flag = false;

    public MyOnDismissCallback(final ArrayAdapter<StockItem> adapter, Context con) {
        mAdapter = adapter;
        context = con;
    }


    @Override
    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
        TinyDB tinydb = new TinyDB(context);
        String currentSym = PersianDigitConverter.EnglishNumber(tinydb.getString("selectedSym"));
        ArrayList<String> Syms = tinydb.getListString("SymsList");
        ArrayList<String> SymsData = tinydb.getListString("SymsDataList");
        for (int position : reverseSortedPositions) {
            if(Syms.get(position).equals(currentSym)){
                flag = true;
            }
            mAdapter.remove(position);
            Syms.remove(position);
            SymsData.remove(position);
        }
        if(flag) {
            if (Syms.size() > 0)
                currentSym = Syms.get(0);
            else
                currentSym = "";
        }
        tinydb.putString("selectedSym" , PersianDigitConverter.PerisanNumber(currentSym));
        tinydb.putListString("SymsList" , Syms);
        tinydb.putListString("SymsDataList" , SymsData);

    }
}
