package org.qris.timereport;

import org.qris.timereport.R;
import org.qris.timereport.Constants;
import org.qris.timereport.ProjectList.ProjectListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import static org.qris.timereport.Constants.*;

public class ActivityList  extends Activity implements OnItemClickListener  {
	static final boolean DEBUG = true;
	static final String TAG = "ActivityList";

	private static String ORDER_BY = ACTIVITY + " DESC";
	
	private LayoutInflater mFactory;
	private ListView mActList;

	private Cursor mCursor;

	private boolean mPicker = false;

	private final Handler mHandler = new Handler();
	
	private int mIdIndex;
	private int mActIndex;
	private int mActNrIndex;
	private int mStatusIndex;
	
	public class ActivityListAdapter extends CursorAdapter {

		public ActivityListAdapter(Context context, Cursor c) {
			super(context, c);
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return (mFactory.inflate(R.layout.activity_item, parent, false));
		}

		public void bindView(View view, Context context, Cursor cursor) {
			final int id = cursor.getInt(mIdIndex);
			final TextView act = (TextView) view
					.findViewById(R.id.ActName);
			final TextView actnr = (TextView) view
					.findViewById(R.id.ActNumber);

			act.setText(cursor.getString(mActIndex));
			actnr.setText(cursor.getString(mActNrIndex));

			CheckBox onButton = (CheckBox) view
					.findViewById(R.id.StatusCheckBox);
			// No checkbox when we only pick an item from the list
			onButton.setVisibility(mPicker ? View.GONE : View.VISIBLE);
			// if no picker have no attributes to the invisible checkbox
			if (!mPicker) {
				onButton.setChecked(cursor.getInt(mStatusIndex) == 1);
				onButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						boolean isChecked = ((CheckBox) v).isChecked();
						ContentValues values = new ContentValues();
						values.put(Constants.STATUS, isChecked);
						Uri uri = ContentUris.withAppendedId(getIntent()
								.getData(), id);
						getContentResolver().update(uri, values, null, null);
					}
				});
			}
		}

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFactory = LayoutInflater.from(this);

		final Intent intent = getIntent();

		if (intent.getData() == null) {
			intent.setData(Constants.ACTIVITY_URI);
		}
		if (Intent.ACTION_PICK.equals(getIntent().getAction())) {
			mCursor = getActivities(Constants.STATUS + "='1'");
			mPicker = true;
		} else {
			mCursor = getActivities(null);
			mPicker = false;
		}
		
		mIdIndex = mCursor.getColumnIndex(Constants._ID);
		mActIndex = mCursor.getColumnIndex(Constants.ACTIVITY);
		mActNrIndex = mCursor.getColumnIndex(Constants.ACTIVITY_NR);
		mStatusIndex = mCursor.getColumnIndex(Constants.STATUS);

		updateLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//mCursor.deactivate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Send a message to avoid a possible ANR.
		mHandler.post(new Runnable() {
			public void run() {
				updateLayout();
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.projecteditmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_add:
			Intent intent = new Intent(this, ActivityEditor.class);
			Uri uri = getIntent().getData();
			intent.setData(uri);
			intent.setAction("Add");
			startActivity(intent);
			return true;
		}
		return false;
	}

	private Cursor getActivities(String selection) {
		// TODO Auto-generated method stub
		return managedQuery(ACTIVITY_URI, FROM_ACTIVITY_PROJECTION, selection,
				null, ORDER_BY);
	}

	private void updateLayout() {
		setContentView(R.layout.projectlist);
		mActList = (ListView) findViewById(R.id.list);
		mActList.setAdapter(new ActivityListAdapter(this, mCursor));
		mActList.setItemsCanFocus(false);
		mActList.setVerticalScrollBarEnabled(true);
		mActList.setFastScrollEnabled(true);
		mActList.setOnItemClickListener(this);
		// mProjectList.setOnCreateContextMenuListener(this);
	}

	void returnResult(String result_string) {
		// Create the Intent object
		Intent i = new Intent();
		// Put an extra named "result" in the intent
		i.putExtra("result", result_string);
		// Make this Intent the result for this activity
		setResult(RESULT_OK, i);
		// End this activity
		finish();
	}
	
	@Override
	public void onItemClick(AdapterView parent, View v, int pos, long id) {
		if (DEBUG)
			Log.d(TAG, "selected" + id);
		if (Intent.ACTION_PICK.equals(getIntent().getAction())) {
			Cursor mCursor = managedQuery(
					ContentUris.withAppendedId(getIntent().getData(), id),
					Constants.FROM_ACTIVITY_PROJECTION, null, null, null);
			int actIndex = mCursor.getColumnIndex(Constants.ACTIVITY);
			mCursor.moveToFirst();
			String selected = mCursor.getString(actIndex);
			returnResult(selected);
		} else {
			Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
			Intent intent = new Intent(this, ActivityEditor.class);
			intent.setData(uri);
			intent.setAction("Edit");
			startActivity(intent);
		}

	}

}
