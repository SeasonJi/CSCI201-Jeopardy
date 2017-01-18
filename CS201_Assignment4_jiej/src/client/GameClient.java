package client;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import frames.MainGUI;
import frames.StartWindowGUI;
import game_logic.CurrentQuestion;
import game_logic.GameData;
import client.Message;

public class GameClient extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
    private String teamName ;	
    private StartWindowGUI SW;
    private GameData gameData;
    private int remainPlayers;
    
    private Socket s = null;
    
    private MainGUI mainGUI;

	public GameClient(String host, int port, StartWindowGUI SWGUI, String teamName)
	{	
		SW = SWGUI;
		this.teamName = teamName;
		try 
		{			
			s = new Socket(host, port);	
		
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			oos.flush();
			
			Message m = new Message();
			m.teamName = teamName;
			m.isTeamName = true;
			oos.writeObject(m);
			oos.flush();
			
			this.start();
		}
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}
	
	public void sendMessage(Message m) 
	{
		try 
		{
			oos.writeObject(m);
			oos.flush();
		} 
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}
	
	public void run(){
		try
		{
			while(true)//specify which kind of message is sent
			{	
				Message m = (Message)ois.readObject();
				if(m.isNumberOfPlayers)
				{		
					remainPlayers = m.numberOfPlayers;
					SW.setWaitLabel(remainPlayers);							
				}
				else if(m.isGameData)
				{
					gameData = m.gameData;
					mainGUI = new MainGUI(gameData, SW.getUser(), teamName, this);
					mainGUI.setVisible(true);
					SW.dispose();			
				}
				else if(m.isBuzzIn)
				{
					mainGUI.buzzIn(m.currentQuestionX, m.currentQuestionY, m.howManyTeamAnswered);
				}
				else if(m.isBuzzInTeam)
				{
					mainGUI.getBuzzInTeam(m.currentQuestionX, m.currentQuestionY, m.buzzInTeamName);
				}
				else if(m.isContinuePlay)
				{
					mainGUI.continuePlay(m.currentQuestionX, m.currentQuestionY);
				}
				else if(m.isCurrentQuestion)
				{		
					mainGUI.changeToQuestionPanel(m.currentQuestionX, m.currentQuestionY);
				}
				else if(m.isNextQuestion)
				{
					mainGUI.nextQuestion(m.currentQuestionX, m.currentQuestionY, m.originalTurn);
				}
				else if(m.isOnePlayerLeft)
				{
					SW.setWaitLabel(remainPlayers + 1);	
				}
				else if(m.isUpdate)
				{
					mainGUI.getUpdate(m.updateString, m.updateScore, m.isRightOrWrong);
				}
				else if(m.isReadyForFinalJeopardy)
				{
					mainGUI.finalJeopardyReady(m.currentQuestionX, m.currentQuestionY, m.teamName);
				}
				else if(m.isBetSet)
				{
					mainGUI.FJGUI.getBetSet(m.teamName, m.bet);
				}
				else if(m.isFJAnswered)
				{
					mainGUI.FJGUI.increaseNumberOfAnswers(m.teamName);
				}
				else if(m.isUpdateToAllClients)
				{
					mainGUI.getUpdateToAllClients(m.teamName,m.updateString, m.updateScore, m.isRightOrWrong);
				}
			}
		}
		catch (ClassNotFoundException cnfe) 
		{
			cnfe.printStackTrace();
		}
		catch (IOException ioe) 
		{
			SW.setWaitLabelCancel("Sorry the host cancel the game! Please join another game");
			SW.cancelGame();
			try 
			{
				join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		finally 
		{
			try 
			{
				if (s != null) 
				{
					s.close();
				}	
			} 
			catch (IOException ioe) 
			{
				ioe.printStackTrace();
			}
		}
	}
}


