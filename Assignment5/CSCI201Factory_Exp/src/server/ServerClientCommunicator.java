package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import resource.Factory;
import resource.Resource;
import utilities.Util;

public class ServerClientCommunicator extends Thread {

	private Socket socket;
	private ObjectOutputStream oos;
	private BufferedReader br;
	private ServerListener serverListener;
	private String needResourceName = "";
	private String previousName = "";
	//Lab 10 expansion code
	private static final String[] productNames = {"Amazing Computer", "Amazing Server", "Cheap Computer", "Cheap Server", "Good Computer", "Okay Computer"};
	
	public ServerClientCommunicator(Socket socket, ServerListener serverListener) throws IOException {
		this.socket = socket;
		this.serverListener = serverListener;
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void sendFactory(Factory factory) {
		try {
			oos.writeObject(factory);
			oos.flush();
		} catch (IOException ioe) {
			Util.printExceptionToCommand(ioe);
		}
	}
	
	public void sendResource(Resource resource){
		/*try{
			new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}*/
		try {
			oos.writeObject(resource);
			oos.flush();
		} catch(IOException ioe) {
			Util.printExceptionToCommand(ioe);
		}
	}
	
	public void run() {
		try {
			String line = br.readLine();
			//Lab 8 expansion modifications
			//System.out.println("I am in the run methode");
			while (line != null) {
				if(line.contains("Toggle: ")) //If we are getting a request for toggling the currently produced resource
				{
					line = line.replace("Toggle: ", ""); //Strip the encoding word
					System.out.println("I have received this message: "+ line);
					//serverListener.getWarehouse().toggleProduction(line); //toggle the production
					previousName = needResourceName; //save the previous resource that came in
					needResourceName = line; //update the needed resource
					if(!previousName.equals(line)){
						FactoryServerGUI.addMessage(socket.getInetAddress() + ":" + socket.getPort() + " - " + line);
					}
					line = br.readLine(); 
				} else if (line.contains("SQLUpdate: ")){ //Found a SQL request 
					System.out.println("Update SQL Database"); //debugging
					line.replace("SQLUpdate: ", ""); //Remove tag
					char[] numArray = line.toCharArray();
					MySQLDriver msql = new MySQLDriver(); //Make a new MySQLDriver
					msql.connect(); //Connect to the server
					for(int i = 0; i < 6; i++)//for all the items
					{
							msql.updateWithValue(productNames[i], Character.getNumericValue(numArray[i])); //update them
					}
					msql.stop(); //disconnect from the DB
					line = br.readLine();
				} else { //Otherwise...
					//needResourceName = "";
					line = br.readLine(); //REMEMBER THIS PLACE
				}
			}
		} catch (IOException ioe) {
			serverListener.removeServerClientCommunicator(this);
			FactoryServerGUI.addMessage(socket.getInetAddress() + ":" + socket.getPort() + " - " + Constants.clientDisconnected);
			// this means that the socket is closed since no more lines are being received
			try {
				socket.close();
			} catch (IOException ioe1) {
				Util.printExceptionToCommand(ioe1);
			}
		}
	}
	public String getResourceName() {
		return needResourceName;
	}
}
