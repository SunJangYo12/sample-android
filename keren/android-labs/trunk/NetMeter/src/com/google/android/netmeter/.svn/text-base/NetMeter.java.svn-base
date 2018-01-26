/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.netmeter;


import java.util.Vector;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * 
 * Main controller activity for NetMeter application.
 * 
 * Creates the display (table plus graph view) and connects to
 * the NetMeterService, starting it if necessary. Since the service
 * will directly update the display when it generates new data, references
 * of the display elements are passed to the service after binding.
 */
public class NetMeter extends Activity {
	final private String TAG="NetMeter";
	
	private NetMeterService mService;
	private Vector<TextView> mStatsFields;
	private Vector<TextView> mInfoFields;
	private Vector<TextView> mCpuFields;
	
	//private PowerMon mPower;
	
	private GraphView mGraph;
	
	/**
	 * Service connection callback object used to establish communication with 
	 * the service after binding to it.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            
        	// Get reference to (local) service from binder
            mService = ((NetMeterService.NetMeterBinder)service).getService();
            Log.i(TAG, "service connected");
            // link up the display elements to be updated by the service
            mService.setDisplay(mStatsFields, mInfoFields, mCpuFields, mGraph);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Log.i(TAG, "service disconnected - should never happen");
        }
    };

	
    /** 
     * Framework method called when the activity is first created. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        startService(new Intent(this, NetMeterService.class));
        
        setContentView(R.layout.main);
        mStatsFields = new Vector<TextView>();
        mInfoFields = new Vector<TextView>();
        mCpuFields = new Vector<TextView>();
        
        mGraph = (GraphView)findViewById(R.id.graph);
        
        createTable();
    }

    /**
     * Framework method to create menu structure.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return super.onCreateOptionsMenu(menu);
    }

    /**
     * Framework method called when activity menu option is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.reset:
    		mService.resetCounters();
    		break;
    	case R.id.toggle:
    		String banner = mGraph.toggleScale();
    		Toast.makeText(this, banner, Toast.LENGTH_SHORT).show();
    		break;
    	case R.id.top:
    		Intent intent = new Intent();
            intent.setClass(this, TaskList.class);
            startActivity(intent);
            break;
    	case R.id.help:
    		Intent myIntent = new Intent();
    		myIntent.setClass(this, HelpActivity.class);
    		startActivity(myIntent);
    		break;
    	case R.id.stop:
    		stopService(new Intent(this, NetMeterService.class));
    		finish();
    		break;
    	}
    	return true;
    }

    /**
     * Framework method called when activity becomes the foreground activity.
     * 
     * onResume/onPause implement the most narrow window of activity life-cycle
     * during which the activity is in focus and foreground.
     */
    @Override
    public void onResume() {
    	super.onResume();
    	bindService(new Intent(this, 
                NetMeterService.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    /**
     * Framework method called when activity looses foreground position
     */
    @Override
    public void onPause() {
    	super.onPause();
    	unbindService(mConnection);
    }
 
    /**
     *  Algorithmically generate the table on the top half of the screen,
     *  which is used to display status and cummulative usage of
     *  cellular and wifi network interfaces, as well as the current
     *  CPU usage.
     */
    private void createTable() {
    	TableLayout table = (TableLayout)findViewById(R.id.disp);
    	
    	mInfoFields.addElement(createTableRow(table, R.string.disp_cell, -1, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.disp_in, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.disp_out, 0));
    	createTableRow(table, 0, 0, 0);
    	mInfoFields.addElement(createTableRow(table, R.string.disp_wifi, -1, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.disp_in, 0));
    	mStatsFields.addElement(createTableRow(table, -1, R.string.disp_out, 0));
    	createTableRow(table, 0, 0, 0);
    	mCpuFields.addElement(createTableRow(table, R.string.disp_cpu,
    				R.string.disp_cpu_type, 0));
    }
    
    /**
     * Helper function to generate a table row based on 4 integer arguments which
     * represent the column cells in the row.
     * 
     * If the associated value is -1, the cell is invisible, if it is 0, the cell is set
     * to an empty text and otherwise the number is assumed to be the ID of a text
     * resource.
     * 
     */
    private TextView createTableRow(TableLayout table, int c1, int c2, int c3) {
    	int[] cell_text_ids = {c1, c2, c3};
    	TableRow tr = new TableRow(this);
		table.addView(tr);
		for (int i=0; i < 3; ++i) {
			TextView txt = new TextView(this);
			tr.addView(txt);
			if (cell_text_ids[i] == -1) {
				txt.setVisibility(View.INVISIBLE);
			} else if (cell_text_ids[i] == 0) {
				txt.setText("");
				txt.setGravity(Gravity.RIGHT);
				return txt;
			} else {
				txt.setText(getString(cell_text_ids[i]));
			}
		}
		return null;
    }
}