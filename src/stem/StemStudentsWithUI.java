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
import sim.util.Interval;
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
	
	/** Controls the size at which nodes in the network are drawn */
	public double nodeSize = 5.0;
	
    private ArrayList<ChartGenerator> chartGenerators = new ArrayList<ChartGenerator>();
    HistogramGenerator aveInterestHist;
    HistogramGenerator activitiesDoneHist;
    HistogramGenerator[] interestHist = new HistogramGenerator[StemStudents.NUM_TOPICS];
    TimeSeriesChartGenerator aveInterestTimeSeries;

    // Gender ratios
    DefaultCategoryDataset genderRatioDataset = new DefaultCategoryDataset();
    
    // Activity counts
    DefaultCategoryDataset activityCountDataset = new DefaultCategoryDataset();
    DefaultCategoryDataset netEffectOfActivities = new DefaultCategoryDataset();
    DefaultCategoryDataset netEffectOfRules = new DefaultCategoryDataset();
    

	public StemStudentsWithUI(String[] args) {
		super(new StemStudents(System.currentTimeMillis(), args));
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
        i.addInspector(new SimpleInspector(new NetworkProperties(this), this), "Network");
        i.addInspector(new SimpleInspector(model.ruleSet, this), "Rules");

        return i;
    }
    
	public void start() {
		super.start();
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
		NetworkDisplay.frame.setVisible(false);

		aveInterestHist = ChartUtils.attachHistogram(null, 7, "Average Interest", "Interest Level", "Count", controller);
		aveInterestHist.getFrame().setVisible(false);
		
		interestHist[0] = ChartUtils.attachHistogram(null, 7, "Exploration Index", "Interest Level", "Count", controller);
		interestHist[1] = ChartUtils.attachHistogram(null, 7, "Science Index", "Interest Level", "Count", controller);
		interestHist[2] = ChartUtils.attachHistogram(null, 7, "Human Index", "Interest Level", "Count", controller);
		// Make the histograms small
		for (int i = 0; i < 3; i++) {
			interestHist[i].setScale(0.5);
			interestHist[i].getFrame().setSize(373, 294);
			interestHist[i].getFrame().setVisible(false);
		}

		activitiesDoneHist = ChartUtils.attachHistogram(null, 7, "Activities Done per Day", "Number of Activities per Day", "Count", controller);
		activitiesDoneHist.getFrame().setVisible(false);

		aveInterestTimeSeries = ChartUtils.attachTimeSeries(
				new XYSeries[] {model.dataLogger.interest1Series.getData(), model.dataLogger.interest2Series.getData(), model.dataLogger.interest3Series.getData()}, 
        		"Average Interest Over Time", "Days", "Interest Level", c, 1);
		aveInterestTimeSeries.getFrame().setVisible(true);
		aveInterestTimeSeries.setYAxisRange(0, 1);
		
		// create gender ratio bar chart
		registerBarChart(c, "Activity Gender Ratio", "Activity", "Ratio of Female Participants", genderRatioDataset, PlotOrientation.HORIZONTAL, false, true, false, false);
		
		JFreeChart chart = registerBarChart(c, "Activity Counts", "Activity", "Number of Times Activity Has Been Done", activityCountDataset, PlotOrientation.VERTICAL, false, true, false, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		chart = registerBarChart(c, "Effect of Activies on Interest", "Activity", "Change in Interest Level", netEffectOfActivities, PlotOrientation.VERTICAL, false, true, false, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		((BarRenderer)chart.getCategoryPlot().getRenderer()).setItemMargin(0);
		
		chart = registerBarChart(c, "Effect of Rules on Interest", "Rule", "Change in Interest Level", netEffectOfRules, PlotOrientation.VERTICAL, false, true, false, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		((BarRenderer)chart.getCategoryPlot().getRenderer()).setItemMargin(0);
		

		HistogramGenerator test = ChartUtilities.buildHistogramGenerator(this, "Participation Histograms", "Number of Students");
		test.getFrame().setVisible(false);
		for (int j = 0; j < StemStudents.NUM_ACTIVITY_TYPES; j++) {
			final int activityIndex = j;
			ChartUtilities.addSeries(this, test, model.activityTypes.get(j).name, new ProvidesDoubles() {
				public double[] provide() {
					double[] counts = new double[model.students.size()];
					for (int i = 0; i < model.students.size(); i++)
						counts[i] = model.activityTypes.get(activityIndex).mapActivityCountToLikert(model.students.get(i).activityCounts[activityIndex]);
					return counts;
				}
			}, 5);
		}
		
		((Console)controller).setSize(400, 550);
	}
	
	@Override
	public boolean step() {
		super.step();
		
		String[] activityNames = new String[StemStudents.NUM_ACTIVITY_TYPES];
		String[] topicNames = new String[] { "Exploration", "Science", "Human/Bio" };
		
		for (int i = 0; i < StemStudents.NUM_ACTIVITY_TYPES; i++) {
			activityNames[i] = model.activityTypes.get(i).name;
			genderRatioDataset.setValue(model.dataLogger.activityGenderRatios[i], "Activity", activityNames[i]);
			
			activityCountDataset.setValue(model.dataLogger.activityCounts[i], "Activity", activityNames[i]);
			
			for (int j = 0; j < StemStudents.NUM_TOPICS; j++)
				netEffectOfActivities.setValue(model.dataLogger.netEffectOfActivities[i][j], topicNames[j], activityNames[i]);
		}
		
		for (int i = 0; i < model.ruleSet.rules.size(); i++)
			for (int j = 0; j < StemStudents.NUM_TOPICS; j++)
				netEffectOfRules.setValue(model.dataLogger.netEffectOfRules[i][j], topicNames[j], model.ruleSet.rules.get(i).getClass().getSimpleName());
		
		return true;
	}
	
	public JFreeChart registerBarChart(final Controller c, String title, String categoryAxisLabel, String valueAxisLabel, 
			CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls, boolean visible) 
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

        ((BarRenderer)pl.getRenderer()).setShadowVisible(false);
        ((BarRenderer)pl.getRenderer()).setBarPainter(new StandardBarPainter());
        
        
        ChartFrame frame = new ChartFrame(title, chart);
        frame.setVisible(visible);
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
		StemStudents model;
		StemStudentsWithUI modelUI;
		
		public NetworkProperties(StemStudentsWithUI modelUI) {
			this.modelUI = modelUI;
			this.model = modelUI.model;
		}
		
		public int getNumFriendsPerYouth() { return model.numFriendsPerStudent; }
		public void setNumFriendsPerYouth(int val) { model.numFriendsPerStudent = val; }

		public double getSmallWorldRewireProbability() { return model.smallWorldRewireProbability; }
		public void setSmallWorldRewireProbability(double val) { model.smallWorldRewireProbability = val; }
		public Object domSmallWorldRewireProbability() { return new Interval(0.0, 1.0); }
		

		public double getInterGenderRewireProbability() { return model.interGenderRewireProbability; }
		public void setInterGenderRewireProbability(double val) { model.interGenderRewireProbability = val; }
		public Object domInterGenderRewireProbability() { return new Interval(0.0, 1.0); }
	
		public double getNodeSize() { return modelUI.nodeSize; }
		public void setNodeSize(double val) { modelUI.nodeSize = val; }
		public Object domNodeSize() { return new Interval(0.0, 10.0); }

		/** Probability of making a new friend when participating in an activity. */
		public double getMakeFriendProbability() { return model.makeFriendProbability; }
		public void setMakeFriendProbability(double val) { model.makeFriendProbability = val; }
		public Object domMakeFriendProbability() { return new Interval(0.0,0.5);}

		/** Probability of closing a triad, i.e. become friends with a friend of a friend. */
		public double getCloseTriadProbability() { return model.closeTriadProbability; }
		public void setCloseTriadProbability(double val) { model.closeTriadProbability = val; }
		public Object domCloseTriadProbability() { return new Interval(0.0,0.5); }

	}

	public static void main(String[] args) {
		new StemStudentsWithUI(args).createController();
	}
	
	public class ModelStats
	{
//		public int[] classCounts
	}
	
}
