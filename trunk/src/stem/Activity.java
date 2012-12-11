package stem;

import java.util.ArrayList;

import sim.engine.*;

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
	
	/** name of the activity, e.g. Library, Scouts, etc. */
	public String name;
	public TopicVector content;
	public ArrayList<Student> participants = new ArrayList<Student>();
	public ArrayList<Adult> leaders = new ArrayList<Adult>();

	public boolean isSchoolRelated = false;
	public boolean isVoluntary = false;
	public boolean isParentMediated = false;
	
	/** how often this activity is repeated */
	public int daysBetween;	
	/** how many times this activity is repeated */
	public int numRepeats; 	
	
	public Activity(TopicVector content, ArrayList<Student> participants, ArrayList<Adult> leaders, 
			boolean isSchoolRelated, boolean isVoluntary, boolean isParentMediated) {
		super();
		this.content = content;
		this.participants = participants;
		this.leaders = leaders;
		this.isSchoolRelated = isSchoolRelated;
		this.isVoluntary = isVoluntary;
		this.isParentMediated = isParentMediated;
	}

	public Activity(TopicVector content, ArrayList<Student> participants) {
		this.content = content;
		this.participants = participants;
	}

	public Activity(TopicVector content) {
		this.content = content;
	}

	@Override
	public void step(SimState state) {
		for (Student s : participants)
			s.doActivity(this);		
	}
	
	static public Activity createFromType(StemStudents model, ActivityType type) {
		Activity a = new Activity(type.content);
		a.type = type;

		a.isSchoolRelated = model.random.nextDouble() < type.probSchoolRelated;
		a.isVoluntary = model.random.nextDouble() < type.probVoluntary;
		a.isParentMediated = model.random.nextDouble() < type.probParentMediated;
		
		return a;
	}

}
