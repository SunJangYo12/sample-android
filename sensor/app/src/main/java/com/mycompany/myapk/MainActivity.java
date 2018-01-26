package com.mycompany.myapk;

import android.app.*;
import android.os.*;
import android.hardware.*;
import android.graphics.*;

public class MainActivity extends Activity 
{
	SensorManager sensorManager;
	Sensor proximitySensor, gyroscopeSensor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		
		if(proximitySensor == null) {
			//Log.e(TAG, "Proximity sensor not available.");
			finish(); // Close app
		}
		
		sensorManager.registerListener(proximitySensorListener,  proximitySensor, 2 * 1000 * 1000);
		//sensorManager.registerListener(gyroscopeSensorListener,  gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	

	// Create listener
	SensorEventListener proximitySensorListener = new SensorEventListener() 
	{
		@Override
		public void onSensorChanged(SensorEvent sensorEvent)
		{
			if(sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
				// Detected something nearby
				getWindow().getDecorView().setBackgroundColor(Color.RED);
			} 
			else {
				// Nothing is nearby
				getWindow().getDecorView().setBackgroundColor(Color.GREEN);
			}
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int i) {
		}
	};
	
	// Create listener
	SensorEventListener gyroscopeSensorListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent sensorEvent)
		{
			if(sensorEvent.values[2] > 0.5f) { // anticlockwise
				getWindow().getDecorView().setBackgroundColor(Color.BLUE);
			} 
			else if(sensorEvent.values[2] < -0.5f) { // clockwise
				getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int i) {
		}
	};


	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		
		sensorManager.unregisterListener(proximitySensorListener);
	}
	
}
