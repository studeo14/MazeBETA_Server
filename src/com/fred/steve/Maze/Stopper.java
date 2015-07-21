package com.fred.steve.Maze;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Stopper {
	static ObjectInputStream sIn;
	static ObjectOutputStream sOut;
	static Socket socket;
	static final String SERVER= "localhost";
	static final int PORT = 1720;
	public static void main(String... args){
		boolean b = connect();
		if(b){
		boolean a = stopSever();
		boolean c = disconnect();
		}
	}
	
	private static boolean connect() {
		try{
			socket = new Socket(SERVER, PORT);
		}catch(Exception e){
			System.out.println("Socket Broke");
			return false;
		}
		
		/*
		 * Create both data streams
		 */
		try{
			sIn = new ObjectInputStream(socket.getInputStream());
			sOut = new ObjectOutputStream(socket.getOutputStream());
		}catch(IOException e){
			System.out.println("IO streams Broke");
			return false;
		}
		//everything worked
		System.out.println("Everything Worked!");
		return true;
	}
	
	private static boolean stopSever() {
		try{
			sOut.writeObject(new PlayerMessage(PlayerMessage.TYPE.STOP_SERVER, "27",-1,-1));
		}catch(IOException e){
			return false;
		}
		//everything worked!
		return true;
	}
	private static boolean disconnect() {
		try{
			sOut.writeObject(new PlayerMessage(PlayerMessage.TYPE.DISCONNECT));
		}catch(IOException e){
			System.out.println("Error sending disconnect message. Hopefully because the server has closed.");
		}
		// try to close the connection
		try {
			if(sOut != null) sOut.close();
		}
		catch(Exception e) {
			
			return false;
		}
		try {
			if(sIn != null) sIn.close();
			}
		catch(Exception e) {
			
			return false;
		}
		try {
			if(socket != null) socket.close();
		}
		catch (Exception e) {
			
			return false;
		}
		return true;	
	}
}
