package com.ccit.server;

public class ClientInfo1 
{
	//0表示发送方1表示接受方
   private int clientType=0; 
   //0表示没有缓存1报头缓存2图片数据缓存
   private DataPack dp=null;
   public ClientInfo1(){}
   public ClientInfo1(int clientType,DataPack dp)
   {
	   this.clientType=clientType;
	   this.dp=dp;
   }
	public int getClientType() {
		return clientType;
	}
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}
	public DataPack getDp() {
		return dp;
	}
	public void setDp(DataPack dp) {
		this.dp = dp;
	}
   
}
