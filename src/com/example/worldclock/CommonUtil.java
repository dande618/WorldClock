package com.example.worldclock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

public class CommonUtil {
	public static String getCurrentTime(Context context, String cityName) {
		Date date = Calendar.getInstance().getTime();
		java.text.DateFormat dateFormat = DateFormat.getTimeFormat(context);
		if (TextUtils.isEmpty(cityName)) {
			dateFormat.setTimeZone(TimeZone.getDefault());
		} else {
			dateFormat
					.setTimeZone(TimeZone.getTimeZone(splitAndJoin(cityName)));
		}
		String currentTime = dateFormat.format(date);
		return currentTime;
	}

	public static String getCurrentDate(Context context, String cityName) {
		Date date = Calendar.getInstance().getTime();
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		if (TextUtils.isEmpty(cityName)) {
			dateFormat.setTimeZone(TimeZone.getDefault());
		} else {
			dateFormat
					.setTimeZone(TimeZone.getTimeZone(splitAndJoin(cityName)));
		}
		String currentDate = dateFormat.format(date);
		return currentDate;
	}

	public static ArrayList<String> getCitiesOfTimezone(int timezone) {
		int offsetMillis = 3600000 * timezone;
		String[] citieStrings = TimeZone.getAvailableIDs(offsetMillis);
		return splitAndJoin(citieStrings);
	}

	public static ArrayList<String> splitAndJoin(String[] ids) {
		ArrayList<String> list = new ArrayList<String>();
		for (String id : ids) {
			String[] strings = StringUtils.split(id, "/");
			if (strings.length == 1) {
				list.add(id);
				continue;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = strings.length - 1; i >= 0; i--) {
				sb.append(strings[i]);
				if (i > 0)
					sb.append("/");
			}
			list.add(sb.toString());
		}
		return list;
	}

	public static String splitAndJoin(String id) {
		String[] strings = StringUtils.split(id, "/");
		if (strings.length == 1) {
			return strings[0];
		}
		StringBuilder sb = new StringBuilder();
		for (int i = strings.length - 1; i >= 0; i--) {
			sb.append(strings[i]);
			if (i > 0)
				sb.append("/");
		}
		return sb.toString();
	}

	public static int getCurrentOffset() {
		return TimeZone.getDefault().getRawOffset();
	}

	public static int getTargetOffset(String cityId) {
		String timezoneName = splitAndJoin(cityId);
		return TimeZone.getTimeZone(timezoneName).getRawOffset();
	}
}
