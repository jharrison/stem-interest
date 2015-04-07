package stem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;

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

	public static final int NUM_ACTIVITY_TYPES = 21;
	public static final int NUM_TOPICS = TopicVector.VECTOR_SIZE;	
	public static String[] TOPIC_NAMES = new String[] { "Earth/Space Science", "Life Science", "Tech/Eng.", "Mathematics" };
	
	public GregorianCalendar date;

	ArrayList<Student> students = new ArrayList<Student>();
	ArrayList<Student> students7th = new ArrayList<Student>();
	ArrayList<ActivityType> activityTypes = new ArrayList<ActivityType>();
	ActivityType internet = null;	// mentoring will focus on this activity type

//	public ArrayList<Activity> activities = new ArrayList<Activity>();
	public ArrayList<RepeatingActivity> repeatingActivities = new ArrayList<RepeatingActivity>();
	/** Indices of the activities, allows for shuffling to randomize the order. */
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	
	public RuleSet ruleSet;
	
	/** This is the topic to which coordinateable activities will be coordinated.
	 * @see coordinationLevel */
	public TopicVector coordinatedTopic = new TopicVector();

	public UndirectedSparseGraph<Student, SimpleEdge> network = new UndirectedSparseGraph<Student, SimpleEdge>();

	public DataLogger dataLogger;
	public String outputFilename = "";
	public String youthLogFilename = "youthLog.csv";
	
//    String[] activityNames = new String[] { "Library", "Museum", "Scouts", "NationalParks", "Afterschool", 
//    		"Talk", "SummerCamp", "Hike", "Garden", "Experiments", "Read", "Internet", "Computer", "TV", "Build", "Class" };

    public String[] activityNames = new String[1];
//    public String[] getActivityNames() { return activityNames; }
	
	
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
	
	static public int numStudents = 140;  //# from survey that have valid values
	public int getNumYouth() { return numStudents; }
	public void setNumYouth(int val) { numStudents = val; }

	public int classSize = 18; //Approx. # from data.  Adjusted slightly to get same number in each class.
	public int getClassSize() { return classSize; }
	public void setClassSize(int val) { classSize = val; }

	public int maxActivitiesPerDay = 3;	
	public int getMaxActivitiesPerDay() { return maxActivitiesPerDay; }
	public void setMaxActivitiesPerDay(int val) { maxActivitiesPerDay = val; }

	protected int runDuration = 365;
	public int getRunDuration() { return runDuration; }
	public void setRunDuration(int val) { runDuration = val; }
	
	public double interestThreshold = 0.5;
	public double getInterestThreshold() { return interestThreshold; }
	public void setInterestThreshold(double val) { interestThreshold = val; }
	public Object domInterestThreshold() { return new Interval(0.0, 1.0); }
	
	public double interestThresholdNoise = 0.0;
	public double getInterestThresholdNoise() { return interestThresholdNoise; }
	public void setInterestThresholdNoise(double val) { interestThresholdNoise = val; }
	public Object domInterestThresholdNoise() { return new Interval(0.0, 1.0); }

	public double leaderExpertise = 0.5;
	public double leaderExpertiseNoise = 0.0;
	
	public double leaderPassion = 0.5;
	public double leaderPassionNoise = 0.0;
	
	public double expertiseThreshold = 0.5;
	public double expertiseThresholdNoise = 0.0;

	public double passionThreshold = 0.5;
	public double passionThresholdNoise = 0.0;
	
	public double interestChangeRate = 0.01;
	public double getInterestChangeRate() { return interestChangeRate; }
	public void setInterestChangeRate(double val) { interestChangeRate = val; }
	public Object domInterestChangeRate() { return new Interval(0.0, 1.0); }

	public double interestDecayRate = 1.0;	// this is currently unused, so the accessors are hidden
//	public double getInterestDecayExponent() { return interestDecayRate; }
//	public void setInterestDecayExponent(double val) { interestDecayRate = val; }
//	public Object domInterestDecayExponent() { return new Interval(0.0, 1.0); }
	
	public boolean randomizeInterests = false;	// this is currently unused, so the accessors are hidden
//	public boolean getRandomizeInterests() { return randomizeInterests; }
//	public void setRandomizeInterests(boolean val) { randomizeInterests = val; }
	
	public boolean randomizeStuffIDo = false;	// this is currently unused, so the accessors are hidden
//	public boolean getRandomizeStuffIDo() { return randomizeStuffIDo; }
//	public void setRandomizeStuffIDo(boolean val) { randomizeStuffIDo = val; }

	/** How much to change the participation rate in an activity if interest has
	 * been increased or decreased. */
	public double participationChangeRate = 0.001;
	public double getParticipationChangeRate() { return participationChangeRate; }
	public void setParticipationChangeRate(double val) { participationChangeRate = val; }
	public Object domParticipationChangeRate() { return new Interval(0.0,0.5); }

	/** Multiplier that is applied to the participation rates and allows activity levels to be adjusted broadly. */
	public double participationMultiplier = 1.0;
	public double getParticipationMultiplier() { return participationMultiplier; }
	public void setParticipationMultiplier(double val) { participationMultiplier = val; }
	public Object domParticipationMultiplier() { return new Interval(0.0, 1.0); }


	/** Extent to which activities are coordinated with current school topic. */
	public double coordinationLevel = 0.0;
//	public double getCoordinationLevel() { return coordinationLevel; }
//	public void setCoordinationLevel(double val) { coordinationLevel = val; }
//	public Object domCoordinationLevel() { return new Interval(0.0,1.0); }
	
	public int activityMatchingMethod = 0;
	public int getActivityMatchingMethod() { return activityMatchingMethod;	}
	public void setActivityMatchingMethod(int val) { activityMatchingMethod = val;	}
	public Object domActivityMatchingMethod() { return new String[] { "Random", "InOrder", "FriendChain" }; }
	
//	public int[] getFriendCounts() {
//		int [] counts = new int[numStudents];
//		int index = 0;
//		for (Student s : students)
//			counts[index++] = s.friends.size();
//		
//		return counts;
//	}
	
//	public String getActivitiesWithFriends() {
//		if ((dataLogger == null) || (dataLogger.activitiesDone == 0))
//			return "N/A";
//		return String.format("%.1f%%", 100 * dataLogger.activitiesDoneWithFriends / (double)dataLogger.activitiesDone);
//	}
	


//	double[] expectedActivitiesPerDay = new double[numStudents];
//	public double[] getExpectedActivitiesPerDay() {
//		Arrays.fill(expectedActivitiesPerDay, 0);
//		if ((students != null) && !students.isEmpty()) {
//			for (int i = 0; i < numStudents; i++) {
//				int activitiesPerYear = 0;
//				for (int j = 0; j < NUM_ACTIVITY_TYPES; j++) {
//					int opportunities = activityTypes.get(j).calcOpportunitiesPerYear();
////					activitiesPerYear += opportunities * students.get(i).participationRates[j] * 
////							(activityTypes.get(j).name.equals("Class") ? 1 : participationMultiplier);
//					activitiesPerYear += opportunities * students.get(i).participationRates[j];
//				}
//				expectedActivitiesPerDay[i] = activitiesPerYear / 365.0;
//			}
//		}
//		
//		return expectedActivitiesPerDay;
//	}

	/** Extent to which activities are coordinated with current school topic. */
	public double mentorProbability = 0.0;

	public double getMentorProbability() { return mentorProbability; }
	public void setMentorProbability(double val) { mentorProbability = val; }
	public Object domMentorProbability() { return new Interval(0.0,1.0); }
	
	
	public double friendRuleWeight = 1.0;
	public double choiceRuleV2Weight = 1.0;
	public double parentRuleWeight = 1.0;
	public double leaderRuleV2Weight = 0.9;

	public StemStudents(long seed, String[] args) {
		super(seed);
		params = new Parameters(this, args);
		ruleSet = new RuleSet(this);
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
			
			// init the activity names based on the info read from the file
			activityNames = new String[activityTypes.size()];
			for (int i = 0; i < activityTypes.size(); i++)
				activityNames[i] = activityTypes.get(i).name;
			
			internet = activityTypes.get(14);	// "Web sites"
			
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
	
	private ArrayList<Student> readStudentsFromFile(String filename) {

		ArrayList<Student> studentList = new ArrayList<Student>();
		
		BufferedReader fileReader = null;
		
		//Read in initial student info and interests from data file
		try {
			fileReader = new BufferedReader(new FileReader(filename));
			fileReader.readLine(); // Read in the header line of the file.
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// create a hash map to check for duplicate IDs
		HashMap<Integer, Student> studentsByID = new HashMap<Integer, Student>(); 
		
		for (int i = 0; i < numStudents; i++) {
			String line = null;
			try {
				line = fileReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (line == null) {
				System.err.format("Error: input file only contains %d entries but numStudents is set to %d.\n", i, numStudents);
				break;
			}
			
			Student s = Student.parseStudent(this, line);
			possiblyRandomize(s);
			s.parent = createRandomAdult();
			studentList.add(s);
			
			if (studentsByID.containsKey(s.id)) {
				System.err.format("Error: Duplicate student ID found (%d)", s.id);
				System.exit(-1);
			}
			else
				studentsByID.put(s.id, s);
		}
		// Close the buffered reader
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return studentList;
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
	 * Match participants to activities randomly.
	 */
	private void matchParticipantsToActivities_Random(ArrayList<Student> participants, ArrayList<Activity> activities) {
		ArrayList<Activity> nonFullActivities = new ArrayList<Activity>(activities);
		for (Student s : participants) {
			Activity a = nonFullActivities.get(random.nextInt(nonFullActivities.size()));
			a.addParticipant(s);
			if (a.isFull())
				nonFullActivities.remove(a);
		}
	}
	
	/**
	 * Match participants to activities by looping through participants in order.
	 * This only works because the artificial social network was created by starting
	 * with a ring, which means students adjacent in the list are more likely to be
	 * friends.
	 */
	private void matchParticipantsToActivities_InOrder(ArrayList<Student> participants, ArrayList<Activity> activities) {

		int pIndex = 0;
		for (Activity a : activities) {
			for ( ; pIndex < participants.size() && !a.isFull(); pIndex++)
				a.addParticipant(participants.get(pIndex));
			
			if (pIndex >= participants.size())
				break;
		}
	}
	
	
	/**
	 * Match participants to activities by adding a participant, then his/her friends,
	 * then their friends, etc. Uses a depth-first search.
	 */
	private void matchParticipantsToActivities_FriendChain(ArrayList<Student> participants, ArrayList<Activity> activities) {
		ArrayList<Student> participantsCopy = new ArrayList<Student>(participants);
		
		for (Activity a : activities) {
			while (!a.isFull() && !participantsCopy.isEmpty())
				addFriendChain(participantsCopy.get(0), a, participantsCopy);

			if (participantsCopy.isEmpty())
				break;
		}
	}
	
	/**
	 * Recursive function for adding chains of friends in a depth-first manner.
	 */
	private void addFriendChain(Student s, Activity a, ArrayList<Student> participants) {
		if (a.isFull())
			return;
		
		a.addParticipant(s);
		participants.remove(s);
		for (Student f : s.friends)
			if (participants.contains(f))
				addFriendChain(f, a, participants);
	}

	private void matchParticipantsToActivities(ArrayList<Student> participants, ArrayList<Activity> activities) {
		switch (activityMatchingMethod) {
		case 0:	matchParticipantsToActivities_Random(participants, activities);			break;
		case 1:	matchParticipantsToActivities_InOrder(participants, activities);		break;
		case 2: matchParticipantsToActivities_FriendChain(participants, activities);	break;
		}

		int total = 0;
		for (Activity a : activities)
			total += ((RepeatingActivity)a).potentialParticipants.size();
		if (total != participants.size())
			System.err.format("Error in matchParticipantsToActivities: %d participants != %d assigned.\n", total, participants.size());
	}
	
	/** Create a new set of repeating activities and assign participants. */
	private void organizeRepeatingActivities() {

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
			int remainingParticipants = allParticipants.size();
			ArrayList<Activity> activities = new ArrayList<Activity>();
			for (int j = 0; j < numActivities; j++) {
				RepeatingActivity a = RepeatingActivity.createFromType(this, type);
				
				// calculate the number of participants to make the groups as evenly sized as possible
				a.maxParticipants = (int)Math.ceil(remainingParticipants / (double)(numActivities-j));
				remainingParticipants -= a.maxParticipants;
				
				// assign leaders to activiy
				while (a.leaders.size() < type.numLeaders)
					a.leaders.add(createRandomAdult());
				activities.add(a);
			}
						
			matchParticipantsToActivities(allParticipants, activities);
			
			// add this repeating activity to the list
			for (Activity a : activities)
				repeatingActivities.add((RepeatingActivity)a);
			
//			System.out.format("%20s: ", type.name);
//			for (Activity a : activities)
//				System.out.format("%d ", ((RepeatingActivity)a).potentialParticipants.size());
//			System.out.println();
		}
	}
	
	private boolean receivesMentoringToday(Student s) {
		return random.nextBoolean(mentorProbability);			
	}
	
	/**
	 * Schedule one day's worth of activities.
	 */
	private void doActivitiesForDay() {
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
			// consider mentoring
			boolean isMentored = receivesMentoringToday(s);
			if (isMentored) {
				// create an internet activity tailored to this student
				Activity a = Activity.createFromType(this, internet, s.interest.createFocusedVector());
				a.addParticipant(s);
				s.activities.add(a);
				activities.add(a);				
			}
			// loop through activities in random order
			MTFUtilities.shuffle(indices, random);
			for (int i : indices) {
				// don't overschedule
				if (s.activities.size() >= maxActivitiesPerDay)
					break;

				ActivityType type = activityTypes.get(i);
				
				if (type.isRepeating)
					continue;	// repeating activities are handled above
				
				if (isMentored && (type == internet))
					continue;	// don't double-schedule internet
				
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
	private Activity findActivityToJoin(Student s, ArrayList<Activity> activities, ActivityType type) {
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
	private void createOrJoinActivity(Student s, ArrayList<Activity> activities, ActivityType type) {
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
	
	/** Event that is triggered when a student's interest levels are changed. */
	public void studentParticipationChanged(Student s, Activity a, double delta, Rule r) {
		dataLogger.studentParticipationChanged(s, a, delta, r);
	}
	
	
	private void printDayInfo() {
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

		students = readStudentsFromFile("./data/initialStudentInput.csv");
		students7th = readStudentsFromFile("./data/seventhGradeStudentInput.csv");
		
		compareStudents(students, "6th grader (before simulation)", students7th, "7th grader (from data)");
		
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
			@Override
			public void step(SimState state) {
				if (state.schedule.getSteps() % 365 == 0)
					dataLogger.writeYouthLog(youthLogFilename);
//				System.out.println(state.schedule.getSteps());
			}
		});
		
		schedule.scheduleRepeating(new Steppable() {
			public void step(SimState state) {
				date.add(Calendar.DATE, 1);
				if (state.schedule.getSteps() >= runDuration)	
					state.kill();
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
	
	private double average(double[] array) {
		double total = 0;
		int n;
		for (n = 0; n < array.length; n++)
			total += array[n];
		
		return (n == 0) ? 0 : (total / n);
	}

	private double median(double[] array) {
	
		Arrays.sort(array);
		int middle = array.length / 2;
		if (array.length % 2 == 1)
			return array[middle];
		else
			return (array[middle-1] + array[middle]) * 0.5;
	}
	
	
	private void printRunSummary() {
		System.out.println();
		
	//		for (int i = 0; i < NUM_ACTIVITY_TYPES; i++)
	//		System.out.format("%f, ", (dataLogger.activitiesDoneWithFriendsAll[i] / (double)dataLogger.activityCounts[i]));
	//	System.out.println(getActivitiesWithFriends());
	
	//	System.out.format("%f, %f\n", mentorProbability, getRatioOfInterested());
	
	// 0:FriendRule, 1:ParentRule, 2:LeaderRuleV2, 3:ChoiceRuleV2
		
	System.out.format("Mentor: %f, %f, %f, %f, %f, %f, %f\n", mentorProbability, 
			dataLogger.calcProportionOfInterested(), dataLogger.calcAverageInterest(),
			average(dataLogger.netEffectOfRules[0]),
			average(dataLogger.netEffectOfRules[1]),
			average(dataLogger.netEffectOfRules[2]),
			average(dataLogger.netEffectOfRules[3]));
		// 
		double[] adHocActivitiesDone = dataLogger.activitiesDoneWatcher.getDataPoint();
		double[] organizedActivitiesDone = dataLogger.organizedActivitiesDoneWatcher.getDataPoint();

		System.out.format("Ad hoc activities per day.    Average: %.2f, Median: %.2f\n", average(adHocActivitiesDone), median(adHocActivitiesDone));
		System.out.format("Organized activities per day. Average: %.2f, Median: %.2f\n", average(organizedActivitiesDone), median(organizedActivitiesDone));
		
		System.out.println();
		
		
//		double steps = schedule.getSteps();
//		for (Student s : students) {
//			System.out.format("%d, %f, %f\n", s.id, s.interest.getAverage(), (s.activitiesDone / steps));
//		}
		
		System.out.format("Net Effect of Encouragement: Parent: %.2f, Sibling: %.2f, Friend: %.2f, NoOne: %.2f\n", 
				dataLogger.netEffectOfEncouragementOnInterest[0],
				dataLogger.netEffectOfEncouragementOnInterest[1],
				dataLogger.netEffectOfEncouragementOnInterest[2],
				dataLogger.netEffectOfEncouragementOnInterest[3]);

		System.out.format("AveIntr: %f, PropOfIntr: %f, LeaderExp: %f, Noise: %f, MatchingMethod: %d, FriendCopart: %f, IntrThresh: %f, IntrThreshNoise: %f\n", 
				dataLogger.calcAverageInterest(), dataLogger.calcProportionOfInterested(),
				leaderExpertise, leaderExpertiseNoise,
				activityMatchingMethod,
				dataLogger.activitiesDoneWithFriends / (double)dataLogger.activitiesDone,
				interestThreshold, interestThresholdNoise);
			
			
	}

	double[][] interestDiffs = new double[][] { {0}, {0}, {0}, {0} };
//	public double[] getInterestDiffs1() { return interestDiffs[0]; }
//	public double[] getInterestDiffs2() { return interestDiffs[1]; }
//	public double[] getInterestDiffs3() { return interestDiffs[2]; }
//	public double[] getInterestDiffs4() { return interestDiffs[3]; }
	
	public void compareStudents(ArrayList<Student> students1, String name1, ArrayList<Student> students2, String name2) {
//		compareStudents_SSE(students1, students2);
		compareStudents_Averages(students1, name1, students2, name2);
	}
	
	public void compareStudents_SSE(ArrayList<Student> students1, ArrayList<Student> students2) {

		// sort the students by ID
		Collections.sort(students1);
		Collections.sort(students2);
		
		// do pairwise comparison
		int n = students1.size();
		if (n != students2.size()) {
			System.err.format("Error: can't compare student lists because list1 contains %d and list2 contains %d students.\n", students1.size(), students2.size());
			return;
		}

		interestDiffs = new double[4][n];
		double SSE = 0;
		
		for (int i = 0; i < n; i++) {
			Student s1 = students1.get(i);
			Student s2 = students2.get(i);
			
			if (s1.id != s2.id) {
				System.err.format("Error: can't compare students because their IDs don't match (%d != %d).\n", s1.id, s2.id);
				return;
			}
			
			for (int j = 0; j < NUM_TOPICS; j++) {
				double diff = s2.interest.topics[j] - s1.interest.topics[j];
				SSE += diff * diff;
				interestDiffs[j][i] = diff;
			}
		}
		
		double meanSquaredError = SSE / n;
		System.out.format("MSE: %.2f\n", meanSquaredError);
		
	}	
	
	public void compareStudents_Averages(ArrayList<Student> students1, String name1, ArrayList<Student> students2, String name2) {
		double total1 = 0.0, total2 = 0.0;
		
		for (Student s : students1)
			for (int j = 0; j < NUM_TOPICS; j++) 
				total1 += s.interest.topics[j];

		for (Student s : students2)
			for (int j = 0; j < NUM_TOPICS; j++) 
				total2 += s.interest.topics[j];

		double ave1 = total1 / (students1.size() * NUM_TOPICS);
		double ave2 = total2 / (students2.size() * NUM_TOPICS);
		
		System.out.format("Average %s: %.2f, Average %s: %.2f, Difference: %.2f\n", name1, ave1, name2, ave2, (ave2-ave1));
	}

	@Override
	public void finish() {
		super.finish();
		dataLogger.close();
		printRunSummary();
		compareStudents(students, "7th grader (after simulation)", students7th, "7th grader (from data)");
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
