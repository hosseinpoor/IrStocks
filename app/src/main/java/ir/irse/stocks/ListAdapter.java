package ir.irse.stocks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import ir.irse.stocks.other.PersianDigitConverter;
import ir.irse.stocks.other.StockItem;
import ir.irse.stocks.other.TinyDB;

public class ListAdapter extends BaseAdapter
{
    private Context context;
    private List<StockItem> messageItems;

    public ListAdapter(Context context, List<StockItem> navDrawerItems) {
        this.context = context;
        this.messageItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        StockItem m = messageItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        final SharedPreferences pref = context.getSharedPreferences("pref" , Context.MODE_PRIVATE);
        final int mode = pref.getInt("mode" , 1);
        if(mode == 1){
            if(messageItems.get(position).getPercentage().isEmpty()){
                convertView = mInflater.inflate(
                        R.layout.cap_item, null
                );
            }
            else {
                if (messageItems.get(position).getPercentage().startsWith("-")) {
                    convertView = mInflater.inflate(
                            R.layout.neg_item, null
                    );
                } else {
                    convertView = mInflater.inflate(
                            R.layout.pos_item, null
                    );
                }
                TextView txtRate = (TextView) convertView.findViewById(R.id.rate);
                txtRate.setText(m.getPercentage().replace("-", ""));
            }

        }
        else if(mode == 2){
            if(messageItems.get(position).getPriceChange().isEmpty()){
                convertView = mInflater.inflate(
                        R.layout.cap_item, null
                );
            }
            else {
                if (messageItems.get(position).getPriceChange().startsWith("-")) {
                    convertView = mInflater.inflate(
                            R.layout.neg_item, null
                    );

                } else {
                    convertView = mInflater.inflate(
                            R.layout.pos_item, null
                    );
                }

                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(symbols);
                df.setGroupingSize(3);
                df.setMaximumFractionDigits(0);
                TextView txtRate = (TextView) convertView.findViewById(R.id.rate);
                txtRate.setText(PersianDigitConverter.PerisanNumber(df.format(Float.parseFloat(PersianDigitConverter.EnglishNumber(m.getPriceChange()).replace("-", "")))));
            }
        }
        else{

            if (messageItems.get(position).getPriceChange().startsWith("-"))
                convertView = mInflater.inflate(R.layout.redcap_item, null);
            else
            convertView = mInflater.inflate(R.layout.cap_item, null);

            TextView txtRate = (TextView) convertView.findViewById(R.id.rate);
            if(!m.getMarketCap().isEmpty())
                txtRate.setText( m.getMarketCap());
        }

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.modlayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor e = pref.edit();
                e.putInt("mode" , (mode+1)%3+1);
                e.apply();
                ((Activity)context).recreate();
            }
        });

        TextView txtBrand = (TextView) convertView.findViewById(R.id.brand);
        TextView txtValue = (TextView) convertView.findViewById(R.id.value);
        txtBrand.setText( m.getSymbol() );
        txtValue.setText( m.getPrice() );

        TinyDB tinyDB = new TinyDB(context);
        String temp = tinyDB.getString("selectedSym");

        if(messageItems.get(position).getSymbol().equals(temp))
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorSelected));

        return convertView;
    }

}
