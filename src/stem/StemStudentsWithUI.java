package stem;

import java.awt.Color;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

import masoncsc.util.ChartUtils;

import sim.display.ChartUtilities;
import sim.display.ChartUtilities.ProvidesDoubles;
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

    // Gender ratios
    DefaultCategoryDataset genderRatioDataset = new DefaultCategoryDataset();
    
    // Activity counts
    DefaultCategoryDataset activityCountDataset = new DefaultCategoryDataset();
    

	public StemStudentsWithUI() {
		super(new StemStudents(System.currentTimeMillis()));
		model = (StemStudents)state;
	}

	public StemStudentsWithUI(SimState state) {
		super(state);
		model = (StemStudents)state;
	}

	public static String getName() {
		return "STEM Interest";
	}

	public Object getSimulationInspectedObject() {
		return state;
	} // non-volatile

    @Override
    public Inspector getInspector()
    {
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

		aveInterestHist.updateSeries(0, model.dataLogger.averageInterestWatcher.getDataPoint());
		aveInterestHist.update(ChartGenerator.FORCE_KEY, true);
		
		for (int i = 0; i < StemStudents.NUM_TOPICS; i++) {
			interestHist[i].updateSeries(0, model.dataLogger.interestWatcher[i].getDataPoint());
			interestHist[i].update(ChartGenerator.FORCE_KEY, true);
		}
		activitiesDoneHist.updateSeries(0, model.dataLogger.activitiesDoneWatcher.getDataPoint());
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
				new XYSeries[] {model.dataLogger.interest1Series.getData(), model.dataLogger.interest2Series.getData(), model.dataLogger.interest3Series.getData()}, 
        		"Average Interest Over Time", "Days", "Interest Level", c, 1);
		aveInterestTimeSeries.getFrame().setVisible(true);
		aveInterestTimeSeries.setYAxisRange(0, 1);
		
		// create gender ratio bar chart
		registerBarChart(c, "Activity Gender Ratio", "Activity", "Ratio of Female Participants", genderRatioDataset, PlotOrientation.HORIZONTAL, false, true, false);
		
		JFreeChart chart = registerBarChart(c, "Activity Counts", "Activity", "Number of Times Activity Has Been Done", activityCountDataset, PlotOrientation.VERTICAL, false, true, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		

		HistogramGenerator test = ChartUtilities.buildHistogramGenerator(this, "Participation Histograms", "Number of Students");
		for (int j = 0; j < StemStudents.NUM_ACTIVITY_TYPES; j++) {
			final int activityIndex = j;
//			HistogramGenerator test = ChartUtilities.buildHistogramGenerator(this, model.activityTypes.get(j).name + " Participation", "Number of Students");

			ChartUtilities.addSeries(this, test, model.activityTypes.get(j).name, new ProvidesDoubles() {
				public double[] provide() {
					double[] counts = new double[model.students.size()];
					for (int i = 0; i < model.students.size(); i++)
						//counts[i] = model.students.get(i).activityCounts[activityIndex];
						counts[i] = model.activityTypes.get(activityIndex).mapActivityCountToLikert(model.students.get(i).activityCounts[activityIndex]);
					return counts;
				}
			}, 5);
		}
		
		((Console)controller).setSize(400, 500);
	}
	
	@Override
	public boolean step() {
		super.step();
		
		String[] activityNames = new String[StemStudents.NUM_ACTIVITY_TYPES];
		
		for (int i = 0; i < StemStudents.NUM_ACTIVITY_TYPES; i++) {
			activityNames[i] = model.activityTypes.get(i).name;
			genderRatioDataset.setValue(model.dataLogger.activityGenderRatios[i], "Activity", activityNames[i]);
			
			activityCountDataset.setValue(model.dataLogger.activityCounts[i], "Activity", activityNames[i]);
		}
		
		return true;
	}
	
	public JFreeChart registerBarChart(final Controller c, String title, String categoryAxisLabel, String valueAxisLabel, 
			CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) 
	{ 
		JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(Color.BLACK);

        CategoryPlot pl = chart.getCategoryPlot();
        pl.setBackgroundPaint(Color.WHITE);
        pl.setRangeGridlinePaint(Color.BLUE);

        // set the range axis to display integers only...  
        NumberAxis rangeAxis = (NumberAxis) pl.getRangeAxis();
//        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        rangeAxis.setRangeWithMargins(0, 1);
        CategoryAxis xAxis = pl.getDomainAxis();
//        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);


        ((BarRenderer)pl.getRenderer()).setShadowVisible(false);
        ((BarRenderer)pl.getRenderer()).setBarPainter(new StandardBarPainter());
        
        
        ChartFrame frame = new ChartFrame(title, chart);
        frame.setVisible(true);
        frame.setSize(200, 600);

        frame.pack();
        c.registerFrame(frame);
        
        return chart;
	}
	
	public void studentSelected(Student s) {
		System.out.println("Youth clicked.");
		Bag inspectors = new Bag();
		Bag names = new Bag();
		inspectors.add(new SimpleInspector(s, this));
		names.add("Youth");
		controller.setInspectors(inspectors, names);
	}
	
	public class NetworkProperties {
		
	}

	public static void main(String[] args) {
		new StemStudentsWithUI().createController();
	}
	
	public class ModelStats
	{
//		public int[] classCounts
	}
}
