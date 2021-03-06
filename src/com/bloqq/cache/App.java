package com.bloqq.cache;

import com.bloqq.sqlite.BloqqDBHelper;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class App extends Application {

	private static DiskCache sDiskCache;

	private static SQLiteDatabase sDb;

	private static BloqqDBHelper sHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		createDb();

		try {
			sDiskCache = new DiskCache(getApplicationContext(), "icons",
					10 * 1024 * 1024, CompressFormat.JPEG, 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DiskCache getDiskCache() {
		return sDiskCache;
	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	public SQLiteDatabase getDB() {
		return sDb;
	}

	public void createDb() {
		sHelper = new BloqqDBHelper(this);
		sDb = sHelper.getWritableDatabase();
	}
}
