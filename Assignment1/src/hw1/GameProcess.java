package hw1;

import java.util.*;
import java.io.*;

public class GameProcess {
	private String [] team; //the name for each team
	private int num; //how many teams?
	private int whichTeam;//the team that is answering now
	private int [] score;//an array to keep track the score of each team
	
	public GameProcess(int in)
	{
		num = in;
		team = new String[num];
		Random rand = new Random();
		whichTeam = rand.nextInt(num) + 0;
		
		score = new int[num];
		for(int i = 0; i < num; i ++)
		{
			score[i] = 0; //initiate each team's score with 0
		}
	}
	
	public void gpReset()
	{
		for(int i = 0; i < num; i ++)
		{
			score[i] = 0;
		}
	}
	
	public void setName(int teamNum, String in)
	{
		team[teamNum] = in;
	}
	
	public String getName(int in)
	{
		return team[in];
	}
	
	public String currentTeam()
	{
		return team[whichTeam];
	}
	
	public String nextTeam()
	{
		whichTeam += 1;
		whichTeam = whichTeam%num;
		
		return team[whichTeam];
		
	}
	
	public void setScore(String t, int in)
	{
		for(int i = 0; i < num; i++)
		{
			if(team[i].equals(t))
			{
				score[i] += in;
			}
		}

	}
	
	public int getScore(int index)
	{
		return score[index];
	}
	
}
