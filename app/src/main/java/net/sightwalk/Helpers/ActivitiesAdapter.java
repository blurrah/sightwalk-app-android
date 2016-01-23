package net.sightwalk.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.sightwalk.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class ActivitiesAdapter extends CursorAdapter {

    private TextView activityName;
    private TextView activityDistance;
    private TextView activityTime;

    public ActivitiesAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_activities_list_item, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        activityName = (TextView) view.findViewById(R.id.activityName);
        activityDistance = (TextView) view.findViewById(R.id.activityDistance);
        activityTime = (TextView) view.findViewById(R.id.activityTime);

        String outputName = cursor.getString(cursor.getColumnIndex("name"));
        String outputDistance = distanceConverter(cursor.getInt(cursor.getColumnIndex("distance")));

        String startTime = formatDateString(cursor.getString(cursor.getColumnIndex("startTime")));

        String outputTime = "| Gelopen op: "+startTime;

        activityName.setText(outputName);
        activityDistance.setText(outputDistance);
        activityTime.setText(outputTime);
    }

    private String distanceConverter(Integer distance){
        String outputString;

        if(distance > 1000){
            outputString = distance / 1000 + "km";
        }else {
            outputString = distance + "m";
        }

        return "Afstand: " + outputString;
    }

    private String formatDateString(String timeString){
        String dateTime = timeString;

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        DateTime jodatime = dtf.parseDateTime(dateTime);

        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");

        return dtfOut.print(jodatime);
    }

    private String elapsedTime(Date startDate, Date endDate){
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;

        return " | Tijd: " + elapsedHours + " uur " + elapsedMinutes + " min.";
    }
}