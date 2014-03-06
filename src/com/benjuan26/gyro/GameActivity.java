package com.benjuan26.gyro;

import java.util.Random;

import android.graphics.Color;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class GameActivity extends Activity implements SensorEventListener {
	private SensorManager manager;
	private Sensor gyro;
	private int screenHeight;
	private int screenWidth;
	private Ball ball;
	private Ball target;
	private Random random = new Random();
	private TextView timerLabel;
	private TextView scoreLabel;
	private int score = 0;
	private Vibrator vibrator;
	private CustomCountdown countdown = new CustomCountdown(30000, 1000);
	
	private final double MAX_GRAV = 9.81;
	private final int DIFFICULTY = 40;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ball = new Ball(this, Style.FILL, Color.BLACK);
		setContentView(ball);
		
		getWindow().addContentView(getLayoutInflater().inflate(R.layout.activity_game, null),
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		timerLabel = (TextView) findViewById(R.id.countdown);
		scoreLabel = (TextView) findViewById(R.id.scoreLabel);
		
		countdown.start();
		
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		gyro = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
		
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		target = new Ball(this, Style.STROKE, Color.RED);
		getWindow().addContentView(target, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		newTarget();
	}
	
	public void newTarget(){
		int x = 10 + random.nextInt(screenWidth - 20);
		int y = 10 + random.nextInt(screenHeight - 20);
		
		target.setBallX(x);
		target.setBallY(y);
		
		target.invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		manager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		manager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		
		int centreX = screenWidth/2;
		int centreY = screenHeight/2;
		
		double xVal = values[0];
		double yVal = values[1];
		double xFactor = xVal/MAX_GRAV;
		double yFactor = yVal/MAX_GRAV;
		int x = (int) (centreX - (screenWidth * xFactor));
		int y = (int) (centreY + (screenHeight * yFactor));
		
		ball.setBallX(x);
		ball.setBallY(y);
		//Log.i("Gyro", ball.getBallX()+", "+ball.getBallY());
		
		checkHits();
		
		ball.invalidate();
	}
	
	public void checkHits(){
		if ( Math.abs( ball.getBallX() - target.getBallX() ) <= DIFFICULTY &&
				Math.abs( ball.getBallY() - target.getBallY() ) <= DIFFICULTY){
			score++;
			scoreLabel.setText(""+score);
			
			newTarget();
			
			if (vibrator.hasVibrator()) vibrator.vibrate(10);
		}
	}
	
	public void reset(){
		timerLabel.setTextColor(Color.BLACK);
		countdown.start();
		score = 0;
		scoreLabel.setText(""+score);
		
		newTarget();
	}
	
	public class CustomCountdown extends CountDownTimer{
		public CustomCountdown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public void onFinish() {
			timerLabel.setText("0:00");
			
			AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			builder.setTitle("Time is up!");
			builder.setMessage("You popped "+score+" bubbles! Play again?");
			
			builder.setPositiveButton(android.R.string.yes, new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					reset();
				}
			});
			
			builder.setNegativeButton(android.R.string.no, new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					((Activity)GameActivity.this).finish();
				}
			});
			
			builder.show();
		}

		public void onTick(long millis) {
			if (millis <= 5000) timerLabel.setTextColor(Color.RED);
			
			long second = (millis / 1000) % 60;
			long minute = (millis / (1000 * 60)) % 60;
			
			timerLabel.setText(String.format("%01d:%02d", minute, second));
		}
	}

}
