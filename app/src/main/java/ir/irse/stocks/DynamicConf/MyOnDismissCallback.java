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
        ArrayList<String> chartD = tinydb.getListString("SymsDchartList");
        ArrayList<String> chartW = tinydb.getListString("SymsWchartList");
        ArrayList<String> chartM = tinydb.getListString("SymsMchartList");
        ArrayList<String> chart3M = tinydb.getListString("Syms3MchartList");
        ArrayList<String> chart6M = tinydb.getListString("Syms6MchartList");
        ArrayList<String> chartY = tinydb.getListString("SymsYchartList");
        ArrayList<String> chart2Y = tinydb.getListString("Syms2YchartList");
        ArrayList<String> chart5Y = tinydb.getListString("Syms5YchartList");
        ArrayList<String> chart10Y = tinydb.getListString("Syms10YchartList");

        for (int position : reverseSortedPositions) {
            if(Syms.get(position).equals(currentSym)){
                flag = true;
            }
            mAdapter.remove(position);
            Syms.remove(position);
            SymsData.remove(position);
            chartD.remove(position);
            chartW.remove(position);
            chartM.remove(position);
            chart3M.remove(position);
            chart6M.remove(position);
            chartY.remove(position);
            chart2Y.remove(position);
            chart5Y.remove(position);
            chart10Y.remove(position);
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
        tinydb.putListString("SymsDchartList" , chartD);
        tinydb.putListString("SymsWchartList" , chartW);
        tinydb.putListString("SymsMchartList" , chartM);
        tinydb.putListString("Syms3MchartList" , chart3M);
        tinydb.putListString("Syms6MchartList" , chart6M);
        tinydb.putListString("SymsYchartList" , chartY);
        tinydb.putListString("Syms2YchartList" , chart2Y);
        tinydb.putListString("Syms5YchartList" , chart5Y);
        tinydb.putListString("Syms10YchartList" , chart10Y);

    }
}
