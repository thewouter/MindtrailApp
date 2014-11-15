package nl.wouter.MindTrail;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
    private String groupName, routeName;
    private String ip;
    private boolean online = false;
    private NetworkHandler network;
    private Button sendCoords, switchSending;
    private TextView display, imageIndexDisplay, mainDisplay;
    private ArrayList<PostAlert> alerts;
    private long timeCounter = 0;
    private static long TIME_BETWEEN_SOUNDS = 1000;
    private AudioTrack audioTrack;
    private Thread mainLoop;
    private Handler handler = new Handler();
    private int bufferSize = 5, bufferStart = 0;
    private Gallery gallery;
    private LocationManager locationManager;
	private String provider;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1, 
			MINIMUM_TIME_BETWEEN_UPDATES = NetworkHandler.SEND_INTERVAL/2; // in Milliseconds
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		groupName = extras.getString("groupName");
		ip = extras.getString("ip");
		routeName = extras.getString("routeName");
		online = extras.getBoolean("online");
		mainDisplay = (TextView) findViewById(R.id.main_message);
		switchSending = (Button) findViewById(R.id.switch_sending);
		sendCoords = (Button) findViewById(R.id.send);
		display = (TextView) findViewById(R.id.display);
		gallery = (Gallery) findViewById(R.id.gallery);
		imageIndexDisplay = (TextView) findViewById(R.id.imageIndexDisplay);
    	alerts = FileHandler.loadAlerts(routeName, this);
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setOnItemSelectedListener(new OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            	imageIndexDisplay.setText("" + (position + 1));
            }

			public void onNothingSelected(AdapterView<?> arg0) {
				
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
    	if(online) {
    		if(ip.split(":").length != 2){
    			online = false;
    			return;
    		}
    		network = new NetworkHandler(this, ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
    		network.startSending();
    	}
    	Date date = new Date();
    	timeCounter = date.getTime();
        int minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_STEREO, 
                AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_STEREO, 
             AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
        Log.d("mainActivity", "creating mainloop");
        mainLoop = new Thread(){ // checking for messages from host
        	public void run(){
        		Log.d("MainActivity", "starting readLoop");
        		if(!online) return;
        		while(true){
        			Log.d("MainActivity", "reading...");
        			if(Util.messagesReceived.size() > 0){
        				Log.d("MainActivity", "found something!");
        				ArrayList<Integer> ints = Util.messagesReceived;
        				final char[] arr = new char[ints.size()];
        				for(int i = 0; i < ints.size(); i++){
        					int temp = ints.get(i);
        					arr[i] = (char) temp;
        				}
        				MainActivity.this.runOnUiThread(new Runnable(){
        					public void run(){
        						display.setText(new String(arr));
        						MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.phone);
        						try {
									player.prepare();
								} catch (IllegalStateException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
        						player.start();
        					}
        				});
        				Util.messagesReceived.clear(); 
        			}else{
        				Log.d("MainActivity", "nothing...");
        			}
        			try {
						sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        };
        Log.d("mainActivity", "starting mainloop");
        mainLoop.start();
        Log.d("MainActivity", "done creating");
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location != null)
        	mainDisplay.setText(location.getLatitude() + " " + location.getLongitude());
		locationManager.requestLocationUpdates(provider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);

	}
	
	public String getGroupname(){
		return groupName;
	}
	
	@Override
	protected void onDestroy() {
		if(online) network.stopSending();
		locationManager.removeUpdates(this);
		super.onDestroy();
	}

	public void locationChanged() { 
    	Date date = new Date();
    	timeCounter = date.getTime();
		for(int i = bufferStart; i < Math.min(bufferStart + bufferSize, alerts.size()); i++){
			if(alerts.get(i).check(this)){ 	//alert can be removed, and new can be added;
				bufferStart = i;
				break;
			}
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		locationManager.requestLocationUpdates(provider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
		Log.d("mainActivity", "started requesting location updates on provider: " + provider);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		//locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Util.LAT = location.getLatitude();
		Util.LON = location.getLongitude();
		mainDisplay.setText(Util.LON + " " + Util.LAT);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
	
	private class ImageAdapter extends BaseAdapter {
        private Context ctx;
        

        public ImageAdapter(Context c) {
            ctx = c;
        }

        public int getCount() {
        	if(alerts == null){
        		return 0;
        	}
            return alerts.size();
        }

        public Object getItem(int arg0) {
            return arg0;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ImageView iv = new ImageView(ctx);
            //Toast.makeText(ctx, Util.saveLocation + File.separator + routeName + File.separator + "images" + File.separator + (arg0 + 1) + ".png", Toast.LENGTH_SHORT).show();
            //Toast.makeText(ctx, "" + (arg0 + 1), Toast.LENGTH_LONG).show();
            Bitmap bmp = BitmapFactory.decodeFile(Util.saveLocation + File.separator + routeName + File.separator + "images" + File.separator + (arg0 + 1) + ".png");
            iv.setImageBitmap(bmp);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            return iv;
        }
    }
}
