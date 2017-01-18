package game_logic;

import java.io.Serializable;

public class CurrentQuestion implements Serializable  {
	
	public static final long serialVersionUID = 1;
	private int x;
	private int y;
	
	public CurrentQuestion(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
}
