package ir.irse.wear;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ir.irse.wear.other.PersianDigitConverter;

import static android.content.Context.MODE_PRIVATE;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<String> mSym;
    private ArrayList<String> mPercentage;
    private ArrayList<String> mPricechange;
    private ArrayList<String> mmarketcap;
    private ArrayList<String> mVal;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContex;

    // data is passed into the constructor
    ListAdapter(Context context, ArrayList<String> sym , ArrayList<String> percentage , ArrayList<String> pricechange , ArrayList<String> marketcap ,  ArrayList<String> value) {
        this.mInflater = LayoutInflater.from(context);
        this.mSym = sym;
        this.mPercentage = percentage;
        this.mPricechange = pricechange;
        this.mmarketcap = marketcap;
        this.mVal = value;
        this.mContex = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String sym = mSym.get(position);
        final String per = mPercentage.get(position);
        final String pric = mPricechange.get(position);
        final String mc = mmarketcap.get(position);
        String val = mVal.get(position);
        holder.Symbol.setText(PersianDigitConverter.PerisanNumber(sym));
        holder.Value.setText(PersianDigitConverter.PerisanNumber(val));

        SharedPreferences preferences = mContex.getSharedPreferences("Pref", MODE_PRIVATE);
        int mode = preferences.getInt("mode" , 1);
        switch (mode){
            case 1:
                holder.MPP.setText(PersianDigitConverter.PerisanNumber(per));
                break;
            case 2 :
                holder.MPP.setText(PersianDigitConverter.PerisanNumber(pric));
                break;
            case 3 :
                holder.MPP.setText(PersianDigitConverter.PerisanNumber(mc));
                break;
        }

        if(per.startsWith("-"))
            holder.MPP.setBackground(mContex.getResources().getDrawable(R.drawable.red_rounded_back));
        else
            holder.MPP.setBackground(mContex.getResources().getDrawable(R.drawable.green_rounded_back));

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mSym.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Symbol , MPP , Value;

        ViewHolder(View itemView) {
            super(itemView);
            Symbol = itemView.findViewById(R.id.symtxt);
            MPP = itemView.findViewById(R.id.mpptxt);
            Value = itemView.findViewById(R.id.valtxt);
            itemView.setOnClickListener(this);
            MPP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences preferences = mContex.getSharedPreferences("Pref", MODE_PRIVATE);
                    int mode = preferences.getInt("mode" , 1);
                    SharedPreferences.Editor e = preferences.edit();
                    e.putInt("mode" , (mode%3)+1);
                    e.apply();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int pos) {
        return mSym.get(pos);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public String getSym(int index){
        return mSym.get(index);
    }
    public String getValue(int index){
        return mVal.get(index);
    }
    public String getPercentage(int index){
        return mPercentage.get(index);
    }
    public String getPriceChange(int index){
        return mPricechange.get(index);
    }
    public String getMarketCap(int index){
        return mmarketcap.get(index);
    }
    public ArrayList<String> getAllSyms(){
        return mSym;
    }

    public void setSym(int index , String sym){
        mSym.set(index,sym);
    }
    public void setPercentage(int index , String percentage){
        mPercentage.set(index,percentage);
    }
    public void setPriceChange(int index , String pricechange){
        mPricechange.set(index,pricechange);
    }
    public void setMarketCap(int index , String marketcap){
        mmarketcap.set(index,marketcap);
    }
    public void setValue(int index , String value){
        mVal.set(index,value);
    }

    public void add(String sym , String percentage , String pricechange , String marketcap ,  String value){
        mSym.add(sym);
        mPercentage.add(percentage);
        mPricechange.add(pricechange);
        mmarketcap.add(marketcap);
        mVal.add(value);
        notifyDataSetChanged();
    }

    public void remove(String sym){
        int position = mSym.indexOf(sym);
        mSym.remove(position);
        mVal.remove(position);
        mPricechange.remove(position);
        mPercentage.remove(position);
        mmarketcap.remove(position);
    }
}
