import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import com.ccit.util.SmallButton;
import com.ccit.util.TopPanel;


public class Client extends JFrame 
{
	private static final long serialVersionUID = 2894849525208480284L;

	private int mouseX = 0;
	private int mouseY = 0;

	private TopPanel top=new TopPanel();
	private Image topbak=null;
	private JScrollPane jp=null;
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
	private GraphicsDevice myd =null; 
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
    		if (e.getModifiers() == e.BUTTON1_MASK) 
    		{
    			int x=Client.this.getX()+e.getX()-mouseX;
    			int y=Client.this.getY() + e.getY() -mouseY;
    		    Client.this.setLocation(x,y);
    		}
    		}
    };
    public Client()
    {
    	System.setProperty("sun.java2d.noddraw", "true");
    	this.setUndecorated(true);
    	myd=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	this.setLayout(null);
    	//this.setBounds(200, 200, 800, 600);
    	this.setSize(800, 600);

    	init();
    	this.getRootPane().setBorder(new LineBorder(new Color(120,120,120),3));
    	this.setVisible(true);
    	
    	initListener();
       
    }
    public void init()
    {
    	top.setBounds((this.getWidth()-550)/2,1,550,30);
    	initIcon();
    	initSmallbutton();
    	top.setLayout(new FlowLayout(FlowLayout.RIGHT,10,0));
    	top.add(new JLabel("   "));
    	top.add(screen);
    	top.add(small);
    	top.add(simple);
    	top.add(close);
    	top.add(new JLabel("   "));
    	
    	this.add(top);
    	jp=new JScrollPane();
    	//jp.setBounds(0, 0, 800, 600);
    	this.add(jp);
    	Dimension scr=Toolkit.getDefaultToolkit().getScreenSize();
			int width=(int)scr.getWidth();
			int height=(int)scr.getHeight();
			this.setLocation((width-800)/2,(height-600)/2);
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
    	small=new SmallButton(small_i);
    	small.setRolloverIcon(small_im);
    	screen=new SmallButton(screen_i);
    	screen.setRolloverIcon(screen_im);
    	simple=new SmallButton(simple_i);
    	simple.setRolloverIcon(simple_im);
    }
    /**
     * 初始化所有按纽的临听器
     */
    public void initListener()
    {
    	   top.addMouseListener( moveWindowListener);
    	   top.addMouseMotionListener(moveWindowListener);
           close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
           small.addActionListener(new ActionListener() {
   			
   			@Override
   			public void actionPerformed(ActionEvent e) {
   				// TODO Auto-generated method stub
   				Client.this.setExtendedState(JFrame.ICONIFIED);
   			}
   		});
           simple.addActionListener(new ActionListener() {
      			
      			@Override
      			public void actionPerformed(ActionEvent e) {
      				Client.this.setSize(800, 600);
      				top.setBounds((Client.this.getWidth()-550)/2,1,550,30);
      				Dimension sc=Toolkit.getDefaultToolkit().getScreenSize();
      				int width=(int)sc.getWidth();
      				int height=(int)sc.getHeight();
      				Client.this.setLocation((width-800)/2,(height-600)/2);
      				Client.this.setExtendedState(JFrame.NORMAL);
      			}
      		});
           screen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fullScreen();
			}
		});
    	
    }
    
    public void fullScreen()
    {

    	this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    	top.setBounds((this.getWidth()-550)/2,1,550,30);
    	this.setLocation(0, 0);    	
    }

   
    public static void main(String[] args)
    {
		new Client();
	}
}
