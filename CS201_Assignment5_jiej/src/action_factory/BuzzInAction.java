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
import server.Client;

public class BuzzInAction extends Action{

	@Override
	public void executeAction(MainGUINetworked mainGUI, ServerGameData gameData,
			Message message, Client client) {
		BuzzInMessage buzzMessage = (BuzzInMessage) message;
		client.getCurrentQuestion().waitBuzzin = false;
		client.getCurrentQuestion().isBuzzinTeam = true;
		//update the team on the current question to be the one who buzzed in
		client.getCurrentQuestion().updateTeam(buzzMessage.getBuzzInTeam(), gameData);
		
		if(client.getTeamIndex()==client.getCurrentQuestion().getOriginalTeam()){
			client.getCurrentQuestion().getGameTimer().reset();
		}
	}

}
