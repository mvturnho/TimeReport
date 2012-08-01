package org.qris.timereport;

import static org.qris.timereport.Constants.PROJECT_URI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.qris.timereport.dialogs.ExportDialog;
import org.qris.timereport.dialogs.NewDbDialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DatabaseList extends ListActivity {
    static final boolean DEBUG = true;
    static final String TAG = "ActivityList";

	private List<String> items = null;
	private List<String> path = null;
	private String root = "/";
	private String dirPath = "/data/data/org.qris.timereport/databases";

	private static final int NEW_DB_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.databaselist);

		// Inform the list we provide context menus for items
		getListView().setOnCreateContextMenuListener(this);

		getDir(dirPath);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			if (DEBUG) Log.e(TAG, "bad menuInfo", e);
			return;
		}

		if (DEBUG) Log.d(TAG, "selected:" + info.position + " filename="
				+ items.get(info.position));

		// Setup the menu header
		menu.setHeaderTitle("Database Action");

		// Add a menu item to delete the note
		menu.add(0, 0, 0, R.string.menu_delete);
		menu.add(0, 1, 0, R.string.menu_dbexport);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			if (DEBUG) Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case 0: {
			if (DEBUG) Log.d(TAG, "Delete:" + info.position + " filename="
					+ items.get(info.position));
			if (items.size() > 1) {
				String fname = items.get(info.position);
				File f = new File(dirPath + "/" + fname + ".db");
				if (f.exists())
					f.delete();
				items.remove(info.position);
				setSelectedDb(items.get(0));
				getDir(dirPath);
			}
			return true;
		}
		case 1:
			try {
				saveDataBase(items.get(info.position)+".db");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.qris.timereport.ActivityList#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.db_menu, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.qris.timereport.ActivityList#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		// super.onListItemClick(l, v, position, id);
		String filename = items.get(position);
		setSelectedDb(filename);
		finish();
	}

	private void setSelectedDb(String filename) {
		// EventsProvider event_provider = (EventsProvider)
		// (getContentResolver().acquireContentProviderClient(PROJECT_URI).getLocalContentProvider());
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences prefs = this.getSharedPreferences("timereport", mode);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("database", filename);
		editor.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.qris.timereport.ActivityList#onOptionsItemSelected(android.view.MenuItem
	 * )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// return super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_new:
			if (DEBUG) Log.d(TAG, "menu_newdb");
			showDialog(NEW_DB_DIALOG_ID);
			return true;
		case R.id.menu_import:
			if (DEBUG) Log.d(TAG, "menu_import");
//			try {
//				copyDataBase("TimeReport2007.db");
//				copyDataBase("TimeReport2008.db");
//				copyDataBase("TimeReport2009.db");
//				copyDataBase("TimeReport2010.db");
//				copyDataBase("TimeReport2010-exx.db");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return true;
		}
		return false;

	}
	
	private void saveDataBase(String dbFile)  throws IOException{
		ProgressDialog dialog = ProgressDialog.show(this, "","Please wait for few seconds...", true);
        File root = Environment.getExternalStorageDirectory();
        File outFile = new File(root, dbFile);
        InputStream databaseInputStream = new FileInputStream(dirPath + "/"+dbFile);
        OutputStream databaseOutputStream = new FileOutputStream(outFile);

        byte[] buffer = new byte[1];
        int length;
        while ( (length = databaseInputStream.read(buffer)) > 0 ) {
                databaseOutputStream.write(buffer);
        }

        //Close the streams
        databaseOutputStream.flush();
        databaseOutputStream.close();
        databaseInputStream.close();	
        dialog.dismiss();
	}

	private void copyDataBase(String inputFile) throws IOException{

        OutputStream databaseOutputStream = new FileOutputStream(dirPath + "/"+inputFile);
        File root = Environment.getExternalStorageDirectory();
        File inFile = new File(root, inputFile);
        InputStream databaseInputStream = new FileInputStream(inFile);

        byte[] buffer = new byte[1];
        int length;
        while ( (length = databaseInputStream.read(buffer)) > 0 ) {
                databaseOutputStream.write(buffer);
                //Log.w("Bytes: ", ((Integer)length).toString());
                //Log.w("value", buffer.toString());
        }

        //Close the streams
        databaseOutputStream.flush();
        databaseOutputStream.close();
        databaseInputStream.close();
 }

	private void getDir(String dirPath) {
		// myPath.setText("Location: " + dirPath);

		items = new ArrayList<String>();
		path = new ArrayList<String>();
		// Environment.getDataDirectory()
		File f = new File(dirPath);
		if (DEBUG) Log.d(TAG, f.toString());
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.isDirectory()) {
				String fname = file.getName();
				// Log.d("dir", fname);
				if(fname.contains("journal")) continue;
				int dotindex = fname.lastIndexOf(".");
				if (DEBUG) Log.d(TAG, "found file: "+file.toString());
				if (dotindex > 0) {
					if (DEBUG) Log.d(TAG, "added: " + file.toString());
					items.add(fname.substring(0, dotindex));
				}
			}
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.file_row, items);
		setListAdapter(fileList);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if (id == NEW_DB_DIALOG_ID) {
			return new NewDbDialog(this, mNewDbSetListener, "");
		}
		return null;
	}

	private NewDbDialog.OnNewDbSetListener mNewDbSetListener = new NewDbDialog.OnNewDbSetListener() {
		public void onNewDbDo(String filename) {
			if (DEBUG) Log.d("NewDb", "FileName=" + filename);
			setSelectedDb(filename);
			finish();
		}
	};

}
