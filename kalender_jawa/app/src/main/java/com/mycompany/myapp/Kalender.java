package com.mycompany.myapp;

import java.util.Calendar;  
import java.util.Date;  

public class Kalender
{  
    //utility function  

    private double intPart(double floatNum)
	{  
		if ((float)floatNum < -0.0000001)
		{  
			return (double) Math.ceil(floatNum-0.0000001);  
		}  
		return (double)Math.floor(floatNum +0.0000001);      
    }  

    private static int JGREG= 15 + 31*(10+12*1582);  
    //private static double HALFSECOND = 0.5;  


    private static double toJulian(int y, int m, int d)
	{  
		int year=y;  
		int month=m; // jan=1, feb=2,...  
		int day=d;  
		int julianYear = year;  
		if (year < 0) julianYear++;  
		int julianMonth = month;  
		if (month > 2) 
		{  
			julianMonth++;  
		}  
		else 
		{  
			julianYear--;  
			julianMonth += 13;  
		}  

		double julian = (java.lang.Math.floor(365.25 * julianYear) + java.lang.Math.floor(30.6001*julianMonth) + day + 1720995.0);  
		if (day + 31 * (month + 12 * year) >= JGREG)
		{  
			// change over to Gregorian calendar  
			int ja = (int)(0.01 * julianYear);  
			julian += 2 - ja + (0.25 * ja);  
		}  
		return java.lang.Math.floor(julian);  
	}  

    String[] MasehiToJawa(int year, int month, int day)
	{  
        double julian = toJulian(year, month, day);  
        double d=day;   
        double m=month;   
        double y=year;  
        int mYear;  
        int mMonth;  
        int mDay;  
        int sDay;  

        String[] bulanjawa = {"Sura","Sapar","Mulud","Bakdamulud","Jumadilawal","Jumadilakhir", "Rejeb","Ruwah","Pasa","Sawal","Dulkaidah","Besar"};  

        String[] bulanmasehi = new String[]{"Januari", "Februari", "Maret", "April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};  

        String[] harimasehi = new String[]{"Minggu","Senin", "Selasa","Rabu","Kamis","Jum'at","Sabtu"};  

        Calendar c = Calendar.getInstance();  
        c.set(Calendar.YEAR, year);  
        c.set(Calendar.MONTH, month);  
        c.set(Calendar.DAY_OF_MONTH, day);  

        mYear = c.get(Calendar.YEAR);  
        mMonth = c.get(Calendar.MONTH);  
        mDay = c.get(Calendar.DAY_OF_MONTH);  
        sDay = c.get(Calendar.DAY_OF_WEEK);  

        c.set(Calendar.DAY_OF_MONTH, 1);  
        int startDayofMonth = c.get(Calendar.DAY_OF_WEEK);  

        if(julian>=1937808 && julian<=536838867)
		{  
            double mPart = (m-13)/12;  
            double jd = intPart((1461*(y+4800+intPart(mPart)))/4) + intPart((367*(m-1-12*(intPart(mPart))))/12)  -  intPart((3*(intPart((y+4900+intPart(mPart))/100)))/4)+d-32075;  

            double l = jd-1948440+10632;  
            double n = intPart((l-1)/10631);  
            l = l-10631*n+354;  
            double j = (intPart((10985-l)/5316))*(intPart((50*l)/17719))+(intPart(l/5670))*(intPart((43*l)/15238));  
            l = l-(intPart((30-j)/15))*(intPart((17719*j)/50))-(intPart(j/16))*(intPart((15238*j)/43))+29;  

            m = (double)intPart((24*l)/709);  
            d = (double)l-intPart((709*m)/24);  
            y = (double)30*n+j-30;  

            /* 
             *  
			 untuk menghitung tahun jawa Be, alip, ehe dsb... 
			 double yj = y;//+512; Tahun jawa = Tahun Hijriyah + 512 
			 double i = yj; 
			 double yn=0.; 
			 if (i >= 8) { 
			 while (i > 7){ 
			 i = i - 8; 
			 yn = i; 
			 } 
			 } else { 
			 yn = i; 
			 } 
			 */  

            if(julian<=1948439) y--;  
        }  
        return new String[]
		{  
			harimasehi[sDay-1],  //hari Masehi   0  
			bulanmasehi[mMonth], //Bulan Masehi  1  
			String.valueOf(mDay), //Tgl masehi    2  
			String.valueOf(mYear),  //Thn masehi    3  
			
			HariPasaran(mYear, mMonth, mDay), //nama pasaran  4  
			String.valueOf((int)d), //Tanggal Jawa  5  
			bulanjawa[(int)m-1],   //Bulan Jawa    6  
			
			String.valueOf((int)y),  //Tahun Jawa    7  
			String.valueOf((int)startDayofMonth) //Awal hari     8  
        };  
    }  

    String HariPasaran(int year, int month, int day)
	{  
        String[] pasaran = new String[]{"Pahing", "Pon", "Wage","kliwon","Legi"};  

        Calendar tglInit = Calendar.getInstance();  
        tglInit.set(1900, 12, 1);  
		
        Calendar tglDicari = Calendar.getInstance();  
        tglDicari.set(year, month, day);  

        long miliday = 24 * 60 * 60 * 1000;  

        long tglDicariMilis = tglDicari.getTimeInMillis();  
        long tglInitMilis = tglInit.getTimeInMillis();  
        long selisih =  (tglDicariMilis-tglInitMilis)/miliday;  
        long hasil = selisih % 5;  
        return String.valueOf(pasaran[(int)hasil]);  
    }  
}//end class  
