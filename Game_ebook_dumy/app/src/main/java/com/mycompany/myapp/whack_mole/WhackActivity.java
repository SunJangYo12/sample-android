package com.mycompany.myapp.whack_mole;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.mycompany.myapp.R;
import android.view.*;
import android.widget.*;
import android.media.*;
import android.content.*;
import java.io.*;
import org.xmlpull.v1.*;
import android.database.*;

public class WhackActivity extends Activity 
{
	
    private Whack_SurfaceView myWhackAMoleView;
	private static final int TOGGLE_SOUND = 1;
	private boolean soundEnabled = true;
	
	// memori
	public static final String PREFERENCES_NAME = "MyPreferences";
	
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
		
	/*	SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);	
		soundEnabled = settings.getBoolean("soundSetting", true);	
		myWhackAMoleView.soundOn = soundEnabled;
		
		try {
			readXML();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		DatabaseAdapter db = new DatabaseAdapter(this);	
		try {
			db.open(); 	
		}catch(SQLException sqle){ 
			throw sqle;
		}
		Cursor c = db.getRecord(1);        	
		startManagingCursor(c);
		if (c.moveToFirst())
		{
			do {
				soundEnabled =  Boolean.parseBoolean((c.getString(1)));
			} while (c.moveToNext());
		}
		db.close();
		myWhackAMoleView.soundOn = soundEnabled;

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
	        	
			/*	SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);	
				SharedPreferences.Editor editor = settings.edit();	
				editor.putBoolean("soundSetting", soundEnabled);	
				editor.commit();
				
				writeXML();*/
				
				DatabaseAdapter db = new DatabaseAdapter(this);	
				try {
					db.open(); 
				}
				catch(SQLException sqle){ 
					throw sqle;
				}
				db.insertOrUpdateRecord(Boolean.toString(soundEnabled));	
				db.close();
				
				Toast.makeText(this, soundEnabledText, Toast.LENGTH_SHORT).show();
				
	        	break;     
        	}
        	return false;
       }
	   
	// Storing Data with writeXML() Method
	public void writeXML() {
		try {	
			String profileFileName = "settings";	
			FileOutputStream fOut = openFileOutput(profileFileName + ".xml", MODE_WORLD_WRITEABLE);
			
			StringBuffer profileXML = new StringBuffer();	
			profileXML.append("<sound_setting>" +  soundEnabled + "</sound_setting>\n");	
			
			OutputStreamWriter osw = new OutputStreamWriter(fOut); 	
			osw.write(profileXML.toString());	
			osw.flush();
			osw.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void readXML() throws XmlPullParserException, IOException
	{
		String tagName = "";	
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();	
		factory.setNamespaceAware(true);
		
		XmlPullParser xpp = factory.newPullParser();       
		try {        	 
			InputStream in = openFileInput("settings.xml");	
			
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(isr);
			String str;
			StringBuffer buf = new StringBuffer();
			while ((str = reader.readLine()) != null) {
				buf.append(str);
			}
			in.close(); 
			xpp.setInput(new StringReader(buf.toString()));           
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_DOCUMENT) {
				}
				else if(eventType == XmlPullParser.END_DOCUMENT) {
				}
				else if(eventType == XmlPullParser.START_TAG) {
					tagName = xpp.getName();
				} 
				else if(eventType == XmlPullParser.END_TAG) {
				}
				else if(eventType == XmlPullParser.TEXT) {          	
					if (tagName.contains("sound_setting")) {
						soundEnabled = 	Boolean.parseBoolean(xpp.getText().toString());
					}
				}
				eventType = xpp.next();
			}
		}
		catch (Exception FileNotFoundException){
			System.out.println("File Not Found");
		} 
	}
}

