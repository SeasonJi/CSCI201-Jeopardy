package other_gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import frames.MainGUINetworked;
import main.Main;

public class GameTimer extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public ImageIcon clockPics[];	

	public  int currentImage;
	public  int totalImage;
	private static Timer timer;
	public GameTimer(int countDown)
	{
		totalImage = countDown;
		currentImage = 0;

		clockPics = new ImageIcon[totalImage+1];
		for (int i = 0; i < clockPics.length; i++)
		{
			clockPics[i] = scaleImage(new ImageIcon("images/clockAnimation/frame_" + i + "_delay-0.06s.jpg"), 100, 100);
		}
		
	}
	
	
	//time++
	public void setClock(JLabel clockLabel)
	{
		currentImage++;
		clockLabel.setIcon(clockPics[currentImage]);
	}
	
	//resize image
	public ImageIcon scaleImage(ImageIcon in, int w, int h)
	{
		Image temp = in.getImage();
		Image resized = temp.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
		ImageIcon finalIcon = new ImageIcon(resized);

	    return finalIcon;
	}
	
	//reset
	public void reset()
	{
		currentImage = 0;
	}

}
