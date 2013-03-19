package stem.rules;

import stem.Adult;
import stem.Student;
import stem.TopicVector;
import stem.activities.Activity;

/**
 * Unrelated Adults<BR>
 * Unrelated adults are associated with interactions in activities; each has
 * an arbitrary level of expertise and passion in the topic of that
 * activity. There are some arbitrary fixed number of unrelated adults
 * available for interactions in the community. 
 * <ul>
 * <li>
 * When unrelated adults are
 * skilled at sharing their expertise and passion in a topic in an activity,
 * the value to interest increases. 
 * <li>
 * When unrelated adults are skilled at
 * sharing expertise but not passion in a topic in an activity, the value to
 * interest stays the same (unless the interest threshold has been exceeded,
 * then it increases). 
 * <li>
 * When unrelated adults are skilled at sharing passion
 * but not expertise in a topic in an activity, interest increases if interest 
 * is already below the interest threshold. Otherwise, no change.
 * <li> 
 * When unrelated adults are neither skilled nor passionate, the
 * value to interest decreases.
 * </ul>
 * 
 * @author Joey Harrison
 *
 */
public class UnrelatedAdultRule extends Rule
{

	@Override
	public void apply(Student s, Activity a) {
		// --- Unrelated Adults
		// if expertise & passion, interest increases
		// if expertise & !passion
		//		if interest > threshold, interest increases. else, no change
		// if !expertise & passion, no change
		//		if interest < threshold, interest increases. else, no change
		// if !expertise & !passion, interest decreases
		
		for (Adult adult : a.leaders)
			for (int i = 0; i < TopicVector.VECTOR_SIZE; i++) {
				boolean expertise = adult.expertise.topics[i] > s.model.expertiseThreshold;
				boolean passion = adult.passion.topics[i] > s.model.passionThreshold;
				
				if (expertise && passion)
					s.increaseInterest(i, a.content.topics[i], weight);
				else if (expertise && !passion && s.interest.topics[i] > s.model.interestThreshold)
					s.increaseInterest(i, a.content.topics[i], weight);
				else if (!expertise && passion && s.interest.topics[i] < s.model.interestThreshold) 
					s.increaseInterest(i, a.content.topics[i], weight);
				else if (!expertise && !passion)
					s.decreaseInterest(i, a.content.topics[i], weight);
			}

	}

}
