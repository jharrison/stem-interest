package stem;

import java.util.ArrayList;

import stem.activities.Activity;


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
	/** Count of activities this student has done. */
	public int activitesDone = 0;

	public int [] stuffIDo = new int[StemStudents.NUM_ACTIVITY_TYPES];
	
	public ArrayList<Student> friends = new ArrayList<Student>();
	
	/** Activities for today */
	public ArrayList<Activity> activities = new ArrayList<Activity>();

	public Student(StemStudents model) {
		this.model = model;
	}

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
		int friendCount = activity.countParticipants(friends);
				
		if (friendCount > 0) {
			increaseInterest(activity.content);
		}		
		else {

			boolean makeFriends = false;
			for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
				if (interest.topics[i] < model.interestThreshold)
					decreaseInterest(i, activity.content.topics[i]);
				else
					makeFriends = true;

			//TODO make friends
		}
		
		// --- Parents / Caregivers
		boolean parentPresent = false;
		for (Adult a : activity.leaders)
			if (a == parent)
				parentPresent = true;
				
		if (parentPresent) {
			increaseInterest(activity.content);
		}
		else {
			decreaseInterest(activity.content);
		}
		
		if (activity.isParentMediated) {
			increaseInterest(activity.content);
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
			
		activitesDone++;
		model.activityCounts[activity.type.id]++;
	}
	
	/**
	 * Check to see if the student is interested in the given content.
	 * @param content
	 * @return true if any topic exceeds threshold, false otherwise
	 */
	public boolean isInterestedInContent(TopicVector content) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			if (content.topics[i] > model.interestThreshold)
				return true;
		
		return false;		
	}
	
	public void increaseInterest(int topicIndex, double topicRelevance) {
		interest.topics[topicIndex] += model.interestChangeRate * topicRelevance;
		if (interest.topics[topicIndex] > TopicVector.MAX_INTEREST)
			interest.topics[topicIndex] = TopicVector.MAX_INTEREST;
	}
	
	public void increaseInterest(TopicVector relevance) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			increaseInterest(i, relevance.topics[i]);		
	}
	
	public void decreaseInterest(int topicIndex, double topicRelevance) {
		interest.topics[topicIndex] -= model.interestChangeRate * topicRelevance;
		if (interest.topics[topicIndex] < TopicVector.MIN_INTEREST)
			interest.topics[topicIndex] = TopicVector.MIN_INTEREST;
	}

	public void decreaseInterest(TopicVector relevance) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			decreaseInterest(i, relevance.topics[i]);		
	}
	
	
	public double[] getInterest() {
		return interest.topics;
	}

	public double getAverageInterest() {
		return interest.getAverage();
	}
	
	public static Student parseStudent(StemStudents model, String line) {
		Student student = new Student(model);

		String[] tokens = line.split(",");
		student.id = Integer.parseInt(tokens[0]);
		// skip 1: gender
		// skip 2: school
		// skip 3: teacher
		// read 4-18: the 15 activities
		for (int i = 0; i < 15; i++)
			student.stuffIDo[i] = Integer.parseInt(tokens[i+4]);
		// hard-code school for everyday
		student.stuffIDo[15] = 5;
		// skip 19: Other_me
		// skip 20: Name_other
		// skip 21-43: Stuff that interests me
		// read 44, 45, 46: interest levels for the three aggregate categories
		double techInterest 	= Double.parseDouble(tokens[44]);
		double earthInterest 	= Double.parseDouble(tokens[45]);
		double humanInterest 	= Double.parseDouble(tokens[46]);
		
		student.interest = new TopicVector(techInterest, earthInterest, humanInterest);
		
		return student;
	}

}
