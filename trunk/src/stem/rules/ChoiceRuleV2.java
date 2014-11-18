package stem.rules;

import stem.Student;
import stem.activities.Activity;

/**
 * Choice<BR>
 * <ul>
 * <li>If the student has a low degree of choice, interest and participation decrease.</li>
 * <li>If the student has a moderate degree of choice, no change.</li>
 * <li>If the student has a high degree of choice, interest and participation increase.</li>
 * </ul>
 * 
 * @author Joey Harrison
 *
 */
public class ChoiceRuleV2 extends Rule
{
	@Override
	public void apply(Student s, Activity a) {
		switch (a.type.degreeOfChoice) {
		case 0: // Low: decrease interest and participation
			s.decreaseInterest(a, weight, this);
//			s.decreaseParticipationRate(a.type.id);
			s.decreaseParticipationRate(a, true);
			break;
		case 1: // Moderate: no change
			break;
		case 2: // High: increase interest and participation
			s.increaseInterest(a, weight, this);
//			s.increaseParticipationRate(a.type.id);
			s.increaseParticipationRate(a, true);
			break;
		}
	}
}
