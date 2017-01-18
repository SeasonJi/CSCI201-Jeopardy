package frames;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import game_logic.ServerGameData;
import game_logic.User;
import listeners.NetworkedWindowListener;
import messages.PlayerLeftMessage;
import messages.RestartGameMessage;
import other_gui.FinalJeopardyGUINetworked;
import other_gui.GameTimer;
import other_gui.QuestionGUIElement;
import other_gui.QuestionGUIElementNetworked;
import other_gui.TeamGUIComponents;
import server.Client;

//used only for a networked game and inherits from MainGUI
public class MainGUINetworked extends MainGUI {

	private Client client;
	//has a networked game data
	private ServerGameData serverGameData;
	//had a networked FJ panel that I need as a memeber variable
	private FinalJeopardyGUINetworked fjGUI;
	
	//hw5 new components
	private Timer timer;
	private GameTimer jt;	
	
	
	public MainGUINetworked(ServerGameData gameData, Client client, User loggedInUser) {
		super(loggedInUser);
		this.serverGameData = gameData;
		this.client = client;
		//calls a method in MainGUI that basically acts as a constructor
		//since you can only call the super class's constructor as the first line of the child constructor,
		//but I need to have serverGameData initialized before I can cosntruct my GUI, this is the solution
		make(gameData);
		usernameLabel.setText(gameData.getTeamName(client.getTeamIndex()));
		
		//hw5 new TODO
		jt = new GameTimer(15);
		clockLabels = new JLabel[gameData.getNumberOfTeams()];
		addClockPicToPanel();		
	    ActionListener timerListener = new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				jt.setClock(clockLabels[gameData.getCurrentTeam().getTeamIndex()]);
				setJeopardyLabel(jt.currentImage);
				if(jt.currentImage == jt.totalImage)
				{
					changeClock();
					jt.reset();
				}				
				
			}
		};
		timer = new Timer(1000, timerListener);
	    timer.start();
	}
	
	//hw5 new functions
	//let next team play
	public void changeClock()
	{
		clockPanels.get(gameData.getCurrentTeam().getTeamIndex()).setVisible(false);
		addUpdate("Team "+gameData.getCurrentTeam().getTeamName()+" didn't choose a question promptly! Now next team!");
		
		gameData.nextTurn();
		showMainPanel();
		
		addUpdate("It is Team "+gameData.getCurrentTeam().getTeamName()+" 's turn to choose a question");
	
		clockPanels.get(gameData.getCurrentTeam().getTeamIndex()).setVisible(true);
	}
	
	public ImageIcon scaleImage(Image in, int w, int h)
	{
		Image temp = in.getScaledInstance(w, h,  java.awt.Image.SCALE_DEFAULT);
		ImageIcon finalIcon = new ImageIcon(temp);

	    return finalIcon;
	}
	
	public void addClockPicToPanel()
	{
		for(int i = 0; i < gameData.getNumberOfTeams(); i++)
		{
			Image temp = null;
			try 
			{
				temp = ImageIO.read(new File("images/clockAnimation/frame_" + 0+ "_delay-0.06s.jpg"));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			ImageIcon icon = scaleImage(temp, 100, 100);
			clockLabels[i] = new JLabel(icon);
			clockPanels.get(i).add(clockLabels[i]);
			//initially invisible
			clockPanels.get(i).setVisible(false);
			
		}
		//set current team's clock visible
		clockPanels.get(gameData.getCurrentTeam().getTeamIndex()).setVisible(true);
	}
	
	public void setJeopardyLabel(int timePassed)
	{
		this.jeopardyLabel.setText("Jeopardy :"+ (15-timePassed));
	}

	public Timer getTimer()
	{
		return timer;
	}
	////////////////////////////////////////////////////////////////////////////////////
	
	public FinalJeopardyGUINetworked getFJGUI(){
		return fjGUI;
	}
	
	//disables all enabled buttons to have enabled icon
	public void disableAllButtons(){
		for (QuestionGUIElement question : gameData.getQuestions()){
			if (!question.isAsked()){
				question.getGameBoardButton().setDisabledIcon(QuestionGUIElement.getEnabledIcon());
				question.getGameBoardButton().setEnabled(false);
			}
		}
	}
	
	//enables all questions that have not been chosen
	public void enableAllButtons(){
		for (QuestionGUIElement question : gameData.getQuestions()){
			if (!question.isAsked()){
				question.getGameBoardButton().setIcon(QuestionGUIElement.getEnabledIcon());
				question.getGameBoardButton().setEnabled(true);
			}
		}
	}
	
	//depending on whether the current team is same at the client's team index, we enable or disable all buttons
	//override showMainPanel from super class in order to always check if we should enable.disbale buttons
	@Override
	public void showMainPanel() 
	{
		//reset
		jt.reset();
		timer.start();
		
		if (gameData.getCurrentTeam().getTeamIndex() != client.getTeamIndex()) disableAllButtons();
		
		else enableAllButtons();
		
		super.showMainPanel();
	}
	
	//override from super class; only add the restart option if the client is the host
	@Override
	protected void createMenu() {

		if (client.isHost()){
			menu.add(restartThisGameButton);
		}
	
		menu.add(chooseNewGameFileButton);
		menu.add(logoutButton);
		menu.add(exitButton);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}
	
	//in the non networked game, this logic happens in the AnsweringLogic class in the QuestionGUIElement
	//but we need to be able to call this from QuestionAnsweredAction class
	public void startFinalJeopardy(){
		gameData.disableRemainingButtons();
		addUpdate("It's time for Final Jeopardy!");
		gameData.determineFinalists();
		//if no one made it show the main panel and show the rating window
		if (gameData.getFinalistsAndEliminatedTeams().getFinalists().size() == 0){
			showMainPanel();
			new WinnersAndRatingGUINetworked(serverGameData, this, client, true).setVisible(true);
		}
		else{
			//if this client did not make it to FJ, show the rating window
			if (gameData.getFinalistsAndEliminatedTeams().getElimindatedTeamsIndices().contains(client.getTeamIndex())){
				showMainPanel();
				client.setElimindated(true);
				new WinnersAndRatingGUINetworked(serverGameData, this, client, false).setVisible(true);
			}
			// create and store a networked fjpanel and switch to it
			else{
				fjGUI = new FinalJeopardyGUINetworked(serverGameData, this, client);
				changePanel(fjGUI);
			}
		}

	}
	
	//sets the bet for the provided team with the provided bet amount, called from SetBetAction class
	public void setBet(int team, int bet){
		TeamGUIComponents teamData = serverGameData.getTeam(team);
		teamData.setBet(bet);
		fjGUI.updateTeamBet(teamData);
	}
	
	//since we serialize over the gameData with all GUI objects transient, we need to repopulate them on the client side
	//we override this from the super class in order to add different action listeners to the question object
	//and so we can iterate over the networked questions instead
	@Override
	protected void populateQuestionButtons(){
		for (int x = 0; x<QUESTIONS_LENGTH_AND_WIDTH; x++){
			for (int y = 0; y<QUESTIONS_LENGTH_AND_WIDTH; y++){
				QuestionGUIElementNetworked question = serverGameData.getNetworkedQuestions()[x][y];
				question.setClient(client, gameData.getNumberOfTeams());
				question.addActionListeners(this, serverGameData);
				questionButtons[question.getX()][question.getY()] = question.getGameBoardButton();
			}
		}

	}
	// adding event listeners, override from MainGUI
	@Override
	protected void addListeners() {

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//add window listener
		addWindowListener(new NetworkedWindowListener(client, MainGUINetworked.this));
		//add action listeners
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendPlayerLeftMessage();
				System.exit(0);
			}
		});

		restartThisGameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//choose a different team to start the game
				gameData.chooseFirstTeam();
				client.sendMessage(new RestartGameMessage(gameData.getCurrentTeam().getTeamIndex()));
			}
		});

		chooseNewGameFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendPlayerLeftMessage();
				new StartWindowGUI(loggedInUser).setVisible(true);
			}
		});
		
		logoutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				sendPlayerLeftMessage();
				new LoginGUI();
			}
		});
	}
	
	private void sendPlayerLeftMessage(){
		client.sendMessage(new PlayerLeftMessage(client.getTeamIndex()));
		client.close();
		dispose();
	}

}
