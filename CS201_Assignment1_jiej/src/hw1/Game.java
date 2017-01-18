package hw1;

import java.util.Scanner;

import java.io.*;

public class Game {
	
	public static void gameExit()
	{
		System.exit(0);
	}
	
	
	public static void main(String [] args) throws IOException 
	{
		////////////File Reading Part starts//////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////
		System.out.println("Welcome to Jeopardy!");		
		FileReader fr;
		BufferedReader br;
		
		Store st = new Store();
		
		try
		{
			fr = new FileReader(args[0]);
			//fr = new FileReader("sports.txt");
			//fr = new FileReader("alt.txt");
			br = new BufferedReader(fr);
			
			//read categories
			String buffer = br.readLine();
			st.addCategory(buffer);
			
			//read money
			buffer = br.readLine();
			st.addPointValue(buffer);
			
			buffer = br.readLine();//read in first question
			
			while(buffer != null)
			{	//read the next line in advance to see if 
				//the question finish in one line or two
				String next = br.readLine();

				
				if(next == null)//reach the end
				{
					st.addQuestion(buffer);
					break;
				}
				else if(next.substring(0, 2).equals("::"))//question has just one line
				{
					st.addQuestion(buffer);
					buffer = next;
				}
				else
				{
					buffer = buffer + next;//multiple lines
				}
					
			}
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("FileNotFoundException: " + fnfe.getMessage());
		}
		catch(IOException ioe)
		{
			System.out.println("IOException: " + ioe.getMessage());
		}
		
		if(st.questionCounter != 26)
		//if(st.questionCounter != 5)
		{
			System.out.println("Not exactly 26 questions! Terminate now.");
			return;
		}
		
		System.out.println("Data reading succeeds! Please enter the number of teams that will be playing the game: ");

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader nbr = new BufferedReader(isr);
		
		int howManyTeam;
		
		while(true)
		{
			String line = nbr.readLine();
			try
			{
				howManyTeam = Integer.parseInt(line);
			}
			catch(NumberFormatException nfe)
			{
				System.out.println("Invalid entry! Should enter number.");
				continue;
			}
			System.out.println("You entered " + howManyTeam);
			
			if(howManyTeam < 1 || howManyTeam > 4)
			{
				System.out.println("Wrong number of teams, please enter between 1 and 4.");
			}
			else
			{
				System.out.println("Correct number of teams!");
				break;
			}
		}
		/////////////////////File Reading Part Ends//////////////////////////////////////
		/////////////////////Normal Round Starts/////////////////////////////////////////
		GameProcess gp = new GameProcess(howManyTeam);
		
		
		for(int i = 0; i < howManyTeam; i++)
		{
			System.out.println("Please choose a name for Team" + (i+1));
			String tName = nbr.readLine();
			gp.setName(i, tName);
		}
		System.out.println("Thank you! Setting up game for you.");		
		
		Scanner s = new Scanner(System.in);
		String c;//category
		String v;//value
		
		//i learned this method from online
		//basically if I wanna replay, I start the while loop again
		//by entering "break outerloop"
		boolean isFinished = false;
		while(isFinished == false)
		{
			int qCounter = 0;//counter for how many questions have been answered
			int roundCounter = 0;//counter for which round it is now
			st.stReset();
			gp.gpReset();
			
			outerloop:
			while(true)
			{
				while(qCounter != 25)
				{

						if(roundCounter == 0)
						{
							System.out.println("The team go first will be " + gp.currentTeam());
						}
						else
						{
							System.out.println("The next team is " + gp.nextTeam());
						}
						
						while(true)
						{
							while(true)
							{
								System.out.println("Please choose a category: ");
								c = s.nextLine().toLowerCase();
								////////////exit//////////////
								if(c.toLowerCase().equals("exit"))
								{
									gameExit();
								}
								//////////////////////////////
								
								//////////////////replay///////////////
								if(c.toLowerCase().equals("replay"))
								{
									break outerloop;
								}
								///////////////////////////////////////
								
								if(st.hasCategory(c) == false)
								{
									System.out.println("Cannot find this Category! Please try again.");
								}
								else
								{
									break;
								}
							}
							
							while(true)
							{
								System.out.println("Please choose a dollar value: ");
								v = s.nextLine().toLowerCase();
								////////////exit////////////
								if(v.toLowerCase().equals("exit"))
								{
									gameExit();
								}
								///////////////////////////
								//////////////////replay///////////////
								if(v.toLowerCase().equals("replay"))
								{
									break outerloop;//now restart the whole loop
								}
								///////////////////////////////////////
								if(st.hasPoint(v) == false)
								{
									System.out.println("Cannot find this value! Please try again.");
								}
								else
								{
									break;
								}
							}
							
							if(st.isAsked(c+v) == true)
							{
								System.out.println("This question has already been answered! Please choose another one.");
							}
							else
							{
								break;
							}
						}
					
						String ask = st.showQuestion(c+v);		
						System.out.println(ask);
						
						System.out.println("Please enter your answer. Remember to enter it as a question.");
						String reply = s.nextLine();
						///////////////exit///////////////////
						if(reply.toLowerCase().equals("exit"))
						{
							gameExit();
						}
						//////////////////////////////////////
						//////////////////replay///////////////
						if(reply.toLowerCase().equals("replay"))
						{
							break outerloop;
						}
						///////////////////////////////////////
						String [] ansBuff = reply.split(" ");
						
						if( ( (ansBuff[0].toLowerCase().equals("what"))==false && (ansBuff[0].toLowerCase().equals("who"))==false 
								&& (ansBuff[0].toLowerCase().equals("where"))==false              
								&& (ansBuff[0].toLowerCase().equals("when"))==false ) 
								|| ( (ansBuff[1].toLowerCase().equals("is"))==false && (ansBuff[1].toLowerCase().equals("are"))==false ) )
						{
							System.out.println("Wrong answer format! Please answer it as a question.");
							reply = s.nextLine();
							///////////////exit///////////////////
							if(reply.toLowerCase().equals("exit"))
							{
								gameExit();
							}
							//////////////////////////////////////
							//////////////////replay///////////////
							if(reply.toLowerCase().equals("replay"))
							{
								break outerloop;
							}
							///////////////////////////////////////
						}
						
						boolean result = st.compareAnswer(c+v, reply);
						if(result == true)
						{
							System.out.println("You got the answer right! " + v + " will be added to your total." );
							int realPoint = Integer.parseInt(v);
							gp.setScore(gp.currentTeam(), realPoint);
							
						}
						else
						{
							System.out.println("Wrong answer! -" + v + " points!");
							int realPoint1 = Integer.parseInt(v);
							int realPoint2 = realPoint1*(-1);
							gp.setScore(gp.currentTeam(), realPoint2);
						}
						
						st.setAsked(c+v);//this question cannot be chosen again
						
						System.out.println("Here are the updated scores: ");
						System.out.println("------------------------------");
						for(int i = 0; i < howManyTeam; i++)
						{
							System.out.println(gp.getName(i) + ":" + gp.getScore(i)+ " ");
						}

						System.out.println("------------------------------");
						
						qCounter++;
						roundCounter++;
						
				}
				
				////////////////////////////Normal rounds end!! Now Final Jeopardy!//////////////////
				System.out.println("Every question has been answered! Now it's Final Jeopardy!");
				
				int[] wager = new int[howManyTeam];//store everyone's wager
				String[] fjAns = new String[howManyTeam];//store everyone's final jeopardy answer
				
				
				for(int i = 0; i < howManyTeam; i++)
				{
					//only those with point more than 0 can make bet 
					if(gp.getScore(i) > 0)
					{
						System.out.println("Team " + gp.getName(i) + ", please enter your bet.");

						int bet;//buffer

						while(true)
						{
							
							String line1 = s.next();
							
							///////////////exit///////////////////
							if(line1.toLowerCase().equals("exit"))
							{
								gameExit();
							}
							//////////////////////////////////////
							//////////////////replay///////////////
							if(line1.toLowerCase().equals("replay"))
							{
								break outerloop;
							}
							///////////////////////////////////////

							
							try
							{
								bet = Integer.parseInt(line1);
							}
							catch(NumberFormatException nfe)
							{
								System.out.println("Invalid entry! Should enter number.");
								continue;
							}
							
							System.out.println("Your bet is " + bet);
							
							
							while(true)
							{
								if(bet > gp.getScore(i) || bet < 0)
								{
									System.out.println("Your bet cannot be more than your actual money or negative. Please try again.");
									String line2 = s.next();					
									///////////////exit///////////////////
									if(line2.toLowerCase().equals("exit"))
									{
										gameExit();
									}
									//////////////////////////////////////
									//////////////////replay///////////////
									if(line2.toLowerCase().equals("replay"))
									{
										break outerloop;
									}
									///////////////////////////////////////
									try
									{
										bet = Integer.parseInt(line2);
									}
									catch(NumberFormatException nfe)
									{
										System.out.println("Invalid entry! Should enter number.");
										continue;
									}
								}
								else
								{
									break;
								}
							}
							wager[i] = bet;
							break;
						}
					}
					else 
					{
						continue;
					}
				}
				//check if every team has a negative point
				int allNegative = -10000;
				
				for(int i = 0; i < howManyTeam; i++)
				{
					if(gp.getScore(i) > allNegative)
					{
						allNegative = i;
						allNegative = gp.getScore(i);
					}
				}
				
				//if someone has more than 0, show final jeopardy, if not, no winner 
				if(allNegative > 0)
				{
					System.out.println("The question is: ");
					System.out.println(st.showQuestion("FJ"));
				}
				else
				{
					System.out.println("Every team has a negative point! Not gonna show the final jeopardy.");
				}

				Scanner sn = new Scanner(System.in);
				for(int i = 0; i < howManyTeam; i++)
				{
					//only those with points more than 0 can answer final jeopardy
					if(gp.getScore(i) > 0)
					{
						System.out.println("Team " + gp.getName(i) + ". Please enter your answer: ");
						String f = sn.nextLine();
						///////////////exit///////////////////
						if(f.toLowerCase().equals("exit"))
						{
							gameExit();
						}
						//////////////////////////////////////
						//////////////////replay///////////////
						if(f.toLowerCase().equals("replay"))
						{
							break outerloop;
						}
						///////////////////////////////////////
						
						String [] anotherBuff = f.split(" ");
						
						//check if the user writes as a question
						if( ((anotherBuff[0].toLowerCase().equals("what"))==false 
								&& (anotherBuff[0].toLowerCase().equals("who"))==false 
								&& (anotherBuff[0].toLowerCase().equals("where"))==false              
								&& (anotherBuff[0].toLowerCase().equals("when"))==false) 
								|| ((anotherBuff[1].toLowerCase().equals("is"))==false 
								&& (anotherBuff[1].toLowerCase().equals("are"))==false) )
						{
							System.out.println("Wrong answer format! Please answer it as a question.");
							f = sn.nextLine();
							///////////////exit///////////////////
							if(f.toLowerCase().equals("exit"))
							{
								gameExit();
							}
							//////////////////////////////////////
							//////////////////replay///////////////
							if(f.toLowerCase().equals("replay"))
							{
								break outerloop;
							}
							///////////////////////////////////////
							
						}
						
						fjAns[i] = f;
					}
					else 
					{
						continue;
					}

				}
				
				for(int i = 0; i < howManyTeam; i++)
				{
					if(gp.getScore(i) > 0)
					{
						if(st.compareAnswer("FJ", fjAns[i]) == true)
						{
							gp.setScore(gp.getName(i), wager[i]);
						}
						else
						{
							int point = (-1)*wager[i];
							gp.setScore(gp.getName(i), point);
						}
					}
					else
					{
						continue;
					}
				}
				
				int winNum = -10000;
				int winScore = -10000;
				//find the winning team and its score
				
				
				for(int i = 0; i < howManyTeam; i++)
				{
					if(gp.getScore(i) > winScore)
					{
						winNum = i;
						winScore = gp.getScore(i);
					}
				}
				
				System.out.println("---------Summary-----------");//print the summary of the game
				for(int i = 0; i < howManyTeam; i++)
				{
					System.out.println("Team " + gp.getName(i) + ": " + gp.getScore(i));
				}
				System.out.println("---------------------------");
				
				if(winScore <= 0)
				{
					System.out.println("Every one has a negative point! There is no winner.");
				}
				else
				{
					System.out.println("The winner is: " + gp.getName(winNum) + ". Their score is " + winScore);
					for(int i = 0; i < howManyTeam; i++)
					{
						if(gp.getScore(i) == winScore && ((gp.getName(winNum)).equals(gp.getName(i))== false) )
						{
							System.out.println("There are multiple winners! They are " + gp.getName(i) +", with score of " + gp.getScore(i));
						}
					}
				}
				
				sn.close();
				
				isFinished = true;//a complete round is finished
				break outerloop;
			}
		}		
		
		s.close();
	}
	
}