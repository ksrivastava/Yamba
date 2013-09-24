package com.example.kaustubh.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	/*
	 * Unbound service, where the life-cycle of the service is not dependent on
	 * the Activity
	 * 
	 * The work of a service should often be in a separate thread from the main
	 * UI thread
	 */
	private static final String TAG = "UpdaterService";
	private static final String STATUS = "Timeline";
	public static final String NEW_STATUS_INTENT = "NEW_EXTRA_STATUS_COUNT";
	static final int DELAY = 10000; // 10 seconds
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;

	@Override
	public IBinder onBind(Intent intent) {
		/*
		 * onBind is for bound services that return the actual implementation of
		 * a 'binder'
		 */
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		this.yamba = (YambaApplication) getApplication();
		Log.d(STATUS, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*
		 * The service could get multiple requests to start again, and each of
		 * them will cause the onStartCommand() to execute.
		 * 
		 * START_STICKY is used as a flag to indicate this service is started
		 * and stopped explicitly
		 */

		super.onStartCommand(intent, flags, startId);
		Log.d(STATUS, "Starting Service from Boot");
		if (this.runFlag) {
			Log.d(STATUS, "New Service");
			this.updater.interrupt();
			this.updater = null;
			this.updater = new Updater();
		}

		this.runFlag = true;
		this.yamba.setServiceRunning(true);
		this.updater.start();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.runFlag = false;
		this.yamba.setServiceRunning(false);
		this.updater.interrupt(); // Stops the thread
		this.updater = null; // Helps GC
	}

	private class Updater extends Thread {

		//private static final String RECEIVE_TIMELINE_NOTIFICATIONS = "com.example.kaustubh.yamba.RECEIVE_TIMELINE_NOTIFICATIONS";
		
		// Java generics
		Intent intent;

		public Updater() {
			/*
			 * This gives our thread a name. Helps in debugging.
			 */
			super("UpdaterService-Updater");
		}

		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			// Creates reference to the service
			while (updaterService.runFlag) {
				Log.d(TAG, "Background Thread Running");
				try {
					YambaApplication yamba = (YambaApplication) updaterService
							.getApplication();
					int newUpdates = yamba.fetchStatusUpdates();
					if (newUpdates > 0) {
						intent = new Intent(NEW_STATUS_INTENT);
						updaterService.sendBroadcast(intent);
						Log.d(TAG, "We have new statuses");
					}
					Thread.sleep(DELAY);

				} catch (InterruptedException e) {
					/*
					 * Called when we signal interrupt() to a running thread.
					 */
					updaterService.runFlag = false;
				}
			}
		}

	}

}
