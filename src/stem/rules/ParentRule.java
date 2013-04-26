package stem.rules;

import stem.Adult;
import stem.Student;
import stem.activities.Activity;

/**
 * Parents/Caregivers
 * <ul>
 * <li>
 * When parents are participants in an activity, interest increases. 
 * <li>
 * When no parents are participants in an activity, interest decreases. 
 * <li>
 * When parents actively broker access to activities to
 * allow children to pursue interest, interest increases.
 * </ul>
 * 
 * @author Joey Harrison
 *
 */
public class ParentRule extends Rule
{

	@Override
	public void apply(Student s, Activity a) {
		// --- Parents / Caregivers
		boolean parentPresent = false;
		for (Adult adult : a.leaders)
			if (adult == s.parent)
				parentPresent = true;
				
		if (parentPresent) {
			s.increaseInterest(a.content, weight);
			s.increaseProbOfParticipating(a.type.id);
		}
		
		if (a.isParentEncouraged) {
			s.increaseInterest(a.content, weight);
			s.increaseProbOfParticipating(a.type.id);
		}

	}

}
