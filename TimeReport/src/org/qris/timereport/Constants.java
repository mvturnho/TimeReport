package org.qris.timereport;

import static android.provider.BaseColumns._ID;
import static org.qris.timereport.Constants.TIME;
import static org.qris.timereport.Constants.TITLE;
import android.provider.BaseColumns;
import android.net.Uri;

public interface Constants extends BaseColumns {
	public static final String TIME_FORMAT = "%H:%M";
	public static final String DATE_FORMAT = "%Y-%m-%d";

	public static final String TABLE_NAME = "events";
	public static final String PROJECT_TABLE_NAME = "projects";
	public static final String ACTIVITY_TABLE_NAME = "activities";

	// Columns in the TimeReport database
	public static final String TIME = "time";
	public static final String TITLE = "title";
	public static final String START = "start";
	public static final String DURATION = "duration";
	public static final String PROJECT = "project";
	public static final String PROJECT_NR = "projectnr";
	public static final String RATE = "rate";
	public static final String ACTIVITY = "activity";
	public static final String ACTIVITY_NR = "activitynr";
	public static final String STATUS = "status";
	public static final String NOTE = "note";
	public static final String _SYNC_ID = "_sync_id";

	public static final String AUTHORITY = "org.qris.timereport";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri PROJECT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + PROJECT_TABLE_NAME);
	public static final Uri ACTIVITY_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + ACTIVITY_TABLE_NAME);

	public static final String[] FROM_PROJECTION = new String[] { 
		_ID, 
		START, 
		PROJECT, };
	public static final String[] FROM_CONTENT_PROJECTION = new String[] { 
		_ID, 
		START, 
		DURATION, 
		PROJECT,
		PROJECT_NR, 
		ACTIVITY, 
		ACTIVITY_NR, 
		STATUS, NOTE };
	public static final String[] FROM_PROJECT_PROJECTION = new String[] { 
		_ID, 
		PROJECT, 
		PROJECT_NR, 
		RATE,
		STATUS };
	public static final String[] FROM_ACTIVITY_PROJECTION = new String[] { 
		_ID, 
		ACTIVITY, 
		ACTIVITY_NR, 
		STATUS };

}
