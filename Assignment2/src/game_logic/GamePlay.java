package game_logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GamePlay extends GameData{
	private int numberOfTeams;
	private int whoseTurn;
	
	//how many questions have been chosen
	private int numberOfChosenQuestions;
	//total points for each team, each TeamPoints object holds team index, team points, and team name
	List<TeamData> teamData;
	
	private static Set<String> unmodifiableSetAnswerVerbs;
	private static Set<String> unmodifiableSetAnswerNouns;
	private static final String EXIT = "exit";
	private static final String RESTART = "restart";
	
	
	//DOES NOT IMPLEMENT EXIT AND REPLAY LOGIC
	public GamePlay(String fileName) {
		super(fileName);
		br = new BufferedReader(new InputStreamReader(System.in));
		//initialize private variables
		numberOfChosenQuestions = 0;
		teamData = new ArrayList<>();
		
		initializeAnswerFormatSet();
		chooseTeams();
		playGame();
	}
	
	private void playGame(){
		startGame();
		askQuestions();
		playFinalJeopardy();
	}
	
	public void initializeAnswerFormatSet(){
		Set<String> nounsModifiableSet = new HashSet<>();
		Set<String> verbsModifiableSet = new HashSet<>();
		nounsModifiableSet.add("who");
		nounsModifiableSet.add("where");
		nounsModifiableSet.add("when");
		nounsModifiableSet.add("what");
		verbsModifiableSet.add("is");
		verbsModifiableSet.add("are");
		
		unmodifiableSetAnswerNouns = Collections.unmodifiableSet(nounsModifiableSet);
		unmodifiableSetAnswerVerbs = Collections.unmodifiableSet(verbsModifiableSet);
	}
	
	//increment whose turn it is
	private int nextTurn(int currentTurn){

		return (currentTurn + 1) == numberOfTeams ? 0 : currentTurn + 1;
	}
	
	//check whether the answer is in the format of a question
	private boolean validAnswerFormat(String answer){
		
		if (answer.length() < 1) return false;
		
		String[] splitAnswer = answer.trim().split("\\s+");
		
		if (splitAnswer.length < 2) return false;
		
		return unmodifiableSetAnswerVerbs.contains(splitAnswer[1].toLowerCase()) && unmodifiableSetAnswerNouns.contains(splitAnswer[0].toLowerCase());
	}
	
	private void chooseTeams(){
		
		System.out.println("Welcome to Jeopardy!");
		System.out.println("Please enter the number of teams that will be playing in this game");
		
		//determines whether the user gave a valid input
		boolean successful = false;
		
		while (!successful){
			
			try{
				String input = br.readLine();
				checkForRestartOrExit(input);
				numberOfTeams = Integer.parseInt(input);
				//this syntax is used throughout the code, so if you don't know how it works, look at the following comment
				// conditional ? true condition action : false condition action
				successful = (numberOfTeams>0) && (numberOfTeams<5) ? true : false;
			}
			catch(IOException ioe){}
			catch(NumberFormatException nfe){}
			finally{
				
				if (!successful){
					System.out.println("Invalid entry; Please try again.");
				}		
			}
		
		}
		
		teamData = new ArrayList<>(numberOfTeams);
		Set<String> duplicateTeamNamesCheck = new HashSet<>();

		//choose team names
		for (int i = 1; i <= numberOfTeams; i++){
			
			successful = false;
			
			while (!successful){
				System.out.println("Please choose a name for Team "+i);
				
				try {
					String input = br.readLine();
					
					if (duplicateTeamNamesCheck.contains(input.toLowerCase())){
						System.out.println("This name has already been chosen by another team. Please choose something else.");
					}
					else if (input.trim().equals("")){
						System.out.println("Your team name cannot be white space.");
					}
					else{
						teamData.add(new TeamData(i-1, 0L, input));
						duplicateTeamNamesCheck.add(input.toLowerCase());
						successful = true;
					}

				} 
				catch (IOException e) {}
				finally{
					
					if (!successful){
						System.out.println("Invalid Entry; Please try again.");
					}
				}
			}
		}
		
	}
	
	private void startGame()
	{
		System.out.println("Ready to Play!");
		Random rand = new Random();
		int firstTeam = rand.nextInt(numberOfTeams);
		System.out.println("The team to go first will be "+teamData.get(firstTeam).getTeamName());
		
		whoseTurn = firstTeam;
	}
	
	private void checkForRestartOrExit(String line){
		
		line = line.trim().toLowerCase();
		
		if (line.equals(EXIT)){
			System.exit(0);
		}
		
		else if (line.equals(RESTART)){
			resetData();
			playGame();
		}
	}
	
	private void resetData(){
		whoseTurn = -1;
		numberOfChosenQuestions = 0;
		//total points for each team, each TeamPoints object holds team index, team points, and team name
		for (TeamData team : teamData){
			team.setPoints(0);
		}
		
		for (int i = 0; i<5; i++){
			
			for (int j = 0; j<5; j++){
				questions[i][j].resetHasBeenAsked();
			}
		}
		
	}
	
	private void askQuestions(){
		
		while (numberOfChosenQuestions < 25){
			
			System.out.println("Please choose a Category");
			
			String category = "";
			Integer pointValue = -1;
			int xIndex = -1;
			int yIndex = -1;
			boolean successfulChoice = false;
			
			//must loop until valid category and pointValue are chosen
			while (!successfulChoice){
				boolean validInputs= false;
				//loops until input for category from user is valid
				while (!validInputs){
					
					try {
						
						String input = br.readLine();
						checkForRestartOrExit(input);
						category = input.toLowerCase();
						validInputs = categoriesMap.containsKey(category.toLowerCase()) ? true : false;
						
					} 
					catch (IOException e) {}
					finally{
						
						if (!validInputs){
							System.out.println("Invalid Category choice; Please try again.");
						}
					}
				}
				
				System.out.println("Please enter the dollar value of the question you wish to answer.");
				validInputs = false;
				
				//loops until input for point value from user is valid
				while (!validInputs){
					
					try {
						String input = br.readLine();
						checkForRestartOrExit(input);
						pointValue = Integer.parseInt(input);
						validInputs = pointValuesMapToIndex.containsKey(pointValue) ? true : false;
		
					}
					catch (NumberFormatException e) {}
					catch (IOException e) {}
					finally{
						
						if (!validInputs){
							System.out.println("Invalid dollar value; Please try again.");
						}
					}
					
				}
				
				xIndex = categoriesMap.get(category.trim()).getIndex();
				yIndex = pointValuesMapToIndex.get(pointValue);
				
				//check whether this question has been asked already
				successfulChoice = !questions[xIndex][yIndex].hasBeenAsked();
				
				if (!successfulChoice){
					System.out.println("This question has already been chosen; Please try again.");
				}
			}
			
			answerQuestion(xIndex, yIndex, pointValue);
			if (numberOfChosenQuestions != 25) System.out.println("It is "+teamData.get(whoseTurn).getTeamName()+"'s turn to choose a question!");
		}

	}
	
	private void answerQuestion(int xIndex, int yIndex, int pointValue){
		
		questions[xIndex][yIndex].setHasBeenAsked();
		System.out.println(questions[xIndex][yIndex].getQuestion());
		System.out.println("Please enter your answer. Remember to pose it as a question.");
		
		String givenAnswer = "";
		String expectedAnswer = questions[xIndex][yIndex].getAnswer();
		boolean skipAnswerCheck = false;
		
		try {
			givenAnswer = br.readLine();
			checkForRestartOrExit(givenAnswer);
			
			//if the answer is not in a question format, give them another chance
			if (!validAnswerFormat(givenAnswer)){
				System.out.println("Invalid question format. Remember to pose your answer as a question.");
				
				try{
					givenAnswer = br.readLine();
					checkForRestartOrExit(givenAnswer);
					//if still not valid format, mark question as incorrect
					if (!validAnswerFormat(givenAnswer.trim())){
						
						System.out.println("Invalid question format. Your answer will be marked as incorrect. "+pointValue+" will be deducted from your total.");
						teamData.get(whoseTurn).deductPoints(pointValue);
						skipAnswerCheck = true;
					}
				}
				catch (IOException e){
					System.out.println("Something went wrong!");
				}
			}
			
		} catch (IOException e) {
			System.out.println("Something went wrong!");
		}
		
		//if we have not already deducted points because of format, actually check validity of their answer
		if (!skipAnswerCheck){
			
			if (!givenAnswer.toLowerCase().endsWith(expectedAnswer.toLowerCase())){
				System.out.println("You got the answer wrong! "+pointValue+" will be deducted from your total.");
				System.out.println("The expected answer was: "+expectedAnswer);
				teamData.get(whoseTurn).deductPoints(pointValue);
			}
			
			else{
				
				System.out.println("You got the answer right! "+pointValue+" will be added to your total. ");
				teamData.get(whoseTurn).addPoints(pointValue);
			}	
		}
		
		Output.printScores(numberOfTeams, teamData);
		
		if (numberOfChosenQuestions == 25){
			System.out.println("All the questions have been chosen. Time for final jeopardy!");
		}
		
		else{
			whoseTurn = nextTurn(whoseTurn);
		}
		
		numberOfChosenQuestions++;	
	}
	
	private void makeBets(List<TeamData> finalists){
		boolean successfulBet = false;
		
		//let each team make a bet for final jeopardy
		for (int i = 0; i<finalists.size(); i++){
			TeamData currentTeam = finalists.get(i);
			
			while (!successfulBet){
				System.out.println("Team "+currentTeam.getTeamName()+", please give a dollar amount from your total that you would like to bet");
				
				try {
					String input = br.readLine();
					checkForRestartOrExit(input);
					long tempBet = Integer.parseInt(input);
					successfulBet = (tempBet>0) && (tempBet < (currentTeam.getPoints() + 1)) ? true : false;
					currentTeam.setBet(tempBet);
				} 
				catch (NumberFormatException e) {} 
				catch (IOException e) {}
				finally{
					
					if (!successfulBet){
						System.out.println("Invalid bet; Please try again.");
					}
				}
			}
			
			successfulBet = false;
		}

	}
	
	private List<TeamData> getFinalists(){
		List<TeamData> finalTeams = new ArrayList<>();
		
		for (int i = 0; i<teamData.size(); i++){
			
			TeamData team = teamData.get(i);
			
			if (team.getPoints() > 0){
				finalTeams.add(team);
			}
			
			else{
				System.out.println("Sorry, "+team.getTeamName()+", you have been eliminated from the game!");
			}
		}
		
		return finalTeams;
	}
	
	private void playFinalJeopardy(){
		
		System.out.println("Welcome to Final Jeopardy!");
		//get the finalists for the round
		List<TeamData> finalTeams = getFinalists();
		
		if (finalTeams.isEmpty()){
			exit ("None of the teams made it to the Final Jeopardy round! Nobody wins, GAME OVER!");
		}
		
		//have all the teams make a bet
		makeBets(finalTeams);
		
		System.out.println("The question is: ");
		System.out.println(finalJeopardyQuestion);
		
		//have each question provide an answer and calculate their new score
		for (int i = 0; i<finalTeams.size(); i++){
			TeamData currentTeam = finalTeams.get(i);
			System.out.println("Team "+currentTeam.getTeamName()+", please enter your answer.");
			String answer = "";
			
			try {
				answer = br.readLine();
				checkForRestartOrExit(answer);
				
				if (!validAnswerFormat(answer.trim())){
					System.out.println("Invalid question format. Your answer will be marked as incorrect");
					currentTeam.deductPoints(currentTeam.getBet());
				}
				
				else{
					
					if (answer.toLowerCase().endsWith(finalJeopardyAnswer.toLowerCase())){
						currentTeam.addPoints(currentTeam.getBet()); 
					}
					
					else{
						currentTeam.deductPoints(currentTeam.getBet());
					}
				}
				
			} 
			catch (IOException e) {
				System.out.println("Something went wrong!");
			}
			
			Output.printScores(finalTeams.size(), finalTeams);
		}
		
		ArrayList<Integer> winners = getWinners(finalTeams);
		
		if (winners.size() == 0){
			System.out.println("There were no winners!");
		}
		
		else{
			String toPrint = winners.size() > 1 ? "And the winners are " : "And the winner is ";
			System.out.print(toPrint + finalTeams.get(winners.get(0)).getTeamName());
			
			if (winners.size() > 1){
				
				for (int i = 1; i<winners.size(); i++){
					System.out.print(", " + finalTeams.get(winners.get(i)).getTeamName());
				}
			}
		}
	}
	
	
	private ArrayList<Integer> getWinners(List<TeamData> finalTeams){
		
		//sorts the finalists in order of their total score
		Collections.sort(finalTeams, TeamData.getComparator());
		ArrayList<Integer> winners = new ArrayList<>();
		
		//the team at the end of the list must have the highest score and is definitely a winner
		TeamData definiteWinnerObject = finalTeams.get(finalTeams.size() - 1);
		int definiteWinner = definiteWinnerObject.getTeam();
		long max = definiteWinnerObject.getPoints();
		//if the max score is 0, we know that no one won
		if (max == 0) return winners;
		
		winners.add(definiteWinner);
		
		//check to see if there are other winners
		if (finalTeams.size() > 1){
			
			for (int i = finalTeams.size() -2; i > -1; i--){
				
				if (finalTeams.get(i).getPoints() == max){
					winners.add(finalTeams.get(i).getTeam());
				}
			}	
		}
		
		return winners;
	}

}
