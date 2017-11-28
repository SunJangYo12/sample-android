package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.view.*;
import java.util.*;
import android.graphics.*;
import android.media.*;
public class MainActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		TextView t = (TextView)findViewById(R.id.han);
		t.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				testNotification();
			}
		});
	}
	
	private void createDownloadNotification()
	{
        Intent intent = new Intent(this, MainActivity.class);
	
    	RemoteViews	rm = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom);
      
        PendingIntent pause = PendingIntent.getActivity(this, 0, intent, 0);
		//PendingIntent dismiss = PendingIntent.getService(getApplicationContext(), DISMISS, dismissIntent, 0);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
        rm.setOnClickPendingIntent(R.id.pause, pause);
        //rm.setOnClickPendingIntent(R.id.dismiss, dismiss);
		
		Notification noti = new Notification.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			//.setPriority(Notification.PRIORITY_MAX)
			.setContent(rm)
			.build(); 


		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		noti.flags |=Notification.FLAG_NO_CLEAR;//Do not clear the notification
        noti.defaults |=Notification.DEFAULT_LIGHTS;// LED
        noti.defaults |=Notification.DEFAULT_VIBRATE;//Vibration
        noti.defaults |=Notification.DEFAULT_SOUND;// Sound
		
		int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
		
		notificationManager.notify(m, noti);
    }
	
	private void testNotification() {
		Notification.Builder builder = new Notification.Builder(this);
		builder.setContentTitle("APP_NAME")
			.setContentText("msg")
			.setContentIntent(PendingIntent.getActivity(this, UUID.randomUUID().hashCode(), new Intent(this, MainActivity.class), Notification.FLAG_AUTO_CANCEL))
			.setWhen(System.currentTimeMillis())
			.setPriority(Notification.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			//.setDefaults(Notification.DEFAULT_ALL)
			.setVibrate(new long[] {0, 1000, 200,1000 })
			.setLights(Color.MAGENTA, 500, 500)
			.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
			.setSmallIcon(R.drawable.ic_launcher);

        Notification ntf = builder.build();
//        ntf.flags = Notification.DEFAULT_ALL;
//        ntf.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
//        ntf.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
        notificationManager.notify(0, ntf);
	}
	
}

