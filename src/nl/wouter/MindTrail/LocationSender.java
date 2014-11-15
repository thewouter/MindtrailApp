package nl.wouter.MindTrail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;
import android.widget.Toast;

public class LocationSender extends Thread {
	private String ip, groupName;
	private int port;
	private int lon, lat;
	
	private Socket socket;
	private PrintStream out;
	private BufferedReader in;
	
	public LocationSender(String ip, int port, String groupName, int lon, int lat){
		this.ip = ip;
		this.port = port;
		this.groupName = groupName;
		this.lon = lon;
		this.lat = lat;
	}
	
	public void run() {
		sendCoordinates();
	}
	
	public boolean connect(){
		try {
			socket = new Socket(ip, port);
			out = new PrintStream((socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			Log.e("NetworkHandler", "unknown host: " + ip + ":" + port, e);
			return false;
		} catch (IOException e) {
			Log.e("NetworkHandler", "other exception", e);
			return false;
		} finally{
			Log.d("LocationSender", "Connection established");
		}
		return true;
	}
	
	public void sendGroupName(){
		Log.d("networkhandler", "sending groupname...");
		sendMessage(groupName);
	}
	
	public void disconnect(){
		try {
			socket.close();
		} catch (IOException e) {
			Log.e("NetwordHandler", "error in disconnect", e);
		}
	}
	
	public void messageReceived(int message){
		Log.d("LocationSender", "received: " + message);
		Util.messagesReceived.add(message);
	}
	
	public boolean sendCoordinates(){
		if(!connect())
			
			return false;
		sendGroupName();
		try {
			byte[] bytes = Util.intToBytes(lon);
			Log.d("locationSender", lat + " " + lon);
			out.write(bytes);
			out.write(Util.intToBytes(lat));
		} catch (IOException e1) {
			Log.e("LocationSenderStarter", "error in sending coordinates", e1);
		}
		int received = readInt();
		for(int i = 0; i < received; i++){
			messageReceived(readInt());
		}
		disconnect();
		return true;
	}
	
	public int readInt(){
		byte[] bytes = new byte[4];
		try {
			bytes[0] = (byte) in.read();
			bytes[1] = (byte) in.read();
			bytes[2] = (byte) in.read();
			bytes[3] = (byte) in.read();
		} catch (IOException e) {
			Log.e("LocationSender", "error in receiving integer", e);
		}
		return Util.byteArrayToInt(bytes);
	}
	
	public void sendMessage(String s){
		out.println(s);
	}
}
