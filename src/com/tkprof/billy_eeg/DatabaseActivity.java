package com.tkprof.billy_eeg;

import java.util.List;
import java.util.Random;
 

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View; 
import android.widget.ListView;
import android.widget.TextView; 
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class DatabaseActivity extends ListActivity {
  private BillyEEGDataSource datasource;
  
  private String date1 ="";
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.listview); 
 
    Bundle extras = getIntent().getExtras();
    if (extras != null) { 
     Toast.makeText(this, "Extra: " +extras.getString("datetime") , Toast.LENGTH_SHORT)
  		.show();
      date1 = extras.getString("datetime"); 
    }
    
    datasource = new BillyEEGDataSource(this);
    datasource.open();
    List<EEGRecord> values ;
    
    if (date1.length() > 1 ){
    	  values = datasource.getAllRowsByDate(date1)  ;
    }else {
        values = datasource.getDailySum()  ;
    }

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
     
    switch (view.getId()) {
    case R.id.add: 
      break;
    case R.id.delete:
      if (getListAdapter().getCount() > 0) {
        EEGRecord = (EEGRecord) getListAdapter().getItem(0);
        datasource.deleteRow(EEGRecord);
        adapter.remove(EEGRecord);
      }
      break;
    }
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
   
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
		 	super.onListItemClick(l, v, position, id);
		 	
	 TextView txt1 = (TextView) v.findViewById(android.R.id.text1);
	 Toast.makeText(this, "sel: " + txt1.getText().toString(), Toast.LENGTH_SHORT)
 		.show();
	 
	  Intent intent = new Intent(this, DatabaseActivity.class);

	  intent.putExtra("datetime",  txt1.getText().toString().substring(0, 10));
	    startActivity(intent);
	     
	}
  
} 

