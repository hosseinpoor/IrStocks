package ir.irse.stocks.DynamicConf;

import android.view.View;
import android.widget.AdapterView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

public class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

    private final DynamicListView mListView;

    public MyOnItemLongClickListener(final DynamicListView listView) {
        mListView = listView;
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (mListView != null) {
            mListView.startDragging(position - mListView.getHeaderViewsCount());
        }
        return true;
    }
}
