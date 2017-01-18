package game_logic;

import javax.swing.*;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartScreen extends JFrame {
	public static final long serialVersionUID = 1;
	
	private JPanel top, cen, bot;
	private JLabel welcomeToJeopardy, intro, chooseFile, howManyTeam, inputLabel;
	private JLabel team1JL, team2JL, team3JL, team4JL;
	private JTextField team1JTF, team2JTF, team3JTF, team4JTF;
	private JButton chooseButton, quitButton, clearButton, startButton;
	private JSlider teamSlide;
	
	private JCheckBox quickPlay;
	
	private String filename;
	private boolean chooseButtonPressed;
	
	public GamePlay gp;
	public GameData gd;
	public static List<TeamData> teamData;
	
	public PlayScreen ps;
	
	public boolean scQuickPlay;
	
	//public boolean quickPlayChecked;//true to just 5 questions, 25 if false
	
	public StartScreen()
	{
		super("Jeopardy");
		setSize(1000, 600);
		setLocation(200,200);
		
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	public void initializeComponents()
	{
		chooseButtonPressed = false;
		
		top = new JPanel();
		top.setLayout(new GridBagLayout());
		cen = new JPanel();
		cen.setLayout(new GridBagLayout());
		bot = new JPanel();
		bot.setLayout(new GridBagLayout());
		
		welcomeToJeopardy = new JLabel();
		intro = new JLabel();
		chooseFile = new JLabel();
		howManyTeam = new JLabel();
		inputLabel = new JLabel();
		
		team1JL = new JLabel("Please enter name for team 1:");
		team1JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team2JL = new JLabel("Please enter name for team 2:");
		team2JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team3JL = new JLabel("Please enter name for team 3:");
		team3JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team4JL = new JLabel("Please enter name for team 4:");
		team4JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		team1JTF = new JTextField("", 24);
		team2JTF = new JTextField("", 24);
		team3JTF = new JTextField("", 24);
		team4JTF = new JTextField("", 24);
		
		chooseButton = new JButton("Choose File");
		quitButton = new JButton("Exit");
		clearButton = new JButton("Clear data");
		startButton = new JButton("Start game");
		teamSlide = new JSlider(JSlider.HORIZONTAL, 1, 4, 1);
		
		quickPlay = new JCheckBox("Quick Play");
	}
	
	public void createGUI()
	{	//north part with title and quick play//////////
		top.setBackground(Color.CYAN);//set the background color
		
		welcomeToJeopardy.setText("Welcome to Jeopardy!");
		welcomeToJeopardy.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeToJeopardy.setFont(new Font("Tahoma", Font.PLAIN, 30));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		top.add(welcomeToJeopardy, gbc);//////"Welcome to jeopardy!"
		
		quickPlay.setFont(new Font("Tahoma", Font.PLAIN, 20));
		quickPlay.setBackground(Color.CYAN);
		quickPlay.setForeground(Color.GRAY);
		gbc.gridx = 1;
		gbc.gridy = 0;
		top.add(quickPlay, gbc);
		
		intro.setText("Choose the game file, number of teams, and team names before starting the name.");
		intro.setHorizontalAlignment(SwingConstants.CENTER);
		intro.setFont(new Font("Tahoma", Font.PLAIN, 15));
		gbc.gridx = 0;
		gbc.gridy = 1;
		top.add(intro, gbc);
		add(top, BorderLayout.NORTH);
		////////////////////////////////////////////////
		
		//center part with game file chooser and team number chooser
		cen.setBackground(Color.BLUE);
		
		chooseFile.setText("Please select a game file:");
		chooseFile.setForeground(Color.white);
		chooseFile.setHorizontalAlignment(SwingConstants.CENTER);
		chooseFile.setFont(new Font("Tahoma", Font.PLAIN, 15));
		gbc.gridx = 0;
		gbc.gridy = 0;
		cen.add(chooseFile, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		cen.add(chooseButton, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		cen.add(inputLabel, gbc);
		
		howManyTeam.setText("Please choose the number of teams that will be playing on the slider below:");
		howManyTeam.setForeground(Color.white);
		howManyTeam.setHorizontalAlignment(SwingConstants.CENTER);
		howManyTeam.setFont(new Font("Tahoma", Font.PLAIN, 15));
		gbc.gridx = 0;
		gbc.gridy = 1;
		cen.add(howManyTeam, gbc);
		
		teamSlide.setMaximum(4);
		teamSlide.setPaintLabels(true);
		teamSlide.setPaintTicks(true);
		teamSlide.setMajorTickSpacing(1);
		teamSlide.setValue(1);//set the initial team number as 1
		teamSlide.setPreferredSize(new Dimension(700, 100));
		gbc.gridx = 0;
		gbc.gridy = 2;
		cen.add(teamSlide, gbc);
		
		//enter team1 infor
		gbc.gridx = 0;
		gbc.gridy = 3;
		team1JL.setForeground(Color.white);
		cen.add(team1JL, gbc);
		gbc.gridx = 0;
		gbc.gridy = 4;
		cen.add(team1JTF, gbc);
		
		//enter team2 infor
		gbc.gridx = 1;
		gbc.gridy = 3;
		team2JL.setForeground(Color.white);
		cen.add(team2JL, gbc);
		gbc.gridx = 1;
		gbc.gridy = 4;
		cen.add(team2JTF, gbc);
		team2JL.setVisible(false);//initially team2 choice cannot be seen
		team2JTF.setVisible(false);
		
		//enter team3 infor
		gbc.gridx = 0;
		gbc.gridy = 5;
		team3JL.setForeground(Color.white);
		cen.add(team3JL, gbc);
		gbc.gridx = 0;
		gbc.gridy = 6;
		cen.add(team3JTF, gbc);
		team3JL.setVisible(false);//initially team3 choice cannot be seen
		team3JTF.setVisible(false);
		
		//enter team4 infor
		gbc.gridx = 1;
		gbc.gridy = 5;
		team4JL.setForeground(Color.white);
		cen.add(team4JL, gbc);
		gbc.gridx = 1;
		gbc.gridy = 6;
		cen.add(team4JTF, gbc);
		team4JL.setVisible(false);//initially team4 choice cannot be seen
		team4JTF.setVisible(false);
		
		add(cen, BorderLayout.CENTER);
		//////////////////////////////////////////////////////////////////
		
		//bottom part with quit, clear, and start button/////////////////
		bot.setBackground(Color.blue);
		gbc.gridx = 0;
		gbc.gridy = 0;
		quitButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		bot.add(quitButton, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		clearButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		bot.add(clearButton, gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 0;
		startButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		bot.add(startButton, gbc);
		startButton.setEnabled(false);
		
		add(bot, BorderLayout.SOUTH);
		///////////////////////////////////////////////////////////////////
	}
	
	public void addEvents()
	{	//check twice if the user really want to exit
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				int selection = JOptionPane.showConfirmDialog(StartScreen.this, "Do you really want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION);
				switch(selection)
				{
				case JOptionPane.YES_OPTION:
					System.exit(0);
					break;
				case JOptionPane.NO_OPTION:
					return;
				}
			}
		});
		
		//if quickplay is checked, then just play 5 questions, else 25 questions
		//use a boolean to mark whether quickplay is checked, and this boolean will 
		//later be passed to playscreen
		quickPlay.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getSource() == quickPlay)
				{
					if(quickPlay.isSelected() == true)
					{
						scQuickPlay = true;
					}
					else
					{
						scQuickPlay = false;
					}
				}
			}
		});
		//choose game file
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				fileChooser();
				gd = new GameData(filename);
				//use the GameData to check the
				//input file first, and we will
				//import into gameplay again when
				//startButton is pressed
				
				if(gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
				chooseButtonPressed = true;
			}
		});
		//quit
		quitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				System.exit(0);
			}
		});
		//clear
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				teamSlide.setValue(1);
				team1JTF.setText("");
				team2JTF.setText("");
				team3JTF.setText("");
				team4JTF.setText("");
				
				chooseButtonPressed = false;
			}
		});
		//start
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				int value = teamSlide.getValue();
				teamData = new ArrayList<>(value);
				addTeamInfor(value);
				if(checkSameName(value) == true)//no duplicate name
				{
					System.out.println(teamData.get(0).getTeamName());
					//gp = new GamePlay(filename);
					ps = new PlayScreen(teamData, gd);
					ps.quickPlayChecked = scQuickPlay;
					ps.setVisible(true);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Duplicate names! Please enter names again."); 
					return;
				}
			}
		});

		teamSlide.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)
			{
				int value = teamSlide.getValue();
				if(value == 1)
				{
					team1JL.setVisible(true);
					team2JL.setVisible(false);
					team3JL.setVisible(false);
					team4JL.setVisible(false);
					
					team1JTF.setVisible(true);
					team2JTF.setVisible(false);
					team3JTF.setVisible(false);
					team4JTF.setVisible(false);
				}
				else if(value == 2)
				{
					team1JL.setVisible(true);
					team2JL.setVisible(true);
					team3JL.setVisible(false);
					team4JL.setVisible(false);
					
					team1JTF.setVisible(true);
					team2JTF.setVisible(true);
					team3JTF.setVisible(false);
					team4JTF.setVisible(false);
				}
				else if(value == 3)
				{
					team1JL.setVisible(true);
					team2JL.setVisible(true);
					team3JL.setVisible(true);
					team4JL.setVisible(false);
					
					team1JTF.setVisible(true);
					team2JTF.setVisible(true);
					team3JTF.setVisible(true);
					team4JTF.setVisible(false);
				}
				else if(value == 4)
				{
					team1JL.setVisible(true);
					team2JL.setVisible(true);
					team3JL.setVisible(true);
					team4JL.setVisible(true);
					
					team1JTF.setVisible(true);
					team2JTF.setVisible(true);
					team3JTF.setVisible(true);
					team4JTF.setVisible(true);
				}
				System.out.println(value);
			}
		});
		
		team1JTF.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
            public void insertUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }

            public void removeUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
		});
		
		team2JTF.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
            public void insertUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }

            public void removeUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
		});
		
		team3JTF.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
            public void insertUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }

            public void removeUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
		});
		
		team4JTF.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
            public void insertUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }

            public void removeUpdate(DocumentEvent arg0) 
            {
				if(chooseButtonPressed == true && gd.getIsExit() == false && checkName(teamSlide.getValue())==true )
				{
					startButton.setEnabled(true);
				}
				else
				{
					startButton.setEnabled(false);
				}
            }
		});

	}
	
	//used by the action listener of choose button
	public void fileChooser()
	{
		JFileChooser jf = new JFileChooser();
		FileNameExtensionFilter fnef = new FileNameExtensionFilter("TEXT FILE", "txt", "text");
		jf.setFileFilter(fnef);
		int returnValue = jf.showOpenDialog(this);
		if(returnValue == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = jf.getSelectedFile();
			if(  !selectedFile.exists() )
			{
				JOptionPane.showMessageDialog(null, "Invalid file. Please choose again."); 
			}
			filename = selectedFile.toString();//detailed path
			String filename1 = selectedFile.getName();//succinct name
			inputLabel.setText(filename1);
			
		}
	}
	
	//check if everyone has entered their names
	public boolean checkName(int n)
	{
		if(n == 1 && team1JTF.getText().isEmpty() == false)
		{
			return true;
		}
		else if(n == 2 && team1JTF.getText().isEmpty() == false
				&& team2JTF.getText().isEmpty() == false)
		{
			return true;
		}
		else if(n == 3 && team1JTF.getText().isEmpty() == false
				&& team2JTF.getText().isEmpty() == false && team3JTF.getText().isEmpty() == false)
		{
			return true;
		}
		else if(n == 4 && team1JTF.getText().isEmpty() == false
				&& team2JTF.getText().isEmpty() == false && team3JTF.getText().isEmpty() == false
				&& team4JTF.getText().isEmpty() == false)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//check duplicate names
	public boolean checkSameName(int n)
	{
		if(n == 1)
		{
			System.out.println(team1JTF.getText());
			return true;
		}
		else if(n == 2 && team1JTF.getText().equals(team2JTF.getText())==false  )
		{
			return true;
		}
		else if(n == 3 && team1JTF.getText().equals(team2JTF.getText())== false 
					&& team1JTF.getText().equals(team3JTF.getText())== false  
					&& team2JTF.getText().equals(team3JTF.getText())== false )
		{
			return true;
		}
		else if(n == 4 && team1JTF.getText().equals(team2JTF.getText())== false 
					&& team1JTF.getText().equals(team3JTF.getText())== false 
					&& team1JTF.getText().equals(team4JTF.getText())== false 
					&& team2JTF.getText().equals(team3JTF.getText())== false 
					&& team2JTF.getText().equals(team4JTF.getText())== false 
					&& team3JTF.getText().equals(team4JTF.getText())== false)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	//import team infor from start screen to TeamData
	public void addTeamInfor(Integer in)
	{
		if(in == 1)
		{
			teamData.add(new TeamData(0, 0L, team1JTF.getText()));
		}
		else if(in == 2)
		{
			teamData.add(new TeamData(0, 0L, team1JTF.getText()));
			teamData.add(new TeamData(1, 0L, team2JTF.getText()));
		}
		else if(in == 3)
		{
			teamData.add(new TeamData(0, 0L, team1JTF.getText()));
			teamData.add(new TeamData(1, 0L, team2JTF.getText()));
			teamData.add(new TeamData(2, 0L, team3JTF.getText()));
		}
		else if(in == 4)
		{
			teamData.add(new TeamData(0, 0L, team1JTF.getText()));
			teamData.add(new TeamData(1, 0L, team2JTF.getText()));
			teamData.add(new TeamData(2, 0L, team3JTF.getText()));
			teamData.add(new TeamData(3, 0L, team4JTF.getText()));
		}
	}
	
}
