package nl.wouter.mindtrail2015;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import nl.wouter.mindtrail2015.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, 
		LocationListener{
	
	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 2;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    
    private String groupName;
    private String ip;
    private boolean online = false, isSending = false;
    private TextView display, imageIndexDisplay, mainDisplay;
    private ArrayList<PostAlert> alerts; 
    private int bufferSize = 5, bufferStart = 0;
    private ViewPager pager;
    private Button helpButton;
    private PagerAdapter adapter;
	private static String routeName = "mindtrail";
	private SendLoop mainLoop;
	private MediaPlayer player;
	private int lastPost = 0;
	
	private static String CODE = "MINI";
   
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		buildGoogleApiClient();
		googleApiClient.connect();
		
		locationRequest = LocationRequest.create();
        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        
        Intent i = getIntent();
		Bundle extras = i.getExtras();
		groupName = extras.getString("groupName");
		ip = extras.getString("ip");
		online = extras.getBoolean("online");
		mainDisplay = (TextView) findViewById(R.id.main_message);
		display = (TextView) findViewById(R.id.display);
		pager = (ViewPager) findViewById(R.id.pager);
		helpButton = (Button) findViewById(R.id.helpButton);
		imageIndexDisplay = (TextView) findViewById(R.id.imageIndexDisplay);
    	alerts = FileHandler.loadAlerts(routeName, this);
    	Bitmap[] images = loadBitmaps(routeName);
    	adapter = new ViewPagerAdapter(MainActivity.this, images);
		player = new MediaPlayer();
		
    	helpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(MainActivity.this);
				new AlertDialog.Builder(MainActivity.this)
			    .setTitle("Check Code")
			    .setMessage("Bel met de tochtstaf als je verdwaald bent voor de nood-code")
			    .setView(input)
			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            Editable value = input.getText(); 
			            if(CODE.equals(value.toString())){
							pager.setAdapter(adapter);
							helpButton.setVisibility(View.GONE);
			            }else{
			            	Log.d("MainActivity",value.toString());
			            }
			        }
			    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            // Do nothing.
			        }
			    }).show();
			}
		});
    	
    	if(online) {
			Log.d("MainActivity", "starting online...");
			isSending = true;
    		if(ip.split(":").length != 2){
    			online = false;
    			Toast.makeText(MainActivity.this, "Invalid IP", Toast.LENGTH_LONG).show();
    		}
    	}
    	
        Log.d("mainActivity", "creating mainloop");
        
        mainLoop = new SendLoop();
        
        mainLoop.start();
	}
	
	protected synchronized void buildGoogleApiClient() {
	    googleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}
	
	public Bitmap[] loadBitmaps(String routename) {
        Log.d("MainActivity", "loading images...");
        String[] lImages = {};
		try {
			lImages = getAssets().list("route/" + routename + "/images");
		} catch (IOException e) {
			Log.e("MainActivity", "IOException", e);
		}
        int size = lImages.length;
        Log.d("MainActivity", "The amount of images is:" + size);
        Bitmap[] bitmaps = new Bitmap[size];
        for(int i = 1; i < size; i++){
        	Bitmap bmp = getBitmapFromAsset(MainActivity.this, "route/" + routeName + 
        			"/images/" + (i) + ".png");
        	bitmaps[i-1] = bmp;
        }
        Log.d("MainAcivity", "loaded images");
        return bitmaps;
    }
    
    public Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            Log.e("mainactivity", "error in getBimapFromAsset", e);
        }
        return bitmap;
    }
	
	@Override
	protected void onStart() {
		/*locationClient.connect();
	  	if (checkPlayServices()) {
		  	Toast.makeText(MainActivity.this, "Authenticated google Play Services", Toast.LENGTH_LONG).show();
	  	}*/
		//startLocationUpdates();
		super.onStart();
	}

	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
		        showErrorDialog(status);
		    } else {
		        Toast.makeText(this, "This device is not supported.", 
		            Toast.LENGTH_LONG).show();
		        finish();
		    }
		    return false;
		}
		return true;
	} 

	private void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this, 
		      REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case REQUEST_CODE_RECOVER_PLAY_SERVICES:
	            if (resultCode == RESULT_CANCELED) {
	            	Toast.makeText(this, "Google Play Services must be installed.",
	            			Toast.LENGTH_SHORT).show();
	            	finish();
	            }
	            return;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy() {/*
		 // If the client is connected
        if (locationClient.isConnected()) {
            /**
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             **//*
            locationClient.removeLocationUpdates(this);
        }
        /**
         * After disconnect() is called, the client is
         * considered "dead".
         *//*
        locationClient.disconnect();*/
		stopLocationUpdates();
        ((SendLoop) mainLoop).stopRunning();
		super.onDestroy();
	}

	@Override
	public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
        //locationClient.requestLocationUpdates(locationRequest, this);
    }
	
	protected void startLocationUpdates(){
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}
	
	protected void stopLocationUpdates(){
		LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
	}

	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
		startLocationUpdates();
		
	}
	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Terug")
	        .setMessage("Weet je zeker dat je de tocht wil stoppen?")
	        .setPositiveButton("ja", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            finish();    
	        }

	    })
	    .setNegativeButton("nee", null)
	    .show();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        }
		startLocationUpdates();
		
	}
	
	public void locationChanged() { 
		Log.d("MainActivity", "checking posts");
		
		for(PostAlert p :alerts){
			if(p.check(this)){
				int i = alerts.indexOf(p);
				imageIndexDisplay.setText(i + 1 + "");
				pager.setCurrentItem(i);
				lastPost = i;
				break;
			}
		}
		pager.setCurrentItem(lastPost);
		LinkedList<PostAlert> toRemove = new LinkedList<PostAlert>();
		for(PostAlert a: Util.scaryPosts){
			if(a.check(this)){
				toRemove.add(a);
			}
		}
		Util.scaryPosts.removeAll(toRemove);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		//Log.d("MainActivity", "Location has changed to :" + location.getLatitude() + " " + location.getLongitude());
		Util.LAT = location.getLatitude();
		Util.LON = location.getLongitude();
		mainDisplay.setText(Util.decimalToDMS(Util.LON) + "\n" + Util.decimalToDMS(Util.LAT) + 
				"\n" + "acc.: " + (int) location.getAccuracy() + " m.");
		locationChanged();
	}
	
	public String getGroupname(){
		return groupName;
	}
	
	private class SendLoop extends Thread{
		Boolean running = true;
		LocationSender sender;
		
		public void stopRunning(){
			running = false;
			Log.d("SendLoop", "Stopping sending cordinates");
		}
		
    	public void run(){
			sender = new LocationSender(ip, groupName, MainActivity.this );
    		if(!online) return;
    		while(running){
    			if(isSending){
        			Log.d("MainActivity", "sending data to server at " + ip);
    				sender.sendCoordinates();
    			}else{
    				Log.d("MainActivity", "Not sending ");
    			}
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
    						//player.reset();
    						display.setText(new String(arr));
    						player = MediaPlayer.create(MainActivity.this, R.raw.phone);
    						try {
								//player.prepare();
								Log.d("MainActivity", "starting playing");
	    						player.start();
	    						Log.d("MainActivity", "started playing");
							} catch (IllegalStateException e) {
								e.printStackTrace();
								Log.e("MainActivity", "Illeagal state", e);
							}
    						player.start();
    						Log.d("MainActivity", player.isPlaying() + "");
    					}
    				});
    				Util.messagesReceived.clear(); 
    			}
    			try {
					sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.e("MainActivity", "Crashed on sleep", e);
				}
    		}
    	}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		startLocationUpdates();
	}
}

