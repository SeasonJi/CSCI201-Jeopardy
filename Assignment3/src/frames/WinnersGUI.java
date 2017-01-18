package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import game_logic.GameData;
import game_logic.TeamData;
import listeners.ExitWindowListener;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;

public class WinnersGUI extends JFrame{

	private JLabel andTheWinnersAre;
	private JTextPane winners;
	private GameData gameData;
	private JButton okay;
	
	private JPanel centerPanel, innerPanel;
	private JSlider rateSlider;
	private JLabel ratePromptLabel;
	private JLabel currentRateLabel;
	private JLabel averageLabel;
	
	public WinnersGUI(GameData gameData){
		super("Rate this game");
		setSize(600,600);
		setLocation(20,200);
		
		this.gameData = gameData;
		initializeComponents();
		createGUI();
		addListeners();
	}
	
	//private methods
	private void initializeComponents(){
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(3,1));
		innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		
		andTheWinnersAre = new JLabel("");
		andTheWinnersAre.setHorizontalAlignment(SwingConstants.CENTER);

		winners = new JTextPane();
		okay = new JButton("Okay");
		okay.setEnabled(false);
		
		
		rateSlider = new JSlider();
		rateSlider.setFont(new Font("Serif", Font.BOLD, 15));
		rateSlider.setMaximum(5);
		rateSlider.setMinimum(1);
		rateSlider.setValue(1);
		rateSlider.setMajorTickSpacing(1);
		rateSlider.setPaintTicks(true);
		rateSlider.setPaintLabels(true);
		rateSlider.setPreferredSize(new Dimension(450, 100));
		
		ratePromptLabel = new JLabel("Please rate this game file from 1 to 5:");
		ratePromptLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		ratePromptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		currentRateLabel = new JLabel("1");
		currentRateLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		currentRateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		currentRateLabel.setPreferredSize(new Dimension(150, 100));
		
		averageLabel = new JLabel("current average rating: ");
		if(gameData.getRatePeople() == -1 || gameData.getTotalRate() == -1)
		{
			averageLabel.setText("current average rating: 0/5");
		}
		else
		{
			averageLabel.setText("current average rating: " + (gameData.getTotalRate()/gameData.getRatePeople()) + "/5" );
		}
		
		
		averageLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		averageLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	private void createGUI(){
		JPanel topPanel = new JPanel(new BorderLayout());
		//JPanel northPanel = new JPanel();
		
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, winners, andTheWinnersAre, topPanel, centerPanel, innerPanel, rateSlider);
		currentRateLabel.setBackground(AppearanceConstants.mediumGray);
		currentRateLabel.setForeground(Color.WHITE);
		currentRateLabel.setOpaque(true);

		AppearanceSettings.setFont(AppearanceConstants.fontMedium, okay, andTheWinnersAre);
		
		winners.setEditable(false);
		//centers the text
		//sourced from: http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment
		StyledDocument doc = winners.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		winners.setFont(AppearanceConstants.fontLarge);
		okay.setBackground(Color.gray);
		okay.setForeground(AppearanceConstants.darkBlue);
		
		//northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.PAGE_AXIS));
		
		List<TeamData> winnersList = gameData.getFinalistsAndEliminatedTeams().getWinners();
		//no winners
		if (winnersList.size() == 0){
			andTheWinnersAre.setText("Sad!");		
			winners.setText("There were no winners!");
		}
		//at least 1 winner
		else{
			String winnersAre = winnersList.size() == 1 ? "And the winner is..." : "And the winners are...";
			StringBuilder teamsBuilder = new StringBuilder();
			teamsBuilder.append(winnersList.get(0).getTeamName());
			
			for (int i = 1; i<winnersList.size(); i++){
				teamsBuilder.append(" and "+winnersList.get(i).getTeamName());
			}
			
			andTheWinnersAre.setText(winnersAre);
			winners.setText(teamsBuilder.toString());
		}
		
		topPanel.add(andTheWinnersAre, BorderLayout.NORTH);
		topPanel.add(winners, BorderLayout.CENTER);
		//topPanel.add(okay, BorderLayout.SOUTH);
		
		
		innerPanel.add(rateSlider);
		//innerPanel.add(Box.createGlue());
		innerPanel.add(currentRateLabel);
		centerPanel.add(ratePromptLabel);
		centerPanel.add(innerPanel);
		centerPanel.add(averageLabel);
		
		//setSize(300, 300);
		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(okay, BorderLayout.SOUTH);
	}
	
	private void addListeners(){
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindowListener(this));
		
		okay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int newRate = rateSlider.getValue();
				
				int updateTotalPeople = gameData.getRatePeople();
				if(updateTotalPeople == -1)
				{
					updateTotalPeople = 0;
				}
				int updateTotalRate = gameData.getTotalRate();
				if(gameData.getTotalRate() == -1)
				{
					updateTotalRate = 0;
				}
												
				updateTotalPeople++;
				updateTotalRate += newRate;
												
				ArrayList<String> reWrite = new ArrayList<String>();

				try {
					BufferedReader br = new BufferedReader(new FileReader(gameData.getFilename()));
					String temp;
					
					while((temp=br.readLine()) != null)
					{
						reWrite.add(temp);
					}
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				reWrite.remove(reWrite.size()-2);
				reWrite.remove(reWrite.size()-1);
				reWrite.add(Integer.toString(updateTotalPeople));
				reWrite.add(Integer.toString(updateTotalRate));
				
				try {
					PrintWriter pw = new PrintWriter(gameData.getFilename());
					for(String s : reWrite)
					{
						pw.println(s);
					}
					pw.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				dispose();		
				
			}
			
		});
		
		rateSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent ce)
			{
				okay.setEnabled(true);//okay button is only enabled when slider has changed!
				currentRateLabel.setText(Integer.toString(rateSlider.getValue()));
			}
		});
		
	}
}
