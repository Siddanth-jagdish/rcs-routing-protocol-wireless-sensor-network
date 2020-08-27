import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;


public class Calculation {
	
	static String routingFilename = "RoutingLog.txt";
	static String batteryInfoFile = "AverageBatteryInfomation.txt";

	public static void flushData() {
		{
    	BufferedWriter out = null;
    	try  
    	{
    	    FileWriter fstream = new FileWriter(routingFilename, false); //true tells to append data.
    	    out = new BufferedWriter(fstream);
    	    out.write("");
    	}
    	catch (IOException e)
    	{
    	    System.err.println("Error: " + e.getMessage());
    	}
    	finally
    	{
    	    if(out != null) {
    	        try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	}
		}

		{
	    	BufferedWriter out = null;
	    	try  
	    	{
	    	    FileWriter fstream = new FileWriter(batteryInfoFile, false); //true tells to append data.
	    	    out = new BufferedWriter(fstream);
	    	    out.write("");
	    	}
	    	catch (IOException e)
	    	{
	    	    System.err.println("Error: " + e.getMessage());
	    	}
	    	finally
	    	{
	    	    if(out != null) {
	    	        try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    	    }
	    	}
			}
    }
	
	public static void writeLine(String line) {
    	BufferedWriter out = null;
    	try  
    	{
    	    FileWriter fstream = new FileWriter(routingFilename, true); //true tells to append data.
    	    out = new BufferedWriter(fstream);
    	    out.write(line);
    	    out.newLine();
    	}
    	catch (IOException e)
    	{
    	    System.err.println("Error: " + e.getMessage());
    	}
    	finally
    	{
    	    if(out != null) {
    	        try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	}	
    }
	
	
	public static void CalculateAndWrite(Graph<String, String> proposedFlow, Graph<String, String> existingflow, Graph<String, String> aMimoFlow, String message, String mFrom, String mTo, Map<String, Integer> batteryLevel) {
		List<Packet> packets = Packet.SendPackets(message, mFrom, mTo);
		for (Packet p : packets) {
			for (String s : existingflow.getVertices()) {
				BufferedWriter out = null;
		    	try  
		    	{
		    	    FileWriter fstream = new FileWriter(routingFilename, true); //true tells to append data.
		    	    out = new BufferedWriter(fstream);
		    	    if(s.equals(mTo)) {
		    	    	p.Type = "R";
		    	    } else if(s.equals(mFrom)) {
		    	    	p.Type = "S";
		    	    } else {
		    	    	p.Type = "F";
		    	    }
		    	    out.write("At Node - " + s + " - " + p.toString());
		    	    out.newLine();
		    	}
		    	catch (IOException e)
		    	{
		    	    System.err.println("Error: " + e.getMessage());
		    	}
		    	finally
		    	{
		    	    if(out != null) {
		    	        try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    	    }
		    	}	
			}
		}
		
		double data1 = getProposedAverageEnergy(batteryLevel);
		
		double data2 = getExistingEnergy(batteryLevel);
	
		
		{
			BufferedWriter out = null;
			try {
				FileWriter fstream = new FileWriter(batteryInfoFile, true); // true
																			// tells
																			// to
																			// append
																			// data.
				out = new BufferedWriter(fstream);
				out.write(data1 + " " + data2);
				out.newLine();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}

	private static double getExistingEnergy(
			Map<String, Integer> batteryLevel) {
		
		double result = 0.0;
	    int minimum = 11;
	    for(String s:batteryLevel.keySet()){
	    	if(minimum>batteryLevel.get(s)){
	    		minimum = batteryLevel.get(s);
	    	}
	    }
		
	    result = minimum;
		return result;
	}

	private static double getProposedAverageEnergy(
			Map<String, Integer> batteryLevel) {
		
	    double result = 0.0;
	    double total = 0.0;
	    for(String s:batteryLevel.keySet()){
	    	total = total + batteryLevel.get(s);
	    }
	    result = total / batteryLevel.size();
		return result;
	}

	
	
}
