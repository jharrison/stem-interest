package stem.rules;

import stem.Student;
import stem.activities.Activity;

/**
 * Base class for all rules affecting how interest level changes based on 
 * conditions and participants of an activity.
 * @author Joey Harrison
 *
 */
abstract public class Rule
{
	/** Is this rule in use? */
	public boolean isActive = true;
	
	/** How much does this rule change interest? [0,3] */
	public double weight = 1.0;


	public Rule() {}
	
	public Rule(double weight) {
		this.weight = weight;
	}
	
	public Rule(boolean isActive, double weight) {
		this.isActive = isActive;
		this.weight = weight;
	}
	
	abstract public void apply(Student s, Activity a);

}
