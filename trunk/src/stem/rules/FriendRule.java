package stem.rules;

import stem.Student;
import stem.TopicVector;
import stem.activities.Activity;

/**
 * Peers
 * <ul>
 * <li>When friends are participants in an activity, 
 * interest increases (below the interest threshold). 
 * <li>When no friends are participants in an activity, 
 * interest decreases (below the interest threshold). 
 * <li>Above the interest threshold, children/youth have a certain probability 
 * of making friends with others in the activity.
 * </ul>
 * 
 * @author Joey Harrison
 *
 */
public class FriendRule extends Rule
{

	@Override
	public void apply(Student s, Activity a) {		
		// count friends
		int friendCount = a.countParticipants(s.friends);
		int goodExperience = 0;
				
		if (friendCount > 0) {
			s.increaseInterest(a, weight, this);
			goodExperience++;
		}		
		else {
			for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
				if (s.interest.topics[i] < s.model.interestThreshold) {
					s.decreaseInterest(a, i, weight, this);
					goodExperience--;
				}
		}
		// Now adjust prob. of doing activity again based on good/bad experience
		// Note that goodExperience may still be 0 if student is interested in the topics
		if (goodExperience > 0)
			s.increaseParticipationRate(a.type.id);
		else if (goodExperience < 0)
			s.decreaseParticipationRate(a.type.id);
	}

}
