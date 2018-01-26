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

import java.text.DecimalFormat;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
/**
 * 
 * @author Bernhard Suter
 *
 * Calculator for special purpose "bistromatic" calculations. It maintains the set of 
 * current values for each parameter and updates the display section of the screen layout.
 * 
 * One data field is currently in focus for input and some rows or columns in the table can
 * be hidden if their parameter value corresponds to a "not applicable" value
 * (e.g. 0% for tax rate).
 */
class Calculator {
	// The parameters which can enter into the bistromatic calculations
	final public static int AMOUNT = 0; // Total amount on restaurant bill
	final public static int TAX = 1;    // Applicable tax rate, if any
	final public static int TIP = 2;   // Applicable tipping rate, if any
	final public static int PEOPLE = 3; // Number of people by which to dived the bill
	final public static int FX = 4;     // Currency exchange rate, if any
	
	// input filed highlight color
	final private int COLOR_ACTIVE = Color.DKGRAY;

	// Updated current values used in calculations.
	double mAmountVal;
	double mTaxRate;
	double mTipRate;
	double mFxRate;
	int mPersons;

	
	TableLayout mDisplay; //The main table view widget which contains all the display elements 
	
	// A cache of text views for more rapid lookup - not sure this is really needed...
	HashMap<Integer, TextView> mViews = new HashMap<Integer, TextView>();
	
	// Number formats for currency numbers and percentage rates.
	DecimalFormat mFmt = new DecimalFormat("###,###,##0.00");
	DecimalFormat mFmtRate = new DecimalFormat("###.##");
	/**
	 * Initialize object instance
	 * 
	 * @param display Display section top level table widget.
	 */
	public Calculator(TableLayout display) {
		mDisplay = display;
	
		cacheView(R.id.loc_amount);
		cacheView(R.id.ref_amount);
		cacheView(R.id.loc_tax);
		cacheView(R.id.ref_tax);
		cacheView(R.id.loc_tip);
		cacheView(R.id.ref_tip);
		cacheView(R.id.loc_total);
		cacheView(R.id.ref_total);
		cacheView(R.id.loc_people);
		cacheView(R.id.ref_people);
		
		// Update display with initial values
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mDisplay.getContext());
		setFeatureValue(AMOUNT, prefs.getFloat("amount", (float) 0.0));
		setFeatureValue(TAX, prefs.getFloat("tax", (float) 0.0));
		setFeatureValue(TIP, prefs.getFloat("tip", (float) 20.0));
		setFeatureValue(FX, prefs.getFloat("fx", (float) 1.0));
		setFeatureValue(PEOPLE, prefs.getInt("people", 1));
	}
	/**
	 * Save the current values into a preferences manager to make them persistent.
	 */
	public void saveState() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mDisplay.getContext());
		SharedPreferences.Editor ed = prefs.edit();

		ed.putFloat("amount", (float) mAmountVal);
		ed.putFloat("tax", (float) mTaxRate);
		ed.putFloat("tip", (float) mTipRate);
		ed.putFloat("fx", (float) mFxRate);
		ed.putInt("people", mPersons);
		ed.commit();
	}
	
	/**
	 * Set a new value for any of the parameters which are part of the "bistromatic"
	 * calculus model.
	 * 
	 * @param feature_id Input parameter ID (see list at top of class)
	 * @param val new value for parameter
	 */
	public void setFeatureValue(int feature_id, double val) {
		String label;
		TextView view;
		switch (feature_id) {
		case AMOUNT:
			mAmountVal = val;
			break;
		case TAX:
			mTaxRate = val;
			label = mDisplay.getContext().getString(R.string.disp_tax);
			view = ((TextView)mDisplay.findViewById(R.id.head_tax));
			view.setText(mFmtRate.format(mTaxRate)+label);
			break;
		case TIP:
			mTipRate = val;
			label = mDisplay.getContext().getString(R.string.disp_tip);
			view = ((TextView)mDisplay.findViewById(R.id.head_tip));
			view.setText(mFmtRate.format(mTipRate)+label);
			break;
		case PEOPLE:
			mPersons = (int)val;
			if (mPersons == 0) mPersons = 1; // Turn 0 into specific "n.a." value
			label = mDisplay.getContext().getString(R.string.disp_people);
			view = ((TextView)mDisplay.findViewById(R.id.head_people));
			view.setText(label+" ("+Integer.toString(mPersons)+")");
			break;
		case FX:
			if (val == 0.0) {
				mFxRate = 1;
			} else {
				mFxRate = val;
			}
			label = mDisplay.getContext().getString(R.string.disp_ref_currency);
			view = ((TextView)mDisplay.findViewById(R.id.head_fx));
			view.setText(label+"\n(1:"+mFmt.format(mFxRate)+")");
		}
		
		recalculate();
	}
	
	/**
	 * Recalculate all values based on input parameters and refresh dispplay.
	 */
	public void recalculate() {
		double tax_amount = 0.0;
		double tip_amount =0.0;
		double total_amount;
		double per_person_amount;
		
		
		if (mTaxRate != 0.0) {
			tax_amount = mAmountVal / 100 * mTaxRate;
			setFieldValue(R.id.loc_tax, tax_amount);
			if (mFxRate != 1.0) {
				setFieldValue(R.id.ref_tax, tax_amount * mFxRate);
			} else {
				setFieldString(R.id.ref_tax, "");
			}
		} else {
			setFieldString(R.id.loc_tax, "");
			setFieldString(R.id.ref_tax, "");
		}
		if (mTipRate != 0.0) {
			tip_amount = mAmountVal / 100 * mTipRate;
			setFieldValue(R.id.loc_tip, tip_amount);
			if (mFxRate != 1.0) {
				setFieldValue(R.id.ref_tip, tip_amount * mFxRate);
			} else {
				setFieldString(R.id.ref_tip, "");
			}
		} else {
			setFieldString(R.id.loc_tip, "");
			setFieldString(R.id.ref_tip, "");
		}
		total_amount = mAmountVal + tax_amount + tip_amount;
		if (mPersons != 1) {
			per_person_amount = total_amount / mPersons;
			setFieldValue(R.id.loc_people, per_person_amount);
			if (mFxRate != 1.0) {
				setFieldValue(R.id.ref_people, per_person_amount * mFxRate);
			} else {
				setFieldString(R.id.ref_people, "");
			}
		} else {
			setFieldString(R.id.loc_people, "");
			setFieldString(R.id.ref_people, "");
		}
		
		setFieldValue(R.id.loc_amount, mAmountVal);
		setFieldValue(R.id.loc_total, total_amount);
		if (mFxRate != 1.0) {
			setFieldValue(R.id.ref_amount, mAmountVal * mFxRate);
			setFieldValue(R.id.ref_total, total_amount * mFxRate);
		} else {
			setFieldString(R.id.ref_amount, "");
			setFieldString(R.id.ref_total, "");
		}
	}
	
	/**
	 * Hide any rows or columns for which the input parameter is the "not applicable" value.
	 */
	public void hideUnusedFeatures() {
		hideFeature(TAX, mTaxRate == 0.0);
		hideFeature(TIP, mTipRate == 0.0);
		hideFeature(PEOPLE, mPersons == 1);
		hideFeature(FX, mFxRate == 1.0);
	}
	
	/**
	 * Determine whether a particular row is hidden
	 * 
	 * @param feature_id input parameter/row ID (see definitions on top of class)
	 * @return boolean - true if hidden, false if visible
	 */
	public boolean isHidden(int feature_id) {
		switch (feature_id) {
		case TAX:
			return mTaxRate == 0.0;
		case TIP:
			return mTipRate == 0.0;
		case PEOPLE:
			return mPersons == 1;
		case FX:
			return mFxRate == 1.0;
		default:
			return false;
		}
	}
	
	/**
	 * Turn any particular optional row/col on or off
	 * 
	 * @param feature_id parameter/row ID (see top of class)
	 * @param hide make invisible if true, visible if false
	 */
	public void hideFeature(int feature_id, boolean hide) {
		int visibility = hide ? View.GONE : View.VISIBLE;
		
		switch(feature_id) {
		case TAX:
			mDisplay.findViewById(R.id.row_tax).setVisibility(visibility);
			break;
		case TIP:
			mDisplay.findViewById(R.id.row_tip).setVisibility(visibility);
			break;
		case PEOPLE:
			mDisplay.findViewById(R.id.row_people).setVisibility(visibility);
			break;
		case FX:
			mDisplay.findViewById(R.id.head_fx).setVisibility(visibility);
			mViews.get(R.id.ref_amount).setVisibility(visibility);
			mViews.get(R.id.ref_tax).setVisibility(visibility);
			mViews.get(R.id.ref_tip).setVisibility(visibility);
			mViews.get(R.id.ref_total).setVisibility(visibility);
			mViews.get(R.id.ref_people).setVisibility(visibility);
			break;
		}
	}
	
	/**
	 * Change background color to indicate which field is currently active for input.
	 * 
	 * @param feature_id input field which is to be made active.
	 */
	public void setFocus(int feature_id) {
		int bg_color = mViews.get(R.id.loc_total).getSolidColor();
		int color;

		color = feature_id == AMOUNT ? COLOR_ACTIVE : bg_color;
		mViews.get(R.id.loc_amount).setBackgroundColor(color);
		color = feature_id == TAX ? COLOR_ACTIVE : bg_color;
		mDisplay.findViewById(R.id.head_tax).setBackgroundColor(color);
		color = feature_id == TIP ? COLOR_ACTIVE : bg_color;
		mDisplay.findViewById(R.id.head_tip).setBackgroundColor(color);
		color = feature_id == PEOPLE ? COLOR_ACTIVE : bg_color;
		mDisplay.findViewById(R.id.head_people).setBackgroundColor(color);
		color = feature_id == FX ? COLOR_ACTIVE : bg_color;
		mDisplay.findViewById(R.id.head_fx).setBackgroundColor(color);

	}
	
	/**
	 * Get the current string value of a particular filed
	 * 
	 * @param feature_id field selector.
	 * @return String value of field.
	 */
	public String getFieldValue(int feature_id) {
		TextView field = mViews.get(feature_id);
		return field.getText().toString();
	}
	
	/**
	 * Private helper to set a TextView with a double value formatted as currency
	 * 
	 * @param id resource ID for TextView field (in cache)
	 * @param val new value
	 */
	private void setFieldValue(int id, double val) {
		TextView field = mViews.get(id);
		field.setText(mFmt.format(val));
	}
	
	/**
	 * Private helper to set a TextView with a new string value.
	 * 
	 * @param id resource ID for TextView field.
	 * @param str New value.
	 */
	private void setFieldString(int id, String str) {
		TextView field = mViews.get(id);
		field.setText(str);
	}
	
	/**
	 * Build a cache of TextView objects indexed by resource ID.
	 * (This is probably useless if findViewById itself is very fast).
	 * 
	 * @param id Resource ID
	 */
	private void cacheView(int id) {
		mViews.put(id, (TextView)mDisplay.findViewById(id));
	}
	
}