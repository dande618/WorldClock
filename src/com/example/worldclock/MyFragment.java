package com.example.worldclock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class MyFragment extends Fragment {
	private Context mContext;
	private CityManager mCityManager;
	private TextView mTimeTextView;
	private TextView mDateTextView;
	private TextView mCityTextView;
	private ImageView mLeftArrow;
	private ImageView mRightArrow;
	private MyClock mClock = null;

	private int mCityNum = MainActivity.DEFAULT_CITY;

	public MyFragment(Activity activity, int cityId) {
		mContext = activity;
		mCityNum = cityId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		mCityManager = CityManager.getInstance(mContext);
		initViews(view);
		loadCity();
		return view;
	}

	@Override
	public void onDestroyView() {
		if (mClock != null) {
			mClock.stop();
		}
		super.onDestroyView();
	}

	private void initViews(View view) {
		mClock = (MyClock) view.findViewById(R.id.clock);
		mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
		mDateTextView = (TextView) view.findViewById(R.id.tv_date);
		mCityTextView = (TextView) view.findViewById(R.id.tv_city);
		mLeftArrow = (ImageView) view.findViewById(R.id.left_arrow);
		mRightArrow = (ImageView) view.findViewById(R.id.right_arrow);
	}

	private void loadCity() {

		switch (mCityNum) {
		case MainActivity.FIRST_CITY:
			mLeftArrow.setVisibility(View.INVISIBLE);
			if (mCityManager.loadCityCount() < 1) {
				showDefault();
			} else {
				update(mCityManager.loadCityName(MainActivity.FIRST_CITY));
			}
			break;
		case MainActivity.SECEND_CITY:
			mRightArrow.setVisibility(View.INVISIBLE);
			if (mCityManager.loadCityCount() < 2) {
				return;
			} else {
				update(mCityManager.loadCityName(MainActivity.SECEND_CITY));
			}
			break;
		default:
			break;
		}
	}

	private void showDefault() {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				update(mCityManager.getCurrentCityName());
			}
		});
	}

	public void update(String cityName) {
		showTimeAndDate(cityName);
		mCityTextView.setText(cityName);
		mClock.update(cityName);
	}

	public void update() {
		String cityName = mCityTextView.getText().toString();
		if (!cityName.equals("City"))
			showTimeAndDate(cityName);
	}

	public String getClockCityName() {
		return mCityTextView.getText().toString();
	}

	private void showTimeAndDate(String cityName) {
		String[] strings = CommonUtil.getTargetTime(mContext, cityName);
		mTimeTextView.setText(strings[0]);
		mDateTextView.setText(strings[1]);
	}
}
