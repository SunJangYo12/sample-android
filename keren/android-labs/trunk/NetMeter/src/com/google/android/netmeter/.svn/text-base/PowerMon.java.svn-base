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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

public class PowerMon {
	private TextView mOutput = null;
	private int mLevel = 0;
	
	BroadcastReceiver mBattReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			context.unregisterReceiver(this);
			int rawlevel = intent.getIntExtra("level", -1);
			int scale = intent.getIntExtra("scale", -1);
			//int status = intent.getIntExtra("status", -1);
			//int health = intent.getIntExtra("health", -1);
			int level = -1;  // percentage, or -1 for unknown
			if (rawlevel >= 0 && scale > 0) {
				level = (rawlevel * 100) / scale;
			}
			
			if (mOutput != null) {
				mOutput.setText(Integer.toString(level)+"%");
			}
			mLevel = level;
		}
	};

	public void linkDisplay(TextView view) {
		mOutput = view;
		mOutput.setText(Integer.toString(mLevel)+"%");
	}
	
	public PowerMon(Context context) {
		IntentFilter battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(mBattReceiver, battFilter);

	}
}
