package game_logic;

import java.io.Serializable;
import java.util.Comparator;

public class TeamData implements Serializable{
	//index of this team in the TeamDataList in the GameData class
	protected int teamIndex;
	public Integer totalPoints;
	protected String teamName;
	//their final jeopardy bet
	protected int bet;
	protected Boolean correctFJAnswer;
	
	public TeamData(Integer team, Integer totalPoints, String teamName){
		this.teamIndex = team;
		this.totalPoints = totalPoints;
		this.teamName = teamName;
		correctFJAnswer = false;
	}
	public boolean getCorrectFJAnswer(){
		return correctFJAnswer;
	}
	public int getBet(){
		return bet;
	}
	
	//called from GameData to deduct or add the bet amount depending on whether they got the fj answer right
	public String addOrDeductBet(){
		
		String update = "";
		
		if (correctFJAnswer){
			addPoints(bet);
			update = teamName +" got the Final Jeopardy answer right! $"+bet+" was added to their total";
		}
		
		else{
			deductPoints(bet);
			update = teamName +" got the Final Jeopardy answer wrong! $"+bet+" was deducted from their total";
		}
		
		return update;
	}
	
	public void addPoints(int points){
		totalPoints += points;
	}
	
	public void deductPoints(int points){
		totalPoints -= points;
	}
	
	//GETTERS
	public String getTeamName(){
		return teamName;
	}

	public Integer getPoints(){
		return totalPoints;
	}
	
	public int getTeamIndex(){
		return teamIndex;
	}
	
	//comparator for sorting teams
	//it is passed into the sort method from the Java Collections class as a custom comparator
	//this will sort the team in order of their total points
	private static class PointComparator implements Comparator<TeamData>{

		@Override
		public int compare(TeamData teamOne, TeamData teamTwo) {
			return teamOne.getPoints().compareTo(teamTwo.getPoints());
		}
		
	}
	
	public static PointComparator getComparator(){
		return new PointComparator();
	}
}
