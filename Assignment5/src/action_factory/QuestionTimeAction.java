package action_factory;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import frames.MainGUINetworked;
import game_logic.ServerGameData;
import messages.Message;
import messages.QuestionTimeMessage;
import other_gui.QuestionGUIElementNetworked;
import server.Client;

public class QuestionTimeAction extends Action {
	
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData, Message message,Client client) 
	{
		QuestionTimeMessage qTimeMessage = (QuestionTimeMessage) message;
		//set the current question on the client using the indices provided in the message
		client.setCurrentQuestion(qTimeMessage.getX(), qTimeMessage.getY());
	
		
		//get current question and set left reply time
		client.getCurrentQuestion().setTimeLabel(qTimeMessage.runningTime);
		
		//set the clock pic for current time
		Image temp = null;
		try 
		{
			temp = ImageIO.read(new File("images/clockAnimation/frame_" + qTimeMessage.runningTime + "_delay-0.06s.jpg"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		Image resizeTemp = temp.getScaledInstance(100, 100, java.awt.Image.SCALE_DEFAULT);
		ImageIcon finalIcon = new ImageIcon(resizeTemp);
		
		//set the icon to corresponding team
		mainGUI.clockLabels[client.getCurrentQuestion().getCurrentTeam().getTeamIndex()].setIcon(finalIcon);
		
		for(int i = 0; i < gameData.getNumberOfTeams(); i++)
		{
			if(client.getCurrentQuestion().getCurrentTeam().getTeamIndex() != i)
			{
				mainGUI.clockPanels.get(i).setVisible(false);
			}
			else
			{			
				mainGUI.clockPanels.get(i).setVisible(true);
			}
		}
		
		if(client.getCurrentQuestion().waitBuzzin == true)
		{
			mainGUI.clockPanels.get(client.getCurrentQuestion().getCurrentTeam().getTeamIndex()).setVisible(false);
			client.getCurrentQuestion().getTeamLabel().setIcon(finalIcon);
		}
		else
		{
			client.getCurrentQuestion().getTeamLabel().setIcon(null);
		}
	}

}
