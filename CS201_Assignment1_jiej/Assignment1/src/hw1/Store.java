package hw1;

import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class Store 
{
	public int questionCounter;
	private HashMap<String, String[]> hash; //I use a hashmap to store categories, points, and corresponding questions and answers.
											//the key is the combination of category and point, for example, "baseball200",
											//and the value is a string array, with string[0] of the question, string[1] of the answer
											//string[2] of whether the question is answered or not(I store it as "true" and "false")
	private String [] category;
	private String [] point;
	private boolean FJexist;//is there any multiple Final Jeopardy?
	
	
	public Store()
	{
		questionCounter = 0;
		FJexist = false;
		hash = new HashMap<String, String[]>();
	}
	
	public void addCategory(String in)
	{
		category = in.split("::");
		if( category.length != 5)
		{
			System.out.println("Wrong number of categories! Need 5 categories. Terminate now.");
			return;
		}
		for(int i = 0; i < 5; i++)
		{
			category[i] = category[i].toLowerCase();
		}
		//check duplicate categories
		for(int i = 0; i < 5; i++)
		{
			for(int j = i+1; j < 5; j++)
			{
				if(category[i] == category[j])
				{
					System.out.println("There are duplicate categories. Terminate now.");
				}
			}
		}
		
	}
	
	public void addPointValue(String in)
	{
		point = in.split("::");
		if( point.length != 5)
		{
			System.out.println("Wrong number of point values! Need 5 point values. Terminate now.");
			return;
		}
		//check duplicate categories
		for(int i = 0; i < 5; i++)
		{
			for(int j = i+1; j < 5; j++)
			{
				if(point[i] == point[j])
				{
					System.out.println("There are duplicate point values. Terminate now.");
					break;
				}
			}
		}
		
			
	}
	
	public void addQuestion(String in)
	{
		String[] buff = in.split("::");
		String[] pair = new String[3];
		//if a valid question
		if(buff.length == 5)
		{
			buff[1] = buff[1].toLowerCase();
			buff[2] = buff[2].toLowerCase();
			if(check(buff[1], buff[2]) == true)//buff[0] is blank, starts from buff[1]
			{
				pair[0] = buff[3];//question
				pair[1] = buff[4];//answer
				pair[2] = "false"; //have this question been answered?
				hash.put((buff[1])+(buff[2]), pair);
			}
			else
			{
				System.out.println("Error with category and point value.");
			}
		}
		else if(buff[1].equals("FJ"))//Here is final jeopardy
		{
			if(FJexist == true)
			{
				System.out.println("FJ already exists! Terminate now.");
				return;
			}
			pair[0] = buff[2];
			pair[1] = buff[3];
			hash.put(buff[1], pair);
			FJexist = true;
		}
		else
		{
			System.out.println("Wrong question format. Terminate now.");
			return;
		}
		questionCounter++;
	}
	//check if this question actually exists
	public boolean check(String catIn, String pointIn)
	{
		boolean catCheck = false;
		boolean pointCheck = false;
		for(int i = 0; i < 5; i++)
		{
			if(this.category[i].equals(catIn))
			{
				catCheck = true;
			}
			if(this.point[i].equals(pointIn))
			{
				pointCheck = true;
			} 
		}
		boolean result = catCheck&&pointCheck;
		
		return result;
	}
	//check if this category exists
	public boolean hasCategory(String in)
	{
		for(int i = 0; i < 5; i++)
		{
			if(category[i].equals(in))
			{
				return true;
			}
		}
		return false;
	}
	//check if this point value exists
	public boolean hasPoint(String in)
	{
		for(int i = 0; i < 5; i++)
		{
			if(point[i].equals(in))
			{
				return true;
			}
		}
		return false;
	}	
	//display the question
	public String showQuestion(String in)
	{
		String [] two = hash.get(in);
		return two[0];
	}
	//compare the entered line with answer
	public boolean compareAnswer(String in, String ans)
	{
		String [] two = hash.get(in);
		String ss = ans.toLowerCase();
		
		String sss = two[1].toLowerCase();
		
		//all the possible answers
		String ra1 = "who is " + sss;
		String ra2 = "who are " + sss;
		String ra3 = "what is " + sss;
		String ra4 = "what are " + sss;
		String ra5 = "where is " + sss;
		String ra6 = "where are " + sss;
		String ra7 = "when is " + sss;
		String ra8 = "when are " + sss;
		
		
		if(ss.equals(ra1) || ss.equals(ra2) || ss.equals(ra3) || ss.equals(ra4) ||ss.equals(ra5)
				|| ss.equals(ra6) || ss.equals(ra7) || ss.equals(ra8))
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	//make the question cannot be chosen again
	public void setAsked(String q)
	{
		String [] set = hash.get(q);
		set[2] = "true";
	}
	//see if the question has been chosen before
	public boolean isAsked(String q)
	{
		String [] set = hash.get(q);
		if(set[2].equals("true"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	//reset all the questions as not answered
	public void stReset()
	{
		for(String key : hash.keySet())
		{
			hash.get(key)[2] = "false";
		}
	}
}
