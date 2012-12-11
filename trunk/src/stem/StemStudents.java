package stem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import masoncsc.datawatcher.*;
import masoncsc.util.Pair;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Interval;
import stem.network.*;

/**
 * An agent-based model of students interest in STEM with SYNERGIES project.
 * @author Joey Harrison
 * @author Matthew Hendrey
 * @version 0.1, October 12, 2012
 *
 */
public class StemStudents extends SimState
{
	private static final long serialVersionUID = 1L;
	
	public GregorianCalendar date;
	
	ArrayList<Student> students = new ArrayList<Student>();
	ArrayList<ActivityType> activityTypes = new ArrayList<ActivityType>();

	static public final int NUM_ACTIVITY_TYPES = 16;
	static public final int NUM_TOPICS = 3;	

	public ArrayList<Activity> scienceClasses = new ArrayList<Activity>();
	public ArrayList<Activity> activities = new ArrayList<Activity>();

	public UndirectedSparseGraph<Student, SimpleEdge> network = new UndirectedSparseGraph<Student, SimpleEdge>();
	
	ScreenDataWriter averageInterestScreenWriter;
	DoubleArrayWatcher averageInterestWatcher;
	DoubleArrayWatcher[] interestWatcher = new DoubleArrayWatcher[NUM_TOPICS];

	TimeSeriesDataStore<Double> interest1Series = new TimeSeriesDataStore<Double>("Technology/Engineering/Math");
	TimeSeriesDataStore<Double> interest2Series = new TimeSeriesDataStore<Double>("Earth/Space Science");
	TimeSeriesDataStore<Double> interest3Series = new TimeSeriesDataStore<Double>("Human/Biology");
	
	ArrayList<DataWatcher> dataWatchers = new ArrayList<DataWatcher>();
	
	
	// Start getters/setters here
	
	public int numStudents = 170;  //# from survey that have valid values
	public int getNumStudents() { return numStudents; }
	public void setNumStudents(int val) { numStudents = val; }

	public int classSize = 17; //Approx. # from data.  Adjusted slightly to get same number in each class.
	public int getClassSize() { return classSize; }
	public void getClassSize(int val) { classSize = val; }
	
	public int numFriendsPerStudent = 3;	
	public int getNumFriendsPerStudent() { return numFriendsPerStudent; }
	public void setNumFriendsPerStudent(int val) { numFriendsPerStudent = val; }

	public double smallWorldRewireProbability = 0.5;
	public double getSmallWorldRewireProbability() { return smallWorldRewireProbability; }
	public void setSmallWorldRewireProbability(double val) { smallWorldRewireProbability = val; }
	public Object domSmallWorldRewireProbability() { return new Interval(0.0, 1.0); }

	public double interestThreshold = 0.5;
	public double getInterestThreshold() { return interestThreshold; }
	public void setInterestThreshold(double val) { interestThreshold = val; }
	public Object domInterestThreshold() { return new Interval(0.0, 1.0); }

	public double expertiseThreshold = 0.5;
	public double getExpertiseThreshold() { return expertiseThreshold; }
	public void setExpertiseThreshold(double val) { expertiseThreshold = val; }
	public Object domExpertiseThreshold() { return new Interval(0.0, 1.0); }

	public double passionThreshold = 0.5;
	public double getPassionThreshold() { return passionThreshold; }
	public void setPassionThreshold(double val) { passionThreshold = val; }
	public Object domPassionThreshold() { return new Interval(0.0, 1.0); }
	
	public double interestChangeRate = 0.01;
	public double getInterestChangeRate() { return interestChangeRate; }
	public void setInterestChangeRate(double val) { interestChangeRate = val; }
	public Object domInterestChangeRate() { return new Interval(0.0, 1.0); }

	public double interestDecayRate = 1.0;
	public double getInterestDecayExponent() { return interestDecayRate; }
	public void setInterestDecayExponent(double val) { interestDecayRate = val; }
	public Object domInterestDecayExponent() { return new Interval(0.0, 1.0); }
	
	public double nodeSize = 2.5;
	public double getNodeSize() { return nodeSize; }
	public void setNodeSize(double val) { nodeSize = val; }
	public Object domNodeSize() { return new Interval(0.0, 10.0); }

	
	public int maxActivitiesPerDay = 3;	
	public int getMaxActivitiesPerDay() { return maxActivitiesPerDay; }
	public void setgetMaxActivitiesPerDay(int val) { maxActivitiesPerDay = val; }
	

	public StemStudents(long seed) {
		super(seed);
	}
	
	public void readInActivityTypes() throws IOException {
		activityTypes.clear();
		BufferedReader initActivities = null;
		
		initActivities = new BufferedReader(new FileReader ("./data/initialActivityInput.csv"));
		initActivities.readLine(); //Read in the header line of the file
		
		String line = null;
		while ((line = initActivities.readLine()) != null)
		{
			ActivityType a = ActivityType.parseActivityType(line);
			activityTypes.add(a);
		}	
	}

	public void initStudents() {
		students.clear();
		BufferedReader initInterests = null;
		
		/*
		 * Read in initial interests from data file, initialInterests.csv
		 */
		try {
			initInterests = new BufferedReader(new FileReader("./data/initialStudentInput.csv"));
			initInterests.readLine(); //Read in the header line of the file.
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		for (int i = 0; i < numStudents; i++) {
			String line = null;
			try {
				line = initInterests.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				System.err.format("Error: input file only contains %d entries but numStudents is set to %d.\n", i, numStudents);
				break;
			}
			
			Student s = Student.parseStudent(this, line);
			s.parent = new Adult(TopicVector.createRandom(random), TopicVector.createRandom(random));
			students.add(s);
		}
		// Close the buffered reader
		try {
			initInterests.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void initScienceClasses() {
		// init classes
		scienceClasses.clear();
		int numClasses = (int)Math.ceil(numStudents / (double)classSize);
		int studentIndex = 0;
		
		for (int i = 0; i < numClasses; i++) {
			// create activity for science class
			Activity scienceClass = new Activity(TopicVector.createRandom(random));
			
			// add students
			for (int j = 0; (j < classSize) && (studentIndex < students.size()); j++)
				scienceClass.participants.add(students.get(studentIndex++));
			
			// add teacher
			scienceClass.leaders.add(new Adult(TopicVector.createRandom(random), TopicVector.createRandom(random)));

			scienceClass.isSchoolRelated = true;
			scienceClass.isVoluntary = false;			
			scienceClass.isParentMediated = false;
			
			scienceClasses.add(scienceClass);

			System.out.format("Science class: %s\n", scienceClass.content);
			System.out.format("Teacher %s\n", scienceClass.leaders.get(0));
		}
	}
	
	/**
	 * Create a small-world network representing friendships between players.
	 * Based on pseudocode from Prettejohn, Berryman, and McDonnell (2011)
	 */
	public void initSmallWorldNetwork() {
		// First, create a ring network
		int n = students.size();
		for (int i = 0; i < n; i++) {
			Student p1 = students.get(i);
			for (int j = i+1; j <= i+numFriendsPerStudent/2; j++) {
				Student p2 = students.get(j % n);
				p1.friends.add(p2);
				p2.friends.add(p1);
			}
		}
		
		// Second, rewire edges randomly with probability smallWorldRewireProbability
		for (int i = 0; i < n; i++) {
			Student p1 = students.get(i);
			for (int j = i+1; j <= i+numFriendsPerStudent/2; j++) {
				if (random.nextDouble() >= smallWorldRewireProbability)
					continue;
				Student p2 = students.get(j % n);
				p1.friends.remove(p2);
				p2.friends.remove(p1);
				
				// pick a random node that isn't i, or adjacent to i
				Student p3;
				do {
					p3 = students.get(random.nextInt(n));
				} 
				while ((p3 == p1) || (p3.friends.contains(p1)));
				
				p1.friends.add(p3);
				p3.friends.add(p1);
			}
		}
		

		network = new UndirectedSparseGraph<Student, SimpleEdge>();
		for (Student p : students)
			network.addVertex(p);
		
		for (Student p : students) {
			Student p1 = (Student)p;
			for (Student p2 : p1.friends) {
				if (!network.isNeighbor(p1, p2))
					network.addEdge(new SimpleEdge(""), p1, p2);					
			}
		}
	}

	public void initDataLogging() {
		dataWatchers.clear();
		
		averageInterestWatcher = new DoubleArrayWatcher() {
			// anonymous constructor
			{
				data = new double[numStudents];
			}

			@Override
			protected void updateDataPoint() {
				for (int i = 0; i < students.size(); i++)
					data[i] = students.get(i).getAverageInterest();				
			}
			
			@Override
			public String getCSVHeader() {
				return null;
			}
		};
		dataWatchers.add(averageInterestWatcher);

		for (int i = 0; i < NUM_TOPICS; i++) {
			final int topic = i;
			interestWatcher[i] = new DoubleArrayWatcher() {
				// anonymous constructor
				{
					data = new double[numStudents];
				}

				@Override
				protected void updateDataPoint() {
					for (int j = 0; j < students.size(); j++)
						data[j] = students.get(j).interest.topics[topic];				
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
                final long currentStep = schedule.getSteps();
                double total = 0;
                for (Student s : students)
                	total += s.interest.topics[0];
                dataPoint = new Pair<Long, Double>(currentStep, (total / students.size()));
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
                final long currentStep = schedule.getSteps();
                double total = 0;
                for (Student s : students)
                	total += s.interest.topics[1];
                dataPoint = new Pair<Long, Double>(currentStep, (total / students.size()));
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
                final long currentStep = schedule.getSteps();
                double total = 0;
                for (Student s : students)
                	total += s.interest.topics[2];
                dataPoint = new Pair<Long, Double>(currentStep, (total / students.size()));
            }

            @Override
            public String getCSVHeader() {
                return "Step, " + interest3Series.getDescription();
            }
        });
		
	}
	
	/**
	 * Initialize the activity schedule based on survey data. Students were asked
	 * how often, from 1-5, they did certain activites outside of school. For now,
	 * we are interpretting these values to mean:
	 * 1: never
	 * 2: once every 30 days
	 * 3: once every 10 days
	 * 4: once every 3 days
	 * 5: every day
	 * 
	 * NOTE: This function employs static, fixed-interval scheduling
	 * 
	 */
	public void initStaticSchedule() {
		int[] intervals = new int[] { 0, 0, 30, 10, 3, 1 };	// it's one-based so stuff an extra zero in there

		activities.clear();
		for (Student s : students) {
			for (int i = 0; i < NUM_ACTIVITY_TYPES; i++) {
				if (s.stuffIDo[i] == 1)	// never
					continue;
				int daysBetween = intervals[s.stuffIDo[i]];

				Activity a = Activity.createFromType(this, activityTypes.get(i));
				a.participants.add(s);
				a.daysBetween = daysBetween;
				if (a.isParentMediated)
					a.leaders.add(s.parent);
				activities.add(a);					
			}
		}
		
		for (Activity a : activities)
			schedule.scheduleRepeating(a.daysBetween, a);
		
	}
	
	/**
	 * Schedule one day's worth of activities.
	 */
	public void doActivitiesForDay() {
		activities.clear();
		boolean schoolDay = isSchoolDay();
		boolean weekend = isWeekend();
		boolean summer = isSummer();
		
		/**
		 * The responses to the question of "how often do you do the following...?" are mapped as follows:
		 * All the time:			1.0
		 * Often:					0.75
		 * Every once in a while:	0.5			
		 * Very rarely:				0.25
		 * Never:					0.0
		 * 
		 * These represent the probabilities that the student will do the activity at any given opportunity.
		 */
		double[] probOfParticipating = new double[] { 0, 0, 0.25, 0.5, 0.75, 1.0 };	// it's one-based so stuff an extra zero in there
		
		for (Student s : students) {
			s.activities.clear();
			for (int i = 0; i < NUM_ACTIVITY_TYPES; i++) {
				if (s.stuffIDo[i] == 1)	// never
					continue;

				ActivityType type = activityTypes.get(i);
				//TODO implement repeating activities
//				if (type.isRepeating)	
//					continue;
				
				// is this a valid day for this activity?
				if ((schoolDay && !type.onSchoolDay) ||
					(weekend && !type.onWeekendDay) ||
					(summer && !type.onSummer))
					continue;

				// stochastically decide whether to do this activity today or not
				if (random.nextDouble() < probOfParticipating[s.stuffIDo[i]])
					createOrJoinActivity(s, type);
				
				// don't overschedule
				if (s.activities.size() >= maxActivitiesPerDay)
					break;
			}
		}
		
		// Now do them
		for (Activity a : activities)
			a.step(this);
	}
	
	/**
	 * Find an activity to join. If there are no existing activities that match 
	 * the given type, return null. If there's a matching activity in which a 
	 * friend is participating, join that one. Otherwise, pick a random matching
	 * activity.
	 * @param s Student that wants to join an activity.
	 * @param type Type of the activity.
	 * @return The matching activity or null if none exists
	 */
	public Activity findActivityToJoin(Student s, ActivityType type) {
		ArrayList<Activity> matches = new ArrayList<Activity>();
		for (Activity a : activities)
			if ((a.type == type) && (a.participants.size() < type.maxParticipants))
				matches.add(a);
				
		if (matches.size() == 0)
			return null;
				
		// check for friends
		for (Activity a : matches)
			for (Student p : a.participants)
				if (s.friends.contains(p))
					return a;
					
		// if this is a friends-only activity and none of the matches contains a friend, don't join
		if (type.withFriendsOnly)
			return null;
		
		// randomly pick one of the matching open activities to join
		return matches.get(random.nextInt(matches.size()));
	}

	/**
	 * If a matching activity already exists, join it. Otherwise, create a new one.
	 * @param s Student that will be participating in the activity.
	 * @param type Type of the activity.
	 */
	public void createOrJoinActivity(Student s, ActivityType type) {
		Activity a = findActivityToJoin(s, type);
		
		if (a == null) {	
			a = Activity.createFromType(this, type);
			
			// if this activity involves a parent, add one
			if (type.numParents > 0)
				a.leaders.add(s.parent);
			
			// add leaders
			while (a.leaders.size() < type.numLeaders)
				a.leaders.add(new Adult(TopicVector.createRandom(random), TopicVector.createRandom(random)));
			
			activities.add(a);			
		}
		
		a.participants.add(s);
		s.activities.add(a);
	}
	
	public void printDayInfo() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.format("Step: %d, Date: %s, %d, %d, %d, %s, %s, %s\n", schedule.getSteps(), df.format(date.getTime()), 
        	date.get(Calendar.DAY_OF_WEEK), date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.DAY_OF_YEAR),
        	isSchoolDay(), isWeekend(), isSummer());
	}
	
	@SuppressWarnings("serial")
	public void start() {
		super.start();
		date = new GregorianCalendar(2012, 8, 4);	// Sept 4th. Month is zero-based for some strange reason
		
		// Read in the characteristics of each activity
		try {
			readInActivityTypes();
			if (activityTypes.size() != NUM_ACTIVITY_TYPES)
				System.err.format("Error: %d activity types read from file, should be %d.\n", activityTypes.size(), NUM_ACTIVITY_TYPES);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Problem Reading in Activities");
		}

		initStudents();
		initScienceClasses();
		initSmallWorldNetwork();
//		initStaticSchedule();		
		initDataLogging();

		//averageInterestScreenWriter = new ScreenDataWriter(averageInterestWatcher);

		
//		for (Activity a : scienceClasses)
//			schedule.scheduleRepeating(a);
		
		schedule.scheduleRepeating(new Steppable() {
			@Override
			public void step(SimState state) {
				printDayInfo();
				doActivitiesForDay();
				decayInterests();
						
				for (DataWatcher<?> dw : dataWatchers)
					dw.update();
				
				date.add(Calendar.DATE, 1);
				
				if (state.schedule.getSteps() > 1000)
					state.finish();
			}
		});
	}
	
	/**
	 * Decay all the student's interest levels.
	 */
	public void decayInterests() {
		for (Student s : students)
			s.interest.scale(interestDecayRate);
	}
	
	/**
	 * Let's assume day 0 is a Monday
	 * @param day
	 * @return
	 */
	private boolean isSchoolDay() {
		//TODO make this more comprehensive, e.g. exclude breaks
		return !isWeekend() && !isSummer();
	}
	
	private boolean isWeekend() {
		int day = date.get(Calendar.DAY_OF_WEEK);
		if ((day == Calendar.SATURDAY) || (day == Calendar.SUNDAY))
			return true;
		return false;
	}
	
	private boolean isSummer() {
		int day = date.get(Calendar.DAY_OF_YEAR);
		if ((day > 158) && (day < 247))
			return true;
		
		return false;		
	}
	

	public static void main(String[] args) {
		doLoop(StemStudents.class, args);
		System.exit(0);
	}

}
