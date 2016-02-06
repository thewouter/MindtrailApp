package nl.wouter.mindtrail2015;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;

public class PostAlert {

	private double lat;
	private double lon;
	private int distance;
	private String sound, routeName;
	private boolean isPlaying = false, hasPlayedOnce = false;
	private MediaPlayer player;

	public PostAlert(double lat, double lon, int distance, String sound, String routeName) {
		this.lat = lat;
		this.lon = lon;
		this.distance = distance;
		this.sound = sound;
		this.routeName = routeName;
		this.player = new MediaPlayer();
	}
	
	/**
	 * @param context
	 * @return Whether alert is in range.
	 */
	
	public boolean check(Context context){
		double distanceFromAlert = Util.distFrom(Util.LAT, Util.LON, lat, lon);
		if(distanceFromAlert <= distance/* && !isPlaying*/){
			
			isPlaying = true;
			player.reset();
			try {
				String path = "route" + File.separator + routeName + File.separator + "sounds" + File.separator + sound;
				AssetFileDescriptor fis = context.getAssets().openFd(path);
				player.setDataSource(fis.getFileDescriptor(), fis.getStartOffset(), fis.getLength());
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
			} catch (Exception e) {
				Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
			
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					isPlaying = false;
					hasPlayedOnce = true;
				}
			});
			return true;
		}
		return false;
	}
	
	public boolean hasPlayedOnce(){
		return hasPlayedOnce;
	}

	public String getCoordinates() {
		return lat + " " + lon;
	}
}
