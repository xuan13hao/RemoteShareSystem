package com.ccit.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ccit.server.AcceptData;
import com.ccit.server.DataPack;

public class RemoteScreenFrame extends JPanel implements Runnable
{

	
	private static final long serialVersionUID = -7396147656796734875L;
	
	private List<DataPack> dataPacks=null;
	private BufferedImage image;

	//定义检测SocketChannel的Selector对象
	private Selector selector = null;
	//定义处理编码和解码的字符集
	private Charset charset = Charset.forName("UTF-8");
	//客户端SocketChannel
	private SocketChannel sc = null;
	

	private Client frame;
	private JScrollPane panel;

	public RemoteScreenFrame(Client frame, JScrollPane scrollPane) {
		super();
	
		this.frame = frame;
		this.panel = scrollPane;
		try {
			image=ImageIO.read(this.getClass().getResourceAsStream("/res/ccit.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public void initLisener()
    {
       
    }
	public void init() throws IOException
	{
		selector = Selector.open();
		//加载资源文件conf.properties
		ResourceBundle rb=ResourceBundle.getBundle("conf");
		String hostName=rb.getString("hostName");
		int port=Integer.parseInt(rb.getString("port"));
		InetSocketAddress isa = new InetSocketAddress(hostName,port);
		//调用open静态方法创建连接到指定主机的SocketChannel
		sc = SocketChannel.open(isa);
		//设置该sc以非阻塞方式工作
		sc.configureBlocking(false);
		//将SocketChannel对象注册到指定Selector
		sc.register(selector, SelectionKey.OP_READ);
		//启动读取服务器端数据的线程
		//new ClientThread().start();
		
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// 执行父类的绘制方法
		int width=this.getWidth();
		int height=this.getHeight();
		int iwidth=image.getWidth();
		int iheight=image.getHeight();
		g.drawImage(image, (width-iwidth)/2, (height/iheight)/2, this);	// 将获取的屏幕图像绘制到组件上
		frame.repaint();
	}

	@Override
	public  void run() 
	{
		
		try {
			init();
			while (selector.select() > 0) 
			{
				//遍历每个有可用IO操作Channel对应的SelectionKey
				for (SelectionKey sk : selector.selectedKeys())
				{
					//删除正在处理的SelectionKey
					selector.selectedKeys().remove(sk);
					//如果该SelectionKey对应的Channel中有可读的数据
					if (sk.isReadable())
					{
						//使用NIO读取Channel中的数据
						SocketChannel sc = (SocketChannel)sk.channel();
						
						if(sc.isOpen())
						{
							try
							{   
								AcceptData acceptData=new AcceptData();
								dataPacks=acceptData.init(selector, sk, sc);
							}
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
							readDataPacks(sc);
	
						}

					}
				}
			}
		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "服务器没有开启，请联管理员，并重新启动");
			frame.dispose();
			e.printStackTrace();
		}
	
}	

	 public  void readDataPacks(SocketChannel sc)
	    {
	    	if(null!=dataPacks)
	    	{
	    		for(DataPack dp:dataPacks)
	    		{
			    	if(dp.getProtocl().equals(DataPack.PRO.get("SCREENDATA")))
			        {
			        	
			        	ByteArrayInputStream bin = new ByteArrayInputStream(dp.getData());
						
							try {
								image=ImageIO.read(bin);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (!frame.isShowing()|| frame.getExtendedState() == JFrame.ICONIFIED)
								continue;
							Dimension preferredSize = new Dimension(image.getWidth(), image.getHeight());		// 根据图片大小设置组件大小
						   setPreferredSize(preferredSize);
							revalidate();
							repaint();
					
			        }
			       
	    		}
	    	}
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
				ImageIO.write(image,"gif",btout);
				data = btout.toByteArray();
				btout.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
}
