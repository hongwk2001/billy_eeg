package com.tkprof.billy_eeg;

import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View; 
import android.widget.TextView; 
import android.widget.ArrayAdapter;

public class TestDatabaseActivity extends ListActivity {
  private BillyEEGDataSource datasource;

  TextView  tv_att_avg, tv_att_tot, tv_med_avg, tv_med_tot; 
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.listview);
    
    tv_att_avg = (TextView) findViewById(R.id.tv_att_avg);
    tv_att_tot = (TextView) findViewById(R.id.tv_att_tot);
    tv_med_avg = (TextView) findViewById(R.id.tv_med_avg);
    tv_med_tot = (TextView) findViewById(R.id.tv_med_tot);
 
    Bundle extras = getIntent().getExtras();
    if (extras != null) { 
	  	tv_att_avg.setText( extras.getString("att_avg") );
	  	tv_att_tot.setText( extras.getString("att_tot") );
	  	tv_med_avg.setText( extras.getString("med_avg") );
	  	tv_med_tot.setText( extras.getString("med_tot") );
    }
    
    datasource = new BillyEEGDataSource(this);
    datasource.open();

    List<EEGRecord> values = datasource.getAllRowsDesc ();

    // use the SimpleCursorAdapter to show the
    // elements in a ListView
    ArrayAdapter<EEGRecord> adapter = new ArrayAdapter<EEGRecord>(this,
        android.R.layout.simple_list_item_1, values);
    setListAdapter(adapter);
  }

  // Will be called via the onClick attribute
  // of the buttons in main.xml
  public void onClick(View view) {
    @SuppressWarnings("unchecked")
    ArrayAdapter<EEGRecord> adapter = (ArrayAdapter<EEGRecord>) getListAdapter();
    EEGRecord EEGRecord = null;
     
    /*switch (view.getId()) {
    case R.id.add:
 
        int att_avg = Integer.parseInt(tv_att_tot.getText().toString());
		int att_tot = Integer.parseInt(tv_att_avg.getText().toString());
		 int med_avg = Integer.parseInt(tv_med_avg.getText().toString());
		 int med_tot = Integer.parseInt(tv_med_tot.getText().toString());

      // save the new EEGRecord to the database
      EEGRecord = datasource.createEEG( att_avg, att_tot, med_avg, med_tot );
      adapter.add(EEGRecord);
      break;
    case R.id.delete:
      if (getListAdapter().getCount() > 0) {
        EEGRecord = (EEGRecord) getListAdapter().getItem(0);
        datasource.deleteRow(EEGRecord);
        adapter.remove(EEGRecord);
      }
      break;
    }*/
    adapter.notifyDataSetChanged();
  }

  @Override
  protected void onResume() {
    datasource.open();
    super.onResume();
  }

  @Override
  protected void onPause() {
    datasource.close();
    super.onPause();
  } 
} 

