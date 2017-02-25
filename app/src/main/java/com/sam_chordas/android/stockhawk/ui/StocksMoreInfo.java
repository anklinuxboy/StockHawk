package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.sam_chordas.android.stockhawk.data.Constants.stocksMoreInfoIntentKey;

public class StocksMoreInfo extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private String symbol;
    private RelativeLayout emptyLayout;
    private GraphView graphView;
    private LineGraphSeries<DataPoint> lineGraphSeries;

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
        emptyLayout = (RelativeLayout) findViewById(R.id.empty_info);
        graphView = (GraphView) findViewById(R.id.graph);

        if (symbol != null) {
            moreInfoTextView.setText(symbol.toUpperCase());
        }

        lineGraphSeries = new LineGraphSeries<>();

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
        String todaysDate = Utils.getFormattedDate(null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Float bidPrice = Float.parseFloat(cursor.getString(1));
                String time = cursor.getString(2);
                String storedDate = cursor.getString(3);
                String formattedDate = Utils.getFormattedDate(storedDate);
                if (formattedDate.equals(todaysDate)) {
                    int formattedTime = Utils.convertTo24HourFormat(time);
                    lineGraphSeries.appendData(new DataPoint(formattedTime, bidPrice), false, 100000);
                } else {
                    graphView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                    cursor.close();
                    break;
                }
            }
        }
        graphView.addSeries(lineGraphSeries);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
