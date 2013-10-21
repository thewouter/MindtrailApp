package nl.wouter.gpstest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private String groupName, routeName;
    private String ip;
    private MyLocationListener locationListener;
    private boolean online = false;
    private NetworkHandler network;
    private Button sendCoords, switchSending, show;
    private TextView display;
    private ArrayList<PostAlert> alerts;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		groupName = extras.getString("groupName");
		ip = extras.getString("ip");
		routeName = extras.getString("routeName");
		online = extras.getBoolean("online");
		Log.d("MainActivity", "routeName is " + routeName);
		switchSending = (Button) findViewById(R.id.switch_sending);
		sendCoords = (Button) findViewById(R.id.send);
		show = (Button) findViewById(R.id.show_coords);
		display = (TextView) findViewById(R.id.display);
		
		show.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				display.setText(alerts.get(0).getCoordinates());
				
				//display.setText(Util.decimalToDMS(Util.LAT) + " " + Util.decimalToDMS(Util.LON));
			}
		});
		switchSending.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(online) network.startSending();
			}
		});
		sendCoords.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(online) network.stopSending();
			}
		});
    	locationListener = new MyLocationListener(this);
    	if(online) {
    		if(ip.split(":").length != 2){
    			online = false;
    			return;
    		}
    		network = new NetworkHandler(this, ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
    		network.startSending();
    	}
    	alerts = FileHandler.loadAlerts(routeName, this);
	} 
	
	public String getGroupname(){
		return groupName;
	}
	
	protected void onDestroy() {
		if(online) network.stopSending();
		locationListener.stopTracking();
		super.onDestroy();
	}

	public Location getCurrentLocation(){
		return locationListener.getCurrentLocation();
	}

	public void locationChanged() {
		//Toast.makeText(this, alerts.size() + "", Toast.LENGTH_LONG).show();
		for(PostAlert a:alerts){
			a.check(this);
		}
	}
	
}
