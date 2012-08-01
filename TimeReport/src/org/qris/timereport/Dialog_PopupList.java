package org.qris.timereport;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.WindowManager.LayoutParams;

public class Dialog_PopupList extends Dialog implements
		AdapterView.OnItemClickListener {
	public interface OnItemSelected {
		void onItemSelected(int index);
	}

	final private OnItemSelected mListener;

	public Dialog_PopupList(Context context, OnItemSelected listener,
			int initialIndex, String listdata[], int x, int y) {
		super(context);

		mListener = listener;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_popuplist);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.listadapter_popuplistviewitem, R.id.plwi_text,
				listdata);

		final ListView list = (ListView) findViewById(R.id.dpl_list);
		list.setAdapter(adapter);
		list.setSelection(initialIndex);
		list.setSelected(true);
		list.setOnItemClickListener(this);
		list.setDividerHeight(0);

		show();
	}

	public void onItemClick(AdapterView parent, View v, int position, long id) {
		mListener.onItemSelected(position);
		dismiss();
	}

}
