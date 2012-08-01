package org.qris.timereport;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.qris.timereport.R;
import org.qris.timereport.R.id;
import org.qris.timereport.R.layout;

import android.appwidget.AppWidgetManager;

import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.widget.RemoteViews;


public class EventWidget extends AppWidgetProvider {
   // Define the format string for the date and time
   private SimpleDateFormat formatter = new SimpleDateFormat( 
         "EEE, d MMM yyyy\nHH:mm:ss.SSS");

   @Override
   public void onUpdate(Context context,
         AppWidgetManager appWidgetManager, int[] appWidgetIds) {
      // Retrieve and format the current date and time
      String now = formatter.format(new Date()); 
      
      // Change the text in the widget
      RemoteViews updateViews = new RemoteViews( 
            context.getPackageName(), R.layout.widget);
      updateViews.setTextViewText(R.id.text, now); 
      appWidgetManager.updateAppWidget(appWidgetIds, updateViews); 
      
      // Not really necessary, just a habit
      super.onUpdate(context, appWidgetManager, appWidgetIds); 
   }
   
}