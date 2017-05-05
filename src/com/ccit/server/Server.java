package com.ccit.server;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import com.ccit.util.ByteIntSwitch;

public class Server 
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
    	if (sk.isReadable())
		{
			 SocketChannel sc = (SocketChannel)sk.channel();      //获取该SelectionKey对应的Channel，该Channel中有可读的数据
			 ByteBuffer buff = ByteBuffer.allocate(1024*20);       //定义准备执行读取数据的ByteBuffer
		     
			 byte []remainbuff=null;//读完数据，还有剩余
			
			 while(sc.read(buff)>0)
		     {
				 buff.flip();//此时buff的remai为limit
				 
				 
				 
				 
				 //读取报头
				 String content="";
				 int picdatalen=0;
				 byte []head=new byte[8];
				 //没有缓存，且够报头长度
				 if(buff.limit()>=8&&null==remainbuff)
				 {
					 //buff够一个报头时
					 buff.get(head, 0, 8);//此是position在８位置
					 content+=new String(head,"ISO-8859-1");
				 }
                 //有缓存,缓存长度够报头
				 if(null!=remainbuff&&remainbuff.length>=8)
				 {
					 buff.get(remainbuff, 0, 8);//此是position在８位置
					 content+=new String(head,"ISO-8859-1");
					 if(remainbuff.length==8)remainbuff=null;
					 if(remainbuff.length>8)
					 {
					 byte[] lastbuff=new byte[remainbuff.length-8];
					 System.arraycopy(remainbuff, 8, lastbuff, 0, remainbuff.length-8);
					 remainbuff=lastbuff;
					 }
				 }
				 //有缓存，且长度不够报头
                 if(null!=remainbuff&&remainbuff.length<8)
				 {
					 System.arraycopy(remainbuff, 0, head, 0, remainbuff.length);
					 buff.get(head, remainbuff.length, 8-remainbuff.length);
					 remainbuff=null;
				 }
				 
				 //分析报头
				 if(content.startsWith("~~^^"))
				 {
					//是我们的屏幕数据 
					 byte []piclen=new byte[4];
					 System.arraycopy(head, 4, piclen, 0, 4);
					 picdatalen=ByteIntSwitch.toInt(piclen);
					 System.out.println("------屏幕数据大小----"+picdatalen);
					
					 //从缓存在读取指定长度的图片数据
					 byte []picdata=new byte[picdatalen];
					 int remain=buff.remaining();
					 int offset=0;
					 if(null!=remainbuff&&remainbuff.length<picdatalen)
					 {
						 System.arraycopy(remainbuff, 0, picdatalen, 0, remainbuff.length);
						 offset+=remainbuff.length;
						 remainbuff=null;
					 }
					 if(null!=remainbuff&&remainbuff.length==picdatalen)
					 {
						 System.arraycopy(remainbuff, 0, picdatalen, 0,picdatalen);
						 offset=picdatalen;
						 remainbuff=null;
					 }
					 if(null!=remainbuff&&remainbuff.length>picdatalen)
					 {
						 System.out.println("---------不可能吧-------------");
					 }
					 //缓存足够
					 if(remain>=picdatalen)
					 {
						 buff.get(picdata,0,picdatalen);
						 //剩余的数据缓存起来
						 remainbuff=new byte[remain-picdatalen];
						 buff.get(remainbuff,0,remain-picdatalen);
						 offset=picdatalen;
						// continue; 
					 }else if(remain>0)
					 {
					   //缓存不足图片数据
					   buff.get(picdata,0,remain);
					   offset+=remain;
					   buff.clear();
					 }
					 
					 while(picdatalen>offset&&sc.read(buff)>0)
					 {
						buff.flip();
						if(buff.limit()==picdatalen-offset)
						{
						  buff.get(picdata,offset,picdatalen-offset);
						  offset=picdatalen;
						}else if( buff.limit()>picdatalen-offset)
						{
							buff.get(picdata,offset,picdatalen-offset);
							offset=picdatalen;
							int rema=buff.remaining();
							//缓存起来
							remainbuff=new byte[rema];
						    buff.get(remainbuff,0,rema);
						}else
						{
							buff.get(picdata,offset,buff.limit());
							offset+=buff.limit();
							
						}
						
					 }//end while while(picdatalen>offset&&sc.read(buff)>0)
					 System.out.println("---------"+offset+"---"+picdatalen+"---------------");
				 }//end if  if(content.startsWith("~~^^"))
				
		    	 
		     }//end  while(sc.read(buff)>0)
			
		
		}
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

    
    public void readback()
    {
    	//开始读取数据
	       
		 /*   String content="";
		    int len=0;
	        while((len=sc.read(buff))>0)
	        {
	        	buff.flip();
	            if(len>=8)
	            {
	            	
	            	int offset=0;
	            	byte []picdata=null;
	            	int picdatalen=0;
		            	byte []header=new byte[8];
		            	buff.get(header,0,8);
		                if(null!=header)
			            {
			            content+=new String(header,"ISO-8859-1");
			            System.out.println("报头："+content);
			            }
		                if(content.startsWith("~~^^"))
		                {
				              //接受的是屏幕数据长度 
				            	byte piclen[]=new byte[4];
				            	System.arraycopy(header,4, piclen, 0, 4);
				            	picdatalen=ByteIntSwitch.toInt(piclen);
				            	System.out.println("数据长度为："+picdatalen);
				            	int remain=buff.remaining();
				            	//int offset=0;
				            	picdata=new byte[picdatalen];
				            	
				            	if(remain>=picdatalen)
					        	   {
					        		   buff.get(picdata,0,picdatalen);
					        		   offset=picdatalen;
					        	   }else if(remain>0)
					        	   {
						        		   buff.get(picdata,offset,remain);//读第一次buff剩作的内容
						        		   offset+=remain;
						        		   buff.clear();
						        		   while(offset<picdatalen&&sc.read(buff)>0)
						        		   {
						        			    buff.flip();
						        			    //如果缓存在够图片数据
						        			    if(buff.limit()>=picdatalen-offset)
						        			    {
						        			    	buff.get(picdata,offset,picdatalen-offset);
						        			    	offset=picdatalen;
						        			    }else
						        			    {
						        			    	buff.get(picdata,offset,buff.limit());
						        			    	offset+=buff.limit();
						        			    	buff.clear();
						        			    }
						        			    
						        		   }
					        		 
				        		   
				        	          }else if(remain==0)
				        	          {
				        	        	  while(offset<picdatalen&&sc.read(buff)>0)
						        		   {
						        			    buff.flip();
						        			    //如果缓存在够图片数据
						        			    if(buff.limit()>=picdatalen-offset)
						        			    {
						        			    	buff.get(picdata,offset,picdatalen-offset);
						        			    	offset=picdatalen;
						        			    }else
						        			    {
						        			    	buff.get(picdata,offset,buff.limit());
						        			    	offset+=buff.limit();
						        			    	buff.clear();
						        			    }
						        			    
						        		   }
				        	          }
				            	
		                }
		                System.out.println("--------"+picdatalen+"----------"+offset+"---------------------");
	            	
	            }else
	            {
	            	
	            }
	        
	        	
	        	
	        	sk.interestOps(SelectionKey.OP_READ);
	        }	*/
	//如果捕捉到该sk对应的Channel出现了异常，即表明该Channel
	//对应的Client出现了问题，所以从Selector中取消sk的注册
    }
	public static void main(String[] args) throws IOException
	{
	   new Server().init();

	}
	
}
