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
public class ExportDialog extends AlertDialog implements OnClickListener,
		OnCheckedChangeListener {

	/**
	 * The callback interface used to indicate the user is done.
	 */
	public interface OnExportSetListener {

		/**
		 * @param view
		 *            The view associated with this listener.
		 * @param hourOfDay
		 *            The hour that was set.
		 * @param minute
		 *            The minute that was set.
		 */
		void onExportDo(String fileName, int exportType);
	}

	private final OnExportSetListener mCallback;
	private final EditText mFileName;
	private int mCheckId;

	public final static int CSV = 0;
	public final static int HTML = 1;

	/**
	 * @param context
	 */
	public ExportDialog(Context context, OnExportSetListener callback, String filename) {
		this(context, android.R.style.Theme_Translucent,callback,filename);
	}

	/**
	 * @param context
	 * @param theme
	 */
	public ExportDialog(Context context, int theme, OnExportSetListener callback,String filename) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		mCallback = callback;
		setButton(context.getText(R.string.button_export), this);
		setButton2(context.getText(R.string.button_cancel),
				(OnClickListener) null);

		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.exportdialog, null);
		setView(view);
		mFileName = (EditText) view.findViewById(R.id.export_file_name);
		mFileName.setText(filename);
		mCheckId = R.id.export_cvs_RadioButton;
		RadioGroup group = (RadioGroup) view
				.findViewById(R.id.export_radiogroup);
		group.check(mCheckId);
		group.setOnCheckedChangeListener(this);

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
			if (mCheckId == R.id.export_cvs_RadioButton)
				exportType = CSV;
			else
				exportType = HTML;
			mCallback.onExportDo(fileName, exportType);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		mCheckId = checkedId;

	}

}
