package com.fred.steve.Maze;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayerMessage implements Serializable{
	/**
	 * Unique ID so that we can sent over the internet
	 */
	private static final long serialVersionUID = 2171351081370949192L;
	public static enum TYPE{DISCONNECT, SEND_UPDATE_TO_SERVER,SEND_UPDATE_TO_PLAYER, USERNAME_TAKEN, CONNECT, TOO_MANY_PLAYERS, STOP_SERVER}
	public TYPE myType;
	public int myX, myY;
	public String username;
	
	public ArrayList<PlayerData> myData;
	
	PlayerMessage(TYPE type){
		myType = type;
	}
	PlayerMessage(TYPE type, ArrayList<PlayerData> data){
		myType = type;myData = data;
	}
	PlayerMessage(TYPE type, String username){
		myType = type;this.username = username;
	}
	PlayerMessage(TYPE type,String username, int newX, int newY){
		myType = type;myX = newX;myY = newY;this.username = username;
	}
	public int getX(){
		return this.myX;
	}
	public int getY(){
		return this.myY;
	}
}
