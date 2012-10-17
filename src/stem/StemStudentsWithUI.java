package stem;

import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.SimpleInspector;
import sim.util.Bag;
import stem.network.NetworkDisplay;

/**
 * GUI front end to the StemStudents model.
 * @author Joey Harrison
 * @author Matthew Hendrey
 * @version 0.1, October 12, 2012
 *
 */
public class StemStudentsWithUI extends GUIState
{

	public NetworkDisplay networkDisplay;

	public StemStudentsWithUI() {
		super(new StemStudents(System.currentTimeMillis()));
	}

	public StemStudentsWithUI(SimState state) {
		super(state);
	}

	public static void main(String[] args) {
		new StemStudentsWithUI().createController();
	}

	public static String getName() {
		return "Individual adoption model with decay";
	}

	public Object getSimulationInspectedObject() {
		return state;
	} // non-volatile

	public void start() {
		super.start();
		// set up our portrayals
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	@SuppressWarnings("serial")
	public void setupPortrayals() {
		networkDisplay.reset();
		networkDisplay.repaint();
		
		this.scheduleRepeatingImmediatelyBefore(networkDisplay);
		
		this.scheduleRepeatingImmediatelyAfter(new Steppable() {			
			@Override
			public void step(SimState state) {
				if (state.schedule.getTime() == Schedule.AFTER_SIMULATION)	// watch out for that last step, it's a doozy
					return;
			}			
		});

	}

	public void init(final Controller c) {
		super.init(c);
		
		networkDisplay = new NetworkDisplay(this);
		NetworkDisplay.frame.setTitle("Friend Network");
		c.registerFrame(NetworkDisplay.frame);
		NetworkDisplay.frame.setVisible(true);

		((Console)controller).setSize(400, 500);
	}
	
	public void studentSelected(Student s) {
		System.out.println("Student clicked.");
		Bag inspectors = new Bag();
		Bag names = new Bag();
		inspectors.add(new SimpleInspector(s, this));
		names.add("Student");
		controller.setInspectors(inspectors, names);
	}
}
