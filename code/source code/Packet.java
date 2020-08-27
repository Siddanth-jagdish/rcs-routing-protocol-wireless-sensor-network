import java.util.ArrayList;


public class Packet {
	public String From;
	public String To;
	public String Source;
	public String Destination;
	public String Time;
	public String Type;
	public String message;
	public int SequenceNo;
	
	
	public static Packet ForwardPacket(Packet p, String currentNode, String nexNode) {
		Packet np = new Packet();
		np.From = currentNode;
		np.To = nexNode;
		np.Source = p.Source;
		np.Destination = p.Destination;
		np.message = p.message;
		np.Time = ""+System.currentTimeMillis();
		np.Type = "F";
		return p;	
	}
	
	public static Packet ReceivePacket(Packet p, String currentNode) {
		Packet np = new Packet();
		np.From = p.From;
		np.To = currentNode;
		np.Source = p.Source;
		np.Destination = p.Destination;
		np.message = p.message;
		np.Time = ""+System.currentTimeMillis();
		np.Type = "R";
		return p;	
	}
	
	public String toString()
	{
		String str = Type + "-" + From + "-" + To + "-" + Source + "-" + Destination + "-" + SequenceNo + "-" + message + "-" + Time;
		return str;
	}
	
	public static ArrayList<Packet> SendPackets(String message, String source, String Destination) {
		ArrayList<Packet> packets = new ArrayList<Packet>();
		for(int i=0;i<message.length();i++){
			Packet p = new Packet();
			p.From = source;
			p.Destination = Destination;
			p.To = Destination;
			p.Time = ""+System.currentTimeMillis();
			p.Type = "S";
			p.message = ""+message.charAt(i);
			p.SequenceNo = i;
			packets.add(p); 
		}
		return packets;
	}
}
