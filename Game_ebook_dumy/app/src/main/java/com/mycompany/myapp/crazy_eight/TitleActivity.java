package com.mycompany.myapp.crazy_eight;
import android.app.*;
import android.os.*;
import android.view.*;

public class TitleActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		TitleView tView = new TitleView(this);
		
		// menonaktifkan layar timeout untuk melihat
        tView.setKeepScreenOn(true);
		
		//fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
        getWindow().setFlags(
		WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);	
        setContentView(tView);
	}
	
}
