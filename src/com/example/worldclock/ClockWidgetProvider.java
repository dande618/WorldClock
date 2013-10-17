package com.example.worldclock;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

public class ClockWidgetProvider extends AppWidgetProvider {

	private Drawable dial_drawable, hour_drawable, minute_drawable,
			second_drawable;
	int w;
	int h;
	int centerX;
	int centerY;
	double scale;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		Log.i("onUpdate", "ClockWidgetProvider onUpdate");

		final int n = appWidgetIds.length;
		for (int i = 0; i < n; i++) {
			appWidgetManager.updateAppWidget(appWidgetIds[i],
					updateViews(context));
		}
		startService(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(MyService.UPDATE_CLOCK)) {
			updateViews(context);
		} else if (intent.getAction().equals(
				"android.intent.action.USER_PRESENT")) {
			startService(context);
		}
		super.onReceive(context, intent);
		Log.i("onReceive", "onReceive " + intent.getAction());
	}

	private void startService(Context context) {
		context.startService(new Intent(MyService.SERVICE_ACTION));
	}

	protected RemoteViews updateViews(Context context) {
		// TODO decide one clock or two clocks?
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.clockwidget_layout1);
		dial_drawable = context.getResources().getDrawable(
				R.drawable.clock_dial);
		hour_drawable = context.getResources()
				.getDrawable(R.drawable.hour_hand);
		minute_drawable = context.getResources().getDrawable(
				R.drawable.minute_hand);
		second_drawable = context.getResources().getDrawable(
				R.drawable.second_hand);

		w = dial_drawable.getIntrinsicWidth();
		h = dial_drawable.getIntrinsicHeight();
		centerX = w / 2;
		centerY = h / 2;
		views.setImageViewBitmap(R.id.dialimg, drawClock());
		return views;
	}

	private Bitmap drawClock() {
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);

		Log.i("onDrawClock", "ClockWidgetProvider onDrawClock");

		// TODO set time zone
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f;
		float minuteRotate = minute * 6.0f + second / 60.0f * 6.0f;
		float secondRotate = second * 6.0f;

		int size = Math.min(w, h);
		scale = (double) size / dial_drawable.getIntrinsicWidth();
		dial_drawable.setBounds(centerX - size / 2, centerY - size / 2, centerX
				+ size / 2, centerY + size / 2);
		dial_drawable.draw(canvas);
		drawHands(hour_drawable, canvas, hourRotate);
		drawHands(minute_drawable, canvas, minuteRotate);
		drawHands(second_drawable, canvas, secondRotate);
		return bitmap;
	}

	private void drawHands(Drawable handBitmap, Canvas canvas, float roate) {
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
}
