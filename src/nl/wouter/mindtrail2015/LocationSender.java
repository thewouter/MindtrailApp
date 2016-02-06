package nl.wouter.mindtrail2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.util.Log;

public class LocationSender{
	private String ip, groupName;
	
	private Socket socket;
	private PrintStream out;
	private BufferedReader in;
	@SuppressWarnings("unused")
	private Context context;
	
	private static int TIME_OUT_MS = 20000;
	
	public LocationSender(String ip, String groupName, Context context){
		this.ip = ip;
		this.groupName = groupName;
		this.context = context;
		
	}
	
	public boolean connect() throws NumberFormatException, UnknownHostException, IOException{
		Log.d("LocationSender", "Conecting to server " + ip + "...");
		socket = new Socket();
		socket.connect(new InetSocketAddress(ip.split(":")[0], Integer.parseInt(ip.split(":")[1])), TIME_OUT_MS);
		socket.setSoTimeout(TIME_OUT_MS);
		out = new PrintStream((socket.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		Log.d("LocationSender", "Connected.");
		return true;
	}
	
	public void sendGroupName(){
		Log.d("networkhandler", "sending groupname...");
		sendMessage(groupName);
	}
	
	public void disconnect() throws IOException{
		socket.close();
	}
	
	public void messageReceived(int message){
		Log.d("LocationSender", "received: " + message);
		Util.messagesReceived.add(message);
	}
	
	public boolean sendCoordinates(){
		try {
			if(socket != null && socket.isConnected()){
				Log.w("LocationSender", "socket was still connected, was disconnected first!");
				socket.close();
			}
			connect();
			sendGroupName();
			int sendLon = (int)(Util.LON * 10000000);
			int sendLat = (int)(Util.LAT * 10000000);
			
			sendMessage("" + sendLon);
			sendMessage("" + sendLat);
			/*
			byte[] bytesLon = Util.intToBytes(sendLon);
			byte[] bytesLat = Util.intToBytes(sendLat);
			out.write(bytesLon);
			out.write(bytesLat);*/
			Log.d("locationSender", "Sendt: " + Util.LAT + " " + Util.LON);
			
			Log.d("LocationSender", "start reading received messages");
			int received = Integer.parseInt(read());
			Log.d("LocationSender",  received + " messages to read");
			for(int i = 0; i < received; i++){
				messageReceived(readInt());
			}
			Log.d("LocationSender", "Messages read, now disconnecting");
			disconnect();
			Log.d("LocationSender", "Disconnected.");
		} catch (NumberFormatException e) {
			//Toast.makeText(context, "connecting to server failed, NumberFormatException!", Toast.LENGTH_LONG).show();
			Log.e("LocationSender", "Sending Location Failed, NumberFormatException", e);
			return false;
		} catch (UnknownHostException e) {
			//Toast.makeText(context, "connecting to server failed, UnknownHostException!", Toast.LENGTH_LONG).show();
			Log.e("LocationSender", "Sending Location Failed, UnknownHostException", e);
			return false;
		} catch (IOException e) {
			//Toast.makeText(context, "connecting to server failed, IOException!", Toast.LENGTH_LONG).show();
			Log.e("LocationSender", "Sending Location Failed, IOException", e);
			return false;
		}
		return true;
	}
	
	private String read() throws IOException{
		return in.readLine();
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
			return 0;
		}
		return Util.byteArrayToInt(bytes);
	}
	
	public void sendMessage(String s){
		out.println(s);
	}
}
