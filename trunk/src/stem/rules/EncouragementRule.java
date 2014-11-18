package stem.rules;

import stem.Student;
import stem.Student.Encouragement;
import stem.activities.Activity;

public class EncouragementRule extends Rule
{

	@Override
	public void apply(Student s, Activity a) {
		int encouragementLevel = 0;
		if (s.activityEncouragement[a.type.id][Encouragement.Parent.ordinal()])
			encouragementLevel++;
		if (s.activityEncouragement[a.type.id][Encouragement.Sibling.ordinal()])
			encouragementLevel++;
		if (s.activityEncouragement[a.type.id][Encouragement.Friend.ordinal()])
			encouragementLevel++;

		// if the student's interest in the main topic of this activity is below the interest threshold,
		// change participation based on encouragement.
		// else, participation level changes based on whether the topic is interesting
		
		int mainTopicIndex = a.content.getMainTopicIndex();
		if (s.interest.topics[mainTopicIndex] < s.interestThreshold) {
			if (encouragementLevel > 0)
				s.increaseParticipationRate(a, false);
			else
				s.decreaseParticipationRate(a, false);
		}
		
	}
}
