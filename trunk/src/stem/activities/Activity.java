package stem.activities;

import java.util.ArrayList;

import sim.engine.*;
import stem.Adult;
import stem.StemStudents;
import stem.Student;
import stem.TopicVector;

/**
 * An Activity contains all the information needed for the activity to be
 * done. 
 * @author Joey Harrison
 * @author Matthew Hendrey
 * @version 0.1, October 12, 2012
 *
 */
public class Activity implements Steppable
{
	private static final long serialVersionUID = 1L;
	
	public ActivityType type;
	
	/* Max number of participants in the this activity. This can be altered to
	 * be different from the number specified in the ActivityType.
	 */
	public int maxParticipants;
	
	public TopicVector content;
	public ArrayList<Student> participants = new ArrayList<Student>();
	public ArrayList<Adult> leaders = new ArrayList<Adult>();

	public boolean isSchoolRelated = false;
	public boolean isVoluntary = false;
//	public boolean isParentEncouraged = false;  // this is no longer needed
	
	/** how often this activity is repeated */
	public int daysBetween;	
	/** how many times this activity is repeated */
	public int numRepeats; 
	
	public Activity() {
	}

	public Activity(TopicVector content) {
		this.content = content;
	}

//	public Activity(TopicVector content, ArrayList<Student> participants) {
//		this.content = content;
//		this.participants = participants;
//	}
//	
//	public Activity(TopicVector content, ArrayList<Student> participants, ArrayList<Adult> leaders, 
//			boolean isSchoolRelated, boolean isVoluntary, boolean isParentEncouraged) {
//		this.content = content;
//		this.participants = participants;
//		this.leaders = leaders;
//		this.isSchoolRelated = isSchoolRelated;
//		this.isVoluntary = isVoluntary;
//		this.isParentEncouraged = isParentEncouraged;
//	}
	
	@Override
	public String toString() {
		return String.format("%s [%s]", type.name, content.toString());
	}

	public void addParticipant(Student s) {
		participants.add(s);
	}
	
	public boolean isFull() {
		return participants.size() >= maxParticipants;
	}
	
	public boolean contains(Student s) {
		return participants.contains(s);
	}
	
	public boolean contains(ArrayList<Student> students) {
		for (Student s : students)
			if (participants.contains(s))
				return true;
		return false;
	}
	
	/** Count how many students in the given list are participating in this activity. */ 
	public int countParticipants(ArrayList<Student> students) {
		int participantCount = 0;
		for (Student p : participants) 
			if (students.contains(p))
				participantCount++;
		return participantCount;
	}

	@Override
	public void step(SimState state) {
		//System.out.format("Step: %d, activity: %s, content: %s\n", state.schedule.getSteps(), type.name, content.toString());
		
		for (Student s : participants)
			s.doActivity(this);		
	}
	
	static public Activity createFromType(StemStudents model, ActivityType type) {
		Activity a = new Activity(type.content);
		a.type = type;
		a.maxParticipants = type.maxParticipants;

		a.isSchoolRelated = model.random.nextDouble() < type.probSchoolRelated;
		a.isVoluntary = model.random.nextDouble() < type.probVoluntary;
//		a.isParentEncouraged = model.random.nextDouble() < type.probParentEncouraged;
		
		return a;
	}
	
	static public Activity createFromType(StemStudents model, ActivityType type, TopicVector content) {
		Activity a = new Activity(content);
		a.type = type;
		a.maxParticipants = type.maxParticipants;

		a.isSchoolRelated = model.random.nextDouble() < type.probSchoolRelated;
		a.isVoluntary = model.random.nextDouble() < type.probVoluntary;
//		a.isParentEncouraged = model.random.nextDouble() < type.probParentEncouraged;
		
		return a;
	}
}
