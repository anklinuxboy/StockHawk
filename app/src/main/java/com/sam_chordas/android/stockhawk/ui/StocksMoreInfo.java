package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import static com.sam_chordas.android.stockhawk.data.Constants.stocksMoreInfoIntentKey;

public class StocksMoreInfo extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_more_info_activity);
        Intent intent = getIntent();
        String symbol = intent.getStringExtra(stocksMoreInfoIntentKey);

        TextView moreInfoTextView = (TextView) findViewById(R.id.more_info_text);
        if (symbol != null) {
            System.out.println("symbol " + symbol);
            moreInfoTextView.setText(symbol.toUpperCase());
        }

        GraphView graphView = (GraphView) findViewById(R.id.graph);

        Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.CREATED},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{symbol},
                null);

        if (c != null) {
            while (c.moveToNext()) {
                System.out.println(c.getString(c.getColumnIndex(QuoteColumns.SYMBOL)) + ", " +
                c.getString(c.getColumnIndex(QuoteColumns.CREATED)));
            }
        }


        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        graphView.addSeries(lineGraphSeries);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
