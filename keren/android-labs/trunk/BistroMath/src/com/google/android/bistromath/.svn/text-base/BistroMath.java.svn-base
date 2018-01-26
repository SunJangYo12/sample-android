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
package com.google.android.bistromath;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Bernhard Suter
 *
 * Main activity for a specialized "bistromatic" calculator. It can be used to calculate tax and/or
 * tip on a restaurant bill, divide it evenly between N people for foreign travelers figure out
 * how much all this would be in your own currency.
 * 
 * The screen consists of a display grid at the top and a virtual key-pad (unless a physical 
 * keyboard is present). The activity captures touch and key-press events and translates them
 * into numerical inputs for the calculator module to update the display.
 *
 */
public class BistroMath extends Activity {
	// String values for all keys in virtual keypad
	final private String[] mKeys = {"1", "2", "3", "4", "5", "6",
						 	"7", "8", "9", ".", "0", "C"};
	final int CALC_INTENT_RETURN = 1;
	
	// Layout container for the virtual key-pad
	private TableLayout mKeypad;
	
	// "bistromatic" calculator and display engine
	private Calculator mCalculator;
	
	// Current input value in process of being entered
	private double mValue;
	private int mDecimalPos; // 1, 10 or 100 as a divider for current input digit
	
	// Which calculator input parameter are we currently capturing
	private int mCurrentInputField = Calculator.AMOUNT;
	
	/**
	 * Framework method to initialize activity
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	// Inflate layout tree from XML description
    	setContentView(R.layout.main);
    	
    	// initialize input value to 0
    	resetValue();

    	// Container view for virtual key-pad
    	mKeypad = (TableLayout) findViewById(R.id.keypad);
    	
    	// initialize calculator engine
    	mCalculator = new Calculator((TableLayout)findViewById(R.id.display));
    	mCalculator.hideUnusedFeatures();
    	mCalculator.setFocus(mCurrentInputField);
    	
    	// Define a touch-screen handler for touch-sensitive parts of display grid
    	// to switch input focus.
    	View.OnTouchListener touch = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				setFieldFocus(v.getId());
				return false;
			}
    	};
			
		// Define a touch-screen handler for touch-sensitive parts of display grid
    	// to send values to expense reporting app.
    	View.OnTouchListener report = new View.OnTouchListener() {
    		public boolean onTouch(View v, MotionEvent event) {
    			sendToExpenseApp(v.getId());
    			return false;
					}	
	    };	
    		
    	
    	// Install touch-screen handler to certain rows and cells of display grid
    	findViewById(R.id.row_amount).setOnTouchListener(touch);
    	findViewById(R.id.row_tax).setOnTouchListener(touch);
    	findViewById(R.id.row_tip).setOnTouchListener(touch);
    	findViewById(R.id.row_people).setOnTouchListener(touch);
    	findViewById(R.id.head_fx).setOnTouchListener(touch);
    	
    	findViewById(R.id.loc_total).setOnTouchListener(report);
    	findViewById(R.id.ref_total).setOnTouchListener(report);
    	findViewById(R.id.loc_people).setOnTouchListener(report);
    	findViewById(R.id.ref_people).setOnTouchListener(report);

    	// Key-press listener - each key sens its label as the value
    	View.OnClickListener listener = new View.OnClickListener() {
    		public void onClick(View v) {
    			// Perform action on click
    			updateValue((String)((TextView)v).getText());
    		}
    	};
    	// Create the grid of buttons in the table view
    	mKeypad.setStretchAllColumns(true);
    	for(int row = 0; row < 4; row++) {
    		TableRow tr = new TableRow(this);
    		mKeypad.addView(tr);
    		for (int col=0; col<3; col++) {
    			Button b = new Button(this);
    			b.setText(mKeys[row*3+col]);
    			b.setTextSize((float) 30.0);
    			b.setOnClickListener(listener);
    			tr.addView(b);
    		}
    	}
 
    }
   
    /**
     * Framework method to attach the activity menu (defined in XML)
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Framework method called each time before menu is displayed
	 * to do dynamic updates.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		setMenuStateIcon(menu, R.id.m_amount, Calculator.AMOUNT);
		setMenuStateIcon(menu, R.id.m_tax, Calculator.TAX);
		setMenuStateIcon(menu, R.id.m_tip, Calculator.TIP);
		setMenuStateIcon(menu, R.id.m_people, Calculator.PEOPLE);
		setMenuStateIcon(menu, R.id.m_fx, Calculator.FX);
		return true;
	}
    
	/**
	 * Framework method called when activity menu option is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.m_help) {
			Intent myIntent = new Intent();
			myIntent.setClass(this, HelpActivity.class);
			startActivity(myIntent);
		} else if (item.getItemId() == R.id.m_calculator) { 
			Intent myIntent = new Intent();
			myIntent.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
			startActivity(myIntent);
		} else if (item.getItemId() == R.id.m_expense) {
			sendToExpenseApp(R.id.loc_total);
		}else {	
			setFieldFocus(item.getItemId());
		}
		return true;
	}

	
	/**
	 * Framework method to save called when activity is suspended.
	 * Used here to save calculator state into preference manager.
	 */
	@Override
	public void onPause() {
		super.onPause();
		mCalculator.saveState();
	}

	/**
	 * Activity key-press handler.
	 * 
	 * Translate supported keys into their corresponding values of the virtual key-pad
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
		case KeyEvent.KEYCODE_0:
			updateValue("0");
			break;
		case KeyEvent.KEYCODE_1:
			updateValue("1");
			break;
		case KeyEvent.KEYCODE_2:
			updateValue("2");
			break;
		case KeyEvent.KEYCODE_3:
			updateValue("3");
			break;
		case KeyEvent.KEYCODE_4:
			updateValue("4");
			break;
		case KeyEvent.KEYCODE_5:
			updateValue("5");
			break;
		case KeyEvent.KEYCODE_6:
			updateValue("6");
			break;
		case KeyEvent.KEYCODE_7:
			updateValue("7");
			break;
		case KeyEvent.KEYCODE_8:
			updateValue("8");
			break;
		case KeyEvent.KEYCODE_9:
			updateValue("9");
			break;
		case KeyEvent.KEYCODE_PERIOD:
			updateValue(".");
			break;
		case KeyEvent.KEYCODE_DEL:
			updateValue("C");
			break;
		default:
				return super.onKeyDown(keyCode, event);
		}
		return true;
	}
    
	/**
	 * Helper to set the input field focus based on menu selctions or display grid 
	 * touch events.
	 * 
	 * @param resource_id menu or grid row/cell resource IDs
	 */
	protected void setFieldFocus(int resource_id) {
		mCalculator.hideUnusedFeatures();
		switch(resource_id) {
		case R.id.row_amount:
		case R.id.m_amount:
			mCurrentInputField = Calculator.AMOUNT;
			break;
		case R.id.row_tax:
		case R.id.m_tax:
			mCalculator.hideFeature(Calculator.TAX, false);
			mCurrentInputField = Calculator.TAX;
			break;
		case R.id.row_tip:
		case R.id.m_tip:
			mCalculator.hideFeature(Calculator.TIP, false);
			mCurrentInputField = Calculator.TIP;
			break;
		case R.id.row_people:
		case R.id.m_people:
			mCalculator.hideFeature(Calculator.PEOPLE, false);
			mCurrentInputField = Calculator.PEOPLE;
			break;
		case R.id.head_fx:
		case R.id.m_fx:
			mCalculator.hideFeature(Calculator.FX, false);
			mCurrentInputField = Calculator.FX;
			break;
		}
		resetValue();
		mCalculator.setFocus(mCurrentInputField);
	}
	
	/**
	 * Helper to set the appropriate icon for each parameter's menu option
	 * depending on its current focus and visibility status.
	 * 
	 * @param menu menu object
	 * @param resource resource ID of menu item
	 * @param feature_id calculator parameter/feature id
	 */
	protected void setMenuStateIcon(Menu menu, int resource, int feature_id) {
		if (feature_id == mCurrentInputField) {
			menu.findItem(resource).setIcon(android.R.drawable.presence_online);
		} else if (mCalculator.isHidden(feature_id)) {
			menu.findItem(resource).setIcon(android.R.drawable.presence_offline);
		} else {
			menu.findItem(resource).setIcon(android.R.drawable.presence_invisible);
		}
	}
	
	/**
	 * Process each input digit from the keypad and update the current value
	 * 
	 * @param c Supported single character strings of key-pad values
	 */
    protected void updateValue(String c) {
    	if (c == "C") {  // clear
    		resetValue();
    		mCalculator.setFeatureValue(mCurrentInputField, mValue);	
    		return;
    	} else if (c == ".") {  // decimal point
    		mDecimalPos = 10;
    		return;
    	} else {   // digits
    		double digit = Double.parseDouble(c);
    		if (mDecimalPos == 1) {
    			mValue = mValue * 10 + digit;
    		} else if  (mDecimalPos > 100) {
    			return;
    		} else {
    			mValue += digit / mDecimalPos;
    			mDecimalPos *= 10;
    		}
    		mCalculator.setFeatureValue(mCurrentInputField, mValue);	
    	}
    }
    /**
     * Reset the staged input value to 0.00
     */
    protected void resetValue() {
    	mValue = 0.0;
    	mDecimalPos = 1;
    }
    
    protected void sendToExpenseApp(int field_id) {
    	Intent launchIntent = new Intent();
        launchIntent.setAction("com.funkyandroid.action.NEW_TRANSACTION");
        launchIntent.putExtra("com.funkyandroid.CATEGORY", "BistroMath");
        launchIntent.putExtra("com.funkyandroid.AMOUNT", mCalculator.getFieldValue(field_id));
        try {
        	startActivity(launchIntent);
        } catch (ActivityNotFoundException e) {
        	Toast.makeText(this, getString(R.string.err_expense),
        			Toast.LENGTH_LONG).show();
        }
    }
}