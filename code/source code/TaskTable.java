import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;

public class TaskTable extends JFrame {

	JFrame ref=null;
    private static final long serialVersionUID = 1L;
    private JTable table;
    WirelessSensorNetwork network;
    DefaultTableModel model;
    int count = 0;
    Object[][] data;
    
    public TaskTable(Object[][] data1, WirelessSensorNetwork n, int count) {
    	this.count = count;
    	
    	ref = this;
    	
    	count++;
    	
    	data = new Object[count-1][count];
    	
    	for(int j=0;j<count-1;j++) {
	    	data[j][0] = j;
    	}
    	
        Object[] columnNames = new Object[count];
        for(int i=0;i<count;i++) {
        	if(i==0) {
        		columnNames[0] = "";
        	} else {
        	 columnNames[i] = i-1;
        	}
        }
        
        network = n;
        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model) {

            private static final long serialVersionUID = 1L;

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
        };
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
		panel.add(scrollPane);
		
		JPanel jpPathType = new JPanel();
		jpPathType.add(getSubmitButton());

		panel.add(jpPathType);
		
		getContentPane().add(panel);
    }

    public Component getSubmitButton() {
		JButton addNode = new JButton("Submit");
		addNode.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
				
				Object[][] data1 = new Object[count][count+1];
		    	
		    	for(int j=0;j<count;j++) {
			    	data1[j][0] = j;
		    	}
		    	
				 for (int count = 0; count < model.getRowCount(); count++){
					 for(int j=1;j<model.getRowCount()+1;j++) {
						 try {
							 data1[count][j] = Boolean.parseBoolean(model.getValueAt(count, j).toString());
						 } catch(Exception ex) {
							 data1[count][j] = false;
						 }
			    	      
					 }
			    }
			    network.AddGraph(data1, count);
			}
		});
		return addNode;
	}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	
            	Object[][] data = {
                        {"Buy", false},
                        {"Sell", true},
                        {"Sell", true},
                        {"Buy", false}
                    };
            	
                TaskTable frame = new TaskTable(data,null,2);
                frame.setTitle("Manual Network Configuration");
                frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
                
                frame.pack();
                frame.setLocation(150, 150);
                frame.setVisible(true);
                JPanel buttonPanel = new JPanel();

                buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
                buttonPanel.add(frame.getSubmitButton());
                frame.add(buttonPanel);
            }
        });
    }
}