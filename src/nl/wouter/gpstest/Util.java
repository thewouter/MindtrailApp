package nl.wouter.gpstest;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.os.Environment;

public class Util {
	
	public static double LON = 0;
	public static double LAT = 0;
	public static PendingIntent intent = null;
	public static String saveLocation = Environment.getExternalStorageDirectory().getPath() + File.separator + "GpsTest";
	public static ArrayList<Integer> messagesReceived = new ArrayList<Integer>();

	public static String decimalToDMS(double coord) {
	    String output, degrees, minutes, seconds;
	    double mod = coord % 1;
	    int intPart = (int)coord;
	    degrees = String.valueOf(intPart);
	    coord = mod * 60;
	    mod = coord % 1;
	    intPart = (int)coord;
	    minutes = String.valueOf(intPart);
	    coord = mod * 60;
	    intPart = (int)coord;
	    seconds = String.valueOf(intPart);
	    output = degrees + "°" + minutes + "\'" + seconds + "\"";
	    return output;
	}
	
	public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	    
	    int meterConversion = 1609;

	    return Float.valueOf((float) (dist * meterConversion));
	}

	public static byte[] intToBytes( final int i ) {
	    ByteBuffer bb = ByteBuffer.allocate(4); 
	    bb.putInt(i); 
	    return bb.array();
	}
	
	public static int byteArrayToInt(byte[] b) {
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}

}
