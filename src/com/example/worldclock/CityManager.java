package com.example.worldclock;

import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class CityManager {
	private static CityManager mCityManager;
	private static Context mContext;
	private static SharedPreferences mSharedata;

	private CityManager() {
	}

	public static CityManager getInstance(Context context) {
		if (mCityManager == null) {
			mCityManager = new CityManager();
			mContext = context;
			mSharedata = mContext.getSharedPreferences("data",
					Context.MODE_PRIVATE);
		}
		return mCityManager;
	}

	private void saveCityCount(int cityCount) {
		Editor editor = mSharedata.edit();
		editor.putInt("city_count", cityCount);
		editor.commit();
	}

	public int loadCityCount() {
		int cityCount = mSharedata.getInt("city_count", 0);
		return cityCount;
	}

	public void saveCityName(int id, String name) {
		Editor editor = mSharedata.edit();
		switch (id) {
		case 0:
		case 1:
			if (TextUtils.isEmpty(name) || name.equals("City")) {
				name = getCurrentCityName();
			}
			editor.putString("first_city_name", name);
			if (loadCityCount() == 0)
				saveCityCount(1);
			break;
		case 2:
			if (name.equals("City")) {
				return;
			}
			editor.putString("secend_city_name", name);
			saveCityCount(2);
			break;
		}
		editor.commit();
	}

	public String loadCityName(int id) {
		switch (id) {
		case 1:
			return mSharedata.getString("first_city_name", null);
		case 2:
			return mSharedata.getString("secend_city_name", null);
		}
		return null;
	}

	public String getCurrentCityName() {
		return CommonUtil.splitAndJoin(TimeZone.getDefault().getID());
	}

	public boolean deleteSecendCity() {
		saveCityCount(1);
		return mSharedata.edit().remove("secend_city_name").commit();
	}
}
