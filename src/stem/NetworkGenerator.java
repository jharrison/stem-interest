package stem;

import java.util.ArrayList;

import ec.util.MersenneTwisterFast;

public class NetworkGenerator
{
	/**
	 * Create a small-world network representing friendships between players.
	 * Based on pseudocode from Prettejohn, Berryman, and McDonnell (2011)
	 */
	static public void initSmallWorldNetwork(ArrayList<Student> students, int numFriendsPerStudent, double rewireProbability, MersenneTwisterFast random) {
		// First, create a ring network
		initRingNetwork(students, numFriendsPerStudent);
				
		// Second, rewire edges randomly with probability smallWorldRewireProbability
		rewireNetworkLinks(students, numFriendsPerStudent, rewireProbability, random);		
	}
	
	static public void initRingNetwork(ArrayList<Student> students, int numFriendsPerStudent) {
		// First, create a ring network
		int n = students.size();
		for (int i = 0; i < n; i++) {
			Student p1 = students.get(i);
			for (int j = i+1; j <= i+numFriendsPerStudent/2; j++) {
				Student p2 = students.get(j % n);
				p1.friends.add(p2);
				p2.friends.add(p1);
			}
		}
	}
	
	static public void rewireNetworkLinks(ArrayList<Student> students, int numFriendsPerStudent, double probability, MersenneTwisterFast random) {
		int n = students.size();
		for (int i = 0; i < n; i++) {
			Student p1 = students.get(i);
			for (int j = i+1; j <= i+numFriendsPerStudent/2; j++) {
				if (random.nextDouble() >= probability)
					continue;
				Student p2 = students.get(j % n);
				p1.friends.remove(p2);
				p2.friends.remove(p1);
				
				// pick a random node that isn't i, or adjacent to i
				Student p3;
				do {
					p3 = students.get(random.nextInt(n));
				} 
				while ((p3 == p1) || (p3.friends.contains(p1)));
				
				p1.friends.add(p3);
				p3.friends.add(p1);
			}
		}
	
	}

}
