package com.mycompany.myapp.crazy_eight;

import com.mycompany.myapp.R;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.*;
import android.content.*;

public class TitleView extends View {
	
	private Bitmap titleGraphic;
	private int screenW;                               
	private int screenH;
	private Bitmap playButtonUp;
	private Bitmap playButtonDown;
	private boolean playButtonPressed;
	
	private Context myContext;


	public TitleView(Context context) {
		super(context);
		myContext = context;
		
		titleGraphic = BitmapFactory.decodeResource(getResources(),	R.drawable.title_graphic);
		playButtonUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_up);
		playButtonDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_button_down);
	}
	
	// menentukan layar dimensi dan berpusat grafis Umum
	@Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){                      
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;
    }

	@Override 
	protected void onDraw(Canvas canvas) {
		if (playButtonPressed) {
			canvas.drawBitmap(playButtonDown, (screenW-playButtonUp.getWidth())/2, (int)(screenH*0.7), null);
		} else {
			canvas.drawBitmap(playButtonUp, (screenW-playButtonUp.getWidth())/2, (int)(screenH*0.7), null);
		}
		canvas.drawBitmap(titleGraphic,	(screenW-titleGraphic.getWidth())/2, 0, null);
		//canvas.drawBitmap(titleGraphic, 0, 0, null);
	}

	public boolean onTouchEvent(MotionEvent event) { 
		int eventaction = event.getAction();   
		int X = (int)event.getX();
		int Y = (int)event.getY();
		switch (eventaction ) {
			case MotionEvent.ACTION_DOWN:
				//handel button press
				if (X > (screenW-playButtonUp.getWidth())/2  &&
					X < ((screenW-playButtonUp.getWidth())/2+playButtonUp.getWidth()) &&
					Y > (int)(screenH*0.7) &&
					Y < (int)(screenH*0.7) + playButtonUp.getHeight()) {
					playButtonPressed = true;
				}	
				break;
			case MotionEvent.ACTION_MOVE: 
				break;

			case MotionEvent.ACTION_UP:

				if (playButtonPressed) {
					Intent gameIntent = new Intent(myContext, GameActivity.class);	            
					myContext.startActivity(gameIntent);
				}
				playButtonPressed = false;
				break;
		} 
		invalidate();
		return true;      
	}
}
