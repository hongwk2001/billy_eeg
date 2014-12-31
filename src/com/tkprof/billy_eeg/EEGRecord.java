package com.tkprof.billy_eeg;
  
public class EEGRecord {
  private String datetime;
  private int att_avg;
  private int att_tot;
  private int med_avg;
  private int med_tot;

  public String getDateTime() {
    return datetime;
  }

  public void setDateTime(String dt) {
    this.datetime = dt;
  }

  public int getAttAvg() {
    return att_avg;
  }

  public void setAttAvg(int val) {
    this.att_avg = val;
  }

  public int getMedAvg() {
	    return med_avg;
	  }

	  public void setMedAvg(int val) {
	    this.med_avg = val;
	  }

	  public int getAttTot() {
		    return att_tot;
		  }
	  
	  public void setAttTot(int val) {
		    this.att_tot = val;
	 }

	  public int getMedTot() {
		    return med_tot;
		  }

	  public void setMedTot(int val) {
	    this.med_tot = val;
	  }
	  
  // Will be used by the ArrayAdapter in the ListView
  @Override
  public String toString() {
    return  datetime 
    		+" "+ att_avg
    		+" "+ att_tot
    		+" "+ med_avg
    		+" "+ med_tot; 
  }
} 