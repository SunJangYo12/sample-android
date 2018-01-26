package com.mycompany.myapp;


import java.util.ArrayList;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.widget.ArrayAdapter;

public class VoiceHelper implements RecognitionListener {
	MainActivity mVoiceRecognition;

	public VoiceHelper(MainActivity instance) {
		mVoiceRecognition = instance;
	}
	public void onResults(Bundle data) {
		ArrayList<String> matches = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		mVoiceRecognition.output.setText(matches.get(0));
	}

	public void onBeginningOfSpeech() {
		mVoiceRecognition.output.setText("Sounding good!");
	}
	public void onBufferReceived(byte[] buffer) {
		//Log.d(TAG, "onBufferReceived");
	}
	public void onEndOfSpeech() {
		//Log.d(TAG, "onEndofSpeech");
		mVoiceRecognition.output.setText("Tunggu ...");
	}
	public void onError(int error) {
		//Log.d(TAG, "error " + error);		
		mVoiceRecognition.output.setText("error " + error);
	}
	public void onEvent(int eventType, Bundle params) {
		//Log.d(TAG, "onEvent " + eventType);
	}
	public void onPartialResults(Bundle partialResults) {
		//Log.d(TAG, "onPartialResults");
	}
	public void onReadyForSpeech(Bundle params) {
		//Log.d(TAG, "onReadyForSpeech");
	}		
	public void onRmsChanged(float rmsdB) {
		//Log.d(TAG, "onRmsChanged");
	}
}
