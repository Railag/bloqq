package com.bloqq.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class BloqqDBHelper extends SQLiteOpenHelper {

	public static final class Hist implements BaseColumns {
		public static final Uri CONTENT_URI_HIST_DB = Uri
				.parse("content://com.bloqq.sqlite.BloqqContentProvider/hist");
		public static final String TABLE_NAME_HIST = "hist";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_URL = "url";
		public static final String COLUMN_DATE = "date";
	}

	public static final class Fav implements BaseColumns {
		public static final Uri CONTENT_URI_FAV_DB = Uri
				.parse("content://com.bloqq.sqlite.BloqqContentProvider/fav");
		public static final String TABLE_NAME_FAV = "fav";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_URL = "url";
	}

	private static final String DB_NAME = "bloqq.db";
	private static final int VERSION = 1;
	private static final String CREATE_TABLE_HOME = "CREATE TABLE "
			+ Hist.TABLE_NAME_HIST + "(" + Hist._ID + " INTEGER PRIMARY KEY, "
			+ Hist.COLUMN_TITLE + " TEXT, " + Hist.COLUMN_DATE + " TEXT, "
			+ Hist.COLUMN_URL + " TEXT)";

	private static final String CREATE_TABLE_FAV = "CREATE TABLE "
			+ Fav.TABLE_NAME_FAV + "(" + Fav._ID + " INTEGER PRIMARY KEY, "
			+ Fav.COLUMN_TITLE + " TEXT, " + Fav.COLUMN_URL + " TEXT)";

	public BloqqDBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("DEBUG", "onCreate DB");
		db.execSQL(CREATE_TABLE_HOME);
		db.execSQL(CREATE_TABLE_FAV);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("DEBUG", "onUpgrade");
	}

	public static String getDbName() {
		return DB_NAME;
	}
}
