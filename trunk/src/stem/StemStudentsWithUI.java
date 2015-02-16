	package stem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

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

import edu.uci.ics.jung.algorithms.metrics.Metrics;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;

import masoncsc.util.ChartUtils;

import sim.display.ChartUtilities;
import sim.display.ChartUtilities.ProvidesDoubleDoubles;
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
import sim.util.Double2D;
import sim.util.Interval;
import sim.util.media.chart.ChartGenerator;
import sim.util.media.chart.HistogramGenerator;
import sim.util.media.chart.HistogramSeriesAttributes;
import sim.util.media.chart.ScatterPlotGenerator;
import sim.util.media.chart.ScatterPlotSeriesAttributes;
import sim.util.media.chart.TimeSeriesChartGenerator;
import stem.Student.Encouragement;
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
    HistogramGenerator organizedActivitiesDoneHist;
    HistogramGenerator[] interestHist = new HistogramGenerator[StemStudents.NUM_TOPICS];
    TimeSeriesChartGenerator aveInterestTimeSeries;
    ScatterPlotGenerator interestVsParticipationGen;

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

        i.setVolatile(false);
        i.addInspector(new SimpleInspector(model, this), "Main");
        i.addInspector(new SimpleInspector(new NetworkProperties(this), this), "Network");
        i.addInspector(new SimpleInspector(new LeaderProperties(this), this), "Leaders");
        i.addInspector(new SimpleInspector(model.ruleSet, this), "Rules");
        i.addInspector(new SimpleInspector(new MetricsProperties(this), this), "Metrics");

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
		
		organizedActivitiesDoneHist.updateSeries(0, model.dataLogger.organizedActivitiesDoneWatcher.getDataPoint());
		organizedActivitiesDoneHist.update(ChartGenerator.FORCE_KEY, true);
		
		interestVsParticipationGen.updateSeries(0, getInterestVsParticipation());
		interestVsParticipationGen.update(ChartGenerator.FORCE_KEY, true);
	}

	public void init(final Controller c) {
		super.init(c);
		
		aveInterestTimeSeries = ChartUtils.attachTimeSeries(
				new XYSeries[] {model.dataLogger.interest1Series.getData(), model.dataLogger.interest2Series.getData(), model.dataLogger.interest3Series.getData()}, 
        		"Average Interest Over Time", "Days", "Interest Level", c, 1);
		aveInterestTimeSeries.getFrame().setVisible(true);
		aveInterestTimeSeries.setYAxisRange(0, 1);
				
		interestHist[0] = ChartUtils.attachHistogram(null, 7, "Tech/Eng Index", "Interest Level", "Count", controller);
		interestHist[1] = ChartUtils.attachHistogram(null, 7, "Earth/Space Index", "Interest Level", "Count", controller);
		interestHist[2] = ChartUtils.attachHistogram(null, 7, "Human/Bio Index", "Interest Level", "Count", controller);
		// Make the histograms small
		for (int i = 0; i < 3; i++) {
			interestHist[i].setScale(0.5);
			interestHist[i].getFrame().setSize(373, 284);
			interestHist[i].getFrame().setVisible(false);
			interestHist[i].setXAxisRange(0, 1);
			interestHist[i].setYAxisRange(0, 100);
		}
		// set colors to match the colors used (by default) in the other plots
		interestHist[0].getChart().getXYPlot().getRenderer().setSeriesPaint(0, new Color(255,85,85));	// red
		interestHist[1].getChart().getXYPlot().getRenderer().setSeriesPaint(0, new Color(85,85,255));	// blue
		interestHist[2].getChart().getXYPlot().getRenderer().setSeriesPaint(0, new Color(85,255,85));	// green
		
		// place them underneath aveInterestTimeSeries
		int width = interestHist[0].getFrame().getWidth();
		int yLoc = aveInterestTimeSeries.getFrame().getHeight() + aveInterestTimeSeries.getFrame().getInsets().top + aveInterestTimeSeries.getFrame().getInsets().bottom;
		interestHist[0].getFrame().setLocation(0, yLoc);
		interestHist[1].getFrame().setLocation(width, yLoc);
		interestHist[2].getFrame().setLocation(width*2, yLoc);
		
		aveInterestHist = ChartUtils.attachHistogram(null, 7, "Average Interest", "Interest Level", "Count", controller);
		aveInterestHist.getFrame().setVisible(false);

		networkDisplay = new NetworkDisplay(this);
		NetworkDisplay.frame.setTitle("Friend Network");
		c.registerFrame(NetworkDisplay.frame);
		NetworkDisplay.frame.setVisible(false);

		activitiesDoneHist = ChartUtils.attachHistogram(null, 7, "Ad Hoc Activities Done per Day", "Number of Ad Hoc Activities per Day", "Count", controller);
		activitiesDoneHist.getFrame().setVisible(false);
		
		organizedActivitiesDoneHist = ChartUtils.attachHistogram(null, 7, "Organized Activities Done per Day", "Number of Organized Activities per Day", "Count", controller);
		organizedActivitiesDoneHist.getFrame().setVisible(false);

		
		// create gender ratio bar chart
		registerBarChart(c, "Activity Gender Ratio", "Activity", "Ratio of Female Participants", genderRatioDataset, PlotOrientation.HORIZONTAL, false, true, false, false);
		
		JFreeChart chart = registerBarChart(c, "Activity Counts", "Activity", "Number of Times Activity Has Been Done", activityCountDataset, PlotOrientation.VERTICAL, false, true, false, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		chart = registerBarChart(c, "Effect of Activities on Interest", "Activity", "Change in Interest Level", netEffectOfActivities, PlotOrientation.VERTICAL, false, true, false, true);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		((BarRenderer)chart.getCategoryPlot().getRenderer()).setItemMargin(0);
		
		chart = registerBarChart(c, "Effect of Rules on Interest", "Rule", "Change in Interest Level", netEffectOfRules, PlotOrientation.VERTICAL, false, true, false, true);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		((BarRenderer)chart.getCategoryPlot().getRenderer()).setItemMargin(0);
		

		HistogramGenerator histGen = ChartUtilities.buildHistogramGenerator(this, "Participation Histograms", "Number of Students");
		HistogramSeriesAttributes histAttr = new HistogramSeriesAttributes(histGen, "Participation Histograms", 0, new double[StemStudents.NUM_ACTIVITY_TYPES], 5, null);
		histGen.getFrame().setVisible(false);
		for (int j = 0; j < StemStudents.NUM_ACTIVITY_TYPES; j++) {
			final int activityIndex = j;
			ChartUtilities.addSeries(histGen, model.activityTypes.get(j).name, 5);
			ChartUtilities.scheduleSeries(this, histAttr, new ProvidesDoubles() {
				public double[] provide() {
					double[] counts = new double[model.students.size()];
					for (int i = 0; i < model.students.size(); i++)
						counts[i] = model.activityTypes.get(activityIndex).mapActivityCountToLikert(model.students.get(i).activityCounts[activityIndex]);
					return counts;
				}});
			
//			ChartUtilities.addSeries(this, histGen, model.activityTypes.get(j).name, new ProvidesDoubles() {
//				public double[] provide() {
//					double[] counts = new double[model.students.size()];
//					for (int i = 0; i < model.students.size(); i++)
//						counts[i] = model.activityTypes.get(activityIndex).mapActivityCountToLikert(model.students.get(i).activityCounts[activityIndex]);
//					return counts;
//				}
//			}, 5);
		}
		
		interestVsParticipationGen = ChartUtilities.buildScatterPlotGenerator(this, "Interest vs Participation", "Average Interest Level", "Activities per Day");
		ScatterPlotSeriesAttributes interestVsParticipationAttr = ChartUtilities.addSeries(interestVsParticipationGen, "Youth");
		interestVsParticipationAttr.setShapeNum(0);
		interestVsParticipationAttr.setSymbolOpacity(0.3);
		interestVsParticipationGen.setXAxisRange(0, 3.1);
		interestVsParticipationGen.setYAxisRange(-0.1, 1.0);
//		ChartUtilities.scheduleSeries(this, interestVsParticipationAttr, new ProvidesDoubleDoubles() {
//			public double[][] provide() {
//				return getInterestVsParticipation();
//			}
//		});
//		ChartUtilities.scheduleSeries(this, interestVsParticipationAttr, null);
		
		((Console)controller).setSize(400, 534);
	}
	
	@Override
	public boolean step() {
		super.step();
		
		String[] activityNames = new String[StemStudents.NUM_ACTIVITY_TYPES];
		String[] topicNames = new String[] { "Tech/Eng", "Earth/Space", "Human/Bio" };
		
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
	
	
	public Double2D[] convertToDouble2DArray(double[][] array) {
		int n = array.length;
		Double2D[] points = new Double2D[n];
		for (int i = 0; i < n; i++) {
			points[i] = new Double2D(array[i][0], array[i][1]);
		}
		
		return points;
	}
	
	public double[][] getInterestVsParticipation() {
		int n = model.students.size();
		double[][] array = new double[2][n];

		for (int i = 0; i < n; i++) {
			Student s = model.students.get(i);
			array[0][i] = s.activitiesDone / Math.max(model.schedule.getSteps(), 1.0);
			array[1][i] = s.interest.getAverage();
		}
		
		return array;
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
		
		public double getClusteringCoefficient() {
			Map<Student, Double> map = Metrics.clusteringCoefficients(model.network);

			double total = 0;
			for (Double d : map.values())
				total += d.doubleValue();
			return total / map.size();
		}
		
		public double getDiameter() {
			return DistanceStatistics.diameter(model.network);
		}
		
		int[] degrees = new int[StemStudents.numStudents];
		public int[] getDegreeHistogram() {
			for (int i = 0; i < model.students.size(); i++)
				degrees[i] = model.network.degree(model.students.get(i));
			
			return degrees;
		}

	}
	
	public class LeaderProperties {

		StemStudents model;
		StemStudentsWithUI modelUI;
		
		public LeaderProperties(StemStudentsWithUI modelUI) {
			this.modelUI = modelUI;
			this.model = modelUI.model;
		}

		public double getLeaderExpertise() { return model.leaderExpertise; }
		public void setLeaderExpertise(double val) { model.leaderExpertise = val; }
		public Object domLeaderExpertise() { return new Interval(0.0, 1.0); }
		
		public double getLeaderExpertiseNoise() { return model.leaderExpertiseNoise; }
		public void setLeaderExpertiseNoise(double val) { model.leaderExpertiseNoise = val; }
		public Object domLeaderExpertiseNoise() { return new Interval(0.0, 1.0); }
		
		public double getLeaderPassion() { return model.leaderPassion; }
		public void setLeaderPassion(double val) { model.leaderPassion = val; }
		public Object domLeaderPassion() { return new Interval(0.0, 1.0); }
		
		public double getLeaderPassionNoise() { return model.leaderPassionNoise; }
		public void setLeaderPassionNoise(double val) { model.leaderPassionNoise = val; }
		public Object domLeaderPassionNoise() { return new Interval(0.0, 1.0); }

		public double getExpertiseThreshold() { return model.expertiseThreshold; }
		public void setExpertiseThreshold(double val) { model.expertiseThreshold = val; }
		public Object domExpertiseThreshold() { return new Interval(0.0, 1.0); }

		public double getExpertiseThresholdNoise() { return model.expertiseThresholdNoise; }
		public void setExpertiseThresholdNoise(double val) { model.expertiseThresholdNoise = val; }
		public Object domExpertiseThresholdNoise() { return new Interval(0.0, 1.0); }

		public double getPassionThreshold() { return model.passionThreshold; }
		public void setPassionThreshold(double val) { model.passionThreshold = val; }
		public Object domPassionThreshold() { return new Interval(0.0, 1.0); }

		public double getPassionThresholdNoise() { return model.passionThresholdNoise; }
		public void setPassionThresholdNoise(double val) { model.passionThresholdNoise = val; }
		public Object domPassionThresholdNoise() { return new Interval(0.0, 1.0); }
		

		int n = 1000;
		double[] samples = new double[n];
		public double[] getExpertiseSample() {
			for (int i = 0; i < n; i++) {
				do {
				samples[i] = model.leaderExpertise + model.leaderExpertiseNoise * model.random.nextGaussian();
				} while(samples[i] < 0 || samples[i] > 1);
			}
			
			return samples;
		}
	}
	
	public class MetricsProperties {
		StemStudents model;
		StemStudentsWithUI modelUI;
		
		public MetricsProperties(StemStudentsWithUI modelUI) {
			this.modelUI = modelUI;
			this.model = modelUI.model;
		}
		
		private int activity = 0;
		public int getActivity() { return activity; }
		public void setActivity(int val) { activity = val; }
		public Object domActivity() { return model.activityNames; }
		
		public int[] getEncouragementLevel() {
			int[] array = new int[model.students.size()];
			for (int i = 0; i < model.students.size(); i++) {
				Student s = model.students.get(i);
				int count = 0;
				if (s.activityEncouragement[activity][Encouragement.Parent.ordinal()])
					count++;
				if (s.activityEncouragement[activity][Encouragement.Sibling.ordinal()])
					count++;
				if (s.activityEncouragement[activity][Encouragement.Friend.ordinal()])
					count++;
				
				array[i] = count;
			}
			
			return array;
		}
		
		private double average(double[] array) {
			double sum = 0;
			for (double value : array)
				sum += value;
			return sum / array.length;
		}
		
		public Double2D[] getInterestVsActivitiesPerDay() {
			Double2D[] array = new Double2D[model.students.size()];
			
			for (int i = 0; i < model.students.size(); i++) {
				Student s = model.students.get(i);
				double aveInterest = s.interest.getAverage();
				
				double activitiesPerDay = s.activitiesDone / Math.max(model.schedule.getSteps(), 1.0);
				array[i] = new Double2D(activitiesPerDay, aveInterest);
			}
			
			return array;
		}	
		
		public Double2D[] getInterestVsParticipation() {
			Double2D[] array = new Double2D[model.students.size()];
			
			for (int i = 0; i < model.students.size(); i++) {
				Student s = model.students.get(i);
				double aveInterest = s.interest.getAverage();
				
				double aveParticipation = average(s.participationRates);
				array[i] = new Double2D(aveParticipation, aveInterest);
			}
			
			return array;
		}
		
	}

	public static void main(String[] args) {
		new StemStudentsWithUI(args).createController();
	}
	
	public class ModelStats
	{
//		public int[] classCounts
	}
	
}
