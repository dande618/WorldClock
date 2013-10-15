package com.example.worldclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity {

	private BroadcastReceiver mReceiver = null;
	private Fragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null)
			mFragment = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mFragment == null) {
			mFragment = new MyFragment(this);
		}
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.fragment_main, mFragment).commit();
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager()));
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter minuteTimeFilter = new IntentFilter(
				Intent.ACTION_TIME_TICK);
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// showTimeAndDate();
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

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return (new MyFragment(MainActivity.this));
		}

		@Override
		public int getCount() {
			return 2;
		}

	}
}
