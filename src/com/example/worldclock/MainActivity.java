package com.example.worldclock;

import java.util.ArrayList;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener {

	private TextView mTimeTextView;
	private TextView mDateTextView;
	private TextView mCityTextView;
	private Button mRightButton;
	private ImageView mLeftArrow;
	private ImageView mRightArrow;
	private MyClock mClock = null;
	private BroadcastReceiver mReceiver = null;
	private CityManager mCityManager;

	private int mCurrentCityID = 0;
	private String mCityName = "";

	private final int DEFAULT_CITY = 0;
	private final int FIRST_CITY = 1;
	private final int SECEND_CITY = 2;
	private final String CITY_ID_TO_LOAD = "city_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mCityManager = CityManager.getInstance(getBaseContext());

		if (getIntent().hasExtra(CITY_ID_TO_LOAD)) {
			mCurrentCityID = getIntent().getIntExtra(CITY_ID_TO_LOAD,
					DEFAULT_CITY);
			mCityName = mCityManager.loadCityName(mCurrentCityID);
		} else if (mCityManager.loadCityCount() > 0) {
			mCurrentCityID = 1;
			mCityName = mCityManager.loadCityName(mCurrentCityID);
		}

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
				showTimeAndDate();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mClock != null) {
			mClock.stop();
		}
	}

	private void initListView() {
		ListView listview;
		listview = (ListView) findViewById(R.id.list_cities);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		MyAdapter adapter = new MyAdapter(getBaseContext(), metrics);
		listview.setAdapter(adapter);
		listview.setSelection(12);
		adapter.notifyDataSetInvalidated();
		listview.setOnItemClickListener(this);
	}

	private void initViews() {
		mClock = (MyClock) findViewById(R.id.clock);
		mTimeTextView = (TextView) findViewById(R.id.tv_time);
		mDateTextView = (TextView) findViewById(R.id.tv_date);
		mCityTextView = (TextView) findViewById(R.id.tv_city);
		mRightButton = (Button) findViewById(R.id.right_button);
		mLeftArrow = (ImageView) findViewById(R.id.left_arrow);
		mRightArrow = (ImageView) findViewById(R.id.right_arrow);

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
				mCityName = textview.getText().toString();
				update();
			}
		});

		Button doneButton = (Button) findViewById(R.id.left_button);
		doneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CityManager.getInstance(getBaseContext()).saveCityName(
						mCurrentCityID, mCityName);
			}
		});
	}

	private void loadCity(int cityNumber) {
		switch (cityNumber) {
		case DEFAULT_CITY:
			// TODO
			mLeftArrow.setVisibility(View.INVISIBLE);
			mRightButton.setText("New");
			showDefault();
			break;
		case FIRST_CITY:
			mLeftArrow.setVisibility(View.INVISIBLE);
			mRightButton.setText("New");
			update();
			if (mCityManager.loadCityCount() == 2) {
				mRightButton.setClickable(false);
				mRightButton
						.setBackgroundResource(R.drawable.cmd_btn_grey_normal_disable);
			}
			OnClickListener listener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this,
							MainActivity.class);
					intent.putExtra(CITY_ID_TO_LOAD, 2);
					MainActivity.this.startActivity(intent);
				}
			};
			mRightArrow.setOnClickListener(listener);
			mRightButton.setOnClickListener(listener);
			break;
		case SECEND_CITY:
			// TODO
			mRightArrow.setVisibility(View.INVISIBLE);
			mRightButton.setText("Delete");
			if (TextUtils.isEmpty(mCityName)) {
				showDefault();
			} else {
				update();
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
				mCityName = CommonUtil.splitAndJoin(TimeZone.getDefault()
						.getID());
				update();
			}
		});
	}

	private void update() {
		showTimeAndDate();
		mCityTextView.setText(mCityName);
		mClock.update(mCityName);
	}

	private void showTimeAndDate() {
		mTimeTextView.setText(CommonUtil.getCurrentTime(getBaseContext(),
				mCityName));
		mDateTextView.setText(CommonUtil.getCurrentDate(getBaseContext(),
				mCityName));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ArrayList<String> citylList = CommonUtil
				.getCitiesOfTimezone(position - 12);
		String[] cities = new String[citylList.size()];
		cities = (String[]) citylList.toArray(cities);
		final String[] strs = cities;
		new AlertDialog.Builder(MainActivity.this).setTitle("Choose a city")
				.setItems(cities, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mCityName = strs[which];
						update();
						dialog.dismiss();
					}
				}).show();
	}
}
