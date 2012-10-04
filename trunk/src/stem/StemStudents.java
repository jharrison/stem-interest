package stem;

import sim.engine.SimState;

public class StemStudents extends SimState
{
	private static final long serialVersionUID = 1L;

	public StemStudents(long seed) {
		super(seed);
	}

	public static void main(String[] args) {
		doLoop(StemStudents.class, args);
		System.exit(0);
	}

}
