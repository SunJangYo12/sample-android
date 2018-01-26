package com.google.android.netmeter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Toast;


/**
 * 
 * Activity which displays a list of currently running processes,
 * sorted by CPU utilization - similar to the unix top utility.
 *
 */
public class TaskList extends ListActivity {
	private static final int DELAY = 30000;
	final private DecimalFormat mPercentFmt = new DecimalFormat("#0.0");
	private Top mTop;
	private ArrayAdapter<String> mAdapter;
	
	// Handler which is executed every 30s
	// to recalculate the task list and refresh
	// the display.
	private Handler mHandler = new Handler();
	private Runnable mRefreshTask = new Runnable() {
		public void run() {
			redrawList();
			mHandler.postDelayed(mRefreshTask, DELAY);
		}
	};
	
	 /** 
	  * Framework method called when the activity is first created. 
	  */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAdapter = new ArrayAdapter<String>(this,
                R.layout.tasklist, new ArrayList<String>());
        setListAdapter(mAdapter);
    }
    
    /**
     * Framework method called when the activity gains forground focus.
     * 
     * Periodic polling takes place between onResume and onPause.
     */
    @Override
    public void onResume() {
    	super.onResume();
    	mTop = new Top();
    	Toast.makeText(this, getText(R.string.disp_collecting), Toast.LENGTH_SHORT).show();
    	mHandler.postDelayed(mRefreshTask, 1000);
    }
	/**
	 * Framework method called when the activity looses foreground focus.
	 */
    @Override
    public void onPause() {
    	super.onPause();
    	mHandler.removeCallbacks(mRefreshTask);
    	mTop = null;
    }
    
    /**
     * Regenerate the list of processes sorted by CPU usage
     * and update the ListView through the array adapter.
     * 
     * This update operation seems to be quite CPU intensive.
     * 
     */
    private void redrawList() {
    	Vector<Top.Task> top_list = mTop.getTopN();
    	mAdapter.clear();
		for(Iterator<Top.Task> it = top_list.iterator(); it.hasNext(); ) {
			Top.Task task = it.next();
			//if (task.getUsage() == 0) break;
			mAdapter.add(mPercentFmt.format(((double)task.getUsage())/10.0)
					+ "%  " + task.getName());
		}
    }
}
