package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import com.mycompany.myapp.dasar.*;
import com.mycompany.myapp.crazy_eight.*;
import com.mycompany.myapp.whack_mole.*;

public class MainActivity extends Activity 
{
	Button mdasar, mkartu, mtikus;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		mdasar = (Button)findViewById(R.id.dasar);
		mdasar.setOnClickListener(new View.OnClickListener()
	    	{
			    public void onClick(View v){
					Intent i = new Intent(MainActivity.this, DasarActivity.class);
					startActivity(i);
		    	}
	    	});
		mkartu = (Button)findViewById(R.id.kartu);
		mkartu.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v){
					Intent i = new Intent(MainActivity.this, TitleActivity.class);
					startActivity(i);
				}
			});
		mtikus = (Button)findViewById(R.id.tikus);
		mtikus.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v){
					Intent i = new Intent(MainActivity.this, WhackActivity.class);
					startActivity(i);
				}
			});
    }
}
