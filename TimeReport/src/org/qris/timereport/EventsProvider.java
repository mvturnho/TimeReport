package org.qris.timereport;

import static android.provider.BaseColumns._ID;
import static org.qris.timereport.Constants.*;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class EventsProvider extends ContentProvider {
	private static final int EVENTS = 1;
	private static final int EVENTS_ID = 2;
	private static final int PROJECTS = 3;
	private static final int PROJECTS_ID = 4;
	private static final int ACTIVITIES = 5;
	private static final int ACTIVITIES_ID = 6;

	/** The MIME type of a directory of events */
	private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.example.event";
	private static final String PROJECT_TYPE = "vnd.android.cursor.dir/vnd.example.project";
	private static final String ACTIVITY_TYPE = "vnd.android.cursor.dir/vnd.example.activity";

	/** The MIME type of a single event */
	private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.example.event";
	private static final String PROJECT_ITEM_TYPE = "vnd.android.cursor.item/vnd.example.project";
	private static final String ACTIVITY_ITEM_TYPE = "vnd.android.cursor.item/vnd.example.activity";

	private EventsData events;
	private UriMatcher uriMatcher;

	@Override
	public boolean onCreate() {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "events", EVENTS);
		uriMatcher.addURI(AUTHORITY, "events/#", EVENTS_ID);
		uriMatcher.addURI(AUTHORITY, "projects", PROJECTS);
		uriMatcher.addURI(AUTHORITY, "projects/#", PROJECTS_ID);
		uriMatcher.addURI(AUTHORITY, "activities", ACTIVITIES);
		uriMatcher.addURI(AUTHORITY, "activities/#", ACTIVITIES_ID);
		
		//events = new EventsData(getContext());
		return true;
	}
	
	public void openDb(String dbFileName) {
		if(events != null);
			//events.close();
		events = new EventsData(getContext(), dbFileName);
	}
	
	public String getDbFileName() {
		return events.getDbFileName();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		Cursor cursor = null;
		String tablename = "";
		long id;
		switch (uriMatcher.match(uri)) {
		case EVENTS_ID:
			tablename = TABLE_NAME;
			id = Long.parseLong(uri.getPathSegments().get(1));
			selection = appendRowId(selection, id);
			break;
		case EVENTS:
			// Get the database and run the query
			tablename = TABLE_NAME;
			break;
		case PROJECTS:
			// Get the database and run the query
			tablename = PROJECT_TABLE_NAME;
			break;
		case PROJECTS_ID:
			tablename = PROJECT_TABLE_NAME;
			id = Long.parseLong(uri.getPathSegments().get(1));
			selection = appendRowId(selection, id);
			break;
		case ACTIVITIES:
			// Get the database and run the query
			tablename = ACTIVITY_TABLE_NAME;
			break;
		case ACTIVITIES_ID:
			tablename = ACTIVITY_TABLE_NAME;
			id = Long.parseLong(uri.getPathSegments().get(1));
			selection = appendRowId(selection, id);
			break;
	
		}
		return getCursor( uri,  tablename,  projection,  selection,  selectionArgs,  orderBy);
	}
	
	// Tell the cursor what uri to watch, so it knows when its
	// source data changes	
	private Cursor getCursor(Uri uri, String tablename, String[] projection, String selection, String[] selectionArgs, String orderBy) {
		Cursor cursor = null;
		SQLiteDatabase db;
		db = events.getReadableDatabase();
		cursor = db.query(tablename, projection, selection, selectionArgs,
				null, null, orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case EVENTS:
			return CONTENT_TYPE;
		case EVENTS_ID:
			return CONTENT_ITEM_TYPE;
		case PROJECTS:
			return PROJECT_TYPE;
		case PROJECTS_ID:
			return PROJECT_ITEM_TYPE;
		case ACTIVITIES:
			return ACTIVITY_TYPE;
		case ACTIVITIES_ID:
			return ACTIVITY_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = events.getWritableDatabase();
		Uri newUri = null;
		long id;
		switch (uriMatcher.match(uri)) {
		case EVENTS:
			id = db.insertOrThrow(TABLE_NAME, null, values);
			// Notify any watchers of the change
			newUri = ContentUris.withAppendedId(CONTENT_URI, id);
			break;
		case PROJECTS:
			id = db.insertOrThrow(PROJECT_TABLE_NAME, null, values);
			// Notify any watchers of the change
			newUri = ContentUris.withAppendedId(PROJECT_URI, id);
			break;
		case ACTIVITIES:
			id = db.insertOrThrow(ACTIVITY_TABLE_NAME, null, values);
			// Notify any watchers of the change
			newUri = ContentUris.withAppendedId(ACTIVITY_URI, id);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
//		// Validate the requested uri
//		if (uriMatcher.match(uri) != EVENTS) {
//			throw new IllegalArgumentException("Unknown URI " + uri);
//		}
//
//		// Insert into database
//		long id = db.insertOrThrow(TABLE_NAME, null, values);

		// Notify any watchers of the change
//		Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
//		getContext().getContentResolver().notifyChange(newUri, null);
		getContext().getContentResolver().notifyChange(newUri, null);
		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = events.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case EVENTS:
			count = db.delete(TABLE_NAME, selection, selectionArgs);
			break;
		case EVENTS_ID:
			long id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.delete(TABLE_NAME, appendRowId(selection, id),
					selectionArgs);
			break;
		case PROJECTS:
			count = db.delete(PROJECT_TABLE_NAME, selection, selectionArgs);
			break;
		case PROJECTS_ID:
			id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.delete(PROJECT_TABLE_NAME, appendRowId(selection, id),
					selectionArgs);
			break;
		case ACTIVITIES:
			count = db.delete(ACTIVITY_TABLE_NAME, selection, selectionArgs);
			break;
		case ACTIVITIES_ID:
			id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.delete(ACTIVITY_TABLE_NAME, appendRowId(selection, id),
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Notify any watchers of the change
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = events.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case EVENTS:
			count = db.update(TABLE_NAME, values, selection, selectionArgs);
			break;
		case EVENTS_ID:
			long id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.update(TABLE_NAME, values, appendRowId(selection, id),
					selectionArgs);
			break;
		case PROJECTS:
			count = db.update(PROJECT_TABLE_NAME, values, selection, selectionArgs);
			break;
		case PROJECTS_ID:
			id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.update(PROJECT_TABLE_NAME, values, appendRowId(selection, id),
					selectionArgs);
			break;
		case ACTIVITIES:
			count = db.update(ACTIVITY_TABLE_NAME, values, selection, selectionArgs);
			break;
		case ACTIVITIES_ID:
			id = Long.parseLong(uri.getPathSegments().get(1));
			count = db.update(ACTIVITY_TABLE_NAME, values, appendRowId(selection, id),
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Notify any watchers of the change
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/** Append an id test to a SQL selection expression */
	private String appendRowId(String selection, long id) {
		return _ID
				+ "="
				+ id
				+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')'
						: "");
	}

}
