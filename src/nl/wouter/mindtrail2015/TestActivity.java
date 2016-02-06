package nl.wouter.mindtrail2015;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import nl.wouter.mindtrail2015.R;

public class TestActivity extends Activity {
	
	Button b;
	TextView t;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		b = (Button) findViewById(R.id.test_button);
		t = (TextView) findViewById(R.id.test_text);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Context context = TestActivity.this;
				String routeName = "mindtrail", sound = "sonar1.wav";
				MediaPlayer player = new MediaPlayer();
				try {
					String path = "route" + File.separator + routeName + File.separator + "sounds" + File.separator + sound;
					AssetFileDescriptor fis = getAssets().openFd(path);
					player.setDataSource(fis.getFileDescriptor(), fis.getStartOffset(), fis.getLength());
					player.prepare();
				} catch (IllegalArgumentException e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (SecurityException e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (IllegalStateException e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (IOException e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				player.start();
			}
		});
	}
}
