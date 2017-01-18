package client;

import java.awt.Rectangle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libraries.ImageLibrary;
import resource.Product;

public class FactoryWashroom extends FactoryObject{

	private Lock mLock;
	
	protected FactoryWashroom(Rectangle inDimensions) {
		super(inDimensions);
		mLabel = "Washroom";
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "washroom" + Constants.png);
		mLock = new ReentrantLock();
	}

	public void lock(){
		mLock.lock();
	}
	
	public void unlock(){
		mLock.unlock();
	}
	
	public void wash(Product mProductToMake) throws InterruptedException{
		for(int i = 0; i < mProductToMake.getResourcesNeeded().size(); i++)
		{
			Thread.sleep(100);
		}
	}
	
}
