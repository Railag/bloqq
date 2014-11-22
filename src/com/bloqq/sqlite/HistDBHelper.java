package com.bloqq.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class HistDBHelper extends SQLiteOpenHelper {

	public static final class Hist implements BaseColumns {
		public static final Uri CONTENT_URI_HIST_DB = Uri
				.parse("content://com.bloqq.sqlite.HistContentProvider/hist");
		public static final String TABLE_NAME_HIST = "hist";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_URL = "url";
	}

	private static final String DB_NAME = "hist.db";
	private static final int VERSION = 1;
	private static final String CREATE_TABLE_HOME = "CREATE TABLE "
			+ Hist.TABLE_NAME_HIST + "(" + Hist._ID + " INTEGER PRIMARY KEY, "
			+ Hist.COLUMN_TITLE + " TEXT, " + Hist.COLUMN_URL + " TEXT)";

	public HistDBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("DEBUG", "onCreate DB");
		db.execSQL(CREATE_TABLE_HOME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("DEBUG", "onUpgrade");
	}

	public static String getDbName() {
		return DB_NAME;
	}
}
