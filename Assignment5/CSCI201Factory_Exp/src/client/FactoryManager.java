package client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import resource.Factory;
import resource.Resource;

public class FactoryManager implements Runnable , ChangeListener{

	//This lock ensures that only one animation thread will go at a time
	private Lock animationLock;
	
	//The simulation that is currently running, and where it will be displayed
	private FactorySimulation mFactorySimulation;
	private FactoryPanel mRenderPanel;
	
	//This thread will update and draw the factorySimulation member variable
	private Thread animator;
	private volatile boolean running;
	private long period = 10; //magic number
	
	//Speed of the simulation, 1x is normal time
	private double speed = Constants.simulation_1x;
	
	//Member variable saving of the Factory and the Table
	private Factory mFactory;
	private JTable mTable;
	
	//Lab 10 Expansion Code
	FactoryClientListener fcl;
	
	//instance constructor
	{
		animationLock = new ReentrantLock();
		running = false;
	}
	
	public void loadFactory(Factory inFactory, JTable inTable) {
		//must stop the current animation, load in the factory, and start the new factorySimulation
		mFactory = inFactory;
		mTable = inTable;
		
		stop();
		mFactorySimulation = new FactorySimulation(inFactory, inTable);
		mFactorySimulation.setFactoryClientListener(fcl); //Lab 10 Expansion: set the FCL of the FS
		mRenderPanel.refresh();
		start();
	}
	
	public void setFactoryRenderer(FactoryPanel inRenderPanel) {
		mRenderPanel = inRenderPanel;
	}
	
	//starts the animation of the factorySimulation member variable
	public void start() {
		startAnimation();
	}
	
	private void startAnimation() {
		if(animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}
	
	//halts the animation of the factorySimulation
	public void stop() {
		running = false;
		animator = null;
	}

	@Override
	public void run() {
		animationLock.lock();
		long beforeTime = 0, deltaTime = 0, sleepTime = 0;
		beforeTime = System.nanoTime();
		running = true;
		while(running) {
			//if we have a simulation to run, run the simulation
			if(mFactorySimulation != null) {
				mFactorySimulation.update(((double)deltaTime/(double)1000000000)*speed);//seconds
			}
			//if we have somewhere to render the simulation, render it there
			if(mRenderPanel != null) {
				mRenderPanel.render(mFactorySimulation); //create the graphic
				mRenderPanel.paint(); //paint the graphic onto the screen
			}
			deltaTime = System.nanoTime() - beforeTime;
			sleepTime = period - deltaTime;
			
			if(sleepTime <= 0L) {
				sleepTime = 5L;
			}
			
			try{//sleep a bit so we don't hog the CPU
				Thread.sleep(sleepTime);
			} catch(InterruptedException ex){}
			
			beforeTime = System.nanoTime();
		}
		animationLock.unlock();
	}

	@Override
	public void stateChanged(ChangeEvent ce) {
		//Listen for JSlider changing, this will change the speed of the simulation
		JSlider source = (JSlider) ce.getSource();
		speed = source.getValue();
	}
	
	public void resetFactory() //Lab 5: reset the factory
	{
		loadFactory(mFactory,mTable);
	}
	public void deliver(Resource resource) {
		if(mFactorySimulation != null) {
			if(mFactorySimulation.getMailBox() != null) {
				mFactorySimulation.getMailBox().insert(resource);
			}
		}
	}
	
	//Lab 8 Expansion code
	public Factory getFactory() //We will need to get the factory's current resources and decide to request one of them
	{
		return mFactory;
	}

	
	//Lab 10 expansion code
	public void setFCL(FactoryClientListener fcl) {
		// TODO Auto-generated method stub
		this.fcl = fcl;
	}
}
