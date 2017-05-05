package com.ccit.util;

public class Mousexy 
{
  private int x;
  private int y;
  public Mousexy(int x,int y)
  {
	  this.x=x;
	  this.y=y;
  }
  public Mousexy(){}
public int getX() {
	return x;
}
public synchronized void setX(int x) {
	this.x = x;
}
public int getY() {
	return y;
}
public synchronized void setY(int y) {
	this.y = y;
}
  
}
