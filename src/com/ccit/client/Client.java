package com.ccit.client;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import com.ccit.util.SmallButton;
import com.ccit.util.TopPanel;


public class Client extends JFrame 
{
	private static final long serialVersionUID = 2894849525208480284L;

	private int mouseX = 0;
	private int mouseY = 0;

	private TopPanel top=new TopPanel();
	private JScrollPane show=null;
	private SmallButton close;
	private SmallButton small;
	private SmallButton screen;
	private SmallButton simple;
	private Icon close_i;
	private Icon close_im;
	private Icon small_i;
	private Icon small_im;
	private Icon screen_i;
	private Icon screen_im;
	private Icon simple_i;
	private Icon simple_im;
	private RemoteScreenFrame rf;
	private JLayeredPane layer;
	private boolean isFull=false;
    private JPopupMenu pop=new JPopupMenu();
    private JMenuItem fullscreen=new JMenuItem("全屏");
    private JMenuItem simpleScreen=new JMenuItem("退出全屏");
    private int  initWidth=800;
    private int initHeight=600;
	 /**
     * 窗体移动的鼠标事件
     */
    private MouseAdapter moveWindowListener = new MouseAdapter() 
    {
    	public void mousePressed(MouseEvent mouseEvent) 
    	{
    		if (mouseEvent.getButton() == mouseEvent.BUTTON1) 
    		{
    		mouseX = mouseEvent.getX();
    		mouseY = mouseEvent.getY();
    		}
    	}
    	public void mouseDragged(MouseEvent e) 
    	{
    		if (e.getModifiers()== e.BUTTON1_MASK) 
    		{
    			int x=Client.this.getX()+e.getX()-mouseX;
    			int y=Client.this.getY() + e.getY() -mouseY;
    		    Client.this.setLocation(x,y);
    		}
    		}
    };
    public Client()
    {
    	super("加中远程桌面共享客户端V1.0");
    	this.setUndecorated(true);
    	init();   	
    	initListener();
    	initPopMenu();
    }
    public void init()
    {
    	this.setSize(initWidth, initHeight);
    	layer=this.getLayeredPane();
    	show=new JScrollPane();
    	show.setBounds(0, 0,initWidth, initHeight);
    	rf=new RemoteScreenFrame(this,show);
		show.setViewportView(rf);
    	//show.add(rf);
		new Thread(rf).start();
    	top.setBounds((this.getWidth()-550)/2,2,550,30);
    	initIcon();
    	initSmallbutton();
    	top.setLayout(new FlowLayout(FlowLayout.RIGHT,10,0));
    	top.add(new JLabel("   "));
    	top.add(screen);
    	top.add(small);
    	top.add(simple);
    	top.add(close);
    	top.add(new JLabel("   "));
    	top.setOpaque(false);
    	layer.add(top, 8);
    	layer.add(show,5);
    	//将窗体放在屏幕中间
	    Dimension scr=Toolkit.getDefaultToolkit().getScreenSize();
		int width=(int)scr.getWidth();
		int height=(int)scr.getHeight();
		this.setLocation((width-initWidth)/2,(height-initHeight)/2);
		this.getRootPane().setBorder(new LineBorder(new Color(28,132,206),2));
    	this.setVisible(true);
    	this.setIconImage(this.getToolkit().getImage(Login.class.getResource("/com/ccit/res/log.png")));
    }
    /**
     * 初始化图标
     */
    public void initIcon()
    {
    	close_i=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_close.png"));
    	close_im=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_close_m.png"));
    	small_i=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_small.png"));
    	small_im=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_small_m.png"));
    	screen_i=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_screen.png"));
    	screen_im=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_screen_m.png"));
    	simple_i=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_simple.png"));
    	simple_im=new ImageIcon(this.getClass().getResource("/com/ccit/res/s_simple_m.png"));	
    }
    /**
     * 初始化标题标上的小按纽
     */
    public void initSmallbutton()
    {
    	close=new SmallButton(close_i);
    	close.setRolloverIcon(close_im);
    	close.setToolTipText("关闭窗体");
    	small=new SmallButton(small_i);
    	small.setRolloverIcon(small_im);
    	small.setToolTipText("最小化");
    	
    	screen=new SmallButton(screen_i);
    	screen.setRolloverIcon(screen_im);
    	screen.setToolTipText("全屏");
    	simple=new SmallButton(simple_i);
    	simple.setRolloverIcon(simple_im);
    	simple.setToolTipText("还原");
    	simple.setVisible(false);
    }
    /**
     * 初始化所有按纽的临听器
     */
    public void initListener()
    {
    	   top.addMouseListener( moveWindowListener);
    	   top.addMouseMotionListener(moveWindowListener);
    	   //关闭窗体
           close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		   });
           //最小化
           small.addActionListener(new ActionListener() {
   			
   			@Override
   			public void actionPerformed(ActionEvent e) {
   				// TODO Auto-generated method stub
   				Client.this.setExtendedState(JFrame.ICONIFIED);
   			}
   		});
           //正常
           simple.addActionListener(new ActionListener() {
      			
      			@Override
      			public void actionPerformed(ActionEvent e) {
      				simpleScreen();	
      			}
      		});
           //全屏
           screen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fullScreen();
			}
		});
           //隐藏标题控制栏
           show.addMouseMotionListener(new MouseAdapter() 
           {
			@Override
   			public void mouseMoved(MouseEvent e) 
   			{
				if(isFull)
				{
   					int starx=(Client.this.getWidth()-550)/2;
   					if(e.getX()>=starx&&e.getX()<=starx+550&&e.getY()>=0&&e.getY()<=24)
   					{
   						top.setVisible(true);	
   					}else 
   					{
   						top.setVisible(false);
   					}
				}
   			}
           	   
   		});

    }
    public void simpleScreen()
    {
    	    this.setSize(initWidth, initHeight);
			top.setBounds((Client.this.getWidth()-550)/2,1,550,30);
			Dimension sc=Toolkit.getDefaultToolkit().getScreenSize();
			int width=(int)sc.getWidth();
			int height=(int)sc.getHeight();
			this.setLocation((width-initWidth)/2,(height-initHeight)/2);
			this.setExtendedState(JFrame.NORMAL);
			isFull=false;
			simple.setVisible(false);
			screen.setVisible(true);
			this.setAlwaysOnTop(false);
			simpleScreen.setEnabled(false);
			fullscreen.setEnabled(true);
			show.setBounds(0, 0,initWidth, initHeight);
			show.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			show.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }
    
    public void fullScreen()
    {
    	this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    	top.setBounds((this.getWidth()-550)/2,1,550,30);
    	this.setLocation(0, 0);   
    	this.setAlwaysOnTop(true);
    	top.setVisible(false);
    	isFull=true;
    	screen.setVisible(false);
    	simple.setVisible(true);
    	fullscreen.setEnabled(false);
    	simpleScreen.setEnabled(true);
    	show.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER 
);
    	show.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER 
);
    	//System.out.println(this.getWidth());
    	show.setBounds((this.getWidth()-1024)/2, 0, 1024, 768);
    	
    }
    public void initPopMenu()
    {
    	fullscreen.addActionListener(new ActionListener() 
    	{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				
				fullScreen();
			}
		});
    	simpleScreen.addActionListener(new ActionListener() 
    	{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				simpleScreen();
				top.setVisible(true);
			}
		});
    	
    	pop.add(fullscreen);
    	pop.addSeparator();
    	pop.add(simpleScreen);
    	simpleScreen.setEnabled(false);
    	show.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) 
				{
                     pop.show(Client.this, e.getX(), e.getY());
                }
			}
    		
		});
    	
        
    }
   
    public static void main(String[] args)
    {
		new Client();
	}
}
