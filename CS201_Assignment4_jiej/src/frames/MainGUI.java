package frames;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import client.GameClient;
import game_logic.Category;
import game_logic.CurrentQuestion;
import game_logic.GameData;
import game_logic.User;
import listeners.ExitWindowListener;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;
import other_gui.FinalJeopardyGUI;
import other_gui.QuestionGUIElement;
import other_gui.TeamGUIComponents;
import client.Message;
import server.ServerListener;

public class MainGUI extends JFrame {
	
	//hw4 new parameters
	private GameClient gc;
	private String teamName;
	public FinalJeopardyGUI FJGUI;
	private int totalAnsweredTeams = 0;

	private JPanel mainPanel;
	private JPanel currentPanel;

	private JPanel questionsPanel;
	private GameData gameData;
	private JButton[][] questionButtons;

	private static final int QUESTIONS_LENGTH_AND_WIDTH = 5;

	private JTextArea updatesTextArea;
	private JMenuBar menuBar;
	private JMenu menu;
	
	private JMenuItem logoutButton;
	private JMenuItem exitButton;
	private JMenuItem restartThisGameButton;
	private JMenuItem chooseNewGameFileButton;
	//in case we need to know which user is logged in
	private User loggedInUser;
	
	public MainGUI(GameData gd, User user, String name, GameClient client) 
	{
		super("Jeopardy!!");
		
		gameData = gd;
		loggedInUser = user;		
		teamName = name;
		gc = client;	
		
		initializeComponents();
		createGUI();
		addListeners();
	}

	//hw4 new functions
	public String getTeamName()
	{
		return teamName;
	}
	
	public GameClient getGameClient()
	{
		return gc;
	}
	
	public void update(String in,int point,boolean buffer)
	{
		 Message m = new Message();
		 
		 m.isUpdate = true;
		 m.updateString = in;
		 m.updateScore = point;
		 m.isRightOrWrong = buffer;
		 gc.sendMessage(m);
	}
	
	public void getUpdate(String in, int point,boolean buffer)
	{
		if(!gameData.getCurrentTeam().getTeamName().equals(getTeamName()))
		{
			if(buffer)
			{
				gameData.getCurrentTeam().addPoints(point);
				addUpdate(in);
			}
			else
			{
				gameData.getCurrentTeam().deductPoints(point);
				addUpdate(in);
			}
		}
	}
	
	public void updateToAllClients(String name, String in, int score, boolean buffer)
	{
		 Message m = new Message();
		 
		 m.isUpdateToAllClients = true;
		 m.teamName = name;
		 m.updateString = in;
		 m.updateScore = score;
		 m.isRightOrWrong = buffer;		
		 gc.sendMessage(m);
	}
	
	public void getUpdateToAllClients(String name,String in, int point,boolean buffer)
	{
		addUpdate(in);	
		totalAnsweredTeams++;	
		for (TeamGUIComponents teamData : gameData.getFinalistsAndEliminatedTeams().getFinalists())
		{	
			if(teamData.getTeamName().equals(name))
			{
				if(buffer)
				{	
					teamData.addPoints(point);				
				}
				else
				{
					teamData.deductPoints(point);				
				}
				teamData.updatePointsLabel();
			}
		}
	}
	
	//buzz in functions!
	public void buzzIn(int x, int y, int totalAnsweredTeams)
	{
		
		for (QuestionGUIElement q : gameData.getQuestions()) 
		{
			if(questionButtons[x][y] == q.getGameBoardButton())
			{
				q.buzzIn(totalAnsweredTeams);
			}
		}
	}
	
	public void sendBuzzIn(int x, int y, int totalAnsweredTeams)
	{
		Message m = new Message();
		
		m.isBuzzIn = true;
		m.setCurrentQuestion(x, y);
		m.howManyTeamAnswered = totalAnsweredTeams;
		gc.sendMessage(m);
	}
	
	public void getBuzzInTeam(int x, int y, String name)
	{
		int whichTeam = -1 ;
		//loop to find the team
		for(int i=0;i<gameData.getTeamDataList().size();i++)
		{
			if(gameData.getTeamDataList().get(i).getTeamName() == name)
			{
				whichTeam = i;
			}
		}		
		gameData.setNextTurn(whichTeam);

		addUpdate(name + " buzz in to answer");
		
		QuestionGUIElement q1 = null;
		//find the correpsonding question
		for (QuestionGUIElement q : gameData.getQuestions()) 
		{
			if(questionButtons[x][y] == q.getGameBoardButton())
			{
				q1 = q;			
			}
		}
		
		//if in the current replying team's window, hide passbutton, else hide buzzin button
		if(this.getTeamName().equals(name))
		{
			q1.hidePass(whichTeam);						
		}
		else
		{
		    q1.hideBuzzIn(whichTeam);
		}
		
	}
	public void sendBuzzInTeam(String teamName, int x, int y)
	{
		 Message m = new Message();
		 
		 m.isBuzzInTeam = true;
		 m.buzzInTeamName = teamName;
		 m.setCurrentQuestion(x, y);
		 gc.sendMessage(m);
	}
	//////////////////////////////////////////////////////////////
	
	public void continuePlay(int x, int y)
	{
		showMainPanel();
		for (QuestionGUIElement question : gameData.getQuestions()) 
		{
			if(questionButtons[x][y] == question.getGameBoardButton())
			{
				question.correctAnswer();
			}
		}
	}
	
	public void sendContinuePlay(int x, int y)
	{
		
		 Message m = new Message();
		 
		 m.isContinuePlay = true;
		 m.setCurrentQuestion(x, y);
	     gc.sendMessage(m);
	}
	
	public void sendCurrentQuestion(int x, int y)
	{
		 Message m = new Message();
		 
		 m.isCurrentQuestion = true;
		 m.setCurrentQuestion(x, y);
	     gc.sendMessage(m);
	}
	
	public void nextQuestion(int x, int y, int originalTurn)
	{
		showMainPanel();
		gameData.setNextTurn(originalTurn);
		
		for (QuestionGUIElement question : gameData.getQuestions()) 
		{
			if(questionButtons[x][y] == question.getGameBoardButton())
			{
				question.incorrectAnswer();
			}
		}
	}
	
	public void sendNextQuestion(int x, int y,int originalTurn)
	{
		 Message m = new Message();
		 
		 m.isNextQuestion = true;
		 m.setCurrentQuestion(x, y);
		 m.originalTurn = originalTurn;
	     gc.sendMessage(m);
	}
	
	
	public void changeToQuestionPanel(int x, int y)
	{
		for (QuestionGUIElement question : gameData.getQuestions())
		{
			if(questionButtons[x][y] == question.getGameBoardButton())
			{
				question.changeToQuestionGUI();
			}
		}
	}
	
	public void setFinalJeopardyGUI(FinalJeopardyGUI fj)
	{
		 FJGUI = fj;
	}

	public void sendFinalJeopardyReady(int x, int y, String name)
	{
		 Message m = new Message();

		 m.isReadyForFinalJeopardy = true;
		 m.setCurrentQuestion(x, y);
		 m.teamName = name;
	     gc.sendMessage(m);
	}
	public void finalJeopardyReady(int x, int y, String name)
	{
		for (QuestionGUIElement question : gameData.getQuestions())
		{
			if(questionButtons[x][y] == question.getGameBoardButton())
			{
				question.finalJeopardyReady(name);
			}
		}
	}
	
	//original public methods////////////////////////////////////////////////////////////	
	public void addUpdate(String update) 
	{
		updatesTextArea.append("\n" + update);
	}

	// this method changes the current panel to the provided panel
	public void changePanel(JPanel panel) 
	{
		remove(currentPanel);
		currentPanel = panel;
		add(currentPanel, BorderLayout.CENTER);
		// must repaint or the change won't show
		repaint();
		revalidate();
	}

	public void showMainPanel()
	{
		changePanel(mainPanel);
	}

	// private methods
	private void initializeComponents() {
		mainPanel = new JPanel();
		currentPanel = mainPanel;
		exitButton = new JMenuItem("Exit Game");
		restartThisGameButton = new JMenuItem("Restart This Game");
		chooseNewGameFileButton = new JMenuItem("Choose New Game File");
		logoutButton = new JMenuItem("Logout");
		updatesTextArea = new JTextArea("Welcome to Jeopardy!");
		menu = new JMenu("Menu");
		questionButtons = new JButton[QUESTIONS_LENGTH_AND_WIDTH][QUESTIONS_LENGTH_AND_WIDTH];
		menuBar = new JMenuBar();
		questionsPanel = new JPanel(new GridLayout(QUESTIONS_LENGTH_AND_WIDTH, QUESTIONS_LENGTH_AND_WIDTH));
	}
	private void createGUI() {

		createMenu();
		createMainPanel();

		add(mainPanel, BorderLayout.CENTER);
		add(createProgressPanel(), BorderLayout.EAST);
		setSize(1500, 825);
	}

	// creating the JMenuBar
	private void createMenu() {

		menu.add(restartThisGameButton);
		menu.add(chooseNewGameFileButton);
		menu.add(logoutButton);
		menu.add(exitButton);
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}

	// creating the main panel (the game board)
	private void createMainPanel() {
		mainPanel.setLayout(new BorderLayout());

		// getting the panel that holds the 'jeopardy' label
		JPanel jeopardyPanel = createJeopardyPanel();
		// getting the cateogries panel
		JPanel categoriesPanel = createCategoriesAndQuestionsPanels();
		JPanel northPanel = new JPanel();

		northPanel.setLayout(new BorderLayout());
		northPanel.add(jeopardyPanel, BorderLayout.NORTH);
		northPanel.add(categoriesPanel, BorderLayout.SOUTH);

		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(questionsPanel, BorderLayout.CENTER);
	}

	// creates the panel with the jeopardy label
	private JPanel createJeopardyPanel() {
		JPanel jeopardyPanel = new JPanel();
		JLabel jeopardyLabel = new JLabel("Jeopardy");
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, jeopardyPanel, jeopardyLabel);
		jeopardyLabel.setFont(AppearanceConstants.fontLarge);
		jeopardyPanel.add(jeopardyLabel);

		return jeopardyPanel;
	}
    
	// creates both the categories panel and the questions panel
	private JPanel createCategoriesAndQuestionsPanels() {
		JPanel categoriesPanel = new JPanel(new GridLayout(1, QUESTIONS_LENGTH_AND_WIDTH));
		AppearanceSettings.setBackground(Color.darkGray, categoriesPanel, questionsPanel);

		Map<String, Category> categories = gameData.getCategories();
		JLabel[] categoryLabels = new JLabel[QUESTIONS_LENGTH_AND_WIDTH];

		// iterate through the map of categories, and place them in the correct index
		for (Map.Entry<String, Category> category : categories.entrySet()) {
			categoryLabels[category.getValue().getIndex()] = category.getValue().getCategoryLabel();
		}

		// place the question buttons in the proper indices in 'questionButtons'
		for (QuestionGUIElement question : gameData.getQuestions()) {

			// adding action listeners to the question
			question.addActionListeners(this, gameData);
			questionButtons[question.getX()][question.getY()] = question.getGameBoardButton();
		}

		// actually adding the categories and questions into their respective panels
		for (int i = 0; i < QUESTIONS_LENGTH_AND_WIDTH; i++) {

			categoryLabels[i].setPreferredSize(new Dimension(100, 70));
			categoryLabels[i].setIcon(Category.getIcon());
			categoriesPanel.add(categoryLabels[i]);

			for (int j = 0; j < QUESTIONS_LENGTH_AND_WIDTH; j++) {
				// have to use opposite indices because of how GridLayout adds components
				questionsPanel.add(questionButtons[j][i]);
			}
		}

		return categoriesPanel;
	}

	// creates the panel with the team points, and the Game Progress area
	private JPanel createProgressPanel() {
		// create panels
		JPanel pointsPanel = new JPanel(new GridLayout(gameData.getNumberOfTeams(), 2));
		JPanel southEastPanel = new JPanel(new BorderLayout());
		JPanel eastPanel = new JPanel();
		// other local variables
		JLabel updatesLabel = new JLabel("Game Progress");
		JScrollPane updatesScrollPane = new JScrollPane(updatesTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// setting appearances
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, southEastPanel, updatesLabel, updatesScrollPane,
				updatesTextArea);
		AppearanceSettings.setSize(400, 400, pointsPanel, updatesScrollPane);
		AppearanceSettings.setTextComponents(updatesTextArea);

		updatesLabel.setFont(AppearanceConstants.fontLarge);
		pointsPanel.setBackground(Color.darkGray);
		updatesLabel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		updatesScrollPane.setBorder(null);

		updatesTextArea.setText("Welcome to Jeopardy!");
		updatesTextArea.setFont(AppearanceConstants.fontSmall);
		updatesTextArea.append("The team to go first will be " + gameData.getCurrentTeam().getTeamName());

		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));
		// adding components/containers
		southEastPanel.add(updatesLabel, BorderLayout.NORTH);
		southEastPanel.add(updatesScrollPane, BorderLayout.CENTER);

		// adding team labels, which are stored in the TeamGUIComponents class,
		// to the appropriate panel
		for (int i = 0; i < gameData.getNumberOfTeams(); i++) {
			TeamGUIComponents team = gameData.getTeamDataList().get(i);
			pointsPanel.add(team.getMainTeamNameLabel());
			pointsPanel.add(team.getTotalPointsLabel());
		}

		eastPanel.add(pointsPanel);
		eastPanel.add(southEastPanel);

		return eastPanel;
	}

	// adding even listeners
	private void addListeners() {

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//add window listener
		addWindowListener(new ExitWindowListener(this));
		//add action listeners
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}

		});

		restartThisGameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updatesTextArea.setText("Game has been restarted.");
				// reset all data
				gameData.restartGame();
				// repaint the board to show updated data
				showMainPanel();
				addUpdate("The team to go first will be " + gameData.getCurrentTeam().getTeamName());
			}

		});

		chooseNewGameFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new StartWindowGUI(loggedInUser).setVisible(true);
			}

		});
		
		logoutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new LoginGUI().setVisible(true);
			}
			
		});
	}

}
