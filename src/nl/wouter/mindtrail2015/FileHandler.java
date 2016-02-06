package nl.wouter.mindtrail2015;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class FileHandler {
	
	public static ArrayList<PostAlert> loadAlerts(String tochtName, Context context){
		ArrayList<PostAlert> alerts = new ArrayList<PostAlert>();
		InputStream in = null;;
		byte[] fileContent = null;
		String location = "route" + File.separator + tochtName + File.separator + "crossings.dat";
		try {
			in = context.getAssets().open(location);
			fileContent = new byte[in.available()];
			in.read(fileContent);
		} catch (FileNotFoundException e) {
			Toast.makeText(context, "FileNotFound: " + location, Toast.LENGTH_SHORT).show();
			Log.e("FileHandler", "File not Found!", e);
			String total = "a";
			try {
				for(String s:context.getAssets().list("route/")){
					total = total + s + " ";
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.i("FileHandler", total);
			return alerts;
		} catch (IOException e) {
			Toast.makeText(context, "IOException", Toast.LENGTH_SHORT).show();
			return alerts;
		} catch (Exception e){
			Toast.makeText(context, "Other exception", Toast.LENGTH_LONG).show();
			Log.e("FileHandler", "Other exception", e);
			return alerts;
		} finally {
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			Log.d("FileHandler", "read:" + fileContent.length);
			int i = 0;
			int id;
			boolean isNext = ((id = fileContent[i++]) != 48 ? true: false);
			while(isNext){
				switch(id){
				case 49: // normal PostAlert
					Log.d("FileHandler", "starting a new Alert");
					int[] ints = new int[3];
					String sound = null;
					for(int ii = 0; ii <= ints.length; ii++){ // four pieces of data per postAlert
						String data = "";
						boolean running = true;
						while(running && i < fileContent.length){
							byte b = fileContent[i++];
							if(b != 32){
								data = data + (char) b;
							}else{
								running = false;
							}
						}
						Log.d("FileHandler", "Read piece " + ii + ": " + data);
						if(ii < ints.length) {
							ints[ii] = Integer.parseInt(data);
						}else{
							sound = data;
						}
					}
					alerts.add(new PostAlert((double)ints[0] / 1000000.0, (double)ints[1] / 1000000.0, ints[2], sound, tochtName));
					break;
				case 50: //scary postAlert
					Log.d("FileHandler", "starting a new Alert");
					int[] ints2 = new int[3];
					String sound2 = null;
					for(int ii = 0; ii <= ints2.length; ii++){ // four pieces of data per postAlert
						String data = "";
						boolean running = true;
						while(running && i < fileContent.length){
							byte b = fileContent[i++];
							if(b != 32){
								data = data + (char) b;
							}else{
								running = false;
							}
						}
						Log.d("FileHandler", "Read piece " + ii + ": " + data);
						if(ii < ints2.length) {
							ints2[ii] = Integer.parseInt(data);
						}else{
							sound2 = data;
						}
					}
					Util.scaryPosts.add(new PostAlert((double)ints2[0] / 1000000.0, (double)ints2[1] / 1000000.0, ints2[2], sound2, tochtName));
					break;
				}
				isNext = ((id = fileContent[i++]) != 48);
			}
		}catch(Exception e){
			Toast.makeText(context, "Something went wrong loading: " 
					+ "route" + File.separator + tochtName + 
					File.separator + "data.dat\n" + e.getMessage() , Toast.LENGTH_LONG).show();
			return alerts;
		}
		return alerts;
	}
	
}
