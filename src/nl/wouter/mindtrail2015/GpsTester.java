package nl.wouter.mindtrail2015;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

public class GpsTester extends Service implements 
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener{
	private static final String TAG = "GpsTester";
	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 2;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    LocationListener[] LocationListeners = new LocationListener[] { new LocationListener()};
        	
	//private LocationClient locationClient;
	private LocationRequest locationRequest;

	private class LocationListener implements com.google.android.gms.location.LocationListener{
	    @SuppressWarnings("unused")
		Location mLastLocation;
	    public LocationListener()
	    {
	        Log.e(TAG, "LocationListener");
	        mLastLocation = null;
	    }
	    @Override
	    public void onLocationChanged(Location location)
	    {
	        Log.d(TAG, "onLocationChanged: " + location);
	        mLastLocation=location;
	    }
	} 
	
	
	@Override
	public IBinder onBind(Intent arg0){
	    return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
	    Log.d(TAG, "onStartCommand");
	   // locationClient.connect();
	    
	    new Thread(){
	    	public void run(){
	    		try {
					Thread.sleep(30*60*1000);
				} catch (InterruptedException e) {}
	    		Log.d(TAG, "stopping location updates");
	    	//	locationClient.removeLocationUpdates(LocationListeners[0]);
	    	//	if(locationClient.isConnected())locationClient.disconnect();
	    		stopSelf();
	    	}
	    }.start();
	    
	    super.onStartCommand(intent, flags, startId);       
	    return START_STICKY;
	}
	
	public void stopService(){
  //      if (locationClient.isConnected()) {
   //         locationClient.removeLocationUpdates(LocationListeners[0]);
    //    }
    //    locationClient.disconnect();
		stopSelf();
		this.onDestroy();
	}
	
	@Override
	public void onCreate()
	{
	    Log.d(TAG, "onCreate");
	    if(checkPlayServices()){
	    	Log.d(TAG, "authenticated");
	    }
	    locationRequest = LocationRequest.create();
        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
	 //   locationClient = new LocationClient(this, this, this);
	}
	
	void showErrorDialog(int code) {
	}
	
	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
		        showErrorDialog(status);
		    } else {
		        Toast.makeText(this, "This device is not supported.", 
		            Toast.LENGTH_LONG).show();
		        //finish();
		    }
		    return false;
		}
		return true;
	} 
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "ConnectionFailed", Toast.LENGTH_LONG).show();
		
	}
	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
        //locationClient.requestLocationUpdates(locationRequest, LocationListeners[0]);
	}

	@Override
	public void onConnectionSuspended(int cause) {
	    // TODO Auto-generated method stub
	    
	}

	
}
