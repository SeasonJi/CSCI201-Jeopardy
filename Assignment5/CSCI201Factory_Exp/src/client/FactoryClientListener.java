package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import resource.Factory;
import resource.Resource;
import utilities.Util;

public class FactoryClientListener extends Thread {

	private Socket mSocket;
	private ObjectInputStream ois;
	private PrintWriter pw;
	private FactoryManager mFManager;
	private FactoryClientGUI mFClientGUI;
	//Lab 8 Expannsion code member vars
	private Factory mFactory;  //get a pointer to the factory
	private Vector<Resource> tempResources; //pointer to the resources
	private String previous = "";
	
	public FactoryClientListener(FactoryManager inFManager, FactoryClientGUI inFClientGUI, Socket inSocket) {
		mSocket = inSocket;
		mFManager = inFManager;
		mFClientGUI = inFClientGUI;
		//mFactory = mFManager.getFactory(); //get the factory from the manager
		boolean socketReady = initializeVariables();
		if (socketReady) {
			start();
		}
	}

	private boolean initializeVariables() {
		try {
			ois = new ObjectInputStream(mSocket.getInputStream());
			pw = new PrintWriter(mSocket.getOutputStream());
		} catch (IOException ioe) {
			Util.printExceptionToCommand(ioe);
			Util.printMessageToCommand(Constants.unableToGetStreams);
			return false;
		}
		return true;
	}
	
	public void sendMessage(String msg) { //LOOK HERE
		pw.println(msg);
		pw.flush();
	}
	
	public void run() {
		try {
			mFClientGUI.addMessage(Constants.waitingForFactoryConfigMessage);
			Factory factory;
			while(true) {
				Object obj = ois.readObject();
				if(obj instanceof Factory) {
					factory = (Factory) obj;
					mFManager.loadFactory(factory, mFClientGUI.getTable());
					mFClientGUI.addMessage(Constants.factoryReceived);
					mFClientGUI.addMessage(factory.toString());
				} else if(obj instanceof Resource) {
					Resource toDeliver = (Resource) obj;
					mFManager.deliver(toDeliver);
					mFClientGUI.addMessage(Constants.resourceReceived);
					mFClientGUI.addMessage(toDeliver.toString());
				}
				
				//Lab 8 Expansion Code
				mFactory = mFManager.getFactory(); //get the factory from the manager
				if(mFactory != null) //Don't toggle anything until the factory is loaded
				{
					tempResources = mFactory.getResources(); //get a pointer to the factory's resources
					if(tempResources.size() == 0){ //just a checker to make sure the tempResource pointer isn't empty
						System.out.println("I am empty");
					}
						Resource lowest = tempResources.get(0); //get the first element
						for(Resource r : tempResources) //For all the resources
						{
							if(lowest.getQuantity() > r.getQuantity() ) //If the current lowest resource is lower than the next one
							{
								lowest = r; //the next resource is the lowest 
							}
						}
					//Once we have determine the resource with the lowest amount, signal the Warehouse to toggle what resource to generate
					String toSend = "Toggle: " + lowest.getName(); // our encoding method is the keyword Toggle and then the resource name
					//System.out.println("I am sending this: "+ toSend);
						if(!previous.equals(lowest.getName())){ //We don't want to send out the same lowest item twice
							System.out.println("I am sending the message");
							sendMessage(toSend); //Send it to the server.	
							previous = lowest.getName();
						}
				}
			}
		} catch (IOException ioe) {
			mFClientGUI.addMessage(Constants.serverCommunicationFailed);
		} catch (ClassNotFoundException cnfe) {
			Util.printExceptionToCommand(cnfe);
		}
	}
}
