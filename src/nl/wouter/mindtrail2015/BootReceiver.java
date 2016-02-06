package nl.wouter.mindtrail2015;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
        	//Toast.makeText(context, "Initiating GpsTestAlarm", Toast.LENGTH_LONG).show();
        	Log.d("BootReceiver", "Booted");
        	alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        	Intent intent1 = new Intent(context, GpsTester.class);
        	alarmIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
        	
        	// Set the alarm to start at approximately 02:00
        	Calendar calendar = Calendar.getInstance();
        	calendar.setTimeInMillis(System.currentTimeMillis());
        	calendar.set(Calendar.HOUR_OF_DAY, 2);

        	// With setInexactRepeating(), you have to use one of the AlarmManager interval
        	// constants--in this case, AlarmManager.INTERVAL_DAY.
        	alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        	        AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }
}
