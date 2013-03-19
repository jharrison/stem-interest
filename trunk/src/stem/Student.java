package stem;

import java.util.ArrayList;

import stem.activities.Activity;
import stem.rules.Rule;


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
	public StemStudents model;
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
	 * student's interest vector in the process.
	 */
	public void doActivity(Activity activity) {
		for (Rule r : model.ruleSet.rules) {
			if (r.isActive)
				r.apply(this, activity);
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
	
	public void increaseInterest(int topicIndex, double topicRelevance, double weight) {
		interest.topics[topicIndex] += model.interestChangeRate * topicRelevance;
		if (interest.topics[topicIndex] > TopicVector.MAX_INTEREST)
			interest.topics[topicIndex] = TopicVector.MAX_INTEREST;
	}
	
	public void increaseInterest(TopicVector relevance, double weight) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			increaseInterest(i, relevance.topics[i], weight);		
	}
	
	public void decreaseInterest(int topicIndex, double topicRelevance, double weight) {
		interest.topics[topicIndex] -= model.interestChangeRate * topicRelevance;
		if (interest.topics[topicIndex] < TopicVector.MIN_INTEREST)
			interest.topics[topicIndex] = TopicVector.MIN_INTEREST;
	}

	public void decreaseInterest(TopicVector relevance, double weight) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			decreaseInterest(i, relevance.topics[i], weight);		
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
