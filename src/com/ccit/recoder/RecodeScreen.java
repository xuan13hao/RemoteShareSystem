package com.ccit.recoder;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.ccit.util.ByteIntSwitch;
import com.ccit.util.MouseHook;
import com.ccit.util.Mousexy;
public class RecodeScreen extends Thread 
{

    private Mousexy xy=new Mousexy();  
    private Dimension dimension ;     //桌面区域
    private Rectangle rectangel;      //截图矩形大小
    private Robot robot ;             //截图机器人
    private Image icon;               //鼠标
	private Selector selector = null;  //定义检测SocketChannel的Selector对象
	private SocketChannel sc = null; //客户端SocketChannel
	private JFrame jf=null;
    private String hostName=null;
    private Integer port=9999;
	public RecodeScreen(JFrame jf)
	{
		this.jf=jf;
	}
	public void init()
	{
		//启动一个线程来计算光标的坐标
		MouseHook mouseHook=new MouseHook(xy); 
		mouseHook.start();
		
		try {
			selector = Selector.open();
			//加载资源文件conf.properties
			ResourceBundle rb=ResourceBundle.getBundle("conf");
			hostName=rb.getString("hostName");
			port=Integer.parseInt(rb.getString("port"));
			InetSocketAddress isa = new InetSocketAddress(hostName,port);
			sc = SocketChannel.open(isa);//调用open静态方法创建连接到指定主机的SocketChannel	
			sc.configureBlocking(false);//设置该sc以非阻塞方式工作
			sc.register(selector, SelectionKey.OP_READ);//将SocketChannel对象注册到指定Selector
			
			icon=ImageIO.read(this.getClass().getResourceAsStream("/res/cursor1.png"));
			dimension = Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕大小
			rectangel=new Rectangle(0,0,(int)dimension.getWidth(),(int)dimension.getHeight());
			robot = new Robot();

		} catch (IOException e) {
			System.out.println("初始化时发生IO异常");
			e.printStackTrace();
		} catch (AWTException e) {
			System.out.println("生成机器人出错");
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void run() 
	{
	
	     init();
	     while(true)
	     {
	    	
	    	 try {
	         if(sc.isOpen())
	         {
	        	 //图片数据
	        	 byte []sendimage=captureScreenToByte();
		    	 
		    	
		    	 byte []header=new byte[8]; //定义报头
		    	 //组装报头
				 System.arraycopy("~~^^".getBytes("ISO-8859-1"), 0, header, 0, 4);
				 System.arraycopy(ByteIntSwitch.toByteArray(sendimage.length, 4),0,header,4,4);
	
				byte [] pack=new byte[sendimage.length+8];
				System.arraycopy(header,0, pack, 0, 8);
				System.arraycopy(sendimage,0, pack, 8, sendimage.length);
				
	    	    sc.write(ByteBuffer.wrap(pack));
				 //sc.write(ByteBuffer.wrap(header));
	    	   //  sc.write(ByteBuffer.wrap(sendimage));  
	    	 
	         }else
	         {
	        	 return ;
	         }
				Thread.sleep(200);
			}  catch (Exception e) {
			    JOptionPane.showMessageDialog(null, "服务器没有开启，先开启服务器，重新链接");
				System.exit(0);
			    jf.dispose(); 
			    try {
					if(null!=sc)sc.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
	     }	
	}
	
   public  synchronized byte[] captureScreenToByte()
   {
	   //截屏幕图
	   BufferedImage image=robot.createScreenCapture(rectangel);
	     Graphics g=image.getGraphics();
	    g.drawImage(icon, xy.getX(), xy.getY(), null);
	   return getBufferedImageData(image);
   }
	
	/**
	 * 获取BufferedImage的图片字节数据(一般供于传输)
	 * @param image
	 * @return
	 */
	public static synchronized byte[] getBufferedImageData(BufferedImage image){
		byte[] data = null;
		if(image!=null){
			try {
				ByteArrayOutputStream btout = new ByteArrayOutputStream();
				ImageIO.write(image,"jpg",btout);
				data = btout.toByteArray();
				btout.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}
	

}
