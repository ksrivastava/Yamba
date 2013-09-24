package com.example.kaustubh.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity {
	YambaApplication yamba;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		yamba = (YambaApplication) getApplication();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * We are going to add the options menu option on the statusActivity
		 * screen. When someone presses the options (hardware) button, we
		 * inflate the menu from res/menu/menu.xml The menu.xml has the options
		 * the user can go to
		 * 
		 * Called only once first time menu is opened
		 */
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * If a menu item is selected. Switch by id of the selected item Sends
		 * intents to specific Activity/Service
		 */
		switch (item.getItemId()) {
		case R.id.itemToggleServices:
			if (yamba.isServiceRunning()) 
				stopService(new Intent(this, UpdaterService.class));
			 else
				startService(new Intent(this, UpdaterService.class));
			break;
		case R.id.itemStatus:
			startActivity(new Intent(this, StatusActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemTimeline:
			startActivity(new Intent(this, TimelineActivity.class).addFlags(
					Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
					Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemPurge:
			((YambaApplication) getApplication()).getStatusData().delete();
			Toast.makeText(this, R.string.msgAllDataPurged, Toast.LENGTH_LONG).show();
			break;
		}
		return true;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		MenuItem toggleItem = menu.findItem(R.id.itemToggleServices);
		if (yamba.isServiceRunning())  {
			toggleItem.setTitle(R.string.titleServiceStop);
			toggleItem.setIcon(android.R.drawable.ic_media_pause);
		}
		
		else {
			toggleItem.setTitle(R.string.titleServiceStart);
			toggleItem.setIcon(android.R.drawable.ic_media_play);
		}
		
		return true;
	}

}
