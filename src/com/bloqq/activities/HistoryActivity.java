package com.bloqq.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.bloqq.R;
import com.bloqq.sqlite.HistDBHelper;

public class HistoryActivity extends ListActivity {

	private Cursor mCursor;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hist);

		// Query for all people contacts using the Contacts.People convenience
		// class.
		// Put a managed wrapper around the retrieved cursor so we don't have to
		// worry about
		// requerying or closing it as the activity changes state.
		mCursor = getContentResolver().query(
				HistDBHelper.Hist.CONTENT_URI_HIST_DB, null, null, null, null);
		// startManagingCursor(mCursor);

		// Now create a new list adapter bound to the cursor.
		// SimpleListAdapter is designed for binding to a Cursor.
		ListAdapter adapter = new SimpleCursorAdapter(this, // Context.
				R.layout.hist_item, // Specify the row template
									// to use (here, two
									// columns bound to the
									// two retrieved
									// cursorrows).
				mCursor, // Pass in the cursor to bind to.
				new String[] { HistDBHelper.Hist.COLUMN_TITLE,
						HistDBHelper.Hist.COLUMN_URL }, // Array of cursor
														// columns to bind to.
				new int[] { R.id.hist_item_title, R.id.hist_item_url }); // Parallel
																			// array
																			// of
																			// which
																			// template
																			// objects
																			// to
																			// bind
																			// to
																			// those
																			// columns.

		// Bind to our new adapter.
		setListAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View item,
					int position, long id) {
				Cursor itemCursor = (Cursor) getListAdapter().getItem(position);

				String title = itemCursor.getString(itemCursor
						.getColumnIndex(HistDBHelper.Hist.COLUMN_TITLE));
				String url = itemCursor.getString(itemCursor
						.getColumnIndex(HistDBHelper.Hist.COLUMN_URL));
				Log.i("DEBUG", getListAdapter().getItem(position).getClass()
						.getSimpleName());
				Log.i("DEBUG", "TITLE: " + title + " | URL: " + url);
				Intent data = new Intent();
				data.putExtra(MainActivity.EXTRA_URL, url);
				setResult(RESULT_OK, data);
				finish();
			}
		});

	}
}
