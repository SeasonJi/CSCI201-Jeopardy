package messages;

//used to set the fish icon
public class FishMessage implements Message {
	
	private int time;
	
	public FishMessage(int time)
	{
		this.time = time;
	}
	
	public int getBuzzInTime()
	{
		return time;
	}


}
