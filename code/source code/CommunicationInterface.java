
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
 
public interface CommunicationInterface extends Remote {
	public int AddNewNode() throws RemoteException; 
    public ArrayList<String> getAllNodes(int nodeId) throws RemoteException;
    public void sendMessage(int nodeId, String message, int desnode) throws RemoteException;
    public ArrayList<String> getRoutingLog(int nodeId) throws RemoteException; 
}
