/**
 * 
 */
package org.qris.timereport;

import org.qris.timereport.R;
import android.app.Activity;

import android.content.ContentValues;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * @author MVTURNHO
 * 
 */
public class ActivityEditor extends Activity {
	private int mIDIndex;
	private int mActivityIndex;
	private int mNumIndex;
	private int mStatusIndex;

	private boolean mDirty = false;
	private String mAction;
	private Uri mUri;
	private Cursor mCursor = null;

	private EditText mActivityText;
	private EditText mActivityNrText;
	private CheckBox mStatusCheckBox;
	private EditText mRateText;
	private Button mDeleteButton;
	private Button mSaveButton;
	private Button mCancelButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityedit);
		
		final Intent intent = getIntent();
		mAction = intent.getAction();
		mUri = intent.getData();
		if (!mAction.matches("Add")) {
			mCursor = managedQuery(mUri, Constants.FROM_ACTIVITY_PROJECTION,
					null, null, null);
			mIDIndex = mCursor.getColumnIndex(android.provider.BaseColumns._ID);
			mActivityIndex = mCursor.getColumnIndex(Constants.ACTIVITY);
			mNumIndex = mCursor.getColumnIndex(Constants.ACTIVITY_NR);
			mStatusIndex = mCursor.getColumnIndex(Constants.STATUS);
		}
		mDeleteButton = (Button) this.findViewById(R.id.button_delete);
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDirty = false;
				Uri uri = getIntent().getData();
				getContentResolver().delete(uri, null, null);
				finish();
			}
		});
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
		mActivityText = (EditText) findViewById(R.id.item_activity);
		mActivityNrText = (EditText) findViewById(R.id.item_num);
		mStatusCheckBox = (CheckBox) findViewById(R.id.item_status);

		mDeleteButton.setVisibility(Button.INVISIBLE);
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mDirty)
			saveRecord();
	}

	private void saveRecord() {
		ContentValues values = new ContentValues();

		values.put(Constants.ACTIVITY, mActivityText.getText().toString());
		values.put(Constants.ACTIVITY_NR, mActivityNrText.getText().toString());
		values.put(Constants.STATUS, mStatusCheckBox.isChecked());
		if (!mAction.matches("Add"))
			getContentResolver().update(mUri, values, null, null);
		else
			getContentResolver().insert(mUri, values);
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mCursor != null) {
			// Make sure we are at the one and only row in the cursor.
			mCursor.moveToFirst();
			mActivityText.setText(mCursor.getString(mActivityIndex));
			mActivityNrText.setText(mCursor.getString(mNumIndex));
			mStatusCheckBox.setChecked(mCursor.getInt(mStatusIndex) != 0);
			// mActivityNrText.setText(mCursor.getString(mNumIndex));
			mDeleteButton.setVisibility(Button.VISIBLE);
		} else {
			;
		}
	}
	
}
