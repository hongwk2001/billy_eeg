package com.tkprof.billy_eeg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

public class BillyEEGDataSource {

  // Database fields
  private SQLiteDatabase database;
  private BillyEEGDBHelper dbHelper;
  private String[] allColumns = { 
		  BillyEEGDBHelper.COLUMN_DATETIME,
		  BillyEEGDBHelper.COLUMN_ATT_AVG ,
		  BillyEEGDBHelper.COLUMN_ATT_TOT ,
		  BillyEEGDBHelper.COLUMN_MED_AVG ,
		  BillyEEGDBHelper.COLUMN_MED_TOT };

  public BillyEEGDataSource(Context context) {
    dbHelper = new BillyEEGDBHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public EEGRecord createEEG(int att_avg, int att_tot, int med_avg, int med_tot) {
    ContentValues values = new ContentValues();
    
    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    String date = df.format(Calendar.getInstance().getTime());
    
    Log.i("date", date);
    
    values.put(BillyEEGDBHelper.COLUMN_DATETIME, date);
    values.put(BillyEEGDBHelper.COLUMN_ATT_AVG, att_avg);
    values.put(BillyEEGDBHelper.COLUMN_ATT_TOT, att_tot);
    values.put(BillyEEGDBHelper.COLUMN_MED_AVG, med_avg);
    values.put(BillyEEGDBHelper.COLUMN_MED_TOT, med_tot);
    long insertId = database.insertWithOnConflict (BillyEEGDBHelper.TABLE_BILLY_EEG,
    		null, values, SQLiteDatabase.CONFLICT_REPLACE);
    Log.i("insertid", ""+insertId) ;
    Cursor cursor = database.query(BillyEEGDBHelper.TABLE_BILLY_EEG,
        allColumns,    "rowid = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    EEGRecord newrow = cursorToRow(cursor);
    cursor.close();
    return newrow;
  }

  public void deleteRow(EEGRecord row) {
    String dt = row.getDateTime();
    Log.i("delete", "Comment deleted with id: " + dt);
    database.delete(BillyEEGDBHelper.TABLE_BILLY_EEG, BillyEEGDBHelper.COLUMN_DATETIME
        + " = '" + dt +"'", null);
  }

  public List<EEGRecord> getDailySum() {
	    List<EEGRecord> rows = new ArrayList<EEGRecord>();

	    Cursor cursor = database.rawQuery (
              "select substr(datetime, 1,10) dt," +
              " avg(att_avg), sum(att_tot),  " +
              " avg(med_avg), sum(med_tot)   " +
	           " from billy_eeg  " +
	           "group by substr(datetime, 1,10) " +
	           "order by dt desc" 
	    		, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	EEGRecord eachrow = cursorToRow(cursor);
	    	rows.add(eachrow);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return rows;
	  }
  public List<EEGRecord> getAllRowsByDate( String date1) {
	    List<EEGRecord> rows = new ArrayList<EEGRecord>();
	    
	    String [] dates = {date1};

	    Cursor cursor = database.rawQuery (
	              "select substr(datetime, 12,5) dt," +
	              "  att_avg , att_tot ,  " +
	              " med_avg ,  med_tot   " +
		           " from billy_eeg  " +
	               "where  datetime like ?||'%' " +
		           "order by dt desc" 
		    		, dates);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	EEGRecord eachrow = cursorToRow(cursor);
	    	rows.add(eachrow);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return rows;
	  }
  public List<EEGRecord> getAllRows() {
    List<EEGRecord> rows = new ArrayList<EEGRecord>();

    Cursor cursor = database.query(BillyEEGDBHelper.TABLE_BILLY_EEG,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
    	EEGRecord eachrow = cursorToRow(cursor);
    	rows.add(eachrow);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return rows;
  }
  
  public List<EEGRecord> getAllRowsDesc() {
	    List<EEGRecord> rows = new ArrayList<EEGRecord>();

	    Cursor cursor = database.query(
	    		BillyEEGDBHelper.TABLE_BILLY_EEG,
	        allColumns, null, null,  null, null, "datetime desc");

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	EEGRecord eachrow = cursorToRow(cursor);
	    	rows.add(eachrow);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return rows;
	}

  private EEGRecord cursorToRow(Cursor cursor) {
	  EEGRecord newrow = new EEGRecord();
	  newrow.setDateTime(cursor.getString(0));   
	  newrow.setAttAvg(cursor.getInt(1));
	  newrow.setAttTot(cursor.getInt(2));
	  newrow.setMedAvg(cursor.getInt(3));
	  newrow.setMedTot(cursor.getInt(4)); 
    return newrow;
  }
} 