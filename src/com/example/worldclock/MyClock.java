package com.example.worldclock;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class MyClock extends View {

	private BitmapDrawable bmdHour;
	private BitmapDrawable bmdMinute;
	private BitmapDrawable bmdSecond;
	private BitmapDrawable bmdDial;
	private TimeZone timeZone;

	static Handler mClockHandler;

	int centerX;
	int centerY;
	double scale;
	int mWaitTime;

	public MyClock(Context context, AttributeSet attr) {
		super(context, attr);
		bmdHour = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.hour_hand));
		bmdMinute = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.minute_hand));
		bmdSecond = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.second_hand));
		bmdDial = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.clock_dial));
		start();
	}

	private void start() {
		mClockHandler = new Handler();
		mClockHandler.post(tickRunnable);
	}

	private Runnable tickRunnable = new Runnable() {
		public void run() {
			postInvalidate();
			mWaitTime = 1000 - (int) (System.currentTimeMillis() % 1000);
			// Log.e("DK2013", "" + mWaitTime);
			mClockHandler.postDelayed(tickRunnable, mWaitTime);
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Calendar cal;
		if (timeZone == null) {
			cal = Calendar.getInstance();
		} else {
			cal = Calendar.getInstance(timeZone);
		}
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f;
		float minuteRotate = minute * 6.0f;
		float secondRotate = second * 6.0f;

		centerX = getWidth() / 2;
		centerY = getHeight() / 2;

		int size = Math.min(getWidth(), getHeight());
		scale = (double) size / bmdDial.getIntrinsicWidth();
		bmdDial.setBounds(centerX - size / 2, centerY - size / 2, centerX
				+ size / 2, centerY + size / 2);
		bmdDial.draw(canvas);

		drawHands(bmdHour, canvas, hourRotate);
		drawHands(bmdMinute, canvas, minuteRotate);
		drawHands(bmdSecond, canvas, secondRotate);

	}

	private void drawHands(BitmapDrawable handBitmap, Canvas canvas, float roate) {
		canvas.save();
		int mTempWidth = (int) (handBitmap.getIntrinsicWidth() * scale);
		int mTempHeigh = (int) (handBitmap.getIntrinsicHeight() * scale);
		canvas.rotate(roate, centerX, centerY);
		handBitmap.setBounds(centerX - (mTempWidth / 2), centerY
				- (mTempHeigh / 2), centerX + (mTempWidth / 2), centerY
				+ (mTempHeigh / 2));
		handBitmap.draw(canvas);
		canvas.restore();
	}

	protected void stop() {
		mClockHandler.removeCallbacks(tickRunnable);
	}

	public void update(String cityName) {
		this.timeZone = TimeZone.getTimeZone(CommonUtil.splitAndJoin(cityName));
		postInvalidate();
	}
}
