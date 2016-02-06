package nl.wouter.mindtrail2015;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import nl.wouter.mindtrail2015.R;

public class MainActivityOptions extends Activity {
	
	Button accept;
	EditText ip, groupName;
	CheckBox online;
	
	private static String STANDARD_IP = "woutervanharten.nl:4444";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("mainActivityOptions", "creating options menu");
		setContentView(R.layout.route_options);
		accept = (Button) findViewById(R.id.accept);
		ip = (EditText) findViewById(R.id.ip);
		groupName = (EditText) findViewById(R.id.name_group);
		online = (CheckBox) findViewById(R.id.online);
		ip.setText(STANDARD_IP);
		accept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity m = new MainActivity();
				Intent i = new Intent(MainActivityOptions.this, m.getClass());
				i.putExtra("groupName", groupName.getText().toString().replace(" ", "$"));
				i.putExtra("ip", ip.getText().toString());
				i.putExtra("online", online.isChecked());
				startActivity(i);
				finish();
			}
		});
		online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ip.setEnabled(isChecked);
			}
		});
		online.setChecked(true);
	}
}
