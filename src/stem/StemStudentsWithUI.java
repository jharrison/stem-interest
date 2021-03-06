package stem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import masoncsc.util.Stats;

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
import stem.activities.ActivityType;
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

	Color[] topicColors = new Color[] { 
			new Color(255,85,85), 		// red
			new Color(85,85,255), 		// blue
			new Color(85,255,85), 		// green
			new Color(255,255,85) 		// yellow
	};
	
	/** Controls the size at which nodes in the network are drawn */
	public double nodeSize = 5.0;
	
    private ArrayList<ChartGenerator> chartGenerators = new ArrayList<ChartGenerator>();
    HistogramGenerator aveInterestHist;
    HistogramGenerator activitiesDoneHist;
    HistogramGenerator organizedActivitiesDoneHist;
    HistogramGenerator[] interestHist = new HistogramGenerator[StemStudents.NUM_TOPICS];
    TimeSeriesChartGenerator aveInterestTimeSeries;
    TimeSeriesChartGenerator percentOfInterestedYouthTimeSeries;
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
				new XYSeries[] {model.dataLogger.interest1Series.getData(), model.dataLogger.interest2Series.getData(), model.dataLogger.interest3Series.getData(), model.dataLogger.interest4Series.getData()}, 
        		"Average Interest Over Time", "Days", "Interest Level", c, 1);
		aveInterestTimeSeries.getFrame().setVisible(false);
		aveInterestTimeSeries.setYAxisRange(0, 1);
		

		percentOfInterestedYouthTimeSeries = ChartUtils.attachTimeSeries(model.dataLogger.proportionOfInterestedYouth.getData(), "Day", c, 1);
				
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++) {
			// create and attach the histogram
			interestHist[i] = ChartUtils.attachHistogram(null, 7, StemStudents.TOPIC_NAMES[i], "Interest Level", "Count", controller, false);
			
			// make it small
			interestHist[i].setScale(0.5);
			interestHist[i].getFrame().setSize(373, 284);
			interestHist[i].setXAxisRange(0, 1);
			interestHist[i].setYAxisRange(0, 100);

			// set colors to match the colors used (by default) in the other plots
			interestHist[i].getChart().getXYPlot().getRenderer().setSeriesPaint(0, topicColors[i]);
		}
				
		// place the histograms underneath aveInterestTimeSeries
		int width = interestHist[0].getFrame().getWidth();
		int height = interestHist[0].getFrame().getHeight();
		int yLoc = aveInterestTimeSeries.getFrame().getHeight() + aveInterestTimeSeries.getFrame().getInsets().top + aveInterestTimeSeries.getFrame().getInsets().bottom;
		interestHist[0].getFrame().setLocation(0, yLoc);
		interestHist[1].getFrame().setLocation(width, yLoc);
		interestHist[2].getFrame().setLocation(0, yLoc+height);
		interestHist[3].getFrame().setLocation(width, yLoc+height);
		
		aveInterestHist = ChartUtils.attachHistogram(null, 7, "Average Interest", "Interest Level", "Count", controller, false);

		networkDisplay = new NetworkDisplay(this);
		NetworkDisplay.frame.setTitle("Friend Network");
		NetworkDisplay.frame.setVisible(false);
		c.registerFrame(NetworkDisplay.frame);

		activitiesDoneHist = ChartUtils.attachHistogram(null, 7, "Ad Hoc Activities Done per Day", "Number of Ad Hoc Activities per Day", "Count", controller, false);
		
		organizedActivitiesDoneHist = ChartUtils.attachHistogram(null, 7, "Organized Activities Done per Day", "Number of Organized Activities per Day", "Count", controller, false);

		
		// create gender ratio bar chart
		registerBarChart(c, "Activity Gender Ratio", "Activity", "Ratio of Female Participants", genderRatioDataset, PlotOrientation.HORIZONTAL, false, true, false, false);
		
		JFreeChart chart = registerBarChart(c, "Activity Counts", "Activity", "Number of Times Activity Has Been Done", activityCountDataset, PlotOrientation.VERTICAL, false, true, false, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		chart = registerBarChart(c, "Effect of Activities on Interest", "Activity", "Change in Interest Level", netEffectOfActivities, PlotOrientation.VERTICAL, false, true, false, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		((BarRenderer)chart.getCategoryPlot().getRenderer()).setItemMargin(0);
		
		chart = registerBarChart(c, "Effect of Rules on Interest", "Rule", "Change in Interest Level", netEffectOfRules, PlotOrientation.VERTICAL, false, true, false, false);
		chart.getCategoryPlot().getRangeAxis().setAutoRange(true);
		chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		((BarRenderer)chart.getCategoryPlot().getRenderer()).setItemMargin(0);
		

		// This is commented out because changes to ChartUtilities broke it
//		HistogramGenerator histGen = ChartUtilities.buildHistogramGenerator(this, "Participation Histograms", "Number of Students");
//		HistogramSeriesAttributes histAttr = new HistogramSeriesAttributes(histGen, "Participation Histograms", 0, new double[StemStudents.NUM_ACTIVITY_TYPES], 5, null);
//		histGen.getFrame().setVisible(false);
//		for (int j = 0; j < StemStudents.NUM_ACTIVITY_TYPES; j++) {
//			final int activityIndex = j;
//			ChartUtilities.addSeries(histGen, model.activityTypes.get(j).name, 5);
//			ChartUtilities.scheduleSeries(this, histAttr, new ProvidesDoubles() {
//				public double[] provide() {
//					double[] counts = new double[model.students.size()];
//					for (int i = 0; i < model.students.size(); i++)
//						counts[i] = model.activityTypes.get(activityIndex).mapActivityCountToLikert(model.students.get(i).activityCounts[activityIndex]);
//					return counts;
//				}});
//			
////			ChartUtilities.addSeries(this, histGen, model.activityTypes.get(j).name, new ProvidesDoubles() {
////				public double[] provide() {
////					double[] counts = new double[model.students.size()];
////					for (int i = 0; i < model.students.size(); i++)
////						counts[i] = model.activityTypes.get(activityIndex).mapActivityCountToLikert(model.students.get(i).activityCounts[activityIndex]);
////					return counts;
////				}
////			}, 5);
//		}
		
		interestVsParticipationGen = ChartUtilities.buildScatterPlotGenerator(this, "Interest vs Participation", "Average Interest Level", "Activities per Day");
		interestVsParticipationGen.getFrame().setVisible(true);
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
		
		((Console)controller).setSize(424, 518);	// just big enough to show all the model properties without needing to scroll
	}
	
	@Override
	public boolean step() {
		super.step();
		
		String[] activityNames = new String[StemStudents.NUM_ACTIVITY_TYPES];
		
		for (int i = 0; i < StemStudents.NUM_ACTIVITY_TYPES; i++) {
			activityNames[i] = model.activityTypes.get(i).name;
			genderRatioDataset.setValue(model.dataLogger.activityGenderRatios[i], "Activity", activityNames[i]);
			
			activityCountDataset.setValue(model.dataLogger.activityCounts[i], "Activity", activityNames[i]);
			
			for (int j = 0; j < StemStudents.NUM_TOPICS; j++)
				netEffectOfActivities.setValue(model.dataLogger.netEffectOfActivities[i][j], StemStudents.TOPIC_NAMES[j], activityNames[i]);
		}
		
		for (int i = 0; i < model.ruleSet.rules.size(); i++)
			for (int j = 0; j < StemStudents.NUM_TOPICS; j++)
				netEffectOfRules.setValue(model.dataLogger.netEffectOfRules[i][j], StemStudents.TOPIC_NAMES[j], model.ruleSet.rules.get(i).getClass().getSimpleName());
		
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
		

//		int n = 1000;
//		double[] samples = new double[n];
//		public double[] getExpertiseSample() {
//			for (int i = 0; i < n; i++) {
//				do {
//				samples[i] = model.leaderExpertise + model.leaderExpertiseNoise * model.random.nextGaussian();
//				} while(samples[i] < 0 || samples[i] > 1);
//			}
//			
//			return samples;
//		}
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
		
		public double[] getParticipationRates() {
			double[] array = new double[model.students.size()];

			for (int i = 0; i < model.students.size(); i++) {
				Student s = model.students.get(i);
				array[i] = s.participationRates[this.activity];
			}
			
			return array;
		}
		
		double[] expectedActivitiesPerDay = new double[StemStudents.numStudents];
		/**
		 * Get the expected number of activities per day. 
		 * @return
		 */
		public double[] getExpectedActivitiesPerDay() {
			Arrays.fill(expectedActivitiesPerDay, 0);
			if ((model.students == null) || model.students.isEmpty())
				return expectedActivitiesPerDay;
			
			for (int i = 0; i < StemStudents.numStudents; i++) {
				int activitiesPerYear = 0;
				Student s = model.students.get(i);
				for (int j = 0; j < StemStudents.NUM_ACTIVITY_TYPES; j++) {
					ActivityType at = model.activityTypes.get(j);
					double opportunities = at.calcOpportunitiesPerYear() / (double)at.daysBetween;
					activitiesPerYear += opportunities * s.participationRates[j];
				}
				expectedActivitiesPerDay[i] = activitiesPerYear / 365.0;
			}
			
			return expectedActivitiesPerDay;
		}

		
		public double[] getTotalActivitiesPerDay() {
			if ((model == null) || (model.dataLogger == null) || (model.dataLogger.activitiesDoneWatcher == null))
				return null;
			
			double[] totalActivitiesPerDay = new double[StemStudents.numStudents];
			double[] adHoc = model.dataLogger.activitiesDoneWatcher.getDataPoint();
			double[] organized = model.dataLogger.organizedActivitiesDoneWatcher.getDataPoint();

			for (int i = 0; i < StemStudents.numStudents; i++) {
				totalActivitiesPerDay[i] = adHoc[i] + organized[i];
			}
			
			return totalActivitiesPerDay;
		}
		
		public double[] getTotalActivitiesPerDayAveraged() {
			double[] totalActivitiesPerDay = new double[StemStudents.numStudents];
			double years = Math.max(1, model.years);

			for (int i = 0; i < StemStudents.numStudents; i++) {
				totalActivitiesPerDay[i] = model.totalActivities[i] / years;
			}
			
			return totalActivitiesPerDay;
		}
		
		public boolean getCompareActivitiesPerDay() { return false; }
		public void setCompareActivitiesPerDay(boolean val) {
			double[] expected = getExpectedActivitiesPerDay();
			double[] actual = getTotalActivitiesPerDay();
			double[] averaged = getTotalActivitiesPerDayAveraged();

			double abe = Stats.calcAreaBetweenECDFs(expected, actual);
			double abeAve = Stats.calcAreaBetweenECDFs(expected, averaged);
			System.out.format("ActivitiesPerDay ABE, expected vs actual: %.3f, expected vs average of %d years: %.3f\n", abe, (int)model.years, abeAve);
			
			int[] expectedCounts = getExpectedActivityCounts();
			int[] actualCounts = model.dataLogger.activityCounts;
			for (int i = 0; i < StemStudents.NUM_ACTIVITY_TYPES; i++) {
				ActivityType at = model.activityTypes.get(i);
				System.out.format("Expected: %6d, Actual %6d, (%s)\n", expectedCounts[i], actualCounts[i], at.name);
			}
			
		}
		
		private double canDoToday(ActivityType at, int day) {
			Calendar date = new GregorianCalendar(2012, 8, 4);	// Sept 4th. Month is zero-based for some strange reason
			date.add(Calendar.DATE, day);
			
			boolean schoolDay = model.isSchoolDay(date);
			boolean weekend = model.isWeekend(date);
			boolean summer = model.isSummer(date);

			// is this a valid day for this activity?
			if ((schoolDay && !at.onSchoolDay) ||
				(weekend && !at.onWeekendDay) ||
				(summer && !at.onSummer))
				return 0;
			
			return 1;
		}
		
		private double calcExpectedActivitiesPerYear(ActivityType at, double p) {
			double[] e = new double[365];
			int b = at.daysBetween;

			for (int i = 0; i < 365; i++) {
				e[i] = canDoToday(at, i);
				if (e[i] == 0)
					continue;
				
				e[i] = p;
				
				for (int j = 1; j < b; j++) {
					if ((i-j) >= 0)
						e[i] *= (1-e[i-j]);
				}		
				
			}
			
			double totalExp = 0;
			for (int i = 0; i < 365; i++)
				totalExp += e[i];

			return totalExp;
		}
		
		public int[] getExpectedActivityCounts() {
			int[] counts = new int[StemStudents.NUM_ACTIVITY_TYPES];
			Arrays.fill(counts, 0);
			for (int i = 0; i < StemStudents.NUM_ACTIVITY_TYPES; i++) {
				ActivityType at = model.activityTypes.get(i);
				double opportunities = at.calcOpportunitiesPerYear() / (double)at.daysBetween;
				for (Student s : model.students) {
//					counts[i] += Math.ceil(opportunities * s.participationRates[i]);
					counts[i] += calcExpectedActivitiesPerYear(at, s.participationRates[i]);
				}
			}
			
			return counts;
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
