package game_logic;

import java.awt.Color;
import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import other_gui.AppearanceConstants;

public class Category implements Serializable{

	//The category name
	private String category;
	//image icon
	private static ImageIcon icon;
	//The category's index in ordering the categories
	private int index;
	//The label displayed on the MainGUI above the game board
	private JLabel categoryLabel;
	
	public Category(String category, int index){
		this.category = category;
		this.index = index;
		
		//create the label
		categoryLabel = new JLabel(category, icon, JLabel.CENTER);
		categoryLabel.setHorizontalTextPosition(JLabel.CENTER);
		categoryLabel.setVerticalTextPosition(JLabel.CENTER);
		categoryLabel.setForeground(Color.lightGray);
		categoryLabel.setFont(AppearanceConstants.fontMedium);
	}
	
	public static ImageIcon getIcon(){
		return icon;
	}
	//set the icon
	public static void setIcon(String filePath){
		Image darkBlueImage = new ImageIcon(filePath).getImage();
		icon = new ImageIcon(darkBlueImage.getScaledInstance(400, 400,  Image.SCALE_SMOOTH));
	}
	
	public static void clearIcon(){
		icon = null;
	}
	
	public int getIndex(){
		return index;
	}
	
	public JLabel getCategoryLabel(){
		return categoryLabel;
	}
}
