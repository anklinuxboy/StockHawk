package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0) {
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0) {
            for (int i = 0; i < resultsArray.length(); i++) {
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      builder.withValue(QuoteColumns.CREATED, jsonObject.getString("LastTradeTime"));
      builder.withValue(QuoteColumns.DATE, jsonObject.getString("LastTradeDate"));
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }

  public static boolean checkIfValidResponse(String response) {
      JSONObject jsonObject = null;
      try {
          jsonObject = new JSONObject(response);
          if (jsonObject != null && jsonObject.length() != 0 && !(((jsonObject
                  .getJSONObject("query")).getJSONObject("results")).getJSONObject("quote"))
                  .getString("Bid").equals("null")) {
              return true;
          }
      } catch (JSONException e) {
          Log.e(LOG_TAG, e.toString());
      }
      return false;
  }

  public static int convertTo24HourFormat(String time) {
    int convertedTime = -1;
    if (time.contains("pm")) {
      convertedTime = getCorrectTime(time);
    } else if (time.contains("am")) {
      convertedTime = getCorrectTime(time);
    }

    return convertedTime;
  }

  private static int getCorrectTime(String time) {
    int convertedTime = -1;
    if (time.length() == 7) {
      convertedTime = Integer.parseInt(time.substring(0,2));
      convertedTime = (convertedTime + 12) % 24;
    } else if (time.length() == 6) {
      convertedTime = Integer.parseInt(time.substring(0,1));
    }

    return convertedTime;
  }

  public static String getFormattedDate(String date) {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    String formattedDate;
    Date storedDate = null;
    if (date == null) {
      formattedDate = dateFormat.format(calendar.getTime());
    } else {
      try {
        storedDate = dateFormat.parse(date);
      } catch (ParseException e) {
        Log.e(LOG_TAG, e.toString());
      }
      formattedDate = dateFormat.format(storedDate);
    }
    return formattedDate;
  }
}
