package ir.irse.wear;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.irse.wear.other.PersianDigitConverter;
import ir.irse.wear.other.TinyDB;

class SymsListAdapter extends RecyclerView.Adapter<SymsListAdapter.ViewHolder> {

    private List<String> mSym;
    private List<String> mName;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContex;

    // data is passed into the constructor
    SymsListAdapter(Context context, List<String> sym , List<String> name ) {
        this.mInflater = LayoutInflater.from(context);
        this.mSym = sym;
        this.mName = name;
        this.mContex = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.symslist_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String sym = mSym.get(position);
        String name = mName.get(position);
        holder.Symbol.setText(PersianDigitConverter.PerisanNumber(sym));
        holder.Name.setText(PersianDigitConverter.PerisanNumber(name));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mSym.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Symbol , Name ;
        TinyDB tinydb;
        ArrayList<String> Syms ;
        ArrayList<String> SymsData ;


        ViewHolder(View itemView) {
            super(itemView);
            Symbol = itemView.findViewById(R.id.symtxtadd);
            Name = itemView.findViewById(R.id.nametxtadd);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            tinydb = new TinyDB(mContex);
            Syms = tinydb.getListString("SymsList");
            SymsData = tinydb.getListString("SymsDataList");
            Toast.makeText(mContex , Symbol.getText().toString() + " اضافه شد" , Toast.LENGTH_SHORT).show();
            String symbol = PersianDigitConverter.EnglishNumber(Symbol.getText().toString());
            String s = getSymData(symbol);
            Syms.add(symbol);
            SymsData.add(s);
            tinydb.putListString("SymsList" , Syms);
            tinydb.putListString("SymsDataList" , SymsData);
            mSym.remove(symbol);
            mName.remove(PersianDigitConverter.EnglishNumber(Name.getText().toString()));
            notifyDataSetChanged();
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

    public String getSymData(final String symbol) {

        TinyDB tinydb = new TinyDB(mContex);
        ArrayList<String> Names = tinydb.getListString("restNames");
        ArrayList<String> Titles = tinydb.getListString("restTitles");
        int index = Names.indexOf(symbol);

        String s = "{\n" +
                "    \"InsCode\": null,\n" +
                "    \"CIsin\": null,\n" +
                "    \"LVal18AFC\": \"" +symbol+ "\",\n" +
                "    \"LVal30\": \""+Titles.get(index)+"\",\n" +
                "    \"PriceFirst\": null,\n" +
                "    \"PDrCotVal\": null,\n" +
                "    \"PClosing\": null,\n" +
                "    \"ZTotTran\": null,\n" +
                "    \"QTotTran5J\": null,\n" +
                "    \"QTotCap\": null,\n" +
                "    \"PriceMin\": null,\n" +
                "    \"PriceMax\": null,\n" +
                "    \"PriceYesterday\": null,\n" +
                "    \"BaseVol\": null,\n" +
                "    \"EPS\": null,\n" +
                "    \"YVal\": null\n" +
                "}";

        return s;

    }

}
