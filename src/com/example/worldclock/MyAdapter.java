package com.example.worldclock;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private DisplayMetrics mMetrics;

	public MyAdapter(Context context, DisplayMetrics metrics) {
		super();
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMetrics = metrics;
	}

	@Override
	public int getCount() {
		return 25;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new Holder();
			holder.cityTextView = (TextView) convertView
					.findViewById(R.id.tv_city_list);
			holder.timezoneTextView = (TextView) convertView
					.findViewById(R.id.tv_timezone_list);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		int timezone = position - 12;
		if (timezone >= 0) {
			holder.timezoneTextView.setText("+" + timezone + "");
		} else {
			holder.timezoneTextView.setText(timezone + "");
		}
		String city = CommonUtil.getCitiesOfTimezone(position - 12).get(0);
		holder.cityTextView.setText(city);
		Animation animation = null;
		// animation = new TranslateAnimation(0, 0, mMetrics.heightPixels,
		// 0);
		animation = new AlphaAnimation(0, 1);
		// animation = new ScaleAnimation((float)1.0, (float)1.0 ,(float)0,
		// (float)1.0);
		animation.setDuration(500);
		convertView.startAnimation(animation);
		animation = null;
		return convertView;
	}

	private class Holder {
		public TextView cityTextView;
		public TextView timezoneTextView;
	}
}
