package stem;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.util.Interval;
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
	
	public int numStudents = 200;
	public int getNumStudents() { return numStudents; }
	public void setNumStudents(int val) { numStudents = val; }

	public int classSize = 30;
	public int getClassSize() { return classSize; }
	public void getClassSize(int val) { classSize = val; }
	
	public int numFriendsPerStudent = 4;	
	public int getNumFriendsPerStudent() { return numFriendsPerStudent; }
	public void setNumFriendsPerStudent(int val) { numFriendsPerStudent = val; }

	public double smallWorldRewireProbability = 0.5;
	public double getSmallWorldRewireProbability() { return smallWorldRewireProbability; }
	public void setSmallWorldRewireProbability(double val) { smallWorldRewireProbability = val; }
	public Object domSmallWorldRewireProbability() { return new Interval(0.0, 1.0); }
	
	
	

	public StemStudents(long seed) {
		super(seed);
	}
	
	public void initStudents() {
		students.clear();
		for (int i = 0; i < numStudents; i++) {
			Student s = new Student(TopicVector.createRandom(random));
			s.parent = new Adult(TopicVector.createRandom(random), TopicVector.createRandom(random));
			students.add(s);
		}
		
	}
	
	public void start() {
		super.start();
		initStudents();
	}
	

	public static void main(String[] args) {
		doLoop(StemStudents.class, args);
		System.exit(0);
	}

}
