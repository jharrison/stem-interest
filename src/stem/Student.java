package stem;

import java.util.ArrayList;

/**
 * Student (Child/Youth) 
 * <p>Each begins with a level of interest in a particular
 * cluster of science topics (e.g., “astronomy”). When a child/youth has an
 * interaction in a science activity, their interest level changes.</p>
 * 
 * @author Joey Harrison
 * @author Matthew Hendrey
 * @version 0.1, October 12, 2012
 * 
 */
public class Student
{
	StemStudents model;
	public TopicVector interest;
	public double interestThreshold = 0.5; // TODO figure out what this should be
	public Adult parent;
	public int id;

	public ArrayList<Student> friends = new ArrayList<Student>();

	public Student(StemStudents model, TopicVector interest) {
		this.model = model;
		this.interest = interest;
	}

	/**
	 * The student engages in the given activity, possibly changing the
	 * student's interest vector in the process. The change in interest
	 * is determined by the following factors:
	 * <p>
	 * 
	 * Peers
	 * <ul>
	 * <li>
	 * When friends are participants in an activity, the value to interest
	 * increases (below the interest threshold). 
	 * <li>When no friends are
	 * participants in an activity, the value to interest decreases (below the
	 * interest threshold). 
	 * <li>
	 * Above the interest threshold, children/youth have a
	 * certain probability of making friends with others in the activity.
	 * </ul>
	 * 
	 * Parents/Caregivers
	 * <ul>
	 * <li>
	 * When parents are participants in an activity, the value to interest
	 * increases. 
	 * <li>
	 * When no parents are participants in an activity, the value to
	 * interest decreases. 
	 * <li>
	 * When parents actively broker access to activities to
	 * allow children to pursue interest, value to interest increases.
	 * </ul>
	 * <p>
	 * 
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
	 * but not expertise in a topic in an activity, the value to interest stays
	 * the same.
	 * <li> 
	 * When unrelated adults are neither skilled nor passionate, the
	 * value to interest decreases.
	 * </ul>
	 * 
	 * Web resource
	 * Web resources vary in their fit to interest and expertise level of youth.
	 * Web resources that are a good fit to both increase interest. Web
	 * resources that are a poor fit to both decrease interest. Relative to
	 * interactions with people,
	 * 
	 * 
	 */
	public void doActivity(Activity activity) {

		// --- Peers
		// count friends
		int friendCount = 0;
		for (Student p : activity.participants) 
			if (friends.contains(p))
				friendCount++;
		
		if (friendCount == 0) {
			// interest decreases
		}
		else {
			// interest increases, perhaps moreso if multiple friends are present,
			// but shall not exceed the interest threshold
		}
		
		// --- Parents / Caregivers
		boolean parentPresent = false;
		for (Adult a : activity.leaders)
			if (a == parent)
				parentPresent = true;
				
		if (parentPresent) {
			// interest increases
		}
		else {
			// interest decreases
		}
		
		if (activity.isParentMediated) {
			// interest increases
		}
		
		// --- Unrelated Adults
		// if expertise & passion, interest increases
		// if expertise & !passion
		//		if interest > threshold, interest increases. else, no change
		// if !expertise & passion, no change
		// if !expertise & !passion, interest decreases
		
		for (Adult adult : activity.leaders)
			for (int i = 0; i < TopicVector.VECTOR_SIZE; i++) {
				boolean expertise = adult.expertise.topics[i] > model.expertiseThreshold;
				boolean passion = adult.passion.topics[i] > model.passionThreshold;
				
				if (expertise && passion)
					increaseInterest(i, activity.content.topics[i]);
				else if (expertise && !passion && interest.topics[i] > model.interestThreshold)
					increaseInterest(i, activity.content.topics[i]);
				else if (!expertise && passion) 
					{} // no change
				else if (!expertise && !passion)
					decreaseInterest(i, activity.content.topics[i]);
			}
			
			
	}
	
	public void increaseInterest(int topicIndex, double topicRelevance) {
		interest.topics[topicIndex] += model.interestChangeRate * topicRelevance;
		if (interest.topics[topicIndex] > TopicVector.MAX_INTEREST)
			interest.topics[topicIndex] = TopicVector.MAX_INTEREST;
	}
	
	public void decreaseInterest(int topicIndex, double topicRelevance) {
		interest.topics[topicIndex] -= model.interestChangeRate * topicRelevance;
		if (interest.topics[topicIndex] < TopicVector.MIN_INTEREST)
			interest.topics[topicIndex] = TopicVector.MIN_INTEREST;
	}
	
	public double[] getInterest() {
		return interest.topics;
	}

	public double getAverageInterest() {
		return interest.getAverage();
	}

}
