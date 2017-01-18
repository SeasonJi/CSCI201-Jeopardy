package other_gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import frames.MainGUI;
import frames.WinnersAndRatingGUI;
import game_logic.GameData;

//JPanel that contains the final jeopardy gui elements and functionality
public class FinalJeopardyGUI extends JPanel {

	private JLabel jeopardyQuestion;
	private GameData gameData;
	private int numTeamsBet;
	private int numTeamsAnswered;
	private MainGUI mainGUI;
	
	//hw4 new components
	private Vector<JLabel> betLabels;
	private Vector<JPanel> betPanels;
	private Vector<JPanel> betOuterPanels;
	private JLabel notifyLabel;
	
	public FinalJeopardyGUI(GameData gameData, MainGUI mainGUI)
	{	
		this.gameData = gameData;
		this.mainGUI = mainGUI;
		numTeamsBet = 0;
		numTeamsAnswered = 0;
		
		initialize();
		createGUI();
		
	}
	//public methods
	//called every time a 'Set Bet' button is pressed
	public void increaseNumberOfBets(String update){
		//increase the number of teams that have made their bet
		numTeamsBet++;
		mainGUI.addUpdate(update);
		
		if (allTeamsBet()){
			
			mainGUI.addUpdate("All teams have bet! The Final Jeopardy question is: "+"\n"+gameData.getFinalJeopardyQuestion());
			//display jeopardy question
			jeopardyQuestion.setText(gameData.getFinalJeopardyQuestion());
			//enabling all of the 'Submit Answer' buttons
			for (TeamGUIComponents team : gameData.getTeamDataList()){
				team.getFJAnswerButton().setEnabled(true);
			}
		}
	}
	
	public void increaseNumberOfAnswers(String name){
		numTeamsAnswered++;
		//checks to see if all teams have answered the question
		if (allTeamsAnswered())
		{
			mainGUI.addUpdate("All teams have answered. The Final Jeopardy answer is: "+gameData.getFinalJeopardyAnswer());
			gameData.addOrDeductTeamBets(mainGUI);
			notifyLabel.setVisible(false);
			
			new WinnersAndRatingGUI(gameData).setVisible(true);
		}
	}

	//returns a boolean indicating whether all teams have made their bet
	public Boolean allTeamsBet(){
		return numTeamsBet == gameData.getFinalistsAndEliminatedTeams().getFinalists().size();
	}
	
	//returns a boolean indication whether all teams have answered the final jeopardy question
	public Boolean allTeamsAnswered(){
		return numTeamsAnswered == gameData.getFinalistsAndEliminatedTeams().getFinalists().size();
	}

	//private methods
	//all other GUI components we will reference from the TeamGUIComponents objects
	private void initialize(){
		jeopardyQuestion = new JLabel("Wait for it...");
		notifyLabel = new JLabel("Waiting for the rest of the teams to answer...");
		notifyLabel.setForeground(Color.GRAY);
		notifyLabel.setFont(AppearanceConstants.fontMedium);
		
		//hw4 new components
		betPanels = new Vector<JPanel>(gameData.getFinalistsAndEliminatedTeams().getFinalists().size());
		betLabels = new Vector<JLabel>(gameData.getFinalistsAndEliminatedTeams().getFinalists().size());
		betOuterPanels = new Vector<JPanel>(gameData.getFinalistsAndEliminatedTeams().getFinalists().size());

	}
	
	private void createGUI(){
		if(mainGUI.getTeamName().equals("none"))
		{
			setLayout(new GridLayout(gameData.getFinalistsAndEliminatedTeams().getFinalists().size() + 3, 1));
		}
		else
		{
			setLayout(new GridLayout(gameData.getFinalistsAndEliminatedTeams().getFinalists().size() + 6, 1));	
		}
		//local variables
		JPanel answerPanel = new JPanel(new GridLayout(2, 2));
		JPanel questionPanel = new JPanel();
		JPanel titlePanel = new JPanel();
		
		JLabel titleLabel = new JLabel("Final Jeopardy Round");
		
		for(int i = 0;i < gameData.getFinalistsAndEliminatedTeams().getFinalists().size(); i++)
		{
			JLabel betLabel = new JLabel("waiting for " + gameData.getFinalistsAndEliminatedTeams().getFinalists().get(i).getTeamName()+" to set their bet");
			betLabel.setBackground(Color.darkGray);
			betLabel.setOpaque(true);
			betLabel.setForeground(Color.lightGray);
			betLabel.setFont(AppearanceConstants.fontSmall);
			AppearanceSettings.setTextAlignment(betLabel);
			JPanel betPanel = new JPanel();
			
			AppearanceSettings.setBackground(Color.darkGray, betPanel);
			betPanel.add(betLabel);
			betLabels.add(betLabel);			
			betOuterPanels.add(betPanel);
		}
		
		//setting appearance of components
		AppearanceSettings.setBackground(Color.darkGray, answerPanel, notifyLabel, this);
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, questionPanel, jeopardyQuestion);
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, titlePanel, titleLabel);
		AppearanceSettings.setTextAlignment(jeopardyQuestion,notifyLabel, titleLabel);
		//other appearance settings
		titleLabel.setForeground(Color.lightGray);
		titleLabel.setOpaque(true);
		titleLabel.setFont(AppearanceConstants.fontLarge);
		
		jeopardyQuestion.setForeground(Color.darkGray);
		jeopardyQuestion.setFont(AppearanceConstants.fontMedium);
		jeopardyQuestion.setPreferredSize(new Dimension(1000, 50));				

		titlePanel.add(titleLabel);
		titlePanel.setPreferredSize(new Dimension(1000, 70));			

		//add the panel with the FJ title
		add(titlePanel);
		//iterate over the final teams and add their gui components to the panels
		for (int i = 0; i < gameData.getFinalistsAndEliminatedTeams().getFinalists().size(); i++){
			
			JPanel teamAnswerPanel = new JPanel(new BorderLayout());
			JPanel teamBetPanel = new JPanel(new BorderLayout());
			
			JPanel betLabelAndButtonPanel = new JPanel(new GridLayout(1, 2));
			JPanel sliderPanel = new JPanel(new GridLayout(1, 2));
			
			TeamGUIComponents team = gameData.getFinalistsAndEliminatedTeams().getFinalists().get(i);
			
			//pass in the mainGUI
			team.passMainGUI(mainGUI);
			//initialize the team's slider based on their total, and add action listeners to its buttons
			team.prepareForFinalJeopardy(this, gameData);
			
			sliderPanel.setPreferredSize(new Dimension(800, 100));
			teamAnswerPanel.setPreferredSize(new Dimension(500, 60));
			AppearanceSettings.setBackground(Color.darkGray, betLabelAndButtonPanel, sliderPanel, teamAnswerPanel, teamBetPanel);

			//create/add this team's answer panel
			teamAnswerPanel.add(team.getFJAnswerTextField(), BorderLayout.CENTER);
			teamAnswerPanel.add(team.getFJAnswerButton(), BorderLayout.EAST);
			
			sliderPanel.add(team.getBetSlider());
			sliderPanel.add(betLabelAndButtonPanel);
			
			betLabelAndButtonPanel.add(team.getBetLabel());
			betLabelAndButtonPanel.add(team.getBetButton());
			
			teamBetPanel.add(team.getBetSlider(), BorderLayout.CENTER);
			teamBetPanel.add(betLabelAndButtonPanel, BorderLayout.EAST);
			teamBetPanel.add(team.getFJTeamNameLabel(), BorderLayout.WEST);
			
			
			//add this team's bet panel
			if(mainGUI.getTeamName().equals("none"))//not networked
			{
				add(teamBetPanel);
				answerPanel.add(teamAnswerPanel);
			}
			else//networked, just show client's panel
			{	
				if(mainGUI.getTeamName().equals(gameData.getFinalistsAndEliminatedTeams().getFinalists().get(i).getTeamName()))
				{
					answerPanel.add(teamAnswerPanel);
				}
			}
			betPanels.add(teamBetPanel);
			
		}
		
		if(!mainGUI.getTeamName().equals("none"))//not networked
		{	
			for(int i = 0; i<betPanels.size(); i++)
			{
				if(mainGUI.getTeamName().equals(gameData.getFinalistsAndEliminatedTeams().getFinalists().get(i).getTeamName()))
				{
					add(betPanels.get(i));
				}
			}
		
			for(int i = 0; i < betOuterPanels.size(); i++)
			{
				add(betOuterPanels.get(i));	
				if(mainGUI.getTeamName().equals(gameData.getFinalistsAndEliminatedTeams().getFinalists().get(i).getTeamName()))
				{
					betLabels.get(i).setText("My team bet $??");	
				}
			}	
		}
		
		questionPanel.add(jeopardyQuestion);
		//panel with the FJ question
		add(questionPanel);
		//panel with all the submit answer buttons and text fields
		add(answerPanel);
		
		if(!mainGUI.getTeamName().equals("none"))
		{
			add(notifyLabel);
		}
	}
	
	//hw4 new functions
	public void getBetSet(String name, int bet)
	{	
		if(!mainGUI.getTeamName().equals(name))
		{
			increaseNumberOfBets(name + " bet $" + bet);
		}
		for(int i = 0; i < betPanels.size(); i++)
		{
			if(name.equals(gameData.getFinalistsAndEliminatedTeams().getFinalists().get(i).getTeamName()))
			{
				betLabels.get(i).setText(name + " bet $" + bet);
			}
		}
	}
	
}
