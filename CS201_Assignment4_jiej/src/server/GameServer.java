package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import client.Message;
import frames.StartWindowGUI;
import game_logic.GameData;
import game_logic.User;

public class GameServer {
	
	private ServerListener listener;
	public GameData gameData;
	private int port, totalPlayers;
	private User user;
	private List<String> teamNames;
	private Vector<ServerThread> serverThreads;
	private ServerSocket ss;	
	private StartWindowGUI SW;
	
	public GameServer(int port, int players, StartWindowGUI SWGUI, GameData gd, User loggedInUser, List<String> names)
	{
		this.port = port;
		totalPlayers = players;
		SW = SWGUI;
		gameData = gd;
		user = loggedInUser;
		teamNames = names;
		serverThreads = new Vector<ServerThread>();	

		try 
		{
			ss = new ServerSocket(port);
			System.out.println("port is: " + port);
		}
		catch (IOException e) 
		{
			 e.printStackTrace();
		}	    
		
		listener = new ServerListener(this,ss,SW,players,serverThreads,gameData,user,teamNames);
		listener.start();
	}
	
	public void addTeamName(String name)
	{
		listener.addTeamName(name);
	}
	
	public void sendMessageToAllClients(Message m) 
	{	
		for (ServerThread st : serverThreads) 
		{
			st.sendMessage(m);		
		}
	}
	

}
