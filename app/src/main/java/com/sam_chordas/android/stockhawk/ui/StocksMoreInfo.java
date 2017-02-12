package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sam_chordas.android.stockhawk.R;

import static com.sam_chordas.android.stockhawk.data.Constants.stocksMoreInfoIntentKey;

/**
 * Created by ankit on 2/11/17.
 */

public class StocksMoreInfo extends AppCompatActivity {

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

        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        graphView.addSeries(lineGraphSeries);
    }
}
