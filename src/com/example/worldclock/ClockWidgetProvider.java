package com.example.worldclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class ClockWidgetProvider extends AppWidgetProvider {

	public static int DISPLAYWIDTH;
	public static int DISPLAYHEIGHT;
	private static Canvas dial_Canvas;
	private static Drawable dial_drawable, hour_drawable, minute_drawable,
			second_drawable, background_drawable, markers_drawable,
			numbers_drawable;
	private static boolean mChanged = false;
	protected static Time mCalendar = new Time();
	private static float mMinutes;
	private static float mHour;
	private static float mSecond;
	private static RemoteViews views;
	private static Bitmap b;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		Log.e("onUpdate", "ClockWidgetProvider onUpdate");

		final int n = appWidgetIds.length;
		for (int i = 0; i < n; i++) {
			updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
		}
	}

	private void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {

		views = new RemoteViews(context.getPackageName(),
				R.layout.clockwidget_layout1);

		background_drawable = context.getResources().getDrawable(
				R.drawable.back_org);
		markers_drawable = context.getResources().getDrawable(
				R.drawable.mark_org);
		numbers_drawable = context.getResources().getDrawable(
				R.drawable.numb_org);
		dial_drawable = context.getResources().getDrawable(
				R.drawable.clock_dial);
		hour_drawable = context.getResources()
				.getDrawable(R.drawable.hour_hand);
		minute_drawable = context.getResources().getDrawable(
				R.drawable.minute_hand);
		second_drawable = context.getResources().getDrawable(
				R.drawable.second_hand);

		DISPLAYWIDTH = dial_drawable.getIntrinsicWidth();
		DISPLAYHEIGHT = dial_drawable.getIntrinsicHeight();

		b = Bitmap.createBitmap(DISPLAYWIDTH, DISPLAYHEIGHT,
				Bitmap.Config.ARGB_8888);
		dial_Canvas = new Canvas(b);
		onTimeChanged();

		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.e("onReceive", "onReceive " + intent.getAction());

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.clockwidget_layout1);

		Intent AlarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(
				Intent.CATEGORY_LAUNCHER).setComponent(
				new ComponentName("com.android.alarmclock",
						"com.android.alarmclock.AlarmClock"));
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				AlarmClockIntent, 0);
		views.setOnClickPendingIntent(R.id.Widget, pendingIntent);

		AppWidgetManager.getInstance(context).updateAppWidget(
				intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS),
				views);
	}

	protected static void onDrawClock() {

		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		Log.e("onDrawClock", "ClockWidgetProvider onDrawClock");

		int availableWidth = DISPLAYWIDTH;
		int availableHeight = DISPLAYHEIGHT;

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = dial_drawable;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		// //////////////////////
		final Drawable background = background_drawable;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			background.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		background.draw(dial_Canvas);
		dial_Canvas.save();

		// ////////////////////////////
		final Drawable numbers = numbers_drawable;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			numbers.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		numbers.draw(dial_Canvas);
		dial_Canvas.save();

		// //////////////////////////////////////
		final Drawable markers = markers_drawable;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			markers.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		markers.draw(dial_Canvas);
		dial_Canvas.save();

		// ////////////////////////////////
		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(dial_Canvas);

		dial_Canvas.save();
		dial_Canvas.rotate(mHour / 12.0f * 360.0f, x, y);

		final Drawable hourHand = hour_drawable;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		hourHand.draw(dial_Canvas);
		dial_Canvas.restore();
		dial_Canvas.save();
		dial_Canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

		final Drawable minuteHand = minute_drawable;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		minuteHand.draw(dial_Canvas);

		dial_Canvas.restore();
		dial_Canvas.save();
		dial_Canvas.rotate(mSecond / 60.0f * 60.0f * 360.0f, x, y);

		Log.e("second onTimeChanged", mSecond / 30.0f * 360.0f + "");

		final Drawable secondHand = second_drawable;
		if (changed) {
			w = secondHand.getIntrinsicWidth();
			h = secondHand.getIntrinsicHeight();
			secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		secondHand.draw(dial_Canvas);
		dial_Canvas.restore();
		if (scaled) {
			dial_Canvas.restore();
		}
	}

	protected static void onTimeChanged() {

		mCalendar.setToNow();
		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mSecond = second / 60.0f;
		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;

		b.eraseColor(Color.TRANSPARENT);

		onDrawClock();

		views.setImageViewBitmap(R.id.dialimg, b);
	}

	protected static void broadcastTimeChanging() {
		mCalendar.setToNow();
		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mSecond = second / 60.0f;
		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;
	}
}
