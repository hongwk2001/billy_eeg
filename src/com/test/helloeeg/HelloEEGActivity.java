package com.test.helloeeg;

import java.util.Locale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.neurosky.thinkgear.*;

public class HelloEEGActivity extends Activity {
	BluetoothAdapter bluetoothAdapter;
	
	TextView tv_conn ;
	TextView tv_attention   ;
	TextView tv_meditation  ;
	TextView tv_blink       ; 
	TextView tv_signal ;
	
	TextView tv_att_tot    ;
	TextView tv_att_avg   ;
	TextView tv_med_avg   ;
	TextView tv_med_tot   ;
	CheckBox chk_read ;
	
	TextView tv_raw_count   ;
	
	 
	TGDevice tgDevice;
	final boolean rawEnabled = false;
	
	static final int CONST_ATTENTION = 1;
	static final int CONST_MEDITATION = 2;
	static final int CONST_SIGNAL = 3;
	
	
	int att_avg=0, med_avg=0, att_cnt=0, med_cnt=0, att_tot=0, med_tot=0;
	int att_val =0, med_val = 0, poor_signal = 1 ; 
	
	int len_limit = 50;
	int read_interval = 5, read_cnt = 0;
	
    PowerManager.WakeLock wl ;
    TextToSpeech ttobj;
    
    GraphViewSeries att_Series, med_Series, sig_Series;
    int i_att_no=0, i_med_no= 0, i_sig_no =0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv_conn = (TextView)findViewById(R.id.tv_conn);
        
        tv_attention   =(TextView) findViewById(R.id.tv_attention  );
        tv_meditation  =(TextView) findViewById(R.id.tv_meditation );
        tv_blink       =(TextView) findViewById(R.id.tv_blink      ); 
        tv_signal =(TextView) findViewById(R.id.tv_signal);
        
		 tv_att_tot   = (TextView)findViewById(R.id.tv_att_tot);
		 tv_att_avg   = (TextView)findViewById(R.id.tv_att_avg);
		 tv_med_avg   = (TextView)findViewById(R.id.tv_med_avg);
		 tv_med_tot   = (TextView)findViewById(R.id.tv_med_tot);
		 chk_read = (CheckBox)findViewById(R.id.chk_read);
 
		  prepGraph();
			   
    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");

        tv_conn.setText("");
        tv_conn.append("Android version: " + Integer.valueOf(android.os.Build.VERSION.SDK) + "\n" );
        
        ttobj=new TextToSpeech(getApplicationContext(), 
        	      new TextToSpeech.OnInitListener() {
        	      @Override
        	      public void onInit(int status) {
        	         if(status != TextToSpeech.ERROR){
        	             ttobj.setLanguage(Locale.US);
        	            }				
        	         }
        	      });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if(bluetoothAdapter == null) {
        	// Alert user that Bluetooth is not available
        	Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }else {
        	/* create the TGDevice */
        	tgDevice = new TGDevice(bluetoothAdapter, handler);
        }
    }
    
	private void prepGraph() { 
		GraphViewData  gvd[]  = new GraphViewData[] {  new GraphViewData(0, 0d) }; 
		  att_Series = new GraphViewSeries("att", new GraphViewSeriesStyle(Color.rgb(200, 50, 00), 3),gvd);
		  med_Series = new GraphViewSeries("med", new GraphViewSeriesStyle(Color.rgb(90, 250, 00), 3),gvd);
		  sig_Series = new GraphViewSeries("sig", new GraphViewSeriesStyle(Color.rgb(255,255,0), 3),gvd);
		   GraphView graphView = new LineGraphView(
			       this /*context*/ , "" /*"GraphViewDemo"*/ /* heading */
		   );
		   graphView.setScrollable(true);
		   graphView.setManualYAxisBounds(100d, 0d);
		    graphView.addSeries(att_Series); // data 
		    graphView.addSeries(med_Series); // data 
		    graphView.addSeries(sig_Series); // data 
		 graphView.setViewPort(0, 12);
		   graphView.getGraphViewStyle().setNumHorizontalLabels(5);
		   graphView.getGraphViewStyle().setNumVerticalLabels(5); 
		   graphView.getGraphViewStyle().setVerticalLabelsWidth(40);
		   LinearLayout layout = (LinearLayout) findViewById(R.id.ll_graph);
		   layout.addView(graphView);
	}
    
    @Override
    public void onDestroy() {
    	tgDevice.close();
        
        if (wl.isHeld()) wl.release();
        
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
         }
         super.onPause();
         
         super.onDestroy();
    }
    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:

                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
	                	//tv_conn.append("Connecting...\n");
	                	tv_conn.setText("Connecting... "); 
	                	break;		                    
	                case TGDevice.STATE_CONNECTED: 
	                	tv_conn.setText("Connected. ");
	                	tgDevice.start();
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
	                	//tv_conn.append("Can't find\n");
	                	tv_conn.setText("Can't find. Turn on BT or Clean up other BT connection ");
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
	                	//tv_conn.append("not paired\n");
	                	tv_conn.setText("not paired ");
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	tv_conn.setText("Disconnected.... ");
                } 
                break;
            case TGDevice.MSG_POOR_SIGNAL:
            	poor_signal = msg.arg1;
            	prepend(tv_signal, ((200-poor_signal)/2)  );
            	appendGraph(CONST_SIGNAL, ((200-poor_signal)/2));
            	if (poor_signal == 0) { 
            		if ( read_cnt ++ == read_interval){
            			readAttMed(); read_cnt = 0;
                    }
            	}
                break;
            case TGDevice.MSG_RAW_DATA:
            	//prepend(tv_values, "Got raw: " + msg.arg1 + "\n" );
            	 //tv_values.append("Got raw: " + msg.arg1 + "\n"); 
            	break;
            case TGDevice.MSG_HEART_RATE:
            	//prepend(tv_values, "Heart rate: " + msg.arg1 + "\n" );
            	// tv_values.append("Heart rate: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_ATTENTION: 
            	att_val =msg.arg1 ;
            	prepend(tv_attention, msg.arg1);
            	updateAvgTotal(CONST_ATTENTION, msg.arg1);
            	appendGraph(CONST_ATTENTION, att_val);
            	break;
            case TGDevice.MSG_MEDITATION:
            	 med_val = msg.arg1 ;     
            	prepend(tv_meditation, msg.arg1);
            	updateAvgTotal(CONST_MEDITATION, msg.arg1);
            	appendGraph(CONST_MEDITATION, med_val);
            	break;
            case TGDevice.MSG_BLINK:
            	//tv_values.append("Blink: " + msg.arg1 + "\n"); 
            	prepend(tv_blink, msg.arg1);
            	break;
            case TGDevice.MSG_RAW_COUNT:
            	//tv_values.append("Raw Count: " + msg.arg1 + "\n"); 
            	//prepend(tv_raw_count, msg.arg1); 
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	tv_conn.setText("Low Battery");
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	break;
            case TGDevice.MSG_RAW_MULTI:
            	//TGRawMulti rawM = (TGRawMulti)msg.obj;
            	//tv_values.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            	//prepend(tv_values, "Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2 + "\n");
            default:
            	break;
        }
        }
    };
    
    //CONST_ATTENTION, msg.arg1
    private void updateAvgTotal(int Mtype1, int val1){
    	// only calculate good signal
    	if ( poor_signal > 0 ) return;
    	
    	switch ( Mtype1  ) {
    	case  CONST_ATTENTION :
    		att_tot = att_tot + val1;
    		att_cnt ++;
    		att_avg =  att_tot   / att_cnt;
    		
    	    tv_att_avg.setText(""+att_avg);
    	    tv_att_tot.setText(""+att_tot); 
    		break;
    	case  CONST_MEDITATION :
    		med_tot = med_tot + val1;
    		med_cnt ++;
    		med_avg =  med_tot / med_cnt;

    	    tv_med_avg.setText(""+med_avg);
    	    tv_med_tot.setText(""+med_tot);
    		break;
    	}
    }
    
    private void prepend(TextView tv, String i){
    	String s = i + " " +  tv.getText().toString() ; 
    	if ( s.length() > len_limit){
    		s = s.substring( 0,len_limit );
    	} 
    	tv.setText(s);
    }
    private void prepend(TextView tv, int i){
    	String s = i + " " +  tv.getText().toString() ; 
    	if ( s.length() > len_limit){
    		s = s.substring( 0,len_limit );
    	} 
    	tv.setText(s);
    }
    
    public void doStuff(View view) {
    	Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
    //	Log.i("click1", "test"); 
    	if(tgDevice.getState() == TGDevice.STATE_CONNECTING ) { 
    		//tv_conn.append(Disconnected....);
            prepend(tv_conn, "Already Connecting\n");
    		return;
    	}
    	
    	if(tgDevice.getState() == TGDevice.STATE_CONNECTED ) { 
    		prepend(tv_conn, "Alrready Connected \n");
    		return;
    	}

    	try {
    		 tgDevice.connect(rawEnabled  );   
    	 //tgDevice.connect(rawEnabled : boolean);
    	}catch (Exception e){
    		Log.i("TGconnect Error" , e.getMessage());
    		prepend(tv_conn, "TGconnect Error" + e.getMessage());
    	}
    }
    
    public void stop(View view) {
    		tgDevice.close() ;  

    }
     
    
    public void clickToggleScreenOn(View view){
      	ToggleButton  chb = (ToggleButton) view;
      	if (chb.isChecked()) {
      		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
          }else{
       	   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
          }
      }
        
    
    public void toggleBackground(View view){
    	ToggleButton  chb = (ToggleButton) view;
   	if (chb.isChecked()) { 
   		wl.acquire();
       }else{
       	if (wl.isHeld()) wl.release();
       }
   }
 
   public void appendGraph(int type, int val){
	   switch (type ){
	   case CONST_ATTENTION :
	      att_Series.appendData(new GraphViewData(i_att_no, val),  true, 40) ;
	      i_att_no ++;
	   break;
	   case CONST_MEDITATION :
		   med_Series.appendData(new GraphViewData(i_med_no, val),  true, 40) ;
		   i_med_no ++;
		   break; 
	   case CONST_SIGNAL :
		   sig_Series.appendData(new GraphViewData(i_sig_no, val),  true, 40) ;
		   i_sig_no ++;
		   break;
	   } 
   }
   
    public void readAttMed(){
    	 
    	if (!chk_read.isChecked()){
    		return; 
    	}
    	int read_att, read_med ;
    	read_att = (att_val+4) /10; //small round, trunc is default.
    	read_med = (med_val+4) /10;
    	//get att value, get med value 
    	String toSpeak ="" + read_att + "   " + read_med ; 
    	ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    	//set read_interval
    	read_interval = Integer.parseInt(((EditText) findViewById(R.id.et_read_interval)).getText().toString());
    }
}