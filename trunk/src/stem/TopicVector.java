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
	
	double[] topics = new double[VECTOR_SIZE];
	
	public TopicVector() {
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
	
	@Override
	public String toString() {
		//TODO generalize this for any vector size
		return String.format("[%.4f, %.4f, %.4f]", topics[0], topics[1], topics[2]);
	}
	
	static public TopicVector createRandom(MersenneTwisterFast random) {
		TopicVector tv = new TopicVector();
		for (int i = 0; i < VECTOR_SIZE; i++)
			tv.topics[i] = random.nextDouble();
		return tv;
	}

}
