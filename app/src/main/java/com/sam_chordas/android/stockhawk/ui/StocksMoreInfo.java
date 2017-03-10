package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;

import static com.sam_chordas.android.stockhawk.data.Constants.stocksMoreInfoIntentKey;

public class StocksMoreInfo extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String symbol;
    private LineChart lineChart;
    private LineData data;
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
        lineChart = (LineChart) findViewById(R.id.graph);

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
        ArrayList<Entry> yVals = new ArrayList<>();
        LineDataSet set;
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
                yVals.add(new Entry(i, bidPrice));
            }
            set = new LineDataSet(yVals, "Dataset");
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setCubicIntensity(0.1f);
            set.setLineWidth(0.8f);
            data = new LineData(set);
            lineChart.setData(data);
            lineChart.invalidate();0
            cursor.close();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
