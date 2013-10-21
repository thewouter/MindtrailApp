package nl.wouter.gpstest;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GetGpsActivity extends Activity {
	MyLocationListener locationListener;
	Button getData;
	TextView outputView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_gps);
		locationListener = new MyLocationListener(this);
		getData = (Button) findViewById(R.id.get_gps_button);
		outputView = (TextView) findViewById(R.id.gps_uitput);
		
		getData.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Location l = locationListener.getCurrentLocation();
				if(l == null){
					Toast.makeText(GetGpsActivity.this, "no location detected?!", Toast.LENGTH_LONG).show();
					return;
				}
				outputView.setText("lon: " + Util.decimalToDMS(l.getLongitude()) + " lat: " + Util.decimalToDMS(l.getLatitude()));
			}
		});
	}
	
}
