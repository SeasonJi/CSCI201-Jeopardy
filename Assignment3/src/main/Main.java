package main;

import java.awt.Dimension;

import frames.Login;
import frames.StartWindowGUI;

public class Main {

	public static void main (String [] args){

		//StartWindowGUI startWindow = new StartWindowGUI();
		//startWindow.setVisible(true);
		//Dimension d = startWindow.getSize();
		//System.out.println("height: "+d.getHeight()+"; width: "+d.getWidth());
		Login l = new Login();
		l.setVisible(true);
	}

}
