package com.bloqq.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.bloqq.cache.App;

public class BloqqContentProvider extends ContentProvider {

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		App app = (App) getContext();
		SQLiteDatabase db = app.getDB();
		Cursor cur = db.query(uri.getPath().replace('/', ' '), projection,
				selection, selectionArgs, null, null, sortOrder);
		// BloqqDBHelper.Hist.COLUMN_URL + " DESC");
		return cur;
	}

	@Override
	public String getType(Uri uri) {
		Log.i("DEBUG", "getType " + uri.toString());
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		App app = (App) getContext();
		SQLiteDatabase db = app.getDB();
		long cur = db.insert(uri.getPath().replace('/', ' '), null, values);
		return Uri.parse("" + cur);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.i("DEBUG", "delete " + uri.toString());
		App app = (App) getContext();
		SQLiteDatabase db = app.getDB();
		int deletedRowsNumber = db.delete(uri.getPath().replace('/', ' '),
				selection, selectionArgs);
		Log.i("DEBUG", deletedRowsNumber + " rows deleted");
		return deletedRowsNumber;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.i("DEBUG", "update " + uri.toString());
		App app = (App) getContext();
		SQLiteDatabase db = app.getDB();
		int result = db.update(uri.getPath().replace('/', ' '), values,
				selection, selectionArgs);
		Log.i("DEBUG", "Update result: " + result);
		return result;
	}
}
