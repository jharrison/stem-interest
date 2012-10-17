package stem;

/**
 * An adults expertise & passion affect how student's interest in STEM 
 * changes during activity together
 * @author Joey Harrison
 * @author Matthew Hendrey
 * @version 0.1, October 12, 2012
 *
 */
public class Adult
{
	/**
	 * Parents knowledge of STEM topics
	 */
	public TopicVector expertise;

	/**
	 * Parents passion of STEM topics
	 */
	public TopicVector passion;
	
	/**
	 * Create <code>Adult</code> with given expertise & passion
	 * @param expertise
	 * @param passion
	 */
	public Adult(TopicVector expertise, TopicVector passion) {
		this.expertise = expertise;
		this.passion = passion;
	}

}
