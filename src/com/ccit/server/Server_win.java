package com.ccit.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
public class Server_win extends JFrame 
{
	private static final long serialVersionUID = 6615143869796102066L;
	private Selector selector = null; //用于检测所有Channel状态的Selector
	ServerSocketChannel server=null;
	InetSocketAddress isa=null;
	AcceptData   acceptData=null;
	private List<DataPack> dataPacks=null;
	private SystemTray systemTray=null;
	private Image icon=null;
	private String hostName=null;
	private Integer port=9999;
	public Server_win()
	{
		super("加中远程桌面共享服务端v1.0");
		this.setSize(300, 60);
		this.setResizable(false);
		this.setLocation(400, 400);
		JLabel jb=new JLabel("服务器已启动...");
		this.add(jb);
		this.setVisible(true);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		 initSystemTray();
	}

    public void init()throws IOException
    {
		selector = Selector.open();
	    server = ServerSocketChannel.open();                //通过open方法来打开一个未绑定的ServerSocketChannel实例
	  //加载资源文件conf.properties
		ResourceBundle rb=ResourceBundle.getBundle("conf");
		hostName=rb.getString("hostName");
		port=Integer.parseInt(rb.getString("port"));
	    isa=new InetSocketAddress(hostName,port);
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
		
			//开始读取数据
			try
			{   
			  acceptData=new AcceptData(selector,sk,sc);
			  dataPacks=acceptData.init(selector,sk,sc);
			 
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
			//处理数据
			readDataPacks(sc);
			

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
		        	//给客户端广播数据
		        	processBroad(sc,dp);
		        }
		        //验证客户端用户名和密码
		        if(dp.getProtocl().equals(DataPack.PRO.get("USERPASSWD")))
		        {
		        	processClient(sc,dp);
		        }
		        //验证发送端是否合法
		        if(dp.getProtocl().equals(DataPack.PRO.get("ISCAPTUR")))
		        {
		        	
		        }
    		}
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
    //------------------------------------------------------
	public void processBroad(SocketChannel sc,DataPack dp)
	{
		 //sk.attach(new ClientInfo1());
		//遍历该selector里注册的所有SelectKey
		for (SelectionKey key :selector.keys())
		{
		    if(null!=key.attachment())continue;	
			//获取该key对应的Channel
			Channel targetChannel = key.channel();
			//如果该channel是SocketChannel对象
			if (targetChannel instanceof SocketChannel)
			{
				//将读到的内容写入该Channel中
				SocketChannel dest = (SocketChannel)targetChannel;
				byte []datapack=new byte[DataPack.HEADLEN+dp.getData().length];
				System.arraycopy(dp.getHeader(),0 , datapack, 0, DataPack.HEADLEN);
				System.arraycopy(dp.getData(),0 , datapack,DataPack.HEADLEN,dp.getData().length);
				
				try {
					dest.write(ByteBuffer.wrap(datapack));
				} catch (IOException e) {
					System.out.println("广播失败.....");
					e.printStackTrace();
				}
			}
		}
	}
	public void processClient(SocketChannel sc,DataPack dp)
	{
		//username&&password
	
			try {
				String str=new String(dp.getData(),"UTF-8");
				if(null!=str)
				{
				 String spli[]=str.split(";;");

						 if("xaccit".equals(spli[0])&&"123".equals(spli[1]))
						 {
							 
								sc.write(ByteBuffer.wrap("PASS".getBytes()));
							
						 }else
						  {
							 sc.write(ByteBuffer.wrap("NOPASS".getBytes()));
						  }
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	/**
	 * 初始化系统托盘的方法
	 */
	private void initSystemTray() {
		if (SystemTray.isSupported())
			systemTray = SystemTray.getSystemTray();
		
		URL url = getClass().getResource("/com/ccit/res/log.png");
        icon=Toolkit.getDefaultToolkit().createImage(url);
		TrayIcon trayIcon = new TrayIcon(icon);
		trayIcon.setToolTip("加中远程桌面服务端V1.0");
		trayIcon.setImageAutoSize(true);
		
		PopupMenu popupMenu = new PopupMenu("托盘菜单");

		// 创建显示主窗体菜单项
		MenuItem showMenuItem = new MenuItem("显示主窗体");
		showMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Server_win.this.setExtendedState(JFrame.NORMAL);
				Server_win.this.setVisible(true);
			}
		});

		// 创建退出菜单项
		MenuItem exitMenuItem = new MenuItem("退出");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		popupMenu.add(showMenuItem);
		popupMenu.addSeparator();
		popupMenu.add(exitMenuItem);
		trayIcon.setPopupMenu(popupMenu);
		try {
			systemTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
    public static void main(String[] args) throws IOException
	{
    	
	   new Server_win().init();

	}
}
