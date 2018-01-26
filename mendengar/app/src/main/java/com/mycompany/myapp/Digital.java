package com.mycompany.myapp;
import android.view.*;
import android.media.*;
import android.graphics.*;
import android.content.*;
import android.app.*;
import android.widget.*;
import android.os.*;

public class Digital extends Activity implements View.OnClickListener {
	private GrafikSuara output;
	private Switch aSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		RelativeLayout layout = new RelativeLayout(this);
		aSwitch = new Switch(this);
		aSwitch.setId(View.generateViewId());
		aSwitch.setTextOn("Rekam");
		aSwitch.setOnClickListener(this);
		layout.addView(aSwitch);
		output = new GrafikSuara(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, aSwitch.getId());
		output.setLayoutParams(lp);
		layout.addView(output);
		setContentView(layout);
	}
	@Override
	protected void onPause() {
		super.onPause();
		output.selesai();
	}
	@Override
	public void onClick(View v) 
	{
		if (aSwitch.isChecked()) 
		{
			output.mulai();
			
		} 
		else 
		{
			output.selesai();
		}
	}
}

class GrafikSuara extends SurfaceView implements SurfaceHolder.Callback 
{
	private AnimasiThread animasiThread;
	private boolean ready = false;
	private int width, height;

	public GrafikSuara(Context context)
	{
		super(context);
		getHolder().addCallback(this);
	}
	public void mulai() {
		if (ready){
			animasiThread = new AnimasiThread(getHolder());
			animasiThread.setWidth(width);
			animasiThread.setHeight(height);
			animasiThread.startCapture();
		}
	}
	public void selesai() { 
		if (animasiThread != null)
		{
			animasiThread.stopCapture();
			animasiThread = null;
		}
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		ready = true;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.width = width;
		this.height = height;
		if (animasiThread != null)
		{
			animasiThread.setWidth(width);
			animasiThread.setHeight(height);
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (animasiThread != null)
		{
			animasiThread.stopCapture();
		}
	}
} // class grapic

class AnimasiThread extends Thread
{
	private SurfaceHolder surfaceHolder;
	private int captureSize;
	private AudioRecord audioRecord;
	private boolean running;
	private int width, height;
	private float midLine, scale;
	private final Paint warnaGaris;
	private final Paint warnaMerah;
	private static int getar = 0;
	
	public AnimasiThread(SurfaceHolder surfaceHolder)
	{
		this.surfaceHolder = surfaceHolder;
		this.running = false;
		captureSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
									  AudioFormat.ENCODING_PCM_16BIT, captureSize);
		warnaGaris = new Paint();
		warnaGaris.setColor(Color.GREEN);
		warnaMerah = new Paint();
		warnaMerah.setColor(Color.RED);
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
		this.midLine = height / 2;
		this.scale =  height / (float) (2 * Short.MAX_VALUE);
	}
	public void startCapture() {
		audioRecord.startRecording();
		running = true;
		start();
	}
	public void stopCapture() {
		running = false;
		audioRecord.stop();
	}
	@Override
	public void run()
	{
		final short[] buffer = new short[captureSize];
		
		while(running) 
		{
			audioRecord.read(buffer, 0, captureSize);
			Canvas c = null;
			Float lastX = null, lastY = null;
			try
			{
				c = surfaceHolder.lockCanvas();
				c.drawColor(Color.BLACK);
				for (int i = 0; i < captureSize; i++) //< ribuan
				{
					float trY = midLine - buffer[i] * scale; //= 43.57755
					if ((lastX != null) && (lastY != null))
					{
						c.drawLine(lastX, lastY, i, trY, warnaGaris);
						c.drawPoint(i, trY+300, warnaMerah);
						
					} 
					else
					{
						c.drawText("amplitudo : "+buffer[i], 10, 60, warnaGaris);
						c.drawText("frekuensi : "+buffer[i], 10, 60+40, warnaGaris);
						
					}
					lastX = (float) i;
					lastY = trY;
				}
			} 
			finally
			{
				if (c != null) 
				{
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}

	

