package nl.wouter.MindTrail;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenu extends ListActivity {
	
	String classes[] = {"MainActivityOptions", "GetGpsActivity", "TestActivity"};
	String names[] = {"Open route", "getGpsData", "Test"};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_list_item_1, names));
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			Class myClass = Class.forName("nl.wouter.MindTrail." + classes[position]);
			Intent toStart = new Intent(MainMenu.this, myClass);
			startActivity(toStart);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
