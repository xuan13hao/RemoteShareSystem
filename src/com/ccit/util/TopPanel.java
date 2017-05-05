package com.ccit.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class TopPanel extends JPanel 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -9182108082223914583L;
	private Image image=null;
    public TopPanel()
    {
    	super();
    	this.setSize(550, 30);
		try {
			image=ImageIO.read(this.getClass().getResourceAsStream("/com/ccit/res/client_top.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(550,30);
	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);

		g.drawImage(image, 0, 0, this);
		
	}
	
 
}
