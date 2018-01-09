package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;

public class MainActivity extends Activity implements View.OnClickListener {

    private Switch sw;
    private VideoRecordService.LocalBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        sw = new Switch(this);
        sw.setTextOn("Perekam Aktif");
        sw.setTextOff("Perekam Tidak Aktif");
        sw.setOnClickListener(this);
        sw.setId(View.generateViewId());
        layout.addView(sw);
        setContentView(layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (binder == null) {
			Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, VideoRecordService.class);
            bindService(intent, serviceConnection, 0);
        }
    }

    @Override
    public void onClick(View v) {
        if (sw.isChecked()) {
            Intent intent = new Intent(this, VideoRecordService.class);
            startService(intent);
            finish();
        } else if (binder != null) {
            binder.matikan();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (binder != null) {
			Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
			
            unbindService(serviceConnection);
            binder = null;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (VideoRecordService.LocalBinder) service;
            sw.setChecked(binder.isAktif());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

}
