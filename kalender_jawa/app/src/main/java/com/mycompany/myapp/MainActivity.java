package com.mycompany.myapp;

import java.io.StreamCorruptedException;  
import java.util.Calendar;  
import java.util.Date;  
import java.util.GregorianCalendar;  
import java.util.Locale;  

import android.app.Activity;  
import android.app.DatePickerDialog;  
import android.app.Dialog;  
import android.graphics.Color;  
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.ViewGroup.LayoutParams;  
import android.widget.AdapterView;  
import android.widget.ArrayAdapter;  
import android.widget.Button;  
import android.widget.DatePicker;  
import android.widget.EditText;  
import android.widget.GridView;  
import android.widget.ListView;  
import android.widget.TableLayout;  
import android.widget.TableRow;  
import android.widget.TextView;  
import android.widget.Toast;  
import android.widget.AdapterView.OnItemClickListener;  

public class MainActivity extends Activity {  
    private TextView mDateDisplay;  
    private Button mPickDate;  
    private int mYear;  
    private int mMonth;  
    private int mDay;  
    private int sDay;  
    private Calendar c;  
    static final int DATE_DIALOG_ID = 0;  

    static final String[] bulanmasehi = new String[]{  
        "Januari", "Februari", "Maret", "April","Mei","Juni",  
        "Juli","Agustus","September","Oktober","November","Desember"  
    };  
    static final String[] harimasehi = new String[]{  
        "Minggu","Senin", "Selasa","Rabu","Kamis","Jum'at","Sabtu"  
    };  
    static final String[] harimasehi_singkat = new String[]{  
        "Min","Sen", "Sel","Rab","Kam","Jum","Sab"  
    };  

    //String[] harijawa = {"Ahad", "Senen", "Selasa", "Rebo", "Kemis","Jemuah", "Setu"};  


    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.main);  

        // capture our View elements  
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);  
        mPickDate = (Button) findViewById(R.id.pickDate);  


        mPickDate.setOnClickListener(new View.OnClickListener() {  
				public void onClick(View v) {  
					showDialog(DATE_DIALOG_ID);  
				}  
			});  


        // get the current date  
        c = Calendar.getInstance();  
        mYear = c.get(Calendar.YEAR);  
        mMonth = c.get(Calendar.MONTH);  
        mDay = c.get(Calendar.DAY_OF_MONTH);  
        sDay = c.get(Calendar.DAY_OF_WEEK);  

        // display the current date (this method is below)  
        updateDisplay(mYear, mMonth, mDay);  

    }  

	// updates the date in the TextView  
    private void updateDisplay(int y, int m, int d) 
	{  
        final Kalender kal = new Kalender();  
        final int yy = y;  
        final int mm = m;  
        String[] jawa = kal.MasehiToJawa(y, m, d);// (mDay, mMonth, mYear);  
        mDateDisplay.setText(  
			new StringBuilder()  
			.append(jawa[0]+" ")  
			.append(jawa[4]+", ")  
			.append(jawa[2]+" ")  
			.append(jawa[1]+" ")  
			.append(jawa[3]+" ")  
			.append("(" +jawa[5]+" ")  
			.append(jawa[6]+" ")  
			.append(jawa[7]+" H.)")  

		);  

        TableLayout table = (TableLayout)findViewById(R.id.tableLayout);  
        table.removeAllViews();  
        //header  
        TableRow row = new TableRow(this);  
        row.setId(100);  
        for(int hari = 0;hari<7;hari++)
		{  
            TextView tv = new TextView(this);  
            tv.setId(hari+101);  
            tv.setText(harimasehi_singkat[hari]);  
            tv.setPadding(10, 2, 10, 2);  
            row.addView(tv);  
		}  
        table.addView(row);  
        //data  
        int idx = 1;  
        int startday = Integer.parseInt(jawa[8]);  
        int endday = total_hari(y, m);  
        
        int jumlah_baris = 6;  
        if ( (((endday - startday) % 6) == 0) || (m==1)) 
			jumlah_baris = 5;  

        StringBuilder sb = new StringBuilder();  

        for(int i = 0; i < jumlah_baris;i++)
		{  
            TableRow r = new TableRow(this);  
            r.setId(110+i);  
            for(int j=1;j<8;j++)
			{  
                final Button b = new Button(this);  

                b.setId(idx+180);  
                int tgl = (j+idx)-startday;  
                Boolean nocontent = true;  
                if(idx==1) 
				{  
                    if(j < startday)
						nocontent = true;
					else
						nocontent = false;  
                } 
				else
				{  
                    if(tgl <= endday)
						nocontent = false;
					else 
						nocontent = true;  
                }  

                if(!nocontent)
				{  
                    b.setText(String.valueOf(tgl));  
                    if(tgl==d)b.setBackgroundColor(Color.GRAY);  
                    jawa = kal.MasehiToJawa(y, m, tgl);  

                    sb = TanggalPenting(jawa, sb);  
                    TextView tvx = (TextView)findViewById(R.id.legendDisplay);        
                    tvx.setText(sb);  
                }     
                else 
				{  
                    b.setText(" ");  
                    b.setVisibility(View.INVISIBLE);  
                }  

                //t.setPadding(10, 2, 10, 2);  
                r.addView(b);  
                b.setOnClickListener(new OnClickListener() {  
						@Override  
						public void onClick(View v) {  
							if(b.getText()!=" ") {  
								int dd = Integer.parseInt(b.getText().toString());  
								String[] jawa = kal.MasehiToJawa(yy, mm, dd);  
								String pesan = jawa[0]+" "+jawa[4]+", "+jawa[5]+" "+jawa[6]+" "+jawa[7];  
								Toast.makeText(getApplicationContext(),pesan, Toast.LENGTH_SHORT).show();  
							}     
						}  
					});  
            }//end for j  
            idx +=7;  
            table.addView(r);  
        }//end for i  
    }  

    private int total_hari(int y, int m)
	{  
        int total = 0;  
        int jumlah_hari[] = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};  
        total = jumlah_hari[m];  
        if(m == 1)
		{  
            if ((y % 400 == 0) || ((y % 4 == 0) && (y % 100 != 0)))  
            {  
                total = 29;  
            }  
        }  
        return total;  
    }  

    private StringBuilder TanggalPenting(String[] kal, StringBuilder sb)
	{  
        if(kal[5].equals("1") )  
            sb.append(""+kal[2]+" "+kal[1]+" : "+kal[5]+ " "+kal[6]+" "+kal[7]);  

        if(kal[6].equalsIgnoreCase("Rejeb") && kal[5].equals("27") )  
            sb.append(""+kal[2]+" "+kal[1]+" : Israa' Mi'raj");  

        if(kal[6].equalsIgnoreCase("Mulud") && kal[5].equals("12") )  
            sb.append(""+kal[2]+" "+kal[1]+" : Maulud Nabi");  

        if(kal[6].equalsIgnoreCase("Pasa") && kal[5].equals("17") )  
            sb.append(""+kal[2]+" "+kal[1]+" : Nuzulul Qur'an");  

        if(kal[6].equalsIgnoreCase("Pasa") && kal[5].equals("21") )  
            sb.append(""+kal[2]+" "+kal[1]+" : Awal Lailatul Qadar");  

        if(kal[6].equalsIgnoreCase("Besar") && kal[5].equals("10") )  
            sb.append(""+kal[2]+" "+kal[1]+" : Idhul Adha");  

        if(kal[0].equalsIgnoreCase("Minggu") && kal[4].equalsIgnoreCase("Wage"))  
            sb.append(""+kal[2]+" "+kal[1]+" : Minggu Wage Puasa Weton");  

        return sb;  
    }  
	// the callback received when the user "sets" the date in the dialog  
    private DatePickerDialog.OnDateSetListener mDateSetListener =  
	new DatePickerDialog.OnDateSetListener()
	{  

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{  
			mYear = year;  
			mMonth = monthOfYear;  
			mDay = dayOfMonth;  

			c = Calendar.getInstance();  
			c.set(Calendar.YEAR, mYear);  
			c.set(Calendar.MONTH, mMonth);  
			c.set(Calendar.DAY_OF_MONTH, mDay);  

			updateDisplay(mYear, mMonth, mDay);  
		}  
	};  
	@Override  
	protected Dialog onCreateDialog(int id) 
	{  
		switch (id) {  
			case DATE_DIALOG_ID:  
				return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);  
		}  
		return null;  
	}  
}  

