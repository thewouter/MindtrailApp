package nl.wouter.gpstest;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener{
	
	private Activity activity;
	private LocationManager locationManager;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = NetworkHandler.SEND_INTERVAL/2; // in Milliseconds

	public MyLocationListener(Activity activity){
		this.activity = activity;
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
		Location l = getCurrentLocation();
		if(l != null){
			Util.LAT = l.getLatitude();
			Util.LON = l.getLongitude();
		}
	}
	
	public void onLocationChanged(Location location) {
		Util.LON = location.getLongitude();
		Util.LAT = location.getLatitude();
		if(activity instanceof MainActivity){
			((MainActivity)activity).locationChanged();
		}
		//Toast.makeText(activity, "location changed to: " + Util.LAT + " " + Util.LON, Toast.LENGTH_LONG).show();
	}

	public void onProviderDisabled(String provider) {
		
	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(activity, "enabled GPS", Toast.LENGTH_LONG).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	public Location getCurrentLocation(){
		return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	public void stopTracking() {
		locationManager.removeUpdates(this);
	}

}
