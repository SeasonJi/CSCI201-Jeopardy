package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import client.Message;
import frames.MainGUI;
import frames.StartWindowGUI;
import game_logic.CurrentQuestion;
import game_logic.GameData;
import game_logic.User;
import other_gui.QuestionGUIElement;

public class ServerListener extends Thread 
{
	private GameServer gs;
	private ServerSocket ss;
	private StartWindowGUI SW;
	private int totalPlayers, counter;
	private Vector<ServerThread> serverThreads;
	private GameData gameData;
	private User user;
	private List<String> teamNames;
	
	public ServerListener(GameServer server, ServerSocket sskt, StartWindowGUI SWGUI, int players, Vector<ServerThread> serverThreads, GameData gd, User loggedInUser, List<String> names) 
	{
		gs = server;
		ss = sskt;
		SW = SWGUI;
		totalPlayers = players;
		this.serverThreads = serverThreads;
		gameData = gd;
		user = loggedInUser;
		teamNames = names;
		
	    counter = totalPlayers ;
	}	
	
	public void sendMessageToAllClients(Message m)
	{
		for (ServerThread st : serverThreads) 
		{
			st.sendMessage(m);
		}
	}
	
	public void addTeamName(String in)
	{	
		teamNames.add(in);
	}
	
	public void run()
	{
		try{
			while(counter!=0)
			{		
				Socket s = ss.accept();
				counter--;
				
				SW.setWaitLabel(counter);
				
				System.out.println("connection from " + s.getInetAddress());
				ServerThread st = new ServerThread(s, gs, counter);
				
				serverThreads.add(st);	
				
				Message m = new Message();
				m.numberOfPlayers = counter;
				m.isNumberOfPlayers = true;
				gs.sendMessageToAllClients(m);
			}	
			
			while(teamNames.size() != totalPlayers)
			{
				this.yield();
			}
			
			gameData.setTeams(teamNames, totalPlayers);
			
			for(ServerThread st:serverThreads)
			{
				Message m = new Message();
				m.gameData = gameData;
				m.isGameData = true;
				st.sendMessage(m);
			}
			
			SW.dispose();
		}
		catch (IOException ioe) 
		{		
			System.out.println("ioe " + ioe.getMessage());	
		} 
		finally 
		{
			if (ss != null) 
			{
				try 
				{
					ss.close();
				} 
				catch (IOException ioe) 
				{
					System.out.println("ioe closing ss: " + ioe.getMessage());
					
				}
			}
		}		
	}
	
}

