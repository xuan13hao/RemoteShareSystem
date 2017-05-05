import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Test {
	private JFrame frame = null;
	JPanel pane = null;
	int mouseX = 0;
	int mouseY = 0;
	public Test() {
	frame = new JFrame();
	frame.setUndecorated(true);
	pane = (JPanel) frame.getContentPane();
	pane.setBackground(Color.black);
	pane.addMouseListener(new MouseAdapter() {
	public void mousePressed(MouseEvent mouseEvent) {
	if (mouseEvent.getButton() == mouseEvent.BUTTON1) {
	mouseX = mouseEvent.getX();
	mouseY = mouseEvent.getY();
	}
	}
	});
	pane.addMouseMotionListener(new MouseMotionAdapter() {
	public void mouseDragged(MouseEvent e) {
	if (e.getModifiers() == e.BUTTON1_MASK) {
	frame.setLocation(frame.getX() + e.getX() - mouseX,
	frame.getY() + e.getY() -
	mouseY);
	}
	}
	});
	frame.setSize(300, 200);
	frame.setVisible(true);
	}
	public static void main(String args[]) {
	Test t = new Test();
	}
	}
