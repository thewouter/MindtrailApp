package nl.wouter.gpstest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Thread(){
			public void run(){
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally{
					Intent start = new Intent(Splash.this, MainMenu.class);
					Splash.this.startActivity(start);
				}
			}
		}.start();
	}

	protected void onPause() {
		super.onPause();
		finish();
	}
	
	
}
