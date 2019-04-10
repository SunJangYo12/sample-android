package com.draw;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SingleTouchEventView st = new SingleTouchEventView(this, null);
  		setContentView(st);
  		addContentView(st.btnReset, st.params);
	}
}

class SingleTouchEventView extends View {
 
 private Paint paint = new Paint();
 private Path path = new Path();
 
 public Button btnReset;
 public LayoutParams params;

 public SingleTouchEventView(Context context, AttributeSet attrs) {
  super(context, attrs);

  paint.setAntiAlias(true);
  paint.setStrokeWidth(6f);
  paint.setColor(Color.BLACK);
  paint.setStyle(Paint.Style.STROKE);
  paint.setStrokeJoin(Paint.Join.ROUND);
  
     btnReset = new Button(context);
  btnReset.setText("Clear Screen");

  params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
  btnReset.setLayoutParams(params);
  
  btnReset.setOnClickListener(new View.OnClickListener() {
   
   @Override
   public void onClick(View arg0) {
    // TODO Auto-generated method stub
    // resets the screen
    path.reset();

    // Calls the onDraw() method
    invalidate();
   }
  });
  
 }

 @Override
 protected void onDraw(Canvas canvas) {
  canvas.drawPath(path, paint);
 }

 @Override
 public boolean onTouchEvent(MotionEvent event) {
  float eventX = event.getX();
  float eventY = event.getY();

  switch (event.getAction()) {
  case MotionEvent.ACTION_DOWN:
   path.moveTo(eventX, eventY);
   return true;
  case MotionEvent.ACTION_MOVE:
   path.lineTo(eventX, eventY);
   btnReset.setText("Clea: "+eventX+", "+eventY);
   break;
  case MotionEvent.ACTION_UP:
   // nothing to do
   break;
  default:
   return false;
  }

  // Schedules a repaint.
  invalidate();
  return true;
 }

}
