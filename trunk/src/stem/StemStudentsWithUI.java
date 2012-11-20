package stem;

import java.util.ArrayList;

import javax.swing.JFrame;

import masoncsc.util.ChartUtils;

import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.SimpleInspector;
import sim.util.Bag;
import sim.util.media.chart.ChartGenerator;
import sim.util.media.chart.HistogramGenerator;
import stem.network.NetworkDisplay;

/**
 * GUI front end to the StemStudents model.
 * @author Joey Harrison
 * @author Matthew Hendrey
 * @version 0.1, October 12, 2012
 *
 */
public class StemStudentsWithUI extends GUIState
{

	public NetworkDisplay networkDisplay;
	StemStudents model;
    private ArrayList<ChartGenerator> chartGenerators = new ArrayList<ChartGenerator>();
    HistogramGenerator aveInterestHist;

	public StemStudentsWithUI() {
		super(new StemStudents(System.currentTimeMillis()));
		model = (StemStudents)state;
	}

	public StemStudentsWithUI(SimState state) {
		super(state);
		model = (StemStudents)state;
	}

	public static void main(String[] args) {
		new StemStudentsWithUI().createController();
	}

	public static String getName() {
		return "STEM Students";
	}

	public Object getSimulationInspectedObject() {
		return state;
	} // non-volatile

	public void start() {
		super.start();
		// set up our portrayals
		setupPortrayals();
		System.out.println("Start");
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}
	
	

	@SuppressWarnings("serial")
	public void setupPortrayals() {
		networkDisplay.reset();
		networkDisplay.repaint();
		
		chartGenerators.clear();
		
		this.scheduleRepeatingImmediatelyBefore(networkDisplay);
		
		this.scheduleRepeatingImmediatelyAfter(new Steppable() {			
			@Override
			public void step(SimState state) {
				if (state.schedule.getTime() == Schedule.AFTER_SIMULATION)	// watch out for that last step, it's a doozy
					return;
			}			
		});
		
		this.scheduleRepeatingImmediatelyAfter(new Steppable() {
			
			@Override
			public void step(SimState state) {
//				for (ChartGenerator c : chartGenerators)
//					c.updateChartLater(state.schedule.getSteps());
				updateCharts();
			}
		});
		

	}
	
	public void updateCharts() {

		aveInterestHist.updateSeries(0, model.averageInterestWatcher.getDataPoint());
		aveInterestHist.update(ChartGenerator.FORCE_KEY, true);
	}

	public void init(final Controller c) {
		super.init(c);
		
		networkDisplay = new NetworkDisplay(this);
		NetworkDisplay.frame.setTitle("Friend Network");
		c.registerFrame(NetworkDisplay.frame);
		NetworkDisplay.frame.setVisible(true);

		aveInterestHist = ChartUtils.attachHistogram(null, 10, "Average Interest", "Interest Level", "Count", controller);

		((Console)controller).setSize(400, 500);
	}
	
	public void studentSelected(Student s) {
		System.out.println("Student clicked.");
		Bag inspectors = new Bag();
		Bag names = new Bag();
		inspectors.add(new SimpleInspector(s, this));
		names.add("Student");
		controller.setInspectors(inspectors, names);
	}

}
