import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRootPane;
   
   public class WinFrame extends JFrame {
   private JButton jb=new JButton("full");
    public WinFrame(){
        this.setName("Window 窗口状态");
        //WinFrame.this.setUndecorated(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(jb);
       
        jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				 
				 WinFrame.this.setVisible(false);
				 WinFrame.this.setSize(300,200);
				 
			  WinFrame.this.setUndecorated(true);
			  WinFrame.this.setVisible(true);
			}
		});
       // this.simple();
        
 /*       this.addWindowStateListener(new WindowStateListener () {

            public void windowStateChanged(WindowEvent state) {
                
                if(state.getNewState() == 1 || state.getNewState() == 7) {
                    System.out.println("窗口最小化");
                }else if(state.getNewState() == 0) {
                    System.out.println("窗口恢复到初始状态");
                }else if(state.getNewState() == 6) {
                    full();
                	System.out.println("窗口最大化");
                }
            }
        });*/
       
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
					System.out.println("jjjjj");
				}
			}
        	
		});
        
        
        this.addKeyListener(new KeyAdapter() 
        {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==27)
				{
					
				}
			}
        	
		});
        this.setVisible(true);
    }
    
    public void full()
    {
    	Dimension di=Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rec=new Rectangle(0,0,di.width,di.height);
     	//this.setVisible(false);
        this.setUndecorated(true);
     	this.setSize(di);
     	//this.setVisible(true);
     	this.setAlwaysOnTop(true);
    }
    public void simple()
    {
    	 this.setSize(300,300);
    }
    public static void main(String[] args) {
        new WinFrame();
    }

}