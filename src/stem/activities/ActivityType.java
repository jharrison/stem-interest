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
