package com.ccit.server;

public class ClientInfo 
{
   private int clientType;
   private int catchType;
   private byte[]header;
   private byte[]Catchbuff;
   private int  datalen;
public int getClientType() {
	return clientType;
}
public void setClientType(int clientType) {
	this.clientType = clientType;
}
public int getCatchType() {
	return catchType;
}
public void setCatchType(int catchType) {
	this.catchType = catchType;
}
public byte[] getHeader() {
	return header;
}

public int getDatalen() {
	return datalen;
}
public void setDatalen(int datalen) {
	this.datalen = datalen;
}
public void setHeader(byte[] header) {
	this.header = header;
}
public void setDatalen(byte datalen) {
	this.datalen = datalen;
}
public byte[] getCatchbuff() {
	return Catchbuff;
}
public void setCatchbuff(byte[] catchbuff) {
	Catchbuff = catchbuff;
}
   
   
}
