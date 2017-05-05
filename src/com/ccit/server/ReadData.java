package com.ccit.server;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import com.ccit.util.ByteIntSwitch;

public class ReadData
{
    private ByteBuffer buff=ByteBuffer.allocate(1024*100);
    private ClientInfo ci=null;
    private byte header[]=null;
    private byte data[]=null;
    private int datalen=0;
    
	 public ReadData(SelectionKey sk ,SocketChannel sc) throws IOException
	 {
	    	init(sk,sc);
	 }

	public void init(SelectionKey sk ,SocketChannel sc) throws IOException
	{
		//读入缓存中的数据
		if(null!=sk.attachment())
		{
			ci=(ClientInfo)sk.attachment();
		}
    //------------------不断的读流当中的数据----------------------------
		while(sc.read(buff)>0)
		{
			
			  buff.flip();
			  //没有缓存的情况
              if(null==ci||0==ci.getCatchType())
              {  
            	  //在buff中从报头读
            	  readByteBufferFromHeader();  
              }
              if(null!=ci&&1==ci.getCatchType()&&null==ci.getCatchbuff())
              {
            	  System.out.println("------------00000------------------");
            	  header=ci.getHeader();
            	  datalen=ci.getDatalen();
            	  
            	  //缓存中只缓存了一个报头
            	  readByteBufferFromData();
              }
              //缓存中是报头
              if(null!=ci&&1==ci.getCatchType()&&null!=ci.getCatchbuff()&&ci.getCatchbuff().length>0)
              {
            	  
            		//读取报头，并缓存
            		  if(ci.getCatchbuff().length+buff.remaining()>=8)
            		  {
            			   byte head[]=new byte[8];
            			   System.arraycopy(ci.getCatchbuff(), 0, head, 0, ci.getCatchbuff().length);
            			   buff.get(head,ci.getCatchbuff().length,8-ci.getCatchbuff().length);
            			   ci.setCatchType(0);
             			  ci.setCatchbuff(null);
             			  ci.setDatalen(0);
             			  ci.setHeader(null);
             			   readByteBufferFromData();
            			   
            		  }else
            		  {
            			  marageBuff();   //将现合并缓存
             			 readCatchFromHeader();   
            		  }
            
              }
              //缓存中是数据
              if(null!=ci&&2==ci.getCatchType()&&ci.getCatchbuff().length>0)
              {
            	      
            	    	  if(buff.remaining()+ci.getCatchbuff().length>=ci.getDatalen())
                		  {
                			  byte [] datas=new byte[ci.getDatalen()]; 
                			  System.arraycopy(ci.getCatchbuff(), 0, datas, 0, ci.getCatchbuff().length);
                			  buff.get(datas,ci.getCatchbuff().length,ci.getDatalen()-ci.getCatchbuff().length);
                			  header=ci.getHeader();
                			  //清除缓存
                			  ci.setCatchType(0);
                			  ci.setCatchbuff(null);
                			  ci.setDatalen(0);
                			  ci.setHeader(null);
                			  

                		  }else
                		  {
                			//读取数据，并缓存
               	    	   marageBuff();
               	    	  readCatchFromData(); 
                			  
                		  }
            	     
            	  
            	 
              }
		}//end whil
	//------------------读完了流中的数据----------------------------			  
				  
		if(null!=ci)
		{
			 sk.attach(ci);
		}
		
		//将sk对应的Channel设置成准备下一次读取
		sk.interestOps(SelectionKey.OP_READ);
	}//init方法完
	
	/**
	 * 将报头中的数据长度解析出来
	 * @param header
	 * @return
	 */
	public int parseHeaderToInt(byte[]header)
    {
	
	  	 if(null!=header&&header.length==8)
	  	 {
	  		byte[]tem=new byte[4];
	  		System.arraycopy(header, 4, tem, 0, 4);
	  		return ByteIntSwitch.toInt(tem);
	  	 }else
	  	 {
	  		 return 0;
	  	 }
  	 
    }
		/**
		 * 
		 * 从buff中读报头区
		 * @param buff
		 */
	public void readByteBufferFromHeader()
	    {
		  System.out.println("---从buff从报头读起----------------"+buff.limit());
		  if(buff.remaining()>0)
		  {
			 
			//够一个报头
	   	    if(buff.remaining()>8) 
	   	    {
                readHeader();
	   	    	System.out.println("****报头在解析数据区长度："+datalen);
	   	    	//该读剩下的数据区了
	   	    	readByteBufferFromData();
	 
	   	    }else if(buff.remaining()==8)
	   	    {
	   	    	//缓存中只有报头信息，等待读数据区
	   	        readHeader();
	   	    	if(ci==null)ci=new ClientInfo();
	   	    	ci.setHeader(header);
	   	    	ci.setDatalen(datalen);
	   	    	ci.setCatchType(1);
	   	    	ci.setCatchbuff(null);
	   	    	//从buff中读数据
	   	    	
	   	    	
	   	    }else
	   	    {
	   	    	//剩下的内容缓存起来
	   	    	byte []tem=new byte[buff.remaining()];
	   	    	buff.get(tem);
	   	    	if(null==ci)ci=new ClientInfo();
	   	    	ci.setCatchbuff(tem);
	   	    	ci.setHeader(null);
	   	    	ci.setDatalen(0);
	   	    	ci.setCatchType(1);
	   	    	//从buff中读报头
	   	    }
		  }
	    }


	/**
	    * 从buff中读数据区
	    * 
	    * @param buff
	    */
	    public void readByteBufferFromData()
	    {
	    	if(buff.remaining()>0)
	    	{
			    	if(buff.remaining()>datalen)
			    	{
			    		System.out.println("缴存中的内容大于数据区-------datalen--"+datalen);
			    		//从buff中读到了数据
			    		readPackageFromByteBuff();
			    		//继续从buff中读报头
			    		readByteBufferFromHeader();
			    		
			
			    	}else if(buff.remaining()==datalen)
			    	{
			    		//从buff中读到了数据,则清除缓存
			    		readPackageFromByteBuff();
			    		if(ci!=null)
			    		{
			    			ci.setCatchbuff(null);
			    			ci.setCatchType(0);
			    			ci.setDatalen(0);
			    			ci.setHeader(null);
			    		}
			    	}else
			    	{
			    		System.out.println("读一次buff中吐剩内容）））））））buff.remain--"+buff.remaining());
			    		//放入缓存
			    		byte [] tem=new byte[buff.remaining()];
			    		buff.get(tem);
			    		if(null==ci)ci=new ClientInfo();
			    		ci.setCatchbuff(tem);
			    		ci.setCatchType(2);//表示是数据缓存
			    		ci.setDatalen(datalen);
			    		ci.setHeader(header);
			    		//现在缓存中数据应该从报头读起，但长度示知
			    		//readCatchFromData();
			    		System.out.println("读一次buff中吐剩内容）））））））buff.remain--"+buff.remaining());
			    	}
	    	}
	    }
		private void readPackageFromByteBuff() 
		{
			data=new byte[datalen];
    		//读数据区
    		buff.get(data,0,datalen);
    		//读到了一次完整数据
    		//&&&&&&&&&&&&
    		readedData(data);
	     }

		/**
		 * 从缓存中读报头区
		 */
		private void readCatchFromHeader() 
		{
			if(null!=ci&&ci.getCatchType()==1&&null!=ci.getCatchbuff()&&ci.getCatchbuff().length>0)
			{
					if(ci.getCatchbuff().length>8)
			   		{ 
						
						System.out.println("^^^^^^^^^^^ci.getCatchbuff()="+ci.getCatchbuff().length+"^^^^^^^^^^^^^^^^^^^");
			   			//读报头，并读数据
						System.arraycopy(ci.getCatchbuff(), 0, header, 0, 8);
			   			datalen=parseHeaderToInt(header);
			   			System.out.println("header:-00----"+new String(header,0,4));
			   	    	
			   			//继续缓存
		   				byte tem[]=new byte[ci.getCatchbuff().length-8];
		   				System.arraycopy(ci.getCatchbuff(), 8, tem, 0, tem.length);
		   
		   				//if(null==ci)ci=new ClientInfo();
			    		ci.setCatchbuff(tem);
			    		ci.setCatchType(2);//表示是数据缓存
			    		ci.setHeader(header);
			    		ci.setDatalen(datalen);
			    		
			    		readCatchFromData();

			   		}else if(ci.getCatchbuff().length==8)
			   		{
			   			System.out.println("^^^&&&&&&^^^^^^^^^^^^^^^^");
			   		//读报头，并读数据
			   			header=new byte[8];
			   			System.arraycopy(ci.getCatchbuff(), 0, header, 0, 8);
			   			datalen=parseHeaderToInt(header);
			   			
			   			//if(ci==null)ci=new ClientInfo();
			   	    	ci.setHeader(header);
			   	    	ci.setDatalen(datalen);
			   	    	ci.setCatchType(1);
			   	    	ci.setCatchbuff(null);
			   			
			   			
			   		}
				
			}
			
		}

		/**
		 * 从缓存中读数据区
		 */
		private void readCatchFromData()
		{
			
		    if(null!=ci&&ci.getCatchType()==2&&null!=ci.getCatchbuff()&&ci.getCatchbuff().length>0)
		    {
		    	System.out.println("^^******^^^^^^^^^^^^^^^^^^^^^^");
		 
		    	if(ci.getCatchbuff().length>ci.getDatalen())
		    	{
		    		System.out.println("- 读前：-"+ci.getCatchbuff().length+"-----ci.len="+ci.getDatalen()+"-------------------------------");
		    		
		    	    //从缓存中读到了数据
		    		readPackageFromCatch();
		    		System.out.println("(((((((((ci.getDatalen()"+ci.getDatalen());
		    		//将余下的内容缓存为报头
		    		byte tem[]=new byte[ci.getCatchbuff().length-ci.getDatalen()];
	    			System.arraycopy(ci.getCatchbuff(), ci.getDatalen(), tem, 0, tem.length);
	    			System.out.println("temlllll"+tem.length);
	    			//if(null==ci)ci=new ClientInfo();
	    			ci.setCatchbuff(tem);
	    			ci.setDatalen(0);
	    			ci.setCatchType(1);
	    			ci.setHeader(null);
	    			System.out.println("- 读后：-"+ci.getCatchbuff().length+"-----ci.len="+ci.getDatalen()+"-------------------------------");
	    			//从缓存在读报头
		    		readCatchFromHeader();
           
		    	}else  if(ci.getCatchbuff().length==ci.getDatalen())
		    	{
		    		System.out.print("*");
		    		 //从缓存中读到了数据
		    		readPackageFromCatch();
		    		if(null!=ci)
		    		{
		    			ci.setCatchType(0);
		    			ci.setCatchbuff(null);
		    			ci.setHeader(null);
		    			ci.setDatalen(0);
		    		}
		    		//从余下的内容中读报头
		    		//readByteBufferFromHeader();
		    	}
		    }
		}	
		
   private void readPackageFromCatch() 
   {
	   
	   System.out.println("-------ci-------"+ci);
		//读数据
		data=new byte[ci.getDatalen()];
		System.arraycopy(ci.getCatchbuff(), 0, data, 0, ci.getDatalen());
		header=ci.getHeader();
	 	//读数据完成-------------------------
		//&&&&&&&&&&&&
		//System.out.println(ci.getCatchbuff().length+"LLLLLL");
		readedData(data);
   }

public void marageBuff()
   {
 	     byte[]tem=new byte[buff.remaining()];
		 buff.get(tem);
		 byte[]newtem=new byte[tem.length+ci.getCatchbuff().length];
		 System.arraycopy(ci.getCatchbuff(), 0, newtem, 0, ci.getCatchbuff().length);
		 System.arraycopy(tem, 0, newtem, ci.getCatchbuff().length, tem.length);
		 ci.setCatchbuff(newtem);
		
   }
   private void readHeader() 
   {
	    	header=new byte[8];
	    	buff.get(header,0,8);
	    	datalen=parseHeaderToInt(header);
	    	
  }
   
   public void readedData(byte[]data)
   {
	    
	    if(null!=data&&new String(header,0,4).equals("~~^^"))
	     {
	    	System.out.println("-------"+data.length);
	    	  try {
				    //ByteArrayInputStream bin =new ByteArrayInputStream(data);
					FileOutputStream fo=new FileOutputStream("c:\\tt\\aa"+Math.random()+".gif");
					//ImageIO.write(ImageIO.read(bin), "gif", fo);
					BufferedOutputStream bo=new BufferedOutputStream(fo);
					bo.write(data);
					fo.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
			  data=null;
				 
			}
	     }
	   if(null!=data&&new String(header,0,4).equals("##$$"))
	   {
		   System.out.println("##$$:"+new String(data));
		   data =null;
	   }
	    
   }
}