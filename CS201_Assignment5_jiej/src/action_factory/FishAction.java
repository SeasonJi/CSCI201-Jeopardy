package action_factory;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import frames.MainGUINetworked;
import game_logic.ServerGameData;
import messages.BuzzInMessage;
import messages.Message;
import messages.FishMessage;
import server.Client;

//hw5 new message to set the fish waiting pic
public class FishAction extends Action {
	
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData,Message message, Client client) {
		FishMessage fishMessages = (FishMessage) message;
		
		
		//get image
		Image temp = null;
		try 
		{
			temp = ImageIO.read(new File("images/waitingAnimation/frame_" +fishMessages.getBuzzInTime() + "_delay-0.1s.jpg"));
		}
		catch (IOException e) 
		{	
			e.printStackTrace();
		}
		Image resize = temp.getScaledInstance(100, 100, java.awt.Image.SCALE_DEFAULT);
		ImageIcon fishIcon = new ImageIcon(resize);
		
		
		
		if(client.getTeamIndex() != client.getCurrentQuestion().getCurrentTeam().getTeamIndex())
		{
			client.getCurrentQuestion().announcementsLabel.setIcon(fishIcon);
		}
		
	}

}
