package nl.wouter.MindTrail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
				File theDir = new File(Util.saveLocation);
				if(!theDir.exists()){
					theDir.mkdir();
				}
				t.setText(Environment.getExternalStorageDirectory().toString());
			}
		});
	}
}
