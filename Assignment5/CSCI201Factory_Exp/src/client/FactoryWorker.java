package client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libraries.ImageLibrary;
import resource.Product;
import resource.Resource;

public class FactoryWorker extends FactoryObject implements Runnable, FactoryReporter{
	
	private int mNumber;
	
	protected FactorySimulation mFactorySimulation;
	private Product mProductToMake;
	
	protected Lock mLock;
	protected Condition atLocation;
	
	private Timestamp finished;
	
	//Nodes each worker keeps track of for path finding
	protected FactoryNode mCurrentNode;
	protected FactoryNode mNextNode;
	protected FactoryNode mDestinationNode;
	protected Stack<FactoryNode> mShortestPath;
	
	//Lab 13
	protected Thread mThread;
	
	//instance constructor
	{
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "worker" + Constants.png);
		mLock = new ReentrantLock();
		atLocation = mLock.newCondition();
	}
	
	FactoryWorker(int inNumber, FactoryNode startNode, FactorySimulation inFactorySimulation) {
		super(new Rectangle(startNode.getX(), startNode.getY(), 1, 1));
		mNumber = inNumber;
		mCurrentNode = startNode;
		mFactorySimulation = inFactorySimulation;
		mLabel = Constants.workerString + String.valueOf(mNumber);
		mThread = new Thread(this);
	}
	
	@Override
	public void draw(Graphics g, Point mouseLocation) {
		super.draw(g, mouseLocation);
	}
	
	@Override
	public void update(double deltaTime) {
		if(!mLock.tryLock()) return;
		//if we have somewhere to go, go there
		if(mDestinationNode != null) {
			if(moveTowards(mNextNode,deltaTime * Constants.workerSpeed)) {
				//if we arrived, save our current node
				mCurrentNode = mNextNode;
				if(!mShortestPath.isEmpty()) {
					//if we have somewhere else to go, save that location
					mNextNode = mShortestPath.pop();
					mCurrentNode.unmark(); //Unmark it
				}//if we arrived at the location, signal the worker thread so they can do more actions
				if(mCurrentNode == mDestinationNode){
					mDestinationNode.unmark(); //Unmark the last node
					atLocation.signal();
				}
			}
		}
		mLock.unlock();
	}
	
	//Use a separate thread for expensive operations
	//Path finding
	//Making objects
	//Waiting
	@Override
	public void run() {
		mLock.lock();
		try {
			//Thread.sleep(1000*mNumber); //used to space out the factory workers
			while(true) {
				if(mProductToMake == null) {
					//get an assignment from the table
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					while(!mDestinationNode.aquireNode())Thread.sleep(1);
					mProductToMake = mFactorySimulation.getTaskBoard().getTask();
					Thread.sleep(1000);
					mDestinationNode.releaseNode();
					if(mProductToMake == null) break; //No more tasks, end here
				}
				//find way to the resource room door
				{
					mDestinationNode = mFactorySimulation.getNode("Workroom 2");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					FactoryWorkroomDoor2 fwd2 = (FactoryWorkroomDoor2)mDestinationNode.getObject();
					fwd2.getWorkbench();
				
					//AVENGERS I mean... ROBOTS ASSEMBLE
					ArrayList<FactoryRobot> robots = new ArrayList<FactoryRobot>();
					ArrayList<Thread> robotThreads = new ArrayList<Thread>();
					FactoryRobotBin robotBin;
					
					//Go to the robot bin
					{
						mDestinationNode = mFactorySimulation.getNode("RobotBin");
						mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
						mNextNode = mShortestPath.pop();
						atLocation.await();
						robotBin = (FactoryRobotBin)(mDestinationNode.getObject());
					}
					//Gather the resources
					for(Resource resource : mProductToMake.getResourcesNeeded()) {
						FactoryRobot robot = robotBin.getRobot();
						if(robot != null)
						{
							robots.add(robot);
							robot.getResource(resource, mFactorySimulation.getNode("Workroom 2"));
							// ^ Note, replace "Workroom 2" with the name of the node that contains your resource room door
							robotThreads.add(robot.getThread());
						} else {
							//Work at the same time as your robots
							mDestinationNode = mFactorySimulation.getNode(resource.getName());
							mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
							mNextNode = mShortestPath.pop();
							atLocation.await();
							if(!mProductToMake.getResourcesNeeded().lastElement().equals(resource)){
								mDestinationNode = mFactorySimulation.getNode("RobotBin");
								mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
								mNextNode = mShortestPath.pop();
								atLocation.await();
							}
						}
					}
					//exit after having gotten the resources
						mDestinationNode = mFactorySimulation.getNode("Workroom 2");
						//^ Note, replace "Workroom 2" with the name of the node that contains the resource room dooor
						mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
						mNextNode = mShortestPath.pop();
						atLocation.await();						
						for(FactoryRobot fr : robots) {
							fr.sendBack();
						}
						for(Thread t : robotThreads) {
							t.join();
						}
						fwd2.returnWorkbench();
				}
				//work at the workbench
				{
					//navigate to the workroom
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					
					//get to the workbench
					FactoryWorkroomDoor fwd = (FactoryWorkroomDoor)mDestinationNode.getObject();
					
					//aquire the semaphore
					FactoryWorkbench fwb = fwd.getWorkbench();
					mDestinationNode = mFactorySimulation.getNodes()[fwb.getX()][fwb.getY()];
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					
					//assemble a product
					fwb.assemble(mProductToMake);
					
					//go back to the door to exit
					mDestinationNode = mFactorySimulation.getNode("Workroom");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					
					//return permit of the workbench
					fwd.returnWorkbench(fwb);
					
					
					//take the following code out from the previous lab since the workers are receiving the workbench in the new code we have put above
					/*FactoryNode workbenchNode = mFactorySimulation.getNode("Workbench");
					mShortestPath = mCurrentNode.findShortestPath(workbenchNode);
					mShortestPath.remove(workbenchNode);
					mDestinationNode = mShortestPath.firstElement();
					
					atLocation.await();
					FactoryWorkbench workbench = (FactoryWorkbench)workbenchNode.getObject();
					workbench.lock();
					mDestinationNode = workbenchNode;
					mNextNode = workbenchNode;
					atLocation.await();*/
					//Thread.sleep(1000);
					//workbench.assemble(mProductToMake);
					//workbench.unlock();
				}
				//Wash at the washroom
				{
					FactoryNode WashroomNode = mFactorySimulation.getNode("Washroom");
					mShortestPath = mCurrentNode.findShortestPath(WashroomNode);
					mShortestPath.remove(WashroomNode);
					mDestinationNode = mShortestPath.firstElement();
					//maybe add 5 to 6 workbenches one after the other in order to make them wait
					//This is a hacky solution but it might work :) I love U
					atLocation.await();
					FactoryWashroom washroom = (FactoryWashroom)WashroomNode.getObject();
					washroom.lock();
					mDestinationNode = WashroomNode;
					mNextNode = WashroomNode;
					atLocation.await();
					//Thread.sleep(1000);
					washroom.wash(mProductToMake);
					washroom.unlock();
				}
				
				//update table
				{
					mDestinationNode = mFactorySimulation.getNode("Task Board");
					mShortestPath = mCurrentNode.findShortestPath(mDestinationNode);
					mNextNode = mShortestPath.pop();
					atLocation.await();
					finished = new Timestamp(System.currentTimeMillis());
					mFactorySimulation.getTaskBoard().endTask(mProductToMake);
					mProductToMake = null;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mLock.unlock();
	}

	@Override
	public void report(FileWriter fw) throws IOException {
		// TODO Auto-generated method stub
		fw.write(mNumber + " finished at " + finished + "\n");
	}
	
	public Thread getThread()
	{
		return mThread;
	}

}
