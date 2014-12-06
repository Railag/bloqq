package com.bloqq.sqlite;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.webkit.WebBackForwardList;

public class UpdateHistDbTask extends AsyncTask<WebBackForwardList, Void, Void> {

	private final Context mContext;

	public UpdateHistDbTask(Context context) {
		mContext = context;
	}

	@Override
	protected Void doInBackground(WebBackForwardList... params) {
		WebBackForwardList hist = params[0];
		ContentResolver resolver = mContext.getContentResolver();
		ContentValues values = new ContentValues();

		if (hist.getCurrentItem() != null) {
			Time today = new Time(Time.getCurrentTimezone());
			today.setToNow();
			String date = checkZero(today.monthDay) + "."
					+ checkZero(today.month) + "." + today.year;
			String time = checkZero(today.hour) + ":" + checkZero(today.minute)
					+ ":" + checkZero(today.second);
			String datetime = date + " " + time;
			Log.i("DEBUG", datetime);
			values.put(BloqqDBHelper.Hist.COLUMN_TITLE, hist.getCurrentItem()
					.getTitle());
			values.put(BloqqDBHelper.Hist.COLUMN_URL, hist.getCurrentItem()
					.getOriginalUrl());
			values.put(BloqqDBHelper.Hist.COLUMN_DATE, datetime);

			resolver.insert(BloqqDBHelper.Hist.CONTENT_URI_HIST_DB, values);
		}
		return null;
	}

	private String checkZero(int check) {
		return check > 9 ? String.valueOf(check) : "0" + String.valueOf(check);
	}
}
