package stem;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

import masoncsc.util.ChartUtils;

import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.Inspector;
import sim.portrayal.SimpleInspector;
import sim.portrayal.inspector.TabbedInspector;
import sim.util.Bag;
import sim.util.media.chart.ChartGenerator;
import sim.util.media.chart.HistogramGenerator;
import sim.util.media.chart.TimeSeriesChartGenerator;
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
    HistogramGenerator activitiesDoneHist;
    HistogramGenerator[] interestHist = new HistogramGenerator[StemStudents.NUM_TOPICS];
    TimeSeriesChartGenerator aveInterestTimeSeries;

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
		return "STEM Kids";
	}

	public Object getSimulationInspectedObject() {
		return state;
	} // non-volatile

    @Override
    public Inspector getInspector()
    {
//        super.getInspector();

        TabbedInspector i = new TabbedInspector();

        i.setVolatile(true);
        i.addInspector(new SimpleInspector(model, this), "System");
        i.addInspector(new SimpleInspector(model.ruleSet, this), "Rules");

        return i;
    }
    
	public void start() {
		super.start();
		// set up our portrayals
		setupPortrayals();
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
	
	public JFreeChart createBarChart(final CategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createBarChart("Activity Counts", "Activity", "Count", dataset, PlotOrientation.VERTICAL, true, true, false);
		
		return chart;
	}
    private CategoryDataset createDataset() {
        
        // row keys...
        final String series1 = "Activity";

        // column keys...
        final String category1 = "Category 1";
        final String category2 = "Category 2";
        final String category3 = "Category 3";
        final String category4 = "Category 4";
        final String category5 = "Category 5";

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (int i = 0; i < model.activityNames.length; i++) {
        	dataset.addValue(i, series1, model.activityNames[i]);
        }
        
        return dataset;
        
    }
	
	public void updateCharts() {

		aveInterestHist.updateSeries(0, model.averageInterestWatcher.getDataPoint());
		aveInterestHist.update(ChartGenerator.FORCE_KEY, true);
		
		for (int i = 0; i < StemStudents.NUM_TOPICS; i++) {
			interestHist[i].updateSeries(0, model.interestWatcher[i].getDataPoint());
			interestHist[i].update(ChartGenerator.FORCE_KEY, true);
		}
		activitiesDoneHist.updateSeries(0, model.activitiesDoneWatcher.getDataPoint());
		activitiesDoneHist.update(ChartGenerator.FORCE_KEY, true);
	}

	public void init(final Controller c) {
		super.init(c);
		
		networkDisplay = new NetworkDisplay(this);
		NetworkDisplay.frame.setTitle("Friend Network");
		c.registerFrame(NetworkDisplay.frame);
		NetworkDisplay.frame.setVisible(true);

		aveInterestHist = ChartUtils.attachHistogram(null, 7, "Average Interest", "Interest Level", "Count", controller);
		aveInterestHist.getFrame().setVisible(false);
		
		interestHist[0] = ChartUtils.attachHistogram(null, 7, "Exploration Index", "Interest Level", "Count", controller);
		interestHist[1] = ChartUtils.attachHistogram(null, 7, "Science Index", "Interest Level", "Count", controller);
		interestHist[2] = ChartUtils.attachHistogram(null, 7, "Human Index", "Interest Level", "Count", controller);
		// Make the histograms small
		for (int i = 0; i < 3; i++) {
			interestHist[i].setScale(0.5);
			interestHist[i].getFrame().setSize(373, 294);
		}

		activitiesDoneHist = ChartUtils.attachHistogram(null, 7, "Activities Done per Day", "Number of Activities per Day", "Count", controller);
		activitiesDoneHist.getFrame().setVisible(false);

		aveInterestTimeSeries = ChartUtils.attachTimeSeries(
				new XYSeries[] {model.interest1Series.series, model.interest2Series.series, model.interest3Series.series}, 
        		"Average Interest Over Time", "Days", "Interest Level", c, 1);
		aveInterestTimeSeries.getFrame().setVisible(true);
		aveInterestTimeSeries.setYAxisRange(0, 1);

		((Console)controller).setSize(400, 500);
	}
	
	public void studentSelected(Student s) {
		System.out.println("Kid clicked.");
		Bag inspectors = new Bag();
		Bag names = new Bag();
		inspectors.add(new SimpleInspector(s, this));
		names.add("Student");
		controller.setInspectors(inspectors, names);
	}

}
