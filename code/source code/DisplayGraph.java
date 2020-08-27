
import java.io.*; 

import org.jfree.chart.ChartFactory; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.chart.ChartUtilities; 

public class DisplayGraph {
	   
	   public static void energyPut()throws Exception 
	   {
		   final String fait = "Proposed System";
		    final String fait1 = "Existing System";
		 
	    
	      final DefaultCategoryDataset dataset = new DefaultCategoryDataset( ); 

	      File file = new File("AverageBatteryInfomation.txt");
	      int count =1;
	      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	    	    String line;
	    	    while ((line = br.readLine()) != null) {
	    	       String[] data = line.split(" ");
	    	       dataset.addValue( Double.parseDouble(data[0].toString()) , fait , ""+count);
	    	       dataset.addValue( Double.parseDouble(data[1].toString()) , fait1 ,""+count);
	    	       count++;
	    	    }
	    	}              
	      JFreeChart barChart = ChartFactory.createLineChart3D(
	         "Average Battery Level",             
	         "Iterations",             
	         "Battery Level",             
	         dataset,            
	         PlotOrientation.VERTICAL,             
	         true, true, false);
	         
	      int width = 640; /* Width of the image */              
	      int height = 480; /* Height of the image */                              
	      File barChart3D = new File( "BatteryLevel.jpeg" );                            
	      ChartUtilities.saveChartAsJPEG( barChart3D, barChart, width, height);
	   }
	   
	   

	
}
