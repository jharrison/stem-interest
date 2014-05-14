// $Id: GEM.java,v 1.1 2006/06/01 18:00:34 forbes Exp $
package misc.layout;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;



/**
 * Java implementation of the gem 2D layout.
 * <br>
 * The algorithm needs to get various subgraphs and traversals.
 * The recursive nature of the algorithm is totally captured
 * within those subgraphs and traversals. The main loop of the
 * algorithm is then expressed using the iterator feature, which makes it
 * look like a simple flat iteration over nodes.
 *
 * @author David Duke
 * @author Hacked by Eytan Adar for Guess
 * @author Modified by Joey Harrison for MASON
 */

public class GEMLayout<V, E> extends AbstractLayout<V, E> {

    public final static String Name = "GEM Force-directed";
    
    private Hypergraph<V, E> graph;
    
    private Collection<V> nodes;
    private Collection<E> edges;
    
    private static int nodeCount;
    private static int edgeCount;
    
    //
    // GEM Constants
    //
    private static final int ELEN = 128;
    private static final int ELENSQR = ELEN * ELEN;
    private static final int MAXATTRACT	= 1048576;
    
    //
    // GEM Defualt Parameter Values
    //
    private static final float IMAXTEMPDEF	   = (float)1.0;
    private static final float ISTARTTEMPDEF   = (float)0.3;
    private static final float IFINALTEMPDEF   = (float)0.05;
    private static final int   IMAXITERDEF	   = 10;
    private static final float IGRAVITYDEF	   = (float)0.05;
    private static final float IOSCILLATIONDEF = (float)0.4;
    private static final float IROTATIONDEF    = (float)0.5;
    private static final float ISHAKEDEF       = (float)0.2;
    private static final float AMAXTEMPDEF     = (float)1.5;
    private static final float ASTARTTEMPDEF   = (float)1.0;
    private static final float AFINALTEMPDEF   = (float)0.02;
    private static final int   AMAXITERDEF     = 3;
    private static final float AGRAVITYDEF     = (float)0.1;
    private static final float AOSCILLATIONDEF = (float)0.4;
    private static final float AROTATIONDEF    = (float)0.9;
    private static final float ASHAKEDEF       = (float)0.3;
    private static final float OMAXTEMPDEF     = (float)0.25;
    private static final float OSTARTTEMPDEF   = (float)1.0;
    private static final float OFINALTEMPDEF   = (float)1.0;
    private static final int   OMAXITERDEF     = 3;
    private static final float OGRAVITYDEF     = (float)0.1;
    private static final float OOSCILLATIONDEF = (float)0.4;
    private static final float OROTATIONDEF    = (float)0.9;
    private static final float OSHAKEDEF       = (float)0.3;

    //
    // GEM variables
    //
    private static long 	iteration;
    private static long		temperature;
    private static int		centerX, centerY;
    private static long		maxtemp;
    private static float    oscillation, rotation;

    // Following parameters can be initialised in the original GEM
    // from a configuration file.  Here they are hard-wired, but
    // this could be replaced by configuration from a royere file.
    // (NB how to make this compatible with the "optionality" of
    // modules in Royere?
    private float	i_maxtemp	= IMAXTEMPDEF;
    private float	a_maxtemp	= AMAXTEMPDEF;
    private float	o_maxtemp	= OMAXTEMPDEF;
    private float	i_starttemp	= ISTARTTEMPDEF;
    private float	a_starttemp	= ASTARTTEMPDEF;
    private float	o_starttemp	= OSTARTTEMPDEF;
    private float	i_finaltemp	= IFINALTEMPDEF;
    private float	a_finaltemp	= AFINALTEMPDEF;
    private float	o_finaltemp	= OFINALTEMPDEF;
    private int     i_maxiter	= IMAXITERDEF;
    private int     a_maxiter	= AMAXITERDEF;
    private int     o_maxiter	= OMAXITERDEF;
    private float	i_gravity	= IGRAVITYDEF;
    private float	i_oscillation	= IOSCILLATIONDEF;
    private float	i_rotation	= IROTATIONDEF;
    private float	i_shake		= ISHAKEDEF;
    private float	a_gravity	= AGRAVITYDEF;
    private float	a_oscillation	= AOSCILLATIONDEF;
    private float	a_rotation	= AROTATIONDEF;
    private float	a_shake		= ASHAKEDEF;
    private float	o_gravity	= OGRAVITYDEF;
    private float	o_oscillation	= OOSCILLATIONDEF;
    private float	o_rotation	= OROTATIONDEF;
    private float	o_shake		= OSHAKEDEF;

    private Graph<V, E> g = null;

    /** 
     * Constructor  
     */
	public GEMLayout(Graph<V, E> g) {
		super(g);

		this.g = g;
	}

	public String getName() {
		return Name;
	}

	private static final String GEM2D = "GEM 2D Parameters";

	static class GemP
	{
		public int x, y; // position
		public int in;

		public int iX, iY; // impulse
		public float dir; // direction
		public float heat; // heat
		public float mass; // weight = nr edges
		public boolean mark;

		public GemP(int m) {
			x = 0;
			y = 0;
			iX = iY = 0;
			dir = (float) 0.0;
			heat = 0;
			mass = m;
			mark = false;
		}
	}

	private GemP gemProp[];
	private ArrayList<V> invmap;
	private ArrayList<Integer> adjacent[];
	private HashMap<V, Integer> nodeNumbers;

	public static int rand() {
		return (int) (Math.random() * Integer.MAX_VALUE);
	}

	private int map[];

	private int select() {

		int u;
		int n, v;

		if (iteration == 0) {
			// System.out.print( "New map for " + nodeCount );
			map = new int[nodeCount];
			for (int i = 0; i < nodeCount; i++)
				map[i] = i;
		}
		n = (int) (nodeCount - iteration % nodeCount);
		v = rand() % n; // was 1 + rand() % n due to numbering in GEM
		if (v == nodeCount) v--;
		if (n == nodeCount) n--;
		// System.out.println( "Access n = " + n + " v = " + v );
		u = map[v];
		map[v] = map[n];
		map[n] = u;
		return u;
	}

	private LinkedList<Integer> q;

	private int bfs(int root) {
		Integer uint;
		Iterator<Integer> nodeSet;
		int v, ui;

		if (root >= 0) {
			q = new LinkedList<Integer>();
			if (!gemProp[root].mark) { // root > 0
				for (int vi = 0; vi < nodeCount; vi++) {
					gemProp[vi].in = 0;
				}
			}
			else
				gemProp[root].mark = true; // root = -root;
			q.addFirst(new Integer(root));
			gemProp[root].in = 1;
		}
		if (q.size() == 0) return -1; // null
		v = (((Integer) (q.removeLast())).intValue());

		nodeSet = adjacent[v].iterator();
		while (nodeSet.hasNext()) {
			uint = nodeSet.next();
			ui = uint.intValue();
			if (gemProp[ui].in != 0) {
				q.addFirst(uint);
				gemProp[ui].in = gemProp[v].in + 1;
			}
		}
		return v;
	}

	private int graph_center() {
		GemP p;
		int c, u, v, w; // nodes
		int h;

		c = -1; // for a contented compiler.
		u = -1;

		h = nodeCount + 1;
		for (w = 0; w < nodeCount; w++) {
			v = bfs(w);
			while (v >= 0 && gemProp[v].in < h) {
				u = v;
				v = bfs(-1); // null
			}
			p = gemProp[u];
			if (p.in < h) {
				h = p.in;
				c = w;
			}
		}
		return c;
	}

	private void vertexdata_init(final float starttemp) {
		GemP p;

		temperature = 0;
		centerX = centerY = 0;

		for (int v = 0; v < nodeCount; v++) {
			p = gemProp[v];
			p.heat = starttemp * ELEN;
			temperature += p.heat * p.heat;
			p.iX = p.iY = 0;
			p.dir = 0;
			p.mass = 1 + gemProp[v].mass / 3;
			centerX += p.x;
			centerY += p.y;
		}
		// srand ((unsigned) time (NULL));
	}

	/*
	 * INSERT code from GEM
	 */

	/*
	 * Nasty using global variables to handle return params, but there are too
	 * many vectors in this code!
	 */

	private Point i_impulse(int v) {
		Iterator<Integer> nodeSet;

		int iX, iY, dX, dY, pX, pY;
		int n;
		GemP p, q;

		p = gemProp[v];
		pX = p.x;
		pY = p.y;

		n = (int) (i_shake * ELEN);
		iX = rand() % (2 * n + 1) - n;
		iY = rand() % (2 * n + 1) - n;
		iX += (centerX / nodeCount - pX) * p.mass * i_gravity;
		iY += (centerY / nodeCount - pY) * p.mass * i_gravity;

		for (int u = 0; u < nodeCount; u++) {
			q = gemProp[u];
			if (q.in > 0) {
				dX = pX - q.x;
				dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * ELENSQR / n;
					iY += dY * ELENSQR / n;
				}
			}
		}
		nodeSet = adjacent[v].iterator();
		int u;
		while (nodeSet.hasNext()) {
			u = nodeSet.next().intValue();
			q = gemProp[u];
			if (q.in > 0) {
				dX = pX - q.x;
				dY = pY - q.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, MAXATTRACT);
				iX -= dX * n / ELENSQR;
				iY -= dY * n / ELENSQR;
			}
		}
		return new Point(iX, iY);
	}

	public void insert() {
		
		Iterator<Integer> nodeSet;
		GemP p, q;
		int startNode;

		int v, w;

		int d;

		// System.out.println( "insert phase" );

		vertexdata_init(i_starttemp);

		oscillation = i_oscillation;
		rotation = i_rotation;
		maxtemp = (int) (i_maxtemp * ELEN);

		v = graph_center();

		for (int ui = 0; ui < nodeCount; ui++) {
			gemProp[ui].in = 0;
		}

		gemProp[v].in = -1;

		startNode = -1;
		for (int i = 0; i < nodeCount; i++) {
			d = 0;
			for (int u = 0; u < nodeCount; u++) {
				if (gemProp[u].in < d) {
					d = gemProp[u].in;
					v = u;
				}
			}
			gemProp[v].in = 1;

			nodeSet = adjacent[v].iterator();
			int u;
			while (nodeSet.hasNext()) {
				u = nodeSet.next().intValue();
				if (gemProp[u].in <= 0) gemProp[u].in--;
			}
			p = gemProp[v];
			p.x = p.y = 0;

			if (startNode >= 0) {
				d = 0;
				p = gemProp[v];
				nodeSet = adjacent[v].iterator();
				while (nodeSet.hasNext()) {
					w = ((Integer) nodeSet.next()).intValue();
					q = gemProp[w];
					if (q.in > 0) {
						p.x += q.x;
						p.y += q.y;
						d++;
					}
				}
				if (d > 1) {
					p.x /= d;
					p.y /= d;
				}
				d = 0;
				while ((d++ < i_maxiter) && (p.heat > i_finaltemp * ELEN)) {
					Point impulse = i_impulse(v);
					displace(v, impulse.x, impulse.y);
				}

			}
			else {
				startNode = i;
			}
		}
	}

	private void displace(int v, int iX, int iY) {
		
		int t;
		int n;
		GemP p;

		if (iX != 0 || iY != 0) {
			n = Math.max(Math.abs(iX), Math.abs(iY)) / 16384;
			if (n > 1) {
				iX /= n;
				iY /= n;
			}
			p = gemProp[v];
			t = (int) p.heat;
			n = (int) Math.sqrt(iX * iX + iY * iY);
			iX = iX * t / n;
			iY = iY * t / n;
			p.x += iX;
			p.y += iY;
			centerX += iX;
			centerY += iY;
			// imp = &vi[v].imp;
			n = t * (int) Math.sqrt(p.iX * p.iX + p.iY * p.iY);
			if (n > 0) {
				temperature -= t * t;
				t += t * oscillation * (iX * p.iX + iY * p.iY) / n;
				t = (int) Math.min(t, maxtemp);
				p.dir += rotation * (iX * p.iY - iY * p.iX) / n;
				t -= t * Math.abs(p.dir) / nodeCount;
				t = Math.max(t, 2);
				temperature += t * t;
				p.heat = t;
			}
			p.iX = iX;
			p.iY = iY;
		}
	}

	void a_round() {
		
		Iterator<Integer> nodeSet;
		int v;

		int iX, iY, dX, dY;
		int n;
		int pX, pY;
		GemP p, q;

		for (int i = 0; i < nodeCount; i++) {
			v = select();
			p = gemProp[v];

			pX = p.x;
			pY = p.y;

			n = (int) (a_shake * ELEN);
			iX = rand() % (2 * n + 1) - n;
			iY = rand() % (2 * n + 1) - n;
			iX += (centerX / nodeCount - pX) * p.mass * a_gravity;
			iY += (centerY / nodeCount - pY) * p.mass * a_gravity;

			for (int u = 0; u < nodeCount; u++) {
				q = gemProp[u];
				dX = pX - q.x;
				dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * ELENSQR / n;
					iY += dY * ELENSQR / n;
				}
			}
			nodeSet = adjacent[v].iterator();
			int u;
			while (nodeSet.hasNext()) {
				u = nodeSet.next().intValue();
				q = gemProp[u];
				dX = pX - q.x;
				dY = pY - q.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = (int) Math.min(n, MAXATTRACT);
				iX -= dX * n / ELENSQR;
				iY -= dY * n / ELENSQR;
			}
			displace(v, iX, iY);
			iteration++;
		}
	}

	private void arrange() {
		
		long stop_temperature;
		long stop_iteration;

		vertexdata_init(a_starttemp);

		oscillation = a_oscillation;
		rotation = a_rotation;
		maxtemp = (int) (a_maxtemp * ELEN);
		stop_temperature = (int) (a_finaltemp * a_finaltemp * ELENSQR * nodeCount);
		stop_iteration = a_maxiter * nodeCount * nodeCount;
		iteration = 0;

		// System.out.print( "arrange phase -- temp " );
		// System.out.print( stop_temperature + " iter ");
		// System.out.println ( stop_iteration );

		while (temperature > stop_temperature && iteration < stop_iteration) {
			// com.hp.hpl.guess.ui.StatusBar.setValue((int)stop_iteration,
			// (int)iteration);
			a_round();
		}
		// com.hp.hpl.guess.ui.StatusBar.setValue(100,0);
	}

	/*
	 * Optimisation Code
	 */

	private Point EVdistance(int thisNode, int thatNode, int v) {

		GemP thisGP = gemProp[thisNode];
		GemP thatGP = gemProp[thatNode];
		GemP nodeGP = gemProp[v];

		int aX = thisGP.x;
		int aY = thisGP.y;
		int bX = thatGP.x;
		int bY = thatGP.y;
		int cX = nodeGP.x;
		int cY = nodeGP.y;

		long m, n;

		bX -= aX;
		bY -= aY; /* b' = b - a */
		m = bX * (cX - aX) + bY * (cY - aY); /* m = <b'|c-a> = <b-a|c-a> */
		n = bX * bX + bY * bY; /* n = |b'|^2 = |b-a|^2 */
		if (m < 0) m = 0;
		if (m > n) m = n = 1;
		if ((m >> 17) > 0) { /* prevent integer overflow */
			n /= m >> 16;
			m /= m >> 16;
		}
		if (n != 0) {
			aX += (int) (bX * m / n); /* a' = m/n b' = a + m/n (b-a) */
			aY += (int) (bY * m / n);
		}
		
		return new Point(aX, aY);
	}

	private Point o_impulse(int v) {
		
		Iterator<E> edgeSet;
		int u, w;
		E e;
		int iX, iY, dX, dY;
		int n;
		GemP p, up, wp;
		int pX, pY;

		p = gemProp[v];
		pX = p.x;
		pY = p.y;

		n = (int) (o_shake * ELEN);
		iX = rand() % (2 * n + 1) - n;
		iY = rand() % (2 * n + 1) - n;
		iX += (centerX / nodeCount - pX) * p.mass * o_gravity;
		iY += (centerY / nodeCount - pY) * p.mass * o_gravity;

		edgeSet = edges.iterator();
		while (edgeSet.hasNext()) {
			e = edgeSet.next();
			u = nodeNumbers.get(graph.getSource(e));
			w = nodeNumbers.get(graph.getDest(e));
			if (u != v && w != v) {
				up = gemProp[u];
				wp = gemProp[w];
				dX = (up.x + wp.x) / 2 - pX;
				dY = (up.y + wp.y) / 2 - pY;
				n = dX * dX + dY * dY;
				if (n < 8 * ELENSQR) {
					Point dist = EVdistance(u, w, v); // source, dest, vert
					dX = dist.x;
					dY = dist.y;
					dX -= pX;
					dY -= pY;
					n = dX * dX + dY * dY;
				}
				if (n > 0) {
					iX -= dX * ELENSQR / n;
					iY -= dY * ELENSQR / n;
				}
			}
			else {
				if (u == v) u = w;
				up = gemProp[u];
				dX = pX - up.x;
				dY = pY - up.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, MAXATTRACT);
				iX -= dX * n / ELENSQR;
				iY -= dY * n / ELENSQR;
			}
		}
		return new Point(iX, iY);
	}

	private void o_round() {

		int v;
		for (int i = 0; i < nodeCount; i++) {
			v = select();
			Point impulse = o_impulse(v);
			displace(v, impulse.x, impulse.y);
			iteration++;
		}
	}

	private void optimize() {

		long stop_temperature;
		long stop_iteration;

		vertexdata_init(o_starttemp);
		oscillation = o_oscillation;
		rotation = o_rotation;
		maxtemp = (int) (o_maxtemp * ELEN);
		stop_temperature = (int) (o_finaltemp * o_finaltemp * ELENSQR * nodeCount);
		stop_iteration = o_maxiter * nodeCount * nodeCount;

		// System.out.print( "optimise phase -- temp " );
		// System.out.print( stop_temperature + " iter ");
		// System.out.println ( stop_iteration );

		while (temperature > stop_temperature && iteration < stop_iteration) {
			o_round();
			if ((iteration % 20000) == 0) {
				// System.out.println( iteration + "\t" + temperature );
			}
		}
	}

	/*
	 * Royere main layout method
	 */

	public void computePositions() {

		Integer nodeNr;
		V n;
		GemP p;

		graph = g;

		nodes = graph.getVertices();
		edges = graph.getEdges();

		nodeCount = nodes.size();
		edgeCount = edges.size();

		gemProp = new GemP[nodeCount];
		// invmap = new V[nodeCount];
		invmap = new ArrayList<V>(nodeCount);
		adjacent = new ArrayList[nodeCount];
		nodeNumbers = new HashMap<V, Integer>();

		Iterator<V> nodeSet = nodes.iterator();
		for (int i = 0; nodeSet.hasNext(); i++) {
			n = (V) nodeSet.next();
			// gemProp[i] = new GemP(n.getOutEdges().size());
			gemProp[i] = new GemP(graph.getNeighborCount(n));
			// invmap[i] = n;
			invmap.add(n);
			nodeNumbers.put(n, new Integer(i));
		}
		Iterator<V> neighbors;
		Collection<V> nset;
		for (int i = 0; i < nodeCount; i++) {
			nset = graph.getNeighbors(invmap.get(i));
			neighbors = nset.iterator();
			adjacent[i] = new ArrayList<Integer>(nset.size());
			while (neighbors.hasNext()) {
				n = (V) neighbors.next();
				nodeNr = (Integer) nodeNumbers.get(n);
				adjacent[i].add(nodeNr);
			}
		}

		if (i_finaltemp < i_starttemp) insert();
		if (a_finaltemp < a_starttemp) arrange();
		if (o_finaltemp < o_starttemp) optimize();

		for (int i = 0; i < nodeCount; i++) {
			p = gemProp[i];
			n = invmap.get(i);

			myDone.put(n, new Point2D.Double(p.x, p.y));

		}

		for (V v : myDone.keySet()) {
			Point2D.Double pt = myDone.get(v);
			setLocation(v, pt);
		}

		rescalePositions(0.08, 50, myDone);

		done = true;
	}

	private HashMap<V, Point2D.Double> myDone = new HashMap<V, Point2D.Double>();

	public boolean done = false;

	@Override
	public void initialize() {
		computePositions();
	}

	@Override
	public void reset() {
		computePositions();
	}

	/**
	 * Rescales the x and y coordinates of each node by percent.
	 * 
	 * @param nodes
	 *            the nodes to rescale.
	 */
	public void rescalePositions(double percent, int pad, Map<V, Point2D.Double> locations) {

		int nNodes = nodes.size();
		if (nNodes <= 1) {
			return;
		}

		double[] xPos = new double[nNodes];
		double[] yPos = new double[nNodes];
		ArrayList<V> nlist = new ArrayList<V>();

		double xMax = Double.MIN_VALUE;
		double yMax = Double.MIN_VALUE;
		double xMin = Double.MAX_VALUE;
		double yMin = Double.MAX_VALUE;

		Iterator<V> it = nodes.iterator();
		int i = 0;
		while (it.hasNext()) {
			nlist.add(it.next());
//			Point2D.Double c = (Point2D.Double) transform(nlist.get(i));
			Point2D.Double c = locations.get(nlist.get(i));
			
			xPos[i] = c.getX();
			yPos[i] = c.getY();
			xMax = Math.max(xMax, xPos[i]);
			yMax = Math.max(yMax, yPos[i]);
			xMin = Math.min(xMin, xPos[i]);
			yMin = Math.min(yMin, yPos[i]);
			i++;
		}

		double screenWidth = getSize().width;
		double screenHeight = getSize().height;
		int halfPad = pad / 2;

		double width = (xMax - xMin) * percent;
		double height = (yMax - yMin) * percent;

		if ((width == 0) || (height == 0)) {
			throw (new Error("can't rescale, width or height = 0"));
		}

		// rescale coords of nodes to fit inside frame, move to position
		for (i = 0; i < nNodes; i++) {
			xPos[i] = ((xPos[i] - xMin) / (xMax - xMin)) * (screenWidth - pad) + halfPad;
			yPos[i] = ((yPos[i] - yMin) / (yMax - yMin)) * (screenHeight - pad) + halfPad;

			setLocation(nlist.get(i), new Point2D.Double(xPos[i], yPos[i]));
//			locations.get(nlist.get(i)).setLocation(xPos[i], yPos[i]);

		}
	}
}

