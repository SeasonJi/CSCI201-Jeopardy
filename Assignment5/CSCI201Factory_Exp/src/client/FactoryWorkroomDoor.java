package client;

import java.awt.Rectangle;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import libraries.ImageLibrary;

public class FactoryWorkroomDoor extends FactoryObject{

	private Semaphore workpermit;
	private Vector<FactoryWorkbench> availableworkbenches;
	protected FactoryWorkroomDoor(Rectangle inDimensions, Vector<FactoryWorkbench> workbenches) {
		super(inDimensions);
		mLabel = "Workroom Door";
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "Motherboard" + Constants.png);
		availableworkbenches = workbenches;
		workpermit = new Semaphore(availableworkbenches.size());
	}
	public FactoryWorkbench getWorkbench() throws InterruptedException{
		workpermit.acquire();
		return availableworkbenches.remove(0);
		
	}
	public void returnWorkbench(FactoryWorkbench fw){
		availableworkbenches.add(fw);
		workpermit.release();
	}

}
