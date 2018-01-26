/*Pada artikel Menggambar Dengan Canvas Di
Android, saya menggambar bebas dengan membuat
sebuah turunan baru dari View. Teknik tersebut 
tepat dipakai untuk gambar yang statis. Untuk 
gambar yang perlu diperbaharui secara cepat
dan terus menerus (misalnya pada animasi game), 
Android SDK menyediakan SurfaceView. Salah satu
kelebihan SurfaceView adalah ia dapat di-update 
secara cepat kapan saja, sementara pada View
biasa, pemanggilan invalidate() tidak selalu 
segera memperbaharui tampilan. Walaupun lebih 
cepat, SurfaceView lebih rumit dan membutuhkan 
lebih banyak resource dibandingkan View biasa.
*/

package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.graphics.*;
import java.util.*;
import android.view.*;
import android.content.*;
import android.util.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(new AnimasiView(this, null));
    }
}
class AnimasiView extends SurfaceView implements SurfaceHolder.Callback
{
	/*Sekarang, saya siap untuk membuat 
	 sebuah turunan dari SurfaceHolder yang 
	 saya beri nama AnimasiView.java dengan
	 isi seperti berikut ini:*/
    private AnimasiThread animasiThread;

    public AnimasiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        animasiThread = new AnimasiThread(holder, getContext());
        animasiThread.setRunning(true);
        animasiThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        animasiThread.setWidth(width);
        animasiThread.setHeight(height);
        animasiThread.buatLingkaran();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        animasiThread.setRunning(false);
        try {
            animasiThread.join();
        } catch (InterruptedException e) {}
    }

	/*Pada sebuah SurfaceView, saya dapat 
	 memanggil getHolder() untuk memperoleh
	 SurfaceHolder yang diasosiasikan
	 dengannya. Selain itu, saya juga
	 mendaftarkan SurfaceHolder.Callback 
	 dengan memanggil addCallback() milik 
	 SurfaceHolder. Method surfaceCreated() 
	 akan dipanggil pada saat SurfaceView 
	 sudah selesai dibuat dan saya sudah boleh 
	 mulai menggambar. Method surfaceChanged() 
	 akan dipanggil bila terdapat perubahan 
	 ukuran SurfaceView. Method surfaceDestroyed() 
	 akan dipanggil pada saat SurfaceView dihapus 
	 (tidak dibutuhkan lagi).*/

}

class AnimasiThread extends Thread {

	/*Salah satu hal yang unik pada 
	 SurfaceView adalah ia tidak harus
	 selalu diperbaharui pada UI Thread. 
	 Saya boleh memanipulasi layar dari
	 sebuah thread yang berbeda melalui 
	 SurfaceHolder dari SurfaceView tersebut.
	 Sebagai contoh, saya membuat sebuah 
	 class yang mewakili thread baru dengan
	 nama AnimasiThread.java yang isinya 
	 seperti berikut ini
	 */
    private SurfaceHolder surfaceHolder;
    private Context context;
    private boolean running = false;
    private List<Lingkaran> lingkarans;
    private int width, height;
    private final Object lock = new Object();

    public AnimasiThread(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        lingkarans = new ArrayList<>();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void buatLingkaran() {
        synchronized (lock) {
            lingkarans.clear();
            for (int i = 0; i < 100; i++) {
                lingkarans.add(new Lingkaran(width, height));
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            Canvas c = null;
            try {
                c = surfaceHolder.lockCanvas();
                if (running) {
                    c.drawColor(Color.BLACK);
                    synchronized (lock) {
                        for (Lingkaran lingkaran : lingkarans) {
                            lingkaran.gambar(c);
                            lingkaran.gerak();
                            if (lingkaran.getY() > height) {
                                lingkaran.reset();
                            }
                        }
                    }
                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
	/*Untuk membuat class AnimasiThread, 
	 dibutuhkan sebuah SurfaceHolder dan 
	 Context yang diperoleh dari SurfaceView.
	 Bagian yang paling penting dari class 
	 ini adalah method run(). Saya menggunakan 
	 lockCanvas() dari SurfaceHolder untuk 
	 memperoleh sebuah Canvas. Setelah selesai 
	 menggambar dengan Canvas, saya wajib 
	 memanggil unlockCanvasAndPost() dari 
	 SurfaceHolder.*/
}



class Lingkaran {

    private float x, y;
    private float kecepatan;
    private float ukuran;
    private Paint paint;

    public Lingkaran(int xmax, int ymax) {
        Random random = new Random();
        x = random.nextFloat() * xmax;
        y = random.nextFloat() * ymax;
        kecepatan = 0.1f;
        paint = new Paint();
        paint.setColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        ukuran = 10.0f;
    }

    public void gerak() {
        y += kecepatan;
        kecepatan += 0.1f;
    }

    public void reset() {
        y = 0;
        kecepatan = 0.1f;
    }

    public void gambar(Canvas c) {
        c.drawCircle(x, y, ukuran, paint);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
/*Class Lingkaran menyimpan informasi posisi
dan kecepatan untuk sebuah lingkaran yang
ada di layar. Class ini juga menyediakan
method gambar() untuk menggambar dirinya
pada sebuah Canvas.*/
}


