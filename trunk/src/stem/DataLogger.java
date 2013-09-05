package stem;

import java.util.ArrayList;
import java.util.Arrays;

import masoncsc.datawatcher.*;
import masoncsc.util.Pair;
import sim.engine.SimState;
import sim.engine.Steppable;
import stem.activities.Activity;

public class DataLogger implements Steppable
{
	private static final long serialVersionUID = 1L;
	
	StemStudents model;
	
	
	ScreenDataWriter averageInterestScreenWriter;
	DoubleArrayWatcher averageInterestWatcher;
	DoubleArrayWatcher[] interestWatcher = new DoubleArrayWatcher[StemStudents.NUM_TOPICS];
	DoubleArrayWatcher activitiesDoneWatcher;

	TimeSeriesDataStore<Double> interest1Series = new TimeSeriesDataStore<Double>("Exploration Index");
	TimeSeriesDataStore<Double> interest2Series = new TimeSeriesDataStore<Double>("Science Index");
	TimeSeriesDataStore<Double> interest3Series = new TimeSeriesDataStore<Double>("Human Index");
	
	ArrayList<DataWatcher> dataWatchers = new ArrayList<DataWatcher>();
	
//	/** Counts of how many times a student has done each activity */
	int[] activityCounts = new int[StemStudents.NUM_ACTIVITY_TYPES];		
//	public int[] getActivityCounts() { return activityCounts; }
	
	/** Counts of how many times a female has done each activity */
	int[] activityGenderCounts = new int[StemStudents.NUM_ACTIVITY_TYPES];
	
//	/** Ratio of girls to boys among participants of each activity */
	double[] activityGenderRatios = new double[StemStudents.NUM_ACTIVITY_TYPES];
//	public double[] getActivityGenderRatios() { return activityGenderRatios; }
	
	FileDataWriter averageInterestWriter;
	


	public DataLogger(StemStudents model) {
		super();
		this.model = model;
	}

	public void init() {
		Arrays.fill(activityCounts, 0);
		Arrays.fill(activityGenderCounts, 0);
		Arrays.fill(activityGenderRatios, 0.0);
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
                double total = 0;
                for (Student s : model.students)
                	total += s.interest.topics[0];
                dataPoint = new Pair<Long, Double>(currentStep, (total / model.students.size()));
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
                double total = 0;
                for (Student s : model.students)
                	total += s.interest.topics[1];
                dataPoint = new Pair<Long, Double>(currentStep, (total / model.students.size()));
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
                double total = 0;
                for (Student s : model.students)
                	total += s.interest.topics[2];
                dataPoint = new Pair<Long, Double>(currentStep, (total / model.students.size()));
            }

            @Override
            public String getCSVHeader() {
                return "Step, " + interest3Series.getDescription();
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
					data[i] = model.students.get(i).activitesDone / Math.max(model.schedule.getSteps(), 1.0);				
			}
			
			@Override
			public String getCSVHeader() {
				return null;
			}
		};
		dataWatchers.add(activitiesDoneWatcher);
		
	}
	
	/** Event that is triggered when an activity is done. */
	public void studentParticipated(Student s, Activity activity) {
		int index = activity.type.id;
		
		activityCounts[index]++;
		
		if (s.isFemale)
			activityGenderCounts[index]++;
		
		activityGenderRatios[index] = activityGenderCounts[index] / (double)activityCounts[index];
	}

	@Override
	public void step(SimState arg0) {
		for (DataWatcher<?> dw : dataWatchers)
			dw.update();
	}

}
