package com.mycompany.myapp.dasar;

import android.app.*;
import android.os.*;

public class DasarActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		ViewKu vk = new ViewKu(this);
		setContentView(vk);
	}
	
}
