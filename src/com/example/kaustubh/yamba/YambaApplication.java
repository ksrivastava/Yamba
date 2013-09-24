package com.example.kaustubh.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = "Application";
	Twitter twitter;
	private SharedPreferences prefs;
	private boolean serviceRunning;
	private StatusData statusData;
	@Override
	public void onCreate() {
		/*
		 * Called when App is created
		 */
		super.onCreate();
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		/*
		 * Each app has its own shared preferences available to all components
		 * of the app and this loads the default preferences that are saved on
		 * the phone
		 */
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		/*
		 * Each user can change preferences. So this listener is a mechanism to
		 * notify this activity that the old values are stale
		 */
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public synchronized Twitter getTwitter() {
		/*
		 * 'synchronized' means that only one thread can call the method at any
		 * time
		 */
		if (this.twitter == null) {
			String username, password, apiRoot;
			boolean setDefault;
			/*
			 * We get the username and password from the shared preference
			 * object.
			 */
			username = this.prefs.getString("username", "");
			password = this.prefs.getString("password", "");
			apiRoot = this.prefs.getString("apiRoot",
					"http://yamba.marakana.com/api");
			setDefault = this.prefs.getBoolean("setDefault", true);

			if (setDefault) {
				username = "student";
				password = "password";
				apiRoot = "http://yamba.marakana.com/api";
			}

			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
					&& !TextUtils.isEmpty(apiRoot)) {
				// Create a twitter object
				this.twitter = new Twitter(username, password);
				this.twitter.setAPIRootUrl(apiRoot);
				Log.d(TAG, "Create: " + username + " " + password + " "
						+ apiRoot);
			}

			else {
				Toast.makeText(YambaApplication.this,
						"Problem with setting credentials", Toast.LENGTH_LONG)
						.show();
			}
		}
		return this.twitter;
	}

	public StatusData getStatusData() {
		if (statusData == null)
			statusData = new StatusData(this);
		return statusData;
	}

	public synchronized int fetchStatusUpdates() {
		Log.d(TAG, "Fetching statuses");
		Twitter twitter = this.getTwitter();
		if (twitter == null) {
			Log.e(TAG, "Twitter connection not initalized");
			return 0;
		}
		try {
			List<Status> statusUpdates = twitter.getFriendsTimeline();
			StatusData statusData = this.getStatusData();
			long latestStatusCreatedAtTime = statusData.getLatestStatusCreatedAtTime();
			int count = 0;
			ContentValues values = new ContentValues();
			for (Status status : statusUpdates) {
				values.put(StatusData.C_ID, status.getId());
				long createdAt = status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT, createdAt);
				values.put(StatusData.C_USER, status.getUser().getName());
				values.put(StatusData.C_TEXT, status.getText());
				Log.d(TAG, "beforeInsert");
				this.getStatusData().insertOrIgnore(values);
				Log.d(TAG, "afterInsert");
				if (latestStatusCreatedAtTime < createdAt) {
					count++;
				}
			}
			
			 Log.d(TAG, count > 0 ? "Got " + count + " status updates"
				        : "No new status updates");
			 return count;
		}
		
		catch(RuntimeException e) {
			Log.e(TAG, "Failed to fetch status updates");
			return 0;
		}
		
	}

	@Override
	public synchronized void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {
		/*
		 * Called when user changes preferences. Invalidates the current Twitter
		 */
		this.twitter = null;
		Log.d(TAG, "Twitter object invalidated");
	}

	public boolean isServiceRunning() {
		return serviceRunning;
	}

	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}
	
	public SharedPreferences getPrefs() {
		return prefs;
	}
}
