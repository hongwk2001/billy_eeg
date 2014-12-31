package com.tkprof.billy_eeg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BillyEEGDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_BILLY_EEG = "billy_eeg";
	 // public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_DATETIME = "datetime";
	  public static final String COLUMN_ATT_AVG = "att_avg";
	  public static final String COLUMN_ATT_TOT = "att_tot";
	  public static final String COLUMN_MED_AVG = "med_avg";
	  public static final String COLUMN_MED_TOT = "med_tot";
 
	  private static final String DATABASE_NAME = "billy_eeg.db";
	  private static final int DATABASE_VERSION = 2;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_BILLY_EEG + "("
			  + COLUMN_DATETIME  + " text primary key , " 
				 + COLUMN_ATT_AVG + " integer not null, "
				 + COLUMN_ATT_TOT + " integer not null, "
				 + COLUMN_MED_AVG + " integer not null, "
				 + COLUMN_MED_TOT + " integer not null  ) ";

	  public BillyEEGDBHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(BillyEEGDBHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLY_EEG);
	    onCreate(db);
	  }

	} 
	 