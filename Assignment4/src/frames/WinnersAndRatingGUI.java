package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import game_logic.GameData;
import game_logic.GameFile;
import game_logic.TeamData;
import listeners.ExitWindowListener;
import other_gui.AppearanceConstants;
import other_gui.AppearanceSettings;

public class WinnersAndRatingGUI extends JFrame{

	private JLabel andTheWinnersAre;
	private JTextPane winners;
	private GameData gameData;
	private JButton okay;
	private JLabel ratingChoiceLabel;
	private JSlider ratingSlider;
	
	public WinnersAndRatingGUI(GameData gameData){
		
		this.gameData = gameData;
		initializeComponents();
		createGUI();
		addListeners();
	}
	
	//private methods
	private void initializeComponents(){
		andTheWinnersAre = new JLabel("");
		winners = new JTextPane();
		okay = new JButton("Okay");
		ratingChoiceLabel = new JLabel("3");
		ratingSlider = new JSlider();
	}
	
	private void createGUI(){
		//initialize local variables
		JPanel mainPanel = new JPanel(new GridLayout(3, 1));
		JLabel ratingInstructionsLabel = new JLabel ("Please rate this game file on a scale from 1 to 5", JLabel.CENTER);
		JLabel currentRatingLabel = new JLabel(gameData.getGameFile().getNumberOfRatings() == -1 ? "no rating" : "current average rating : " + gameData.getGameFile().getAverageRating()+"/5", JLabel.CENTER);
		JPanel ratingInstructionsPanel = new JPanel();
		JPanel sliderPanel = new JPanel(new BorderLayout());
		JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
		JPanel okayPanel = new JPanel();
		
		//set appearance of all local and member variables
		AppearanceSettings.setBackground(AppearanceConstants.lightBlue, okayPanel, winners, andTheWinnersAre, ratingChoiceLabel,mainPanel, sliderPanel, ratingInstructionsLabel, currentRatingLabel, ratingInstructionsPanel);
		AppearanceSettings.setBackground(Color.darkGray, winners, ratingChoiceLabel, okay);
		
		AppearanceSettings.setFont(AppearanceConstants.fontMedium, okay, andTheWinnersAre, currentRatingLabel);
		AppearanceSettings.setFont(AppearanceConstants.fontSmall, ratingChoiceLabel, ratingInstructionsLabel);
		
		AppearanceSettings.setOpaque(currentRatingLabel, okay, winners, ratingChoiceLabel, andTheWinnersAre);
		AppearanceSettings.setSliders(1, 5, 3, 1, AppearanceConstants.fontSmall, ratingSlider);
		AppearanceSettings.setForeground(Color.lightGray, okay, winners, ratingChoiceLabel);
		AppearanceSettings.setTextAlignment(ratingInstructionsLabel, currentRatingLabel, ratingChoiceLabel, andTheWinnersAre);
		
		//other appearance settings
		okay.setEnabled(false);
		okay.setBorder(null);
		okay.setPreferredSize(new Dimension(70, 70));
		
		winners.setFont(AppearanceConstants.fontLarge);
		winners.setEditable(false);
		winners.setPreferredSize(new Dimension(600, 100));
		setWinnersText();
		
		ratingChoiceLabel.setPreferredSize(new Dimension(60, 100));
		
		//centers the text
		//sourced from: http://stackoverflow.com/questions/3213045/centering-text-in-a-jtextarea-or-jtextpane-horizontal-text-alignment
		StyledDocument doc = winners.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		//adding components to containers
		okayPanel.add(okay);
		
		sliderPanel.add(ratingSlider, BorderLayout.CENTER);
		sliderPanel.add(ratingChoiceLabel, BorderLayout.EAST);
		
		ratingInstructionsPanel.setLayout(new BoxLayout(ratingInstructionsPanel, BoxLayout.PAGE_AXIS));
		ratingInstructionsPanel.add(ratingInstructionsLabel);
		ratingInstructionsPanel.add(sliderPanel);

		bottomPanel.add(currentRatingLabel);
		bottomPanel.add(okayPanel);
		
		topPanel.add(andTheWinnersAre);
		topPanel.add(winners);
		
		mainPanel.add(topPanel);
		mainPanel.add(ratingInstructionsPanel);
		mainPanel.add(bottomPanel);

		setSize(600, 600);
		add(mainPanel, BorderLayout.CENTER);
	}
	
	//sets the text of the winners label to who the winners are (if any)
	private void setWinnersText(){

		List<TeamData> winnersList = gameData.getFinalistsAndEliminatedTeams().getWinners();
		//no winners
		if (winnersList.size() == 0){
			andTheWinnersAre.setText("Sad!");
			winners.setText("There were no winners!");
		}
		//at least 1 winner
		else{
			String winnersAre = winnersList.size() == 1 ? "And the winner is..." : "And the winners are...";
			//string builder to create string with all the winners
			StringBuilder teamsBuilder = new StringBuilder();
			teamsBuilder.append(winnersList.get(0).getTeamName());
			
			for (int i = 1; i<winnersList.size(); i++){
				teamsBuilder.append(" and "+winnersList.get(i).getTeamName());
			}
			
			andTheWinnersAre.setText(winnersAre);
			winners.setText(teamsBuilder.toString());
		}
		
	}
	
	//writing back to the game file to update rating data
	private void writeToFile(){
		//game file object that was stored in game data
		GameFile gameFile = gameData.getGameFile();
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			//getting file path from the game file object
			fw = new FileWriter(gameFile.getFilePath());
			bw = new BufferedWriter(fw);
			//writing out all the lines of the game file 
			for (String line : gameFile.getFileLines()){
				bw.write(line);
				bw.newLine();
			}
			//if the game has never been rated before
			if (gameFile.getNumberOfRatings() == -1){
				bw.write(Integer.toString(1));
				bw.newLine();
				bw.write(Integer.toString(ratingSlider.getValue()));
			}
			//game has been rated before
			else{
				bw.write(Integer.toString(gameFile.getNumberOfRatings()+1));
				bw.newLine();
				bw.write(Integer.toString(gameFile.getTotalRating()+ratingSlider.getValue()));
			}
			//flush the stream
			bw.flush();
		} 
		
		catch (IOException e) {}
		
		finally{
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} 
			
			catch (IOException e) {}
		}
	}
	
	private void addListeners(){
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//add windowlistener
		addWindowListener(new ExitWindowListener(this));
		//action listener
		okay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				writeToFile();
				dispose();
			}
			
		});
		//change listener
		ratingSlider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				okay.setEnabled(true);
				ratingChoiceLabel.setText(Integer.toString(ratingSlider.getValue()));
			}
			
		});
	}
}
