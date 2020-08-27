import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.ArrayList;

import javax.swing.JFrame;

public class RmiServer extends UnicastRemoteObject implements
		CommunicationInterface {

	/**
	 * @param args
	 */

	WirelessSensorNetwork network;

	public RmiServer() throws RemoteException {
		super(0); // required to avoid the 'rmic' step, see below

		network = new WirelessSensorNetwork();

		Calculation.flushData();
		//network.init();
		// network.start();
	}

	public WirelessSensorNetwork getNetwork() {
		return network;
	}

	@Override
	public int AddNewNode() {
		if (network != null) {
			network.addNode();
		}
		return network.getGraphCount()-1;
	}

	public static void main(String[] args) {

		
		System.out.println("RMI server started");

		try { // special exception handler for registry creation
			LocateRegistry.createRegistry(1099);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			// do nothing, error means registry already exists
			System.out.println("java RMI registry already exists.");
		}

	
		
		JFrame frame = new JFrame();
		frame.setTitle("A Novel Resource Constraint Secure Routing Protocol for Wireless Sensor Network");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		// Instantiate RmiServer

		// Bind this object instance to the name "RmiServer"
		try {
			RmiServer obj = new RmiServer();
			Naming.rebind("//localhost/RmiServer", obj);
			System.out.println("PeerServer bound in registry");

			WirelessSensorNetwork network = obj.getNetwork();

			
			
			//frame.addInScrollPane(network);
			
			frame.getContentPane().add(network);

			//network.init();
			// network.start();

			frame.pack();
			frame.setVisible(true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public ArrayList<String> getAllNodes(int nodeId) throws RemoteException {
		return network.getAllNodes();
	}

	@Override
	public void sendMessage(int nodeId, String message, int desnode)
			throws RemoteException {
		network.sendMessage(nodeId, message, desnode);
		
	}

	@Override
	public ArrayList<String> getRoutingLog(int nodeId) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
