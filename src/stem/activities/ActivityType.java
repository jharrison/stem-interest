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
	public boolean isRepeating;
	
	public ActivityType() {
		
	}
	
	public String toString()
	{
		String sep = ", ";
		String result = Integer.toString(id) + sep + name + sep + content.toString() + sep 
				+ Integer.toString(numLeaders) + sep + Integer.toString(numParents) + sep 
				+ Integer.toString(maxParticipants) + sep + Float.toString(probSchoolRelated) 
				+ sep + Float.toString(probVoluntary) + sep 
				+ Float.toString(probParentEncouraged) + sep + Integer.toString(daysBetween) 
				+ sep + Integer.toString(numRepeats) + sep 
				+ Integer.toString(meetingsBetweenTopicChange) + sep + Boolean.toString(onSchoolDay)
				+ sep + Boolean.toString(onWeekendDay) + sep + Boolean.toString(onWeekendDay) + sep +
				Boolean.toString(withFriendsOnly) + "\n";
		return result;
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
		
		return at;
	}
	
}
