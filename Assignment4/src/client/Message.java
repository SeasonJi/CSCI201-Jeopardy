package client;

import java.io.Serializable;

import game_logic.CurrentQuestion;
import game_logic.GameData;

public class Message implements Serializable{
    
	public int numberOfPlayers ;
	public String teamName;
	public GameData gameData;
	public int currentQuestionX, currentQuestionY;
	
	public String buzzInTeamName;
	public int howManyTeamAnswered;
	
	public String updateString ;
	public int updateScore;
	
	public int originalTurn;
	
	public int bet;
	
	public boolean isNumberOfPlayers = false;
	public boolean isTeamName = false;
	public boolean isGameData = false;
	public boolean isCurrentQuestion = false;
	public boolean isOnePlayerLeft = false;
	public boolean isContinuePlay = false;
	
	public boolean isBuzzIn = false;
	public boolean isBuzzInTeam = false;

	public boolean isNextQuestion =false;
	public boolean isUpdate = false;

	public boolean isRightOrWrong = false;
	
	public boolean isReadyForFinalJeopardy = false;
	public boolean isBetSet = false;
	
	public boolean isUpdateToAllClients = false;
	public boolean isFJAnswered = false;
	
	public Message()
	{
		//empty constructor! does not need to initialize anything
	}
	

	public void setCurrentQuestion(int x, int y){
		currentQuestionX = x;
		currentQuestionY = y;
	}

	
}
