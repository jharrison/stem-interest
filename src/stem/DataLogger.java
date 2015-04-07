package stem;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import masoncsc.datawatcher.*;
import masoncsc.util.Pair;
import sim.engine.SimState;
import sim.engine.Steppable;
import stem.activities.Activity;
import stem.rules.Rule;

public class DataLogger implements Steppable
{
	private static final long serialVersionUID = 1L;
	
	StemStudents model;
	
	
	ScreenDataWriter averageInterestScreenWriter;
	DoubleArrayWatcher averageInterestWatcher;
	DoubleArrayWatcher[] interestWatcher = new DoubleArrayWatcher[StemStudents.NUM_TOPICS];
	DoubleArrayWatcher activitiesDoneWatcher;
	DoubleArrayWatcher organizedActivitiesDoneWatcher;

	TimeSeriesDataStore<Double> interest1Series = new TimeSeriesDataStore<Double>(StemStudents.TOPIC_NAMES[0]);
	TimeSeriesDataStore<Double> interest2Series = new TimeSeriesDataStore<Double>(StemStudents.TOPIC_NAMES[1]);
	TimeSeriesDataStore<Double> interest3Series = new TimeSeriesDataStore<Double>(StemStudents.TOPIC_NAMES[2]);
	TimeSeriesDataStore<Double> interest4Series = new TimeSeriesDataStore<Double>(StemStudents.TOPIC_NAMES[3]);

	TimeSeriesDataStore<Double> proportionOfInterestedYouth = new TimeSeriesDataStore<Double>("Proportion of Youth Above Interest Threshold");
	
	ArrayList<DataWatcher> dataWatchers = new ArrayList<DataWatcher>();
	
//	/** Counts of how many times a student has done each activity */
	public int[] activityCounts = new int[StemStudents.NUM_ACTIVITY_TYPES];		
//	public int[] getActivityCounts() { return activityCounts; }
	
	/** Counts of how many times a female has done each activity */
	int[] activityGenderCounts = new int[StemStudents.NUM_ACTIVITY_TYPES];
	
//	/** Ratio of girls to boys among participants of each activity */
	double[] activityGenderRatios = new double[StemStudents.NUM_ACTIVITY_TYPES];
//	public double[] getActivityGenderRatios() { return activityGenderRatios; }
	
	DataWatcher interestTrendWatcher;
	FileDataWriter interestTrendFileWriter;

	public double[][] netEffectOfActivities = new double[StemStudents.NUM_ACTIVITY_TYPES][StemStudents.NUM_TOPICS];
	public double[][] netEffectOfRules;	// to be initialize in the constructor

	public double[][] netEffectOfRulesOnParticipation;	// to be initialize in the constructor
	public double[] netEffectOfEncouragementOnInterest;	// to be initialize in the constructor

	public long activitiesDone = 0;
	public long activitiesDoneWithFriends = 0;
	public long[] activitiesDoneWithFriendsAll = new long[StemStudents.NUM_ACTIVITY_TYPES];		
	
	DataOutputStream youthLogFile = null;


	public DataLogger(StemStudents model) {
		super();
		this.model = model;
	}
	
	private double calcAverageInterest(int topicIndex) {
		if (model.students.size() == 0)
			return 0;
		
        double total = 0;
        for (Student s : model.students)
        	total += s.interest.topics[topicIndex];
        
        return total / model.students.size();
	}	
	
	public double calcAverageInterest() {
		if (model.students.size() == 0)
			return 0;
		
        double total = 0;
        for (Student s : model.students)
        	for (int j = 0; j < StemStudents.NUM_TOPICS; j++) 
				total += s.interest.topics[j];
        
        return total / (model.students.size() * StemStudents.NUM_TOPICS);
	}

	public void init() {
		activitiesDone = 0;
		activitiesDoneWithFriends = 0;
		Arrays.fill(activitiesDoneWithFriendsAll, 0);
		Arrays.fill(activityCounts, 0);
		Arrays.fill(activityGenderCounts, 0);
		Arrays.fill(activityGenderRatios, 0.0);
		for (double[] row : netEffectOfActivities)
			Arrays.fill(row, 0.0);
		netEffectOfRules = new double[model.ruleSet.rules.size()][StemStudents.NUM_TOPICS];

		netEffectOfRulesOnParticipation = new double[model.ruleSet.rules.size()][StemStudents.NUM_ACTIVITY_TYPES];
		netEffectOfEncouragementOnInterest = new double[4]; // Parent, Sibling, Friend, NoOne
		
		dataWatchers.clear();
		
		averageInterestWatcher = new DoubleArrayWatcher() {
			// anonymous constructor
			{
				data = new double[model.numStudents];
			}

			@Override
			protected void updateDataPoint() {
				for (int i = 0; i < model.students.size(); i++)
					data[i] = model.students.get(i).getAverageInterest();				
			}
			
			@Override
			public String getCSVHeader() {
				return null;
			}
		};
		dataWatchers.add(averageInterestWatcher);

		for (int i = 0; i < StemStudents.NUM_TOPICS; i++) {
			final int topic = i;
			interestWatcher[i] = new DoubleArrayWatcher() {
				// anonymous constructor
				{
					data = new double[model.numStudents];
				}

				@Override
				protected void updateDataPoint() {
					for (int j = 0; j < model.students.size(); j++)
						data[j] = model.students.get(j).interest.topics[topic];				
				}
				
				@Override
				public String getCSVHeader() {
					return null;
				}
			};
			dataWatchers.add(interestWatcher[i]);
		}
		
		interest1Series.clear();
        dataWatchers.add(new PairDataWatcher<Long, Double>() {
            { addListener(interest1Series); }

            @Override
            protected void updateDataPoint() {
                final long currentStep = model.schedule.getSteps();
                dataPoint = new Pair<Long, Double>(currentStep, calcAverageInterest(0));
            }

            @Override
            public String getCSVHeader() {
                return "Step, " + interest1Series.getDescription();
            }
        });

        interest2Series.clear();
        dataWatchers.add(new PairDataWatcher<Long, Double>() {
            { addListener(interest2Series); }

            @Override
            protected void updateDataPoint() {
                final long currentStep = model.schedule.getSteps();
                dataPoint = new Pair<Long, Double>(currentStep, calcAverageInterest(1));
            }

            @Override
            public String getCSVHeader() {
                return "Step, " + interest2Series.getDescription();
            }
        });

        interest3Series.clear();
        dataWatchers.add(new PairDataWatcher<Long, Double>() {
            { addListener(interest3Series); }

            @Override
            protected void updateDataPoint() {
                final long currentStep = model.schedule.getSteps();
                dataPoint = new Pair<Long, Double>(currentStep, calcAverageInterest(2));
            }

            @Override
            public String getCSVHeader() {
                return "Step, " + interest3Series.getDescription();
            }
        });

        interest4Series.clear();
        dataWatchers.add(new PairDataWatcher<Long, Double>() {
            { addListener(interest4Series); }

            @Override
            protected void updateDataPoint() {
                final long currentStep = model.schedule.getSteps();
                dataPoint = new Pair<Long, Double>(currentStep, calcAverageInterest(3));
            }

            @Override
            public String getCSVHeader() {
                return "Step, " + interest4Series.getDescription();
            }
        });

        proportionOfInterestedYouth.clear();
        dataWatchers.add(new PairDataWatcher<Long, Double>() {
            { addListener(proportionOfInterestedYouth); }

            @Override
            protected void updateDataPoint() {
                final long currentStep = model.schedule.getSteps();
                dataPoint = new Pair<Long, Double>(currentStep, calcProportionOfInterested());
            }

            @Override
            public String getCSVHeader() {
                return "Step, " + proportionOfInterestedYouth.getDescription();
            }
        });
        
        
		activitiesDoneWatcher = new DoubleArrayWatcher() {
			// anonymous constructor
			{
				data = new double[model.numStudents];
			}

			@Override
			protected void updateDataPoint() {
				for (int i = 0; i < model.students.size(); i++)
//					data[i] = model.students.get(i).activitiesDone / Math.max(model.schedule.getSteps(), 1.0);	// all activities
//					data[i] = model.students.get(i).organizedActivitiesDone / Math.max(model.schedule.getSteps(), 1.0);	// organized
					data[i] = (model.students.get(i).activitiesDone - model.students.get(i).organizedActivitiesDone) / Math.max(model.schedule.getSteps(), 1.0);	// ad hoc
			}
			
			@Override
			public String getCSVHeader() {
				return null;
			}
		};
		dataWatchers.add(activitiesDoneWatcher);
        
		organizedActivitiesDoneWatcher = new DoubleArrayWatcher() {
			// anonymous constructor
			{
				data = new double[model.numStudents];
			}

			@Override
			protected void updateDataPoint() {
				for (int i = 0; i < model.students.size(); i++)
					data[i] = model.students.get(i).organizedActivitiesDone / Math.max(model.schedule.getSteps(), 1.0);	// organized activities
			}
			
			@Override
			public String getCSVHeader() {
				return null;
			}
		};
		dataWatchers.add(organizedActivitiesDoneWatcher);
		
		if (!model.outputFilename.isEmpty()) {
			interestTrendFileWriter = new FileDataWriter();
			interestTrendWatcher = new ListDataWatcher<String>() {
				{ addListener(interestTrendFileWriter); }
	
	            @Override
	            protected void updateDataPoint() {
	                dataList.clear();
	                dataList.add(String.valueOf(model.params.run));
	                dataList.add(String.valueOf(model.schedule.getSteps()));
	                dataList.add(String.valueOf(calcAverageInterest(0)));
	                dataList.add(String.valueOf(calcAverageInterest(1)));
	                dataList.add(String.valueOf(calcAverageInterest(2)));
	                dataList.add(String.valueOf(calcAverageInterest(3)));
	                dataList.add(String.valueOf(model.coordinationLevel));
	                dataList.add(String.valueOf(model.interestThreshold));
	                dataList.add(String.valueOf(model.interestThresholdNoise));
	                dataList.add(String.valueOf(model.leaderExpertise));
	                dataList.add(String.valueOf(model.leaderExpertiseNoise));
	                dataList.add(String.valueOf(model.leaderPassion));
	                dataList.add(String.valueOf(model.leaderPassionNoise));
	                dataList.add(String.valueOf(model.expertiseThreshold));
	                dataList.add(String.valueOf(model.expertiseThresholdNoise));
	                dataList.add(String.valueOf(model.passionThreshold));
	                dataList.add(String.valueOf(model.passionThresholdNoise));
	                
	            }
	
	            @Override
	            public String getCSVHeader() {
	                String header = "Run, Step, AveIntr1, AveIntr2, AveIntr3, AveIntr4, Coordination, IntrThresh, IntrThreshNs, ldrExp, ldrExpNs, ldrPsn, ldrPsnNs, expThr, expThrNs, psnThr, psnThrNs";
	                return header + "\n";
	            }
			};
			dataWatchers.add(interestTrendWatcher);
		}
		
	}
	
	public double calcProportionOfInterested() {
		int total = 0;
		for (int i = 0; i < model.students.size(); i++)
			for (int j = 0; j < StemStudents.NUM_TOPICS; j++)
				if (model.students.get(i).interest.topics[j] > model.interestThreshold) {
					total++;
					break;
				}
		
		return total / (double)model.numStudents;			
	}
	
	public void start() {
		if (!model.outputFilename.isEmpty())
			interestTrendFileWriter.InitFileDataWriter(model.outputFilename, interestTrendWatcher);
	}
	
	/** Event that is triggered when an activity is done. */
	public void studentParticipated(Student s, Activity activity) {
		int index = activity.type.id;
		
		activityCounts[index]++;
		
		if (s.isFemale)
			activityGenderCounts[index]++;
		
		activityGenderRatios[index] = activityGenderCounts[index] / (double)activityCounts[index];
	}

	/** Event that is triggered when a student's interest levels are changed. */
	public void studentInterestChanged(Student s, Activity a, int topicIndex, double delta, Rule r) {
		netEffectOfActivities[a.type.id][topicIndex] += delta;
		
		int ruleIndex = model.ruleSet.rules.indexOf(r);
		netEffectOfRules[ruleIndex][topicIndex] += delta;
		
		for (int i = 0; i < 4; i++) {
			if (s.activityEncouragement[a.type.id][i])
				netEffectOfEncouragementOnInterest[i] += delta;
		}
	}


	/** Event that is triggered when a student's interest levels are changed. */
	public void studentParticipationChanged(Student s, Activity a, double delta, Rule r) {

		int ruleIndex = model.ruleSet.rules.indexOf(r);
		netEffectOfRulesOnParticipation[ruleIndex][a.type.id] += delta;
		
	}
	
	public void friendRuleFired(Activity a, boolean friendPresent) {
		activitiesDone++;
		if (friendPresent) {
			activitiesDoneWithFriends++;
			activitiesDoneWithFriendsAll[a.type.id]++;
		}
	}

	@Override
	public void step(SimState arg0) {
		for (DataWatcher<?> dw : dataWatchers)
			dw.update();
		
//		if (arg0.schedule.getSteps() % 365 == 0)
//			writeYouthLog(model.youthLogFilename);
	}
	
	public void writeYouthLog(String filename) {
		
		try {
			if (youthLogFile == null) {
				youthLogFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			
				// write headers first time through
				youthLogFile.writeBytes("Step, ID, Sex, Intr1, Intr2, Intr3, Intr4, ActDone, OrgActDone, ActDoneWithFriends, OrgActDoneWithFriends, ParentEnc, SiblingEnc, FriendEnc");
				youthLogFile.writeBytes("\n");
			}
			
			// sort students by ID
			Collections.sort(model.students);

			// write data for each student
			StringBuilder sb = new StringBuilder();
			for (Student s : model.students) {
				sb = new StringBuilder();
				sb.append(model.schedule.getSteps()).append(", ");
				sb.append(s.id).append(", ");
				sb.append(s.isFemale ? "F" : "M").append(", ");
				sb.append(String.format("%.6f", s.interest.topics[0])).append(", ");
				sb.append(String.format("%.6f", s.interest.topics[1])).append(", ");
				sb.append(String.format("%.6f", s.interest.topics[2])).append(", ");
				sb.append(String.format("%.6f", s.interest.topics[3])).append(", ");
				sb.append(s.activitiesDone).append(", ");
				sb.append(s.organizedActivitiesDone).append(", ");
				sb.append(s.activitiesDoneWithFriends).append(", ");
				sb.append(s.organizedActivitiesDoneWithFriends).append(", ");
				sb.append(s.calcParentEncouragement()).append(", ");
				sb.append(s.calcSiblingEncouragement()).append(", ");
				sb.append(s.calcFriendEncouragement());
				
				sb.append("\n");
				youthLogFile.writeBytes(sb.toString());
			}
			
//			youthLogFile.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void close() {
		if (interestTrendFileWriter != null)
			interestTrendFileWriter.close();
		
		if (youthLogFile != null) try {
			youthLogFile.close();
			youthLogFile = null;
		}
		catch (IOException e) { e.printStackTrace(); }
	}

}
