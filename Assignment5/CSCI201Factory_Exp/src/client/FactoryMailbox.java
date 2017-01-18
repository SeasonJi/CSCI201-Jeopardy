package client;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libraries.ImageLibrary;
import resource.Resource;

public class FactoryMailbox extends FactoryObject implements Runnable{

	private Vector<Resource> available;
	private Queue<Resource> mail;
	Random rand;
	
	//Lab 7 Expansion code
	Lock mLock;
	
	protected FactoryMailbox(Vector<Resource> deliveries) {
		super(new Rectangle(0,0,1,1));
		available = deliveries;
		mail = new LinkedList<>();
		mImage = ImageLibrary.getImage(Constants.resourceFolder + "mailbox" + Constants.png);
		mLabel = "Mailbox";
		rand = new Random();
		
		//Lab 7 expansion code
		mLock = new ReentrantLock();
		//new Thread(this).start();
	}
	
	public Resource getStock(){ 
		//int toStock = Math.abs(rand.nextInt() % available.size());
		//int number = Math.abs(rand.nextInt() % 25 + 1);
		//return new Resource(available.elementAt(toStock).getName(),number);
		while(mail.isEmpty()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mail.remove();
	}
	
	@Override
	public void run()
	{
		while(true){ //always generate
			try{
				int toStock = Math.abs(rand.nextInt() % available.size()); //Generate an index for a random resource
				int number = Math.abs(rand.nextInt() % 25 + 1); //Generate a random amount
				Resource temp = new Resource(available.elementAt(toStock).getName(),number); //create a new resource
				System.out.println("Generated a " + temp.getName() + " with amount " + temp.getQuantity() + " and stored it in mailbox, currently " + mail.size() + " items in the mailbox."); //Debugging
				mail.add(temp); //add it to the stock
				Thread.sleep(5000); //sleep for 5 seconds real time
			} catch (InterruptedException e) {
				System.out.println("Interrupted exception in FactoryMailbox!");
				e.printStackTrace();
			}
		}
	}
	public void insert(Resource resource) {
		for(Resource r : available) {
			if(r.getName().equals(resource.getName())) {
				mail.add(resource);
				break;
			}
		}
	}
}
