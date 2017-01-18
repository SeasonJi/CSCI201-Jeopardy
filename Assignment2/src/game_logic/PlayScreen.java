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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class PlayScreen extends JFrame {
	public static final long serialVersionUID = 1;
	
	public List<TeamData> td;//store teams' infor
	public GameData gd;//game status
	
	public int firstTeam;//counter to track the current team
	public boolean chances;//flag whether a team has second chance
	
	//components for displaying all the questions
	private JPanel title, questions, titleAndQuestions, players, gameProcess, playersAndGameProcess;
	private JMenuBar jmb;
	private JMenu jm;
	private JMenuItem restartJMI, chooseJMI, exitJMI;
	private JLabel gameTitle;
	private JLabel team1JL, team2JL, team3JL, team4JL;
	private JLabel gameProcessJL;
	private JScrollPane gameProcessJSP;
	private JTextArea gameProcessJTA;	
	private JButton[][] allQ = new JButton[5][5];
	
	//components for individual question screen
	private JPanel specificQ, specificQsouth;
	private JLabel qInfor, questionShow;
	private JTextField reply;
	private JButton submit;
	
	//components for final jeopardy
	private JLabel fjTitle;
	private JPanel fjJP, fjCenterJP, fjSouthJP;
	private JPanel fjTeam1JP, fjTeam2JP, fjTeam3JP, fjTeam4JP;//to combine each team's label, slide and button
	private JLabel fjTeam1JL, fjTeam2JL, fjTeam3JL, fjTeam4JL;//each team's nameS
	private JLabel team1betJL, team2betJL, team3betJL, team4betJL;//dynamically show each team's bet
	private JSlider team1Slide, team2Slide, team3Slide, team4Slide;//bet slide
	private JButton team1betJB, team2betJB, team3betJB, team4betJB;//bet button
	
	private JLabel fjQuestionDisplay;
	private JPanel submitJP;
	private JPanel team1SJP, team2SJP, team3SJP, team4SJP;
	private JTextField fjTeam1JTF, fjTeam2JTF, fjTeam3JTF, fjTeam4JTF;
	private JButton team1SubmitJB, team2SubmitJB, team3SubmitJB, team4SubmitJB; 
	
	private String fjteam1A, fjteam2A, fjteam3A, fjteam4A;
	private int team1bet, team2bet, team3bet, team4bet;
	
	private int howManyPlayFJ;//count how many are qualified for final jeopardy
	private int clickedSet;
	private int clickedSubmit;
	
	//flag to see if we want to quickplay
	public boolean quickPlayChecked = false;
	
	public PlayScreen(List<TeamData> teamData, GameData gameData)
	{
		super("Jeopardy");
		setSize(1500,1000);
		setLocation(200,200);
		
		td = teamData;
		gd = gameData;
		
		chances = true;
		
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	public void initializeComponents()
	{
		title = new JPanel();
		//MENUBAR////////////////////////////////////////
		jmb = new JMenuBar();
		jm = new JMenu("Menu");
		restartJMI = new JMenuItem("Restart The Game");
		chooseJMI = new JMenuItem("Choose New Game File");
		exitJMI = new JMenuItem("Exit Game");
		jm.add(restartJMI);
		jm.add(chooseJMI);
		jm.add(exitJMI);
		jmb.add(jm);
		setJMenuBar(jmb);
		////////////////////////////////////////////////
		
		//all questions/////////////////////////////////
		questions = new JPanel();
		questions.setLayout(new GridLayout(6, 5));
		
		titleAndQuestions = new JPanel(new BorderLayout());
		////////////////////////////////////////////////
		
		//upper right team data/////////////////////////
		players = new JPanel();
		players.setLayout(new GridLayout(4, 1));
		////////////////////////////////////////////////
		
		//lower right game process details//////////////
		gameProcess = new JPanel();
		gameProcess.setLayout(new GridLayout(2, 1));
		
		playersAndGameProcess = new JPanel();
		playersAndGameProcess.setLayout(new GridLayout(2, 1));
		////////////////////////////////////////////////
		gameTitle = new JLabel("Jeopardy");
		
		if(td.size() == 1)
		{
			team1JL = new JLabel(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
			team1JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team2JL = new JLabel("");
			team2JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team3JL = new JLabel("");
			team3JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team4JL = new JLabel("");
			team4JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		}
		else if(td.size() == 2) 
		{
			team1JL = new JLabel(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
			team1JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team2JL = new JLabel(td.get(1).getTeamName()+ "                         "+ "$" + td.get(1).getPoints());
			team2JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team3JL = new JLabel("");
			team3JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team4JL = new JLabel("");
			team4JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		}
		else if(td.size() == 3) 
		{
			team1JL = new JLabel(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
			team1JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team2JL = new JLabel(td.get(1).getTeamName()+ "                         "+ "$" + td.get(1).getPoints());
			team2JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team3JL = new JLabel(td.get(2).getTeamName()+ "                         "+ "$" + td.get(2).getPoints());
			team3JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team4JL = new JLabel("");
			team4JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		}
		else if(td.size() == 4) 
		{
			team1JL = new JLabel(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
			team1JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team2JL = new JLabel(td.get(1).getTeamName()+ "                         "+ "$" + td.get(1).getPoints());
			team2JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team3JL = new JLabel(td.get(2).getTeamName()+ "                         "+ "$" + td.get(2).getPoints());
			team3JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team4JL = new JLabel(td.get(3).getTeamName()+ "                         "+ "$" + td.get(3).getPoints());
			team4JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
		}
		
		gameProcessJL = new JLabel("Game Progress");
		gameProcessJL.setFont(new Font("Tahoma", Font.PLAIN, 30));
		
		gameProcessJTA = new JTextArea("Welcome to Jeopardy!\n");
		gameProcessJTA.setFont(new Font("Tahoma", Font.PLAIN, 20));
		gameProcessJTA.setLineWrap(true);
		gameProcessJTA.setWrapStyleWord(true);
		
		gameProcessJSP = new JScrollPane(gameProcessJTA);
		
		//choose a team to start/////////////////////
		Random rand = new Random();
		firstTeam = rand.nextInt(td.size());
		gameProcessJTA.append("The team to go first is: " + td.get(firstTeam).getTeamName() + "\n");
		/////////////////////////////////////////////
		
		//initialize final jeopardy components
		fjJP = new JPanel(new BorderLayout());
		fjTitle = new JLabel("Final Jeopardy Round");
		fjTitle.setHorizontalAlignment(SwingConstants.CENTER);
		fjTitle.setFont(new Font("Tahoma", Font.PLAIN, 30));
		
		fjCenterJP = new JPanel();
		fjCenterJP.setLayout(new GridLayout(4,1));
		fjSouthJP = new JPanel();
		fjSouthJP.setLayout(new GridLayout(2,2));
		
		fjTeam1JP = new JPanel();
		fjTeam1JP.setLayout(new GridLayout(1, 4));
		fjTeam2JP = new JPanel();
		fjTeam2JP.setLayout(new GridLayout(1, 4));
		fjTeam3JP = new JPanel();
		fjTeam3JP.setLayout(new GridLayout(1, 4));
		fjTeam4JP = new JPanel();
		fjTeam4JP.setLayout(new GridLayout(1, 4));
		
		fjTeam1JL = new JLabel();
		fjTeam2JL = new JLabel();
		fjTeam3JL = new JLabel();
		fjTeam4JL = new JLabel();
		
		team1betJL = new JLabel();
		team2betJL = new JLabel();
		team3betJL = new JLabel();
		team4betJL = new JLabel();
		
		howManyPlayFJ = 0;//see how which team can play final jeopardy
		clickedSet = 0;//if everyone has clicked set bet, then we can show final question
		clickedSubmit = 0;//if everyone has clicked submit, then we can show who wins
		
		team1betJB = new JButton("Set Bet");
		team1betJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team2betJB = new JButton("Set Bet");
		team2betJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team3betJB = new JButton("Set Bet");
		team3betJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team4betJB = new JButton("Set Bet");
		team4betJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		submitJP = new JPanel();
		submitJP.setLayout(new GridLayout(2,2));
		
		team1SJP = new JPanel();
		team1SJP.setLayout(new GridLayout(1,2));
		team2SJP = new JPanel();
		team2SJP.setLayout(new GridLayout(1,2));
		team3SJP = new JPanel();
		team3SJP.setLayout(new GridLayout(1,2));
		team4SJP = new JPanel();
		team4SJP.setLayout(new GridLayout(1,2));
		
		fjQuestionDisplay = new JLabel();
		fjTeam1JTF = new JTextField("");
		fjTeam2JTF = new JTextField("");
		fjTeam3JTF = new JTextField("");
		fjTeam4JTF = new JTextField("");
		
		team1SubmitJB = new JButton("Submit answer");
		team1SubmitJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team1SubmitJB.setEnabled(false);
		team2SubmitJB = new JButton("Submit answer");
		team2SubmitJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team2SubmitJB.setEnabled(false);
		team3SubmitJB = new JButton("Submit answer");
		team3SubmitJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team3SubmitJB.setEnabled(false);
		team4SubmitJB = new JButton("Submit answer");
		team4SubmitJB.setFont(new Font("Tahoma", Font.PLAIN, 20));
		team4SubmitJB.setEnabled(false);
	}
	
	public void createGUI()
	{
		//title for the north of left part////////////////
		gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
		gameTitle.setFont(new Font("Tahoma", Font.PLAIN, 30));
		title.add(gameTitle);
		title.setBackground(Color.CYAN);
		titleAndQuestions.add(title, BorderLayout.NORTH);
		//////////////////////////////////////////////////
		
		//names for all 5 categories on the first line/////
		for(int i = 0; i < 5; i++)
		{
			JButton jb = new JButton(gd.categoryArray[i]);
			questions.add(jb);
		}
		//showing money value for each of the  5*5 questions///
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 5; j++)
			{
				allQ[i][j] = new JButton(gd.moneyArray[i]);
				allQ[i][j].setBackground(Color.ORANGE);
				questions.add(allQ[i][j]);
			}
		}
		//combine title and questions//////////////////////////
		titleAndQuestions.add(questions, BorderLayout.CENTER);
		add(titleAndQuestions, BorderLayout.CENTER);
		///////////////////////////////////////////////////////
		
		//team names and their scores//////////////////////////
		players.add(team1JL);
		players.add(team2JL);		
		players.add(team3JL);		
		players.add(team4JL);
		
		players.setBackground(Color.LIGHT_GRAY);
		
		playersAndGameProcess.add(players);
		//game progress////////////////////////////////////////
		gameProcess.add(gameProcessJL);
		gameProcess.add(gameProcessJSP);
		gameProcess.setBackground(Color.CYAN);
		playersAndGameProcess.add(gameProcess);
		add(playersAndGameProcess, BorderLayout.EAST);
		//combine teams' details and game progress/////////////
		
		//for individual question display//////////////////////
		specificQ = new JPanel(new BorderLayout());//panel to show all the question components
		specificQ.setBackground(Color.CYAN);
		specificQsouth = new JPanel(new BorderLayout());//panel to show textfield and submit button
		qInfor = new JLabel();//team who chooses this question + corresponding category + corresponding point
		questionShow = new JLabel();//show the question
		reply = new JTextField("");//type in answer
		submit = new JButton("Submit");//submit button
		submit.setFont(new Font("Tahoma", Font.PLAIN, 30));
		submit.setPreferredSize(new Dimension(300, 100));
		///////////////////////////////////////////////////////
	}
	
	public void addEvents()
	{
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				int selection = JOptionPane.showConfirmDialog(PlayScreen.this, "Do you really want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION);
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
		
		
		//Menu buttons
		exitJMI.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				System.exit(0);
			}
		});
		
		restartJMI.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				gameProcessJTA.setText("");
				for(int i = 0; i < 5; i++)
				{
					for(int j = 0; j < 5; j++)
					{
						gd.isAsked[j][i] = false;
						allQ[i][j].setBackground(Color.ORANGE);
					}
				}
				
				for(TeamData t : td)
				{
					t.setPoints(t.getPoints()-t.getPoints());
				}
				
				if(td.size() == 1)
				{
					team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
				}
				else if(td.size() == 2)
				{
					team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					team2JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
				}
				else if(td.size() == 3)
				{
					team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					team2JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					team3JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
				}
				else if(td.size() == 4)
				{
					team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					team2JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					team3JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					team4JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
				}
				
				fjJP.setVisible(false);
				fjJP.removeAll();
				titleAndQuestions.setVisible(true);
			}
		});
		
		chooseJMI.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				for(int i = 0; i < 5; i++)
				{
					for(int j = 0; j < 5; j++)
					{
						gd.isAsked[j][i] = false;
					}
				}
				dispose();
			}
		});
		
		//add action to all 25 buttons
		allQ[0][0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(0,0);
			}
		});
		
		allQ[0][1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(0,1);
			}
		});
		
		allQ[0][2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(0,2);
			}
		});
		
		allQ[0][3].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(0,3);
			}
		});
		
		allQ[0][4].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(0,4);
			}
		});
		
		allQ[1][0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(1,0);
			}
		});
		
		allQ[1][1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(1,1);
			}
		});
		
		allQ[1][2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(1,2);
			}
		});
		
		allQ[1][3].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(1,3);
			}
		});
		
		allQ[1][4].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(1,4);
			}
		});
		
		allQ[2][0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(2,0);
			}
		});
		
		allQ[2][1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(2,1);
			}
		});
		
		allQ[2][2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(2,2);
			}
		});
		
		allQ[2][3].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(2,3);
			}
		});
		
		allQ[2][4].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(2,4);
			}
		});
		
		allQ[3][0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(3,0);
			}
		});
		
		allQ[3][1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(3,1);
			}
		});
		
		allQ[3][2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(3,2);
			}
		});
		
		allQ[3][3].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(3,3);
			}
		});
		
		allQ[3][4].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(3,4);
			}
		});
		
		allQ[4][0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(4,0);
			}
		});
		
		allQ[4][1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(4,1);
			}
		});
		
		allQ[4][2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(4,2);
			}
		});
		
		allQ[4][3].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(4,3);
			}
		});
		
		allQ[4][4].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				questionAction(4,4);
			}
		});
		
		//check answer and then return to the 25 questions layout when submit
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				String thisQuestion = questionShow.getText();
				int tempi = 0;
				int tempj = 1;
				for(int i = 0; i < 5; i++)
				{
					for(int j = 0; j < 5; j++)
					{
						String buffer = "<html>" + gd.questions[j][i].getQuestion() + "</html>";
						if(buffer.equals(thisQuestion))
						{
							tempi = i;
							tempj = j;
						}
					}
				}
				
				//correct answer but wrong format, true, which means one more chance
				//all the other situations, false, no more chances
				String givenAnswer = reply.getText().toLowerCase().trim();
				String realAnswer = gd.questions[tempj][tempi].getAnswer().toLowerCase().trim();
				
				if(givenAnswer.equals(realAnswer) && chances == true)
				{
					JOptionPane.showMessageDialog(null, "Invalid question format! Pose it as a question please.");
					chances = false;//still has one more chance to answer
					return;
				}
				else if(givenAnswer.equals("who is "+realAnswer) || givenAnswer.equals("who are "+realAnswer) || givenAnswer.equals("what is "+realAnswer)
						|| givenAnswer.equals("what is "+realAnswer) || givenAnswer.equals("what are "+realAnswer)
						|| givenAnswer.equals("when is "+realAnswer) || givenAnswer.equals("when are "+realAnswer)
						|| givenAnswer.equals("where is "+realAnswer) || givenAnswer.equals("where are "+realAnswer) )
				{
					chances = false;
					gameProcessJTA.append("You are right! " + gd.moneyArray[tempi] + " will be added to your total.\n");
					int value = Integer.parseInt(gd.moneyArray[tempi]);
					td.get(firstTeam).addPoints(value);
					if(firstTeam == 0)
					{
						team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					}
					else if(firstTeam == 1)
					{
						team2JL.setText(td.get(1).getTeamName()+ "                         "+ "$" + td.get(1).getPoints());
					}
					else if(firstTeam == 2)
					{
						team3JL.setText(td.get(2).getTeamName()+ "                         "+ "$" + td.get(2).getPoints());
					}
					else if(firstTeam == 3)
					{
						team4JL.setText(td.get(3).getTeamName()+ "                         "+ "$" + td.get(3).getPoints());
					}
					else
					{
						System.out.println("Error! Getting team more than 4!!!");
						return;
					}
					int nextTeam = (firstTeam+1)%td.size();
					gameProcessJTA.append("Now it is " + td.get(nextTeam).getTeamName() + "'s turn.\n");
					
					chances = true;
				}
				else
				{
					chances = false;
					gameProcessJTA.append("You are wrong! " + gd.moneyArray[tempi] + " will be deducted from your total.\n");
					int value = Integer.parseInt(gd.moneyArray[tempi]);
					td.get(firstTeam).deductPoints(value);
					
					if(firstTeam == 0)
					{
						team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
					}
					else if(firstTeam == 1)
					{
						team2JL.setText(td.get(1).getTeamName()+ "                         "+ "$" + td.get(1).getPoints());
					}
					else if(firstTeam == 2)
					{
						team3JL.setText(td.get(2).getTeamName()+ "                         "+ "$" + td.get(2).getPoints());
					}
					else if(firstTeam == 3)
					{
						team4JL.setText(td.get(3).getTeamName()+ "                         "+ "$" + td.get(3).getPoints());
					}
					else
					{
						System.out.println("Error! Getting team more than 4!!!");
						return;
					}
					int nextTeam = (firstTeam+1)%td.size();
					gameProcessJTA.append("Now it is " + td.get(nextTeam).getTeamName() + "'s turn.\n");

					chances = true;
				}
				gd.isAsked[tempj][tempi] = true;
				firstTeam++;
				firstTeam = firstTeam%td.size();//next team's round
				System.out.println("Now is "+ firstTeam + td.get(firstTeam).getTeamName());
				
				if(gd.isAllAsked(quickPlayChecked) == true)
				{
					List<TeamData> buffer = new ArrayList<>();
					buffer = getFinalists();
					if(buffer.size() == 0)
					{
						JOptionPane.showMessageDialog(null, "Everyone has negative points! No FJ and no winner.");
						return;
					}
					else
					{
						specificQ.setVisible(false);
						finalJeopardy();
					}
				}
				else
				{
					specificQ.setVisible(false);
					titleAndQuestions.setVisible(true);
				}
			}
		});
		
		//actions for final jeopardy buttons
		team1betJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team1betJB.setEnabled(false);
				team1bet = team1Slide.getValue();
				
				clickedSet++;
				if(clickedSet == howManyPlayFJ)
				{
					fjQuestionDisplay.setText("<html>" + gd.finalJeopardyQuestion + "</html>");
					enableFJSubmit();
				}
				
			}
		});
		
		team2betJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team2betJB.setEnabled(false);
				team2bet = team2Slide.getValue();
				
				clickedSet++;
				if(clickedSet == howManyPlayFJ)
				{
					fjQuestionDisplay.setText("<html>" + gd.finalJeopardyQuestion + "</html>");
					enableFJSubmit();
				}
			}
		});
		
		team3betJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team3betJB.setEnabled(false);
				team3bet = team3Slide.getValue();
				
				clickedSet++;
				if(clickedSet == howManyPlayFJ)
				{
					fjQuestionDisplay.setText("<html>" + gd.finalJeopardyQuestion + "</html>");
					enableFJSubmit();
				}
			}
		});
		
		team4betJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team4betJB.setEnabled(false);
				team4bet = team4Slide.getValue();
				
				clickedSet++;
				if(clickedSet == howManyPlayFJ)
				{
					fjQuestionDisplay.setText("<html>" + gd.finalJeopardyQuestion + "</html>");
					enableFJSubmit();
				}
			}
		});
		
		team1SubmitJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team1SubmitJB.setEnabled(false);
				clickedSubmit++;
				if(clickedSubmit == howManyPlayFJ)
				{
					checkFJAnswer();
				}
			}
		});
		
		team2SubmitJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team2SubmitJB.setEnabled(false);
				clickedSubmit++;
				if(clickedSubmit == howManyPlayFJ)
				{
					checkFJAnswer();
				}
			}
		});
		
		team3SubmitJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team3SubmitJB.setEnabled(false);
				clickedSubmit++;
				if(clickedSubmit == howManyPlayFJ)
				{
					checkFJAnswer();
				}
			}
		});
		
		team4SubmitJB.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				team4SubmitJB.setEnabled(false);
				clickedSubmit++;
				if(clickedSubmit == howManyPlayFJ)
				{
					checkFJAnswer();
				}
			}
		});
	}
	
	//function of showing the question detail when one question is selected
	public void questionAction(int i, int j)
	{
		if(gd.isAsked[j][i] == true)//if has been answered, then you cannot choose
		{
			JOptionPane.showMessageDialog(null, "This question has been answered! Please choose another one.");
			return;
		}
		
		allQ[i][j].setBackground(Color.green);
		
		titleAndQuestions.setVisible(false);
		
		qInfor.setText(td.get(firstTeam).getTeamName() + "       " + gd.categoryArray[j] + "    " + gd.moneyArray[i]);
		qInfor.setFont(new Font("Tahoma", Font.PLAIN, 40));
		qInfor.setHorizontalAlignment(SwingConstants.CENTER);
		specificQ.add(qInfor, BorderLayout.NORTH);
		
		questionShow.setText("<html>" + gd.questions[j][i].getQuestion() + "</html>");
		questionShow.setFont(new Font("Tahoma", Font.PLAIN, 50));
		specificQ.add(questionShow, BorderLayout.CENTER);
		
		reply.setPreferredSize( new Dimension( 700, 40 ) );
		reply.setFont(new Font("Tahoma", Font.PLAIN, 30));
		reply.setText("");
		specificQsouth.add(reply, BorderLayout.WEST);
		
		specificQsouth.add(submit, BorderLayout.EAST);
		
		specificQ.add(specificQsouth, BorderLayout.SOUTH);
		add(specificQ, BorderLayout.CENTER);
		specificQ.setVisible(true);
		
		gameProcessJTA.append("Team " + td.get(firstTeam).getTeamName() + " chooses Category "
				+ gd.categoryArray[j] + " worth " + gd.moneyArray[i]+ ".\n");
	}
	
	//if isAsked[5][5] has all been marked as true(answered), then we enter final jeopardy
	public void finalJeopardy()
	{
		//final jeopardy//////////////////////////
		fjTitle.setForeground(Color.GRAY);
		fjJP.add(fjTitle, BorderLayout.NORTH);
		fjJP.setBackground(Color.YELLOW);
		fjCenterJP.setBackground(Color.LIGHT_GRAY);
		fjSouthJP.setBackground(Color.CYAN);
		
		if(td.size() > 0 && td.get(0).getPoints() > 0)
		{
			howManyPlayFJ++;//now team 1 enter to fj
			
			team1Slide = new JSlider(JSlider.HORIZONTAL, 0, td.get(0).getPoints().intValue(), (td.get(0).getPoints().intValue())/10  );
			team1Slide.setPaintLabels(true);
			team1Slide.setPaintTicks(true);
			team1Slide.setMajorTickSpacing(td.get(0).getPoints().intValue()/10);
			team1Slide.setValue(0);
			team1Slide.setPreferredSize(new Dimension(700, 100));
			
			fjTeam1JL.setText(td.get(0).getTeamName()+"s bet:");
			fjTeam1JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team1betJL.setText("$" + team1Slide.getValue());
			team1Slide.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting())
					{
						team1betJL.setText("$" + team1Slide.getValue());
					}
				}
			});
			
			fjTeam1JP.add(fjTeam1JL);
			fjTeam1JP.add(team1Slide);
			fjTeam1JP.add(team1betJL);
			fjTeam1JP.add(team1betJB);
			fjCenterJP.add(fjTeam1JP);

			team1SJP.add(fjTeam1JTF);
			team1SJP.add(team1SubmitJB);
			submitJP.add(team1SJP);
			
			fjTeam1JTF.setText("Team " + td.get(0).getTeamName() + ", please enter your answer.");
			fjTeam1JTF.setFont(new Font("Tahome", Font.PLAIN, 20));
			
			fjTeam1JTF.addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent fe)
				{
					fjTeam1JTF.setText("");
				}
				
				@Override
				public void focusLost(FocusEvent fe)
				{
					
				}
			});
		}
		if(td.size() > 1 && td.get(1).getPoints() > 0)
		{
			howManyPlayFJ++;//now team 2 enter to fj
			
			team2Slide = new JSlider(JSlider.HORIZONTAL, 0, td.get(1).getPoints().intValue(), (td.get(1).getPoints().intValue())/10  );
			team2Slide.setPaintLabels(true);
			team2Slide.setPaintTicks(true);
			team2Slide.setMajorTickSpacing(td.get(1).getPoints().intValue()/10);
			team2Slide.setValue(0);
			team2Slide.setPreferredSize(new Dimension(700, 100));
			
			fjTeam2JL.setText(td.get(1).getTeamName()+"s bet:");
			fjTeam2JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team2betJL.setText("$" + team2Slide.getValue());
			team2Slide.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting())
					{
						team2betJL.setText("$" + team2Slide.getValue());
					}
				}
			});

			fjTeam2JP.add(fjTeam2JL);
			fjTeam2JP.add(team2Slide);
			fjTeam2JP.add(team2betJL);
			fjTeam2JP.add(team2betJB);
			fjCenterJP.add(fjTeam2JP);
			
			team2SJP.add(fjTeam2JTF);
			team2SJP.add(team2SubmitJB);
			submitJP.add(team2SJP);
			
			fjTeam2JTF.setText("Team " + td.get(1).getTeamName() + ", please enter your answer.");
			fjTeam2JTF.setFont(new Font("Tahome", Font.PLAIN, 20));
			
			fjTeam2JTF.addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent fe)
				{
					fjTeam2JTF.setText("");
				}
				
				@Override
				public void focusLost(FocusEvent fe)
				{
					
				}
			});
		}
		if(td.size() > 2 && td.get(2).getPoints() > 0)
		{
			howManyPlayFJ++;//team 3 in fj
			
			team3Slide = new JSlider(JSlider.HORIZONTAL, 0, td.get(2).getPoints().intValue(), (td.get(2).getPoints().intValue())/10  );
			team3Slide.setPaintLabels(true);
			team3Slide.setPaintTicks(true);
			team3Slide.setMajorTickSpacing(td.get(2).getPoints().intValue()/10);
			team3Slide.setValue(0);
			team3Slide.setPreferredSize(new Dimension(700, 100));
			
			fjTeam3JL.setText(td.get(2).getTeamName()+"s bet:");
			fjTeam3JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team3betJL.setText("$" + team3Slide.getValue());
			team3Slide.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting())
					{
						team3betJL.setText("$" + team3Slide.getValue());
					}
				}
			});
			
			fjTeam3JP.add(fjTeam3JL);
			fjTeam3JP.add(team3Slide);
			fjTeam3JP.add(team3betJL);
			fjTeam3JP.add(team3betJB);
			fjCenterJP.add(fjTeam3JP);
			
			team3SJP.add(fjTeam3JTF);
			team3SJP.add(team3SubmitJB);
			submitJP.add(team3SJP);
			
			fjTeam3JTF.setText("Team " + td.get(2).getTeamName() + ", please enter your answer.");
			fjTeam3JTF.setFont(new Font("Tahome", Font.PLAIN, 20));
			
			fjTeam3JTF.addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent fe)
				{
					fjTeam3JTF.setText("");
				}
				
				@Override
				public void focusLost(FocusEvent fe)
				{
					
				}
			});
		}
		if(td.size() > 3 && td.get(3).getPoints() > 0)
		{
			howManyPlayFJ++;//team 4 play fj
			
			team4Slide = new JSlider(JSlider.HORIZONTAL, 0, td.get(3).getPoints().intValue(), (td.get(3).getPoints().intValue())/10  );
			team4Slide.setPaintLabels(true);
			team4Slide.setPaintTicks(true);
			team4Slide.setMajorTickSpacing(td.get(3).getPoints().intValue()/10);
			team4Slide.setValue(0);
			team4Slide.setPreferredSize(new Dimension(700, 100));
			
			fjTeam4JL.setText(td.get(3).getTeamName()+"s bet:");
			fjTeam4JL.setFont(new Font("Tahoma", Font.PLAIN, 20));
			team4betJL.setText("$" + team4Slide.getValue());
			team4Slide.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					JSlider source = (JSlider)e.getSource();
					if(!source.getValueIsAdjusting())
					{
						team4betJL.setText("$" + team4Slide.getValue());
					}
				}
			});
			
			fjTeam4JP.add(fjTeam4JL);
			fjTeam4JP.add(team4Slide);
			fjTeam4JP.add(team4betJL);
			fjTeam4JP.add(team4betJB);
			fjCenterJP.add(fjTeam4JP);
			
			team4SJP.add(fjTeam4JTF);
			team4SJP.add(team4SubmitJB);
			submitJP.add(team4SJP);
			
			fjTeam4JTF.setText("Team " + td.get(3).getTeamName() + ", please enter your answer.");
			fjTeam4JTF.setFont(new Font("Tahome", Font.PLAIN, 20));
			
			fjTeam4JTF.addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent fe)
				{
					fjTeam4JTF.setText("");
				}
				
				@Override
				public void focusLost(FocusEvent fe)
				{
					
				}
			});
		}

		
		fjJP.add(fjCenterJP, BorderLayout.CENTER);
		
		fjQuestionDisplay.setText("And the question is...");
		fjQuestionDisplay.setFont(new Font("Tahoma", Font.PLAIN, 22));
		fjQuestionDisplay.setHorizontalAlignment(SwingConstants.CENTER);
		
		fjSouthJP.add(fjQuestionDisplay);
		
		fjSouthJP.add(submitJP);
		
		fjJP.add(fjSouthJP, BorderLayout.SOUTH);

		
		add(fjJP, BorderLayout.CENTER);
		fjJP.setVisible(true);
	}
	
	//allow those qualifed to submit fj answer
	public void enableFJSubmit()
	{
		if(td.get(0).getPoints()>0)
		{
			team1SubmitJB.setEnabled(true);
		}
		if(td.size()>1 && td.get(1).getPoints()>0)
		{
			team2SubmitJB.setEnabled(true);
		}
		if(td.size()>2 && td.get(2).getPoints()>0)
		{
			team3SubmitJB.setEnabled(true);
		}
		if(td.size()>3 && td.get(3).getPoints()>0)
		{
			team4SubmitJB.setEnabled(true);
		}
	}
	
	//check everyone's final jeopardy answer
	public void checkFJAnswer()
	{
		//process everyone's score///////////////////////////////
		if(td.get(0).getPoints()>0)
		{
			if(fjTeam1JTF.getText().toLowerCase().trim().equals("who is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam1JTF.getText().toLowerCase().trim().equals("who are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam1JTF.getText().toLowerCase().trim().equals("what is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam1JTF.getText().toLowerCase().trim().equals("what are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam1JTF.getText().toLowerCase().trim().equals("when is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam1JTF.getText().toLowerCase().trim().equals("when are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam1JTF.getText().toLowerCase().trim().equals("where is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam1JTF.getText().toLowerCase().trim().equals("where are " + gd.finalJeopardyAnswer.toLowerCase().trim()))
			{
				td.get(0).addPoints(team1bet);
				team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
			}
			else
			{
				td.get(0).deductPoints(team1bet);
				team1JL.setText(td.get(0).getTeamName()+ "                         "+ "$" + td.get(0).getPoints());
			}
		}
		if(td.size()>1 && td.get(1).getPoints()>0)
		{
			if(fjTeam2JTF.getText().toLowerCase().trim().equals("who is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam2JTF.getText().toLowerCase().trim().equals("who are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam2JTF.getText().toLowerCase().trim().equals("what is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam2JTF.getText().toLowerCase().trim().equals("what are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam2JTF.getText().toLowerCase().trim().equals("when is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam2JTF.getText().toLowerCase().trim().equals("when are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam2JTF.getText().toLowerCase().trim().equals("where is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam2JTF.getText().toLowerCase().trim().equals("where are " + gd.finalJeopardyAnswer.toLowerCase().trim()))
			{
				td.get(1).addPoints(team2bet);
				team2JL.setText(td.get(1).getTeamName()+ "                         "+ "$" + td.get(1).getPoints());
			}
			else
			{
				td.get(1).deductPoints(team2bet);
				team2JL.setText(td.get(1).getTeamName()+ "                         "+ "$" + td.get(1).getPoints());
			}
		}
		if(td.size()>2 && td.get(2).getPoints()>0)
		{
			if(fjTeam3JTF.getText().toLowerCase().trim().equals("who is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam3JTF.getText().toLowerCase().trim().equals("who are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam3JTF.getText().toLowerCase().trim().equals("what is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam3JTF.getText().toLowerCase().trim().equals("what are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam3JTF.getText().toLowerCase().trim().equals("when is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam3JTF.getText().toLowerCase().trim().equals("when are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam3JTF.getText().toLowerCase().trim().equals("where is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam3JTF.getText().toLowerCase().trim().equals("where are " + gd.finalJeopardyAnswer.toLowerCase().trim()))
			{
				td.get(2).addPoints(team3bet);
				team3JL.setText(td.get(2).getTeamName()+ "                         "+ "$" + td.get(2).getPoints());				
			}
			else
			{
				td.get(2).deductPoints(team3bet);
				team3JL.setText(td.get(2).getTeamName()+ "                         "+ "$" + td.get(2).getPoints());	
			}
		}
		if(td.size()>3 && td.get(3).getPoints()>0)
		{
			if(fjTeam4JTF.getText().toLowerCase().trim().equals("who is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam4JTF.getText().toLowerCase().trim().equals("who are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam4JTF.getText().toLowerCase().trim().equals("what is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam4JTF.getText().toLowerCase().trim().equals("what are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam4JTF.getText().toLowerCase().trim().equals("when is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam4JTF.getText().toLowerCase().trim().equals("when are " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam4JTF.getText().toLowerCase().trim().equals("where is " + gd.finalJeopardyAnswer.toLowerCase().trim())
					|| fjTeam4JTF.getText().toLowerCase().trim().equals("where are " + gd.finalJeopardyAnswer.toLowerCase().trim()))
			{
				td.get(3).addPoints(team4bet);
				team4JL.setText(td.get(3).getTeamName()+ "                         "+ "$" + td.get(3).getPoints());	
			}
			else
			{
				td.get(3).deductPoints(team4bet);
				team4JL.setText(td.get(3).getTeamName()+ "                         "+ "$" + td.get(3).getPoints());	
			}
		}
		//////////////////////////////////////////////////////////
		//display the winner
		List<TeamData> finalTeams = getFinalists();
		if(finalTeams.isEmpty() == true)
		{
			JOptionPane.showMessageDialog(null, "Everyone has negative points! No winner!");
			return;
		}
		ArrayList<Integer> winners = getWinners(finalTeams);
		
		String toPrint = winners.size() > 1 ? "And the winners are " : "And the winner is ";
		String fullToPrint = toPrint;

		if (winners.size() > 1)
		{
			for (int i = 0; i<winners.size(); i++)
			{
				if(i == 1)
				{
					fullToPrint+= finalTeams.get(winners.get(i)).getTeamName() + " ";
				}
				else
				{
					fullToPrint+= "and " + finalTeams.get(winners.get(i)).getTeamName() + " ";
				}
			}
		}
		JOptionPane.showMessageDialog(null,  fullToPrint + finalTeams.get(winners.get(0)).getTeamName());
		/////////////////////////////////////////////////////////
	}
	
	//this one is from gameplay, I use a arraylist to store the index of the qualified teams for final jeopardy
	public List<TeamData> getFinalists()
	{
		List<TeamData> finalTeams = new ArrayList<>();
		
		for (int i = 0; i<td.size(); i++)
		{	
			TeamData team = td.get(i);	
			if (team.getPoints() > 0)
			{
				finalTeams.add(team);
			}
			else
			{
				System.out.println("Sorry, "+team.getTeamName()+", you have been eliminated from the game!");
			}
		}
		
		return finalTeams;
	}
	
	//this one is from gameplay, I use a arraylist to store the index of the wining teams
	public ArrayList<Integer> getWinners(List<TeamData> finalTeams)
	{
		ArrayList<Integer> winners = new ArrayList<>();
		int winScore = -100;
		for(int i = 0; i < finalTeams.size(); i++)//find the max point
		{
			if(finalTeams.get(i).getPoints() >= winScore)
			{
				winScore = finalTeams.get(i).getPoints().intValue();
			}
		}
		
		for(int i = 0; i < finalTeams.size(); i++)//any one has that max point wins
		{
			if(finalTeams.get(i).getPoints() == winScore)
			{
				winners.add(i);
			}
		}
		return winners;
	}

}