package org.qris.timereport;

import java.util.Calendar;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ProjectListAdapter extends CursorAdapter{

    private LayoutInflater mFactory;
    
	public ProjectListAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View ret = mFactory.inflate(R.layout.project_item, parent, false);
//
//        ((TextView) ret.findViewById(R.id.am)).setText(mAm);
//        ((TextView) ret.findViewById(R.id.pm)).setText(mPm);
//
//        DigitalClock digitalClock =
//                (DigitalClock) ret.findViewById(R.id.digitalClock);
//        digitalClock.setLive(false);
        return ret;
    }

    public void bindView(View view, Context context, Cursor cursor) {
//        final Alarm alarm = new Alarm(cursor);
//
//        CheckBox onButton = (CheckBox) view.findViewById(R.id.alarmButton);
//        onButton.setChecked(alarm.enabled);
//        onButton.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    boolean isChecked = ((CheckBox) v).isChecked();
//                    Alarms.enableAlarm(AlarmClock.this, alarm.id,
//                        isChecked);
//                    if (isChecked) {
//                        SetAlarm.popAlarmSetToast(AlarmClock.this,
//                            alarm.hour, alarm.minutes, alarm.daysOfWeek);
//                    }
//                }
//        });
//
//        DigitalClock digitalClock =
//                (DigitalClock) view.findViewById(R.id.digitalClock);
//
//        // set the alarm text
//        final Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY, alarm.hour);
//        c.set(Calendar.MINUTE, alarm.minutes);
//        digitalClock.updateTime(c);
//
//        // Set the repeat text or leave it blank if it does not repeat.
//        TextView daysOfWeekView =
//                (TextView) digitalClock.findViewById(R.id.daysOfWeek);
//        final String daysOfWeekStr =
//                alarm.daysOfWeek.toString(AlarmClock.this, false);
//        if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
//            daysOfWeekView.setText(daysOfWeekStr);
//            daysOfWeekView.setVisibility(View.VISIBLE);
//        } else {
//            daysOfWeekView.setVisibility(View.GONE);
//        }
//
//        // Display the label
//        TextView labelView =
//                (TextView) digitalClock.findViewById(R.id.label);
//        if (alarm.label != null && alarm.label.length() != 0) {
//            labelView.setText(alarm.label);
//            labelView.setVisibility(View.VISIBLE);
//        } else {
//            labelView.setVisibility(View.GONE);
//        }
    }
	
}
