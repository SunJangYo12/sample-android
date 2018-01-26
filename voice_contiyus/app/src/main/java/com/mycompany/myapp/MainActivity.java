package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.media.*;
import android.speech.*;
import android.content.*;
import android.util.*;
import java.util.*;

public class MainActivity extends Activity implements View.OnClickListener
{
	private TextView result_tv;
    private Button start_listen_btn,stop_listen_btn,mute;
    private SpeechRecognizerManager mSpeechManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		result_tv=(TextView)findViewById(R.id.result_tv);
        start_listen_btn=(Button)findViewById(R.id.start_listen_btn);
        stop_listen_btn=(Button)findViewById(R.id.stop_listen_btn);
        mute=(Button)findViewById(R.id.mute);
		
		start_listen_btn.setOnClickListener(this);
        stop_listen_btn.setOnClickListener(this);
        mute.setOnClickListener(this);
    }
	@Override
    public void onClick(View v)
	{
		switch (v.getId()) {
			case R.id.start_listen_btn:
				if(mSpeechManager==null)
				{
					SetSpeechListener();
				}
				else if(!mSpeechManager.ismIsListening())
				{
					mSpeechManager.destroy();
					SetSpeechListener();
				}
				result_tv.setText("you_may_speak");

				break;
			case R.id.stop_listen_btn:
				if(mSpeechManager!=null) {
					result_tv.setText("destroied");
					mSpeechManager.destroy();
					mSpeechManager = null;
				}
				break;
			case R.id.mute:
				if(mSpeechManager!=null) {
					if(mSpeechManager.isInMuteMode()) {
						mute.setText("mute");
						mSpeechManager.mute(false);
					}
					else
					{
						mute.setText("un_mute");
						mSpeechManager.mute(true);
					}
				}
				break;
			}
	}
	private void SetSpeechListener()
    {
        mSpeechManager=new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
				@Override
				public void onResults(ArrayList<String> results)
				{
					if(results!=null && results.size()>0)
					{

						if(results.size()==1)
						{
							mSpeechManager.destroy();
							mSpeechManager = null;
							result_tv.setText(results.get(0));
						}
						else {
							StringBuilder sb = new StringBuilder();
							if (results.size() > 5) {
								results = (ArrayList<String>) results.subList(0, 5);
							}
							for (String result : results) {
								sb.append(result).append("\n");
							}
							result_tv.setText(sb.toString());
						}
					}
					else
						result_tv.setText("no_results_found");
				}
			});
    }

    @Override
    protected void onPause() {
        if(mSpeechManager!=null) {
            mSpeechManager.destroy();
            mSpeechManager=null;
        }
        super.onPause();
    }
}

class SpeechRecognizerManager {

    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;

    protected boolean mIsListening;
    private boolean mIsStreamSolo; 


    private boolean mMute=true;
    private final static String TAG="SpeechRecognizerManager";

    private onResultsReady mListener;
	
    public SpeechRecognizerManager(Context context,onResultsReady listener)
    {
        try{
            mListener=listener;
        }
        catch(ClassCastException e)
        {
            Log.e(TAG,e.toString());
        }
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
										 RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
										 context.getPackageName());
        startListening();

    }

    private void listenAgain()
    {
        if(mIsListening) {
            mIsListening = false;
            mSpeechRecognizer.cancel();
            startListening();
        }
    }


    private void startListening()
    {
        if(!mIsListening)
        {
            mIsListening = true; 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // turn off beep sound
                if (!mIsStreamSolo && mMute) {
                    mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
                    mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                }
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }

    public void destroy()
    {
        mIsListening=false;
        if (!mIsStreamSolo) {
            mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            mIsStreamSolo = true;
        }
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer=null;
        }

    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {}

        @Override
        public synchronized void onError(int error)
        {

            if(error==SpeechRecognizer.ERROR_RECOGNIZER_BUSY)
            {
                if(mListener!=null) {
                    ArrayList<String> errorList=new ArrayList<String>(1);
                    errorList.add("ERROR RECOGNIZER BUSY");
                    if(mListener!=null)
						mListener.onResults(errorList);
                }
                return;
            }

            if(error==SpeechRecognizer.ERROR_NO_MATCH)
            {
                if(mListener!=null)
                    mListener.onResults(null);
            }

            if(error==SpeechRecognizer.ERROR_NETWORK)
            {
                ArrayList<String> errorList=new ArrayList<String>(1);
                errorList.add("STOPPED LISTENING");
                if(mListener!=null)
                    mListener.onResults(errorList);
            }
            Log.d(TAG, "error = " + error);
            new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						listenAgain();
					}
				},100);


        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params) {}

        @Override
        public void onResults(Bundle results)
        {
            if(results!=null && mListener!=null)
				mListener.onResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
            listenAgain();

        }

        @Override
        public void onRmsChanged(float rmsdB) {}

    }

    public boolean ismIsListening() {
        return mIsListening;
    }


    public interface onResultsReady
    {
        public void onResults(ArrayList<String> results);
    }

    public void mute(boolean mute)
    {
        mMute=mute;
    }

    public boolean isInMuteMode()
    {
        return mMute;
    }
}
