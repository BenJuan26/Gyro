package com.benjuan26.gyro;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class Ball extends View {
	private int radius = 100; private void setRadius(int r){ radius = r; }
	private int screenHeight;
	private int screenWidth;
	private RectF bounds = new RectF();
	private Paint paint = new Paint();
	private int x; public void setBallX(int newX){ x = newX; } public int getBallX(){return x;}
	private int y; public void setBallY(int newY){ y = newY; } public int getBallY(){return y;}
	
	Ball(Context context, Style style, int color){
		super(context);
		
		DisplayMetrics metrics = new DisplayMetrics();
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
		
		x = screenWidth/2;
		y = screenHeight/2;
		paint.setStyle(style);
		paint.setStrokeWidth(3);
		paint.setColor(color);
	}
	
	public void pop(){
		ValueAnimator animAlpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
		animAlpha.setDuration(200);
		animAlpha.start();
		
		ValueAnimator animRad = ObjectAnimator.ofInt(100, 300);
		animRad.addUpdateListener(new AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				radius = (Integer) animation.getAnimatedValue();
				invalidate();
			}
		});
		animRad.setDuration(200);
		animRad.setInterpolator(new AccelerateInterpolator());
		animRad.start();
	}
	
	protected void onDraw(Canvas canvas){
		Log.d("Gyro", "Radius: "+radius);
		bounds.set(x-radius,y-radius,x+radius,y+radius);
		canvas.drawOval(bounds, paint);
	}

}
