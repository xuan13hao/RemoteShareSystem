package com.ccit.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.ccit.util.ByteIntSwitch;

public class Server1
{
	private static final String CAPTUREPRO="##CCFF$$TTAA##";
    private byte []catchremain=null;
  //  public static ByteBuffer buff = ByteBuffer.allocate(1024*10);  //缓冲验证信息
	private Selector selector = null; //用于检测所有Channel状态的Selector
	private Charset charset = Charset.forName("UTF-8");	//定义实现编码、解码的字符集对象
	ServerSocketChannel server=null;
	InetSocketAddress isa=null;
	
    public void init()throws IOException
    {
		selector = Selector.open();
	    server = ServerSocketChannel.open();                //通过open方法来打开一个未绑定的ServerSocketChannel实例
		isa = new InetSocketAddress("localhost", 9999); 
		server.socket().bind(isa);                          //将该ServerSocketChannel绑定到指定IP地址
		server.configureBlocking(false);            	    //设置ServerSocket以非阻塞方式工作
		server.register(selector, SelectionKey.OP_ACCEPT);  //将server注册到指定Selector对象
		while (selector.select() > 0) 
		{
			//从selector上的已选择Key集中删除正在处理的SelectionKey
			for (SelectionKey sk : selector.selectedKeys())
			{
				selector.selectedKeys().remove(sk); //从selector上的已选择Key集中删除正在处理的SelectionKey
				
				acceptAble(sk);       //接受客户的请求
			
			    readAble(sk);        	//如果sk对应的通道有数据需要读取
			}
		}
    }
    
    /**
     * 读取客户发来的数据
     * @param sk
     * @throws IOException
     */
    public void readAble(SelectionKey sk) throws IOException
    {
    	//如果sk对应的通道有数据需要读取
		if (sk.isReadable())
		{
			//获取该SelectionKey对应的Channel，该Channel中有可读的数据
			SocketChannel sc = (SocketChannel)sk.channel();
			//定义准备执行读取数据的ByteBuffer
			ByteBuffer buff = ByteBuffer.allocate(512);
			
			//开始读取数据
			try
			{   
				byte catchbuff[]=null;
				
				while(sc.read(buff) > 0)
				{
                      buff.flip();
                      catchbuff=new byte[buff.limit()];
                     buff.get(catchbuff);
                     
                /*      while(buff.remaining()>0)
                      {
                        byte data[]=PackData.getPack(sc, buff, 8);
                        if(null!=data)
                        	System.out.println(new String(data));
                      }*/
                      
                      buff.clear();
                      readData(catchbuff,sc);
				}//end while
				//打印从该sk对应的Channel里读取到的数据
				
				//将sk对应的Channel设置成准备下一次读取
				sk.interestOps(SelectionKey.OP_READ);
			}//end try
			//如果捕捉到该sk对应的Channel出现了异常，即表明该Channel
			//对应的Client出现了问题，所以从Selector中取消sk的注册
			catch (IOException ex)
			{
				//从Selector中删除指定的SelectionKey
				sk.cancel();
				if (sk.channel() != null)
				{
					sk.channel().close();
				}
			}

		}
    }
    /**
     * 读取数据
     * @param catchbuff
     * @param sc
     * @throws IOException 
     */
  public void readData(byte[] catchbuff,SocketChannel sc) throws IOException
    {
   
    		//要读报头
    		byte[]header=readHeader(catchbuff,sc);
        	//报头读完后
        	if(parseHeader(header))
        	{
        	   readPicData(catchbuff,sc,parseHeaderToInt(header));
        	}else
        	{
        		
        		System.out.println("报头不合法......");
        	}
    		//System.out.println(new String(header));
 }
  public void readPicData(byte[] catchbuff,SocketChannel sc,int picdatalen) throws IOException
  {
	  ByteBuffer buff=ByteBuffer.allocate(1024*20);
	  byte[]picdata=new byte[picdatalen];
	  int offset=0;
	  while(picdatalen>offset)
	  {
		  if(null!=catchbuff&&catchbuff.length>0)
		  {
			  //有缓存
			  if(catchbuff.length>picdatalen)
			  {
				System.arraycopy(catchbuff, 0, picdata, 0, picdatalen);
				offset=picdatalen;
				//继续缓存
				byte []lesscatch=new byte[catchbuff.length-picdatalen];
				System.arraycopy(catchbuff, picdatalen, lesscatch, 0, catchbuff.length-picdatalen);
				catchbuff=lesscatch;
				readData(catchbuff,sc);
				
			  }else if(catchbuff.length==picdatalen)
			  {
				  System.arraycopy(catchbuff, 0, picdata, 0, picdatalen);
				  offset=picdatalen;
				  catchbuff=null;
				  break;
			  }else
			  {
				  while(sc.read(buff)>0)
				  {
					  buff.flip();
					  if(catchbuff.length+buff.limit()>picdatalen)
					  {
						  //先将缓存中的内容放入图片
						  System.arraycopy(catchbuff, 0, picdata, 0, catchbuff.length);
						  offset+= catchbuff.length;
						 
						 //将流中读的数据放入一个新数组
						  byte newcatch[]=new byte[buff.limit()];

						  buff.get(newcatch);
						  
						  System.arraycopy(newcatch, 0, picdata, offset, picdatalen-catchbuff.length);
						  
						  byte []newlimi=new byte[newcatch.length-picdatalen-catchbuff.length];
						 
						  System.arraycopy(newcatch, picdatalen-catchbuff.length, newlimi, 0, newlimi.length);
						 
						  catchbuff=newlimi;
						  readData(catchbuff,sc);

					  }else if(catchbuff.length+buff.limit()==picdatalen)
					  {
						//先将缓存中的内容放入图片
						  System.arraycopy(catchbuff, 0, picdata, 0, catchbuff.length);
						  offset+= catchbuff.length;
						 
						 //将流中读的数据放入一个新数组
						  byte newcatch[]=new byte[buff.limit()];
						  buff.get(newcatch); 
						  System.arraycopy(newcatch, 0, picdata, offset, picdatalen-catchbuff.length);
						  catchbuff=null;
						  readData(catchbuff,sc);
					  }else
					  {
						byte []newca=new byte[buff.limit()];
						buff.get(newca);
						byte [] newcatchbuff=new byte[newca.length+catchbuff.length];
						System.arraycopy(catchbuff, 0, newcatchbuff, 0, catchbuff.length);
						System.arraycopy(newca, 0, newcatchbuff, catchbuff.length, newcatchbuff.length);
						catchbuff=newcatchbuff;
					  }
						 
				
				  }
			  }
				  
			  
		  }else
		  {
			  //没有缓存
			   while(sc.read(buff)>0)
			   {
				   buff.flip();
				   if(buff.limit()>picdatalen)
				   {
					     byte []limitt=new byte[buff.limit()];
					     buff.get(limitt);
					     System.arraycopy(limitt, 0, picdata, 0, picdatalen);      
						offset=picdatalen;
						
						//继续缓存
						byte []lesscatch=new byte[limitt.length-picdatalen];
						System.arraycopy(limitt, picdatalen, lesscatch, 0,lesscatch.length);
						catchbuff=lesscatch;
						readData(catchbuff,sc);
					   
				   }else if(buff.limit()==picdatalen)
				   {

					     buff.get(picdata);
					        
						 offset=picdatalen;

						catchbuff=null;
						readData(catchbuff,sc);
				   }else
				   {
					   byte []lesss=new byte[buff.limit()];
				       buff.get(lesss);;
				       catchbuff=lesss;
				       break;
				   }
			   }
		  }
		  
		  
	  }
	 
	  
	  
	  
  }
  
 public byte[] readHeader(byte[] catchbuff,SocketChannel sc) throws IOException
  {
	    
	    byte header[]=new byte[8];
	    ByteBuffer buff=ByteBuffer.allocate(512);
		//是否有缓存
	  	if(null!=catchbuff&&catchbuff.length>0)
	  	{
	  		//从缓存中读报头
	  		if(catchbuff.length>8)
	  		{
	  			System.arraycopy(catchbuff, 0, header, 0, 8);
	  			//继续缓存
	  			byte tem[]=new byte[catchbuff.length-8];
	  			System.arraycopy(catchbuff, 8, tem, 0, tem.length);
	  			catchbuff=tem;
	  			//读到了
	  		}else if(catchbuff.length==8)
	  		{
	  			System.arraycopy(catchbuff, 0, header, 0, 8);
	  			//清除缓存
	  			catchbuff=null;
	  		}else
	  		{  
	  			//有缓存但小于报头
		  		while(sc.read(buff)>0)
		  		{
		  			buff.flip();
		  			if(buff.limit()>0)
		  			{
                      
				  			  if(catchbuff.length+buff.limit()>8)
				  			  {
				  				  //读报头并缓存
				  				  byte tem[]=new byte[buff.limit()];
				  				  buff.get(tem);
					  			  System.arraycopy(catchbuff, 0, header, 0, catchbuff.length);
		                          //组装进header 
					  			  System.arraycopy(tem, 0, header,catchbuff.length,8-catchbuff.length);
					  			  //剩作数据
		                          byte[]newcath=new byte[catchbuff.length+tem.length-8];
		                          
		                          System.arraycopy(tem, 8-catchbuff.length, newcath, 0, newcath.length);
		                          break;
		                          //读到了
				  			  }else if(catchbuff.length+buff.limit()==8)
				  			  {
				  				  
				  				  byte tem[]=new byte[buff.limit()];
				  				  buff.get(tem);
					  			  System.arraycopy(catchbuff, 0, header, 0, catchbuff.length);
		                         //组装进header 
					  			  System.arraycopy(tem, 0, header,catchbuff.length,8-catchbuff.length);
					  			  catchbuff=null;
					  			  break;
					  			  //读到了  
				  			  }else
				  			  {
				  				  byte tem[]=new byte[buff.limit()];
				  				  buff.get(tem);
				  				  byte[]newcatch=new byte[tem.length+catchbuff.length];
				  				  System.arraycopy(catchbuff, 0, newcatch, 0, catchbuff.length);
				  				  System.arraycopy(tem, 0, newcatch,catchbuff.length,tem.length);
				  				 //没有读到，让while继续读
				  				  
				  			  }
		  			}//end if 
		  		}//end while
	  			
	  		}
	  		
	  	}else if(buff.limit()>0&&null==catchbuff)
	  	{
	  		//没有缓存的情况
	  		if(sc.read(buff)>0)
	  		{
	  			buff.flip();
	  			if(buff.limit()>0)
	  			{
	  				catchbuff=new byte[buff.limit()];
	  				buff.get(catchbuff);
	  				buff.clear();
	  				readHeader(catchbuff,sc);
	  			}
	  		}
 			 //没有缓存的情况
	  		
	  	}
	return header; 
  }
  public boolean parseHeader(byte[]header)
  {
	   //分析报头
  	   if(parseHeaderToString(header).startsWith("~~^^"))
		{
			
			return true;
		}else
		{
			return false;
		}
  }
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
  public String parseHeaderToString(byte[]header)
  {
	  String re="";
		 if(null!=header&&header.length==8)
		 {
			 byte[] tem=new byte[4];
			 System.arraycopy(header, 0, tem, 0, 4);
			try {
				re= new String(tem,"ISO-8859-1");
				System.out.println("报头:"+re);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 } 
		 return re;
  }
    /**
     * 处理客户的请求
     * @param sk
     * @throws IOException
     */
    public void acceptAble(SelectionKey sk) throws IOException
    {
    	//如果sk对应的通道包含客户端的连接请求
		if (sk.isAcceptable())
		{
			//调用accept方法接受连接，产生服务器端对应的SocketChannel
			SocketChannel sc = server.accept();
		  
			//设置采用非阻塞模式
			sc.configureBlocking(false);
			//将该SocketChannel也注册到selector
			sc.register(selector, SelectionKey.OP_READ);
			//将sk对应的Channel设置成准备接受其他请求
			sk.interestOps(SelectionKey.OP_ACCEPT);
		}
    }
    public void back()
    {
/*    	
		String content = "";
		byte header[]=new byte[8];
		buff.flip();
		if(null==catchbuff&&buff.limit()<8)
		{
			//不够报头缓存
			if(buff.limit()>0)
			{
					catchbuff=new byte[buff.limit()];
					buff.get(catchbuff);
			}
			continue; //继续读
		}
		if(null!=catchbuff&&catchbuff.length+buff.limit()<8)
		{  
			if(buff.limit()>0)
			{
				byte [] secondcatch=new byte[buff.limit()];
				buff.get(secondcatch);
				byte[] newcatch=new byte[catchbuff.length+secondcatch.length];
				System.arraycopy(catchbuff, 0, newcatch, 0, catchbuff.length);
				System.arraycopy(secondcatch, 0, newcatch, 0, secondcatch.length);
			    catchbuff=newcatch;   
			}
			//合并缓存
			continue;
		}
		if(null!=catchbuff&&catchbuff.length+buff.limit()>=8)
		{
			if(catchbuff.length>8)
			{
				System.arraycopy(catchbuff, 0, header, 0, 8);
			}else{
			
			//读报头
			System.arraycopy(catchbuff, 0, header, 0, catchbuff.length);
			buff.get(header, catchbuff.length,8- catchbuff.length);
			catchbuff=null;	
			}
		}
		if(null==catchbuff&&buff.limit()>=8)
		{
			//正常读报头
			buff.get(header, 0, 8);
		}
		//解析报头
		content+=new String(header,"ISO-8859-1");
		if(content.startsWith("~~^^"))
		{
			byte toint[]=new byte[4];
			System.arraycopy(header, 4, toint, 0, 4);
			int picdatalen=ByteIntSwitch.toInt(toint);
			System.out.println("-------报头中数据长---"+picdatalen);
			//读指定图片长度
			int offset=0;
			byte[]picdata=new byte[picdatalen];
			if(null!=catchbuff&&catchbuff.length>8)
			{
				System.arraycopy(catchbuff, 8, picdata, 0, catchbuff.length-8);
			    offset+= catchbuff.length-8;
			    catchbuff=null;
			}
			int remain=buff.remaining();
			 byte[]lastdata=null;
			if(remain>0)
			{
		      lastdata=new byte[remain];
		       buff.get(lastdata);
			}
			while(picdatalen>offset&&sc.read(buff)>0)
			{
					
					if(remain>0)
					{
						if(remain==picdatalen)
						{
							System.arraycopy(lastdata, 0, picdata, 0, picdatalen);
						  // buff.get(picdata, 0, picdatalen);
						   offset=picdatalen;
						   break;
						}
						
                        	除非缓存比图片还要大								
                        if(remain>picdatalen)
						{
							   buff.get(picdata, 0, picdatalen);
							   offset=picdatalen;
							   nextpack=new next
							   
							   break;
						}
						//有问题
						
						if(remain<picdatalen)
						{
							System.arraycopy(lastdata, 0, picdata, 0, lastdata.length);
							//buff.get(picdata,offset,remain);
							offset+=lastdata.length;
							continue;
						}
					}
			}
			
			
		}//end if
		
		
		//读取报头
		//content += charset.decode(buff);
		*/
    }
    public static void main(String[] args) throws IOException
	{
	   new Server1().init();

	}

}
