package com.example.worldclock;

import java.util.ArrayList;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class MyFragment extends Fragment implements OnItemClickListener {
	private Context mContext;
	private CityManager mCityManager;
	private TextView mTimeTextView;
	private TextView mDateTextView;
	private TextView mCityTextView;
	private Button mRightButton;
	private ImageView mLeftArrow;
	private ImageView mRightArrow;
	private int mCurrentCityID = 0;
	private String mCityName = "";
	private MyClock mClock = null;
	private final int DEFAULT_CITY = 0;
	private final int FIRST_CITY = 1;
	private final int SECEND_CITY = 2;
	private final String CITY_ID_TO_LOAD = "city_id";

	public MyFragment(Activity activity) {
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		mCityManager = CityManager.getInstance(mContext);
		initViews(view);
		initListView(view);
		// TODO
		// loadCity(mCurrentCityID);
		loadCity(DEFAULT_CITY);
		return view;
	}

	@Override
	public void onDestroyView() {
		if (mClock != null) {
			mClock.stop();
		}
		super.onDestroyView();
	}

	private void initListView(View view) {
		ListView listview;
		listview = (ListView) view.findViewById(R.id.list_cities);
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		MyAdapter adapter = new MyAdapter(mContext, metrics);
		listview.setAdapter(adapter);
		listview.setSelection(12);
		adapter.notifyDataSetInvalidated();
		listview.setOnItemClickListener(this);
	}

	private void initViews(View view) {
		mClock = (MyClock) view.findViewById(R.id.clock);
		mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
		mDateTextView = (TextView) view.findViewById(R.id.tv_date);
		mCityTextView = (TextView) view.findViewById(R.id.tv_city);
		mRightButton = (Button) view.findViewById(R.id.right_button);
		mLeftArrow = (ImageView) view.findViewById(R.id.left_arrow);
		mRightArrow = (ImageView) view.findViewById(R.id.right_arrow);

		AutoCompleteTextView autoTextView;
		autoTextView = (AutoCompleteTextView) view.findViewById(R.id.edit_text);
		ArrayList<String> cityList;
		cityList = CommonUtil.splitAndJoin(TimeZone.getAvailableIDs());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
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

		Button doneButton = (Button) view.findViewById(R.id.left_button);
		doneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CityManager.getInstance(mContext).saveCityName(mCurrentCityID,
						mCityName);
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
					Intent intent = new Intent(mContext, MainActivity.class);
					intent.putExtra(CITY_ID_TO_LOAD, 2);
					mContext.startActivity(intent);
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
		mTimeTextView.setText(CommonUtil.getCurrentTime(mContext, mCityName));
		mDateTextView.setText(CommonUtil.getCurrentDate(mContext, mCityName));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ArrayList<String> citylList = CommonUtil
				.getCitiesOfTimezone(position - 12);
		String[] cities = new String[citylList.size()];
		cities = (String[]) citylList.toArray(cities);
		final String[] strs = cities;
		new AlertDialog.Builder(mContext).setTitle("Choose a city")
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
