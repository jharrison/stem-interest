package stem.rules;

import java.util.ArrayList;

import stem.Student;
import stem.activities.Activity;

/**
 * Rules for making new friends:
 * During an activity, 
 * 	0) if no friends are present, 
 * 		if random < makeFriendProb,
 * 			pick a new friend with highly similar interests & swap for old friend if she is less similar
 * 	1) if one friend is present,
 * 		if friend of friend is present,
 * 			if random < closeTriadProb,
 * 				close the triad
 * 		else
 * 			same as 0)
 * 2) if two or more friends are present,
 * 		do nothing
 * 
 * @author Joey Harrison
 *
 */
public class MakeFriendRule extends Rule
{
	@Override
	public void apply(Student s, Activity a) {
		int friendCount = a.countParticipants(s.friends);
		
		if (friendCount == 0) {
			if (s.model.random.nextDouble() < s.model.makeFriendProbability) {
				Student potentialNewFriend = findClosestMatch(s, a.participants);
				Student potentialFormerFriend = findWorstMatch(s, s.friends);
				if ((potentialNewFriend != null) && (potentialFormerFriend != null))
					if (s.calcDistance(potentialNewFriend) < s.calcDistance(potentialFormerFriend)) {
						s.model.addFriends(s, potentialNewFriend);
						s.model.removeFriends(s, potentialFormerFriend);		
					}
			}
		}
		else if (friendCount == 1) {
			Student friend = s.friends.get(0);
			if (a.countParticipants(friend.friends) > 1) {	// there will be at least 1, including s
				if (s.model.random.nextDouble() < s.model.closeTriadProbability) {
					Student potentialNewFriend = findClosestMatch(s, friend.friends);
					Student potentialFormerFriend = findWorstMatch(s, s.friends);
					if (s.calcDistance(potentialNewFriend) < s.calcDistance(potentialFormerFriend)) {
						s.model.addFriends(s, potentialNewFriend);
						s.model.removeFriends(s, potentialFormerFriend);
					}
				}
			}
		}
	}
	
	private Student findClosestMatch(Student s, ArrayList<Student> others) {
		double closestDist = Double.POSITIVE_INFINITY;
		Student closest = null;
		for (Student o : others)
			if (o != s) {
				double d = s.calcDistance(o);
				if (d < closestDist) {
					closest = o;
					closestDist = d;
				}
			}
		
		return closest;
	}
	
	private Student findWorstMatch(Student s, ArrayList<Student> others) {
		double worstDist = Double.NEGATIVE_INFINITY;
		Student worst = null;
		for (Student o : others)
			if (o != s) {
				double d = s.calcDistance(o);
				if (d > worstDist) {
					worst = o;
					worstDist = d;
				}
			}
		
		return worst;
	}

}
