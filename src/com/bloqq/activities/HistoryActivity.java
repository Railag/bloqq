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

public class HistoryActivity extends ListActivity {

	private Cursor mCursor;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hist);

		mCursor = getContentResolver().query(
				BloqqDBHelper.Hist.CONTENT_URI_HIST_DB, null, null, null,
				BloqqDBHelper.Hist.COLUMN_DATE + " DESC");

		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.hist_item,
				mCursor, new String[] { BloqqDBHelper.Hist.COLUMN_TITLE,
						BloqqDBHelper.Hist.COLUMN_URL,
						BloqqDBHelper.Hist.COLUMN_DATE }, new int[] {
						R.id.hist_item_title, R.id.hist_item_url,
						R.id.hist_item_date });

		setListAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View item,
					int position, long id) {
				Cursor itemCursor = (Cursor) getListAdapter().getItem(position);

				String url = itemCursor.getString(itemCursor
						.getColumnIndex(BloqqDBHelper.Hist.COLUMN_URL));

				Intent data = new Intent();
				data.putExtra(MainActivity.EXTRA_URL, url);
				setResult(RESULT_OK, data);
				finish();
			}
		});

	}
}
