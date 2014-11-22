package com.bloqq.sqlite;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
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
		/*
		 * for (int i = 0; i < hist.getSize(); i++) {
		 * values.put(HistDBHelper.Hist.COLUMN_TITLE, hist.getItemAtIndex(i)
		 * .getTitle()); values.put(HistDBHelper.Hist.COLUMN_URL,
		 * hist.getItemAtIndex(i) .getOriginalUrl());
		 * 
		 * resolver.insert(HistDBHelper.Hist.CONTENT_URI_HIST_DB, values); }
		 */
		values.put(HistDBHelper.Hist.COLUMN_TITLE, hist.getCurrentItem()
				.getTitle());
		values.put(HistDBHelper.Hist.COLUMN_URL, hist.getCurrentItem()
				.getOriginalUrl());

		resolver.insert(HistDBHelper.Hist.CONTENT_URI_HIST_DB, values);
		return null;
	}
}
