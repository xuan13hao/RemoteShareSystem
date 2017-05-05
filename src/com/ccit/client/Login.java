package com.ccit.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.ccit.server.DataPack;
import com.ccit.util.ByteIntSwitch;

public class Login extends JFrame
{
	private static final long serialVersionUID = 7238997905461242892L;
    private boolean remember=true;
	private JLabel top = null;
	
	private JButton login = new JButton("登陆");
	private JCheckBox register = new JCheckBox("记住密码");


	private Box bottomBut = Box.createHorizontalBox();
	private JPanel bp = new JPanel();
	

	private JLabel user = new JLabel("帐号:");
	private JLabel pwd = new JLabel("密码:");
	private JTextField tuser = new JTextField(15);
	private JPasswordField tpwd = new JPasswordField(15);
	
	private Box b1=Box.createHorizontalBox();
    private Box b2=Box.createHorizontalBox();
    private Box b3=Box.createHorizontalBox();
    private Box b4=Box.createVerticalBox();

    private Font font=new Font("黑体", Font.BOLD, 14);
    private Color bak=new Color(0XE4,0XF4,0XFF);
    
   //定义检测SocketChannel的Selector对象
	private Selector selector = null;
	//定义处理编码和解码的字符集
	private Charset charset = Charset.forName("UTF-8");
	//客户端SocketChannel
	private SocketChannel sc = null;
    private String hostName=null;
    private Integer port=9999;
    public Login() {
		super("加中远程桌面共享客户端V1.0");
		try {
		init();
		initUserAndPassword();
		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "服务器没有开启，暂时无法链接!");
			e.printStackTrace();
		}
	}

   
	public void init()throws Exception {
		
		top=new JLabel(new ImageIcon(Login.class.getResource("/com/ccit/res/icon.png")));
		//this.getContentPane().setBackground(new Color(0XE4,0XF4,0XFF));
		
		tuser.setPreferredSize(new Dimension(200,25));
		tpwd.setPreferredSize(new Dimension(200,25));
		tuser.setToolTipText("请输入账号!");
		tpwd.setToolTipText("请输入密码!");
		user.setFont(font);
		pwd.setFont(font);
		register.setFont(font);
		login.setFont(font);
		//中间布局
		b1.add(Box.createHorizontalStrut(20));
		b1.add(user);
		b1.add(Box.createHorizontalStrut(10));
		b1.add(tuser);
		
		b2.add(Box.createHorizontalStrut(20));
		b2.add(pwd);
		b2.add(Box.createHorizontalStrut(10));
		b2.add(tpwd);
		
		b3.add(Box.createHorizontalStrut(10));
		
		b4.add(Box.createVerticalStrut(10));
		b4.add(b1);
		b4.add(Box.createVerticalStrut(10));
		b4.add(b2);
		b4.add(Box.createVerticalStrut(10));
		b4.add(b3);

		JPanel jj=new JPanel();
		jj.add(b4);
		jj.setBackground(bak);
		register.setSelected(true);
		register.setBackground(new Color(192,226,250));
		//设置底部
		bp.setBackground(new Color(192,226,250));
		bottomBut.add(register);
		bottomBut.add(Box.createHorizontalStrut(80));
		bottomBut.add(login);
		bp.add(bottomBut);
		
		this.add(jj,"Center");
		this.add(top,"North");
		this.add(bp,"South");
		this.intiLisener();
		
		this.setSize(306, 220);
		this.setResizable(false);
		this.setLocation(500, 200);
		this.setIconImage(this.getToolkit().getImage(Login.class.getResource("/com/ccit/res/log.png")));
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	

	}
	public void initUserAndPassword()
	{
		File f=new File("redwww.properties");
		if(f.exists())
		{
		    Properties pro=new Properties();
		    try {
				pro.load(new FileInputStream(f));
				tuser.setText(pro.getProperty("username"));
				tpwd.setText(pro.getProperty("password"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void intiLisener()throws Exception
	{
		login.addActionListener(new ActionListener()
		{
                
			@Override
			public void actionPerformed(ActionEvent e) {
				loginServer();	
		}});
		tpwd.addActionListener(new ActionListener()
		{     
			@Override
			public void actionPerformed(ActionEvent e) {
				loginServer();	
		}});
		
	
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(register.isSelected())
				{
					remember=true;
				}else
				{
					remember=false;
				}
				
			}
		});
	}
	public void loginServer()
	{
		String userName = tuser.getText();
		String userPwd = new String(tpwd.getPassword());
		String userpwd=userName+";;"+userPwd;
		login.setEnabled(false);
		tpwd.setEnabled(false);
		tuser.setEditable(false);
		top.setIcon(new ImageIcon(Login.class.getResource("/com/ccit/res/icon2.png")));
		//Login.this.dispose();
		try {
			//记住密码
			if(remember)
			{
				Properties p=new Properties();
				p.put("username", userName);
				p.put("password", userPwd);
				FileOutputStream pos=new FileOutputStream("redwww.properties");
				p.store(pos, "记住了密码");
				pos.close();
			}

			initSocketChanel();
			//DataPack.PRO.get("USERPASSWD")+
			
			byte userandpwd[]=userpwd.getBytes();
			byte send[]=new byte[DataPack.HEADLEN+userandpwd.length];
			
			System.arraycopy(DataPack.PRO.get("USERPASSWD").getBytes(), 0, send, 0, 4);
			System.arraycopy(ByteIntSwitch.toByteArray(userandpwd.length, 4), 0, send, 4, 4);
			System.arraycopy(userandpwd, 0, send, DataPack.HEADLEN, userandpwd.length);
			sc.write(ByteBuffer.wrap(send));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}	

	public void initSocketChanel()throws IOException
	{
	
			selector = Selector.open();
			//加载资源文件conf.properties
			ResourceBundle rb=ResourceBundle.getBundle("conf");
			hostName=rb.getString("hostName");
			port=Integer.parseInt(rb.getString("port"));
			InetSocketAddress isa = new InetSocketAddress(hostName,port);
			//调用open静态方法创建连接到指定主机的SocketChannel
			sc = SocketChannel.open(isa);
			//设置该sc以非阻塞方式工作
			sc.configureBlocking(false);
			//将SocketChannel对象注册到指定Selector
			sc.register(selector, SelectionKey.OP_READ);
			//启动读取服务器端数据的线程
			new ClientThread().start();
			//创建键盘输入流
		
	}
	
	//定义读取服务器数据的线程
	private class ClientThread extends Thread
	{
		public void run()
		{
			try
			{
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
							ByteBuffer buff = ByteBuffer.allocate(1024);
							String content = "";
							while(sc.read(buff) > 0)
							{
								sc.read(buff); 
								buff.flip();
								content += charset.decode(buff);
							}
							System.out.println(content);
							//打印输出读取的内容
							if(content.equals("PASS"))
							{
								Login.this.dispose();
								new Client();
							}else
							{
								JOptionPane.showMessageDialog(Login.this, "用户名和密码不合法,请重新登陆!");
								Login.this.dispose();
								System.exit(0);
							}
							//为下一次读取作准备
							sk.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		new Login();

	}

}

