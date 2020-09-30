package com.uni.julio.superplus.adapter;

import android.util.Log;
import android.view.View;

import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.RowPresenter;

public class CustomListRowPresenter extends ListRowPresenter {
    public CustomListRowPresenter(){
        super();
    }

    @Override
    public void setExpandedRowHeight(int rowHeight) {
        super.setExpandedRowHeight(20);
    }
    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        int numRows = ((CustomListRow) item).getNumRows();
        ((ListRowPresenter.ViewHolder) holder).getGridView().setNumRows(numRows);
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("----------------------", "-------------------");

                return false;
            }
        });
        super.onBindRowViewHolder(holder, item);
    }
}
