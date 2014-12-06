package com.bloqq.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.bloqq.R;
import com.bloqq.sqlite.BloqqDBHelper;

public class FavoriteActivity extends ListActivity {

	private Cursor mCursor;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav);

		mCursor = getContentResolver().query(
				BloqqDBHelper.Fav.CONTENT_URI_FAV_DB, null, null, null,
				BloqqDBHelper.Fav.COLUMN_TITLE + " DESC");

		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.fav_item,
				mCursor, new String[] { BloqqDBHelper.Fav.COLUMN_TITLE,
						BloqqDBHelper.Fav.COLUMN_URL }, new int[] {
						R.id.fav_item_title, R.id.fav_item_url });

		setListAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View item,
					int position, long id) {
				Cursor itemCursor = (Cursor) getListAdapter().getItem(position);

				String url = itemCursor.getString(itemCursor
						.getColumnIndex(BloqqDBHelper.Fav.COLUMN_URL));
				Intent data = new Intent();
				data.putExtra(MainActivity.EXTRA_URL, url);
				setResult(RESULT_OK, data);
				finish();
			}
		});

	}
}
