package game_logic;

import java.io.Serializable;
import java.util.List;

public class GameFile implements Serializable {
	//total number of people that have rated this file
	private int numRatings;
	//total rating
	private int totalRating;
	//lines of the file
	private List<String> fileLines;
	private String filePath;

	public GameFile(List<String> fileLines, int numRatings, int totalRating, String filePath) {
		this.numRatings = numRatings;
		this.totalRating = totalRating;
		this.fileLines = fileLines;
		this.filePath = filePath;
	}
	
	public int getNumberOfRatings(){
		return numRatings;
	}
	
	public int getTotalRating(){
		return totalRating;
	}
	//get avergae rating
	public int getAverageRating(){
		return (int) Math.round((double) totalRating / (double) numRatings);
	}
	
	public List<String> getFileLines(){
		return fileLines;
	}

	public String getFilePath(){
		return filePath;
	}
}
