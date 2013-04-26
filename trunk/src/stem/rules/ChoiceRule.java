package stem.rules;

import stem.Student;
import stem.activities.Activity;

/**
 * Choice<BR>
 * <ul>
 * <li>If the student has a low degree of choice, interest is unchanged.</li>
 * <li>If the student has a moderate degree of choice, interest increases.</li>
 * <li>If the student has a high degree of choice, interest increases more.</li>
 * </ul>
 * 
 * @author Joey Harrison
 *
 */
public class ChoiceRule extends Rule
{
	@Override
	public void apply(Student s, Activity a) {
		switch (a.degreeOfChoice) {
		case 0: // Low: no change
			break;
		case 1: // Moderate: increase interest & prob. of participating
			s.increaseInterest(a.content, weight);
			s.increaseProbOfParticipating(a.type.id);
			break;
		case 2: // High: increase interest more
			s.increaseInterest(a.content, weight*2);
			s.increaseProbOfParticipating(a.type.id);
			break;
		}
	}

}
