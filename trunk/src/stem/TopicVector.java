package stem;

import ec.util.MersenneTwisterFast;
/**
 * TopicVector represents the three cluster groups given to us during Skype 
 * call on 10/10/2012.  These represent
 * <UL>
 * <LI>technology/engineering/math
 * <LI>earth/space science
 * <LI>human/biology
 * </UL>
 * 
 * @author Joey Harrison
 * @author Matthew Hendrey
 * @version 0.1, October 12, 2012
 *
 */
public class TopicVector
{
	static public int VECTOR_SIZE = 3;
	static public double MAX_INTEREST = 1.0;
	static public double MIN_INTEREST = 0.0;
	
	public double[] topics = new double[VECTOR_SIZE];
	
	public TopicVector() {
	}

	/** Copy constructor. */
	public TopicVector(TopicVector other) {
		for (int i = 0; i < VECTOR_SIZE; i++)
			this.topics[i] = other.topics[i];
	}
	
	/*
	 * Constructor that takes in specific values
	 * @param a1 This is the first element of TopicVector
	 * @param a2 This is the second element of TopicVector
	 * @param a3 This is the third element of TopicVector
	 */
	public TopicVector(double a1, double a2, double a3) {
		topics[0] = a1;
		topics[1] = a2;
		topics[2] = a3;
	}
	
	public double getAverage() {
		double total = 0;
		for (int i = 0; i < VECTOR_SIZE; i++)
			total += topics[i];
		
		return total / VECTOR_SIZE;
	}
	
	/**
	 * Create a TopicVector that is focused on the largest topic in this one.
	 * @return a new TopicVector with all zeroes except for the focus topic which
	 * will be 1.
	 */
	public TopicVector createFocusedVector() {
		TopicVector fv = new TopicVector(0, 0, 0);
		int largestIndex = getMainTopicIndex();
		fv.topics[largestIndex] = 1.0;
		return fv;
	}
	
	/**
	 * Get the index of the main topic in the vector.
	 */
	public int getMainTopicIndex() {
		double largestValue = Double.NEGATIVE_INFINITY;
		int largestIndex = 0;
		for (int i = 0; i < VECTOR_SIZE; i++)
			if (topics[i] > largestValue) {
				largestValue = topics[i];
				largestIndex = i;
			}
		return largestIndex;
	}
	
	/**
	 * Multiply all values of the topic vector by the given scale.
	 */
	public void scale(double scale) {
		for (int i = 0; i < VECTOR_SIZE; i++)
			topics[i] *= scale;
	}
	
	public TopicVector plus(TopicVector other) {
		return new TopicVector(
				topics[0] + other.topics[0], 
				topics[1] + other.topics[1], 
				topics[2] + other.topics[2]);
	}
	
	public TopicVector weightedCombination(TopicVector other, double weight) {
		return new TopicVector(
				weight * topics[0] + (1 - weight) * other.topics[0], 
				weight * topics[1] + (1 - weight) * other.topics[1], 
				weight * topics[2] + (1 - weight) * other.topics[2]);
	}	
	
	static public TopicVector weightedCombination(TopicVector v1, TopicVector v2, double weight) {
		return new TopicVector(
				weight * v1.topics[0] + (1 - weight) * v2.topics[0], 
				weight * v1.topics[1] + (1 - weight) * v2.topics[1], 
				weight * v1.topics[2] + (1 - weight) * v2.topics[2]);
	}
	
	@Override
	public String toString() {
		//TODO generalize this for any vector size
		return String.format("[%.3f, %.3f, %.3f]", topics[0], topics[1], topics[2]);
	}
	
	static public TopicVector createRandom(MersenneTwisterFast random) {
		TopicVector tv = new TopicVector();
		for (int i = 0; i < VECTOR_SIZE; i++)
			tv.topics[i] = random.nextDouble();
		return tv;
	}
	
	static public TopicVector createRandom(MersenneTwisterFast random, double ave, double stdev) {
		TopicVector tv = new TopicVector();
		for (int i = 0; i < VECTOR_SIZE; i++)
			do
				tv.topics[i] = ave + stdev * random.nextGaussian();
			while (tv.topics[i] < MIN_INTEREST || tv.topics[i] > MAX_INTEREST);
		return tv;
	}

}
