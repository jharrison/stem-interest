package stem;

import ec.util.MersenneTwisterFast;

public class TopicVector
{
	static public int VECTOR_SIZE = 3;
	
	double[] topics = new double[VECTOR_SIZE];
	
	public TopicVector() {
	}
	
	static public TopicVector createRandom(MersenneTwisterFast random) {
		TopicVector tv = new TopicVector();
		for (int i = 0; i < VECTOR_SIZE; i++)
			tv.topics[i] = random.nextDouble();
		return tv;
	}

}
