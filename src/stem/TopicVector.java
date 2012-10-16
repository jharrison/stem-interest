package stem;

import ec.util.MersenneTwisterFast;

public class TopicVector
{
	static public int VECTOR_SIZE = 3;
	static public double MAX_INTEREST = 1.0;
	static public double MIN_INTEREST = 0.0;
	
	double[] topics = new double[VECTOR_SIZE];
	
	public TopicVector() {
	}
	
	public double getAverage() {
		double total = 0;
		for (int i = 0; i < VECTOR_SIZE; i++)
			total += topics[i];
		
		return total / VECTOR_SIZE;
	}
	
	static public TopicVector createRandom(MersenneTwisterFast random) {
		TopicVector tv = new TopicVector();
		for (int i = 0; i < VECTOR_SIZE; i++)
			tv.topics[i] = random.nextDouble();
		return tv;
	}

}
