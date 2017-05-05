package com.ccit.util;

import javax.swing.Icon;
import javax.swing.JButton;

public class SmallButton extends JButton 
{
   public SmallButton(Icon icon)
   {
	   super(icon);
	   this.setBorder(null);
   }
}
