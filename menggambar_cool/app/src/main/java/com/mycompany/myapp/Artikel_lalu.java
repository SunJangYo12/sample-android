package com.mycompany.myapp;

/*Saya sudah terbiasa menggunakan Graphics
dan Graphics2D untuk menggambar secara bebas
di Java Swing. Lalu, bagaimana dengan menggambar
di aplikasi Android? Saya dapat menggunakan 
Canvas untuk keperluan tersebut. Mirip seperti 
Graphics di Swing, Canvas di Android juga 
menyediakan method seperti drawLine(), drawRect(),
drawPoint(), drawText(), dan sebagainya untuk
menghasilkan gambar.*/

import android.app.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import android.content.*;

public class Artikel_lalu extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
	}
	
}

class PlotterView extends View 
{
    float xmin = -10, xmax = 10, ymin = -10, ymax = 10;
    float step;
    Paint warnaLatar, warnaGaris;

    PlotterView(Context context) {
        super(context);
        warnaLatar = new Paint();
		warnaLatar.setColor(Color.BLACK);
        warnaGaris = new Paint();
		warnaGaris.setColor(Color.YELLOW);
    }

    void setWarnaLatar(int warna) {
        warnaLatar.setColor(warna);
        invalidate();
    }

    void setWarnaGaris(int warna) {
        warnaGaris.setColor(warna);
        invalidate();
    }
	/*Pada method setWarnaLatar() dan 
	setWarnaGaris(), saya memanggil
	invalidate() agar View ini digambar ulang 
	setelah terdapat perubahan nilai warna 
	latar dan warga garis. Saya akan membuat
	sumbu x selalu tetap sementara nilai sumbu y
	bisa bervariasi tergantung pada ukuran layar.
	Untuk itu, saya perlu menghitung ulang nilai
	skala, nilai y minimal dan nilai y maksimal
	setiap kali terdapat perubahan ukuran komponen.
	Saya dapat melakukannya dengan men-override 
	method onSizeChanged() seperti pada kode
	program berikut ini:*/
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		step = w / (xmax - xmin);
		int jumlahY = h / step;
		ymax = jumlahY / 2;
		ymin = -ymax;
	}
	/*Pekerjaan utama dalam menggambar adalah 
	men-override method onDraw(). Sebagai
	contoh, saya membuat kode program yang 
	menggambar sumbu x dan sumbu y seperti:*/
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isInEditMode()) {
			canvas.drawColor(Color.BLACK);
		} else {
			canvas.drawPaint(warnaLatar);
		}

		// Gambar sumbu
		float x0 = translateX(0);
		float y0 = translateY(0);
		canvas.drawLine(0, y0, getWidth(), y0, warnaGaris);
		for (float x=xmin; x <= xmax; x++) {
			canvas.drawText(x.intValue().toString(), translateX(x), (float) (y0 + 12), warnaGaris);
		}
		canvas.drawLine(x0, 0, x0, getHeight(), warnaGaris);
		for (float y=ymax; y >= ymin; y--) {
			if (y != 0) {
				canvas.drawText(y.intValue().toString(), x0, translateY(y), warnaGaris);
			}
		}
	}

	private float translateX(float x) {
		if (x == 0) {
			x = (xmax < 0)? xmax: ((xmin > 0)? xmin: 0);
		}
		return ((x - xmin) * step) - 12;
	}

	private float translateY(float y) {
		if (y == 0) {
			y = (ymax < 0)? ymax: ((ymin > 0? ymin: 0))
		}
		if (ymax < 0) {
			return (ymax - y) * step;
		} else {
			return (getHeight() - ((y - ymin) * step)) - 12;
		}
	}
}
