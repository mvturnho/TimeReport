package org.qris.timereport;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity; //import android.app.DatePickerDialog;
import android.app.AlertDialog; //import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;

import org.qris.timereport.R;
import org.qris.timereport.Constants;
import org.qris.timereport.dialogs.DatePickerDialog;
import org.qris.timereport.dialogs.TimePickerDialog;

import android.app.Dialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EventEditor extends Activity {
	static final boolean DEBUG = true;
	static final String TAG = "EventEditor";

	static final int PICK_PROJECT = 1;
	static final int PICK_ACTIVITY = 2;

	private String mPickedProject = null;
	private String mPickedActivity = null;

	class myProjectsListAdapter extends CursorAdapter {
		public myProjectsListAdapter(Context context, Cursor c) {
			super(context, c);
		}

		public void bindView(View view, Context context, Cursor cursor) {
			int columnIndex = cursor.getColumnIndexOrThrow(Constants.PROJECT);
			TextView tx = (TextView) view.findViewById(R.id.item_text);
			tx.setText(cursor.getString(columnIndex));
		}

		public String convertToString(Cursor cursor) {
			int columnIndex = cursor.getColumnIndexOrThrow(Constants.PROJECT);
			return cursor.getString(columnIndex);
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			return inflater.inflate(R.layout.search_row, parent, false);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (constraint != null) {
				String sel = Constants.STATUS + "='1' AND " + Constants.PROJECT
						+ " LIKE \'%" + constraint.toString() + "%\'";
				return getContentResolver().query(Constants.PROJECT_URI, null,
						sel, null, null);
				// return myDatabase.searchByTokenReturnName(" ");
			}
			return getContentResolver().query(Constants.PROJECT_URI, null,
					null, null, null); // returns
			// a
		}
	}

	class myActivityListAdapter extends CursorAdapter {
		public myActivityListAdapter(Context context, Cursor c) {
			super(context, c);
		}

		public void bindView(View view, Context context, Cursor cursor) {
			int columnIndex = cursor.getColumnIndexOrThrow(Constants.ACTIVITY);
			TextView tx = (TextView) view.findViewById(R.id.item_text);
			tx.setText(cursor.getString(columnIndex));
		}

		public String convertToString(Cursor cursor) {
			int columnIndex = cursor.getColumnIndexOrThrow(Constants.ACTIVITY);
			return cursor.getString(columnIndex);
		}

		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			return inflater.inflate(R.layout.search_row, parent, false);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (constraint != null) {
				String sel = Constants.STATUS + "='1' AND "
						+ Constants.ACTIVITY + " LIKE \'%"
						+ constraint.toString() + "%\'";
				return getContentResolver().query(Constants.ACTIVITY_URI, null,
						sel, null, null);
				// return myDatabase.searchByTokenReturnName(" ");
			}
			return getContentResolver().query(Constants.ACTIVITY_URI, null,
					null, null, null); // returns
			// a
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// super.onOptionsItemSelected(item);
		Intent intent = null;
		switch (item.getItemId()) {
		// case R.id.menu_edit_delete:
		// Uri uri = getIntent().getData();
		// getContentResolver().delete(uri, null, null);
		// finish();
		// return true;
		case R.id.menu_projects:
			if (DEBUG)
				Log.d(TAG, "menu_projects");
			intent = new Intent(this, ProjectList.class);
			startActivity(intent);
			return true;
		case R.id.menu_activities:
			if (DEBUG)
				Log.d(TAG, "menu_activities");
			intent = new Intent(this, ActivityList.class);
			startActivity(intent);
			return true;

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.itemeditmenu, menu);
		return true;
	}

	private Uri mUri;
	private Cursor mCursor = null;
	private int mIDIndex;
	private int mStartIndex;
	private int mDurationIndex;
	private int mProjectIndex;
	private int mProjectNrIndex;
	private int mActivityIndex;
	private int mActivityNrIndex;
	private int mNoteIndex;

	// private TextView mText;
	private Button mStartDate;
	private Button mStartTime;
	private Button mEndTime;

	private Button mDuration;
	private Button mDeleteButton;
	private Button mSaveButton;
	private Button mCancelButton;

	private AutoCompleteTextView mProject;
	private String mProjectNr;
	private AutoCompleteTextView mActivity;
	private String mActivityNr;
	private EditText mNote;
	private String mAction;
	private static final int START_DATE_DIALOG_ID = 0;
	private static final int START_TIME_DIALOG_ID = 1;
	private static final int END_TIME_DIALOG_ID = 2;
	private static final int DUR_TIME_DIALOG_ID = 3;
	private int dialog_id = 0;
	// private Calendar startCal, endCal;
	private Time startTime, endTime, durTime;

	private boolean mDirty = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventedit);
		if (DEBUG)
			Log.d(TAG, " getIntent");
		final Intent intent = getIntent();
		mAction = intent.getAction();
		mUri = intent.getData();
		if (DEBUG)
			Log.d(TAG, mAction);
		if (!mAction.matches("Add")) {
			if (DEBUG)
				Log.d(TAG, " getCursor");
			mCursor = managedQuery(mUri, Constants.FROM_CONTENT_PROJECTION,
					null, null, null);
			mIDIndex = mCursor.getColumnIndex(android.provider.BaseColumns._ID);
			mStartIndex = mCursor.getColumnIndex(Constants.START);
			mDurationIndex = mCursor.getColumnIndex(Constants.DURATION);
			mActivityIndex = mCursor.getColumnIndex(Constants.ACTIVITY);
			mProjectIndex = mCursor.getColumnIndex(Constants.PROJECT);
			mActivityNrIndex = mCursor.getColumnIndex(Constants.ACTIVITY_NR);
			mProjectNrIndex = mCursor.getColumnIndex(Constants.PROJECT_NR);
			mNoteIndex = mCursor.getColumnIndex(Constants.NOTE);
		}
		// mText = (TextView) findViewById(R.id.NoteText);
		mNote = (EditText) findViewById(R.id.edit_note_text);
		mStartTime = (Button) findViewById(R.id.edit_start_time_button);
		mStartDate = (Button) findViewById(R.id.edit_start_date_button);
		mEndTime = (Button) findViewById(R.id.edit_end_time_button);
		mDuration = (Button) findViewById(R.id.edit_duration_button);
		mProject = (AutoCompleteTextView) findViewById(R.id.edit_project_autotext);

		mActivity = (AutoCompleteTextView) findViewById(R.id.edit_act_autotext);

		startTime = new Time();
		durTime = new Time();
		endTime = new Time();

		mSaveButton = (Button) this.findViewById(R.id.button_save);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDirty = true;
				finish();
			}
		});
		mCancelButton = (Button) this.findViewById(R.id.button_cancel);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDirty = false;
				finish();

			}
		});
		mDeleteButton = (Button) this.findViewById(R.id.button_delete);
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okToDelete();
			}
		});

		mStartDate.setOnClickListener(startListener);
		mStartTime.setOnClickListener(startListener);
		mEndTime.setOnClickListener(startListener);
		mDuration.setOnClickListener(startListener);

		ImageButton ib = (ImageButton) findViewById(R.id.activity_popuplist_button);
		ib.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("vnd.android.cursor.dir/activity");
				startActivityForResult(intent, PICK_ACTIVITY);
			}
		});

		ib = (ImageButton) findViewById(R.id.project_popuplist_button);
		ib.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("vnd.android.cursor.dir/project");
				startActivityForResult(intent, PICK_PROJECT);
			}
		});

		myProjectsListAdapter plist = new myProjectsListAdapter(this, null);
		mProject.setAdapter(plist);
		mProject.setThreshold(1);

		myActivityListAdapter alist = new myActivityListAdapter(this, null);
		mActivity.setAdapter(alist);
		mActivity.setThreshold(1);

		mDeleteButton.setVisibility(Button.INVISIBLE);
	}

	private void okToDelete() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Sure");
		alertDialog.setMessage("Oké to delete this record?");
		alertDialog.setButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mDirty = false;
				Uri uri = getIntent().getData();
				getContentResolver().delete(uri, null, null);
				finish();
				return;
			}
		});
		alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		alertDialog.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// saveActivityPreferences();
		if (mDirty)
			saveRecord();
	}

	private void saveRecord() {
		String actnr = "";
		String projnr = "";
		ContentValues values = new ContentValues();
		// First get the corresponding project number
		projnr = getProjectNr(mProject.getText().toString());
		// next get the corresponding activity number
		actnr = getActivityNr(mActivity.getText().toString());

		values.put(Constants.START, startTime.toMillis(true));
		// values.put(Constants.DURATION, durCal.getTimeInMillis());
		values.put(Constants.DURATION, durTime.toMillis(false));
		values.put(Constants.PROJECT, mProject.getText().toString());
		values.put(Constants.PROJECT_NR, projnr);
		values.put(Constants.ACTIVITY, mActivity.getText().toString());
		values.put(Constants.ACTIVITY_NR, actnr);
		values.put(Constants.NOTE, mNote.getText().toString());
		if (!mAction.matches("Add"))
			getContentResolver().update(mUri, values, null, null);
		else
			getContentResolver().insert(mUri, values);
	}

	private String getProjectNr(String project_name) {
		String projnr = null;
		String sel = Constants.PROJECT + "=\'" + project_name + "\'";
		Cursor cur = getContentResolver().query(Constants.PROJECT_URI,
				Constants.FROM_PROJECT_PROJECTION, sel, null, null);
		int columnIndex = cur.getColumnIndex(Constants.PROJECT_NR);
		if (cur.moveToFirst()) {
			projnr = cur.getString(columnIndex);
		}
		cur.close();
		return projnr;
	}

	private String getActivityNr(String activity_name) {
		String actnr = null;
		String sel = Constants.ACTIVITY + "=\'" + activity_name + "\'";
		Cursor cur = getContentResolver().query(Constants.ACTIVITY_URI,
				Constants.FROM_ACTIVITY_PROJECTION, sel, null, null);
		int columnIndex = cur.getColumnIndex(Constants.ACTIVITY_NR);
		if (cur.moveToFirst()) {
			actnr = cur.getString(columnIndex);
		}
		cur.close();
		return actnr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String pname = null;
		String aname = null;
		durTime.clear("");
		if (mCursor != null) {
			// Make sure we are at the one and only row in the cursor.
			mCursor.moveToFirst();
			mNote.setText(mCursor.getString(mNoteIndex));
			pname = mCursor.getString(mProjectIndex);
			// mProjectNr.setText(mCursor.getString(mProjectNrIndex));
			aname = mCursor.getString(mActivityIndex);
			// mActivityNr.setText(mCursor.getString(mActivityNrIndex));
			startTime.set(mCursor.getLong(mStartIndex));
			startTime.normalize(false);
			durTime.set(mCursor.getLong(mDurationIndex));
			mDeleteButton.setVisibility(Button.VISIBLE);
		} else {
			startTime.setToNow();
			int year = startTime.year;
			int month = startTime.month;
			int monthDay = startTime.monthDay;
			durTime.set(0, 0, 4, monthDay, month, year);
		}
		endTime.set(0, startTime.minute + durTime.minute, startTime.hour
				+ durTime.hour, startTime.monthDay, startTime.month,
				startTime.year);
		endTime.normalize(false);
		mStartTime.setText(startTime.format(Constants.TIME_FORMAT));
		mStartDate.setText(startTime.format(Constants.DATE_FORMAT));
		mEndTime.setText(endTime.format(Constants.TIME_FORMAT));
		mDuration.setText(durTime.format(Constants.TIME_FORMAT));
		if (mPickedProject != null)
			pname = mPickedProject;
		if (mPickedActivity != null)
			aname = mPickedActivity;
		mProject.setText(pname);
		mActivity.setText(aname);
		// restoreActivityPreferences();
	}

	private OnClickListener startListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.edit_start_date_button:
				dialog_id = START_DATE_DIALOG_ID;
				showDialog(START_DATE_DIALOG_ID);
				break;
			case R.id.edit_start_time_button:
				dialog_id = START_TIME_DIALOG_ID;
				showDialog(START_TIME_DIALOG_ID);
				break;
			case R.id.edit_end_time_button:
				dialog_id = END_TIME_DIALOG_ID;
				showDialog(END_TIME_DIALOG_ID);
				break;
			case R.id.edit_duration_button:
				dialog_id = DUR_TIME_DIALOG_ID;
				showDialog(DUR_TIME_DIALOG_ID);
				break;
			}
		}
	};

	protected void saveActivityPreferences() {
		// Create or retrieve the activity preferences object.
		SharedPreferences activityPreferences = getPreferences(Activity.MODE_PRIVATE);
		// Retrieve an editor to modify the shared preferences.
		SharedPreferences.Editor editor = activityPreferences.edit();
		// Store new primitive types in the shared preferences object.
		editor.putString("project_name", mProject.getText().toString());
		editor.putString("activity_name", mActivity.getText().toString());
		// Commit changes.
		editor.commit();
	}

	protected void restoreActivityPreferences() {
		// Create or retrieve the activity preferences object.
		SharedPreferences activityPreferences = getPreferences(Activity.MODE_PRIVATE);
		// Retrieve an editor to modify the shared preferences.
		mProject.setText(activityPreferences.getString("project_name", ""));
		mActivity.setText(activityPreferences.getString("activity_name", ""));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		if (DEBUG)
			Log.d(TAG, "request=" + new Integer(requestCode).toString()
					+ "result=" + new Integer(resultCode).toString());
		if (data != null) {
			if (requestCode == PICK_PROJECT)
				mPickedProject = data.getStringExtra("result");
			if (requestCode == PICK_ACTIVITY)
				mPickedActivity = data.getStringExtra("result");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_TIME_DIALOG_ID:
		case END_TIME_DIALOG_ID:
		case DUR_TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, 0, 0, true);
		case START_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, 2010, 10, 10);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareDialog(int, android.app.Dialog)
	 */
	protected void onPrepareDialog(int id, Dialog d) {
		switch (id) {
		case START_TIME_DIALOG_ID:
			((TimePickerDialog) d).updateTime(startTime.hour, startTime.minute);
			break;
		case START_DATE_DIALOG_ID:
			((DatePickerDialog) d).updateDate(startTime.year, startTime.month,
					startTime.monthDay);
			break;
		case END_TIME_DIALOG_ID:
			((TimePickerDialog) d).updateTime(endTime.hour, endTime.minute);
			break;
		case DUR_TIME_DIALOG_ID:
			// ((TimePickerDialog)
			// d).updateTime(durCal.get(Calendar.HOUR_OF_DAY),
			// durCal.get(Calendar.MINUTE));
			((TimePickerDialog) d).updateTime(durTime.hour, durTime.minute);
			break;
		}
	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
			if (DEBUG)
				Log.d("Button", "dialog_id=" + dialog_id);
			switch (dialog_id) {
			case START_TIME_DIALOG_ID:
				// wCal = startCal;
				int year = startTime.year;
				int month = startTime.month;
				int monthDay = startTime.monthDay;
				startTime
						.set(0, minuteOfHour, hourOfDay, monthDay, month, year);
				// endTime.set(startTime.normalize(false)+durTime.normalize(false));
				endTime.set(0, startTime.minute + durTime.minute,
						startTime.hour + durTime.hour, monthDay, month, year);
				endTime.normalize(false);
				break;
			case DUR_TIME_DIALOG_ID:
				// wCal = durCal;
				// durCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				// durCal.set(Calendar.MINUTE, minuteOfHour);
				// endCal.setTimeInMillis(startCal.getTimeInMillis() +
				// durCal.getTimeInMillis());
				year = startTime.year;
				month = startTime.month;
				monthDay = startTime.monthDay;
				durTime.set(0, minuteOfHour, hourOfDay, monthDay, month, year);
				endTime.set(0, startTime.minute + durTime.minute,
						startTime.hour + durTime.hour, monthDay, month, year);
				endTime.normalize(false);
				break;
			case END_TIME_DIALOG_ID:
				// wCal = endCal;
				year = startTime.year;
				month = startTime.month;
				monthDay = startTime.monthDay;
				endTime.set(0, minuteOfHour, hourOfDay, monthDay, monthDay,
						year);
				endTime.normalize(false);
				durTime.set(0, minuteOfHour - startTime.minute, hourOfDay
						- startTime.hour, monthDay, month, year);
				break;
			}
			if (DEBUG)
				Log.d("duration in millis", "msec=" + durTime.toMillis(true));

			mStartTime.setText(startTime.format(Constants.TIME_FORMAT));
			mStartDate.setText(startTime.format(Constants.DATE_FORMAT));
			mEndTime.setText(endTime.format(Constants.TIME_FORMAT));
			mDuration.setText(durTime.format(Constants.TIME_FORMAT));
		}
	};

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			int hour = startTime.hour;
			int minute = startTime.minute;

			startTime.set(0, minute, hour, dayOfMonth, monthOfYear, year);
			endTime.set(0, startTime.minute + durTime.minute, startTime.hour
					+ durTime.hour, dayOfMonth, monthOfYear, year);
			mStartDate.setText(startTime.format(Constants.DATE_FORMAT));
		}
	};

}
