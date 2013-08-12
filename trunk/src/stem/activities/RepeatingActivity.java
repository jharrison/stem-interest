package stem.activities;

import java.util.ArrayList;

import sim.engine.SimState;
import stem.StemStudents;
import stem.Student;
import stem.TopicVector;

public class RepeatingActivity extends Activity
{
	private static final long serialVersionUID = 1L;
	public int timesRepeated = 0;
	public long lastOccurence = -1;
	
	public ArrayList<Student> potentialParticipants = new ArrayList<Student>();
	
	public RepeatingActivity() {
		super();
	}
	
	public RepeatingActivity(TopicVector content) {
		super(content);
	}
	
	@Override
	public void addParticipant(Student s) {
		potentialParticipants.add(s);
	}

	@Override
	public void step(SimState state) {
		StemStudents model = (StemStudents)state;

		participants.clear();
		for (Student s : potentialParticipants)
	 		if (model.willDoToday(s, this.type)) {
				participants.add(s);
				s.activities.add(this);
	 		}

		if (!(this instanceof ScienceClass))
			System.out.format("Step: %d, activity: %s, content: %s\n", state.schedule.getSteps(), type.name, content.toString());
		
		super.step(state);
		
		lastOccurence = model.schedule.getSteps();
		timesRepeated++;
	}
	
	static public RepeatingActivity createFromType(StemStudents model, ActivityType type) {
		RepeatingActivity a = new RepeatingActivity(type.content);
		a.type = type;

		a.isSchoolRelated = model.random.nextDouble() < type.probSchoolRelated;
		a.isVoluntary = model.random.nextDouble() < type.probVoluntary;
		a.isParentEncouraged = model.random.nextDouble() < type.probParentEncouraged;
		
		return a;
	}


}
