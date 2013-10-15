package com.example.worldclock;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		OnItemClickListener, OnPageChangeListener {
	private int mCurrentCityID = 1;

	private Button mRightButton;
	private CityManager mCityManager;
	private ViewPager mViewPager;
	private boolean secendCityDeleted = false;

	private BroadcastReceiver mReceiver = null;
	public static final int DEFAULT_CITY = 0;
	public static final int FIRST_CITY = 1;
	public static final int SECEND_CITY = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mCityManager = CityManager.getInstance(this);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager()));
		mViewPager.setOnPageChangeListener(MainActivity.this);
		initViews();
		initListView();
		loadCity(mCurrentCityID);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter minuteTimeFilter = new IntentFilter(
				Intent.ACTION_TIME_TICK);
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateAllClocks();
			}
		};
		registerReceiver(mReceiver, minuteTimeFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

	private void initViews() {
		mRightButton = (Button) findViewById(R.id.right_button);

		AutoCompleteTextView autoTextView;
		autoTextView = (AutoCompleteTextView) findViewById(R.id.edit_text);
		ArrayList<String> cityList;
		cityList = CommonUtil.splitAndJoin(TimeZone.getAvailableIDs());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, cityList);
		autoTextView.setAdapter(adapter);
		autoTextView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView textview = (TextView) view;
				String cityName = textview.getText().toString();
				updateCurrentClock(cityName);
			}
		});

		Button doneButton = (Button) findViewById(R.id.left_button);
		doneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CityManager.getInstance(MainActivity.this).saveCityName(
						mCurrentCityID, getClockCityName());
				loadCity(mCurrentCityID);
			}
		});
	}

	private void initListView() {
		ListView listview;
		listview = (ListView) findViewById(R.id.list_cities);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		MyAdapter adapter = new MyAdapter(this, metrics);
		listview.setAdapter(adapter);
		listview.setSelection(12);
		adapter.notifyDataSetInvalidated();
		listview.setOnItemClickListener(this);
	}

	private void loadCity(int cityNumber) {
		switch (cityNumber) {
		case FIRST_CITY:
			mRightButton.setText("New");
			if (mCityManager.loadCityCount() == 2) {
				mRightButton.setClickable(false);
				mRightButton
						.setBackgroundResource(R.drawable.cmd_btn_grey_normal_disable);
			} else {
				mRightButton.setClickable(true);
				mRightButton.setBackgroundResource(R.drawable.bg_button);
				mRightButton.setOnClickListener(new MyListener(SECEND_CITY));
			}
			break;
		case SECEND_CITY:
			mRightButton.setText("Delete");
			if (mCityManager.loadCityCount() < 2) {
				mRightButton.setClickable(false);
				mRightButton
						.setBackgroundResource(R.drawable.cmd_btn_grey_normal_disable);
			} else {
				mRightButton.setClickable(true);
				mRightButton.setBackgroundResource(R.drawable.bg_button);
				mRightButton.setOnClickListener(new MyListener(FIRST_CITY));
			}
			break;
		default:
			break;
		}
	}

	private void updateCurrentClock(String cityName) {
		List<Fragment> list = getSupportFragmentManager().getFragments();
		((MyFragment) list.get(mCurrentCityID - 1)).update(cityName);
	}

	private void updateAllClocks() {
		List<Fragment> list = getSupportFragmentManager().getFragments();
		for (Fragment fragment : list) {
			((MyFragment) fragment).update();
		}
	}

	private String getClockCityName() {
		List<Fragment> list = getSupportFragmentManager().getFragments();
		return ((MyFragment) list.get(mCurrentCityID - 1)).getClockCityName();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ArrayList<String> citylList = CommonUtil
				.getCitiesOfTimezone(position - 12);
		String[] cities = new String[citylList.size()];
		cities = (String[]) citylList.toArray(cities);
		final String[] strs = cities;
		new AlertDialog.Builder(this).setTitle("Choose a city")
				.setItems(cities, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String cityName = strs[which];
						updateCurrentClock(cityName);
						dialog.dismiss();
					}
				}).show();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if (!secendCityDeleted) {
			CityManager.getInstance(MainActivity.this).saveCityName(
					mCurrentCityID, getClockCityName());
		}
		mCurrentCityID = arg0 + 1;
		loadCity(arg0 + 1);
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return (new MyFragment(MainActivity.this, arg0 + 1));
		}

		@Override
		public int getCount() {
			return 2;
		}

	}

	private class MyListener implements OnClickListener {
		int cityNumber;

		public MyListener(int cityNumber) {
			this.cityNumber = cityNumber;
		}

		@Override
		public void onClick(View v) {
			if (mCurrentCityID == SECEND_CITY) {
				mCityManager.deleteSecendCity();
				secendCityDeleted = true;
			}
			mViewPager.setCurrentItem(cityNumber - 1);
		}

	}

}
