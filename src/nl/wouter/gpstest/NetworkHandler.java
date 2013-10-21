package nl.wouter.gpstest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NetworkHandler{
	
	private MainActivity activity;
	private AlarmManager alarmManager;
	
	public static int SEND_INTERVAL = 10000;
	
	public NetworkHandler(MainActivity activity, String ip, int port) {
		this.activity = activity;
		alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(activity, LocationSenderStarter.class);
		
		i.putExtra("port", port);
		i.putExtra("ip", ip);
		i.putExtra("groupName", activity.getGroupname());
		
		Util.intent = PendingIntent.getService(activity, 0, i, 0);
	}
	
	public void stopSending(){
		alarmManager.cancel(Util.intent);
	}

	public void startSending() {
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, SEND_INTERVAL, Util.intent);
	}
}