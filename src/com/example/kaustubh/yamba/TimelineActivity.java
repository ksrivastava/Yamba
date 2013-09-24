package com.example.kaustubh.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kaustubh.yamba.StatusData.DbHelper;

public class TimelineActivity extends BaseActivity {
	private static final String TAG = "TimelineActivity";
	//static final String SEND_TIMELINE_NOTIFICATIONS = "com.example.kaustubh.SEND_TIMELINE_NOTIFICATIONS"; // messes up auto-update
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	TimelineAdapter adapter;
	StatusData statusData;
	TimelineReceiver receiver;
	IntentFilter filter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		if (yamba.getPrefs().getString("username", null) == null) {
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
		
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		statusData = yamba.getStatusData();
		receiver = new TimelineReceiver();
		filter = new IntentFilter(UpdaterService.NEW_STATUS_INTENT);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		statusData.close();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.setupList();
		
		/*
		 * Tells activity to start managing the cursor's life-cycle the same way
		 * it manages its own
		 */

	}
	
	private void setupList() {
		cursor = statusData.getStatusUpdates();
		startManagingCursor(cursor);
		
		adapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(adapter);
		
	}
	
	class TimelineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Cursor before");
			cursor = yamba.getStatusData().getStatusUpdates();
			Log.d(TAG, "Cursor after");
			adapter.changeCursor(cursor); // requery()
			Log.d(TAG, "changeCursor");
			adapter.notifyDataSetChanged();
			Log.d(TAG, "Adapter Updated");
		}
		
	}

}
