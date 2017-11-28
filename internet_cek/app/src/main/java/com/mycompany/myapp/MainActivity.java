package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.*;
import android.widget.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		if(checkInternetConnection(this))
		{
			Toast.makeText(this, "coneck", Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this, "tidak coneck", Toast.LENGTH_LONG).show();
			
		}
    }
	
	public boolean checkInternetConnection(Context context)
	{
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
		{
			return false;
		} 
		else 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState()==NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
