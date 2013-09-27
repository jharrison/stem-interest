package stem.activities;

import stem.TopicVector;

public class ActivityType
{
	public int id;	// this can double as an index
	public String name;
	public TopicVector content;
	public int numLeaders;
	public int numParents;
	public int maxParticipants;
	public float probSchoolRelated;
	public float probVoluntary;
	public float probParentEncouraged;
	public int daysBetween;
	public int numRepeats;
	public int meetingsBetweenTopicChange;
	public boolean onSchoolDay;
	public boolean onWeekendDay;
	public boolean onSummer;
	public boolean withFriendsOnly;
	public int degreeOfChoice;
	public boolean isRepeating;
	public int priority;
	
	public ActivityType() {
		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		result.append(id).append(", ");
		result.append(name).append(", ");
		result.append(content.toString()).append(", ");
		result.append(numLeaders).append(", ");
		result.append(numParents).append(", ");
		result.append(maxParticipants).append(", ");
		result.append(probSchoolRelated).append(", ");
		result.append(probVoluntary).append(", ");
		result.append(probParentEncouraged).append(", ");
		result.append(daysBetween).append(", ");
		result.append(numRepeats).append(", ");
		result.append(meetingsBetweenTopicChange).append(", ");
		result.append(onSchoolDay).append(", ");
		result.append(onWeekendDay).append(", ");
		result.append(onSummer).append(", ");
		result.append(withFriendsOnly).append(", ");
		result.append(degreeOfChoice).append(", ");
		result.append(isRepeating).append(", ");
		result.append(priority);

		return result.toString();
	}
	
	/**
	 * Maps the survey response to the question "How often do you do ...",
	 * which is given in a 5-point likert scale, to participation rates
	 * which are the probably of participating in any given opportunity.
	 * 
	 * @param response 
	 * @return
	 */
	public double mapLikertToParticpationRate(int response) {
		// calculate the number of opportunities in a year
		int opportunities = 0;
		if (onSchoolDay)
			opportunities += 199;
		if (onWeekendDay)
			opportunities += 104;
		if (onSummer)
			opportunities += 62;
		
		// The survey response is a likert scale for which the answers are:
		// 0: Never
		// 1: Very rarely / Few times per year
		// 2: Once in a while / 1-2 per month
		// 3: Often / 1-2 per week
		// 4: All the time / Almost every day
		int timesDone = 0;
		switch (response) {
		case 1: timesDone = 0; break;		// Never
		case 2: timesDone = 3; break;		// few times per year
		case 3: timesDone = 18; break;		// 1-2 times per month
		case 4: timesDone = 78; break;		// 1-2 per week
		case 5: timesDone = 200; break;		// almost every day
		}
		
		return Math.min(1, timesDone / (double)opportunities);
	}
	
	public int mapParticipationRateToLikert(double rate) {
		return 0;
	}
	
	public int mapActivityCountToLikert(int count) {
		if (count == 0)
			return 1;
		if (count <= 3)
			return 2;
		if (count <= 18)
			return 3;
		if (count <= 78)
			return 4;
		
		return 5;
	}
	
	static public ActivityType parseActivityType(String line) {
		String[] tokens = line.split(",");

		ActivityType at = new ActivityType();
		at.id = Integer.parseInt(tokens[0]);
		at.name = tokens[1].trim();
		at.content = new TopicVector(Double.parseDouble(tokens[2]),	Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]));						
		at.numLeaders = Integer.parseInt(tokens[5]);
		at.numParents = Integer.parseInt(tokens[6]);
		at.maxParticipants = Integer.parseInt(tokens[7]);
		at.probSchoolRelated = Float.parseFloat(tokens[8]);
		at.probVoluntary = Float.parseFloat(tokens[9]);
		at.probParentEncouraged = Float.parseFloat(tokens[10]);
		at.daysBetween = Integer.parseInt(tokens[11]);
		at.numRepeats = Integer.parseInt(tokens[12]);
		at.meetingsBetweenTopicChange = Integer.parseInt(tokens[13]);
		at.onSchoolDay = Boolean.parseBoolean(tokens[14]);
		at.onWeekendDay = Boolean.parseBoolean(tokens[15]);
		at.onSummer = Boolean.parseBoolean(tokens[16]);
		at.withFriendsOnly = Boolean.parseBoolean(tokens[17]);
		
		String choice = tokens[18].trim();
		if (choice.equals("LOW"))
			at.degreeOfChoice = 0;
		else if (choice.equals("MODERATE"))
			at.degreeOfChoice = 1;
		else if (choice.equals("HIGH"))
			at.degreeOfChoice = 2;
		else 
			System.err.println("ActivityType degreeOfChoice is not LOW | MODERATE | HIGH\nIt was read in as " + choice + "\nline");

		at.isRepeating = Boolean.parseBoolean(tokens[19]);
		at.priority = Integer.parseInt(tokens[20]);
		
		return at;
	}
	
}