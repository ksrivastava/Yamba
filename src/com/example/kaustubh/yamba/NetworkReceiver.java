package com.example.kaustubh.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
	
	public static final String TAG = "NetworkReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		boolean isNewtorkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (isNewtorkDown) {
			Log.e(TAG, "Network is down");
			context.stopService(new Intent(context, UpdaterService.class));
		}
		else {
			Log.d(TAG, "Network is back on");
			context.startService(new Intent(context, UpdaterService.class));
		}
	}

}
