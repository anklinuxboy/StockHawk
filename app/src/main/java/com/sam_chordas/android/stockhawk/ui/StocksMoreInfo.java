package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;

import static com.sam_chordas.android.stockhawk.data.Constants.stocksMoreInfoIntentKey;

public class StocksMoreInfo extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String symbol;
    private RelativeLayout emptyLayout;
    private BarChart barChart;
    private BarData data;
    private ArrayList<String> xAxisValues = new ArrayList<>();

    static final String[] PROJECTIONS = new String[] {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.CREATED,
            QuoteColumns.DATE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_more_info_activity);
        Intent intent = getIntent();
        symbol = intent.getStringExtra(stocksMoreInfoIntentKey);

        TextView moreInfoTextView = (TextView) findViewById(R.id.more_info_text);
        barChart = (BarChart) findViewById(R.id.graph);
        data = new BarData();

        if (symbol != null) {
            moreInfoTextView.setText(symbol.toUpperCase());
        }

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                PROJECTIONS, QuoteColumns.SYMBOL + " = ?", new String[]{symbol}, null);
    }

    // Check if cursor
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int i = -1;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Float bidPrice = Float.parseFloat(cursor.getString(1));
                String time = cursor.getString(2);
                String storedDate = cursor.getString(3);
                int xValue = Utils.getXValue(time, storedDate);
                if (!xAxisValues.contains(storedDate)) {
                    xAxisValues.add(storedDate);
                    i++;
                }
                Entry entry = new Entry(xValue, bidPrice);
                data.addEntry(entry, i);
            }

            barChart.setData(data);
            barChart.invalidate();
            cursor.close();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
