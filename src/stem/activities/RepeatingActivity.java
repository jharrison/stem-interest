package stem.activities;

import java.util.ArrayList;

import sim.engine.SimState;
import stem.StemStudents;
import stem.Student;
import stem.TopicVector;

public class RepeatingActivity extends Activity
{
	private static final long serialVersionUID = 1L;
	
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
			if (model.willDoToday(s, this.type))
				participants.add(s);
				
		super.step(state);
	}
	
	


}
