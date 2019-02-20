package com.key;

import android.app.Activity;
import android.os.Bundle;
import android.content.*;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startService(new Intent(this, RemoteIME.class));
	}
}
