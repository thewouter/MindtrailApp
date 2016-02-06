package nl.wouter.mindtrail2015;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import nl.wouter.mindtrail2015.R;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		new Thread(){
			public void run(){
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally{
					Intent start = new Intent(SplashScreen.this, MainMenu.class);
					SplashScreen.this.startActivity(start);
					finish();
				}
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
