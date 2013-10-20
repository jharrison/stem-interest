package stem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

import masoncsc.util.MTFUtilities;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import sim.engine.MakesSimState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Interval;
import stem.activities.Activity;
import stem.activities.ActivityType;
import stem.activities.RepeatingActivity;
import stem.network.*;
import stem.rules.Rule;
import stem.rules.RuleSet;

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
	
	Parameters params;

	public static final int NUM_ACTIVITY_TYPES = 16;
	public static final int NUM_TOPICS = 3;	
	
	public GregorianCalendar date;
	
	ArrayList<Student> students = new ArrayList<Student>();
	ArrayList<ActivityType> activityTypes = new ArrayList<ActivityType>();

//	public ArrayList<Activity> activities = new ArrayList<Activity>();
	public ArrayList<RepeatingActivity> repeatingActivities = new ArrayList<RepeatingActivity>();
	/** Indices of the activities, allows for shuffling to randomize the order. */
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	
	public RuleSet ruleSet = new RuleSet();
	
	/** This is the topic to which coordinateable activities will be coordinated.
	 * @see coordinationLevel */
	public TopicVector coordinatedTopic = new TopicVector();

	public UndirectedSparseGraph<Student, SimpleEdge> network = new UndirectedSparseGraph<Student, SimpleEdge>();

	DataLogger dataLogger;
	public String outputFilename = "";
	
    String[] activityNames = new String[] { "Library", "Museum", "Scouts", "NationalParks", "Afterschool", 
    		"Talk", "SummerCamp", "Hike", "Garden", "Experiments", "Read", "Internet", "Computer", "TV", "Build", "Class" };
    public String[] getActivityNames() { return activityNames; }
	
	
	// Start getters/setters here =======================================================
	
    // Network properties are handled by the NetworkProperties class inside StemStudentsWithUI 
    /** Number of friends each student is given during initialization of the friend network */
    public int numFriendsPerStudent = 3;
    
    /** Probability of rewiring links during the network generation algorithm. */
	public double smallWorldRewireProbability = 0.5;	
	
	/** Probability of rewiring links between boys and girls in the network. */
	public double interGenderRewireProbability = 0.00;
	
	/** Probability of making a new friend when participating in an activity. */
	public double makeFriendProbability = 0.01;

	/** Probability of closing a triad, i.e. become friends with a friend of a friend. */
	public double closeTriadProbability = 0.05;	
	
	public int numStudents = 127;  //# from survey that have valid values
	public int getNumYouth() { return numStudents; }
	public void setNumYouth(int val) { numStudents = val; }

	public int classSize = 17; //Approx. # from data.  Adjusted slightly to get same number in each class.
	public int getClassSize() { return classSize; }
	public void setClassSize(int val) { classSize = val; }

	public int maxActivitiesPerDay = 3;	
	public int getMaxActivitiesPerDay() { return maxActivitiesPerDay; }
	public void setMaxActivitiesPerDay(int val) { maxActivitiesPerDay = val; }
	
	public double interestThreshold = 0.75;
	public double getInterestThreshold() { return interestThreshold; }
	public void setInterestThreshold(double val) { interestThreshold = val; }
	public Object domInterestThreshold() { return new Interval(0.0, 1.0); }
	
	public double interestThresholdNoise = 0.0;
	public double getInterestThresholdNoise() { return interestThresholdNoise; }
	public void setInterestThresholdNoise(double val) { interestThresholdNoise = val; }
	public Object domInterestThresholdNoise() { return new Interval(0.0, 1.0); }

	public double leaderExpertise = 0.5;
	public double leaderExpertiseNoise = 0.05;
	
	public double leaderPassion = 0.5;
	public double leaderPassionNoise = 0.05;
	
	public double expertiseThreshold = 0.5;
	public double expertiseThresholdNoise = 0.05;

	public double passionThreshold = 0.5;
	public double passionThresholdNoise = 0.05;
	
	public double interestChangeRate = 0.01;
	public double getInterestChangeRate() { return interestChangeRate; }
	public void setInterestChangeRate(double val) { interestChangeRate = val; }
	public Object domInterestChangeRate() { return new Interval(0.0, 1.0); }

	public double interestDecayRate = 0.993;
	public double getInterestDecayExponent() { return interestDecayRate; }
	public void setInterestDecayExponent(double val) { interestDecayRate = val; }
	public Object domInterestDecayExponent() { return new Interval(0.0, 1.0); }
	
	public boolean randomizeInterests = false;
//	public boolean getRandomizeInterests() { return randomizeInterests; }
//	public void setRandomizeInterests(boolean val) { randomizeInterests = val; }
	
	public boolean randomizeStuffIDo = false;
//	public boolean getRandomizeStuffIDo() { return randomizeStuffIDo; }
//	public void setRandomizeStuffIDo(boolean val) { randomizeStuffIDo = val; }

	/** How much to change the participation rate in an activity if interest has
	 * been increased or decreased. */
	public double participationChangeRate = 0.05;
	public double getParticipationChangeRate() { return participationChangeRate; }
	public void setParticipationChangeRate(double val) { participationChangeRate = val; }
	public Object domParticipationChangeRate() { return new Interval(0.0,0.5); }


	/** Extent to which activities are coordinated with current school topic. */
	public double coordinationLevel = 0.0;
	public double getCoordinationLevel() { return coordinationLevel; }
	public void setCoordinationLevel(double val) { coordinationLevel = val; }
	public Object domCoordinationLevel() { return new Interval(0.0,1.0); }
	
//	public int[] getFriendCounts() {
//		int [] counts = new int[numStudents];
//		int index = 0;
//		for (Student s : students)
//			counts[index++] = s.friends.size();
//		
//		return counts;
//	}
	
	

	public StemStudents(long seed, String[] args) {
		super(seed);
		params = new Parameters(this, args);
		
		dataLogger = new DataLogger(this);
		
		for (int i = 0; i < NUM_ACTIVITY_TYPES; i++)
			indices.add(i);
		
		readActivityTypes();
//		testActivityTypes();
	}
	
	private void testActivityTypes() {

		for (ActivityType at : activityTypes) {
			System.out.format("%-18s", at.name + ":");
			for (int i = 1; i <= 5; i++) {
				System.out.format("%.3f, ", at.mapLikertToParticpationRate(i));
			}
			System.out.println();
		}
	}
	
	public void readActivityTypes() {
		try {
			activityTypes.clear();
			BufferedReader initActivities = null;
			
			initActivities = new BufferedReader(new FileReader ("./data/activityTypes.csv"));
			initActivities.readLine(); //Read in the header line of the file
			
			String line = null;
			while ((line = initActivities.readLine()) != null)
			{
				ActivityType a = ActivityType.parseActivityType(line);
				activityTypes.add(a);
			}	
			
			if (activityTypes.size() != NUM_ACTIVITY_TYPES)
				System.err.format("Error: %d activity types read from file, should be %d.\n", activityTypes.size(), NUM_ACTIVITY_TYPES);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Problem Reading in Activities");
		}
	}
	
	private Adult createRandomAdult() {
//		return new Adult(TopicVector.createRandom(random), TopicVector.createRandom(random));
		return new Adult(TopicVector.createRandom(random, leaderExpertise, leaderExpertiseNoise), 
				TopicVector.createRandom(random, leaderPassion, leaderPassionNoise));
	}
	
	/**
	 * Checks the model parameters and possibly randomize the given student's
	 * interests and stuffIDo.
	 * @param s
	 */
	public void possiblyRandomize(Student s) {
		if (randomizeInterests)
			s.interest = TopicVector.createRandom(random);
		
		if (randomizeStuffIDo)
			for (int i = 0; i < 15; i++)
				s.participationRates[i] = random.nextDouble();
	}

	public void initStudents() {
		students.clear();
		BufferedReader initInterests = null;
		
		/*
		 * Read in initial student info and interests from data file
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
			/*
			 * TODO
			 * The model doesn't seem to handle trying to instantiate with more 
			 * youth than in the input file well.  The histograms have way too 
			 * many records in the 0 bin.
			 */
			if (line == null) {
				System.err.format("Error: input file only contains %d entries but numStudents is set to %d.\n", i, numStudents);
				break;
			}
			
			Student s = Student.parseStudent(this, line);
			possiblyRandomize(s);
			s.parent = createRandomAdult();
			students.add(s);
		}
		// Close the buffered reader
		try {
			initInterests.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void initStudentNetwork() {
		
		// Sort students by elementary school teacher to promote links between students who are close geographically
		Collections.sort(students, new Comparator<Student>() {
			public int compare(Student s1, Student s2) {
				return s1.teacher.compareToIgnoreCase(s2.teacher);
			}
		});
		
		ArrayList<Student> females = new ArrayList<Student>();
		ArrayList<Student> males = new ArrayList<Student>();
		
		for (Student s : students)
			if (s.isFemale)
				females.add(s);
			else
				males.add(s);

		NetworkGenerator.initSmallWorldNetwork(females, numFriendsPerStudent, smallWorldRewireProbability, random);
		NetworkGenerator.initSmallWorldNetwork(males, numFriendsPerStudent, smallWorldRewireProbability, random);
		
		NetworkGenerator.rewireNetworkLinks(students, numFriendsPerStudent, interGenderRewireProbability, random);
		
		// Create and fill the JUNG network to use for display
		network = new UndirectedSparseGraph<Student, SimpleEdge>();
		for (Student p : students)
			network.addVertex(p);
		
		for (Student p1 : students) {
			for (Student p2 : p1.friends) {
				if (!network.isNeighbor(p1, p2))
					network.addEdge(new SimpleEdge(""), p1, p2);					
			}
		}
		
	}
	
	public Collection<Student> getFriends(Student s) {
		return network.getNeighbors(s);
	}
	
	public boolean addFriends(Student a, Student b) {
		// can't become friends if they already are friends
		if (network.isNeighbor(a, b))
			return false;
		
		a.friends.add(b);
		b.friends.add(a);
		network.addEdge(new SimpleEdge(""), a, b);
		
		return true;
	}
	
	public boolean removeFriends(Student a, Student b) {
		// can't remove them if they aren't already friends
		SimpleEdge e = findEdge(a, b);
		if (e == null)
			return false;
		
		a.friends.remove(b);
		b.friends.remove(a);
		network.removeEdge(e);
		
		return true;
	}
	
	private SimpleEdge findEdge(Student a, Student b) {
		if (!network.isNeighbor(a, b))
			return null;
		
		for (SimpleEdge e : network.getEdges()) {
			Collection<Student> nodes = network.getIncidentVertices(e);
			if (nodes.contains(a) && nodes.contains(b))
				return e;
		}
		return null;
	}
	
	public boolean willDoToday(Student s, ActivityType type) {
		// if the student never does this activity, don't bother doing more expensive checks
		if (s.participationRates[type.id] == 0)
			return false;
		
		boolean schoolDay = isSchoolDay(date);
		boolean weekend = isWeekend(date);
		boolean summer = isSummer(date);

		// is this a valid day for this activity?
		if ((schoolDay && !type.onSchoolDay) ||
			(weekend && !type.onWeekendDay) ||
			(summer && !type.onSummer))
			return false;
		
		// have we done this activity too recently?
		if (s.daysSinceActivity[type.id] < type.daysBetween)
			return false;

		// stochastically decide whether to do this activity today or not
		if (random.nextDouble() < s.participationRates[type.id])
			return true;
		
		return false;
	}

	/**
	 * Is today a day when this repeating activity will occur?
	 * @param activity
	 * @return
	 */
	private boolean willOccurToday(RepeatingActivity activity) {

		boolean schoolDay = isSchoolDay(date);
		boolean weekend = isWeekend(date);
		boolean summer = isSummer(date);

		// is this a valid day for this activity?
		if ((schoolDay && !activity.type.onSchoolDay) ||
			(weekend && !activity.type.onWeekendDay) ||
			(summer && !activity.type.onSummer))
			return false;
		
		// have this activity occured too recently?
		if ((activity.lastOccurence != -1) && 
				((schedule.getSteps() - activity.lastOccurence) < activity.type.daysBetween))
			return false;
		
		if (activity.timesRepeated >= activity.type.numRepeats)
			return false;
		
		return true;
	}
	
	
	/**
	 * Match participants to activities such that friends are kept together
	 * as much as possible.
	 */
	public void matchParticipantsToActivities(ArrayList<Student> participants, ArrayList<Activity> activities) {
		ArrayList<Activity> nonFullActivities = new ArrayList<Activity>(activities);
		for (Student s : participants) {
			Activity a = nonFullActivities.get(random.nextInt(nonFullActivities.size()));
			a.addParticipant(s);
			if (a.isFull())
				nonFullActivities.remove(a);
		}
	}
	
	
	/** Create a new set of repeating activities and assign participants. */
	public void organizeRepeatingActivities() {

		repeatingActivities.clear();
		ArrayList<Student> allParticipants = new ArrayList<Student>();
		
		// loop through activities in random order
		MTFUtilities.shuffle(indices, random);
		for (int i : indices) {
			ActivityType type = activityTypes.get(i);
			if (!type.isRepeating)
				continue;
			
			// make a list of everyone who'll be doing this activity
			allParticipants.clear();
			for (Student s : students)
				if (s.participationRates[i] > 0)
					allParticipants.add(s);
			
			// assign participants to activities
			int numActivities = (int)Math.ceil(allParticipants.size() / (double)type.maxParticipants);
			ArrayList<Activity> activities = new ArrayList<Activity>();
			for (int j = 0; j < numActivities; j++) {
				RepeatingActivity a = RepeatingActivity.createFromType(this, type);
				// assign leaders to activiy
				while (a.leaders.size() < type.numLeaders)
					a.leaders.add(createRandomAdult());
				activities.add(a);
			}
						
			matchParticipantsToActivities(allParticipants, activities);
			
			// add this repeating activity to the list
			for (Activity a : activities)
				repeatingActivities.add((RepeatingActivity)a);
		}
	}
	
	/**
	 * Schedule one day's worth of activities.
	 */
	public void doActivitiesForDay() {
		ArrayList<Activity> activities = new ArrayList<Activity>();

		for (Student s : students)
			s.activities.clear();
		
		MTFUtilities.shuffle(repeatingActivities, random);
		Collections.sort(repeatingActivities, new Comparator<RepeatingActivity>() {
			public int compare(RepeatingActivity arg0, RepeatingActivity arg1) {
				return arg0.type.priority - arg1.type.priority;
			}
		});
		for (RepeatingActivity ra : repeatingActivities) {
			if (willOccurToday(ra))
				ra.step(this);
		}
		
		// loop through students in random order
		MTFUtilities.shuffle(students, random);
		for (Student s : students) {
			s.incrementCounters();
			// loop through activities in random order
			MTFUtilities.shuffle(indices, random);
			for (int i : indices) {
				// don't overschedule
				if (s.activities.size() >= maxActivitiesPerDay)
					break;

				ActivityType type = activityTypes.get(i);
				
				if (type.isRepeating)
					continue;	// repeating activities are handled above
				
				if (willDoToday(s, type))
					createOrJoinActivity(s, activities, type);				
			}
		}
		
		// Now do them
		for (Activity a : activities) {
			a.step(this);
		}
	}
	
	/**
	 * Find an activity to join. If there are no existing activities that match 
	 * the given type, return null. If there's a matching activity in which a 
	 * friend is participating, join that one. Otherwise, pick a random matching
	 * activity.
	 * @param s Student that wants to join an activity.
	 * @param activities List of available activities.
	 * @param type Type of the activity to join.
	 * @return The matching activity or null if none exists
	 */
	public Activity findActivityToJoin(Student s, ArrayList<Activity> activities, ActivityType type) {
		ArrayList<Activity> matches = new ArrayList<Activity>();
		for (Activity a : activities)
			if ((a.type == type) && !a.isFull())
				matches.add(a);
				
		if (matches.size() == 0)
			return null;
				
		// check for friends
		for (Activity a : matches)
			if (a.contains(s.friends))
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
	public void createOrJoinActivity(Student s, ArrayList<Activity> activities, ActivityType type) {
		Activity a = findActivityToJoin(s, activities, type);
		
		if (a == null) {	
			a = Activity.createFromType(this, type);
			
			// if this activity involves a parent, add one
			if (type.numParents > 0)
				a.leaders.add(s.parent);
			
			// add leaders
			while (a.leaders.size() < type.numLeaders)
				a.leaders.add(createRandomAdult());
			
			activities.add(a);			
		}
		
		a.addParticipant(s);
		s.activities.add(a);
	}
	
	/** Event that is triggered when an activity is done. */
	public void studentParticipated(Student s, Activity a) {
		dataLogger.studentParticipated(s, a);
	}
	
	/** Event that is triggered when a student's interest levels are changed. */
	public void studentInterestChanged(Student s, Activity a, int topicIndex, double delta, Rule r) {
		dataLogger.studentInterestChanged(s, a, topicIndex, delta, r);
	}
	
	
	public void printDayInfo() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.format("Step: %d, Date: %s, %d, %d, %d, %s, %s, %s\n", schedule.getSteps(), df.format(date.getTime()), 
        	date.get(Calendar.DAY_OF_WEEK), date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.DAY_OF_YEAR),
        	isSchoolDay(date), isWeekend(date), isSummer(date));
	}
	
	@SuppressWarnings("serial")
	public void start() {
		super.start();
		date = new GregorianCalendar(2012, 8, 4);	// Sept 4th. Month is zero-based for some strange reason
		
		// Read in the characteristics of each activity
		readActivityTypes();

		initStudents();
		initStudentNetwork();
		dataLogger.init();
		dataLogger.start();

		final int ORGANIZE_ORDER = 0;
		final int ACTIVITIES_ORDER = 1;
		final int DATA_LOGGER_ORDER = 2;
		final int CALENDAR_ORDER = 3;
		
		// Once per year, organize the repeating activities
		schedule.scheduleRepeating(0.0, ORGANIZE_ORDER, new Steppable() {
			@Override
			public void step(SimState arg0) {
				organizeRepeatingActivities();
			}
		}, 365);
				
		schedule.scheduleRepeating(new Steppable() {
			@Override
			public void step(SimState state) {
//				printDayInfo();
//				coordinatedTopic = scienceClasses.get(0).content;
//				System.out.format("Step: %d, CoordinatedTopic: %s\n", schedule.getSteps(), coordinatedTopic);
				doActivitiesForDay();
				decayInterests();
				
//				int totalFriendCount = 0;
//				for (Student s : students)
//					totalFriendCount += s.friends.size();
//				System.out.format("Network link count: %d, totalFriendCount: %d\n", network.getEdgeCount(), totalFriendCount);
			}
		}, ACTIVITIES_ORDER, 1.0);
		

		
		schedule.scheduleRepeating(dataLogger, DATA_LOGGER_ORDER, 1.0);
		
		schedule.scheduleRepeating(new Steppable() {
			public void step(SimState state) {
				date.add(Calendar.DATE, 1);
				if (state.schedule.getSteps() > 1461)	// 4 years (including one leap day)
					state.finish();
			}
		}, CALENDAR_ORDER, 1.0);
	}
	
	/**
	 * Decay all the student's interest levels.
	 */
	public void decayInterests() {
		for (Student s : students)
			s.interest.scale(interestDecayRate);
	}
	
	/** Is the given day a school day? */
	private boolean isSchoolDay(Calendar date) {
		//TODO make this more comprehensive, e.g. exclude breaks
		return !isWeekend(date) && !isSummer(date);
	}

	/** Is the given day a weekend? */
	private boolean isWeekend(Calendar date) {
		int day = date.get(Calendar.DAY_OF_WEEK);
		if ((day == Calendar.SATURDAY) || (day == Calendar.SUNDAY))
			return true;
		return false;
	}

	/** Is the given day in the summer break? */
	private boolean isSummer(Calendar date) {
		int day = date.get(Calendar.DAY_OF_YEAR);
		if ((day > 158) && (day < 247))
			return true;
		
		return false;		
	}
	

	@Override
	public void finish() {
		super.finish();
		dataLogger.close();
	}
	
	public static void main(String[] args) {
        doLoop(new MakesSimState()
        {
            @Override
            public SimState newInstance(long seed, String[] args)
            {
                return new StemStudents(seed, args);
            }

            @Override
            public Class simulationClass()
            {
                return StemStudents.class;
            }
        }, args);
		System.exit(0);
	}

}
