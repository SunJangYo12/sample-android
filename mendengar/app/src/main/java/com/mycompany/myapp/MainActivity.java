package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.media.*;
import android.content.*;

public class MainActivity extends Activity implements View.OnClickListener
{
	private TextView output;
	private Switch aSwitch;
	private int captureSize;
	private AudioRecord audioRecord;
	private boolean isRunning = false;
	static int j;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		RelativeLayout layout = new RelativeLayout(this);
		aSwitch = new Switch(this);
		aSwitch.setId(View.generateViewId());
		aSwitch.setTextOn("Rekam");
		aSwitch.setOnClickListener(this);
		layout.addView(aSwitch);
		
		output = new TextView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, aSwitch.getId());
		output.setLayoutParams(lp);
		layout.addView(output);
		setContentView(layout);
		captureSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
									  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, captureSize);
	}
	@Override
	public void onClick(View v) 
	{
		if (aSwitch.isChecked()) 
		{
			audioRecord.startRecording();
			isRunning = true;
			new CaptureThread().start();
		} 
		else 
		{
			isRunning = false;
			audioRecord.stop();
			Intent i = new Intent(this, Digital.class);
			startActivity(i);
		}
	}
	class CaptureThread extends Thread 
	{
		@Override
		public void run() 
		{
			final short[] buffer = new short[captureSize];
			while(isRunning) 
			{
				audioRecord.read(buffer, 0, captureSize);
				final StringBuilder text = new StringBuilder();
				
				for (int i=0; i<captureSize; i++)
				{
					text.append(buffer[i]);
					text.append(' ');
					j = i;
				}
				output.postDelayed(new Runnable() 
				{
						@Override
						public void run() 
						{
							output.setText(text.toString());
							if (buffer[j] > 1000)
							{
								isRunning = false;
								Toast.makeText(getApplicationContext(), ""+buffer[j], Toast.LENGTH_SHORT).show();
							}
						}
					}, 100);
			}
		}
	}
}
