package stem;

public class ActivityInput
{
	public int id;
	public String name;
	public TopicVector content;
	public int numLeaders;
	public int numParents;
	public int maxParticipants;
	public float probSchoolRelated;
	public float probVoluntary;
	public float probParentMediated;
	public int daysBetween;
	public int numRepeats;
	public int meetingsBetweenTopicChange;
	public boolean onSchoolDay;
	public boolean onWeekendDay;
	public boolean onSummer;
	
	public ActivityInput(int id, String name, TopicVector content,
			int numLeaders, int numParents, int maxParticipants,
			float probSchoolRelated, float probVoluntary,
			float probParentMediated, int daysBetween, int numRepeats,
			int meetingsBetweenTopicChange, boolean onSchoolDay,
			boolean onWeekendDay, boolean onSummer) 
	{
		this.id = id;
		this.name = name;
		this.content = content;
		this.numLeaders = numLeaders;
		this.numParents = numParents;
		this.maxParticipants = maxParticipants;
		this.probSchoolRelated = probSchoolRelated;
		this.probVoluntary = probVoluntary;
		this.probParentMediated = probParentMediated;
		this.daysBetween = daysBetween;
		this.numRepeats = numRepeats;
		this.meetingsBetweenTopicChange = meetingsBetweenTopicChange;
		this.onSchoolDay = onSchoolDay;
		this.onWeekendDay = onWeekendDay;
		this.onSummer = onSummer;
	}
	
	public String toString()
	{
		String sep = ", ";
		String result = Integer.toString(id) + sep + name + sep + content.toString() + sep 
				+ Integer.toString(numLeaders) + sep + Integer.toString(numParents) + sep 
				+ Integer.toString(maxParticipants) + sep + Float.toString(probSchoolRelated) 
				+ sep + Float.toString(probVoluntary) + sep 
				+ Float.toString(probParentMediated) + sep + Integer.toString(daysBetween) 
				+ sep + Integer.toString(numRepeats) + sep 
				+ Integer.toString(meetingsBetweenTopicChange) + sep + Boolean.toString(onSchoolDay)
				+ sep + Boolean.toString(onWeekendDay) + sep + Boolean.toString(onWeekendDay) + "\n";
		return result;
	}
	
}
