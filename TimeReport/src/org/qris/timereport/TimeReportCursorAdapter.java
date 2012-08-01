/**
 * 
 */
package org.qris.timereport;

import java.util.Calendar;

import org.qris.timereport.R;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * @author MVTURNHO
 * 
 */
public class TimeReportCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	//private int mIDIndex;
	private int mStartIndex;
	private int mDurationIndex;
	private int mProjectIndex;
	private int mActivityIndex;
	private int mNoteIndex;
	
	/**
	 * @param context
	 * @param c
	 */
	public TimeReportCursorAdapter(Context context, Cursor c) {
		super(context, c);
		//mIDIndex = c.getColumnIndex(android.provider.BaseColumns._ID);
		mStartIndex = c.getColumnIndex(Constants.START);
		mDurationIndex = c.getColumnIndex(Constants.DURATION);
		mActivityIndex = c.getColumnIndex(Constants.ACTIVITY);
		mProjectIndex = c.getColumnIndex(Constants.PROJECT);
		mNoteIndex = c.getColumnIndex(Constants.NOTE);

		mInflater = LayoutInflater.from(context);

	}

	/**
	 * @param context
	 * @param c
	 * @param autoRequery
	 */
	public TimeReportCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CursorAdapter#bindView(android.view.View,
	 * android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//Log.d("CursorAdapter", "bindView rowID" + cursor.getInt(mIDIndex));
		//TextView id = (TextView) view.findViewById(R.id.item_rowid);
		TextView date = (TextView) view.findViewById(R.id.item_date);
		TextView time = (TextView) view.findViewById(R.id.item_time);
		TextView duration = (TextView) view.findViewById(R.id.item_duration);
		TextView activity = (TextView) view.findViewById(R.id.item_activity);
		TextView project = (TextView) view.findViewById(R.id.item_project);
		TextView note = (TextView) view.findViewById(R.id.item_note);
		
		long lTime = cursor.getLong(mStartIndex);
		Calendar calStart = Calendar.getInstance();
		Time startTime = new Time();
		startTime.set(lTime);
		//calStart.setTimeInMillis(lTime);
		
		long dTime = cursor.getLong(mDurationIndex);
		Time durTime = new Time();
		durTime.clear("");
		durTime.set(dTime);
		
		//id.setText(Integer.toString(cursor.getInt(mIDIndex)));
		activity.setText(cursor.getString(mActivityIndex));
		project.setText(cursor.getString(mProjectIndex));
		note.setText(cursor.getString(mNoteIndex));
		date.setText(startTime.format(Constants.DATE_FORMAT));
		time.setText(startTime.format(Constants.TIME_FORMAT));
		duration.setText(durTime.format(Constants.TIME_FORMAT));
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CursorAdapter#newView(android.content.Context,
	 * android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		//Log.d("CursorAdapter", "newView");
		return mInflater.inflate(R.layout.item, null);
	}

}
