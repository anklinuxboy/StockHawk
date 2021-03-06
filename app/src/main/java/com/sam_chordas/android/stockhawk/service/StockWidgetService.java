package com.sam_chordas.android.stockhawk.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.StockWidgetItem;
import com.sam_chordas.android.stockhawk.widget.StockHawkAppWidgetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 2/5/17.
 */

public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int appWidgetId;
    private List<StockWidgetItem> widgetItemList = new ArrayList<>();

    ListRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public int getCount() {
        return widgetItemList.size();
    }

    @Override
    public void onCreate() {
        Cursor cursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                widgetItemList.add(new StockWidgetItem(cursor.getString(
                        cursor.getColumnIndex(QuoteColumns.SYMBOL)),
                        cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE))));
            }
            cursor.close();
        }
    }

    @Override
    public void onDataSetChanged() {}

    @Override
    public void onDestroy() {
        widgetItemList.clear();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.stock_symbol, widgetItemList.get(position).getStockSymbol());
        rv.setTextViewText(R.id.stock_price, widgetItemList.get(position).getStockPrice());

        Bundle extras = new Bundle();
        extras.putInt(StockHawkAppWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}