package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.GameClient;
import game_logic.GameData;
import game_logic.User;
import listeners.ExitWindowListener;
import listeners.TextFieldFocusListener;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;
import other_gui.QuestionGUIElement.SubmitAnswerFocusListener;
import server.GameServer;

public class StartWindowGUI extends JFrame{
	
	private JPanel mainPanel;
	private JFileChooser fileChooser;
	private JButton fileChooserButton;
	private List<JTextField> teamNameTextFields;
	private List<JLabel> teamNameLabels;
	private static final int MAX_NUMBER_OF_TEAMS = 4;
	private int numberOfTeams;
	private JButton startGameButton;
	private JButton clearDataButton;
	private JButton exitButton;
	private JSlider slider;
	private JLabel fileNameLabel;
	private JButton logoutButton;
	private JCheckBox quickPlay;
	private Boolean haveNames;
	private Boolean haveValidFile;
	private GameData gameData;
	//logged in user
	private User loggedInUser;
	
	private JLabel explainContentLabel;	
	private JLabel numberOfTeamsLabel;
	private JLabel chooseGameFileLabel;
	
	//hw4 new components
	private JRadioButton notNetworkedButton, hostButton, joinGameButton;
	private JPanel rbPanel;
	private ButtonGroup group;
	private JTextField portField, ipField;
	private JPanel portAndIpPanel;
	private JPanel portPanel;
	private JPanel ipPanel;
	private JLabel waitLabel;
	
	private StartWindowGUI SWGUI;

	public StartWindowGUI(User user){
		
		super("Jeopardy Menu");
		loggedInUser = user;
		numberOfTeams = -1;
		haveNames = false;
		haveValidFile = false;
		
		//HW4 NEW
		SWGUI = this;
		
		initializeComponents();
		createGUI();
		addListeners();
	}
	
	public User getUser()
	{
		return loggedInUser;
	}
	
	//private methods
	private void initializeComponents(){
		
		//hw4 new components
		waitLabel = new JLabel("");		
		
		portField = new JTextField("port");
		portField.setForeground(Color.gray);
		portField.addFocusListener(new TextFieldFocusListener("port", portField));
		
		ipField = new JTextField("ip address");
		ipField.setForeground(Color.gray);
		ipField.addFocusListener(new TextFieldFocusListener("ip address", ipField));
		
		rbPanel = new JPanel();
		rbPanel.setLayout(new BoxLayout(rbPanel, BoxLayout.X_AXIS));
		notNetworkedButton = new JRadioButton("Not Networked");
		hostButton = new JRadioButton("Host Game");
		joinGameButton = new JRadioButton("Join Game");
		group = new ButtonGroup();
		group.add(notNetworkedButton);
		group.add(hostButton);
		group.add(joinGameButton);
		
		
		rbPanel.add(Box.createGlue());
		rbPanel.add(notNetworkedButton);
		rbPanel.add(Box.createGlue());
		rbPanel.add(hostButton);
		rbPanel.add(Box.createGlue());
		rbPanel.add(joinGameButton);
		rbPanel.add(Box.createGlue());
		
		notNetworkedButton.setSelected(true);
		
		notNetworkedButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//dealing with upper part
				slider.setVisible(true);
				numberOfTeamsLabel.setVisible(true);
				fileChooser.setVisible(true);
				chooseGameFileLabel.setVisible(true);	
				fileChooserButton.setVisible(true);
				AppearanceSettings.setSliders(1, MAX_NUMBER_OF_TEAMS, 1, 1, AppearanceConstants.fontSmallest, slider);
				slider.setValue(1);
				quickPlay.setVisible(true);
				
				//center part
				portPanel.setVisible(false);
				ipPanel.setVisible(false);
				fileNameLabel.setVisible(true);
				teamNameLabels.get(0).setText("Please name Team 1");
				
				//bottom part
				startGameButton.setText("Start Jeopardy");
			}
		});
		
		hostButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//dealing with upper part
				slider.setVisible(true);
				numberOfTeamsLabel.setVisible(true);
				fileChooser.setVisible(true);
				chooseGameFileLabel.setVisible(true);
				fileChooserButton.setVisible(true);
				slider.setValue(1);//reset the original slider
				AppearanceSettings.setSliders(2, MAX_NUMBER_OF_TEAMS, 2, 1, AppearanceConstants.fontSmallest, slider);//set the slider with minimal value 2
				slider.setValue(2);
				quickPlay.setVisible(true);
				
				//center part
				portPanel.setVisible(true);
				ipPanel.setVisible(false);
				fileNameLabel.setVisible(true);
				teamNameLabels.get(0).setText("Please choose a team name");

				
				//bottom part
				startGameButton.setEnabled(false);
				startGameButton.setText("Start Game");
			}
		});
		
		joinGameButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//dealing with upper part
				//AppearanceSettings.setSliders(1, MAX_NUMBER_OF_TEAMS, 1, 1, AppearanceConstants.fontSmallest, slider);
				slider.setVisible(false);
				numberOfTeamsLabel.setVisible(false);
				fileChooser.setVisible(false);
				chooseGameFileLabel.setVisible(false);	
				fileChooserButton.setVisible(false);
				quickPlay.setVisible(false);
				
				//center part
				portPanel.setVisible(true);
				ipPanel.setVisible(true);
				fileNameLabel.setVisible(false);
				teamNameLabels.get(0).setText("Please choose a team name");
				
				//bottom part
				startGameButton.setEnabled(false);
				startGameButton.setText("Join Game");
			}
		});
		
		//////////////////////////////////////////////////////////////////////
		mainPanel = new JPanel(new GridLayout(4, 1));
		fileChooser = new JFileChooser();
		teamNameTextFields = new ArrayList<>(4);
		teamNameLabels = new ArrayList<>(4);
		fileNameLabel = new JLabel("");
		logoutButton = new JButton("Logout");
		gameData = new GameData();
		//fileRating = new JLabel("");
		quickPlay = new JCheckBox("Quick Play?");
		
		for (int i = 0; i<MAX_NUMBER_OF_TEAMS; i++){
			teamNameTextFields.add(new JTextField());
			teamNameLabels.add(new JLabel("Please name Team "+(i+1)));
		}
		
		startGameButton = new JButton("Start Jeopardy");
		clearDataButton = new JButton("Clear Choices");
		exitButton = new JButton("Exit");
		fileChooserButton = new JButton("Choose File");
		slider = new JSlider();
		
	}
	
	private void createGUI(){
		//setting appearance of member variable gui components
		//setting background colors
		AppearanceSettings.setBackground(Color.darkGray, exitButton, logoutButton, clearDataButton, startGameButton, slider,
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3), fileChooserButton, waitLabel);
		
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, teamNameTextFields.get(0), teamNameTextFields.get(1), teamNameTextFields.get(2),
				teamNameTextFields.get(3));
		
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, fileNameLabel, mainPanel);
	
		//setting fonts
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, teamNameTextFields.get(0), teamNameTextFields.get(1), teamNameTextFields.get(2), teamNameTextFields.get(3),
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3), 
				fileChooserButton, fileNameLabel, exitButton, clearDataButton, logoutButton, startGameButton, waitLabel);
		
		//other
		AppearanceSettings.setForeground(Color.lightGray, exitButton, logoutButton, clearDataButton, startGameButton,
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3), fileChooserButton,
				fileNameLabel, slider, waitLabel);

		AppearanceSettings.setOpaque(exitButton, clearDataButton, logoutButton, startGameButton, slider,
				teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3), fileChooserButton);

		AppearanceSettings.setSize(180, 70, exitButton, clearDataButton, startGameButton, logoutButton);
		AppearanceSettings.setSize(150, 80, 
				teamNameTextFields.get(0), teamNameTextFields.get(1), teamNameTextFields.get(2), teamNameTextFields.get(3));
		
		
		AppearanceSettings.setSize(250, 100, teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3));
		
		AppearanceSettings.unSetBorderOnButtons(exitButton, logoutButton, clearDataButton, startGameButton, fileChooserButton);
		
		AppearanceSettings.setTextAlignment(teamNameLabels.get(0), teamNameLabels.get(1), teamNameLabels.get(2), teamNameLabels.get(3),
				fileNameLabel);

		setAllInvisible(teamNameTextFields, teamNameLabels);
		//check box settings
		quickPlay.setFont(AppearanceConstants.fontSmallest);
		quickPlay.setHorizontalTextPosition(SwingConstants.LEFT);
		quickPlay.setPreferredSize(new Dimension(200, 30));
		
		//file chooser settings
		fileChooser.setPreferredSize(new Dimension(400, 500));
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
		
		//slider settings
		AppearanceSettings.setSliders(1, MAX_NUMBER_OF_TEAMS, 1, 1, AppearanceConstants.fontSmallest, slider);
		slider.setSnapToTicks(true);
		slider.setPreferredSize(new Dimension(500, 50));
		startGameButton.setEnabled(false);

		createMainPanel();
		
		setBackground(AppearanceConstants.darkBlue);
		add(mainPanel, BorderLayout.CENTER);
		setSize(800, 825);
	}
	
	//sets the label and textField visible again
	private void setVisible(JLabel label, JTextField textField){
		//the first text field is always shown so we can use their border 
		Border border = teamNameTextFields.get(0).getBorder();
		
		textField.setBackground(AppearanceConstants.lightBlue);
		textField.setForeground(Color.black);
		textField.setBorder(border);

		label.setBackground(Color.darkGray);
		label.setForeground(Color.lightGray);
	}
	//I wanted to user BoxLayout for resizability but if you simply set a components invisble with
		// setVisible(false), it changes the size of the components that are visible. This is my way aroung that
	private void setInvisible(JLabel label, JTextField textField){
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, textField, label);
		AppearanceSettings.setForeground(AppearanceConstants.darkBlue, textField, label);
		textField.setBorder(AppearanceConstants.blueLineBorder);
	}
	//used in the constructor to set everything invisible (except the first label and text field)
	private void setAllInvisible(List<JTextField> teamNameTextFields, List<JLabel> teamNameLabels){
		
		for (int i = 1; i<4; i++) setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
	}
	
	private void createMainPanel(){
		//initialize local panels
		JPanel teamNamesPanel = new JPanel();
		JPanel teamLabelsPanel1 = new JPanel();
		JPanel teamLabelsPanel2 = new JPanel();
		JPanel teamTextFieldsPanel1 = new JPanel();
		JPanel teamTextFieldsPanel2 = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel teamsAndFilePanel = new JPanel();
		JPanel numberOfTeamsPanel = new JPanel();
		JPanel fileChooserPanel = new JPanel();
		JPanel northPanel = new JPanel();
		JPanel welcomePanel = new JPanel(new BorderLayout());
		JPanel titlePanel = new JPanel(new BorderLayout());
		//initialize labels
		JLabel welcomeLabel = new JLabel("Welcome to Jeopardy!");
		
		//hw4 change
		//JLabel explainContentLabel = new JLabel("Choose the game file, number of teams, and team names before starting the game.");
		explainContentLabel = new JLabel("Choose whether you are joining or hosting a game or playing not-networked.");

		
		numberOfTeamsLabel = new JLabel("Please choose the number of teams that will be playing on the slider below.");
		chooseGameFileLabel = new JLabel("Please choose a game file.");
				
		//set appearances on local variables
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, notNetworkedButton, hostButton, joinGameButton, welcomeLabel, explainContentLabel, welcomePanel, titlePanel, rbPanel);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, notNetworkedButton, hostButton, joinGameButton,explainContentLabel, chooseGameFileLabel, numberOfTeamsLabel);
		AppearanceSettings.setTextAlignment(welcomeLabel, explainContentLabel, chooseGameFileLabel, numberOfTeamsLabel);
		
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, chooseGameFileLabel, numberOfTeamsLabel, numberOfTeamsPanel, fileChooserPanel, teamsAndFilePanel,
				buttonPanel, teamNamesPanel, teamLabelsPanel1, teamLabelsPanel2, teamTextFieldsPanel1, teamTextFieldsPanel2);
		AppearanceSettings.setForeground(Color.lightGray, chooseGameFileLabel, numberOfTeamsLabel);
		
		AppearanceSettings.setSize(800, 60, welcomePanel, explainContentLabel);
		AppearanceSettings.setSize(800, 100, buttonPanel, numberOfTeamsPanel);
		AppearanceSettings.setSize(800, 80, fileChooserPanel);
		AppearanceSettings.setSize(200, 20, portField, ipField);
		
		welcomeLabel.setFont(AppearanceConstants.fontLarge);
		numberOfTeamsLabel.setSize(700, 50);

		//setting box layouts of panels
		AppearanceSettings.setBoxLayout(BoxLayout.LINE_AXIS, buttonPanel, fileChooserPanel, teamLabelsPanel1, teamLabelsPanel2, teamTextFieldsPanel1, teamTextFieldsPanel2);
		AppearanceSettings.setBoxLayout(BoxLayout.PAGE_AXIS, northPanel, teamNamesPanel, teamsAndFilePanel, numberOfTeamsPanel);

		//method iterates through components and add glue after each one is added, bool indicates whether glue should be added at the initially as well
		AppearanceSettings.addGlue(teamLabelsPanel1, BoxLayout.LINE_AXIS, true, teamNameLabels.get(0), teamNameLabels.get(1));
		AppearanceSettings.addGlue(teamLabelsPanel2, BoxLayout.LINE_AXIS, true, teamNameLabels.get(2), teamNameLabels.get(3));
		AppearanceSettings.addGlue(teamTextFieldsPanel1, BoxLayout.LINE_AXIS, true, teamNameTextFields.get(0), teamNameTextFields.get(1));
		AppearanceSettings.addGlue(teamTextFieldsPanel2, BoxLayout.LINE_AXIS, true, teamNameTextFields.get(2), teamNameTextFields.get(3));
		AppearanceSettings.addGlue(teamNamesPanel, BoxLayout.PAGE_AXIS, true, teamLabelsPanel1, teamTextFieldsPanel1, teamLabelsPanel2, teamTextFieldsPanel2);
		
		//don't want to pass in fileNameLabel since I don't want glue after it
		AppearanceSettings.addGlue(fileChooserPanel, BoxLayout.LINE_AXIS, true, chooseGameFileLabel, fileChooserButton);
		fileChooserPanel.add(fileNameLabel);
		
		//don't want to pass in fileChooserPanel since I don't want glue after it
		AppearanceSettings.addGlue(teamsAndFilePanel, BoxLayout.PAGE_AXIS, true, numberOfTeamsPanel);
		teamsAndFilePanel.add(fileChooserPanel);
		
		AppearanceSettings.addGlue(buttonPanel, BoxLayout.LINE_AXIS, true, startGameButton, clearDataButton, logoutButton, exitButton);
		
		//add other components to other containers
		welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
		welcomePanel.add(quickPlay, BorderLayout.EAST);

		titlePanel.add(welcomePanel, BorderLayout.NORTH);
		titlePanel.add(explainContentLabel, BorderLayout.CENTER);
		titlePanel.add(rbPanel, BorderLayout.SOUTH);
		
		northPanel.add(titlePanel);
		
		portAndIpPanel = new JPanel();
		portPanel = new JPanel();
		portPanel.add(portField);
		ipPanel = new JPanel();
		ipPanel.add(ipField);
		portAndIpPanel.add(portPanel);
		portAndIpPanel.add(ipPanel);
		portPanel.setVisible(false);
		ipPanel.setVisible(false);
		
		AppearanceSettings.setBackground(AppearanceConstants.darkBlue, portAndIpPanel, portPanel, ipPanel);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, portAndIpPanel, portPanel, ipPanel);
		
		numberOfTeamsPanel.setLayout(new GridLayout(3, 1));
		numberOfTeamsPanel.add(portAndIpPanel);
		numberOfTeamsPanel.add(numberOfTeamsLabel);
		numberOfTeamsPanel.add(slider);
		
		teamNamesPanel.add(waitLabel);
				
		mainPanel.add(northPanel);
		mainPanel.add(teamsAndFilePanel);
		mainPanel.add(teamNamesPanel);
		mainPanel.add(buttonPanel);
	}
	
	//determines whether the chosen file is valid
	private void setHaveValidFile(File file){
		
		//if they had already chosen a valid file, but want to replace it, need to clear stored data
		if (haveValidFile) gameData.clearData();
		
		try{				
			//try parsing this file; the parseFile method could throw an exception here, in which case we know it was invalid
			gameData.parseFile(file.getAbsolutePath());
			haveValidFile = true;
			
			if (gameData.getGameFile().getNumberOfRatings() == -1) fileNameLabel.setText(file.getName() + "    no rating");

			else fileNameLabel.setText(file.getName() + "    average rating: "+gameData.getGameFile().getAverageRating()+"/5");
			//check if the user can start the game
			startGameButton.setEnabled(haveValidFile && haveNames());
		}
		
		catch (Exception e){
			haveValidFile = false;
			startGameButton.setEnabled(false);
			fileNameLabel.setText("");
			//pop up with error message
			JOptionPane.showMessageDialog(this,
					e.getMessage(),
					"File Reading Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void addListeners(){
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindowListener(this));
		
		//adding a document listener to each text field. This will allow us to determine if the user has entered team names
		for (int i = 0; i<MAX_NUMBER_OF_TEAMS; i++)
		{
			teamNameTextFields.get(i).getDocument().addDocumentListener(new MyDocumentListener());
		}
		portField.getDocument().addDocumentListener(new MyDocumentListener());
		ipField.getDocument().addDocumentListener(new MyDocumentListener());
		
		
		fileChooserButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.showOpenDialog(StartWindowGUI.this);
				File file = fileChooser.getSelectedFile();
				
				if (file != null) setHaveValidFile(file);
			}
			
		});
		
		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e) {
				
				/*
				//sets appropriate text fields and labels invisible
				for (int i = slider.getValue(); i<MAX_NUMBER_OF_TEAMS; i++){
					setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
				}
				//sets appropriate text fields and labels visible
				for (int i = 0; i<slider.getValue(); i++){
					setVisible(teamNameLabels.get(i), teamNameTextFields.get(i));
				}*/
				
				//hw4 modified slider listener: if under host mode, only enable the first label and textfield
				if(hostButton.isSelected() || joinGameButton.isSelected())
				{
					//sets appropriate text fields and labels invisible
					for (int i = slider.getValue(); i<MAX_NUMBER_OF_TEAMS; i++){
						setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					}
					//sets appropriate text fields and labels visible
					setVisible(teamNameLabels.get(0), teamNameTextFields.get(0));

				}
				else
				{
					//sets appropriate text fields and labels invisible
					for (int i = slider.getValue(); i<MAX_NUMBER_OF_TEAMS; i++){
						setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					}
					//sets appropriate text fields and labels visible
					for (int i = 0; i<slider.getValue(); i++){
						setVisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					}
				}
				
				
				//check if the user can start the game
				startGameButton.setEnabled(haveNames() && haveValidFile);
			}
			
		});
		
		startGameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(notNetworkedButton.isSelected())
				{
					//TODO
					numberOfTeams = slider.getValue();
					List<String> teamNames = new ArrayList<>(numberOfTeams);
					//getting the text in each of the visible text fields and storing them in a list
					for (int i = 0; i< numberOfTeams; i++) {
						teamNames.add(teamNameTextFields.get(i).getText());
					}
					//initializing TeamGUIComponents objects
					gameData.setTeams(teamNames, numberOfTeams);
					gameData.setNumberOfQuestions(quickPlay.isSelected() ? 5 : 25);
					
					new MainGUI(gameData, loggedInUser, "none", null).setVisible(true);
					dispose();
				}
				else if(hostButton.isSelected())
				{
					//TODO
					disableWholeWindow();
					numberOfTeams = slider.getValue();
					List<String> teamNames = new ArrayList<>(numberOfTeams);
					
					gameData.setNumberOfQuestions(quickPlay.isSelected() ? 5 : 25);
					GameServer gs = new GameServer( Integer.parseInt(portField.getText().trim()), slider.getValue(), SWGUI, gameData, loggedInUser, teamNames);
					GameClient gameClient = new GameClient("localhost", Integer.parseInt(portField.getText().trim()), SWGUI, teamNameTextFields.get(0).getText().trim());
				}
				else//joinButton is selected
				{
					//TODO
					disableWholeWindow();
					GameClient gc = new GameClient(ipField.getText().trim(), Integer.parseInt(portField.getText().trim()), SWGUI, teamNameTextFields.get(0).getText().trim());
				}
			}
		});
		
		exitButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
			
		});
		
		clearDataButton.addActionListener(new ActionListener(){

			//reseting all data
			@Override
			public void actionPerformed(ActionEvent e) {
				haveNames = false;
				haveValidFile = false;
				gameData.clearData();
				quickPlay.setSelected(false);
				//start index at 1, we still was to show the 0th elements (team 1)
				for (int i = 1; i<MAX_NUMBER_OF_TEAMS; i++){
					setInvisible(teamNameLabels.get(i), teamNameTextFields.get(i));
					teamNameTextFields.get(i).setText("");
				}
				portField.setText("port");
				portField.setForeground(Color.gray);
				ipField.setText("ip address");
				ipField.setForeground(Color.gray);
				
				startGameButton.setEnabled(false);
				teamNameTextFields.get(0).setText("");
				slider.setValue(1);
				fileNameLabel.setText("");
				
				//hw4 new
				notNetworkedButton.setSelected(true);
				//dealing with upper part
				slider.setVisible(true);
				numberOfTeamsLabel.setVisible(true);
				fileChooser.setVisible(true);
				chooseGameFileLabel.setVisible(true);	
				fileChooserButton.setVisible(true);
				AppearanceSettings.setSliders(1, MAX_NUMBER_OF_TEAMS, 1, 1, AppearanceConstants.fontSmallest, slider);
				quickPlay.setVisible(true);
				
				//center part
				portPanel.setVisible(false);
				ipPanel.setVisible(false);
				
				//bottom part
				startGameButton.setText("Start Jeopardy");
			}
			
		});
		
		logoutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new LoginGUI().setVisible(true);
				dispose();
			}
			
		});
		
	}
	
	//hw4 new function: disable whole window when waiting for other players to join
	public void disableWholeWindow()
	{
		//TODO
		fileChooser.setEnabled(false);
		fileChooserButton.setEnabled(false);
		for(JTextField jtf : teamNameTextFields)
		{
			jtf.setEnabled(false);
		}
		teamNameLabels.get(0).setEnabled(false);
		startGameButton.setEnabled(false);
		clearDataButton.setEnabled(false);
		slider.setEnabled(false);
		fileNameLabel.setEnabled(false);
		quickPlay.setEnabled(false);
		explainContentLabel.setEnabled(false);
		numberOfTeamsLabel.setEnabled(false);
		chooseGameFileLabel.setEnabled(false);
		
		notNetworkedButton.setEnabled(false);
		hostButton.setEnabled(false);
		joinGameButton.setEnabled(false);
		portField.setEnabled(false);
		ipField.setEnabled(false);
	}
	
	public void setWaitLabel(int i)
	{
		//TODO
		waitLabel.setText("Still waiting for " + i + " players to connect...");
	}
	
	public void setWaitLabelCancel(String s)
	{
		//TODO
		waitLabel.setText(s);
	}
	
	public void cancelGame()
	{
		//TODO
		fileChooser.setEnabled(true);
		fileChooserButton.setEnabled(true);
		for(JTextField jtf : teamNameTextFields)
		{
			jtf.setEnabled(true);
		}
		teamNameLabels.get(0).setEnabled(true);
		startGameButton.setEnabled(true);
		clearDataButton.setEnabled(true);
		slider.setEnabled(true);
		fileNameLabel.setEnabled(true);
		quickPlay.setEnabled(true);
		explainContentLabel.setEnabled(true);
		numberOfTeamsLabel.setEnabled(true);
		chooseGameFileLabel.setEnabled(true);
		
		notNetworkedButton.setEnabled(true);
		hostButton.setEnabled(true);
		joinGameButton.setEnabled(true);
		portField.setEnabled(true);
		ipField.setEnabled(true);
	}
	
	//updates and returns the haveNames member variable of whether all teams have been named
	private boolean haveNames(){
		
		haveNames = true;
		//check to see if all relevant text fields have text in them
		//hw4 update: if nonnetwork, check all textfields; if host or join just check the first
		if(notNetworkedButton.isSelected())
		{
			for (int i = 0; i<slider.getValue(); i++){
				
				if (teamNameTextFields.get(i).getText().trim().equals("")) haveNames = false;
			}
		}
		else if(hostButton.isSelected())
		{
			if (teamNameTextFields.get(0).getText().trim().equals("") || portField.getText().trim().equals(""))
			{
				haveNames = false;
			}
		}
		else//joinButton is selected
		{
			if (teamNameTextFields.get(0).getText().trim().equals("") || portField.getText().trim().equals("") || ipField.getText().trim().equals(""))
			{
				haveNames = false;
			}
			haveValidFile = true;
		}
			
		return haveNames;
	}
	
	//document listener; in each method, simply checking whether the user can start the game
	private class MyDocumentListener implements DocumentListener{
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			startGameButton.setEnabled(haveNames() && haveValidFile);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			startGameButton.setEnabled(haveNames() && haveValidFile);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			startGameButton.setEnabled(haveNames() && haveValidFile);
		}
	}
}
