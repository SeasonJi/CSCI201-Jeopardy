package server;

import java.io.EOFException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client.Message;
import game_logic.CurrentQuestion;
import game_logic.GameData;

public class ServerThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private GameServer gs;
	private int remainPlayers;
	String teamName;
	
	public ServerThread(Socket s, GameServer server, int playersLeft) 
	{
		try 
		{
			gs = server;
			remainPlayers = playersLeft;
			oos = new ObjectOutputStream(s.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(s.getInputStream());
			
			this.start();
		} 
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}
	
	public void sendMessage(Message m) {
		try 
		{
			oos.writeObject(m);
			oos.flush();
		}
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}
	
	public void run() 
	{	
		try{
			while(true)
			{
				Message me = (Message)ois.readObject();
				if(me.isTeamName)
				{
	 				teamName = me.teamName;
	 				gs.addTeamName(teamName);		 				
	 			}
	 			else if(me.isBuzzIn)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isBuzzInTeam)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isContinuePlay)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
				else if(me.isCurrentQuestion)
				{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isNextQuestion)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isUpdate)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isReadyForFinalJeopardy)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isBetSet)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isFJAnswered)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	 			else if(me.isUpdateToAllClients)
	 			{
	 				gs.sendMessageToAllClients(me);
	 			}
	         }
		} 
		catch(ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch(EOFException e)
		{
			Message m = new Message();
			m.isOnePlayerLeft = true;
			
			gs.sendMessageToAllClients(m);
			e.printStackTrace();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
	}
}

