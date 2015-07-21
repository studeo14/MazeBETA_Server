package com.fred.steve.Maze;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MazeServerLocal {
		// a unique ID for each connection
		private static int uniqueId;
		//ArrayList of connected members
		private static ArrayList<Player> ml;
		//the port to be run on
		static private final int PORT = 1720;
		//will keep the server running
		static private boolean keepGoing;
		public static final int MAX_PLAYERS = 2;
		public static ArrayList<PlayerData> data;
		
		public MazeServerLocal(){
			ml = new ArrayList<Player>();
		}
		public void begin(){
			keepGoing = true;
			/*
			 * Create a socket server and wait for connections
			 */
			try{
				ServerSocket serverSocket = new ServerSocket(PORT);
				
				//loop to wait for connections
				while(keepGoing){
					//message to say that we are waiting
					display("Server is waiting for Members on port " + PORT + ".");
					
					Socket socket = serverSocket.accept();// accept connection
					//if asked to stop
					if(!keepGoing){
						break;
					}
					Player m = new Player(socket);
					ml.add(m);
					m.start();
				}
				try{
					serverSocket.close();
					for(int i = 0; i < ml.size(); ++i){
						Player mb = ml.get(i);
						try{
							mb.sInput.close();
							mb.sOutput.close();
							mb.socket.close();
						}catch(IOException ioE){}
					}
				}catch(Exception e){e.printStackTrace();}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		protected static void stop(){
			System.out.println("Now Stoping Server...");
			keepGoing = false;
			try{
				new Socket("localhost", PORT);
			}catch(Exception e){}
		}
		private void display(String string) {
			System.out.println(string);
		}
		synchronized static void remove(int id){
			for(Player mn: ml){
				if(mn.id == id){
					ml.remove(mn);
					ml.trimToSize();
					return;
				}
			}
		}
		public static void main(String... args){
			MazeServerLocal ms = new MazeServerLocal();
			ms.begin();
		}
		public void sendUpdate() {
			for(Player p:ml){
				p.writeToMember(new PlayerMessage(PlayerMessage.TYPE.SEND_UPDATE_TO_PLAYER, MazeServerLocal.data));
			}
		}
		
		class Player extends Thread{
			Socket socket;
			ObjectInputStream sInput;
			ObjectOutputStream sOutput;
			
			public int id;
			public PlayerMessage plMes;
			public String username;
			private boolean connected = false;
			
			Player(Socket socket){
				id = ++uniqueId;
				this.socket = socket;
				System.out.println("Thread trying to create Object Input/Output Streams");
				try{
					//create output first
					sOutput = new ObjectOutputStream(socket.getOutputStream());
					sInput = new ObjectInputStream(socket.getInputStream());
					//read the username
				}catch(IOException e){e.printStackTrace();}
			}
			
			public void run(){
				display("Run invoked");
				boolean keepGoing = true;
				while(keepGoing){
					try{
						plMes = (PlayerMessage) sInput.readObject();
						
							display("Message Recieved");
						
							switch(plMes.myType){
							case SEND_UPDATE_TO_SERVER:
								if(connected){
									for(PlayerData pd:data){
										if(this.username == pd.username){
											data.get(data.indexOf(pd)).setX(plMes.getX());
											data.get(data.indexOf(pd)).setY(plMes.getY());
										}
									}
									sendUpdate();
								}
								break;
							case DISCONNECT:
								keepGoing = false;
								break;
							case CONNECT:
								if(ml.size()>=MAX_PLAYERS){
									writeToMember(new PlayerMessage(PlayerMessage.TYPE.TOO_MANY_PLAYERS));
								}else{
									boolean taken = false;
									for(Player p:ml){
										if(plMes.username == p.username){
											writeToMember(new PlayerMessage(PlayerMessage.TYPE.USERNAME_TAKEN));
											taken = true;
											break;
										}
									}
									if(!taken){
										this.username = plMes.username;
										data.add(new PlayerData(this.username));
									}
								}
								connected = true;
								break;
							case STOP_SERVER:
								if(plMes.username.equals("27")){
									display("ADMIN STEVEN FREDERIKSEN STOPPED SERVER.");
									keepGoing = false;
									MazeServerLocal.stop();
								}
								break;
							default:
								break;
							}
						
					}catch(IOException e){
						e.printStackTrace();
						keepGoing = false; display("IOException");}
					catch (ClassNotFoundException e){
						e.printStackTrace();

						keepGoing = false;}
					catch(NullPointerException e){
						e.printStackTrace();

						keepGoing = false;
						display("NullPointerException");
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				remove(id);
				close();
			}
			private void writeToMember(PlayerMessage j){
				try{
					sOutput.writeObject(j);
				}catch(IOException e){
					System.out.println("Error writing to " + username);
					e.printStackTrace();
				}
			}
			// try to close everything
			private void close() {
				// try to close the connection
				try {
					if(sOutput != null) sOutput.close();
				}
				catch(Exception e) {}
				try {
					if(sInput != null) sInput.close();
				}
				catch(Exception e) {};
				try {
					if(socket != null) socket.close();
				}
				catch (Exception e) {}
			}
		}
}
