package nl.wouter.MindTrail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class FileHandler {
	
	public static ArrayList<PostAlert> loadAlerts(String tochtName, Context context){
		ArrayList<PostAlert> alerts = new ArrayList<PostAlert>();
		FileInputStream in = null;;
		byte[] fileContent = null;
		File file = new File(Util.saveLocation + File.separator + tochtName);
		if(!file.exists()) {
			file.mkdir();
			Toast.makeText(context, "Route not available: " + file.getPath(), Toast.LENGTH_LONG).show();
			return alerts;
		}
		file = new File(Util.saveLocation + File.separator + tochtName + File.separator + "data.dat");
		try {
			in = new FileInputStream(file);
			fileContent = new byte[(int)file.length()];
			in.read(fileContent);
		} catch (FileNotFoundException e) {
			Toast.makeText(context, "FileNotFound: ", Toast.LENGTH_SHORT).show();
			return alerts;
		} catch (IOException e) {
			Toast.makeText(context, "IOException", Toast.LENGTH_SHORT).show();
			return alerts;
		} catch (Exception e){
			Toast.makeText(context, "Other exception", Toast.LENGTH_LONG).show();
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
				}
				isNext = ((id = fileContent[i++]) != 48 ? true: false);
			}
		}catch(Exception e){
			Toast.makeText(context, "Something went wrong loading: " 
					+ Util.saveLocation + File.separator + tochtName + 
					File.separator + "data.dat\n" + e.getMessage() , Toast.LENGTH_LONG).show();
			return alerts;
		}
		return alerts;
	}
	
}
