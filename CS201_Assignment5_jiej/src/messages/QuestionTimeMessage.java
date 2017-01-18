package messages;

//synchronize the remaining answer time on each team's questionGUIelement
public class QuestionTimeMessage implements Message{
	
	private int xIndex;
	private int yIndex;
	public int runningTime;
	public QuestionTimeMessage(int x, int y, int time)
	{
		this.xIndex = x;
		this.yIndex = y;
		this.runningTime = time;
	}
	
	public int getX()
	{
		return xIndex;
	}
	
	public int getY()
	{
		return yIndex;
	}
}
