package com.mycompany.myapp.whack_mole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.mycompany.myapp.R;
import java.util.*;
import android.graphics.*;
import android.media.*;

public class Whack_SurfaceView extends SurfaceView implements SurfaceHolder.Callback 
{
    private Context myContext;	
    private SurfaceHolder mySurfaceHolder;
    private Bitmap backgroundImg;
    private int screenW = 1;
    private int screenH = 1;
    private boolean running = false;
    private boolean onTitle = true;
    private WhackAMoleThread thread;
	
	
	// anim
	private int backgroundOrigW;
	private int backgroundOrigH;
	private float scaleW;
	private float scaleH;
	private float drawScaleW;
	private float drawScaleH;
	private Bitmap mask;
	private Bitmap mole;
	// draw
	private int mole1x, mole2x, mole3x, mole4x, mole5x, mole6x, mole7x;
	private int mole1y, mole2y, mole3y, mole4y, mole5y, mole6y, mole7y;
	// anim
	private int activeMole = 0;
	private boolean moleRising = true;
	private boolean moleSinking = false;
	private int moleRate = 5;
	private boolean moleJustHit = false;
	
	// user
	private Bitmap whack;
	private boolean whacking = false;
	private int molesWhacked = 0;
	private int molesMissed = 0;
	private int fingerX, fingerY;
	
	private Paint blackPaint;
	private static SoundPool sounds;
	private static int whackSound;
	private static int missSound;
	public boolean soundOn = true;
	
	
	// game over
	private boolean gameOver = false;
	private Bitmap gameOverDialog;

	
	public Whack_SurfaceView(Context context, AttributeSet attrs) {	
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
		
        thread = new WhackAMoleThread(holder, context,  
			new Handler() {
            @Override
			public void handleMessage(Message m) {
            }
        });
	    setFocusable(true);
    }
	
    public WhackAMoleThread getThread() {	
	    return thread;
    }

    class WhackAMoleThread extends Thread {	
	    public WhackAMoleThread(SurfaceHolder surfaceHolder, Context context, Handler handler)
     	{
	    	mySurfaceHolder = surfaceHolder;
	    	myContext = context;
	    	backgroundImg =  BitmapFactory.decodeResource(context.getResources(),R.drawable.title);
			
			backgroundOrigW = backgroundImg.getWidth();
    		backgroundOrigH = backgroundImg.getHeight();
			
			sounds = new SoundPool(5, AudioManager.STREAM_MUSIC, 0); 
			whackSound = sounds.load(myContext, R.raw.whack, 1);	
			missSound = sounds.load(myContext, R.raw.miss, 1);
    	}
	    @Override
	    public void run() {
	    	while (running) {
	     		Canvas c = null;
	     		try {
	     			c = mySurfaceHolder.lockCanvas(null);
		    		synchronized (mySurfaceHolder) {
		     			draw(c);
						if (!gameOver) {
                        	animateMoles();                    		
                    	}
			    	}
		    	} 
				finally {
		 	    	if (c != null) {
			     		mySurfaceHolder.unlockCanvasAndPost(c);
		     		} 
		    	}
		    }// while
	    }
    	private void draw(Canvas canvas) {
	    	try {
	     		canvas.drawBitmap(backgroundImg, 0, 0, null);
				if (!onTitle) {
					canvas.drawBitmap(mole, mole1x, mole1y, null);
					canvas.drawBitmap(mole, mole2x, mole2y, null);
					canvas.drawBitmap(mole, mole3x, mole3y, null);
					canvas.drawBitmap(mole, mole4x, mole4y, null);
					canvas.drawBitmap(mole, mole5x, mole5y, null);
					canvas.drawBitmap(mole, mole6x, mole6y, null);
					canvas.drawBitmap(mole, mole7x, mole7y, null);
					
					canvas.drawBitmap(mask, (int) 50*drawScaleW,(int) 450*drawScaleH, null);
					canvas.drawBitmap(mask, (int)150*drawScaleW,(int) 400*drawScaleH, null);
					canvas.drawBitmap(mask, (int)250*drawScaleW,(int) 450*drawScaleH, null);
         			canvas.drawBitmap(mask, (int)350*drawScaleW,(int) 400*drawScaleH, null);
	        		canvas.drawBitmap(mask, (int)450*drawScaleW,(int) 450*drawScaleH, null);
	         		canvas.drawBitmap(mask, (int)550*drawScaleW,(int) 400*drawScaleH, null);
		        	canvas.drawBitmap(mask, (int)650*drawScaleW,(int) 450*drawScaleH, null); 
					
					canvas.drawText("Whacked: " + Integer.toString(molesWhacked), 10, blackPaint.getTextSize()+10, blackPaint);
					canvas.drawText("Missed: " + Integer.toString(molesMissed), screenW-(int)(200*drawScaleW), blackPaint.getTextSize()+10, blackPaint);

				} // if

				if (whacking) {
					canvas.drawBitmap(whack, fingerX - (whack.getWidth()/2), fingerY -(whack.getHeight()/2), null);
				}
				if (gameOver) {
                	canvas.drawBitmap(gameOverDialog, (screenW/2) - (gameOverDialog.getWidth()/2), (screenH/2) - (gameOverDialog.getHeight()/2), null);
                }

	      	}
			catch (Exception e) {
	    	}
    	}
    	boolean doTouchEvent(MotionEvent event) {	
	    	synchronized (mySurfaceHolder) {
	     		int eventaction = event.getAction();
	     		int X = (int)event.getX();
	    		int Y = (int)event.getY();
	     		switch (eventaction ) {
	     			case MotionEvent.ACTION_DOWN:
						if (!gameOver){
							fingerX = X;
							fingerY = Y;
							if (!onTitle && detectMoleContact()) {
								whacking = true;
								molesWhacked++;

								if (soundOn) {
									AudioManager audioManager = (AudioManager)myContext.getSystemService(Context.AUDIO_SERVICE);
									float volume = (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
									sounds.play(whackSound, volume, volume, 1, 0, 1);
								}
							}
						}// game over
	     				break;
	     			case MotionEvent.ACTION_MOVE:
	    				break;
	     			case MotionEvent.ACTION_UP:
		     			if (onTitle) {
							backgroundImg = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.background);
							backgroundImg = Bitmap.createScaledBitmap(backgroundImg, screenW, screenH, true);
							
							// anim
							mask = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.mask);
							mole = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.mole);
							whack = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.whack);
							gameOverDialog = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.gameover);
							
							scaleW = (float) screenW/ (float) backgroundOrigW;
							scaleH = (float) screenH/ (float) backgroundOrigH;
							
							mask = Bitmap.createScaledBitmap(mask, (int)(mask.getWidth()*scaleW),(int)(mask.getHeight()*scaleH), true);
							mole = Bitmap.createScaledBitmap(mole, (int)(mole.getWidth()*scaleW),(int)(mole.getHeight()*scaleH), true);
							whack = Bitmap.createScaledBitmap(whack, (int)(whack.getWidth()*scaleW), (int)(whack.getHeight()*scaleH), true);
							gameOverDialog = Bitmap.createScaledBitmap(gameOverDialog,(int)(gameOverDialog.getWidth()*scaleW), (int)(gameOverDialog.getHeight()*scaleH), true);
							
							pickActiveMole();
							onTitle = false;
				    	}
						whacking = false;
						
						if (gameOver) {
							molesWhacked = 0; 
							molesMissed = 0;
							activeMole = 0;
							pickActiveMole();
							gameOver = false;
						}
						break;
		       	}// switch
	    	}
	    	return true;
     	}
    	public void setSurfaceSize(int width, int height) {                       
    		synchronized (mySurfaceHolder) {
	     		screenW = width;
	    		screenH = height;
				
	    		backgroundImg = Bitmap.createScaledBitmap(backgroundImg, width, height, true);
				
				// anim
				drawScaleW = (float) screenW / 800;           
				drawScaleH = (float) screenH / 600;    
				mole1x = (int) (55*drawScaleW);
				mole2x = (int) (155*drawScaleW);
				mole3x = (int) (255*drawScaleW);
				mole4x = (int) (355*drawScaleW);
				mole5x = (int) (455*drawScaleW);
				mole6x = (int) (555*drawScaleW);
				mole7x = (int) (655*drawScaleW);
				mole1y = (int) (475*drawScaleH);
				mole2y = (int) (425*drawScaleH);
				mole3y = (int) (475*drawScaleH);
				mole4y = (int) (425*drawScaleH);
				mole5y = (int) (475*drawScaleH);
				mole6y = (int) (425*drawScaleH);
				mole7y = (int) (475*drawScaleH);
	    	} // sicronized
			
			blackPaint = new Paint();
			blackPaint.setAntiAlias(true);
			blackPaint.setColor(Color.BLACK);
			blackPaint.setStyle(Paint.Style.STROKE);
			blackPaint.setTextAlign(Paint.Align.LEFT);
			blackPaint.setTextSize(drawScaleW*30);
    	}
		
    	public void setRunning(boolean b) {
	    	running = b;
    	}
		
		// anim
		private void pickActiveMole() {
			activeMole = new Random().nextInt(7) + 1;
			moleRising = true;
			moleSinking = false;
			moleJustHit = false;

			if (!moleJustHit && activeMole > 0) {
				molesMissed++;

				if (soundOn) {
					AudioManager audioManager = (AudioManager)myContext.getSystemService(Context.AUDIO_SERVICE);
					float volume = (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					sounds.play(missSound, volume, volume, 1, 0, 1);
				}
			}
			if (molesMissed >= 5) {	
				gameOver = true;
			}
			moleRate = 5 + (int)(molesWhacked/10);

		}
		private void animateMoles() {
			if (activeMole == 1) {
				if (moleRising) {
					mole1y -= moleRate;                    
				} else if (moleSinking) {
					mole1y += moleRate;
				}
				if (mole1y >= (int) (475*drawScaleH) || moleJustHit) {
					mole1y = (int) (475*drawScaleH);
					pickActiveMole();
				}
				if (mole1y <= (int) (300*drawScaleH)) {
					mole1y = (int) (300*drawScaleH);
					moleRising = false;
					moleSinking = true;
				}

			}
			if (activeMole == 2) {
				if (moleRising) {
					mole2y -= moleRate;                    
				} else if (moleSinking) {
					mole2y += moleRate;
				}
				if (mole2y >= (int) (425*drawScaleH) || moleJustHit) {
					mole2y = (int) (425*drawScaleH);
					pickActiveMole();
				}
				if (mole2y <= (int) (250*drawScaleH)) {
					mole2y = (int) (250*drawScaleH);
					moleRising = false;
					moleSinking = true;
				}
			}
			if (activeMole == 3) {
				if (moleRising) {
					mole3y -= moleRate;                    
				} else if (moleSinking) {
					mole3y += moleRate;
				}
				if (mole3y >= (int) (475*drawScaleH) || moleJustHit) {
					mole3y = (int) (475*drawScaleH);
					pickActiveMole();
				}
				if (mole3y <= (int) (300*drawScaleH)) {
					mole3y = (int) (300*drawScaleH);
					moleRising = false;
					moleSinking = true;
				}
			}
			if (activeMole == 4) {
				if (moleRising) {
					mole4y -= moleRate;                    
				} else if (moleSinking) {
					mole4y += moleRate;
				}
				if (mole4y >= (int) (425*drawScaleH) || moleJustHit) {
					mole4y = (int) (425*drawScaleH);
					pickActiveMole();
				}
				if (mole4y <= (int) (250*drawScaleH)) {
					mole4y = (int) (250*drawScaleH);
					moleRising = false;
					moleSinking = true;
				}
			}
			if (activeMole == 5) {
				if (moleRising) {
					mole5y -= moleRate;                    
				} else if (moleSinking) {
					mole5y += moleRate;
				}
				if (mole5y >= (int) (475*drawScaleH) || moleJustHit) {
					mole5y = (int) (475*drawScaleH);
					pickActiveMole();
				}
				if (mole5y <= (int) (300*drawScaleH)) {
					mole5y = (int) (300*drawScaleH);
					moleRising = false;
					moleSinking = true;
				}
			}
			if (activeMole == 6) {
				if (moleRising) {
					mole6y -= moleRate;                    
				} else if (moleSinking) {
					mole6y += moleRate;
				}
				if (mole6y >= (int) (425*drawScaleH) || moleJustHit) {
					mole6y = (int) (425*drawScaleH);
					pickActiveMole();
				}
				if (mole6y <= (int) (250*drawScaleH)) {
					mole6y = (int) (250*drawScaleH);
					moleRising = false;
					moleSinking = true;
				}
			}
			if (activeMole == 7) {
				if (moleRising) {
					mole7y -= moleRate;                    
				} else if (moleSinking) {
					mole7y += moleRate;
				}
				if (mole7y >= (int) (475*drawScaleH) || moleJustHit) {
					mole7y = (int) (475*drawScaleH);
					pickActiveMole();
				}
				if (mole7y <= (int) (300*drawScaleH)) {
					mole7y = (int) (300*drawScaleH);
					moleRising = false;
					moleSinking = true;
				}
			}
		}// metod
		private boolean detectMoleContact() {
			boolean contact = false;
			if (activeMole == 1 && 
				fingerX >= mole1x &&
				fingerX < mole1x+(int)(88*drawScaleW) &&
				fingerY > mole1y &&
				fingerY < (int) 450*drawScaleH) {
				contact = true;
				moleJustHit = true;
			}
			if (activeMole == 2 && 
				fingerX >= mole2x &&
				fingerX < mole2x+(int)(88*drawScaleW) &&
				fingerY > mole2y &&
				fingerY < (int) 400*drawScaleH) {
				contact = true;
				moleJustHit = true;
			}
			if (activeMole == 3 && 
				fingerX >= mole3x &&
				fingerX < mole3x+(int)(88*drawScaleW) &&
				fingerY > mole3y &&
				fingerY < (int) 450*drawScaleH) {
				contact = true;
				moleJustHit = true;
			}
			if (activeMole == 4 && 
				fingerX >= mole4x &&
				fingerX < mole4x+(int)(88*drawScaleW) &&
				fingerY > mole4y &&
				fingerY < (int) 400*drawScaleH) {
				contact = true;
				moleJustHit = true;
			}
			if (activeMole == 5 && 
				fingerX >= mole5x &&
				fingerX < mole5x+(int)(88*drawScaleW) &&
				fingerY > mole5y &&
				fingerY < (int) 450*drawScaleH) {
				contact = true;
				moleJustHit = true;
			}
			if (activeMole == 6 && 
				fingerX >= mole6x &&
				fingerX < mole6x+(int)(88*drawScaleW) &&
				fingerY > mole6y &&
				fingerY < (int) 400*drawScaleH) {
				contact = true;
				moleJustHit = true;
			}
			if (activeMole == 7 && 
				fingerX >= mole7x &&
				fingerX < mole7x+(int)(88*drawScaleW) &&
				fingerY > mole7y &&
				fingerY < (int) 450*drawScaleH) {
				contact = true;
				moleJustHit = true;
			}
			return contact;
		}
		
    } // class
	
	

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return thread.doTouchEvent(event);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	thread.setSurfaceSize(width, height);
    }

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
    	thread.setRunning(true);
    	if (thread.getState() == Thread.State.NEW) {        
    		thread.start();
    	}
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	thread.setRunning(false);
    }
}



