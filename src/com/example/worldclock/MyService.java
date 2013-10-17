package com.example.worldclock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;

public class MyService extends Service implements
		OnSharedPreferenceChangeListener {
	public static final String UPDATE_WIDGET = "com.example.worldclock.UPDATE_WIDGET";
	public static final String SERVICE_ACTION = "com.example.worldclock.TIME_SERVICE";

	public static int cityCount = 0;
	public static String cityName1 = "";
	public static String cityName2 = "";
	public static String time = "";
	public static String date = "";

	public static boolean running = false;
	private BroadcastReceiver mReceiver;
	private CityManager mCityManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		running = true;
		new TimeThread().start();
		registerReceiver();
		SharedPreferences preferences = getSharedPreferences("data",
				Context.MODE_PRIVATE);
		preferences.registerOnSharedPreferenceChangeListener(this);
		fetchData();
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
			Intent intent = new Intent(UPDATE_WIDGET);
			while (running) {
				int waitTime = 1000 - (int) (System.currentTimeMillis() % 1000);
				try {
					Thread.sleep(waitTime);
					sendBroadcast(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void fetchData() {
		mCityManager = CityManager.getInstance(this);
		cityCount = mCityManager.loadCityCount();
		if (cityCount > 0) {
			cityName1 = mCityManager.loadCityName(1);
			setTimeAndDate();
		}
		if (cityCount > 1) {
			cityName2 = mCityManager.loadCityName(2);
		}
	}

	private void registerReceiver() {
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				running = false;
				MyService.this.stopSelf();
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.SCREEN_OFF");
		registerReceiver(mReceiver, filter);
	}

	// TODO change every minute; widget layout; click on widget;
	private void setTimeAndDate() {
		String[] strings = CommonUtil.getTargetTime(this, cityName1);
		time = strings[0];
		date = strings[1];
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("city_count")) {
			cityCount = mCityManager.loadCityCount();
		} else if (key.equals("first_city_name")) {
			cityName1 = mCityManager.loadCityName(1);
			setTimeAndDate();
		} else if (key.equals("secend_city_name")) {
			cityName2 = mCityManager.loadCityName(2);
		}
	}

}
