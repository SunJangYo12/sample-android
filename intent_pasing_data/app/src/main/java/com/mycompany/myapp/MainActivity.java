package com.mycompany.myapp;


import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Hafizh Herdi on 1/19/2017.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final EditText etData1 = (EditText) findViewById(R.id.et_data1);
        final EditText etData2 = (EditText) findViewById(R.id.et_data2);
        Button btSubmit = (Button) findViewById(R.id.bt_submit);
        Button btSubmitIntent = (Button) findViewById(R.id.bt_submitintent);

        btSubmit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					/**
					 * Passing data menggunakan Bundle
					 */

					Bundle bundle = new Bundle();
					bundle.putString("data1", etData1.getText().toString());
					bundle.putString("data2", etData2.getText().toString());
					Intent intent = new Intent(MainActivity.this, SecondActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});

        btSubmitIntent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					/**
					 * Passing data via Intent
					 */ 

					Intent intent = new Intent(MainActivity.this, SecondActivity.class);
					intent.putExtra("data1", etData1.getText().toString());
					intent.putExtra("data2", etData2.getText().toString());
					startActivity(intent);
				}
			});
    }
}

