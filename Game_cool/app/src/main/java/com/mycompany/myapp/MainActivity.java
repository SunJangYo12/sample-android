package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import com.mycompany.myapp.alien.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Button ali = (Button)findViewById(R.id.alien);
		ali.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Intent i = new Intent(MainActivity.this, AlienActivity.class);
				startActivity(i);
			}
		});
    }
}
