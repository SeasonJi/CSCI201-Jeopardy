package client;

import java.awt.Rectangle;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import libraries.ImageLibrary;

public class FactoryWorkroomDoor2 extends FactoryObject{
	private Semaphore workpermit;
	protected FactoryWorkroomDoor2(Rectangle inDimensions) {
		super(inDimensions);
		mLabel = "Workroom Door secondary";
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "Processor" + Constants.png);
		
		workpermit = new Semaphore(3);
	}
	public void getWorkbench() throws InterruptedException{
		workpermit.acquire();
		
	}
	public void returnWorkbench(){
		workpermit.release();
	}
}
