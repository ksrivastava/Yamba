package com.example.kaustubh.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener,
		TextWatcher {
	private static final String TAG = "StatusActivity";
	/*
	 * This is for logging on LogCat. The TAG is most commonly the class. We can
	 * call Log.d(TAG, message) to print the tag and a message. .d = debug .i =
	 * info .e = error .w = warning .wtf = what a terrible failure
	 */

	EditText editText;
	Button updateButton;
	TextView textCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		/*
		 * Bundle is a small amount of data that can be passed into the activity
		 * via the intent that started it. The data is limited to basic data
		 * types.
		 */
		super.onCreate(savedInstanceState); // You want to first make a call to
											// the original method in the parent
		setContentView(R.layout.activity_status);
		/*
		 * Loads the UI from the XML file and inflates it into Java memory
		 * space.
		 * 
		 * Remember: The R class is the automatically generated set of pointers
		 * that helps connect the world of Java to XML and other resources in
		 * the /res folder Similarly, R.layout.activity_status points to
		 * /res/layout/activity_status.xml
		 */

		// Find views
		editText = (EditText) findViewById(R.id.editText);
		// Finds the views and casts it appropriately
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		textCount = (TextView) findViewById(R.id.textCount);

		textCount.setText(Integer.toString(140));
		textCount.setTextColor(Color.GREEN);

		editText.addTextChangedListener(this);
		updateButton.setOnClickListener(this);
		// Register the button to notify this - StatusActivity - when it gets
		// clicked.
	}

	class PostToTwitter extends AsyncTask<String, Integer, String> {
		/*
		 * AsyncTask helps us handle long operations that need to report to the
		 * UI thread.
		 */
		@Override
		protected String doInBackground(String... statuses) {
			/*
			 * What to do in the background. String... declares that it is an
			 * array of strings
			 * 
			 * Returns a string
			 */
			try {
				YambaApplication yamba = (YambaApplication) getApplication();
				// We get a reference to the Application object and cast it to
				// YambaApplication
				Twitter twitter = yamba.getTwitter();
				// Application gives the twitter object to the statusActivity.
				// Could have been new or existing.

				Log.d(TAG, "Posting: " + statuses[0]);
				twitter.updateStatus(statuses[0]);
				Log.d(TAG, "Successfully posted " + statuses[0]);
				return "Success";
			} catch (TwitterException e) {
				Log.e(TAG, "Failed to connect to Twitter service");
				return "Failed To Post " + statuses[0];
			} catch (NullPointerException e) {
				Log.e(TAG, "Caught Null Pointer Exception");
				return "Failed To Post " + statuses[0];
			}

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			/*
			 * What to do when there is some progress. Ex. some % progress bar.
			 */
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			/*
			 * What to do when the task is complete. The argument is what
			 * doInBackground() returns
			 */
			super.onPostExecute(result);

			// Don't forget .show()
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
					.show();
		}

	}


	@Override
	public void onClick(View v) {
		/*
		 * The view is the object that triggered this function i.e. the button
		 */
		String status = editText.getText().toString();
		/*
		 * We can't call twitter.updateStatus(status) because of latency issues.
		 * It may block out UI thread.
		 * 
		 * But we cannot use the Thread class because another thread is not
		 * allowed to update elements in the main UI thread. To do this, we
		 * would have to synchronize with the current state of its objects.
		 * 
		 * Therefore, we must use AsyncTest
		 */
		new PostToTwitter().execute(status);
		// executes Async task
		Log.d(TAG, "Updating");

	}

	@Override
	public void afterTextChanged(Editable statusText) {
		int count = 140 - statusText.length();
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN);
		if (count < 10)
			textCount.setTextColor(Color.YELLOW);
		if (count < 0)
			textCount.setTextColor(Color.RED);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		/*
		 * Called just before the actual text replacement is completed
		 */

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		/*
		 * Called just after the actual text replacement is completed
		 */
	}

}
