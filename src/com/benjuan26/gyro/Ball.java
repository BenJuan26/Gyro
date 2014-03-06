package com.benjuan26.gyro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

public class Ball extends View {
	private static final int radius = 100;
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
	
	protected void onDraw(Canvas canvas){
		bounds.set(x-radius,y-radius,x+radius,y+radius);
		canvas.drawOval(bounds, paint);
	}

}
