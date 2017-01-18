package client;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import libraries.ImageLibrary;

public class FactoryRobotBin extends FactoryObject{

	private Queue<FactoryRobot> robots;
	private Semaphore robotPermit;
	
	protected FactoryRobotBin(Rectangle inDimensions) {
		super(inDimensions);
		robots = new LinkedList<FactoryRobot>();
		robotPermit = new Semaphore(0);
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "robotbin" + Constants.png);
		
	}
	
	public synchronized void addRobot(FactoryRobot fr)
	{
		robots.add(fr);
		robotPermit.release();
	}
	
	public synchronized FactoryRobot getRobot() throws InterruptedException
	{
		if(robotPermit.tryAcquire())
		{
			return robots.remove();
		} else {
			return null;
		}
	}
}
