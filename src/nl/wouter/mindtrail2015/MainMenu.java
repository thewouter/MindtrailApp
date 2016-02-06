package nl.wouter.mindtrail2015;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainMenu extends ListActivity {
	
	String classes[] = {"MainActivityOptions"/*, "GetGpsActivity"/*, "TestActivity"/*, "GpsTester"*/};
	String names[] = {"Start route"/*, "Test GPS"/*, "TempTest"/*, "Test service"*/};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(MainMenu.this, android.R.layout.simple_list_item_1, names));
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			Class<?> myClass = Class.forName("nl.wouter.mindtrail2015." + classes[position]);
			Intent toStart = new Intent(MainMenu.this, myClass);
			if(position < 3)startActivity(toStart);
			else startService(toStart);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
