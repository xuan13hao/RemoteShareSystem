import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;


public class FullFrame extends JFrame 
{

	
	private static final long serialVersionUID = 2106761347294717105L;
	GraphicsDevice myd =null; 
    private JLabel label=null;
    private JPopupMenu pop=new JPopupMenu();
    private JMenuItem fullscreen=new JMenuItem("全屏");
    private JMenuItem simpleScreen=new JMenuItem("退出全屏");
    private JPanel poppan=new JPanel();
    private JTextArea ar=new JTextArea();
    public FullFrame()
    {
    	super("全屏显示！");
    	System.setProperty("sun.java2d.noddraw", "true");
    	this.setUndecorated(true);
    	myd=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	label=new JLabel("最大化");
    	this.add(ar);
    
    	this.setSize(300,200);
    	this.setLocation(200, 200);
    	this.setVisible(true);
    	
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	
    	
    	ar.addKeyListener(new KeyAdapter() 
    	{

			@Override
			public void keyPressed(KeyEvent e) 
			{
				action_max_min(e, myd);
			}
    		
		});
    	this.addWindowListener(new WindowAdapter() 
        {

			@Override
			public void windowStateChanged(WindowEvent e) {
				
				if(e.getNewState() == 6)
				{
					//WinFrame.this.setUndecorated(true);
					//WinFrame.this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
                	//WinFrame.this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
                	//WinFrame.this.setVisible(true);
                	//System.out.println("窗口最大化");
					 myd.setFullScreenWindow(FullFrame.this);
				}
			}
        	
		});
    	initPopMenu();
    }
    public void initPopMenu()
    {
    	fullscreen.addActionListener(new ActionListener() 
    	{
			@Override
			public void actionPerformed(ActionEvent e) {
				
				action_FullScreen(e,myd);
			}
		});
    	simpleScreen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				action_simpleScreen(e,myd);
			}
		});
    	pop.add(fullscreen);
    	pop.addSeparator();
    	pop.add(simpleScreen);
    	//poppan.add(pop);
    	//this.add(pop);
    	ar.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) 
				{
                     pop.show(FullFrame.this, e.getX(), e.getY());
                }
			}
    		
		});
    	
        
    }
    
    public void action_simpleScreen(ActionEvent e,GraphicsDevice myd)
    {
   	 myd.setFullScreenWindow(null); 
    }
         public void action_FullScreen(ActionEvent e,GraphicsDevice myd)
         {
        	 myd.setFullScreenWindow(this); 
         }
         public void action_max_min(KeyEvent e,GraphicsDevice myd) 
         {
    	  if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
    	   myd.setFullScreenWindow(null);
    	  }else if(e.getKeyCode() == KeyEvent.VK_F1) 
    	  {
    		 
    		  ar.setBounds(0, 0, 1024, 768);
    	  // ar.setSize(new Dimension(1024,768));
    	   myd.setFullScreenWindow(this);
    	  }
         }

	public static void main(String[] args) 
	{
	   new FullFrame();
	}

}
