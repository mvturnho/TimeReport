/**
 * 
 */
package org.qris.timereport.dialogs;

import org.qris.timereport.R;
import org.qris.timereport.R.id;
import org.qris.timereport.R.layout;
import org.qris.timereport.R.string;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * @author MVTURNHO
 * 
 */
public class NewDbDialog extends AlertDialog implements OnClickListener {

	/**
	 * The callback interface used to indicate the user is done.
	 */
	public interface OnNewDbSetListener {

		/**
		 * @param view
		 *            The view associated with this listener.
		 * @param hourOfDay
		 *            The hour that was set.
		 * @param minute
		 *            The minute that was set.
		 */
		void onNewDbDo(String fileName);
	}

	private final OnNewDbSetListener mCallback;
	private final EditText mFileName;

	/**
	 * @param context
	 */
	public NewDbDialog(Context context, OnNewDbSetListener callback, String filename) {
		this(context, android.R.style.Theme_Translucent,callback,filename);
	}

	/**
	 * @param context
	 * @param theme
	 */
	public NewDbDialog(Context context, int theme, OnNewDbSetListener callback,String filename) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		mCallback = callback;
		setButton(context.getText(R.string.button_create), this);
		setButton2(context.getText(R.string.button_cancel),
				(OnClickListener) null);

		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.newdb_dialog, null);
		setView(view);
		mFileName = (EditText) view.findViewById(R.id.db_file_name);
		mFileName.setText(filename);
	}

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	// public ExportDialog(Context context, boolean cancelable,
	// OnCancelListener cancelListener) {
	// super(context, cancelable, cancelListener);
	// TODO Auto-generated constructor stub
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.DialogInterface.OnClickListener#onClick(android.content
	 * .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		int exportType;
		if (mCallback != null) {
			String fileName = mFileName.getText().toString();
			mCallback.onNewDbDo(fileName);
		}
	}


}
