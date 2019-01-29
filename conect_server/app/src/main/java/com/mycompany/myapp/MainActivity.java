package com.mycompany.myapp;


/**
 * Class MainActivity
 * @version 1.0 Apr 04, 2011
 * @author Agus Haryanto (agus.superwriter@gmail.com)
 * @website http://agusharyanto.net
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.*;
import android.widget.*;
import android.content.*;

public class MainActivity extends Activity {
	private EditText txtUser;
	private EditText txtPassword;
	private EditText txtStatus;
	private Button btnLogin;
	private String surl = "https://sunjangyo12.000webhostapp.com/login.php/";
	/**
	 * Method yang dipanggil pada saat aplikaasi dijalankan
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		txtUser = (EditText) findViewById(R.id.txtUser);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtStatus = (EditText) findViewById(R.id.txtStatus);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		//daftarkagn even onClick pada btnLogin
		btnLogin.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v){
	            	readWebpage(v);
					/*txtUser.setText(""+readUsage());
					 Intent i = new Intent(MainActivity.this, ScrollActivity.class);
					 startActivity(i);*/
	            }
	        });

		String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
			"Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
			"Linux", "OS/2" };
		ListView ListView01 = (ListView)findViewById(R.id.listview);
        ListView01.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_2, values));
        ListView01.setSelected(true);
	}

	/**
	 * Method untuk Mengirimkan data keserver
	 *
	 */
	public String getRequest(String Url){
		String sret;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(Url);
        try{
            HttpResponse response = client.execute(request);
            sret= request(response);
        }
		catch(Exception ex){
			sret= "Failed Connect to server!";
        }
        return sret;

    }
	/**
	 * Method untuk Menerima data dari server
	 * @param response
	 * @return
	 */
	public static String request(HttpResponse response){
        String result = "";
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }
		catch(Exception ex){
            result = "Error";
        }
        return result;
    }

	/**
	 * Class CallWebPageTask untuk implementasi class AscyncTask
	 */
	private class CallWebPageTask extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog;
		protected Context applicationContext;

		@Override
		protected void onPreExecute() {
			this.dialog = ProgressDialog.show(applicationContext, "Login Process", "Please Wait...", true);
		}

	    @Override
	    protected String doInBackground(String... urls) {
			String response = "";
			response = getRequest(urls[0]);
			return response;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	this.dialog.cancel();
	    	txtStatus.setText(result);
	    }
	}

	public void readWebpage(View view) {
	    CallWebPageTask task = new CallWebPageTask();
	    task.applicationContext = MainActivity.this;
	    String url = surl+"?user="+txtUser.getText().toString()+"&password="+txtPassword.getText().toString();
	    task.execute(new String[] { url });

	}

	private float readUsage() {
        try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" +");  // Split on one or more spaces

			long idle1 = Long.parseLong(toks[4]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			try {
				Thread.sleep(360);
			} 
			catch (Exception e) {}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" +");

			long idle2 = Long.parseLong(toks[4]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			return (float)(cpu2 - cpu1) + ((cpu2 + idle2) - (cpu1 + idle1));

        } 
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

}
