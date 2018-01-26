package com.mycompany.myapp;
import android.app.*;
import android.speech.tts.*;
import android.os.*;
import android.content.*;
import android.util.*;
import java.util.*;
import android.widget.*;

public class TTSservice extends Service implements TextToSpeech.OnInitListener{

	private String str;
	private TextToSpeech mTts;
	private static final String TAG="TTSService";
	public static boolean runing = true;
	public static int i = 0;
	Context ctx;
	
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}


	@Override
	public void onCreate() {
		ctx = this;
		mTts = new TextToSpeech(this,
								this  // OnInitListener
								);
		mTts.setSpeechRate(0.5f);
		Log.v(TAG, "oncreate_service");
		str ="turn left please ";
		
		super.onCreate();
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
		Toast.makeText(ctx,""+i,Toast.LENGTH_LONG).show();
		
        super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		Toast.makeText(ctx,""+i,Toast.LENGTH_LONG).show();
		
		sayHello(str);
		new Thku().start();
		Log.v(TAG, "onstart_service");
		super.onStart(intent, startId);
	}

	@Override
	public void onInit(int status) {
		Log.v(TAG, "oninit");
		if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            } 
			else {

                sayHello(str);

            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
	}
	private void sayHello(String str) {
		mTts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	class Thku extends Thread
	{
		
		@Override
		public void run()
		{
			// TODO: Implement this method
			super.run();
			while(runing)
			{
				mTts.speak("hiigjikkstr", TextToSpeech.QUEUE_FLUSH, null);
				
				runing = false;
			}
		}
		
	}
}
