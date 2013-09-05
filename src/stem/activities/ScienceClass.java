package stem.activities;

import java.util.ArrayList;

import sim.engine.SimState;
import stem.StemStudents;
import stem.TopicVector;

public class ScienceClass extends RepeatingActivity
{
	private static final long serialVersionUID = 1L;
	int meetingsSinceTopicChange = 0;
	int topicIndex = 0;
	
	ArrayList<TopicVector> topics = new ArrayList<TopicVector>();

	public ScienceClass() {
		super();
		
		type = new ActivityType();
		type.name = "Class";
		type.id = 15;
		type.numLeaders = 1;
		type.numParents = 0;
		type.maxParticipants = 30;
		type.daysBetween = 1;
//		type.isRepeating = true;  // not used yet
		type.numRepeats = 1096;
		type.meetingsBetweenTopicChange = 15;
		type.onSchoolDay = true;
		type.onWeekendDay = false;
		type.onSummer = false;
		type.withFriendsOnly = false;
		
		isSchoolRelated = true;
		isVoluntary = false;
		isParentEncouraged = false;

		topics.add(new TopicVector(1.0, 0.0, 0.0));
		topics.add(new TopicVector(0.0, 1.0, 0.0));
		topics.add(new TopicVector(0.0, 0.0, 1.0));
	}

	@Override
	protected void coordinateContent(StemStudents model) {
		// Do nothing. Science class is the source of coordination
	}

	@Override
	public void step(SimState state) {
		content = topics.get(topicIndex);
		super.step(state);
		meetingsSinceTopicChange++;
		if (meetingsSinceTopicChange == type.meetingsBetweenTopicChange) {
			topicIndex = (topicIndex + 1) % topics.size();	// increment topics, circle around if necessary
			meetingsSinceTopicChange = 0;
		}
	}
	
	

}
