package nl.wouter.MindTrail;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GetGpsActivity extends Activity implements LocationListener, GpsStatus.Listener{
	private TextView outputView;
    private LocationManager locationManager;
	private String provider;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1, 
			MINIMUM_TIME_BETWEEN_UPDATES = NetworkHandler.SEND_INTERVAL/2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_gps);
		outputView = (TextView) findViewById(R.id.gps_uitput);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(provider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		outputView.setText("Lon: " + location.getLongitude() + " Lat: " + location.getLatitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(GetGpsActivity.this, "disabled provider: " + arg0, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Toast.makeText(GetGpsActivity.this, "enabled provider: " + arg0, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Toast.makeText(GetGpsActivity.this,arg0 + " changed to " + arg1, Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		
	}
	
}
