package nl.wouter.gpstest;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivityOptions extends Activity {
	
	Button accept;
	EditText ip, groupName, routeName;
	CheckBox online;
	/*
	protected void onStop() {
		super.onStop();
		finish();
	}*/

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_options);
		accept = (Button) findViewById(R.id.accept);
		ip = (EditText) findViewById(R.id.ip);
		groupName = (EditText) findViewById(R.id.name_group);
		routeName = (EditText) findViewById(R.id.name_route);
		online = (CheckBox) findViewById(R.id.online);
		ip.setText("192.168.1.86:4444");
		accept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MainActivity m = new MainActivity();
				Intent i = new Intent(MainActivityOptions.this, m.getClass());
				i.putExtra("groupName", groupName.getText().toString());
				i.putExtra("ip", ip.getText().toString());
				i.putExtra("routeName", routeName.getText().toString());
				i.putExtra("online", online.isChecked());
				startActivity(i);
				
			}
		});
		online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ip.setEnabled(isChecked);
				if(!isChecked){
					ip.setText("");
				}
			}
		});
		online.setChecked(true);
		if(Util.intent != null){
			AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			manager.cancel(Util.intent);
			Util.intent = null;
			Log.d("MainActivityOptions", "Stopping alarmManagers intent");
		}
	}
}
