package nl.wouter.MindTrail;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewDebug.FlagToString;

public class LocationSenderStarter extends Service{
	private String groupName;
	private int lon, lat;

	private String ip;
	private int port;
	
	LocationSender s;
	

	public int onStartCommand(Intent intent, int startId, int i) {
		super.onStart(intent, startId);
		if(intent == null){
			Log.e("LocatonSender", "intent is null");
			return START_FLAG_RETRY;
		}
		Bundle b  = intent.getExtras();
		ip = b.getString("ip");
		port = b.getInt("port");
		groupName = b.getString("groupName");
		lon = (int) (Util.LON * Math.pow(10, 7));
		lat = (int) (Util.LAT * Math.pow(10, 7));
		s = new LocationSender(ip, port, groupName, lon, lat);
		s.start();
		return START_STICKY;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}