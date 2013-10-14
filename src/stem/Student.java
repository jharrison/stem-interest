package stem;

import java.util.ArrayList;
import java.util.Arrays;

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
	public boolean isFemale = true;
	public TopicVector interest;
	public double interestThreshold;
	public double passionThreshold;
	public double expertiseThreshold;
	public Adult parent;
	public int id;
	public String teacher;
	/** Count of activities this student has done. */
	public int activitesDone = 0;

	/** List of responses the student gave to the "stuff I do" questions. */
	private int [] stuffIDo = new int[StemStudents.NUM_ACTIVITY_TYPES];
	public int [] getStuffIDo() { return stuffIDo; }
	
	/** Number of days that have passed since the student has done each of these activities. 
	 * This is used to prevent activities from recurring too frequently. */
	int [] daysSinceActivity = new int[StemStudents.NUM_ACTIVITY_TYPES];
	public int [] getDaysSinceActivity() { return daysSinceActivity; }
	

	public int[] activityCounts = new int[StemStudents.NUM_ACTIVITY_TYPES];		
	
	/*
	 * Array containing the probability of participating in each of the activities.
	 * This was moved into Students so that the probs can evolve depending upon
	 * participation
	 */
	double [] participationRates = new double[StemStudents.NUM_ACTIVITY_TYPES];
	public double [] getParticipationRates() { return participationRates; }
	
	
	public ArrayList<Student> friends = new ArrayList<Student>();
	
	/** Activities for today */
	public ArrayList<Activity> activities = new ArrayList<Activity>();

	public Student(StemStudents model) {
		this.model = model;

		interestThreshold = model.interestThresholdNoise * model.random.nextGaussian() + model.interestThreshold;
		passionThreshold = model.passionThresholdNoise * model.random.nextGaussian() + model.passionThreshold;
		expertiseThreshold = model.expertiseThresholdNoise * model.random.nextGaussian() + model.expertiseThreshold;
		
		// init daysSince to a large number so they're ready to go
		Arrays.fill(daysSinceActivity, Integer.MAX_VALUE);
		Arrays.fill(activityCounts, 0);
	}
	
	public void incrementCounters() {
		
		for (int i = 0; i < StemStudents.NUM_ACTIVITY_TYPES; i++)
			if (daysSinceActivity[i] < Integer.MAX_VALUE)
				daysSinceActivity[i]++;	
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
			
		daysSinceActivity[activity.type.id] = 0;
		activityCounts[activity.type.id]++;
		activitesDone++;

		model.studentParticipated(this, activity);
	}
	
	/**
	 * Calculate the distanace between two students in terms of interests.
	 * @return the root mean square difference.
	 */
	public double calcDistance(Student other) {
		double sum = 0;
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++) {
			double diff = this.interest.topics[i] - other.interest.topics[i];
			sum += diff*diff;
		}
		
		return Math.sqrt(sum/TopicVector.VECTOR_SIZE);
	}
		
	/**
	 * Check to see if the student is interested in the given content.
	 * @param content
	 * @return true if any topic exceeds threshold, false otherwise
	 */
	public boolean isInterestedInContent(TopicVector content) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			if (content.topics[i] > interestThreshold)
				return true;
		
		return false;		
	}
	
	public void increaseInterest(Activity a, int topicIndex, double weight, Rule r) {
		double prevInterest = interest.topics[topicIndex];
		interest.topics[topicIndex] += model.interestChangeRate * a.content.topics[topicIndex] * weight;
		if (interest.topics[topicIndex] > TopicVector.MAX_INTEREST)
			interest.topics[topicIndex] = TopicVector.MAX_INTEREST;
		
		model.studentInterestChanged(this, a, topicIndex, interest.topics[topicIndex] - prevInterest, r);
	}
	
	public void decreaseInterest(Activity a, int topicIndex, double weight, Rule r) {
		double prevInterest = interest.topics[topicIndex];
		interest.topics[topicIndex] -= model.interestChangeRate * a.content.topics[topicIndex] * weight;
		if (interest.topics[topicIndex] < TopicVector.MIN_INTEREST)
			interest.topics[topicIndex] = TopicVector.MIN_INTEREST;

		model.studentInterestChanged(this, a, topicIndex, interest.topics[topicIndex] - prevInterest, r);
	}

	public void increaseInterest(Activity a, double weight, Rule r) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			increaseInterest(a, i, weight, r);
	}
	
	public void decreaseInterest(Activity a, double weight, Rule r) {
		for (int i = 0; i < TopicVector.VECTOR_SIZE; i++)
			decreaseInterest(a, i, weight, r);
	}
	
	public void increaseParticipationRate(int activityID)
	{
		//Don't change prob. of participating in school
		if (activityID == (StemStudents.NUM_ACTIVITY_TYPES - 1))
			return;
		
		participationRates[activityID] += model.participationChangeRate;
		//Don't let it go over 1.0
		if (participationRates[activityID] > 1.0)
			participationRates[activityID] = 1.0;
	}
	
	public void decreaseParticipationRate(int activityID)
	{
		//Don't change prob. of participating in school
		if (activityID == (StemStudents.NUM_ACTIVITY_TYPES - 1))
			return;

		participationRates[activityID] -= model.participationChangeRate;
		//Don't let it go below 0.0
		if (participationRates[activityID] < 0.0)
			participationRates[activityID] = 0.0;
	}
	
	public double[] getInterest() {
		return interest.topics;
	}

	public double getAverageInterest() {
		return interest.getAverage();
	}
	
	public static Student parseStudent(StemStudents model, String line) {
		Student student = new Student(model);

		/**
		 * The responses to the question of "how often do you do the following...?" are mapped as follows:
		 * All the time:			1.0
		 * Often:					0.75
		 * Every once in a while:	0.5			
		 * Very rarely:				0.25
		 * Never:					0.0
		 * 
		 * These represent the probabilities that the student will do the activity at any given opportunity.
		 */
		// It is one-based so stuff an extra zero in there
		final double[] participationRate = new double[] {0, 0, 0.25, 0.5, 0.75, 1.0};
		
		String[] tokens = line.split(",");
		student.id = Integer.parseInt(tokens[0]);
		
		// 1st column: gender, 0 for male, 1 for female
		if (tokens[1].equalsIgnoreCase("NA"))
			student.isFemale = model.random.nextBoolean();
		else
			student.isFemale = (Integer.parseInt(tokens[1]) == 1);
		// skip 2: school
		student.teacher = tokens[3].trim();
		// read 4-18: the 15 activities
		for (int i = 0; i < 15; i++)
		{
			student.stuffIDo[i] = Integer.parseInt(tokens[i+4]);
//			student.participationRates[i] = participationRate[student.stuffIDo[i]];
			student.participationRates[i] = model.activityTypes.get(i).mapLikertToParticpationRate(student.stuffIDo[i]);
		}
		// hard-code school for everyday
		student.stuffIDo[15] = 5;
		student.participationRates[15] = 1.0;
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
