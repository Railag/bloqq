package com.bloqq.sqlite;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class UpdateFavDbTask extends AsyncTask<String, Void, Void> {

	private final Context mContext;

	public UpdateFavDbTask(Context context) {
		mContext = context;
	}

	@Override
	protected Void doInBackground(String... params) {
		String favTitle = params[0];
		String favUrl = params[1];
		ContentResolver resolver = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		if (favUrl != null) {
			values.put(BloqqDBHelper.Fav.COLUMN_TITLE, favTitle);
			values.put(BloqqDBHelper.Fav.COLUMN_URL, favUrl);

			resolver.insert(BloqqDBHelper.Fav.CONTENT_URI_FAV_DB, values);
		}
		return null;
	}
}
