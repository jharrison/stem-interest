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
	static public int VECTOR_SIZE = 4;
	static public double MAX_INTEREST = 1.0;
	static public double MIN_INTEREST = 0.0;
	
	public double[] topics = new double[VECTOR_SIZE];
	
	public TopicVector() {
	}

	public TopicVector(double initialValue) {
		for (int i = 0; i < VECTOR_SIZE; i++)
			topics[i] = initialValue;
	}

	/** Copy constructor. */
	public TopicVector(TopicVector other) {
		for (int i = 0; i < VECTOR_SIZE; i++)
			topics[i] = other.topics[i];
	}
	
	/*
	 * Constructor that takes in specific values
	 * @param a1 This is the first element of TopicVector
	 * @param a2 This is the second element of TopicVector
	 * @param a3 This is the third element of TopicVector
	 */
//	public TopicVector(double a1, double a2, double a3, double a4) {
//		topics[0] = a1;
//		topics[1] = a2;
//		topics[2] = a3;
//		topics[3] = a4;
//	}
	
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
		TopicVector fv = new TopicVector(0);
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
		TopicVector tv = new TopicVector(this);

		for (int i = 0; i < VECTOR_SIZE; i++)
			tv.topics[i] += other.topics[i];
		
		return tv;
	}
	
	public TopicVector weightedCombination(TopicVector other, double weight) {
		TopicVector tv = new TopicVector();

		for (int i = 0; i < VECTOR_SIZE; i++)
			tv.topics[i] = weight * topics[i] + (1 - weight) * other.topics[i];
		
		return tv;
	}	
	
	static public TopicVector weightedCombination(TopicVector v1, TopicVector v2, double weight) {
		TopicVector tv = new TopicVector();

		for (int i = 0; i < VECTOR_SIZE; i++)
			tv.topics[i] = weight * v1.topics[i] + (1 - weight) * v2.topics[i];
		
		return tv;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		
		for (int i = 0; i < topics.length; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(String.format("%.3f", topics[i]));
		}
		sb.append("]");
		
		return sb.toString();
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
