package nl.wouter.gpstest;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

public class PostAlert {

	private double lat;
	private double lon;
	private int distance;
	private String sound, routeName;
	private boolean hasPlayed = false, isPlaying = false;

	public PostAlert(double lat, double lon, int distance, String sound, String routeName) {
		this.lat = lat;
		this.lon = lon;
		this.distance = distance;
		this.sound = sound;
		this.routeName = routeName;
	}
	/**
	 * 
	 * @param context
	 * @return Whether alert can be removed.
	 */
	public boolean check(Context context){
		//Toast.makeText(context, "starting checking alert", Toast.LENGTH_LONG).show();
		if(Util.distFrom(Util.LAT, Util.LON, lat, lon) <= distance && !hasPlayed){
			//Toast.makeText(context, "in Range", Toast.LENGTH_LONG).show();
			isPlaying = true;
			MediaPlayer player = new MediaPlayer();
			try {
				player.setDataSource(Util.saveLocation + File.separator + routeName + File.separator + "sounds" + File.separator + sound);
				player.prepare();
				player.start();
			} catch (IllegalArgumentException e) {
				Toast.makeText(context, "IllegalArgumentException", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IllegalStateException e) {
				Toast.makeText(context, "IllegalStateException", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(context, "IOException: " + e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return false;
			
		}else if(isPlaying){
			hasPlayed = true;
			isPlaying = false;
			//Toast.makeText(context, "out of range: " + Util.distFrom(Util.LAT, Util.LON, lat, lon) + "m.", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	public String getCoordinates() {
		return lat + " " + lon;
	}

}
