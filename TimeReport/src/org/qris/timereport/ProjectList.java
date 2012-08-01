package org.qris.timereport;

import org.qris.timereport.R;
import org.qris.timereport.Constants;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
//import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
//import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import static org.qris.timereport.Constants.*;

public class ProjectList extends Activity implements OnItemClickListener,
		OnItemSelectedListener {
	static final boolean DEBUG = true;
	static final String TAG = "ProjectList";

	private LayoutInflater mFactory;
	private ListView mProjectList;

	private Cursor mCursor;

	private boolean mPicker = false;

	private final Handler mHandler = new Handler();

//	private int mLayoutResId = R.layout.project_item;
//	private View mPrevView = null;
	
	private int mIdIndex;
	private int mProjectIndex;
	private int mProjectNrIndex;
	private int mRateIndex;
	private int mStatusIndex;


	// public static final String[] FROM = new String[] { PROJECT, PROJECT_NR,
	// RATE, STATUS };
	// private static int[] TO = { R.id.ProjectName, R.id.ProjectNumber,
	// R.id.ProjectRate, R.id.StatusText };
	private static String ORDER_BY = PROJECT + " DESC";

	public class ProjectListAdapter extends CursorAdapter {

		public ProjectListAdapter(Context context, Cursor c) {
			super(context, c);
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View newview = mFactory.inflate(R.layout.project_item, parent,
					false);
			return (newview);
		}

		public void bindView(View view, Context context, Cursor cursor) {
			final int id = cursor.getInt(mIdIndex);
			final TextView proj = (TextView) view
					.findViewById(R.id.ProjectName);
			final TextView projnr = (TextView) view
					.findViewById(R.id.ProjectNumber);
			final TextView rate = (TextView) view
					.findViewById(R.id.ProjectRate);

			proj.setText(cursor.getString(mProjectIndex));
			projnr.setText(cursor.getString(mProjectNrIndex));
			rate.setText(Integer.toString(cursor.getInt(mRateIndex)));

			// if(view.isFocused())
			// proj.setText("selected!");

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
			intent.setData(Constants.PROJECT_URI);
		}
		if (Intent.ACTION_PICK.equals(getIntent().getAction())) {
			mCursor = getProjects(Constants.STATUS + "='1'");
			mPicker = true;
		} else {
			mCursor = getProjects(null);
			mPicker = false;
		}
		
		mIdIndex = mCursor.getColumnIndex(Constants._ID);
		mProjectIndex = mCursor.getColumnIndex(Constants.PROJECT);
		mProjectNrIndex = mCursor.getColumnIndex(Constants.PROJECT_NR);
		mRateIndex = mCursor.getColumnIndex(Constants.RATE);
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

	private void updateLayout() {
		setContentView(R.layout.projectlist);
		mProjectList = (ListView) findViewById(R.id.list);
		mProjectList.setAdapter(new ProjectListAdapter(this, mCursor));
		mProjectList.setItemsCanFocus(false);
		mProjectList.setVerticalScrollBarEnabled(true);
		mProjectList.setFastScrollEnabled(true);
		mProjectList.setOnItemClickListener(this);
		mProjectList.setOnItemSelectedListener(this);
		// mProjectList.setOnCreateContextMenuListener(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.projecteditmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_add:
			Intent intent = new Intent(this, ProjectEditor.class);
			Uri uri = getIntent().getData();
			intent.setData(uri);
			intent.setAction("Add");
			startActivity(intent);
			return true;
		}
		return false;
	}

	private Cursor getProjects(String selection) {
		return managedQuery(PROJECT_URI, Constants.FROM_PROJECT_PROJECTION,
				selection, null, ORDER_BY);
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
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		if (DEBUG)
			Log.d(TAG, "selected" + id);
		if (Intent.ACTION_PICK.equals(getIntent().getAction())) {
			Cursor mCursor = managedQuery(
					ContentUris.withAppendedId(getIntent().getData(), id),
					Constants.FROM_PROJECT_PROJECTION, null, null, null);
			int projectIndex = mCursor.getColumnIndex(Constants.PROJECT);
			mCursor.moveToFirst();
			String selected = mCursor.getString(projectIndex);
			returnResult(selected);
		} else {
			Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
			// startActivity(new Intent(Intent.ACTION_EDIT, uri));
			Intent intent = new Intent(this, ProjectEditor.class);
			intent.setData(uri);
			intent.setAction("Edit");
			startActivity(intent);
		}

	}

	@Override
	// parent The AdapterView where the selection happened
	// view The view within the AdapterView that was clicked
	// position The position of the view in the adapter
	// id The row id of the item that is selected
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
//		if (mPrevView != null) {
//			mPrevView.invalidate();
//		}
//		mPrevView = view;
//		final TextView proj = (TextView) view.findViewById(R.id.ProjectName);
//		proj.setText("HELP");
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}
