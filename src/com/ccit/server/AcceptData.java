package com.ccit.server;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class AcceptData
{
    private List<DataPack> datapacks=null;
	PrintStream out=null;
	public static int counter=0;
	protected ClientInfo1 ci=null;
	protected DataPack dp=null;
	protected ByteBuffer buff=ByteBuffer.allocate(1024*100);
	protected Selector selector;
	protected SelectionKey sk ;
	protected SocketChannel sc;
    public AcceptData(){}
    public AcceptData( Selector selector,SelectionKey sk ,SocketChannel sc) throws IOException
    {
    	this.selector=selector;
    	this.sk=sk;
    	this.sc=sc;
    	//init(selector,sk,sc);
    }

	public List<DataPack> init( Selector selector,SelectionKey sk ,SocketChannel sc) throws IOException
	{
		datapacks=new ArrayList<DataPack>();
		out=System.out;//new PrintStream("c:\\tt\\log.log");
		if(null==sk.attachment())
		{
			ci=new ClientInfo1();//初始客户信息对象
		}else
		{
			ci=(ClientInfo1)sk.attachment();
		}
		
		dp=ci.getDp();
	
		while(sc.read(buff)>0)
		{
			
			buff.flip();
			if(null==dp)dp=new DataPack();
            if(null!=dp.getHeader()&&dp.getHeader().length==DataPack.HEADLEN)
            {
            	 readData(buff);
            }else
            {
            	readHeader(buff);
            }
			
			
			
		}//end while
		
		if(null!=dp)
		{
			ci.setDp(dp);
			/*
			if(null!=dp.getHeader())
				out.println("缓存header:XXX"+dp.getHeader().length);
			if(null!=dp.getData())
				out.println("缓存Data:XXX"+dp.getData().length);
		    */
		}
		
		if(null!=ci)sk.attach(ci);
		//将sk对应的Channel设置成准备下一次读取
		sk.interestOps(SelectionKey.OP_READ);
	
		return datapacks;
	}
	
	//读报头
	public void readHeader(ByteBuffer buff)
	{
		//out.println("readheader--------------------------");
		if(buff.remaining()>0)
		{
			//out.println("ReadHeader------remai="+buff.remaining()+"--------------");
			//header中为0时
		 if(null==dp.getHeader())
		  {
			   // out.println("ReadHeader----null==getHeader---------");
				if(buff.remaining()>=DataPack.HEADLEN)
				{
					//out.println("Readhead---"+buff.remaining()+"-----------");
					//buff中大于报头
					byte[]head=new byte[DataPack.HEADLEN];
					buff.get(head,0,DataPack.HEADLEN);
					dp.setHeader(head);
					testHead();
					if(buff.remaining()>0)readData(buff);
				}else
				{
					//out.println("ReadHeader--buff.remaining()<DataPack.HEADLEN---");
					//缓存起来报头
					   int remain=buff.remaining();
					   byte[]tem=new byte[remain];
					   buff.get(tem);
					   dp.setHeader(tem);
	   
				}
		  }else if(dp.getHeader().length>0&&dp.getHeader().length<DataPack.HEADLEN)
		  {
			 // out.println("Readheader----header.len>0-------------------------");
			 //header中有缓存时
			   if(dp.getHeader().length+buff.remaining()>=DataPack.HEADLEN)
			   {
				  // out.println("Readheader----remain+header.len>8-------------------------");
				   byte[]head=new byte[DataPack.HEADLEN];
				   byte[]tem=new byte[DataPack.HEADLEN-dp.getHeader().length];
				   buff.get(tem,0,tem.length);
				  
				   System.arraycopy(dp.getHeader(), 0, head, 0, dp.getHeader().length);
				   System.arraycopy(tem, 0, head,dp.getHeader().length, tem.length);
				   
				   dp.setHeader(head);
				
				   testHead();

				   if(buff.remaining()>0)readData(buff);
			   }else
			   {
				  // out.println("Readheader----remain+header.len<8-------------------------");  
				    int remain=buff.remaining();
				    byte[]tem=new byte[remain];
				    buff.get(tem);
				    byte[]newtem=new byte[remain+dp.getHeader().length];
				    System.arraycopy(dp.getHeader(), 0, newtem, 0,dp.getHeader().length);
				    System.arraycopy(tem, 0, newtem,dp.getHeader().length, tem.length);
				    dp.setHeader(newtem);    
			   }
		  }else
		  {
			 // out.println("报头中有缓存和报头同样长度的数据。。。。。。");
		  }
		}//end if remain>0
	}
	
	private void testHead() 
	{
		// TODO Auto-generated method stub
		if(dp.checkHeader())
		{
		  dp.initDatalen();
		}else
		{
			out.println("非屏幕数据------------------");
		}
	}
	//读数据
	public void readData(ByteBuffer buff)
	{
		//out.println("ReadData----------------------");
		if(buff.remaining()>0)
		{
			//out.println("ReadData-remain>0-remain="+buff.remaining()+"--------------------");
			dp.initDatalen();
			if(null==dp.getData()||dp.getData().length==0)
			{	
				//out.println("ReadData-remain>0-remain="+buff.remaining()+"--------------------");
			    if(buff.remaining()>=dp.getDatalen())
			    {
			    	  byte[] data=new byte[dp.getDatalen()];
			    	  buff.get(data, 0, dp.getDatalen());
			          dp.setData(data);
			    	  //读完一次数据
			    	 // out.println("读完一次数据-------------");
			    	  processData();
			    	  if(buff.remaining()>0)readHeader(buff);
			    }else 
			    {
			    	
			    	//缓存
			  	   int remain=buff.remaining();
				   byte []tem=new byte[remain];
			  	   buff.get(tem);
			  	   dp.setData(tem);  
			    }
			}else if(dp.getData().length>0&&dp.getData().length<dp.getDatalen())
			{
				if(buff.remaining()+dp.getData().length>=dp.getDatalen())
				{
					//out.println("---之前的remain="+buff.remaining()+"=====++数据长为---"+dp.getDatalen()+"++++datalen:"+dp.getData().length);
					byte data[]=new byte[dp.getDatalen()];
			        
					System.arraycopy(dp.getData(), 0, data, 0,dp.getData().length);
					
					buff.get(data,dp.getData().length,dp.getDatalen()-dp.getData().length);
					dp.setData(data);
					//out.println("读完一次数据++++++++++++buff.remain="+buff.remaining());
					processData();
			    	if(buff.remaining()>0)readHeader(buff);
				}else
				{
					//继续缓存
					int remain=buff.remaining();
					//buff.get(dp.getData(), dp.getDataOffset(), remain);
					byte []rema=new byte[remain];
					buff.get(rema);
				    byte []data=new byte[remain+dp.getData().length];
					System.arraycopy(dp.getData(), 0, data, 0,dp.getData().length);
				    System.arraycopy(rema, 0, data,dp.getData().length, remain);
					dp.setData(data);
				}
			}
		}
	 
	}

	public void processData()
	{
		//处理数据的模块
       /* */
		
		datapacks.add(dp);
		dp=new DataPack();
        
	}

}

