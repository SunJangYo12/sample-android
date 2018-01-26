package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.media.*; 
import android.content.*;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.AudioFormat;
import java.util.*;



public class MainActivity extends Activity implements View.OnClickListener
{
	private TextView output;
	private Switch aSwitch;
	private int captureSize;
	private AudioRecord audioRecord;
	private boolean isRunning = false;
	static int j;
	private Audio audio;
	public static String dataS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		RelativeLayout layout = new RelativeLayout(this);
		aSwitch = new Switch(this);
		aSwitch.setId(View.generateViewId());
		aSwitch.setTextOn("Rekam");
		aSwitch.setEnabled(true);
		aSwitch.setOnClickListener(this);
		layout.addView(aSwitch);
		
		output = new TextView(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, aSwitch.getId());
		output.setLayoutParams(lp);
		layout.addView(output);
		setContentView(layout);
		captureSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
									  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, captureSize);
									  
		audio = new Audio();
	
	}
	@Override
	public void onClick(View v) 
	{
		Toast.makeText(this, ""+dataS, Toast.LENGTH_LONG).show();
		if (aSwitch.isChecked()) 
		{
			new recorderThread().start();
		} 
		else 
		{
			//audio.stop();
			
		}
	}
	
	// Audio
    protected class Audio implements Runnable
    {
        private static final String TAG = "Spectrum";

        protected int input;
        protected int sample;
        protected boolean lock;
        protected boolean fill;

        // Data
        protected double frequency;
        protected double fps;

        private AudioRecord audioRecord;

        private static final int OVERSAMPLE = 4;
        private static final int SAMPLES = 4096;
        private static final int RANGE = SAMPLES / 2;
        private static final int STEP = SAMPLES / OVERSAMPLE;

        private static final int N = 4;
        private static final int M = 16;

        private static final double MIN = 0.5;
        private static final double expect = 2.0 * Math.PI * STEP / SAMPLES;

        private long counter;

        private Thread thread;
        private short data[];
        private double buffer[];

        private double xr[];
        private double xi[];

        protected double xa[];

        private double xp[];
        private double xf[];


        // Constructor
        protected Audio()
        {
            data = new short[STEP];
            buffer = new double[SAMPLES];

            xr = new double[SAMPLES];
            xi = new double[SAMPLES];

            xa = new double[RANGE];
            xp = new double[RANGE];
            xf = new double[RANGE];
        }

        // Start audio
        protected void start()
        {
            // Start the thread
            thread = new Thread(this, "Audio");
            thread.start();
        }

        // Run
        @Override
        public void run()
        {
            processAudio();
        }

        // Stop
        protected void stop()
        {
            // Stop and release the audio recorder
            cleanUpAudioRecord();

            Thread t = thread;
            thread = null;

            // Wait for the thread to exit
            while (t != null && t.isAlive())
                Thread.yield();
        }

        // Stop and release the audio recorder
        private void cleanUpAudioRecord()
        {
            if (audioRecord != null &&
				audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
            {
                try
                {
                    if (audioRecord.getRecordingState() ==
                        AudioRecord.RECORDSTATE_RECORDING)
                        audioRecord.stop();

                    audioRecord.release();
                }

                catch (Exception e) {}
            }
        }

        // Process Audio
        protected void processAudio()
        {
            // Assume the output sample will work on the input as
            // there isn't an AudioRecord.getNativeInputSampleRate()
            sample =
                AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

            // Calculate fps
            fps = (double)sample / SAMPLES;

            // Get buffer size
            int size =
                AudioRecord.getMinBufferSize(sample,
                                             AudioFormat.CHANNEL_IN_MONO,
                                             AudioFormat.ENCODING_PCM_16BIT);
            // Give up if it doesn't work
            if (size == AudioRecord.ERROR_BAD_VALUE ||
				size == AudioRecord.ERROR ||
				size <= 0)
            {
                runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							output.setText("eror");
						}
					});

                thread = null;
                return;
            }

            // Create the AudioRecord object
            try
            {
                audioRecord =
                    new AudioRecord(input, sample,
                                    AudioFormat.CHANNEL_IN_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT,
                                    size);
            }

            // Exception
            catch (Exception e)
            {
                runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
							output.setText("eror create audio");
                        }
                    });

                thread = null;
                return;
            }

            // Check audiorecord
            if (audioRecord == null)
            {
                runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							output.setText("eror cek audio");
						}
					});

                thread = null;
                return;
            }

            // Check state
            int state = audioRecord.getState();

            if (state != AudioRecord.STATE_INITIALIZED)
            {
                runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							output.setText("eror state");
							
						}
					});

                audioRecord.release();
                thread = null;
                return;
            }

            // Start recording
            audioRecord.startRecording();

            // Max data
            double dmax = 0.0;

            // Continue until the thread is stopped
            while (thread != null)
            {
                // Read a buffer of data
                size = audioRecord.read(data, 0, STEP);

                // Stop the thread if no data or error state
                if (size <= 0)
                {
                    thread = null;
                    break;
                }

                // Move the main data buffer up
                System.arraycopy(buffer, STEP, buffer, 0, SAMPLES - STEP);

                for (int i = 0; i < STEP; i++)
                    buffer[(SAMPLES - STEP) + i] = data[i];

                // Maximum value
                if (dmax < 4096.0)
                    dmax = 4096.0;

                // Calculate normalising value
                double norm = dmax;

                dmax = 0.0;

                // Copy data to FFT input arrays
                for (int i = 0; i < SAMPLES; i++)
                {
                    // Find the magnitude
                    if (dmax < Math.abs(buffer[i]))
                        dmax = Math.abs(buffer[i]);

                    // Calculate the window
                    double window =
                        0.5 - 0.5 * Math.cos(2.0 * Math.PI *
                                             i / SAMPLES);

                    // Normalise and window the input data
                    xr[i] = buffer[i] / norm * window;
                }

                // do FFT
                fftr(xr, xi);

                // Process FFT output
                for (int i = 1; i < RANGE; i++)
                {
                    double real = xr[i];
                    double imag = xi[i];

                    xa[i] = Math.hypot(real, imag);

                    // Do frequency calculation
                    double p = Math.atan2(imag, real);
                    double dp = xp[i] - p;

                    xp[i] = p;

                    // Calculate phase difference
                    dp -= i * expect;

                    int qpd = (int)(dp / Math.PI);

                    if (qpd >= 0)
                        qpd += qpd & 1;

                    else
                        qpd -= qpd & 1;

                    dp -=  Math.PI * qpd;

                    // Calculate frequency difference
                    double df = OVERSAMPLE * dp / (2.0 * Math.PI);

                    // Calculate actual frequency from slot frequency plus
                    // frequency difference and correction value
                    xf[i] = i * fps + df * fps;
                }

                // Do a full process run every N
                if (++counter % N != 0)
                    continue;

                // Check display lock
                if (lock)
                    continue;

                

                // Update frequency and dB every M
                if (counter % M != 0)
                    continue;

                // Maximum FFT output
                double max = 0.0;

                // Find maximum value
                for (int i = 1; i < RANGE; i++) 
                {
                    if (xa[i] > max)
                    {
                        max = xa[i];
                        frequency = xf[i];
                    }
                }

                // Level
                double level = 0.0;

                for (int i = 0; i < STEP; i++)
                    level += ((double)data[i] / 32768.0) *
						((double)data[i] / 32768.0);

                level = Math.sqrt(level / STEP) * 2.0;

                double dB = Math.log10(level) * 20.0;

                if (dB < -80.0)
                    dB = -80.0; 

                // Update frequency and dB display
                if (max > MIN)
                {
                    final String s = String.format(Locale.getDefault(),
                                                   "%1.1f", frequency);
				    
					if (frequency > 70){
						dataS = "zzzzzzzz";
					}
                    output.post(new Runnable()
						{
							@Override
							public void run()
							{
								
								output.setText(s+" "+dataS);
								dataS = ""+frequency;
							}
						});
                }

                else 
                {
                    frequency = 0.0;
                    final String s = String.format(Locale.getDefault(),
                                                   "%1.1fdB", dB);
                    output.post(new Runnable()
						{
							@Override
							public void run()
							{
								output.setText(s);
							}
						});
                }
            }

            // Stop and release the audio recorder
            cleanUpAudioRecord();
        }

        // Real to complex FFT, ignores imaginary values in input array
        private void fftr(double ar[], double ai[])
        {
            final int n = ar.length;
            final double norm = Math.sqrt(1.0 / n);

            for (int i = 0, j = 0; i < n; i++)
            {
                if (j >= i)
                {
                    double tr = ar[j] * norm;

                    ar[j] = ar[i] * norm;
                    ai[j] = 0.0;

                    ar[i] = tr;
                    ai[i] = 0.0;
                }

                int m = n / 2;
                while (m >= 1 && j >= m)
                {
                    j -= m;
                    m /= 2;
                }
                j += m;
            }

            for (int mmax = 1, istep = 2 * mmax; mmax < n;
			mmax = istep, istep = 2 * mmax)
            {
                double delta = Math.PI / mmax;
                for (int m = 0; m < mmax; m++)
                {
                    double w = m * delta;
                    double wr = Math.cos(w);
                    double wi = Math.sin(w);

                    for (int i = m; i < n; i += istep)
                    {
                        int j = i + mmax;
                        double tr = wr * ar[j] - wi * ai[j];
                        double ti = wr * ai[j] + wi * ar[j];
                        ar[j] = ar[i] - tr;
                        ai[j] = ai[i] - ti;
                        ar[i] += tr;
                        ai[i] += ti;
                    }
                }
            }
        }
    }
	
	class CaptureThread extends Thread 
	{
		@Override
		public void run() 
		{
			final short[] buffer = new short[captureSize];
			while(isRunning) 
			{
				audioRecord.read(buffer, 0, captureSize);
				final StringBuilder text = new StringBuilder();
				
				for (int i=0; i<captureSize; i++)
				{
					text.append(buffer[i]);
					text.append(' ');
					j = i;
				}
				output.postDelayed(new Runnable() 
				{
						@Override
						public void run() 
						{
							output.setText(text.toString());
							if (buffer[j] > 1000)
							{
								isRunning = false;
								Toast.makeText(getApplicationContext(), ""+buffer[j], Toast.LENGTH_SHORT).show();
							}
						}
					}, 100);
			}
		}
	}
	
	class recorderThread extends Thread {
		public boolean recording; //variable to start or stop recording
		public int frequency; //the public variable that contains the frequency value "heard", it is updated continually while the thread is running.
		public recorderThread () {
		}

		@Override
		public void run() {
			AudioRecord recorder;
			int numCrossing,p;
			short audioData[];
			int bufferSize;

			bufferSize=AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_CONFIGURATION_MONO,
													AudioFormat.ENCODING_PCM_16BIT)*3; //get the buffer size to use with this audio record

			recorder = new AudioRecord (AudioSource.MIC,8000,AudioFormat.CHANNEL_CONFIGURATION_MONO,
										AudioFormat.ENCODING_PCM_16BIT,bufferSize); //instantiate the AudioRecorder

			recording=true; //variable to use start or stop recording
			audioData = new short [bufferSize]; //short array that pcm data is put into.


			while (recording) {  //loop while recording is needed
				if (recorder.getState()==android.media.AudioRecord.STATE_INITIALIZED) // check to see if the recorder has initialized yet.
					if (recorder.getRecordingState()==android.media.AudioRecord.RECORDSTATE_STOPPED)
						recorder.startRecording();  //check to see if the Recorder has stopped or is not recording, and make it record.

					else {

						recorder.read(audioData,0,bufferSize); //read the PCM audio data into the audioData array

						//Now we need to decode the PCM data using the Zero Crossings Method

						numCrossing=0; //initialize your number of zero crossings to 0
						for (p=0;p<bufferSize/4;p+=4) {
							if (audioData[p]>0 && audioData[p+1]<=0) numCrossing++;
							if (audioData[p]<0 && audioData[p+1]>=0) numCrossing++;
							if (audioData[p+1]>0 && audioData[p+2]<=0) numCrossing++;
							if (audioData[p+1]<0 && audioData[p+2]>=0) numCrossing++;
							if (audioData[p+2]>0 && audioData[p+3]<=0) numCrossing++;
							if (audioData[p+2]<0 && audioData[p+3]>=0) numCrossing++;
							if (audioData[p+3]>0 && audioData[p+4]<=0) numCrossing++;
							if (audioData[p+3]<0 && audioData[p+4]>=0) numCrossing++;
						}//for p

						for (p=(bufferSize/4)*4;p<bufferSize-1;p++) {
							if (audioData[p]>0 && audioData[p+1]<=0) numCrossing++;
							if (audioData[p]<0 && audioData[p+1]>=0) numCrossing++;
						}



						frequency=(8000/bufferSize)*(numCrossing/2);  // Set the audio Frequency to half the number of zero crossings, times the number of samples our buffersize is per second.
						output.postDelayed(new Runnable() 
							{
								@Override
								public void run() 
								{
									output.setText(""+frequency);
								
								}
							}, 100);
					}//else recorder started

			} //while recording

		
		}//run


	}//recorderThread

	
}

//Author xdebugx.net (Jeremiah McLeod) 8-8-2010




