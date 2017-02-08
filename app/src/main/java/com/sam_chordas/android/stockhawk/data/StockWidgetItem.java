package com.sam_chordas.android.stockhawk.data;

/**
 * Created by ankit on 2/5/17.
 */

public class StockWidgetItem {
    private String stockSymbol;
    private String stockPrice;

    public StockWidgetItem(String symbol, String price) {
        this.stockSymbol = symbol;
        this.stockPrice = price;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getStockPrice() {
        return stockPrice;
    }
}
