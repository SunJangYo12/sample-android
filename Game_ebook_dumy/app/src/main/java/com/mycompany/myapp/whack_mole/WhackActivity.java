package com.mycompany.myapp.whack_mole;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.mycompany.myapp.R;
import android.view.*;
import android.widget.*;
import android.media.*;

public class WhackActivity extends Activity 
{
	
    private Whack_SurfaceView myWhackAMoleView;
	private static final int TOGGLE_SOUND = 1;
	private boolean soundEnabled = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);	
        getWindow().setFlags 
		(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 
        setContentView(R.layout.whack_view);	
        myWhackAMoleView = (Whack_SurfaceView)findViewById(R.id.mole);      	
        myWhackAMoleView.setKeepScreenOn(true);	
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem toggleSound = menu.add(0, TOGGLE_SOUND, 0, "Toggle Sound");
        return true;
    }
	public boolean onOptionsItemSelected(MenuItem item) {	
        switch (item.getItemId()) {
			case TOGGLE_SOUND:
                String soundEnabledText = "Sound On";
        
	        	if (soundEnabled) {
		        	soundEnabled = false;
		        	soundEnabledText = "Sound Off";
					
					myWhackAMoleView.soundOn = false;	
					soundEnabledText = "Sound Off";
	         	}
	        	else {
		        	soundEnabled = true;
					
					myWhackAMoleView.soundOn = true;
		        }
	        	Toast.makeText(this, soundEnabledText, Toast.LENGTH_SHORT).show();
	        	break;     
        	}
        	return false;
       }
}

