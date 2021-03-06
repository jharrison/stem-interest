package stem.rules;

import stem.Adult;
import stem.Student;
import stem.TopicVector;
import stem.activities.Activity;

/**
 * Leaders (previously Unrelated Adults)<BR>
 * Leaders are associated with interactions in activities; each has
 * an arbitrary level of expertise and passion in the topic of that
 * activity. There are some arbitrary fixed number of leaders
 * available for interactions in the community. 
 * <ul>
 * <li>
 * When leaders are skilled at sharing their expertise and passion in the topic
 * of an activity, interest increases. 
 * <li>
 * When leaders are skilled at sharing expertise but not passion in the topic 
 * of an activity, interest decreases (unless the interest threshold has been 
 * exceeded, then it increases). 
 * <li>
 * When leaders are skilled at sharing passion but not expertise in the topic of 
 * an activity, interest increases if interest is already below the interest 
 * threshold. Otherwise, no change.
 * <li> 
 * When leaders are neither skilled nor passionate, interest decreases.
 * </ul>
 * 
 * @author Joey Harrison
 *
 */
public class LeaderRuleV2 extends Rule
{

	@Override
	public void apply(Student s, Activity a) {
		// --- Leaders
		// if expertise & passion, interest increases
		// if expertise & !passion
		//		if interest > threshold, interest increases. else, interest decreases
		// if !expertise & passion
		//		if interest < threshold, interest increases. else, no change
		// if !expertise & !passion, interest decreases
		int goodExperience = 0;  //Should prob. participation inc or dec.
		
		for (Adult adult : a.leaders) {
			for (int i = 0; i < TopicVector.VECTOR_SIZE; i++) {
				boolean expertise = adult.expertise.topics[i] > s.expertiseThreshold;
				boolean passion = adult.passion.topics[i] > s.passionThreshold;
				
				if (expertise && passion) {
					s.increaseInterest(a, i, weight, this);
					goodExperience++;
				}
				else if (expertise && !passion) {
					if (s.interest.topics[i] > s.interestThreshold) {
						s.increaseInterest(a, i, weight, this);
						goodExperience++;
					}
					else {
						s.decreaseInterest(a, i, weight, this);
						goodExperience--;
					}
				}
				else if (!expertise && passion) {
					if (s.interest.topics[i] < s.interestThreshold) {
						s.increaseInterest(a, i, weight, this);
						goodExperience++;
					}
					else {} // do nothing
				}
				else if (!expertise && !passion) {
					s.decreaseInterest(a, i, weight, this);
					goodExperience--;
				}
			}
		}
		//Change probability of participation for the next time
//		if (goodExperience > 0)
//			s.increaseParticipationRate(a.type.id);
//		else if (goodExperience < 0)
//			s.decreaseParticipationRate(a.type.id);

		if (goodExperience > 0)
			s.increaseParticipationRate(a, true);
		else if (goodExperience < 0)
			s.decreaseParticipationRate(a, true);
	}

}
