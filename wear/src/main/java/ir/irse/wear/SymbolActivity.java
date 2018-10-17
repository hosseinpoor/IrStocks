package ir.irse.wear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ir.irse.wear.other.PersianDigitConverter;
import ir.irse.wear.other.TinyDB;

public class SymbolActivity extends WearableActivity {

    TextView sym , remove;
    ArrayList<String> stockSyms = new ArrayList<String>();
    ArrayList<String> stockSymsData = new ArrayList<String>();
    TinyDB tinydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol);
        sym = findViewById(R.id.symbol);
        remove = findViewById(R.id.removebtn);
        tinydb = new TinyDB(SymbolActivity.this);
        stockSyms = tinydb.getListString("SymsList");
        stockSymsData = tinydb.getListString("SymsDataList");
        Bundle b = getIntent().getExtras();
        final int index = b.getInt("position");
        final String symbol = stockSyms.get(index);
        sym.setText(PersianDigitConverter.PerisanNumber(symbol));

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockSyms.remove(index);
                stockSymsData.remove(index);
                tinydb.putListString("SymsList" , stockSyms);
                tinydb.putListString("SymsDataList" , stockSymsData);
                Toast.makeText(SymbolActivity.this , PersianDigitConverter.PerisanNumber(symbol) + " حذف شد" , Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
