package com.example.worldclock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	public static final String UPDATE_CLOCK = "com.example.worldclock.UPDATE_WIDGET";
	public static final String SERVICE_ACTION = "com.example.worldclock.TIME_SERVICE";
	private int waitTime = 1000;
	private Thread timeThread;
	public static volatile boolean running = false;
	private BroadcastReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		running = true;
		timeThread = new TimeThread();
		timeThread.start();
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e("DK2013", "android.intent.action.SCREEN_OFF");
				running = false;
				MyService.this.stopSelf();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.SCREEN_OFF");
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		running = false;
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private class TimeThread extends Thread {
		@Override
		public void run() {
			Intent intent = new Intent(UPDATE_CLOCK);
			while (MyService.running) {
				waitTime = 1000 - (int) (System.currentTimeMillis() % 1000);
				try {
					Thread.sleep(waitTime);
					sendBroadcast(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
