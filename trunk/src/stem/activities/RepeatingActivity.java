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
	public TopicVector originalTopic;
	
	public RepeatingActivity() {
		super();
	}
	
	public RepeatingActivity(TopicVector content) {
		super(content);
		originalTopic = new TopicVector(content);
	}
	
	@Override
	public void addParticipant(Student s) {
		potentialParticipants.add(s);
	}
	
	@Override
	public boolean isFull() {
		return potentialParticipants.size() >= maxParticipants;
	}

	protected void coordinateContent(StemStudents model) {
		content = TopicVector.weightedCombination(model.coordinatedTopic, originalTopic, model.coordinationLevel);
	}

	@Override
	public void step(SimState state) {
		StemStudents model = (StemStudents)state;
		
		coordinateContent(model);

		participants.clear();
		for (Student s : potentialParticipants)
	 		if (model.willDoToday(s, this.type)) {
				participants.add(s);
				s.activities.add(this);
	 		}
		
		super.step(state);
		
		lastOccurence = model.schedule.getSteps();
		timesRepeated++;
	}
	
	static public RepeatingActivity createFromType(StemStudents model, ActivityType type) {
		// This is a hack and I'm not proud of it
//		if (type.name.equals("Class"))
//			return new ScienceClass();
		
		RepeatingActivity a = new RepeatingActivity(type.content);
		a.type = type;
		a.maxParticipants = type.maxParticipants;

		a.isSchoolRelated = model.random.nextDouble() < type.probSchoolRelated;
		a.isVoluntary = model.random.nextDouble() < type.probVoluntary;
//		a.isParentEncouraged = model.random.nextDouble() < type.probParentEncouraged;
		
		return a;
	}


}
