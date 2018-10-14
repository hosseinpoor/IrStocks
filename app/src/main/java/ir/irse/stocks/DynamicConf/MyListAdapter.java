package ir.irse.stocks.DynamicConf;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import ir.irse.stocks.R;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.StockItem;
import ir.irse.stocks.other.TinyDB;

public class MyListAdapter extends ArrayAdapter<StockItem> implements UndoAdapter {


    ArrayList<String> syms = new ArrayList<>();
    ArrayList<String> symsData = new ArrayList<>();
    private final Context mContext;

    public MyListAdapter(final Context context) throws JSONException {
        mContext = context;
        TinyDB tinydb = new TinyDB(mContext);
        syms = tinydb.getListString("SymsList");
        symsData = tinydb.getListString("SymsDataList");

        for(String i:syms) {
            int index =  syms.indexOf(i);
            JSONObject j = new JSONObject(symsData.get(index));
            add(new StockItem(PersianDigitConverter.PerisanNumber(i),PersianDigitConverter.PerisanNumber(j.getString("LVal30")),"",null,null,null,null));
        }
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getSymbol().hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_row_dynamiclistview, parent, false);
        }

        ((TextView) view.findViewById(R.id.op_title)).setText(getItem(position).getSymbol());
        ((TextView) view.findViewById(R.id.op_des)).setText(getItem(position).getMarket());
        ((TextView) view.findViewById(R.id.op_sub)).setText(getItem(position).getName());

        final ImageView remove = view.findViewById(R.id.remove_icon);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyDB tinydb = new TinyDB(mContext);
                Boolean flag = false;
                String currentSym = PersianDigitConverter.EnglishNumber(tinydb.getString("selectedSym"));
                if(syms.get(position).equals(currentSym)){
                    flag = true;
                }
                remove(position);
                syms.remove(position);
                symsData.remove(position);
                if(flag) {
                    if (syms.size() > 0)
                        currentSym = syms.get(0);
                    else
                        currentSym = "";
                }
                tinydb.putString("selectedSym" , PersianDigitConverter.PerisanNumber(currentSym));
                tinydb.putListString("SymsList" , syms);
                tinydb.putListString("SymsDataList" , symsData);
            }
        });
        return view;
    }

    @NonNull
    @Override
    public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull final View view) {

        return view.findViewById(R.id.undo_row_undobutton);
    }
}
