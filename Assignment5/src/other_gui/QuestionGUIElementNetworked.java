package other_gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Timer;

import frames.MainGUI;
import game_logic.GameData;
import messages.BuzzInMessage;
import messages.QuestionAnsweredMessage;
import messages.QuestionClickedMessage;
import messages.QuestionTimeMessage;
import messages.FishMessage;
import server.Client;

//inherits from QuestionGUIElement
public class QuestionGUIElementNetworked extends QuestionGUIElement {

	private Client client;
	//very similar variables as in the AnsweringLogic class
	public Boolean hadSecondChance;
	private TeamGUIComponents currentTeam;
	private TeamGUIComponents originalTeam;
	int teamIndex;
	int numTeams;
	//stores team index as the key to a Boolean indicating whether they have gotten a chance to answer the question
	private HashMap<Integer, Boolean> teamHasAnswered;
	
	//hw5 new parameters
	//used to detect whether we already had a buzzin period or not
	public boolean waitBuzzin = false;
	
	//used to detect whether it was this team buzz in or not
	//if it was this team, clock ticks
	//if not, fish swims
	public boolean isBuzzinTeam = false;
	private GameTimer gt;
	private Timer timer;
	public int fishCount = 0;
	
	public QuestionGUIElementNetworked(String question, String answer, String category, int pointValue, int indexX, int indexY) {
		super(question, answer, category, pointValue, indexX, indexY);
			
	}
	
	//hw5 new functions////////////////////////
	public GameTimer getGameTimer()
	{
		return gt;
	}
	
	public Timer getTimer()
	{
		return timer;
	}
	////////////////////////////////////////////
	
	//set the client and also set the map with the booleans to all have false
	public void setClient(Client client, int numTeams){
		this.client = client;
		this.numTeams = numTeams;
		teamIndex = client.getTeamIndex();
		teamHasAnswered = new HashMap<>();
		for (int i = 0; i< numTeams; i++) teamHasAnswered.put(i, false);
	}
	
	//returns whether every team has had a chance at answering this question
	public Boolean questionDone(){
		Boolean questionDone = true;
		for (Boolean currentTeam : teamHasAnswered.values()) questionDone = questionDone && currentTeam;
		return questionDone;
	}
	
	//overrides the addActionListeners method in super class
	@Override
	public void addActionListeners(MainGUI mainGUI, GameData gameData){
		passButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//send a buzz in message
				client.sendMessage(new BuzzInMessage(teamIndex));
				
			}
			
		});
		
		gameBoardButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//send a question clicked message
				client.sendMessage(new QuestionClickedMessage(getX(), getY()));
				
				//initialize clock///////////////////////////////////////////
				gt = new GameTimer(20);
				ActionListener timerListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent e) 
					{
						gt.currentImage++;
						client.sendMessage(new QuestionTimeMessage(getX(), getY(), gt.currentImage));
						
						setTimeLabel(gt.currentImage);
						
						//time is up!
						if(gt.currentImage == gt.totalImage)
						{
							if(waitBuzzin == false)
							{
								//just send some wrong answer so that we can enter into buzzin period
								client.sendMessage(new QuestionAnsweredMessage("what is whateverWrong"));
							}
							else
							{
								client.sendMessage(new QuestionAnsweredMessage("No more buzzin"));
								timer.stop();
							}
							gt.reset();
						}							 
					}
				};
				timer = new Timer(1000, timerListener);
				//////////////////////////////////////////////////////////////////////
				
				//initialize fish//////////////////////////////////////////////////////
				ActionListener fishListener = new ActionListener()
				{
					public void actionPerformed(ActionEvent e) 
					{
						fishCount++;
						if(fishCount == 8)//only 8 fish pics 
						{
							fishCount = 0;	
						}
							
						if(isBuzzinTeam==true)
						{
							client.sendMessage(new FishMessage(fishCount));	
						}
						else
						{
							announcementsLabel.setIcon(null);		
						}
					}
				 };		
				Timer fishTimer = new Timer(80, fishListener);
				////////////////////////////////////////////////////////////////////////
				
				fishTimer.start();
				timer.start();
			  
			}
		});
		
		submitAnswerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//send question answered message
				client.sendMessage(new QuestionAnsweredMessage(answerField.getText()));
			
			}
			
		});
	}
	
	//override the resetQuestion method
	@Override
	public void resetQuestion(){
		super.resetQuestion();
		hadSecondChance = false;
		currentTeam = null;
		originalTeam = null;
		teamHasAnswered.clear();
		//reset teamHasAnswered map so all teams get a chance to anaswer again
		for (int i = 0; i< numTeams; i++) teamHasAnswered.put(i, false);
	}
	
	@Override
	public void populate(){
		super.populate();
		passButton.setText("Buzz In!");
	}
	
	public int getOriginalTeam(){
		return originalTeam.getTeamIndex();
	}

	public void updateAnnouncements(String announcement){
		announcementsLabel.setText(announcement);
	}
	
	public void setOriginalTeam(int team, GameData gameData){
		originalTeam = gameData.getTeamDataList().get(team);
		updateTeam(team, gameData);
	}
	
	//update the current team of this question
	public void updateTeam(int team, GameData gameData){
		currentTeam = gameData.getTeamDataList().get(team);
		passButton.setVisible(false);
		answerField.setText("");
		//if the current team is this client
		if (team == teamIndex){
			AppearanceSettings.setEnabled(true, submitAnswerButton, answerField);
			announcementsLabel.setText("Answer within 20 seconds");
		}
		//if the current team is an opponent
		else{
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField);
			announcementsLabel.setText(currentTeam.getTeamName()+" buzz in to answer");
		}
		//mark down that this team has had a chance to answer
		teamHasAnswered.replace(team, true);
		hadSecondChance = false;
		teamLabel.setText(currentTeam.getTeamName());
	}
	
	//called from QuestionAnswerAction when there is an illformated answer
	public void illFormattedAnswer(){
		
		if (currentTeam.getTeamIndex() == teamIndex){
			announcementsLabel.setText("You had an illformatted answer. Please try again");
		}
		else{
			announcementsLabel.setText(currentTeam.getTeamName()+" had an illformatted answer. They get to answer again.");
		}
	}
	
	//set the gui to be a buzz in period, also called from QuestionAnswerAction
	public void setBuzzInPeriod(){
		
		passButton.setVisible(true);
		teamLabel.setText("");
		waitBuzzin = true;
		if (teamHasAnswered.get(teamIndex)){
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField, passButton);
			announcementsLabel.setText("It's time to buzz in! But you've already had your chance..");
		}
		else{
			announcementsLabel.setText("Buzz in to answer the question!");
			passButton.setEnabled(true);
			AppearanceSettings.setEnabled(false, submitAnswerButton, answerField);
		}
	}
	
	
	public TeamGUIComponents getCurrentTeam(){
		return currentTeam;
	}
}
