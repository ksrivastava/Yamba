package com.example.kaustubh.yamba;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

	static final String[] FROM = { StatusData.C_CREATED_AT, StatusData.C_USER,
			StatusData.C_TEXT };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };

	public TimelineAdapter(Context context, Cursor c) {
		super(context, R.layout.row, c, FROM, TO);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(row, context, cursor);
		 long timestamp = cursor.getLong(cursor.getColumnIndex(StatusData.C_CREATED_AT));
		 TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt); 
		 textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}
	
}
