package org.qris.timereport;

import static android.provider.BaseColumns._ID;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.qris.timereport.R;
import org.qris.timereport.dialogs.ExportDialog;

import static org.qris.timereport.Constants.*;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeReportList extends ListActivity {

    static final boolean DEBUG = false;
    static final String TAG = "TimeReportList";
    
	private ImageButton mAddButton;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences prefs = this.getSharedPreferences("timereport", mode);
		String fname = prefs.getString("database", "timereport");
		if (fname.length() > 2) {
			EventsProvider event_provider = (EventsProvider) (getContentResolver()
					.acquireContentProviderClient(PROJECT_URI)
					.getLocalContentProvider());
			event_provider.openDb(fname + ".db");
			this.setTitle(fname);
			Cursor cursor = getEvents();
			showEvents(cursor);
		}

	}

	private static final int EXPORT_DIALOG_ID = 0;

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
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_add:
			intent = new Intent(this, EventEditor.class);
			Uri uri = getIntent().getData();
			intent.setData(uri);
			intent.setAction("Add");
			startActivity(intent);
			return true;
			// More items go here (if any) ...
		case R.id.menu_projects:
			if (DEBUG) Log.d(TAG, "menu_projects");
			intent = new Intent(this, ProjectList.class);
			startActivity(intent);
			return true;
		case R.id.menu_activities:
			if (DEBUG) Log.d(TAG, "menu_activities");
			intent = new Intent(this, ActivityList.class);
			startActivity(intent);
			return true;
		case R.id.menu_export:
			if (DEBUG) Log.d(TAG, "menu_export");
			showDialog(EXPORT_DIALOG_ID);
			return true;
		case R.id.menu_databases:
			if (DEBUG) Log.d(TAG, "menu_databases");
			intent = new Intent(this, DatabaseList.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		// super.onListItemClick(l, v, position, id);
		if (DEBUG) Log.d(TAG, "selected" + id);
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		// startActivity(new Intent(Intent.ACTION_EDIT, uri));
		Intent intent = new Intent(this, EventEditor.class);
		intent.setData(uri);
		intent.setAction("Edit");
		startActivity(intent);
	}

	// private static String[] FROM = { _ID, TIME, TITLE, };
	private static String ORDER_BY = START + " DESC";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(Constants.CONTENT_URI);
		}
		setContentView(R.layout.eventlist);
	}

	private void addEvent(String string) {
		// Insert a new record into the Events data source.
		// You would do something similar for delete and update.
		ContentValues values = new ContentValues();
		values.put(START, System.currentTimeMillis());
		values.put(PROJECT, string);
		getContentResolver().insert(CONTENT_URI, values);
	}

	private Cursor getEvents() {
		// Perform a managed query. The Activity will handle closing
		// and re-querying the cursor when needed.
		return managedQuery(CONTENT_URI, FROM_CONTENT_PROJECTION, null, null,
				ORDER_BY);
	}

	private void showEvents(Cursor cursor) {
		// Set up data binding
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.item, cursor, FROM, TO);
		TimeReportCursorAdapter adapter = new TimeReportCursorAdapter(this,
				cursor);
		this.setListAdapter(adapter);
	}

	private void exportEvents(Cursor cursor, String fileName, int type) {
		try {
			int mId = cursor.getColumnIndex(Constants._ID);
			int mStartIndex = cursor.getColumnIndex(Constants.START);
			int mDurationIndex = cursor.getColumnIndex(Constants.DURATION);
			int mActivityIndex = cursor.getColumnIndex(Constants.ACTIVITY);
			int mProjectIndex = cursor.getColumnIndex(Constants.PROJECT);
			int mProjectNrIndex = cursor.getColumnIndex(Constants.PROJECT_NR);
			int mActIndex = cursor.getColumnIndex(Constants.ACTIVITY);
			int mActNrIndex = cursor.getColumnIndex(Constants.ACTIVITY_NR);
			int mNoteIndex = cursor.getColumnIndex(Constants.NOTE);
			Time startTime = new Time();
			Time durTime = new Time();

			if (type == ExportDialog.CSV)
				fileName += ".csv";
			else if (type == ExportDialog.HTML)
				fileName += ".html";

			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				File outFile = new File(root, fileName);
				FileWriter outWriter = new FileWriter(outFile);
				BufferedWriter out = new BufferedWriter(outWriter);
				if (type == ExportDialog.HTML) {
					out
							.write("<TABLE BORDER><TR BGCOLOR=\"#99CCFF\"><TH>id</TH><TH>Date</TH><TH>start</TH><TH>duration</TH><TH>name</TH><TH>nr</TH><TH>act</TH><TH>act nr.</TH><TH>note</TH></TR>");
					// out.write(buf);
					out
							.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"nl\" xml:lang=\"nl\">\n");
					out.write("<head>\n");
					out
							.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
					out.write("<title>Filenaam</title>\n");
					out.write("</head>\n");
					out.write("<body>\n");
				}
				cursor.moveToFirst();
				while (cursor.isAfterLast() == false) {
					startTime.set(cursor.getLong(mStartIndex));
					startTime.normalize(false);
					// long dval = cursor.getLong(mDurationIndex);
					durTime.clear("");
					durTime.set(cursor.getLong(mDurationIndex));
					durTime.normalize(false);
					if (type == ExportDialog.CSV) {
						out.write(cursor.getInt(mId) + ","
								+ startTime.format(Constants.DATE_FORMAT) + ","
								+ startTime.format(Constants.TIME_FORMAT) + ","
								+ durTime.format(Constants.TIME_FORMAT) + ","
								+ cursor.getString(mProjectIndex) + ","
								+ cursor.getString(mProjectNrIndex) + ","
								+ cursor.getString(mActIndex) + ","
								+ cursor.getString(mActNrIndex) + ","
								+ cursor.getString(mNoteIndex) + "\n");
					} else {
						out.write("<TR><TD BGCOLOR=\"#99CCFF\">"
								+ cursor.getInt(mId) + "</TD><TD nowrap>"
								+ startTime.format(Constants.DATE_FORMAT)
								+ "</TD><TD>"
								+ startTime.format(Constants.TIME_FORMAT)
								+ "</TD><TD>"
								+ durTime.format(Constants.TIME_FORMAT)
								+ "</TD><TD>" + cursor.getString(mProjectIndex)
								+ "</TD><TD>"
								+ cursor.getString(mProjectNrIndex)
								+ "</TD><TD>" + cursor.getString(mActIndex)
								+ "</TD><TD>" + cursor.getString(mActNrIndex)
								+ "</TD><TD>" + cursor.getString(mNoteIndex)
								+ "</TD></TR>\n");
					}
					cursor.moveToNext();
				}
				if (type == ExportDialog.HTML)
					out.write("</TABLE BORDER>\n</body>\n</html>\n");

				cursor.close();

				out.close();
				Context context = getApplicationContext();
				CharSequence text = "Exported to " + root.toString() + "  "
						+ fileName;
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		} catch (IOException e) {
			Log.e("IO-error", "Could not write file " + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if (id == EXPORT_DIALOG_ID) {
			EventsProvider event_provider = (EventsProvider) (getContentResolver()
					.acquireContentProviderClient(PROJECT_URI)
					.getLocalContentProvider());
			String filename = event_provider.getDbFileName();
			int dotindex = filename.indexOf(".");
			return new ExportDialog(this, mExportSetListener, filename
					.substring(0, dotindex));
		}
		return null;
	}

	private ExportDialog.OnExportSetListener mExportSetListener = new ExportDialog.OnExportSetListener() {
		public void onExportDo(String fileName, int exportType) {
			if (DEBUG) Log.d("Export", "FileName=" + fileName + " type:" + exportType);
			Cursor cursor = getEvents();
			exportEvents(cursor, fileName, exportType);
		}
	};

}
