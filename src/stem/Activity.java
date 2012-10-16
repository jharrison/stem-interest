package stem;

import java.util.ArrayList;

import sim.engine.*;

/**
 * An Activity contains all the information needed for the activity to be
 * done. 
 * @author jharrison
 *
 */
public class Activity implements Steppable
{
	private static final long serialVersionUID = 1L;
	
	public TopicVector content;
	public ArrayList<Student> participants = new ArrayList<Student>();
	public ArrayList<Adult> leaders = new ArrayList<Adult>();

	public boolean isSchoolRelated = false;
	public boolean isVoluntary = false;
	public boolean isParentMediated = false;
	
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

}
