package nl.wouter.gpstest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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

public class MainActivity extends Activity {
    private String groupName, routeName;
    private String ip;
    private MyLocationListener locationListener;
    private boolean online = false;
    private NetworkHandler network;
    private Button sendCoords, switchSending;
    private TextView display, imageIndexDisplay;
    private ArrayList<PostAlert> alerts, alertsBuffer;
    private long timeCounter = 0;
    private static long TIME_BETWEEN_SOUNDS = 1000;
    private AudioTrack audioTrack;
    private Thread mainLoop;
    private Handler handler = new Handler();
    private int postAlertCounter = 0;
    private Gallery gallery;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		groupName = extras.getString("groupName");
		ip = extras.getString("ip");
		routeName = extras.getString("routeName");
		online = extras.getBoolean("online");
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
    	locationListener = new MyLocationListener(this);
    	if(online) {
    		if(ip.split(":").length != 2){
    			online = false;
    			return;
    		}
    		network = new NetworkHandler(this, ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
    		network.startSending();
    	}
    	alertsBuffer = new ArrayList<PostAlert>();
    	// Add first 5 alerts to buffer
    	if(alerts.size() >=5){
    		alertsBuffer.addAll(alerts.subList(0, 5));
        	postAlertCounter += 5;
    	}
    	Date date = new Date();
    	timeCounter = date.getTime();
        int minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_STEREO, 
                AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_STEREO, 
             AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
        Log.d("mainActivity", "creating mainloop");
        mainLoop = new Thread(){
        	public void run(){
        		Log.d("MainActivity", "starting readLoop");
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
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        };
        Log.d("mainActivity", "starting mainloop");
        mainLoop.start();
        Log.d("MainActivity", "done creating");
	} 
	
	public String getGroupname(){
		return groupName;
	}
	
	protected void onDestroy() {
		if(online) network.stopSending();
		locationListener.stopTracking()	;
		super.onDestroy();
	}

	public Location getCurrentLocation(){
		return locationListener.getCurrentLocation();
	}

	public void locationChanged() { 
    	Date date = new Date();
    	timeCounter = date.getTime();
		for(int i = 0; i < alertsBuffer.size(); i++){
			if(alertsBuffer.get(i).check(this)){ 	//alert can be removed, and new can be added;
				alertsBuffer.removeAll(alertsBuffer.subList(0, i-1));
				alertsBuffer.addAll(alerts.subList(postAlertCounter, postAlertCounter++ + i));
				display.setText("removed alerts, now containing " + alertsBuffer.size() + "alerts");
			}
		}
	}
	
	public void onResume(){
		super.onResume();
	}
	
	public void onPause(){
		super.onPause();
	}
	
	
	public class ImageAdapter extends BaseAdapter {
        private Context ctx;
        int imageBackground;
        

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
