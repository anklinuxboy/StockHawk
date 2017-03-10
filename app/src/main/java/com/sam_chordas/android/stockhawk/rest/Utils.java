package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    double round = 0;
    // Sometimes yahoo API returns invalid data
    try {
      round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    } catch (NumberFormatException e) {
      round = 0.0;
    }
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
          if (jsonObject != null && jsonObject.length() != 0) {
            JSONObject results = jsonObject.getJSONObject("query").getJSONObject("results");
            try {
              JSONArray quotes = results.getJSONArray("quote");
              return true;
            } catch (JSONException e) {
              Log.e(LOG_TAG, e.toString());
              JSONObject quote = results.getJSONObject("quote");
              if (!quote.getString("Bid").equals("null")) {
                return true;
              }
            }
          }
      } catch (JSONException e) {
          Log.e(LOG_TAG, e.toString());
      }
      return false;
  }

  public static int convertTo24HourFormat(String time) {
    int convertedTime = -1;
    if (time.contains("pm")) {
      if (time.length() == 6) {
        convertedTime = Integer.parseInt(time.substring(0,1)) + 12;
      } else if (time.length() == 7) {
        if (time.substring(0,2).equals("12")) {
          convertedTime = 12;
        } else {
          convertedTime = (Integer.parseInt(time.substring(0,2)) + 12) % 24;
        }
      }
    } else if (time.contains("am")) {
      if (time.length() == 6) {
        convertedTime = Integer.parseInt(time.substring(0,1)) + 12;
      } else if (time.length() == 7) {
        convertedTime = (Integer.parseInt(time.substring(0,2)) + 12) % 24;
      }
    }

    return convertedTime;
  }

  private static String getParsedString(String str) {
    return str.replaceAll("[^0-9]+", " ");
  }

  public static int getXValue(String time, String storedDate) {
    String parsedTime = getParsedString(time);
    String parsedDate = getParsedString(storedDate);
    int xValue = getIntFromString(parsedDate) + getIntFromString(parsedTime);
    return xValue;
  }

  private static int getIntFromString(String str) {
    String[] strInts = str.split(" ");
    int result = 0;
    for (String s : strInts) {
      result += Integer.parseInt(s);
    }
    return result;
  }
}
