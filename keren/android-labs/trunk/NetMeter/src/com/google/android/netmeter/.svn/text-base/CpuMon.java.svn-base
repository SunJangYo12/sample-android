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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

import android.util.Log;
import android.widget.TextView;

public class CpuMon {
	final private String STAT_FILE = "/proc/stat";
	final private DecimalFormat mPercentFmt = new DecimalFormat("#0.0");
	
	private long mUser;
	private long mSystem;
	private long mTotal;
	
	private HistoryBuffer mHistory;
		
	private Vector<TextView> mDisplay;
	
	
	public CpuMon() {
		mHistory = new HistoryBuffer();
		readStats();
	}
	
	public HistoryBuffer getHistory() {
		return mHistory;
	}
	
	public void linkDisplay(Vector<TextView> display) {
		mDisplay = display;
		readStats();
	}
	
	public void unlinkDisplay() {
		mDisplay = null;
	}
	
	public boolean readStats() {
		FileReader fstream;
		try {
			fstream = new FileReader(STAT_FILE);
		} catch (FileNotFoundException e) {
			Log.e("MonNet", "Could not read " + STAT_FILE);
			return false;
		}
		BufferedReader in = new BufferedReader(fstream, 500);
		String line;
		try {
			while ((line = in.readLine()) != null) {
				if (line.startsWith("cpu")) {
					updateStats(line.trim().split("[ ]+"));
					return true;
				}
			}
		} catch (IOException e) {
			Log.e("MonNet", e.toString());
		}
		return false;
	}
	
	private void updateStats(String[] segs) {
		// user = user + nice
		long user = Long.parseLong(segs[1]) + Long.parseLong(segs[2]);
		// system = system + intr + soft_irq
		long system = Long.parseLong(segs[3]) + Long.parseLong(segs[6]) + Long.parseLong(segs[7]);
		// total = user + system + idle + io_wait
		long total = user + system + Long.parseLong(segs[4]) + Long.parseLong(segs[5]);
		
		if (mTotal != 0 || total >= mTotal) {
			long duser = user - mUser;
			long dsystem = system - mSystem;
			long dtotal = total - mTotal;
			if (mDisplay != null) {
				mDisplay.get(0).setText(mPercentFmt.format(
						(double)(duser+dsystem)*100.0/dtotal) + "% ("
				+ mPercentFmt.format(
						(double)(duser)*100.0/dtotal) + "/"
				+ mPercentFmt.format(
						(double)(dsystem)*100.0/dtotal) + ")");
			}
			mHistory.add((int)((duser + dsystem) * 100 / dtotal));
		} 
		mUser = user;
		mSystem = system;
		mTotal = total;
		
		
	}
}