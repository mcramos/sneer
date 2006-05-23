package org.sneer.swing.panel;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.sneer.swing.Util;
import org.sneer.swing.panel.decorators.Decorator;
import org.sneer.swing.panel.decorators.TransparentDecorator;
 
public class TransparentPanel extends JPanel implements ComponentListener, 
														 WindowFocusListener, 
														 Runnable{
	private static final long serialVersionUID = 1L;
	
	public static BufferedImage screenImg;
	private static long lastUpdate = 0;
	private boolean isListenerOk = false;

	private Rectangle screenRect;
	private boolean autoRefresh = true;
	private Robot robot;

	public float blur = 1f;
	public int brightness = 100;
	private int refreshFactor = 60000;
	private int refreshSleep = 100;
	private ArrayList<Decorator> decorators = new ArrayList<Decorator>();
	
	public TransparentPanel() {
		decorators.add(new TransparentDecorator(this));
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			return;
		}

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		screenRect = new Rectangle(dim.width, dim.height);
		
	}

	@Override
	public void repaint() {
		if(this.getParent()!=null && !this.isListenerOk)
			initialize();
		super.repaint();
	}

	public void initialize() {
		if(!isListenerOk){
			isListenerOk=true;
			getWindow().addComponentListener(this);
			getWindow().addWindowFocusListener(this);
			updateBackground();
			new Thread(this).start();
		}
	}



	public void updateBackground() {
		screenImg = robot.createScreenCapture(screenRect);
	}

	protected void refresh() {
		if (getWindow().isVisible() && this.isVisible()) repaint();
	}

	// JComponent --------------------------------------------------------------
	protected void paintComponent(Graphics g) {
		for (Decorator decorator : decorators) {
			decorator.paintComponent(g);
		}	
	}

	// ComponentListener -------------------------------------------------------
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {repaint();}
	public void componentResized(ComponentEvent e) {repaint();}
	public void componentShown(ComponentEvent e) {repaint();}

	// WindowFocusListener -----------------------------------------------------
	public void windowGainedFocus(WindowEvent e) {refresh();}
	public void windowLostFocus(WindowEvent e) {refresh();}

	// Runnable ----------------------------------------------------------------
	public void run() {
		try {
			while (true) {
				if(autoRefresh){
					Thread.sleep(refreshSleep);
					long now = System.currentTimeMillis();
					if ((now - lastUpdate) > refreshFactor) {
						Window window = getWindow();
						if (window.isVisible()) {
							window.setVisible(false);
							updateBackground();
							window.setVisible(true);
						}
						lastUpdate = now;
					}
				}else{Thread.sleep(60000);}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private Window getWindow(){
		return Util.getWindow(this);
	}

	// Getters/Setters ----------------------------------------------------
	public float getBlur() {return blur;}
	public void setBlur(float blur) {this.blur = blur;}
	public boolean isAutoRefresh() {return autoRefresh;}
	public void setAutoRefresh(boolean autoRefresh) {this.autoRefresh = autoRefresh;}
	public int getBrightness() {return brightness;}
	public void setBrightness(int brightness) {this.brightness = brightness;}
	public int getRefreshFactor() {return refreshFactor;}
	public void setRefreshFactor(int refreshFactor) {this.refreshFactor = refreshFactor;}
	public int getRefreshSleep() {return refreshSleep;	}
	public void setRefreshSleep(int refreshSleep) {this.refreshSleep = refreshSleep;}
	public ArrayList<Decorator> getDecorators() {return decorators;}

	// Main ----------------------------------------------------
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TransparentPanel pnl = new TransparentPanel();
        f.setSize(200,200);
        f.setContentPane(pnl);
        
        pnl.setBlur(0);
        pnl.setBrightness(200);
        
        f.setVisible(true);
    }
}