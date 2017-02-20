package com.sam_chordas.android.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sam_chordas on 10/5/15.
 */
@Database(version = QuoteDatabase.VERSION)
public class QuoteDatabase {
  private QuoteDatabase(){}

  public static final int VERSION = 9;

  @Table(QuoteColumns.class) public static final String QUOTES = "quotes";

  @OnCreate public static void onCreate(Context context, SQLiteDatabase db) {
  }

  @OnUpgrade public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                          int newVersion) {
  }
}
