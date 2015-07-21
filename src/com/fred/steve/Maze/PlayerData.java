package com.fred.steve.Maze;

public class PlayerData {
	String username;
	int x = 0, y = 0;
	PlayerData(String username){
		this.username = username;
	}
	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}
	public String getUsername(){
		return this.username;
	}
	public void setX(int x){
		 this.x = x;
	}
	public void setY(int y){
		this.y = y;
	}
}
