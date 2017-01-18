package server;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import resource.Factory;
import resource.Resource;

public class FactoryWarehouse implements Runnable{
	/*private Vector<ServerClientCommunicator> communicators;
	private volatile Vector<Resource> resources;
	Random rand;
	
	//Lab 8 expansion code member vars
	String activeResource = "Motherboard";//The current resource being made. Defaults to Motherboard so we don't get NPEs
	
	public FactoryWarehouse(Vector<ServerClientCommunicator> sccVector)
	{
		communicators = sccVector;
		rand = new Random();
	}
	
	public void setFactory(Factory factory)
	{
		resources = factory.getResources();
		
	}
	
	//Lab 8 expansion code: toggleable resources
	public void toggleProduction(String toggled)
	{
		activeResource = toggled;
	}
	
	@Override
	public void run() {
		while(resources == null){
			//don't do anything
			try{
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(true)
		{
			try{
				Thread.sleep(7500);
				//Thread.sleep(1000); //Just to check if multiple stockpersons work
				int toStock = Math.abs(rand.nextInt() % resources.size());
				int number = Math.abs(rand.nextInt()%25+1);
				for(ServerClientCommunicator communicator : communicators){
					//communicator.sendResource(new Resource(resources.elementAt(toStock).getName(), number));
					communicator.sendResource(new Resource(activeResource, number)); //Changed so now it makes the active resource ONLY
				}
				
			} catch(InterruptedException e) {
				
			}
		}
	}*/
	private Vector<ServerClientCommunicator> communicators;
	private volatile Vector<Resource> resources;
	private Resource neededResource = null;
        private Resource TempResource = null;
	private Lock mlock = new ReentrantLock();
	Random rand = new Random();
	
	FactoryWarehouse(Vector<ServerClientCommunicator> sccVector) {
		communicators = sccVector;
	}
	
	public void setFactory(Factory factory) {
		resources = factory.getResources();
	}
	
	public void run() {
			while(true) { //always
				try {
					Thread.sleep(7500);  
					//int toStock = Math.abs(rand.nextInt() % resources.size());
					int number = Math.abs(rand.nextInt() % 25 + 1);
					for(ServerClientCommunicator communicator : communicators) {
						
						if(communicator.getResourceName() != null) {
							//mlock.lock();
							synchronized(resources){
								
								for(Resource r : resources) {
									if(r.getName().equals(communicator.getResourceName())) {
                                    //System.out.println(r.getQuantity());
                                    	TempResource = neededResource;
										neededResource = r;
										System.out.println("find");
                                    	if(TempResource != neededResource){
                                    		break;
                                    	}
									}
								}
							
								if(neededResource != null){ 
									if(TempResource == null || !TempResource.getName().equals( neededResource.getName())){
										if(TempResource == null){
											System.out.println("I am null");
										}
										System.out.println("Sending the resource");
										communicator.sendResource(new Resource(neededResource.getName(),number));
										neededResource = null;
										//TempResource = neededResource;
										//mlock.unlock();
									}
							
								}	
							}
						}
						
					}
				}catch(InterruptedException e) {
						e.printStackTrace();
				}
			}
	}

}
