package stem;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

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
    HistogramGenerator[] interestHist = new HistogramGenerator[StemStudents.NUM_TOPICS];

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
				updateCharts();
			}
		});
		

	}
	
	public void updateCharts() {

		aveInterestHist.updateSeries(0, model.averageInterestWatcher.getDataPoint());
		aveInterestHist.update(ChartGenerator.FORCE_KEY, true);
		
		for (int i = 0; i < StemStudents.NUM_TOPICS; i++) {
			interestHist[i].updateSeries(0, model.interestWatcher[i].getDataPoint());
			interestHist[i].update(ChartGenerator.FORCE_KEY, true);
		}
	}

	public void init(final Controller c) {
		super.init(c);
		
		networkDisplay = new NetworkDisplay(this);
		NetworkDisplay.frame.setTitle("Friend Network");
		c.registerFrame(NetworkDisplay.frame);
		NetworkDisplay.frame.setVisible(true);

		aveInterestHist = ChartUtils.attachHistogram(null, 7, "Average Interest", "Interest Level", "Count", controller);
		interestHist[0] = ChartUtils.attachHistogram(null, 7, "Technology/Engineering/Math", "Interest Level", "Count", controller);
		interestHist[1] = ChartUtils.attachHistogram(null, 7, "Earth/Space Science", "Interest Level", "Count", controller);
		interestHist[2] = ChartUtils.attachHistogram(null, 7, "Human/Biology", "Interest Level", "Count", controller);
		
//		interestHist[0].setScale(0.5);

//		ChartUtils.attachTimeSeries(new XYSeries[] {model.interestSeries.get(0).series, model.interestSeries.get(1).series, model.interestSeries.get(2).series}, 
//        		"Average Interest Over Time", "Days", "Interest Level", c, 1);
		ChartUtils.attachTimeSeries(new XYSeries[] {model.interest1Series.series, model.interest2Series.series, model.interest3Series.series}, 
        		"Average Interest Over Time", "Days", "Interest Level", c, 1);
		


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
