package stem.network;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.freehep.util.export.ExportDialog;
import org.jfree.util.ShapeUtilities;

import sim.display.Display2D;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.gui.SimpleColorMap;
import stem.StemStudents;
import stem.StemStudentsWithUI;
import stem.Student;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * A demonstrator for some of the graph layout algorithms. Allows the user to
 * interactively select one of several graphs, and one of several layouts, and
 * visualizes the combination.
 * 
 * @@author Danyel Fisher
 * @@author Joshua O'Madadhain
 * 
 * Modified by Joey Harrison
 */
public class NetworkDisplay implements Steppable {
	private static final long serialVersionUID = 1L;
	
	private static final ImageIcon CAMERA_ICON = iconFor("Camera.png");
	private static final ImageIcon CAMERA_ICON_P = iconFor("CameraPressed.png");

	private static UndirectedSparseGraph<Student, SimpleEdge> graph;
	private StemStudents model;
	private StemStudentsWithUI modelWithUI;

	public static JFrame frame;
	private LayoutChooser layoutChooser;
	private static VisualizationViewer<Student, SimpleEdge> vv;

	private static final class LayoutChooser implements ActionListener {

		private final JComboBox jcb;

		private final VisualizationViewer<Student, SimpleEdge> vv;

		private LayoutChooser(JComboBox jcb, VisualizationViewer<Student, SimpleEdge> vv) {
			super();
			this.jcb = jcb;
			this.vv = vv;
		}

		public void actionPerformed(ActionEvent arg0) {

			Object[] constructorArgs = { graph };

			Class<?> layoutClass = (Class<?>) jcb.getSelectedItem();
			try {
				Constructor<?> constructor = layoutClass.getConstructor(new Class[] { Graph.class });
				Object o = constructor.newInstance(constructorArgs);
				@SuppressWarnings("unchecked")
				Layout<Student, SimpleEdge> l = (Layout<Student, SimpleEdge>) o;
				l.setInitializer(vv.getGraphLayout());
				l.setSize(vv.getSize());

				//LayoutTransition<String, Integer> lt = new LayoutTransition<String, Integer>(vv, vv.getGraphLayout(), l);
				LayoutTransition<Student, SimpleEdge> lt = new LayoutTransition<Student, SimpleEdge>(vv, vv.getGraphLayout(), l);
				Animator animator = new Animator(lt);
				animator.start();
				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
				vv.repaint();

			} catch (Exception e) {	e.printStackTrace(); }
		}
	}

	public NetworkDisplay(StemStudentsWithUI withUI) {

		this.modelWithUI = withUI;
		this.model = (StemStudents)withUI.state;

		recreateGraph();

		JPanel jp = getGraphPanel();

		frame = new JFrame();
		frame.getContentPane().add(jp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	private JPanel getGraphPanel() {

		vv = new VisualizationViewer<Student, SimpleEdge>(new FRLayout<Student, SimpleEdge>(graph));
		vv.getModel().getRelaxer().setSleepTime(50);

		vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<Student>(vv.getPickedVertexState(), Color.red, Color.yellow));

		final DefaultModalGraphMouse<Student, SimpleEdge> graphMouse = new DefaultModalGraphMouse<Student, SimpleEdge>();

		vv.setGraphMouse(graphMouse);

		/*
		 * This section contains the Transformers which draw the vertices and edges.
		 */
		final BasicStroke width1Stroke = new BasicStroke(1);
		final BasicStroke width3Stroke = new BasicStroke(3);
		final SimpleColorMap colorMap = new SimpleColorMap();


		Transformer<Student, Paint> vertexPainter = new Transformer<Student, Paint>() {						
			public Paint transform(Student s) {
				return colorMap.getColor(s.getAverageInterest());
			}
		};

		Transformer<SimpleEdge, Stroke> edgeStrokeTransformer = new Transformer<SimpleEdge, Stroke>() {
			public Stroke transform(SimpleEdge e) {				
				if (e.type == "parent")					
					return width3Stroke;
				
				return width1Stroke;
			}
		};
		
		Transformer<Student, Shape> vertexShapeTransformer = new Transformer<Student, Shape>() {
			public Shape transform(Student v) {
				double radius = v.friends.size() * model.nodeSize;
//                return new Ellipse2D.Double(-5, -5, 10, 10);
                return new Ellipse2D.Double(-radius, -radius, 2*radius, 2*radius);
			}			
		};


		vv.getRenderContext().setVertexFillPaintTransformer(vertexPainter);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Student, SimpleEdge>());
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexShapeTransformer(vertexShapeTransformer);
		

		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});
		JButton reset = new JButton("reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Layout<Student, SimpleEdge> layout = vv.getGraphLayout();
				layout.initialize();
				Relaxer relaxer = vv.getModel().getRelaxer();
				if (relaxer != null) {
					// if(layout instanceof IterativeContext) {
					relaxer.stop();
					relaxer.prerelax();
					relaxer.relax();
				}
			}
		});

		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(((DefaultModalGraphMouse<Student, SimpleEdge>) vv.getGraphMouse()).getModeListener());
		
		vv.addGraphMouseListener(new GraphMouseListener<Student>() {			
			@Override
			public void graphReleased(Student arg0, MouseEvent arg1) {}
			
			@Override
			public void graphPressed(Student arg0, MouseEvent arg1) {}
			
			@Override
			public void graphClicked(Student arg0, MouseEvent arg1) {
				modelWithUI.studentSelected(arg0);				
			}
		});

		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setLayout(new BorderLayout());
		jp.add(vv, BorderLayout.CENTER);
		Class<?>[] combos = getCombos();
		final JComboBox jcb = new JComboBox(combos);
		// use a renderer to shorten the layout name presentation
		jcb.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String valueString = value.toString();
				valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
				return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
			}
		});
		
		layoutChooser = new LayoutChooser(jcb, vv);
		jcb.addActionListener(layoutChooser);
		jcb.setSelectedItem(FRLayout.class);

		JPanel control_panel = new JPanel(new GridLayout(1, 1));
		JPanel bottomControls = new JPanel();
		control_panel.add(bottomControls);
		jp.add(control_panel, BorderLayout.NORTH);
		
		bottomControls.add(plus);
		bottomControls.add(minus);
		bottomControls.add(jcb);
		bottomControls.add(modeBox);
		bottomControls.add(reset);
		
		JButton snapshotButton;
		snapshotButton = new JButton(CAMERA_ICON);
		snapshotButton.setPressedIcon(CAMERA_ICON_P);
		snapshotButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		snapshotButton.setToolTipText("Create a snapshot (as a PNG file)");
		snapshotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				takeSnapshot();
			}
		});
		bottomControls.add(snapshotButton);

		return jp;
	}

	/**
	 * @@return
	 */
	private static Class<?>[] getCombos() {
		List<Class <?>> layouts = new ArrayList<Class <?>>();
		layouts.add(KKLayout.class);
		layouts.add(FRLayout.class);
		layouts.add(CircleLayout.class);
		layouts.add(SpringLayout.class);
		layouts.add(SpringLayout2.class);
		layouts.add(ISOMLayout.class);
		return (Class[]) layouts.toArray(new Class[0]);
	}
	
	public void reset() {			
		recreateGraph();
		Layout<Student, SimpleEdge> layout = vv.getGraphLayout();
		layout.setGraph(graph);
		
		layoutChooser.actionPerformed(null);

		vv.repaint();
	}
	
	public void repaint() {
		frame.repaint();
		vv.repaint();
	}

	double nextUpdate = 0;

	public void step(SimState state) {
		vv.repaint();
	}

	public void recreateGraph() {
		if (model != null)
			NetworkDisplay.graph = model.network;	
		else
			NetworkDisplay.graph = new UndirectedSparseGraph<Student, SimpleEdge>();
	}

	public static void takeSnapshot() {
		Color original = vv.getBackground();
		vv.setBackground(Color.WHITE);
		ExportDialog export = new ExportDialog();
		export.showExportDialog(vv, "Export view as ...", vv, "export");
		vv.setBackground(original);
	}

	static ImageIcon iconFor(String name) {
		return new ImageIcon(Display2D.class.getResource(name));
	}
}
