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

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
//import android.widget.TextView;


/**
 * @author MVTURNHO
 * 
 */
public class ProjectEditor extends Activity {
	@SuppressWarnings("unused")
	private int mIDIndex;
	private int mProjectIndex;
	private int mNumIndex;
	private int mRateIndex;
	private int mStatusIndex;

	private boolean mDirty = false;
	private String mAction;
	private Uri mUri;
	private Cursor mCursor = null;

	private EditText mProjectText;
	private EditText mProjectNrText;
	private EditText mProjectRate;
	private CheckBox mStatusCheckBox;
	@SuppressWarnings("unused")
	private EditText mRateText;
	private Button mDeleteButton;
	private Button mSaveButton;
	private Button mCancelButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.projectedit);
		
		final Intent intent = getIntent();
		mAction = intent.getAction();
		mUri = intent.getData();
		if (!mAction.matches("Add")) {
			mCursor = managedQuery(mUri, Constants.FROM_PROJECT_PROJECTION,
					null, null, null);
			mIDIndex = mCursor.getColumnIndex(android.provider.BaseColumns._ID);
			mProjectIndex = mCursor.getColumnIndex(Constants.PROJECT);
			mNumIndex = mCursor.getColumnIndex(Constants.PROJECT_NR);
			mRateIndex = mCursor.getColumnIndex(Constants.RATE);
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
		mProjectText = (EditText) findViewById(R.id.item_proj);
		mProjectNrText = (EditText) findViewById(R.id.item_num);
		mProjectRate = (EditText) findViewById(R.id.item_rate);
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

		values.put(Constants.PROJECT, mProjectText.getText().toString());
		values.put(Constants.PROJECT_NR, mProjectNrText.getText().toString());
		values.put(Constants.RATE, mProjectRate.getText().toString());
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
			mProjectText.setText(mCursor.getString(mProjectIndex));
			mProjectNrText.setText(mCursor.getString(mNumIndex));
			mProjectRate.setText(Integer.toString(mCursor.getInt(mRateIndex)));
			mStatusCheckBox.setChecked(mCursor.getInt(mStatusIndex) != 0);
			// mProjectNrText.setText(mCursor.getString(mNumIndex));
			mDeleteButton.setVisibility(Button.VISIBLE);
		} else {
			;
		}
	}
	
}
