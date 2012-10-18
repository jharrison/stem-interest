package stem;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import sim.engine.SimState;
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
	
	ArrayList<Student> students = new ArrayList<Student>();
	
	public int numStudents = 30;
	public int getNumStudents() { return numStudents; }
	public void setNumStudents(int val) { numStudents = val; }

	public int classSize = 30;
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
	
	
	public ArrayList<Activity> scienceClasses = new ArrayList<Activity>();

	public UndirectedSparseGraph<Student, SimpleEdge> network = new UndirectedSparseGraph<Student, SimpleEdge>();
	

	public StemStudents(long seed) {
		super(seed);
	}
	
	public void initStudents() {
		students.clear();
		for (int i = 0; i < numStudents; i++) {
			Student s = new Student(this, TopicVector.createRandom(random));
			s.parent = new Adult(TopicVector.createRandom(random), TopicVector.createRandom(random));
			students.add(s);
		}
		
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
	
	public void start() {
		super.start();
		initStudents();
		initSmallWorldNetwork();
		
		for (Activity a : scienceClasses)
			schedule.scheduleRepeating(a);
	}
	

	public static void main(String[] args) {
		doLoop(StemStudents.class, args);
		System.exit(0);
	}

}
