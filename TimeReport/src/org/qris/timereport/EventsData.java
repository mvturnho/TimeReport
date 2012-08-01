package org.qris.timereport;

import static android.provider.BaseColumns._ID;
import static org.qris.timereport.Constants.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventsData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "timereport.db";
	private static final int DATABASE_VERSION = 3;
	private String mDbFileName;

	/** Create a helper object for the Events database */
	public EventsData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public EventsData(Context ctx,String dbFileName) {
		super(ctx, dbFileName, null, DATABASE_VERSION);
		mDbFileName = dbFileName;
	}
	
	public String getDbFileName() {
		return mDbFileName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
		// + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIME
		// + " LONG," + TITLE + " TEXT NOT NULL);");
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + START + " LONG, "
				+ DURATION + " LONG, " + PROJECT + " TEXT, " + PROJECT_NR
				+ " TEXT, " + ACTIVITY + " TEXT, " + ACTIVITY_NR + " TEXT, "
				+ STATUS + " INTEGER, " + NOTE + " TEXT);");
		db.execSQL("CREATE TABLE " + PROJECT_TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PROJECT + " TEXT, "
				+ PROJECT_NR + " TEXT, " + RATE + " INTEGER, "+ STATUS + " INTEGER);");
		db.execSQL("CREATE TABLE " + ACTIVITY_TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACTIVITY + " TEXT, "
				+ ACTIVITY_NR + " TEXT, " + STATUS + " INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + PROJECT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TABLE_NAME);
		onCreate(db);
	}
}