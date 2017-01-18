package client;

import libraries.ImageLibrary;
import resource.Resource;

public class FactoryStockPerson extends FactoryWorker{
	
	private Resource mProductToStock;

	FactoryStockPerson(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(inNumber, startNode, inFactorySimulation);
		mLabel = "Stockperson " + inNumber;
		
	}

	@Override
	public void run(){
		mLock.lock();
		try{
			while(true){
				if(mProductToStock == null) {
					mDestinationNode = mFactorySimulation.getNode("Mailbox");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					while(!mDestinationNode.aquireNode())
					{
						Thread.sleep(1);
					}
					mProductToStock = mFactorySimulation.getMailBox().getStock();
					//Lab 7 Expansion additions
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
					if(mProductToStock == null) //We didn't get any resources
					{
						//don't update the image
						//don't do anything. Yes it's busy waiting, but more or less this is wasteful but not harmful 
					} else {
						//got an item, lets get the box image
						mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockperson_box" + Constants.png);
					}
				} else {
					mDestinationNode = mFactorySimulation.getNode(mProductToStock.getName());
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryResource toGive = (FactoryResource)mDestinationNode.getObject();
					toGive.giveResource(mProductToStock.getQuantity());
					mImage = ImageLibrary.getImage(Constants.resourceFolder + "stockperson_empty" + Constants.png);
					mProductToStock = null;
				}
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}
}
