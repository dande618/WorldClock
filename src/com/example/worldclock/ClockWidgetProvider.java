package com.example.worldclock;

import java.util.Calendar;
import java.util.TimeZone;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
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
		updateRemoteViews(context, appWidgetManager, appWidgetIds);
		startService(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		// Log.i("onReceive", "onReceive " + intent.getAction());
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(
				context.getPackageName(), ClockWidgetProvider.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		if (appWidgetIds.length == 0) {
			stopService(context);
			return;
		}
		if (intent.getAction().equals(MyService.UPDATE_WIDGET)) {
			updateRemoteViews(context, appWidgetManager, appWidgetIds);
			System.gc();
		} else if (intent.getAction().equals(
				"android.intent.action.USER_PRESENT")) {
			startService(context);
		}
	}

	private void startService(Context context) {
		context.startService(new Intent(MyService.SERVICE_ACTION));
	}

	private void stopService(Context context) {
		context.stopService(new Intent(MyService.SERVICE_ACTION));
	}

	protected void updateRemoteViews(Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {

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

		RemoteViews views;
		Bitmap bitmap1;
		Bitmap bitmap2;
		final int cityCount = MyService.cityCount;
		final String cityName1 = MyService.cityName1;
		final String cityName2 = MyService.cityName2;
		final String time = MyService.time;
		final String date = MyService.date;
		switch (cityCount) {
		case 0:
			views = new RemoteViews(context.getPackageName(),
					R.layout.clockwidget_layout1);
			bitmap1 = drawClock(null);
			views.setImageViewBitmap(R.id.dialimg1, bitmap1);
			views.setTextViewText(R.id.tv_city, cityName1);
			views.setTextViewText(R.id.tv_time, time);
			views.setTextViewText(R.id.tv_date, date);
			appWidgetManager.updateAppWidget(appWidgetIds, views);
			bitmap1.recycle();
			break;
		case 1:
			views = new RemoteViews(context.getPackageName(),
					R.layout.clockwidget_layout1);
			bitmap1 = drawClock(TimeZone.getTimeZone(CommonUtil
					.splitAndJoin(cityName1)));
			views.setImageViewBitmap(R.id.dialimg1, bitmap1);
			views.setTextViewText(R.id.tv_city, cityName1);
			views.setTextViewText(R.id.tv_time, time);
			views.setTextViewText(R.id.tv_date, date);
			appWidgetManager.updateAppWidget(appWidgetIds, views);
			bitmap1.recycle();
			break;
		case 2:
			views = new RemoteViews(context.getPackageName(),
					R.layout.clockwidget_layout2);
			bitmap1 = drawClock(TimeZone.getTimeZone(CommonUtil
					.splitAndJoin(cityName1)));
			bitmap2 = drawClock(TimeZone.getTimeZone(CommonUtil
					.splitAndJoin(cityName2)));
			views.setImageViewBitmap(R.id.dialimg1, bitmap1);
			views.setImageViewBitmap(R.id.dialimg2, bitmap2);
			appWidgetManager.updateAppWidget(appWidgetIds, views);
			bitmap1.recycle();
			bitmap2.recycle();
			break;
		default:
			break;
		}
		views = null;
		bitmap1 = bitmap2 = null;
		dial_drawable = hour_drawable = minute_drawable = second_drawable = null;
	}

	private Bitmap drawClock(TimeZone timeZone) {
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);
		// TODO set time zone
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
