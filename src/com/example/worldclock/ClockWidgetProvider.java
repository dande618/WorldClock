package com.example.worldclock;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
			bitmap1 = drawClock(null, cityCount);
			views.setImageViewBitmap(R.id.dialimg1, bitmap1);
			views.setTextViewText(R.id.tv_city, cityName1);
			views.setTextViewText(R.id.tv_time, time);
			views.setTextViewText(R.id.tv_date, date);
			bindOnclickIntent(context, views);
			appWidgetManager.updateAppWidget(appWidgetIds, views);
			bitmap1.recycle();
			break;
		case 1:
			views = new RemoteViews(context.getPackageName(),
					R.layout.clockwidget_layout1);
			bitmap1 = drawClock(
					TimeZone.getTimeZone(CommonUtil.splitAndJoin(cityName1)),
					cityCount);
			views.setImageViewBitmap(R.id.dialimg1, bitmap1);
			views.setTextViewText(R.id.tv_city, cityName1);
			views.setTextViewText(R.id.tv_time, time);
			views.setTextViewText(R.id.tv_date, date);
			bindOnclickIntent(context, views);
			appWidgetManager.updateAppWidget(appWidgetIds, views);
			bitmap1.recycle();
			break;
		case 2:
			views = new RemoteViews(context.getPackageName(),
					R.layout.clockwidget_layout2);
			bitmap1 = drawClock(
					TimeZone.getTimeZone(CommonUtil.splitAndJoin(cityName1)),
					cityCount);
			bitmap2 = drawClock(
					TimeZone.getTimeZone(CommonUtil.splitAndJoin(cityName2)),
					cityCount);
			views.setImageViewBitmap(R.id.dialimg1, bitmap1);
			views.setImageViewBitmap(R.id.dialimg2, bitmap2);
			views.setTextViewText(R.id.tv_city1, cityName1);
			views.setTextViewText(R.id.tv_city2, cityName2);
			bindOnclickIntent(context, views);
			appWidgetManager.updateAppWidget(appWidgetIds, views);
			bitmap1.recycle();
			bitmap2.recycle();
			break;
		default:
			break;
		}
		bitmap1 = bitmap2 = null;
		System.gc();
	}

	private Bitmap drawClock(TimeZone timeZone, int cityCount) {
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);
		Calendar cal;
		if (timeZone == null) {
			cal = Calendar.getInstance();
		} else {
			cal = Calendar.getInstance(timeZone);
		}
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int amOrPm = cal.get(Calendar.AM_PM);
		float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f;
		float minuteRotate = minute * 6.0f + second / 60.0f * 6.0f;
		float secondRotate = second * 6.0f;

		int size = Math.min(w, h);
		scale = (double) size / dial_drawable.getIntrinsicWidth();
		dial_drawable.setBounds(centerX - size / 2, centerY - size / 2, centerX
				+ size / 2, centerY + size / 2);
		dial_drawable.draw(canvas);
		if (cityCount == 2) {
			drawAMPM(amOrPm, canvas, scale, size);
		}
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

	private void bindOnclickIntent(Context context, RemoteViews views) {
		Intent clickIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				clickIntent, 0);
		views.setOnClickPendingIntent(R.id.widget, pendingIntent);
	}

	private void drawAMPM(int amOrPm, Canvas canvas, double scale, int height) {
		Paint paint = new Paint();
		// paint.setStrokeWidth((float) (60 * scale));
		paint.setTextSize((float) (30 * scale));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
		paint.setTextAlign(Align.CENTER);
		int offset = (int) (30 * scale);
		if (amOrPm == 0) {
			paint.setColor(Color.GREEN); // Text Color
			canvas.drawText("AM", height / 2, height / 2 + offset, paint);
		} else {
			paint.setColor(Color.BLUE); // Text Color
			canvas.drawText("PM", height / 2, height / 2 + offset, paint);
		}
	}
}
