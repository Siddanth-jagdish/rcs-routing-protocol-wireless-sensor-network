import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.shortestpath.BFSDistanceLabeler;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;

public class WirelessSensorNetwork extends JPanel {

	private static final long serialVersionUID = 7526217664458188502L;

	private String mFrom;

	private String mTo;
	private Graph<String, String> mGraph;
	private Set<String> mPred;
	
	private int initialbattery =10;

	Map<String, Integer> batteryLevel = new HashMap<String, Integer>();
	private BFSDistanceLabeler<String, String> bdl;

	private Graph<String, String> path;

	protected boolean ExistingSystem = true;

	private JLabel label;

	private ObservableGraph<String, String> g;

	final VisualizationViewer<String, String> vv;

	private String v_prev;

	private boolean done;

	final Layout<String, String> layout;

	WirelessSensorNetwork network ;
	
	public void AddGraph(Object[][] data, int n) {
		
		for(int i=0;i<n;i++) {
			g.addVertex(""+data[i][0]);
			fromCb.addItem(""+data[i][0]);
			toCb.addItem(""+data[i][0]);
			batterCb.addItem(""+data[i][0]);
			batteryLevel.put(""+data[i][0], initialbattery);
		}
		
		for(int i=0;i<n;i++) {
			for(int j=1;j<n+1;j++) {
				
				
				boolean value;
				try{
				value = (boolean) data[i][j];
				}catch(Exception e) {
					value = false;
				}
				
				if(value) {
				    g.addEdge(""+ data[i][0] + data[j-1][0], ""+ data[i][0], ""+ data[j-1][0]);
				} 
			}
		}
		
		
		layout.initialize();

		Relaxer relaxer = new VisRunner((IterativeContext) layout);
		relaxer.stop();
		relaxer.prerelax();
		StaticLayout<String, String> staticLayout = new StaticLayout<String, String>(
				g, layout);
		LayoutTransition<String, String> lt = new LayoutTransition<String, String>(
				vv, vv.getGraphLayout(), staticLayout);
		Animator animator = new Animator(lt);
		animator.start();
		vv.repaint();
	}
	
	public WirelessSensorNetwork() {
       network = this;
		Calculation.flushData();
		
		init();
		data = "Hello";

		this.mGraph = getGraph();
		setBackground(Color.WHITE);

		layout = new FRLayout<String, String>(mGraph);
		vv = new VisualizationViewer<String, String>(layout);
		vv.setBackground(Color.WHITE);

		vv.getRenderContext().setVertexDrawPaintTransformer(
				new MyVertexDrawPaintFunction<String>());
		vv.getRenderContext().setVertexFillPaintTransformer(
				new MyVertexFillPaintFunction<String>());
		vv.getRenderContext().setEdgeDrawPaintTransformer(
				new MyEdgePaintFunction());
		vv.getRenderContext().setEdgeStrokeTransformer(
				new MyEdgeStrokeFunction());
		vv.getRenderContext().setVertexLabelTransformer(
				new ToStringLabeller<String>());
		vv.setGraphMouse(new DefaultModalGraphMouse<String, String>());
		vv.addPostRenderPaintable(new VisualizationViewer.Paintable() {

			public boolean useTransform() {
				return true;
			}

			public void paint(Graphics g) {
				if (mPred == null)
					return;

				for (String e : layout.getGraph().getEdges()) {

					if (isBlessed(e)) {
						String v1 = mGraph.getEndpoints(e).getFirst();
						String v2 = mGraph.getEndpoints(e).getSecond();
						Point2D p1 = layout.transform(v1);
						Point2D p2 = layout.transform(v2);
						p1 = vv.getRenderContext().getMultiLayerTransformer()
								.transform(Layer.LAYOUT, p1);
						p2 = vv.getRenderContext().getMultiLayerTransformer()
								.transform(Layer.LAYOUT, p2);
						Renderer<String, String> renderer = vv.getRenderer();
						renderer.renderEdge(vv.getRenderContext(), layout, e);
					}
				}
			}
		});

		setLayout(new BorderLayout());
		setSize(100, 400);
		add(vv, BorderLayout.CENTER);
		add(setUpControls(), BorderLayout.SOUTH);
		
		
	}

	public ArrayList<String> getAllNodes() {
		ArrayList<String> nodes = new ArrayList<String>();
		for (java.lang.String n : g.getVertices()) {
			nodes.add(n);
		}
		return nodes;
	}
	
	ArrayList<Packet> trace = new ArrayList<Packet>(); 
	ArrayList<Packet> packets;
	
	public String data;

	public void sendMessage(int from, String message, int to) {
		mFrom = "" + from;
		mTo = "" + to;
		data = message;
		
		//Dividing Packets
	    packets = Packet.SendPackets(message, ""+from, ""+to);
	    
	    for(Packet p:packets){
	    	trace.add(p);
	    }
		markPacketRoute();
		repaint();
	}

	public int getGraphCount() {
		return g.getVertexCount();
	}

	boolean isBlessed(String e) {
		Iterator<String> it = path.getEdges().iterator();
		while (it.hasNext()) {
			String edge = it.next();
			if (edge.equalsIgnoreCase(e.toString()))
				return true;
		}
		return false;
	}

	public class MyEdgePaintFunction implements Transformer<String, Paint> {

		public Paint transform(String e) {
			if (path == null || path.getEdges().size() == 0)
				return Color.BLACK;
			if (isBlessed(e)) {
				return Color.BLUE;// Color.BLUE;
			} else {
				return Color.LIGHT_GRAY;
			}
		}
	}

	public Component getAddNode() {
		JButton addNode = new JButton("Add Node");
		addNode.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
				addNode();
			}
		});
		return addNode;
	}
	
	public Component getManualNodeConfiguration() {
		JButton addNode = new JButton("Manual Network Configuration");
		addNode.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
				
				int n = Integer.parseInt(JOptionPane.showInputDialog("Total number of Nodes ?"));
				
				TaskTable frame = new TaskTable(null,network,n);
		        frame.setTitle("Manual Network Configuration");
		        
		        
		        frame.pack();
		        frame.setLocation(150, 150);
		        frame.setVisible(true);
		        JPanel buttonPanel = new JPanel();

		        buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		        buttonPanel.add(frame.getSubmitButton());
		        frame.add(buttonPanel);
			}
		});
		return addNode;
	}
	
	public void sendMessage(String from, String to, String message) {
		
	}
	
	public Component getSendMessage() {
		JButton reports = new JButton("Send Message");
		reports.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
				ExistingSystem = true;
				for(String v:mGraph.getVertices()){
					if (path.getVertices().contains(v)) {
						 int newbatterylevel =batteryLevel.get(v).intValue()-1;
						 batteryLevel.put(v, newbatterylevel);
					}
				}
				markPacketRoute();
				repaint();
				
			}
		}); 
		return reports;
	}

	
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	List<String> gridheads = new ArrayList<String>();
	
	public Component getElectClusterHead() {
		JButton reports = new JButton("Elect Cluster Head");
		reports.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
				Map<String, Integer> sortedBatteryMapDesc = sortByComparator(batteryLevel, false);
				int countOfCluster = Integer.parseInt(clusterText.getText());
				int i=0;
				gridheads.clear();
				for(String key :sortedBatteryMapDesc.keySet()){
					if(i<countOfCluster) {
						i++;
						gridheads.add(key);
					}
				}
				JOptionPane.showMessageDialog(null, "Elected Cluster Heads are " + gridheads.toString());
			}
		}); 
		return reports;
	}
	
	public Component getFormGrids() {
		JButton reports = new JButton("Form Grid");
		reports.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
			     Collection<java.lang.String> edges = mGraph.getEdges();
			     List<String> edgeList = new ArrayList<String>();
			     for(String edge:edges){
			    	 edgeList.add(edge);
			     }
			     
			     for(String edge:edgeList){
			    	 mGraph.removeEdge(edge);
			     }
			     
			     //Connecting all cluster heads
			     for(String key:gridheads){
			    	 for(String key1: gridheads){
			    		 if(key!=key1){
			    			 mGraph.addEdge(key + key1, key, key1);
					    	 mGraph.addEdge(key1 + key, key1, key);
			    		 }
			    		 
			    	 }
			     }
			     
			     int i=0;
			     for(String key:mGraph.getVertices()){
			    	 if(!gridheads.contains(key)){

			    		 String toconnect = gridheads.get(i%gridheads.size());
			    		 mGraph.addEdge(key + toconnect, key, toconnect);
				    	 mGraph.addEdge(toconnect + key, toconnect, key);
			    		 i++;
			    	 }
			     }
			     vv.repaint();
			}
		}); 
		return reports;
	}
	
	JTextField clusterText;
	public Component getFormText() {
		clusterText = new JTextField();
		clusterText.setText("2");
		return clusterText;
	}
	
	public Component getReports() {
		JButton reports = new JButton("Reports");
		reports.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
				try {
					DisplayGraph.energyPut();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			    JOptionPane.showMessageDialog(null, "Graphs Successfully Generated");
			}
		}); 
		return reports;
	}
	
	public Component getBatteryButton() {
		JButton reports = new JButton("Battery");
		reports.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent ae) {
			    JOptionPane.showMessageDialog(null, "Battery Level for Node " + batterCb.getSelectedItem().toString() + " is " + batteryLevel.get(batterCb.getSelectedItem().toString()));
			}
		}); 
		return reports;
	}


	
	public void addNode() {
		vv.getRenderContext().getPickedVertexState().clear();
		vv.getRenderContext().getPickedEdgeState().clear();
		try {

			if (g.getVertexCount() < 100) {
				String v1 = (new Integer(g.getVertexCount())).toString();

				g.addVertex(v1);

				vv.getRenderContext().getPickedVertexState().pick(v1, true);

				if (v_prev != null) {
					String edge = "" + (g.getEdgeCount());
					vv.getRenderContext().getPickedEdgeState().pick(edge, true);
					if(!v_prev.equals(v1)){
						g.addEdge(v_prev + v1, v_prev, v1);
						g.addEdge(v1 + v_prev, v1, v_prev);
					}
					
					int rand = (int) (Math.random() * g.getVertexCount());
					if(!v1.equals(""+rand)){
					edge = "" + g.getEdgeCount();
					vv.getRenderContext().getPickedEdgeState().pick(edge, true);
					g.addEdge(v1 + rand, v1, "" + rand);
					g.addEdge(rand+v1, "" + rand, v1);
					}
				}

				v_prev = v1;

				layout.initialize();

				Relaxer relaxer = new VisRunner((IterativeContext) layout);
				relaxer.stop();
				relaxer.prerelax();
				StaticLayout<String, String> staticLayout = new StaticLayout<String, String>(
						g, layout);
				LayoutTransition<String, String> lt = new LayoutTransition<String, String>(
						vv, vv.getGraphLayout(), staticLayout);
				Animator animator = new Animator(lt);
				animator.start();
				vv.repaint();

			} else {
				done = true;
			}

		} catch (Exception e) {
			System.out.println(e);

		}
		fromCb.addItem(g.getVertexCount() - 1);
		toCb.addItem(g.getVertexCount() - 1);
		batterCb.addItem(g.getVertexCount() - 1);
		batteryLevel.put(""+(g.getVertexCount() - 1), initialbattery);
	}

	JComboBox fromCb;
	JComboBox toCb;
	JComboBox batterCb;
	
	private Component getSelectionBoxBattery() {

		Set<String> s = new TreeSet<String>();

		for (java.lang.String v : mGraph.getVertices()) {
			s.add(v);
		}
		batterCb = new JComboBox(s.toArray());
		batterCb.setSelectedIndex(-1);
	
		return batterCb;
	}

	private Component getSelectionBoxFrom() {

		Set<String> s = new TreeSet<String>();

		for (java.lang.String v : mGraph.getVertices()) {
			s.add(v);
		}
		fromCb = new JComboBox(s.toArray());
		fromCb.setSelectedIndex(-1);
		fromCb.setBackground(Color.WHITE);
		fromCb.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				java.lang.String v = fromCb.getSelectedItem().toString();
				mFrom = v;
			}
		});
		return fromCb;
	}

	private Component getSelectionBoxTo() {

		Set<String> s = new TreeSet<String>();

		for (java.lang.String v : mGraph.getVertices()) {
			s.add(v);
		}
		toCb = new JComboBox(s.toArray());
		toCb.setSelectedIndex(-1);
		toCb.setBackground(Color.WHITE);
		toCb.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				java.lang.String v = toCb.getSelectedItem().toString();
				mTo = v;
			}
		});
		return toCb;
	}

	public class MyEdgeStrokeFunction implements Transformer<String, Stroke> {
		protected final Stroke THIN = new BasicStroke(1);
		protected final Stroke THICK = new BasicStroke(2);

		public Stroke transform(String e) {
			
			if (path == null || path.getEdges().size() == 0)
				return THIN;

			if (isBlessed(e)) {
				return THICK;
			} else
				return THIN;
		}

	}

	public class MyVertexDrawPaintFunction<V> implements Transformer<V, Paint> {

		public Paint transform(V v) {
			return Color.black;
		}

	}

	public class MyVertexFillPaintFunction<String> implements
			Transformer<String, Paint> {

		public Paint transform(String v) {
			if (v.equals(mFrom)) {
				return Color.BLUE;
			}
			if (v.equals(mTo)) {
				return Color.BLUE;
			}
			if (gridheads.contains(v)){
				return Color.GREEN;
			}
			if (path == null) {
				return Color.LIGHT_GRAY;
			} else {
				if (path.getVertices().contains(v)) {
					return Color.RED;
				} else {
					return Color.LIGHT_GRAY;
				}
			}
		}

	}

	/**
     *  
     */
	private JPanel setUpControls() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
		label = new JLabel(
				"Please Select Sheduler");
		panel.add(label);
		JPanel jpPathType = new JPanel();
		
		jpPathType.add(new JLabel("Path type", SwingConstants.LEFT));
		jpPathType.add(getPathBox());

		jpPathType.add(new JLabel("Source", SwingConstants.LEFT));
		jpPathType.add(getSelectionBox(true));
		
		jpPathType.add(new JLabel("Destination", SwingConstants.LEFT));
		jpPathType.add(getSelectionBox(false));
		
		jpPathType.add(getSendMessage());
		jpPathType.add(getFormText());
		jpPathType.add(getElectClusterHead());
		jpPathType.add(getFormGrids());
		

		JPanel jp4 = new JPanel();
		jp4.add(getManualNodeConfiguration());
		jp4.add(getAddNode());
		jp4.setBackground(Color.white);
		
		jp4.add(getSelectionBoxBattery());
		jp4.add(getBatteryButton());
		jp4.add(getReports());
		

		panel.add(jpPathType);
		panel.add(jp4);
		return panel;
	}

	
	public Component getPathBox() {
		Set<String> str = new HashSet<String>();
		str.add("Existing System");
		str.add("Proposed System");
		final JComboBox path = new JComboBox(str.toArray());
		path.setSelectedItem("Existing System");
		path.setBackground(Color.WHITE);
		path.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String option = (String) path.getSelectedItem();
				if (option.equals("Existing System")) {
					if(gridheads.size()>0){
						ExistingSystem = false;
						path.setSelectedIndex(1);
						label.setText("Proposed System is Activated");
					} else {
						ExistingSystem = true;
						label.setText("Existing System is Activated");
					}
					
				} else {
					ExistingSystem = false;
					label.setText("Proposed System is Activated");
				}
				markPacketRoute();
				repaint();
			}
		});
		return path;
	}

	protected void markPacketRoute() {
		if (mFrom == null || mTo == null) {
			return;
		}
		Graph<String,String> existingFlow, proposedFlow, testFlow;
		existingFlow = getRoutes();
		proposedFlow = proposedScheduling();
		testFlow = getRoutes();
		path = getNewGraph();
		if (ExistingSystem) {
			path = getRoutes();			
		} else {
			path = proposedScheduling();
		}
	   
	   Calculation.writeLine("Proposed System");
	   Calculation.CalculateAndWrite(proposedFlow, existingFlow, testFlow, "HELLO", mFrom, mTo, batteryLevel);
	}

	private Component getSelectionBox(final boolean from) {

		if (from) {
			return getSelectionBoxFrom();
		}
		return getSelectionBoxTo();
	}

	/**
	 * @return
	 * 
	 */
	protected Graph<String, String> getRoutes() {
		path = getNewGraph();

		Set<String> visited = new HashSet<String>();
		LinkedList<String> currpath = new LinkedList<String>();

		existingScheduling(mFrom, visited, path, currpath);

		return path;

	}

	private void existingScheduling(String start, Set<String> visited,
			Graph<String, String> path, LinkedList<String> currpath) {

		if (visited.contains(start)) {
			return;
		}

		visited.add(start);

		currpath.addLast(start);

		if (start.equals(mTo)) {

			String pred = null;

			for (String l : currpath) {
				if (pred != null) {
					path.addVertex(l);
					path.addVertex(pred);
					path.addEdge((pred + l), pred, l);
				}
				pred = l;
			}
			currpath.removeLast();
			visited.remove(start);
			return;
		}

		for (String edge : mGraph.getOutEdges(start)) {
			String succ = mGraph.getDest(edge);
			existingScheduling(succ, visited, path, currpath);
		}

		visited.remove(start);
		currpath.removeLast();
	}

	protected Graph<String, String> proposedScheduling() {
		path = getNewGraph();
		LinkedList<String> currpath = new LinkedList<String>();
		DijkstraShortestPath<String, String> alg = new DijkstraShortestPath(
				mGraph);
		List<String> l = alg.getPath(mFrom, mTo);
		Iterator<String> itrCon = l.iterator();
		while (itrCon.hasNext()) {
			String c = (String) itrCon.next();
			String source = mGraph.getSource(c);
			String dest = mGraph.getDest(c);
			path.addEdge(source + dest, source, dest);
		}
		return path;
	}

	public void init() {

		Graph<String, String> ig = Graphs
				.<String, String> synchronizedGraph(new DirectedSparseGraph<String, String>());

		ObservableGraph<java.lang.String, java.lang.String> og = new ObservableGraph<String, String>(
				ig);
		og.addGraphEventListener(new GraphEventListener<String, String>() {

			public void handleGraphEvent(GraphEvent<String, String> evt) {
				System.err.println("got " + evt);

			}
		});
		this.g = og;
	}

	Graph<String, String> getGraph() {
		return g;
	}

	Graph<String, String> getNewGraph() {
		return new DirectedSparseGraph<String, String>();
	}
}