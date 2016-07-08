package bijava.geometry.dim2;

 
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Diese Klasse implementiert einen Delaunay-Triangulator, der auf dem Bowyer-Watson-Algorithmus basiert.
 * 
 * Die Angabe einer HÃ¼lle ist a priori nicht erforderlich. Fuer die Triangulation groÃ¼er Punktmenge wird jedoch empfohlen, 
 * als Erstes die extremalen Punkte einzufÃ¼gen bzw. den Triangulator Ã¼ber triangulate zu erzeugen.
 * Das Entfernen von Delaunay-Knoten wird unterstÃ¼tzt.
 * Der Triangulator berechnet optional und inkrementell die Geometrie der Voronoi-Regionen.
 *  
 * CHECK: boundaryEntries wird nur fÃ¼r edgesInSight benÃ¶tigt. 
 * EdgesInSight spielt nur fÃ¼r Punkte, die auÃerhalb der konvexen HÃ¼lle hinzugefÃ¼gt werden, eine Rolle - also nicht im BATCH_MODUS.
 * Die stÃ¤ndige Aktualisierung des Array boundaryEntries kann also vom Batch_modus abhÃ¤ngig gemacht werden. 
 * Zur Vereinfachung des Quellcodes und unter Voraussetzung das die performance-relevante AusfÃ¼hrung immer im Batchmodus durchgefÃ¼hrt wird, 
 * sollte die Aktualisierung in eine Methode ausgelagert werden.
 *  
 * TODO
 * - Zwankskanten (aufwÃ¤ndig, da bislang kein Kantenmodell benutzt wurde)
 * - Umstellung auf Simplexe -> Vorbereitung fÃ¼r 3D
 * 
 * @author Kai Kaapke (Institut fuer Bauinformatik, Leibniz UniversitÃ¤t Hannover)
 * @param <E extends javax.vecmath.Point2d>
 */
public final class DelaunayTriangulator<E extends Point2d> {

	public static final boolean BUILD_REGIONS = false;
	public static final boolean TIMER = true;
	public static final int DEBUG = -1;
	private static final double EPSILON = 1.E-8;
	 
	/* Menge der Delaunay-Knoten */
	// TODO private
	protected TreeSet<Point2d> dPoints;

	/* Verzeichnis fÃ¼r Voronoi-Knoten, das assoziierte Delaunay-Dreieck sowie die Nachbar-Knoten */
	protected TSet<Entry> dir;

	/* Verzeichnis der Voronoi-Regionen */
	private Map<Point2d, VoronoiRegion> regionMap;

	/* Eine Folge der offenen Voronoi-Knoten */
	private List<Entry> boundaryEntries;
	private transient Point2d[] boundaryPolygon;
	private transient boolean boundaryModified;

	/* Internes flag, ob Punktmengen oder einzelne Punkte verarbeitet werden (fÃ¼r performance) */
	private transient boolean BATCH_MODE = false;

	/* Marker - Status besucht? */
	private transient Object stamp;
	
	/* Factories fÃ¼r Objekt-Recycling */
//	private transient EdgeFactory edgeFactory;
//	private transient EntryFactory entryFactory;
//	private transient DTriFactory dTriFactory;
//	private transient AdjVNFactory adjVNFactory;
	
	/* Diverse Puffer - Recycling der Collections Ã¼ber .clear zur Vermeidung von new */
	private transient Map<Point2d, Entry> helperMap;
	private transient List<Entry> newVoronoiNodes;
	private transient List<Entry> vq;
	private transient TreeSet<Point2d> dq;
	private transient List<VoronoiEdge> veq;

	private transient List<VoronoiEdge> edgesInSight;
	private transient ConcurrentLinkedQueue<Entry> candidates;
	private transient boolean qIsOutside; 			//  Merkt sich, ob ein hinzuzufÃ¼gender Punkt auÃ¼erhalb der HÃ¼lle liegt
	protected transient List<Point2d> startBox; 	// Ausgangsstruktur
	private transient ArrayList<E> tempStorage; 	// Punkte, die aufgrund einer KollinearitÃ¤t nachtrÃ¤glich eingefÃ¼gt werden mÃ¼ssen.
	

	/* Erzeugt einen DelaunayTriangulator  */
	public DelaunayTriangulator() {
		
		init();
		// Default-Gitter erzeugen
		dir = new ArrayGrid<Entry>();
	}

	/*
	 * Erzeugt einen DelaunayTriangulator
	 * @param rect vrstl. BoundingBox
	 * @param n    vrstl. Anzahl der Punkte
	 */
	public DelaunayTriangulator(final Rectangle2D rect, final int np) {

		init();

		// ZielgrÃ¶Ãe: durchschnittl. Anzahl Punkte pro Zelle
		int nppcell = 250;

		// Parameter: ZellhÃ¶he- und breite, Anzahl Zellen in x- und y-Richtung
		int ncellsy = (int)((np/nppcell) / (1.+(rect.getWidth()/rect.getHeight()))+1.); 
		int ncellsx = ncellsy * (int)((rect.getWidth()/rect.getHeight())+1.);

		System.out.println("BoundingRect:" + rect + ", np= " + np + ", ncellsx=" + ncellsx + ", ncellsy=" + ncellsy);

		dir = new ArrayGrid<Entry>(rect.getMinX(), rect.getMinY(), rect.getMaxX(),rect.getMaxY(), ncellsx, ncellsy);
	}

	private void init() {

		dPoints = new TreeSet<Point2d>(new PointComparator2d<Point2d>());
		boundaryEntries = new ArrayList<Entry>();
		regionMap = new HashMap<Point2d, VoronoiRegion>();
		
//		edgeFactory = new EdgeFactory();
//		entryFactory = new EntryFactory();
//		dTriFactory = new DTriFactory();
//		adjVNFactory = new AdjVNFactory();
		
		tempStorage = new ArrayList<E>();
		helperMap = new HashMap<Point2d, Entry>();
		newVoronoiNodes = new ArrayList<Entry>();
		vq = new ArrayList<Entry>(); 
		dq = new TreeSet<Point2d>(new PointComparator2d<Point2d>());
		veq = new ArrayList<VoronoiEdge>();
		edgesInSight = new ArrayList<VoronoiEdge>();
		candidates = new ConcurrentLinkedQueue<Entry>();
	}

	/** FÃ¼gt einen Punkt zur Triangulierung hinzu */
	public synchronized void addPoint(final E q) {
		add((Point2d) q);
	}

	/** Entfernt einen Punkt aus der Triangulierung */
	public synchronized void removePoint(final E q) {
		this.remove((Point2d)q);
	}

	/** Liefert die Menge der Delaunay-Knoten zurÃ¼ck */
	public synchronized Set<E> getDelaunayNodes() { 
		return (Set<E>)dPoints;
	}

	/** Liefert die Menge der Delaunay-Knoten zurÃ¼ck */
	public synchronized Iterator<Entry> getEntryIterator() { 
		return dir.iterator();
	}

	/** Liefert ein (minimal) umgebendes Rechteck zurÃ¼ck */
	public synchronized Rectangle2D getBoundingRect() { 

		if (dir.size() == 0) {
			return new Rectangle2D.Double(0., 0., 1., 1.);
		}

		double[] mins = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
		double[] maxs = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };

		// Werte das Randpolygon aus
		for (Point2d p : getBoundaryPolygon()) {
			if (p.x < mins[0]) {  mins[0] = p.x; }
			if (p.x > maxs[0]) {  maxs[0] = p.x; }
			if (p.y < mins[1]) {  mins[1] = p.y; }
			if (p.y > maxs[1]) {  maxs[1] = p.y; }
		}
		return new Rectangle2D.Double(mins[0], mins[1], (maxs[0]-mins[0]), (maxs[1]-mins[1]));
	}

	/** Gibt fÃ¼r jedes Dreieck der Triangulierung x1,y1,z1,x2,y2,z2,x3,y3,z3 zurÃ¼ck */
	public synchronized double[] getTriangleCoordinates() {

		int dim = 2;
		double[] result = new double[dir.size()*3*dim];
		int i=0;

		for (Entry e : dir) {
			for (Point2d p : e.dTri) {
				result[i++] = p.x;
				result[i++] = p.y;
			}
		}
		return result;
	}

	/** Gibt fÃ¼r jedes Dreieck der Triangulierung x1,y1,z1,x2,y2,z2,x3,y3,z3 zurÃ¼ck */
	public synchronized E[] getTriangles() {

		Point2d[] tris = new Point2d[dir.size()*3];
		int i=0;
		for (Entry e : dir) {
			for (Point2d p : e.dTri) {
				tris[i++] = p;
			}
		}
		return (E[]) tris;
	}
      
	/** Gibt die konvexe HÃ¼lle aller Delaunay-Knoten zurÃ¼ck */
	public synchronized Point2d[] getBoundaryPolygon() {

//		System.out.println("getBoundaryPolygon " + System.currentTimeMillis());

		if (boundaryPolygon == null || boundaryModified) { // && boundaryEntries.size() > 0) {
			ArrayList<Point2d> points = new ArrayList<Point2d>(boundaryEntries.size()+1);
			buildBoundaryPolygon(boundaryEntries.get(0), points);
			boundaryPolygon = points.toArray(new Point2d[points.size()]);
			boundaryModified = false;
		} 
		return boundaryPolygon;
	}

	/** Gibt den zu q nÃ¤chsten Delaunay-Knoten zurÃ¼ck */
	public synchronized E getClosestDelaunayPoint(final E q) {

		Point2d result = null; 
		double minDist = Double.POSITIVE_INFINITY;

		// Suche den nÃ¤chsten Voronoi-Knoten Vq zu q
		Entry nearestVN = dir.getNearestElement(q);

		// Suche ein Dreieck, das q enthÃ¤lt
		getEdgesInSight(q);
		qIsOutside = (edgesInSight.size() > 0);
//		edgeFactory.release(); // KK3

		if (!qIsOutside) {
			while (nearestVN != Entry.INFINITY && nearestVN.perimeterContains(q) < 0) {
				for (int i=0; i < 3; i++) {
					if (isPointInSemiPlane(nearestVN.dTri[(i+1)%3], nearestVN.dTri[(i+2)%3], q)) {
						nearestVN = nearestVN.adjVN[i];
						break;
					}
				}
			}
			for (Point2d tmp : nearestVN.dTri) {
				double d2 = d2(tmp, q);
				if (d2 < minDist) {
					minDist = d2;
					result = tmp;
				}
			}

		} else {
			for (Point2d bp : getBoundaryPolygon()) {
				double d2 = d2(bp, q);
				if (d2 < minDist) {
					minDist = d2;
					result = bp;
				}
			}
		}

		return (E)result;
	}

	public VoronoiRegion getRegion(Point2d p) {
		return this.regionMap.get(p);
	}
	
	/** Gibt einen Iterator Ã¼ber alle VoronoiRegionen zurÃ¼ck */
	public synchronized Iterator<VoronoiRegion> getVoronoiIterator() {
		if (regionMap.size() == 0) {
//			buildRegionMap(); // TODO
		}
		return regionMap.values().iterator();
	}

	/** TODO
	 *  Weitere API-Methoden:
	 *  - getVoronoiDiagram() : Graph
	 *  - getConnectedTriangles(Point2d delaunayNode): Triangle[]
	 *  - getAdjacentTriangles(vNode)
	 *  - iterator(): Iterator<Triangle2d>
	 *  - addEdge(Point2d p1, Point2d p2) // Zwangskanten
	 */

	protected static double d(Point2d a, Point2d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx*dx+dy*dy);
	}

	protected static double d2(Point2d a, Point2d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return dx*dx+dy*dy;
	}

	/* Fuegt einen Delaunay-Punkt hinzu und aktualisiert die Zerlegung 
	 *  Gibt eine Liste neuer Voronoi-Knoten zurueck.
	 */
	private List<Entry> add(final Point2d q) {

//		if (TIMER) Timer.startTimer("add");

		int n = dPoints.size();

		if (DEBUG > -1)
			System.out.println(n + ".: Adding " + q); 

		if (dPoints.contains(q)) {
			System.out.println("Warning: Point " + q + " is already in the set.");
			return new ArrayList<Entry>(0);
		}

		/* SONDERFALL: Weniger als drei Delaunay-Knoten */
		if (n < 3) {
			dPoints.add(q); 
			if (dPoints.size() == 3) {
				createFirstEntry();
			}
			return new ArrayList<Entry>(0);
		}

		/* 
		 * (0) ÃberprÃ¼fe, ob der neue Punkt auÃerhalb der konvexen HÃ¼lle liegt.
		 *     Im Batch-Modus wird zu Beginn eine genÃ¼gend groÃe HÃ¼lle erzeugt, 
		 *     so dass kein Punkt mehr auÃerhalb liegen kann. 
		 *     D.h. die ÃberprÃ¼fung kann zur Verbesserung der Performance entfallen.
		 */
		if (BATCH_MODE && n > 4) {
			qIsOutside = false;
		} else {
//			if (TIMER) Timer.startTimer("edgesInSight");
			getEdgesInSight(q);
			qIsOutside = (edgesInSight.size() > 0);
//			if (TIMER) Timer.stopTimer("edgesInSight");
		}

		/* (1) Ermittle die Voronoi-Knoten Vq, deren Umkreise den neuen Punkt q enthalten, 
		 *     und die zugehÃ¶rigen Delaunay-Punkte Dq.
		 */
//		if (TIMER) Timer.startTimer("computeAffectedNodes");
		computeAffectedNodes(q); 	
//		if (TIMER) Timer.stopTimer("computeAffectedNodes");

		/* (2) Bestimme die betroffenen Voronoi-Kanten, VEq, auf denen jeweils eine neue Ecken gefunden werden muss. 
		 *     Zu jeder Voronoi-Kante in VEq wird die duale Delaunay-Kante DEq ermittelt und in der VoronoiEdge abgelegt.
		 */
//		if (TIMER) Timer.startTimer("computeAffectedEdges");
		computeAffectedEdges();
//		if (TIMER) Timer.stopTimer("computeAffectedEdges");

		/* (3) Jede betroffene Delaunay-Kante bildet mit q ein Dreieck, 
		 *     dessen Umkreismittelpunkt einen neue Voronoi-Knoten darstellt. 
		 *     Nachfolgend werden diese Voronoi-Knoten ermittelt und 
		 * (4) entsprechend verknÃ¼pft.
		 */
//		if (TIMER) Timer.startTimer("newVoronoiNodes");
		newVoronoiNodes(q);
//		if (TIMER) Timer.stopTimer("newVoronoiNodes");

		// Den Punkt zur Menge der Delaunay-Punkte hinzufÃ¼gen.
		dPoints.add(q);

		/* (5) Behandlung des Sonderfalls, dass der neue Punkt auÃerhalb 
		 *    der konvexen HÃ¼lle aller Delaunay-Knoten und in keinem Umkreis liegt. 
		 */
		if (qIsOutside) {
//			if (TIMER) Timer.startTimer("handleNewPointsOutsideConvexHull");
			handleNewPointsOutsideConvexHull(q); //, dq, newVoronoiNodes);
//			if (TIMER) Timer.stopTimer("handleNewPointsOutsideConvexHull");
		}

		/* 
		 * (6) Die Liste der Rand-Voronoi-Knoten aktualisieren
		 */ 
//		if (TIMER) Timer.startTimer("boundary");
		for(Entry e : newVoronoiNodes) {
			if (e.isBoundaryEntry() > -1) {
				boundaryEntries.add(e);
				boundaryModified = true;
			}
		}
//		if (TIMER) Timer.stopTimer("boundary");

		/* 
		 * (7) Aktualisierung der Voronoi-Regionen
		 */
		if (BUILD_REGIONS) {
//			if (TIMER) Timer.startTimer("region");
			updateRegionMap(q); 
			if (DEBUG > 1)
				System.out.println("REGIONMAP: \n" + regionMap.values());
//			if (TIMER) Timer.stopTimer("region");
		}
		
		// Die verwendeten Edges wieder zurÃ¼ckgeben
//		edgeFactory.release(); // KK3
		
		if (DEBUG > -1) { 
			System.out.println("*********************************"); 
			boolean checkResult = checkConsistency(System.out);
			if (!checkResult && DEBUG > 0) {
				System.out.println("Directory: \n" + dir); 
			}
			System.out.println("Konsistenz: " + checkResult);
			System.out.println("*********************************"); 
		}

		if (DEBUG > 3) {
			System.out.println("...added. \nNew directory: \n" + dir);    
			System.out.println("Boundary:" + Arrays.toString(getBoundaryPolygon()) + "\n");
		}

//		if (TIMER) Timer.stopTimer("add");

		return newVoronoiNodes;
	}                         

	/* 
	 * Step 1: Diese Methode sucht nach Voronoi-Knoten, die naechste Nachbarn des neuen Punktes q sind.
	 * 
	 * @param q - der einzufuegende Delaunay-Knoten
	 * @param vq List<Entry> fÃ¼r betroffene Voronoi-Knoten
	 * @param dq TreeSet<Point2d> fÃ¼r betroffene Delaunay-Knoten
	 */
	private void computeAffectedNodes(final Point2d q) {
		computeAffectedNodes(q, vq, dq);
	}

	private void computeAffectedNodes(final Point2d q, final List<Entry> vq, final TreeSet<Point2d> dq) {

		/* Aktualisiere den Zeitstempel (Besucht-Marke) */
		stamp = new Object();
		Entry.INFINITY.version = stamp;

		vq.clear();
		dq.clear();   // keine Duplikate (d.h. keine koordinatengleichen Punkte)
		candidates.clear();

		// Falls q auÃerhalb liegt, sind die sichtbaren Rand-Dreiecke Kandidaten fÃ¼r betroffene Dreiecke
		for (VoronoiEdge edge : edgesInSight) {  // qIsOutside
			Entry pivotVN = edge.vn1; 			 // pivotVN = edge.marked // getEdgesInSight markiert den NICHT-INFINITY Voronoi-Knoten
			if (edge.vn1 == Entry.INFINITY) {
				pivotVN = edge.vn2;
			}
			candidates.add(pivotVN);
		}

		// Suche den nÃ¤chsten Voronoi-Knoten Vq zu q (Das Dreieck muss q nicht zwangslaeufig enthalten! Test7)
//		Timer.startTimer("getNearest");
		Entry nearestVN = dir.getNearestElement(q);
//		Timer.stopTimer("getNearest");
		if (DEBUG > 2) System.out.println(" computeAffectedNodes: nearestVN= " + nearestVN);

		// Ã¼berprÃ¼fe, von nearestVN ausgehend, die Nachbarn, ob sie von q betroffen sind.
		if (!candidates.contains(nearestVN))
			candidates.add(nearestVN);

		checkVoronoPoint2ds(q, vq, dq);
	}

	/* 
	 *  Diese Methode Ã¼berprÃ¼ft das Umkreiskriterium fuer Voronoi-Knoten, die naheste Nachbarn des neuen Punktes q sind.
	 *  
	 *  Annahme: Sobald ein Dreieck gefunden wurde, in dessen Umkreis der neue Punkt q faellt,
	 *  koennen nur noch die Umkreise von Nachbardreiecken diesen Punkt ebenfalls enthalten.
	 *  Diese Annahme ist zu noch zu verifizieren.
	 */
	private void checkVoronoPoint2ds(final Point2d q, final List<Entry> vq, final TreeSet<Point2d> dq) {             

//		System.out.println("Ausgangs-Kandidatenmenge: " + candidates.toString());

		Entry pivotVN = null;
		while ((pivotVN = candidates.poll()) != null) {
			pivotVN.version = stamp;
			boolean isIn = pivotVN.perimeterContains(q) > 0;
//			System.out.println(" check pivot Entry @" + Integer.toHexString(pivotVN.hashCode()) + ": " + Arrays.toString(pivotVN.dTri) + ", isIn=" + isIn);
			if (isIn) {
				vq.add(pivotVN);
				dq.add(pivotVN.dTri[0]);
				dq.add(pivotVN.dTri[1]);
				dq.add(pivotVN.dTri[2]);
			}
			if (isIn || vq.isEmpty()) {
				for (Entry neighVN : pivotVN.adjVN) {
					if (neighVN.version != stamp && !candidates.contains(neighVN)) {  // contains-Check ist notwendig, da zwei noch nicht besuchte Dreiecke den gleichen Nachbarn haben kÃ¼nnten und die Queue Elemente mehrfach enthalten kÃ¼nnte.
						candidates.add(neighVN);
					}
				}
			}
		}
	}

	/*
	 * Step 2: Derive affected edges in Delaunay and Voronoi diagram 
	 * 
	 * Jeder zu loeschenden Ecke in Vq sind drei assoziierte Knotenpaare, 
	 * die jeweils ein Kante des Delaunay-Diagramms darstellen, und 
	 * drei zugehoerige Eckenpaare, die jeweils eine Strecke des Voronoi-Diagramms darstellen und
	 * senkrecht auf einer Kante stehen, zugeordnet.
	 * 
	 * Diejenigen Strecken (Voronoikanten), die genau eine zu loeschende Ecke besitzen, 
	 * werden in der Folge VEq zusammengefasst. 
	 * Due zugehoerigen (Delaunay-)Kanten werden in der Folge DEq zusammengefasst.  
	 * 
	 * @param ArrayList<Entry> vq
	 * @return List<VoronoiEdge> VEq und implizit DEq
	 */
	private List<VoronoiEdge> computeAffectedEdges() {  // final  List<Entry> vq

		// Durch das Hinzufuegen von q wird eine neue Region gebildet, die die zu loeschenden vq enthÃ¼lt.
		// Die Voronoi-Knoten, die eine Kante mit den Vq hatten, bekommen einen neuen Partner. 
		// Nachfolgend werden diese Kanten ermittelt.
		veq.clear();

		// Laufe ueber alle betroffenen (spaeter zu loeschenden) Voronoi-Knoten
		for (Entry e1 : vq) {
			// Laufe ueber die adjazenten Voronoi-Knoten.
			for (int i=0; i < 3; i++) {
				Entry e2 = e1.adjVN[i];

				// Sofern der Nachbar nicht auch entfernt wird, erzeuge eine entsprechende Kante
				if (!vq.contains(e2)) {
					VoronoiEdge edge = new VoronoiEdge(e1, e2); // KK3
//					VoronoiEdge edge = edgeFactory.bind(e1, e2); 

					// Markiere den Voronoi-Knoten der Kante, der spaeter geloescht wird.
					edge.setMarked(e1);

					// Ermittle die assoziierte Delaunay-Kante. Umlaufsinn: mathematisch positiv; der zu loeschende Voronoi-Knoten liegt links von der Delaunay-Kante
					edge.dn1 = e1.dTri[(i+1)%3];
					edge.dn2 = e1.dTri[(i+2)%3];

					veq.add(edge);
					if (DEBUG > 2)  System.out.println("  Affected edge: " + edge); 
				}
			}

			// Entferne den Eintrag zum Voronoi-Knoten aus dem Verzeichnis
			dir.remove(e1);
			boundaryEntries.remove(e1);
//			entryFactory.release(e1); // KK3
		}

		return veq;
	}

	/*
	 * Step 3: Calculate the new voronoi edges and add them accordingly to the directory
	 * 
	 * Der einzufuegende Knoten q bildet mit jeder Delaunay-Kante (pi,pj) aus Eq ein Dreieck (q, pi, pj).
	 * Die Umkreismittelpunkte dieser Dreicke bilden die neuen Ecken im Voronoi-Diagramm.
	 * 
	 * Besitzen zwei Dreiecke einen gemeinsamen Knoten p, dann besitzen sie eine gemeinsame Kante (p,q)
	 * und folglich sind die zu den beiden Dreicken gehÃ¼renden neuen Ecken benachbart.
	 * 
	 * @param q - der einzufuegende Delaunay-Punkt
	 * @param veq - die Menge der beeinflussten Voronoi-Kanten VEq und implizit die assoziierten Delaunay-Kanten DEq
	 * @return Entry - die neuen Voronoi-Ecken
	 */
	private List<Entry> newVoronoiNodes(final Point2d q) {   // , final TreeSet<Point2d> dq, final List<VoronoiEdge> veq

		newVoronoiNodes.clear();

		// Diese Map hilft beim Finden der Nachbarschaften der neuen Dreiecke
		helperMap.clear();

		double area;

		for (VoronoiEdge ve : veq) {

			if (DEBUG > 2)
				System.out.println("Erzeuge neues Dreieck mit Kante: (" + ve.dn1.x + ", " + ve.dn1.y + ")-(" + ve.dn2.x + ", " + ve.dn2.y + ")"); 

			// Jede Delaunay-Kante in veq bildet mit q ein Dreieck, ...
			Point2d[] dTri = new Point2d[] { q, ve.dn1, ve.dn2 }; // KK3
//			Point2d[] dTri = dTriFactory.bind(q, ve.dn1, ve.dn2);

			// Orientierung sicherstellen
			area = area(dTri[0], dTri[1], dTri[2]);

			// Sonderfall: Der neue Punkt q liegt genau auf dem Rand
//			if (Math.abs(area) <= EPSILON * Math.min(Math.min(d2(dTri[0], dTri[1]), d2(dTri[1], dTri[2])), d2(dTri[2], dTri[0]))) {
			if (Math.abs(area) < EPSILON) {
				if (DEBUG > -1) System.out.println(" q liegt auf dem Rand area = " + area);
				// Da mit der aktuellen Kante kein Dreieck gebildet werden kann, 
				// muss sie nicht weiter verarbeitet werden.
				continue;
			}
			if (area < 0) {
//				dTriFactory.release(dTri);
				dTri = new Point2d[] { q, ve.dn2, ve.dn1 }; // KK3
//				dTri = dTriFactory.bind(q, ve.dn2, ve.dn1);
			}

			// ...dessen Umkreismittelpunkt ist der neue Voronoi-Knoten auf der Voronoi-Kante ve.
			// TODO: Hier ggf. z-Wert fÃ¼r newPoint interpolieren !
			Point2d newPoint = Entry.getPerimeterCenter(dTri);   

			if (DEBUG > 2) System.out.println("newVoronoiNode: (" + newPoint.x + ", " + newPoint.y +")");

			/* Sonderfallbehandlung: ÃberprÃ¼fe, ob der Umkreis des neuen Dreiecks dTri
			 * einen anderen betroffenen Delaunay-Knoten enthÃ¤lt.
			 * (Ist nur notwendig, sofern q auÃerhalb der konvexen HÃ¼lle liegt.)
			 */
			if (qIsOutside && isAffected(q, dq, ve, newPoint)) {
				if (DEBUG > 1) System.out.println(" Sonderfall (newVoronoiNodes) - qIsOutside && isAffected ....");
				continue;
			}

			// Ein Nachbar des Voronoi-Knoten ist durch den nicht zu lÃ¶schenden Voronoi-Knoten der Kante gegeben, ...
			Entry[] adjVN = { ve.unmarked, Entry.INFINITY, Entry.INFINITY }; // KK3
//			Entry[] adjVN = adjVNFactory.bind(ve.unmarked, Entry.INFINITY, Entry.INFINITY);
			
			// ... die anderen Nachbar-Knoten kÃ¶nnen erst im weiteren Verlauf ermittelt werden (siehe Nachbarschaftssuche).
			Entry newEntry = new Entry(newPoint, dTri, adjVN); // KK3
//			Entry newEntry = entryFactory.bind(newPoint, dTri, adjVN);

			// Neuen Voronoi-Knoten ins Verzeichnis einfÃ¼gen
			dir.add(newEntry); 

			/* Nachbarschaftssuche
			 * Ã¼ber die lokale Map werden bereits eingefÃ¼gte Entries gecacht, 
			 * damit die Nachbarschaften der Voronoi-Knoten gesetzt werden kÃ¼nnen.
			 * Die Indexierung ist absolut, da die verbleibende Ecke (ve.unmarked) immer links der Delauny-Kante (ve.dn1,ve.dn2) liegt.
			 */
			Entry neighEntry;
			if ((neighEntry = helperMap.put(ve.dn1, newEntry)) != null) {
				newEntry.adjVN[2] = neighEntry;
				neighEntry.adjVN[1] = newEntry;
			}
			if ((neighEntry = helperMap.put(ve.dn2, newEntry)) != null) {
				newEntry.adjVN[1] = neighEntry;
				neighEntry.adjVN[2] = newEntry;
			}

			// Suche beim stehenden bleibenden Voronoi-Knoten den Index des Nachbarn, der ersetzt werden soll.
			for (int i=0; i < 3; i++)  {
				if (adjVN[0] != Entry.INFINITY && adjVN[0].adjVN[i] == ve.marked) {
					// Tausche den zu lÃ¼schenden Voronoi-Knoten durch den neuen Knoten aus.
					adjVN[0].adjVN[i] = newEntry;
				}
			}
			newVoronoiNodes.add(newEntry);
		}
		return newVoronoiNodes;
	}

	/*
	 * Die Methode Ã¼berprÃ¼ft, ob der Umkreis um das neu gebildete Dreieck, dessen Mittelpunkt newVN ist,
	 * einen anderen Delaunay-Knoten enthÃ¤lt. Die drei Punkte des neuen Dreiecks sind: 
	 * Die beiden Punkte der Kante ve.dn1-ve.dn2 sowie der eingefÃ¼gte Punkt q.
	 */
	private boolean isAffected(final Point2d q, final TreeSet<Point2d> dq, final VoronoiEdge ve, final Point2d newVN) {

		double d = 0, critD = (newVN.x - ve.dn1.x) * (newVN.x - ve.dn1.x) + (newVN.y - ve.dn1.y) * (newVN.y - ve.dn1.y);

		for (Point2d dPoint : dq) {
			if (dPoint == ve.dn1 || dPoint == ve.dn2) 
				continue;
			d = (newVN.x - dPoint.x) * (newVN.x - dPoint.x) + (newVN.y - dPoint.y) * (newVN.y - dPoint.y);
			if (d < critD) {
				if (DEBUG > 1) System.out.println(" Beim Versuch, das Dreieck (" + ve.dn1 + "," + ve.dn2 + "," + q + ") anzulegen, ist aufgefallen, dass der D-Knoten " + dPoint + " im Umkreis liegt.");
				return true;
			}
		}
		return false;
	}

	/* FÃ¼r add(E):
	 * Die Methode erstellt den ersten Eintrag im Verzeichnis, 
	 * sobald drei Punkte fÃ¼r ein Dreieck vorhanden sind. 
	 */
	private Entry createFirstEntry() {

		Point2d[] dTri = dPoints.toArray(new Point2d[3]);
		double area = area(dTri[0], dTri[1], dTri[2]);

		// Sonderfall: Es sind zwar drei Punkte in der Menge, diese sind aber kollinear:
		// Behandlung:   
		if (area == 0.) {
			Point2d p = dPoints.last();
			tempStorage.add((E) p);
			dPoints.remove(p);
			return null;

		} else if (area < 0.) {
			dTri = new Point2d[] { dTri[0], dTri[2], dTri[1] };
		}

		// ...dessen Umkreismittelpunkt ist der neue Voronoi-Knoten auf der Voronoi-Kante ve.
		Point2d newPoint = Entry.getPerimeterCenter(dTri);    

		// Ein Nachbar des Voronoi-Knoten ist durch den nicht zu lÃ¶schenden Voronoi-Knoten der Kante gegeben, ...
		Entry[] adjVN = { Entry.INFINITY, Entry.INFINITY, Entry.INFINITY };

		// ... die anderen Nachbar-Knoten kÃ¼nnen erst ermittelt werden, wenn alle neuen Dreiecke gebildet wurden.
		Entry newEntry = new Entry(newPoint, dTri, adjVN); // KK3
//		Entry newEntry = entryFactory.bind(newPoint, dTri, adjVN);
		
		// zum Verzeichnis hinzufÃ¼gen
		dir.add(newEntry);
		boundaryEntries.add(newEntry);

		if (DEBUG > 2) {
			System.out.println("First entry created:" + newEntry);
			System.out.println("Boundary:" + boundaryEntries);
		}

		if (BUILD_REGIONS) {
			for (int i=0; i < dTri.length; i++) {
				Point2d p = dTri[i];
				VoronoiRegion vr = new VoronoiRegion();
				vr.dNode = p;

				VoronoiEdge edge1 = new VoronoiEdge(Entry.INFINITY, newEntry); 
				edge1.dn2 = p;
				edge1.dn1 = dTri[(i+1)%3];

				VoronoiEdge edge2 = new VoronoiEdge(newEntry, Entry.INFINITY);
				edge2.dn2 = p;
				edge2.dn1 = dTri[(i+2)%3];

				vr.bisectors.add(edge1);
				vr.bisectors.add(edge2);

				regionMap.put(p, vr);
			}
			if (DEBUG > 2)
				System.out.println("First Entry: REGIONMAP: \n" + regionMap.values());
		}

		for (E p : tempStorage) {
			if (DEBUG > 2) System.out.println("Verarbeite Punkt aus createFirstEntry-: " + p + " --------------------");
			this.addPoint(p);
		}

		tempStorage.clear();

		if (DEBUG > 2) 
			System.out.println("createFirstEntry - Ende --------------------\n\n");

		return newEntry;
	}

	/* SONDERFALL: Falls der neue Delaunay-Punkt ausserhalb der konvexen HÃ¼lle 
	 *             der Delaunay-Punkt liegt, ...
	 */
	private void handleNewPointsOutsideConvexHull(final Point2d q) {  // , final TreeSet<Point2d> dq, final List<Entry> newVoronoiNodes

		// Ã¼berprÃ¼fe, welche Kanten vom neuen Delaunay-Punkt aus sichtbar sind. MUSS NEU BERECHNET WERDEN!
		getEdgesInSight(q);

		for (VoronoiEdge ve : edgesInSight) {

			// Jede Delaunay-Kante in veq bildet mit q ein Dreieck, ...
			Point2d[] dTri = new Point2d[] { q, ve.dn1, ve.dn2 };
			if (area(dTri[0], dTri[1], dTri[2]) < 0.) {
				dTri = new Point2d[] { q, ve.dn2, ve.dn1 };
			}
			dq.add(dTri[1]);
			dq.add(dTri[2]);

			Entry[] adjVN = { ve.unmarked, Entry.INFINITY, Entry.INFINITY };
			Entry newEntry = new Entry(Entry.getPerimeterCenter(dTri), dTri, adjVN); // KK3
//			Entry newEntry = entryFactory.bind(Entry.getPerimeterCenter(dTri), dTri, adjVN);
			
			// Setze die Nachbarschaft zum Dreieck mit der sichtbaren Kante
			int idx = 0;
			for (int i=0; i<3; i++) {
				if (ve.unmarked.dTri[i].equals(ve.dn1) || ve.unmarked.dTri[i].equals(ve.dn2)) {
					idx += i;
				}
			}
			ve.unmarked.adjVN[3-idx] = newEntry;

			if (ve.unmarked.isBoundaryEntry() == -1) {
//				System.out.println("add(vq=0):  boundaryEntrie removed1: " + ve.unmarked);
				//System.out.println("add(vq=0):  boundaryEntrie removed1: " + Arrays.toString(ve.unmarked.dTri));
				boundaryEntries.remove(ve.unmarked);
			}

			// Ã¼berprÃ¼fe, mit welchen neuen Dreiecken dieser Eintrag benachbart ist.
			for (Entry e : newVoronoiNodes) {
				idx = -1;
				// q hat in neuen Entries immer den Index 0.
				// Um den Index in adjVN bestimmen zu kÃ¼nnen, 
				// fehlt noch der Index des anderen Delaunay-Knotens (der gemeinsamen Kante)
				if (e.dTri[1] == ve.dn1 || e.dTri[1] == ve.dn2) {
					idx = 1;
				} else if (e.dTri[2] == ve.dn1 || e.dTri[2] == ve.dn2) {
					idx = 2;
				}
				if (idx > 0) {
					e.adjVN[3-idx] = newEntry;

					if (e.dTri[idx] == dTri[1])
						newEntry.adjVN[2] = e;
					else if(e.dTri[idx] == dTri[2]) {
						newEntry.adjVN[1] = e;
					}
					if (e.isBoundaryEntry() == -1) {
//						System.out.println("add(vq=0):  boundaryEntrie removed2: " + Arrays.toString(e.dTri));
						boundaryEntries.remove(e);
					}
				}
			}

			// FÃ¼ge den neuen Eintrag ins Verzeichnis
			dir.add(newEntry); 
			newVoronoiNodes.add(newEntry);
		}
	}

	/* 
	 * Ermittelt alle von q aus sichtbaren Delaunay-Randkanten und 
	 * gibt diese zusammen mit den Voronoi-Kanten zurÃ¼ck.
	 */
	private List<VoronoiEdge> getEdgesInSight(final Point2d q) {

		edgesInSight.clear();
//		System.out.println(" getEdgesInSight (no of boundaryEntries=" + boundaryEntries.size() + ")...");

		for (Entry entry : boundaryEntries) {
			for (int i=0; i < 3; i++) {
				if (entry.adjVN[i] == Entry.INFINITY) {
//					System.out.print("  Testing " + entry.dTri[(i+1)%3] + " - " + entry.dTri[(i+2)%3] +"  =");
					if (isPointInSemiPlane(entry.dTri[(i+1)%3], entry.dTri[(i+2)%3], q)) {
//						System.out.println(" visible");
						VoronoiEdge edge = new VoronoiEdge(entry, entry.adjVN[i]); 
//						VoronoiEdge edge = edgeFactory.bind(entry, entry.adjVN[i]); 
						edge.dn1 = entry.dTri[(i+1)%3];
						edge.dn2 = entry.dTri[(i+2)%3];
						edge.setMarked(entry.adjVN[i]);
						edgesInSight.add(edge);
					} else {
//						System.out.println(" NO: " + area(entry.dTri[(i+1)%3], entry.dTri[(i+2)%3], q));
					}
				}
			}
		}
		return edgesInSight;
	}

	private boolean isPointInSemiPlane(final Point2d d1, final Point2d d2, final Point2d q) {
		return area(d1, d2, q) < 0;
	}

	private void updateRegionMap(final Point2d q) { //, final TreeSet<Point2d> dq, final List<Entry> newVNodes) {

		if (newVoronoiNodes.size() == 0) {
			return;
		}

		// Erstelle eine Voronoi-Region fÃ¼r den Delaunay-Knoten q
		VoronoiRegion vr = createVoronoiRegion(q, newVoronoiNodes.get(0));
		regionMap.put(q, vr);
		updateNeighbourRegions(); // (dq);
	}

	/** 
	 * VR
	 * @param E q	Der neue Delaunay-Knoten
	 * @param Entry start ein Voronoi-Knoten, der aus dem EinfÃ¼gen von q resultiert
	 * @return VoronoiRegion zum Knoten q (ohne Nachbarn)
	 */
	private VoronoiRegion createVoronoiRegion(final Point2d q, final Entry start) {

		if (DEBUG > 2) System.out.println("createVoronoiRegion: " + q + ", start at:  \n " + start);

		Entry neigh = null, pivot = start;
		VoronoiEdge edge = null;
		boolean flag = true;
		int idxOfQ = -1;
		double area = 0.;

		// Aktualisiere den Zeitstempel (Besucht-Marke)
		stamp = new Object();

		// Anlegen der Voronoi-Region
		// Die Ecken der Region laufen im Uhrzeigersinn um den Center
		VoronoiRegion vr = new VoronoiRegion();
		vr.dNode = q;

		// Schritt 1: RÃ¼ckwÃ¤rtslauf: Suche den ersten Voronoi-Knoten der Region.
		//
		// Es ist nicht bekannt, ob die Region offen ist. Offene Regionen haben eine erste und eine letzte Ecke (offener Ring).
		// Die Startecke kann irgendwo auf dem Ring liegen.
		//
		// Gesucht wird in jedem Schritt, ausgehend von der pivot-Ecke, 
		// die nÃ¤chste Ecke des Rings			
		//   Die nÃ¤chste Ecke ist Nachbar der aktuellen Ecke und
		//   das Nachbardreieck muss mit q gebildet werden und 
		//   q muss links der Geraden von der aktuellen zur nÃ¤chste Ecke liegen (links, da RÃ¼ckwÃ¤rtslauf)
		// Die Suche wird abgebrochen, wenn
		//   entweder der Startknoten wieder erreicht wurde (geschlossene Region, Schritt 2 nicht mehr erforderlich)
		//   oder wenn der erste Knoten des offenen Rings gefunden wurde.
		//     Der erste Knoten wurde gefunden, wenn von der pivot-Ecke aus 
		//     kein endlicher Nachbar gefunden werden kann (flag wird nicht auf true gesetzt)
		do {
			flag = false;
			for (int i=0; i < pivot.adjVN.length; i++) {
				neigh = pivot.adjVN[i];
				// System.out.println(" \nneigh = " + neigh.vNode);
				if (neigh != Entry.INFINITY && (idxOfQ = neigh.indexOf(q)) > -1) { 
					if (((area = area(pivot, neigh, q)) < 0) 
							// Sonderfall degenerierte Voronoi-Ecke: RÃ¼ckweg ausschlieÃen (Ausnutzen, dass es nur einen VorgÃ¤nger und einen Nachfolger geben kann)
							|| (area == 0. && pivot.dTri[i] != pivot.dTri[(pivot.indexOf(q)+1)%3])) {

						edge = new VoronoiEdge(neigh, pivot);
						edge.dn1 = neigh.dTri[idxOfQ];
						edge.dn2 = neigh.dTri[(idxOfQ+2)%3];
						vr.insertBisector(edge);
						if (DEBUG > 2) System.out.println("  Erzeuge Kante (1.A): " + edge);

						pivot = neigh;
						flag = true;
						break;
					}
				}
			}
		} while (pivot != start && flag);

		// Schritt 2: VorwÃ¤rtslauf bei offenen Regionen
		//
		// Falls es sich um eine offene Voronoi-Region handelt, 
		// ist die erste Ecke im RÃ¼ckwÃ¤rtslauf oben bestimmt worden und 
		// der offene Ring kann nun im VorwÃ¤rtslauf bestimmt werden.
		if (!flag) {
			// Kante aus dem Unendlichen zur ersten Ecke eingefÃ¼gen
			edge = new VoronoiEdge(Entry.INFINITY, pivot);
			idxOfQ = pivot.indexOf(q);
			edge.dn1 = pivot.dTri[idxOfQ];
			edge.dn2 = pivot.dTri[(idxOfQ+1)%3];
			vr.insertBisector(edge);
			if (DEBUG > 2) System.out.println(" Erzeuge Kante (2.A): " + edge);

			// Vorspringen auf start - Der Teilring zwischen erster Ecke und Start-Ecke wurde im RÃ¼cklauf bereits zur Region hinzugefÃ¼gt.
			pivot = start;

			// VorwÃ¤rtslauf
			do {
				flag = false;
				for (int i=0; i < pivot.adjVN.length; i++) {
					neigh = pivot.adjVN[i];
					if (neigh != Entry.INFINITY && (idxOfQ = neigh.indexOf(q)) > -1) { 
						if (((area = area(pivot, neigh, q)) > 0) 
								// Sonderfall degenerierte Voronoi-Ecke: RÃ¼ckweg ausschlieÃen (Ausnutzen, dass es nur einen VorgÃ¤nger und einen Nachfolger geben kann)
								|| (area == 0. && pivot.dTri[i] == pivot.dTri[(pivot.indexOf(q)+1)%3])) {

							edge = new VoronoiEdge(pivot, neigh);
							edge.dn1 = neigh.dTri[idxOfQ];
							edge.dn2 = neigh.dTri[(idxOfQ+1)%3];
							vr.insertBisector(edge);
							if (DEBUG > 2) System.out.println("  Erzeuge Kante (2.B): " + edge);

							pivot = neigh;
							flag = true;
							break;
						}
					}
				} 
			} while (flag);

			// Kante von der letzten Ecke ins Unendliche eingefÃ¼gen
			edge = new VoronoiEdge(pivot, Entry.INFINITY);
			idxOfQ = pivot.indexOf(q);
			edge.dn1 = pivot.dTri[(idxOfQ)];
			edge.dn2 = pivot.dTri[(idxOfQ+2)%3];  // KK, war +1
			vr.insertBisector(edge);
			if (DEBUG > 2) System.out.println(" Erzeuge Kante (2.C): " + edge);

		} // end (!flag) - VorwÃ¤rtslauf bei offenen Regionen

		return vr;
	}

	/* Nachdem ein neuer Delaunay-Knoten eingefÃ¼gt und seine Voronoi-Region bestimmt wurde,
	 * mÃ¼ssen die Regionen der betroffenen Delaunay-Knoten akualisiert werden.
	 * Momentan ist eine simple Strategie implementiert, bei der diese Regionen neu erstellt werden. 
	 */
	private void updateNeighbourRegions() { //final TreeSet<Point2d> dq) {

		if (DEBUG > 2) System.out.println("updateNeighbourRegions: " + dq);

		// Sonderfall: Es gibt keinen Voronoi-Knoten mehr, aber zwei Regionen mit dem Bisektor senkrecht zu den beiden DPs. Diese werden jedoch NICHT erzeugt!
		if (dPoints.size() == 2) {
			regionMap.clear();
			return;
		}

		for (Point2d vr_center : dq) {
			if (DEBUG > 2) System.out.println(" betrachte Region zu " + vr_center);

			Entry start = null;
			VoronoiRegion vr = regionMap.get(vr_center);
			if (DEBUG > 2) System.out.println(" AKTUALISIERE betroffene Nachbarn: " + vr.dNode);

			for (VoronoiEdge edge : vr.bisectors) {
				if (DEBUG > 3) System.out.println(" dir.contains(edge.vn1): " + edge.vn1);

				if (dir.contains(edge.vn1)) {
					start = edge.vn1;
					break;
				}
				// TODO: hier fehlt ggf. die contains-ÃberprÃ¼fung beim zweiten Punkt der letzten Kante in bisectors zu edge.vn2
			}

			// Falls sich alle Voronoi-Knoten der VR geÃ¤ndert haben, ermittle einen der neuen Voronoi-Knoten zu dem Delaunay-Punkt Ã¼ber das Boyer-Watson-Verzeichnis. 
			if (start == null) {
//				Timer.startTimer("updateRegionMap.searchstart");
				start = getEntry(vr_center);
//				Timer.stopTimer("updateRegionMap.searchstart");
			}

			regionMap.remove(vr_center);
			vr = createVoronoiRegion(vr_center, start);
			regionMap.put(vr_center, vr);
		}
	}

	/* Ermittelt schnell einen Entry, dessen Dreieck mit p gebildet wird:
	 * (Liegt p nicht im aktuellen Dreieck,) 
	 * Ist p nicht Knoten des aktuellen Dreiecks, wird die Suche mit dem Nachbardreieck 
	 * fortgesetzt, das Ã¼ber die Kante erreicht wird, die eine Halbebene erzeugt, 
	 * in der p liegt. Das initiale Dreieck wird Ã¼ber die Gitterstruktur gesucht.
	 */
	private Entry getEntry(Point2d p) {
		return getEntry(null, p);
	}

	private Entry getEntry(Entry pivotVN, Point2d p) {

		if (pivotVN == null) {
			pivotVN = dir.getNearestElement(p);
		}
		while (pivotVN.indexOf(p) == -1) {
			for (int i=0; i < 3; i++) {
				if (isPointInSemiPlane(pivotVN.dTri[(i+1)%3], pivotVN.dTri[(i+2)%3], p)) {
					pivotVN =  pivotVN.adjVN[i];
					break;
				}
			}
		}
		return pivotVN;
	}

	// ----------------------------- Delaunay-Knoten entfernen -------------------------
	//  --------------------------------------------------------------------------------
	//  --------------------------------------------------------------------------------
	private boolean remove(Point2d q) {

		/* (1) Ermittle vom nÃ¤chsten Voronoi-Knoten ausgehend alle Dreiecke, 
		 *     die mit q verbunden sind. Dabei werden die Voronoi-Kanten mitermittelt,
		 *     die mit einem zu lÃ¼schenden Voronoi-Knoten verbunden sind. 
		 */
		computeAffectedNodesForRemoval(q);    

		if (DEBUG > 2) {
			System.out.println("\nZu loeschender Delaunay-Punkt: " + q);
			System.out.println("Betroffene Voronoi-Knoten: \n" + vq);
			System.out.println("Betroffene Delaunay-Knoten: \n" + dq);
			System.out.println("Betroffene Kanten: \n" + veq);
		}

		/* (2) Berechne die innere Triangulation der betroffenen Delaunay-Knoten  
		 *     und verbinde die neuen Dreiecke mit der Ã¼uÃ¼eren Triangulation.
		 */
		newVoronoiNodesForRemoval(); //vq, dq, veq);
		dPoints.remove(q);

		/* (3) Die Liste der Rand-Voronoi-Knoten aktualisieren
		 * 
		 */
		for(Entry e : newVoronoiNodes) {
			if (e.isBoundaryEntry() > -1) {
				if (!boundaryEntries.contains(e)) {   // TODO contains nicht erforderlich
					boundaryEntries.add(e);
					boundaryModified = true;
				}
			}
		}

		/* (4) Aktualisierung der Voronoi-Regionen
		 */
		if (BUILD_REGIONS) {
			regionMap.remove(q);
			updateNeighbourRegions(); // (dq);
			if (DEBUG > 1)
				System.out.println("REGIONMAP: \n" + regionMap.values());
		}

		if (DEBUG > 0) {
			System.out.println("...removed. New directory: \n" + dir);    
			System.out.println("Boundary:" + Arrays.toString(getBoundaryPolygon()) + "\n");
		}
		if (DEBUG > -1) {
			System.out.println("< *********************************");
			boolean checkResult = checkConsistency(System.out);
			if (!checkResult && DEBUG > 0) {
				System.out.println("Directory: \n" + dir);

				for (Point2d p : dPoints) {
					System.out.println("ps.add(new Point2d" + p + ");");
				}

			}
			System.out.println("Konsistenz: " + checkResult);
			System.out.println("********************************** >"); 
		}
		return true;
	}

	/** 
	 * Step 1: Diese Methode sucht nach Voronoi-Knoten, 
	 *         deren Dreiecke eine Kante mit dem zu loeschenden Delaunay-Knoten haben.
	 * 
	 * @param q - der zu loeschende Delaunay-Knoten
	 * @return
	 */
	private void computeAffectedNodesForRemoval(final Point2d q) { //, final List<Entry> vq, final TreeSet<Point2d> dq, final List<VoronoiEdge> veq) {

		// Aktualisiere den Zeitstempel (Besucht-Marke)
		stamp = new Object();
		Entry.INFINITY.version = stamp;

		dq.clear();
		vq.clear();
		veq.clear();
		candidates.clear();

		// Suche den nÃ¼chsten Voronoi-Knoten Vq zu q
		Entry nearestVN = dir.getNearestElement(q);

		// Ã¼berprÃ¼fe, von nearestVN ausgehend, die Nachbarn, ob sie von q betroffen sind.
		if (!candidates.contains(nearestVN))
			candidates.add(nearestVN);

		// Suche ausgehend von Vq die Voronoi-Knoten, die aufgrund der Beteiligung des assoziierten Dreiecks am zu loeschenden Knoten geloescht werden.
		checkVoronoPoint2dsForRemoval(q, vq, dq, veq);    
	}

	/** 
	 *  Diese Methode sucht nach Voronoi-Knoten, deren Dreiecke eine Kante mit dem zu loeschenden Delaunay-Knoten haben.
	 *  Gleichzeitig wird fÃ¼r jeden betroffenen Voronoi-Knoten die Voronoi-Kante gespeichert, die einen neuen Knoten bekommt.
	 */
	private void checkVoronoPoint2dsForRemoval(final Point2d q, final List<Entry> vq, TreeSet<Point2d> dq, final List<VoronoiEdge> veq) {                   

		Entry pivotVN = null;
		while ((pivotVN = candidates.poll()) != null) {
			pivotVN.version = stamp;
			int idx = pivotVN.indexOf(q);
//			System.out.println(" check pivot Entry @" + Integer.toHexString(pivotVN.hashCode()) + ": " + Arrays.toString(pivotVN.dTri) + ", isIn=" + isIn);
			if (idx > -1) {
				vq.add(pivotVN);
				dq.add(pivotVN.dTri[(idx+1)%3]);
				dq.add(pivotVN.dTri[(idx+2)%3]);

				// Die Voronoi-Kante merken, die die verbleibende Dreieckskante schneidet.
				VoronoiEdge edge = new VoronoiEdge(pivotVN, pivotVN.adjVN[idx]);
				edge.dn1 = pivotVN.dTri[(idx+1)%3];
				edge.dn2 = pivotVN.dTri[(idx+2)%3];
				edge.setMarked(pivotVN);
				veq.add(edge);
			}
			if (idx > -1 || vq.isEmpty()) {
				for (Entry neighVN : pivotVN.adjVN) {
					if (neighVN.version != stamp && !candidates.contains(neighVN)) {  // contains-Check ist notwendig, da zwei noch nicht besuchte Dreiecke den gleichen Nachbarn haben kÃ¶nnten und die Queue Elemente mehrfach enthalten kann.
						candidates.add(neighVN);
					}
				}
			}
		}
	}    

	/*
	 * Diese Methode entfernt alle Voronoi-Knoten, deren Dreieck eine Kante mit dem zu lÃ¼schenden Delaunay-Knoten hat, aus dem Verzeichnis.
	 * Jedes dieser Dreiecke hat eine Kante, die erhalten bleibt. Diese Kanten reprÃ¤sentieren, sofern kein Randknoten entfernt wird, ein einfaches Polygon (konvex oder nicht-konvex) 
	 * - bildlich: das Loch in der alten Triangulierung, das durch das Entfernen entstanden ist.
	 * Die Knoten des Polygons werden nun trianguliert und die neuen Dreiecke Ã¼ber die jeweilig stehen gebliebene Kante in die "groÃe" Triangulation eingearbeitet.
	 * Dabei wird geprÃ¼ft, ob die kleine Triangulation "falsche" Dreiecke enthÃ¤lt, die sich bei Triangulation eines nicht-konvexen Lochs ergeben. 
	 * Dazu wird die Tatsache ausgenutzt, dass bei gleichem Umlaufsinn benachbarte Dreiecke
	 * eine entgegengesetzt gerichtete Schnittkante haben. Falsche Dreiecke hingegen Ã¼berdecken ein Nachbar-Dreieck und haben somit eine Kante mit gleicher Richtung.
	 * Beim Entfernen von Randknoten sind verschiedene Sonderfall-Betrachtungen notwendig.
	 */
	private List<Entry> newVoronoiNodesForRemoval() {  // final List<Entry> vq, final TreeSet<Point2d> dq, final List<VoronoiEdge> veq) {

		newVoronoiNodes.clear();

		// Entferne alle betroffenen Voronoi-Knoten aus dem Verzeichnis
		boundaryModified = false;
		for (Entry v : vq) {
			if (v.isBoundaryEntry() > -1) {
				boundaryEntries.remove(v);
				boundaryModified = true;
			}
			dir.remove(v);
		}

		// Erzeuge eine Triangulation des durch das Entfernen entstanden "Lochs" (ggf. nicht-konvex)
		DelaunayTriangulator<E> triangulator = new DelaunayTriangulator<E>();
		for (Point2d dNode : dq) {
			triangulator.add(dNode);
		}

		if (DEBUG > 0) {
			System.out.println("-----------------------------------------");
			System.out.println("Triangulation der LÃ¼cke:\n" + triangulator.dir);
			System.out.println("-----------------------------------------");
		}

		// Sonderfall: Die betroffenen Punkte sind kollinear und liegen am Rand
		if (triangulator.dir.size() == 0) {
			for (VoronoiEdge edge : veq) {
				if (edge.unmarked != Entry.INFINITY) {
					int k = edge.unmarked.indexOf(edge.marked);
					edge.unmarked.adjVN[k] = Entry.INFINITY;
					if (!boundaryEntries.contains(edge.unmarked)) {
						boundaryEntries.add(edge.unmarked);
						boundaryModified = true;
					}
				}
			}
			return newVoronoiNodes;
		} 

		// Allgemeiner Fall: -------------------

		// Aktualisiere den Zeitstempel (Besucht-Marke)
		stamp = new Object();

		// Puffer fÃ¼r die Kanten, fÃ¼r die im folgenden Lauf ein neuer Nachbar gesetzt wird
		ArrayList<VoronoiEdge> treatedEdges = new ArrayList<VoronoiEdge>();

		for (Entry e2 : triangulator.dir) {

			for (VoronoiEdge edge : veq) {

				// Test5  PrÃ¼fe, ob das zu lÃ¶schende Dreieck (marked) ein Randdreieck ist und das verbleibende Dreiech (unmarked) im Unendlichen liegt.
				if (edge.unmarked == Entry.INFINITY) {

					// Wenn das Dreieck(e2.dTri) dn1 und dn2 in der richtigen Orientierung enthÃ¤lt, ist es ein richtiges inneres Dreieck
					int idxOfQ = e2.indexOf(edge.dn1);
					if (idxOfQ > -1 && edge.dn2 == e2.dTri[(idxOfQ+1)%3]) {
						if (!newVoronoiNodes.contains(e2)) {
							if (DEBUG > 2) System.out.println("  A1 Ãbertragung!"); 
							dir.add(e2);
							newVoronoiNodes.add(e2);
							e2.version = stamp;
						}
					}
					continue;
				}

				// PrÃ¼fe, ob die beiden betrachteten Dreiecke (ein inneres Dreieck und ein Ã¤uÃerer Nachbar eines zu lÃ¶schenden Dreiecks) eine gemeinsame Kante haben
				int[][] idx = conjointEdge(edge.unmarked.dTri, e2.dTri);

				// Wenn eine gemeinsame Kante vorliegt,
				if (idx != null) {

					// prÃ¼fe, ob es sich um Nachbarn Ã¼ber die betroffene Kante handelt
					if ((e2.dTri[idx[1][1]] == edge.dn1 && e2.dTri[idx[1][0]] == edge.dn2) ||
							(e2.dTri[idx[1][0]] == edge.dn1 && e2.dTri[idx[1][1]] == edge.dn2)) {

						e2.version = stamp;

						// prÃ¼fe, ob die Orientierung der Kante gegensÃ¤tzlich ist, denn dann handelt es sich bei e2.dTri und edge.unmarked.dTri um neue Nachbarn
						if ((3+(idx[1][0]-idx[0][0]))%3 != (3+(idx[1][1]-idx[0][1]))%3) {

							treatedEdges.add(edge);

							edge.unmarked.adjVN[3-idx[0][0]-idx[0][1]] = e2;
							e2.adjVN[3-idx[1][0]-idx[1][1]] = edge.unmarked;

							if (!(dir.contains(e2))) {
								if (DEBUG > 2) System.out.println("  Ãbertragung!"); 
								dir.add(e2);
								newVoronoiNodes.add(e2);
							}

						} 
					}
				}
			}
		}

		if (DEBUG > 0) {
			System.out.println(" new Triangles:");
			for (Entry tmpE : newVoronoiNodes) {
				System.out.println(" " + Arrays.toString(tmpE.dTri));
			}
			System.out.println(" Treated:");
			for (VoronoiEdge edge : treatedEdges) {
				System.out.println(" " + edge);
			}
		}

		// Kanten, die keine neuen Nachbarn bekommen haben, mÃ¼ssen Randkanten werden.
		for (VoronoiEdge edge : veq) {
			if (edge.unmarked != Entry.INFINITY && !treatedEdges.contains(edge)) {
				int idx = edge.unmarked.indexOf(edge.marked);
				edge.unmarked.adjVN[idx] = Entry.INFINITY;
				if (DEBUG > 0) System.out.println(" Kante " + edge + " hat keinen neuen Nachbarn bekommen. Ersetze " + edge.unmarked.adjVN[idx] + " durch Entry.INFINITY");
			}
		}

		// Dreiecke der innere Triangulation berÃ¼cksichtigen, die keine gemeinsame Kante mit dem Loch haben und die nicht am Rand liegen.
		for (Entry e2 : triangulator.dir) {
			if (e2.version != stamp) { //  && e2.isBoundaryEntry() == -1) {
				// ÃberprÃ¼fe, ob die Nachbarn dieses Dreiecks im Verzeichnis sind
				boolean flag = true;
				for (Entry adjVN : e2.adjVN) {
					if (adjVN != Entry.INFINITY && !dir.contains(adjVN)) {
						flag = false;
						break;
					}
				}
				// Falls die Nachbarn nicht drin sind, dann kann das Dreieck immer noch ein zu Ã¼bernehmendes Dreieck sein
				if (!flag) {
					flag = true;
					for (VoronoiEdge edge : veq) {
						// Teste, ob das Dreieck das Umkreis-Kriterium eines der unmarked verletzt
						for (int i=0; i < 3; i++) {
							if (e2.perimeterContains(edge.unmarked.dTri[i]) > 0 ) {
								flag = false;
								break;
							}
						}
						if (!flag) break;
					}
				}

				if (flag) {
					if (DEBUG > 0) System.out.println(" Inneres Dreieck Ã¼bernommen: " + Arrays.toString(e2.dTri));
					if (!dir.contains(e2)) {
						dir.add(e2); 
						newVoronoiNodes.add(e2);
					}
				} else {
					if (DEBUG > 0) System.out.println(" Neues Dreieck " + Arrays.toString(e2.dTri) + " wird nicht Ã¼bernommen.\n  " + e2);
				}
			}
		}

		// Rand aktualisieren
		for (VoronoiEdge edge : veq) {
			if (edge.unmarked.isBoundaryEntry() == -1) {
				boundaryEntries.remove(edge.unmarked);
				boundaryModified = true;
			} else {
				if (!boundaryEntries.contains(edge.unmarked)) {
					boundaryEntries.add(edge.unmarked);
					boundaryModified = true;
				}
			}
		}

		return newVoronoiNodes;
	}


	/* Bestimmt die gemeinsame Kante der beiden Dreiecke dTri1 und dTri2 
	 * @return  int[][], wobei int[0][] die Indizes (der Delaunay-Knoten) des ersten Dreiecks und 
	 *                         int[1][] die im zweiten Dreieck bedeuten.
	 *          NULL, falls keine gemeinsame Kante
	 */
	private static int[][] conjointEdge(final Point2d[] dTri1, final Point2d[] dTri2) {

		if (dTri1 == null || dTri2 == null)
			return null;

		int n=0;
		int[][] idx = new int[2][2];

		for (int i=0; i < 3 && n < 2; i++) {
			Point2d p1 = dTri1[i];
			for (int j=0; j < 3; j++) {
				if (p1.equals(dTri2[j])) {
//					System.out.println("p1=" + p1 + ", dTri2=" + dTri2[j]);
					idx[0][n] = i; 
					idx[1][n] = j; 
					n++;
					break;
				}
			}
		}
		return (n == 2 ? idx : null);
	}

	/* Ermittelt den orientieren FlÃ¤cheninhalt des Dreiecks p0, p1, p2. 
	 * Ist die FlÃ¤che positiv, dann liegt p2 links der Geraden durch p0 und p1. 
	 * positiv orientiere Dreiecke = counter-clockwise
	 */
	private static double area(final Point2d p0, final Point2d p1, final Point2d p2) {
		return 0.5*((p1.x-p0.x)*(p2.y-p0.y)-(p2.x-p0.x)*(p1.y-p0.y));
	}


	// ----------------------------- Ende remove ---------------------------------------
	//  --------------------------------------------------------------------------------
	//  --------------------------------------------------------------------------------

	/*
	 *  Berechnet rekursiv das Randpolygon Ã¼ber die Nachbarschaften des jeweiligen Randelements.
	 *  PivotVN muss ein Rand-Voronoi-Knoten sein. lastVN ist im ersten Schritt null, die Liste points leer.
	 *  Die Liste aller Randelemente (boundaryEntries) wird hier neu erzeugt (da Abfallprodukt).
	 */
	private void buildBoundaryPolygon(final Entry pivotVN, final ArrayList<Point2d> points) {
		buildBoundaryPolygon(null, pivotVN, points);
	}

	private void buildBoundaryPolygon(final Entry lastVN, final Entry pivotVN, final ArrayList<Point2d> points) {

		int idx = -1;

		for (int i = 0; i < 3; i++) {
			if (pivotVN.adjVN[(3+i-1)%3] != Entry.INFINITY && pivotVN.adjVN[i] == Entry.INFINITY) {
				idx = i;
				break;
			}
		}
		// Sonderfall: pivotVN ist einziger Voronoi-Knoten und hat nur Nachbarn im Unendlichen
		if (idx == -1) {
			for (int i = 0; i < 3; i++) {
				points.add(pivotVN.dTri[i]);
			}
			return;
		}

		// Falls dies der erste Rand-Voronoi-Knoten ist, wird auch der erste Kanten-Knoten eingefÃ¼gt. 
		if (points.size() == 0) { 
			points.add(pivotVN.dTri[(idx+1)%3]);
		}

		// Ermittle den zweiten Delaunay-Randknoten. 
		// Falls dieser mit dem Anfangsknoten zusammenfÃ¼llt, ist der Rand fertig ermittelt.
		Point2d p = pivotVN.dTri[(idx+2)%3];
		if (p.equals(points.get(0)))
			return;

		// FÃ¼gt den Delaunay-Knoten ins Ergebnis ein.
		if (!points.contains(p)) {
			points.add(p);
		} else
			return;

		// Sonderfall: Der nÃ¤chste Nachbar liegt auch im Unendlichen.
		// Dann fÃ¼ge auch den nÃ¤chsten Delaunay-Knoten ins Ergebnis ein.
		if (pivotVN.adjVN[(idx+1)%3] == Entry.INFINITY) {
			idx = (idx+1)%3;
			p = pivotVN.dTri[(idx+2)%3];
			if (p.equals(points.get(0)))
				return;
			points.add(p);
		}

		// Ermittle den Nachbarn, der sowohl am Rand liegt als auch einen gemeinsamen Delaunay-Knoten hat.
		Entry neighVN, tmpVN = pivotVN.adjVN[(idx+1)%3];
		int z=0;  // Schutz gegen Unendlich-Schleife - muss spÃ¼ter entfernt werden TODO
		do {
			neighVN = tmpVN;
			idx = neighVN.indexOf(p);
			// 1) Ermittle beim nÃ¤chsten Nachbarn den Index des Delaunay-Knotens p (= letzter Randpolygon-Knoten)
			z++;
			if (z > 1000) {
				System.out.println("buildBoundaryPolygon (z > 1000): Dreickspunkt=" + p + ", untersuchtes Dreieck = " + Arrays.toString(tmpVN.dTri) + ", Index des Punktes im Dreieck=" + idx);
			}
			// 2) Ã¼berprÃ¼fe, ob der Nachbar Ã¼ber die Kante mit p im Unendlichen liegt 
		}  while ((tmpVN = neighVN.adjVN[3-(idx+(idx+1)%3)]) != Entry.INFINITY);

		// Setze die Rekursion mit dem Nachbar-Knoten fort.
		buildBoundaryPolygon(pivotVN, neighVN, points);
	}

	/* 
	 * Diese Methode ermittelt die Voronoi-Regionen auf der Grundlage des aktuellen Bowyer-Watson-Verzeichnisses.
	 * Alternative zur inkrementellen Aufbauweise Ã¼ber BUILD_REGION. TODO
	 */
//	private void buildRegionMap() {
//	System.out.println("Not implemented yet");
//	}

	public static Rectangle2D getBoundingRect(final Collection<? extends Point2d> points) {

		double[] mins = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
		double[] maxs = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };

		// Werte das Randpolygon aus
		for (Point2d p : points) {
			if (p.x < mins[0]) {  mins[0] = p.x; }
			if (p.x > maxs[0]) {  maxs[0] = p.x; }
			if (p.y < mins[1]) {  mins[1] = p.y; }
			if (p.y > maxs[1]) {  maxs[1] = p.y; }
		}
//		if (DEBUG > -1) {
//		System.out.println("xmin=" + mins[0] + ", xmax=" + maxs[0] + ", ymin=" + mins[1] + ", ymax=" + maxs[1]);
//		}
		return new Rectangle2D.Double(mins[0], mins[1], (maxs[0]-mins[0]), (maxs[1]-mins[1]));
	}

	/* Diese Methode Ã¼berprÃ¼ft, ob das Verzeichnis die vom Bowyer-Watson-Algorithmus vorausgesetzte Struktur hat.
	 * Neben der Existenz adjazenter Voronoi-Knoten im Verzeichnis wird insbesondere geprÃ¼ft, 
	 * ob die Indizes einer Dreiecksschnittkante mit dem Index des entsprechenden Voronoi-Nachbarn konsistent ist.
	 * Weiterhin wird fÃ¼r jedes Dreieck das Umkreis-Kriterium geprÃ¼ft.
	 */ 
	protected boolean checkConsistency() {
		return checkConsistency(System.out);
	}

	protected boolean checkConsistency(PrintStream out) {

		boolean result = true;
		int u = 0;
		System.out.println("checkConsistency: Anzahl der Dreiecke:" + dir.size());
		for (Entry e : dir) {

			// ZÃ¤hler
			if (u > 0 && u % 10000 == 0) {
				System.out.print(u + ",");
				if (u % 100000 == 0) {
					System.out.println("");
				}
			}
			u++;

			// Alle drei Nachbarn mÃ¼ssen existieren und einer ihrer Nachbarn wiederum muss e sein. 
			int z = 0, inf = 0;

			// Ã¼berprÃ¼fe den Umlaufsinn des assoziierten Dreiecks
			if (area(e.dTri[0], e.dTri[1], e.dTri[2]) < 0.) {
				out.println("Falsche Dreiecks-Orientierung im Entry " + e);
				result = false;
				continue;
			}

			for (int i=0; i < 3; i++) {
				if (e.adjVN[i] != Entry.INFINITY) {
					// Der Nachbar muss im Verzeichnis existieren
					if (!dir.contains(e.adjVN[i])) {
						out.println("Der Nachbar mit dem Index " + i + " ist nicht im Verzeichnis.");
						continue;
					}

					// und der Nachbar muss eine gemeinsame Dreiecks-Kante mit e haben.
					int[][] idx = conjointEdge(e.dTri, e.adjVN[i].dTri);
					if (idx == null) {
						out.println("Der Nachbar mit dem Index " + i + " hat keine gemeinsame Dreiecks-Kante mit dem aktuellen Eintrag.");
						continue;
					}

					if (e.perimeterContains(e.adjVN[i].dTri[(3-idx[1][0]-idx[1][1])]) > 0) {  // nicht > -1, wegen degenerierter FÃ¤lle
						out.println(" VERLETZUNG des Delaunay-Kriteriums fÃ¼r Dreieck \n" + e + "\n und D-Punkt:" + e.adjVN[i].dTri[(3-idx[1][0]-idx[1][1])]);
						continue;
					}

					// Weiterhin muss der Adjazenz-Index des Nachbarn mit den Indizes der entsprechenden Delaunay-Knoten passen, 
					// d.h. die Voronoi-Kante zum Nachbar 0 geht Ã¼ber die Delauny-Kante mit den Indizes 1 und 2, usw.
					if (!(3-(idx[0][0]+idx[0][1]) == i)) {
						out.println("Inkonsistente Nachbarschaft zwischen \n" + e + " und \n" + e.adjVN[i]);
						out.println("Index bei 1.: " + (3-(idx[0][0]+idx[0][1])));
						out.println("Index bei 2.: " + (3-(idx[1][0]+idx[1][1])));
						continue;
					}
					z++;

				} else {
					inf++;
				}
			}
			if ((z+inf != 3) || (inf == 3 && dir.size() > 1)) {
				out.println(" (z+inf)-Kriterium verletzt: Entry " + e);
				result = false;
			}

			// ÃberprÃ¼fe ob Randdreicke in boundaryEntries stehen.
			if (e.isBoundaryEntry() > -1) {
				if (!boundaryEntries.contains(e)) {
					System.out.println("Das Dreieck: " + Arrays.toString(e.dTri) + " ist Randdreieck, steht aber NICHT in boundaryEntries");
					result = false;	
				}
			}
		}

		// ÃberprÃ¼fe die RandeintrÃ¤ge
		for (Entry be : boundaryEntries) {
			if (be.isBoundaryEntry() == -1) {
				System.out.println("Das Dreieck: " + Arrays.toString(be.dTri) + " ist KEIN Randdreieck, steht aber in boundaryEntries");
				result = false;
			}
		}
		return result;

	}

	// TODO private
	protected void initializeHull(Rectangle2D rect) {

		if (DEBUG > 1) System.out.println("initializeHull");

		Point2d p_nw = new Point2d(rect.getMinX(), rect.getMinY());
		Point2d p_sw = new Point2d(rect.getMinX(), rect.getMaxY());
		Point2d p_ne = new Point2d(rect.getMaxX(), rect.getMinY());
		Point2d p_se = new Point2d(rect.getMaxX(), rect.getMaxY());

		add(p_sw);
		add(p_nw);
		add(p_se);
		add(p_ne);

		startBox = new ArrayList<Point2d>(4);
		startBox.add(p_sw);
		startBox.add(p_nw);
		startBox.add(p_se);
		startBox.add(p_ne);
	}

	// TODO private
	protected void removeHull() {

		if (DEBUG > 1) System.out.println("removeHull");
//		removeStartBox();

		for (Point2d p : startBox) {
			remove(p);
		}
	} 

	/* 
	 * Entfernt alle Dreiecke, die einen Knoten aus der Startbox haben.
	 * HINWEIS: Das Ergebnis ist keine konvexe HÃ¼lle. 
	 * Der Triangulation sollten danach keine weiteren Punkte hinzugefÃ¼gt werden.
	 * (Die Methoden zur Bestimmung des Randes benÃ¶tigen KonvexitÃ¤t.)
	 */
	protected void removeStartBox() {

//		if (BATCH_MODE) {
//			Entry nearBound = dir.getNearestElement(startBox.get(0));
//			// nearBound muss nicht zwangslÃ¤ufig ein Randelement sein, daher muss noch nachgebessert werden
//			nearBound = searchBoundaryEntry(nearBound, startBox.get(0));

//			boundaryEntries.clear();
//			boundaryEntries.add(nearBound);
//			getBoundaryPolygon();
//		}

		// TODO vereinfachtes Entfernen
		int idx1, idx2, idxNeigh, n;
		System.out.println("boundaryEntries: " + boundaryEntries.size());
		System.out.println("startBox: " + startBox);
		Entry startVN = boundaryEntries.get(0);
		Entry pivotVN = startVN;

		do {
			idx1 = idx2 = idxNeigh = -1;
			n = 0;
			System.out.println("Entferne: " + pivotVN);
			dir.remove(pivotVN);
			boundaryEntries.remove(pivotVN);
			
			for (int i=0; i < 3; i++) {
				if (pivotVN.adjVN[i] == Entry.INFINITY) {
					idx1 = i;
				}
				if (startBox.contains(pivotVN.dTri[i])) {
					idx2 = i;
				}
			}
			if (idx1 > -1) {
				pivotVN = pivotVN.adjVN[(idx1+1)%3];
				System.out.println(" Ã¼ber Infiniy-Kante");
			} else {
				// Nachbarschaft aktualisieren
				idxNeigh = pivotVN.adjVN[idx2].indexOf(pivotVN);
				pivotVN.adjVN[idx2].adjVN[idxNeigh] = Entry.INFINITY;
				boundaryEntries.add(pivotVN.adjVN[idx2]);

				pivotVN = pivotVN.adjVN[(idx2+2)%3];
				System.out.println(" Ã¼ber beteiligten Startbox-Knoten");
			}
			if (n++ > dir.size()) {
				for (Point2d p2 : dPoints) {
					System.out.println("ps.add(new Point2d" + p2 + ");");
				}
				System.exit(0);
			}
			
		} while (pivotVN != startVN);

		boundaryModified = true;
		BATCH_MODE = false;

	}
	
	public Entry searchBoundaryEntry(Entry start, Point2d p) {

		if (start == Entry.INFINITY) throw new IllegalArgumentException("start is INFINITY");
		else if (start == null) throw new IllegalArgumentException("start is null");

		stamp = new Object();

		Entry pivotVN = start;
		while (!(pivotVN.indexOf(p) > -1 && pivotVN.isBoundaryEntry() > -1)) {
			Entry oldPivotVN = pivotVN;
			pivotVN.version = stamp;
			for(int i=0; i < 3; i++) {
				Entry adjVN = pivotVN.adjVN[i];
				if (adjVN != Entry.INFINITY) {
//					System.out.println("area = " + area(pivotVN.dTri[(i+1)%3], pivotVN.dTri[(i+2)%3], p));
					if (adjVN.version != stamp && area(pivotVN.dTri[(i+1)%3], pivotVN.dTri[(i+2)%3], p) <= 0.) {
						pivotVN = adjVN;
						break;
					}
				}
			}
			if (pivotVN == oldPivotVN) {
				return null;
			}
		}
		return pivotVN;
	}
	
	/** 
	 * EXPERIMENTAL!
	 * Diese Methode fÃ¼gt eine zweite Triangulation an.
	 */
	protected void addTriangulation(DelaunayTriangulator<E> t2) {

		int DEBUG2 = 0;

		List<Entry> myBoundary = this.boundaryEntries;
		List<Entry> hisBoundary = t2.boundaryEntries;

		Set<Point2d> pointsToAdd = new TreeSet<Point2d>(new PointComparator2d<Point2d>());
		List<VoronoiEdge> hisAffectedEdges = new ArrayList<VoronoiEdge>();

		// Ermittle die Randkanten der anderen Triangulation, die von dieser aus sichtbar sind und von denen aus die eigenen Randkanten zu sehen sind.
		Point2d d1_1 = null, d1_2 = null, d2_1 = null, d2_2 = null;
		for (Entry e1 : myBoundary) {
			for (int i=0; i < 3; i++) {
				if (e1.adjVN[i] == Entry.INFINITY) {
					d1_1 = e1.dTri[(i+1)%3];
					d1_2 = e1.dTri[(i+2)%3];

					if (DEBUG2 > 2) System.out.println("|" + d1_1 + "," + d1_2 + "|");

					for (Entry e2 : hisBoundary) {
						for (int j=0; j < 3; j++) {
							if (e2.adjVN[j] == Entry.INFINITY) {
								d2_1 = e2.dTri[(j+1)%3];
								d2_2 = e2.dTri[(j+2)%3];

								if (DEBUG2 > 2) {
									System.out.println("   |" + d2_1 + "," + d2_2 + "|");
									System.out.println("	1.: " + d2_1 + " links von |" + d1_1 + "," + d1_2 + "| ?" + isPointInSemiPlane(d1_1, d1_2, d2_1));
									System.out.println("	2.: " + d2_2 + " links von |" + d1_1 + "," + d1_2 + "| ?" + isPointInSemiPlane(d1_1, d1_2, d2_2));
									System.out.println("	3.: " + d1_1 + " links von |" + d2_1 + "," + d2_2 + "| ?" + isPointInSemiPlane(d2_1, d2_2, d1_1));
									System.out.println("	4.: " + d1_2 + " links von |" + d2_1 + "," + d2_2 + "| ?" + isPointInSemiPlane(d2_1, d2_2, d1_2));
								}

								if ((isPointInSemiPlane(d1_1, d1_2, d2_1) ||
										isPointInSemiPlane(d1_1, d1_2, d2_2)) &&
										(isPointInSemiPlane(d2_1, d2_2, d1_1) ||
												isPointInSemiPlane(d2_1, d2_2, d1_2))) {

									pointsToAdd.add(d2_1);
									pointsToAdd.add(d2_2);

									VoronoiEdge edge = new VoronoiEdge(e2, Entry.INFINITY);
									edge.dn1 = d2_1;
									edge.dn2 = d2_2;
									hisAffectedEdges.add(edge);
								}	
							}
						}
					}
				}
			}
		}

		if (DEBUG2 > 0) System.out.println("Points To Add:" + pointsToAdd);

		// Die sichtbaren Delaunay-Punkte der anderen Triangulation werden in diese eingefÃ¼gt.
		List<Entry> myNewEntries = new ArrayList<Entry>();
		for (Point2d p : pointsToAdd) {
			if (DEBUG2 > 0) System.out.println("FÃ¼ge hinzu: " + p);
			myNewEntries.addAll(this.add(p));  // add gibt die neuen Dreiecke zurÃ¼ck
		}

		// myNewBoundary: Alle Entries, die der ersten Triangulation hinzugefÃ¼gt wurden
		// also auch die, die beim HinzufÃ¼gen spÃ¤terer pointsToAdd wieder gelÃ¶scht wurden
		List<Entry> toDelete = new ArrayList<Entry>();
		for (Entry myNewVN : myNewEntries) {
			if (!dir.contains(myNewVN)) {
				toDelete.add(myNewVN);
			}
		}
		myNewEntries.removeAll(toDelete);

		// ? Die Konsistenz dieser Triangulation wird vorÃ¼bergehend verletzt, da die Nachbarn der Ãbergangsdreiecke nur in der zweiten Triangulation sind.
		if (DEBUG2 > 0) {
			System.out.println(" myNewEntries: ");
			for (Entry myNewVN : myNewEntries) {
				System.out.println(" " + myNewVN);	
			}
			System.out.println(" ");
		}

		// ÃberprÃ¼fe, welche der neuen Dreiecke eine gemeinsame Delaunay-Kante mit der zweiten Triangulation haben.
		// -- Ich erinnere mich nicht mehr, warum man "edge.vn1.dTri" nicht fÃ¼r "conjointEdge" nehmen kann. Es ergibt jedenfalls auch falsche "Kantenpaare"
		Point2d[] edgeTmp = new Point2d[3];
		edgeTmp[2] = Entry.INFINITY_POINT;

		for (VoronoiEdge edge : hisAffectedEdges) {
			if (DEBUG2 > 0) System.out.println(" hisAffectedEdge: " +edge);

			for (Entry myNewVN : myNewEntries) {
				if (DEBUG2 > 0) System.out.println("  neuer VN: " + myNewVN);

				edgeTmp[0] = edge.dn1;	edgeTmp[1] = edge.dn2;
				int[][] idx = conjointEdge(myNewVN.dTri, edgeTmp);
				if (idx != null) {
					// Falsche Dreiecke der ersten Triangulation identifizieren
					if (area(edge.dn1, edge.dn2, myNewVN.dTri[3-idx[0][0]-idx[0][1]]) > 0) {
						if (DEBUG2 > 0) System.out.println("  > Falsches Dreieck entdeckt.");

						this.dir.remove(myNewVN);
						boundaryEntries.remove(myNewVN);

					}
					// Nachbarschaft zu Randeelementen der zweiten Triangulation setzen
					// (Standardfall)
					else { 

						// Nachbarschaft setzen
						if (DEBUG2 > 0) {
							System.out.println("    my: " + myNewVN);
							System.out.println("    his: " +edge.vn1);
							System.out.println("   myOldNeigh: " + myNewVN.adjVN[3-idx[0][0]-idx[0][1]]);
							System.out.println("   hisOldNeigh: " + edge.vn1.adjVN[3-idx[1][0]-idx[1][1]]);
						}
						myNewVN.adjVN[3-idx[0][0]-idx[0][1]] = edge.vn1;

						// TODO: Index des neuzusetzenden Nachbarn ermitteln - ist das INFINITY?
						int idx1 = edge.vn1.indexOf(edge.dn1);
						int idx2 = edge.vn1.indexOf(edge.dn2);
						edge.vn1.adjVN[3-idx1-idx2] = myNewVN;

						if (myNewVN.isBoundaryEntry() == -1) {
							boundaryEntries.remove(myNewVN);
						}
						if (edge.vn1.isBoundaryEntry() == -1) {
							t2.boundaryEntries.remove(edge.vn1);
						}
					}
				}
			}
		}

		// ZusammenfÃ¼gen: dir, dPoints, boundaryEntries, regionMap
		dPoints.addAll(t2.dPoints);	
		for (Object e2 : t2.dir) {
			this.dir.add((Entry) e2);
		}
		boundaryEntries.addAll(t2.boundaryEntries);
		boundaryModified = true;

		if (BUILD_REGIONS) {
			for (Point2d p : pointsToAdd) {
				regionMap.remove(p);
				t2.regionMap.remove(p);
			}
			regionMap.putAll(t2.regionMap);

			for (Point2d p : pointsToAdd) {
				dq.clear();
				dq.add(p);
				VoronoiRegion vr = createVoronoiRegion(p, getEntry(p));
				regionMap.put(p, vr);
			}
		}

		if (DEBUG > -1) {
			System.out.println(" join: checkConsistency: " + this.checkConsistency());
			System.out.println(regionMap.values());
			System.out.println(dir);
		}
	}

	/** Erzeugt die Delaunay-Triangulation der Ã¼bergebenen Punktmenge */
	public static <E extends Point2d> DelaunayTriangulator<E> triangulate(Collection<E> points) {
		return triangulate(points, getBoundingRect(points));
	}

	/** Erzeugt die Delaunay-Triangulation der Ã¼bergebenen Punktmenge */
	public static <E extends Point2d> DelaunayTriangulator<E> triangulate(Collection<E> points, Rectangle2D rect) {

		double insetx = Math.ceil(rect.getWidth()/20.);
		double insety = Math.ceil(rect.getHeight()/20.);
		Rectangle2D rect2 = new Rectangle2D.Double(rect.getMinX()-insetx, rect.getMinY()-insety, rect.getWidth()+2*insetx, rect.getHeight()+2*insety);

		DelaunayTriangulator<E> triang = new DelaunayTriangulator<E>(rect2, points.size());
		triang.initializeHull(rect2);
		triang.BATCH_MODE = true;

		if (DEBUG > 0) {
			System.out.println("Rect=" + rect);
			System.out.println("Rect2=" + rect2);
		}

		int i=0, n = points.size(), ms1 = 10000;
		long timeA = System.currentTimeMillis(), timeTotal = System.currentTimeMillis();

		for (E p : points) {
			triang.add(p);
			if (i > 0 && i % 1000 == 0) {
				System.out.print(i + ",");
			}
			if (i > 0 && i % ms1 == 0) {
//				if (TIMER) Timer.print();
				double dpp =  (System.currentTimeMillis() - timeA)/(double)ms1;
				System.out.println(i + ". msecs per point=" + dpp);
				System.out.println("Minutes remaining (approx.):" + ((n - i) * dpp)/(1000.*60.));
				timeA = System.currentTimeMillis();
			}
			i++;
		}
		System.out.println("...fertig.");
		System.out.println(" - Entferne Hülle...");
		triang.removeHull();
		System.out.println("... FINISHED! Total duration = " + (System.currentTimeMillis() - timeTotal)/1000. + " secs.");

//		if (i == n) {
//		System.out.println("Checking consistency: ");
//		System.out.println(i + ". ok? " + triang.checkConsistency() + ", duration in minutes=" + (System.currentTimeMillis() - timeTotal)/1000./60.);
//		}
		return triang;
	}
	
	public static <E extends Point2d> Collection<E> scramble(Collection<E> ps) {
		
		List<E> tmp = new ArrayList<E>(ps.size());
		for (E tp : ps) {
			tmp.add(tp);
		}
		List<E> result = new ArrayList<E>(ps.size());
		while (tmp.size() > 0) {
			int idx = (int) (Math.random()*tmp.size());	
			E tp = tmp.remove(idx);
			result.add(tp);
		}
		return result;
	}

	/** Erzeugt die Delaunay-Triangulation der Ã¼bergebenen Punktmenge */
	protected static <F extends Point2d> DelaunayTriangulator<F> triangulate_parallel(Collection<F> points) {

		int i = 0, nprocs = 2;
		long timeA = System.currentTimeMillis(), timeTotal = System.currentTimeMillis();

		// Anlegen der Punkt-Collections fÃ¼r die einzelnen Triangulatoren
		final Collection<F>[] containers = new ArrayList[nprocs];
		for (i=0; i < nprocs; i++) {
			containers[i] = new ArrayList<F>();
		}

		// Bounding box ermitteln, um Abmessungen fÃ¼r die Point-Collections bestimmen zu kÃ¶nnen
		System.out.println("Bounding box ermitteln...");
		Rectangle2D rect = getBoundingRect(points);

		// Aufteilung der Punkte in Teilmengen (mit diskjunkten HÃ¼llen)
		System.out.println("Punkte aufteilen...");
		double hx = rect.getMinX() + 0.5*rect.getWidth();
//		double hy = mins[1]+0.5*(maxs[1]-mins[1]);
		for (F p : points) {
			if (p.x >= rect.getMinX() && p.x <= hx) {
				i = 0;
			} else if (p.x > hx && p.x <= rect.getMaxX()) {
				i = 1;
			}
			containers[i].add(p);
		}

//		System.out.println("Trianguliere zweite HÃ¤lfte...");
//		DelaunayTriangulator<F> triang2 = DelaunayTriangulator.triangulate(containers[1]);
//		System.out.println("check=" + triang2.checkConsistency_simple(System.out));

//		System.out.println("Trianguliere erste HÃ¤lfte...");
//		DelaunayTriangulator<F> triang1 = DelaunayTriangulator.triangulate(containers[0]);
//		System.out.println("check=" + triang1.checkConsistency_simple(System.out));

//		System.out.println("Verbinde Triangulationen...");
//		triang1.addTriangulation(triang2);
//		System.out.println("...Fertig.");
//		System.out.println("check=" + triang2.checkConsistency_simple(System.out));

//		return triang1;

		System.out.println("Triangulatoren erzeugen...");

		final DelaunayTriangulator[] triangArray = new DelaunayTriangulator[nprocs]; 
		Rectangle2D rect0 = new Rectangle2D.Double(rect.getMinX(), rect.getMinY(), hx-rect.getMinX(), rect.getHeight());
		Rectangle2D rect1 = new Rectangle2D.Double(hx, rect.getMinY(), rect.getMaxX()-hx, rect.getHeight());

		double insetx = Math.ceil(rect0.getWidth()/20.);
		double insety = Math.ceil(rect0.getHeight()/20.);
		rect0 = new Rectangle2D.Double(rect0.getMinX()-insetx, rect0.getMinY()-insety, rect0.getWidth()+2*insetx, rect0.getHeight()+2*insety);

		insetx = Math.ceil(rect1.getWidth()/20.);
		insety = Math.ceil(rect1.getHeight()/20.);
		rect1 = new Rectangle2D.Double(rect1.getMinX()-insetx, rect1.getMinY()-insety, rect1.getWidth()+2*insetx, rect1.getHeight()+2*insety);

		triangArray[0] = new DelaunayTriangulator<F>(rect0, containers[0].size());
		triangArray[1] = new DelaunayTriangulator<F>(rect1, containers[1].size());

		final class Adder implements Runnable {

			DelaunayTriangulator triang;
			Collection<F> points;
			Rectangle2D rect;
			String name;

			public Adder(String name, DelaunayTriangulator triang, Collection<F> points, Rectangle2D rect) {
				this.triang = triang;
				this.points = points;
				this.rect = rect;
				this.name = name;
			}

			public void run() {
				System.out.println(name + " HÃ¼lle initialisieren...");
				triang.initializeHull(rect);

				System.out.println(name + " Punkte triangulieren...");
				for (Point2d p : points) {
					triang.add(p);
				}
				System.out.println(name + " HÃ¼lle entfernen...");
				triang.removeHull();

				System.out.println(name + " fertig.");
			}
		}

		System.out.println("Threads anlegen und starten...");
		Thread t0 = new Thread(new Adder("A", triangArray[0], containers[0], rect0));
		Thread t1 = new Thread(new Adder("B", triangArray[1], containers[1], rect1));
		t0.start();
		t1.start();

		try {
			System.out.println("Haupt-Thread schlafen legen ...");
			t0.join();
			t1.join();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		System.out.println("... FINISHED single triangulations ! duration = " + (System.currentTimeMillis() - timeA)/1000. + " secs.");

		timeA = System.currentTimeMillis();
		System.out.println("Joining triangulations...");

		triangArray[0].addTriangulation(triangArray[1]);

		System.out.println("... FINISHED joining ! duration = " + (System.currentTimeMillis() - timeA)/1000. + " secs.");	
		System.out.println("... total duration = "  + (System.currentTimeMillis() - timeTotal)/1000. + " secs.");	

		return triangArray[0];
	}
	
	// ----------------------------- Klasse PointWrapper (fÃ¼r spÃ¤ter)  -------------------------------------
	//  --------------------------------------------------------------------------------
	//  --------------------------------------------------------------------------------
//	static class PointWrapper extends Point2d {

//	public List<Entry> entries;  // Verweis auf alle Dreiecke an diesem Delaunay-Punkt
//	public Point2d point;

//	public PointWrapper(Point2d p) {
//	this.point = p;
//	entries = new ArrayList<Entry>();
//	}

//	public void addTriangle(Entry e) {
//	entries.add(e);
//	}

//	public Point2d getPoint() {
//	return point;
//	}
//	}

	// ----------------------------- Klasse ENTRY ------------ -------------------------
	//  --------------------------------------------------------------------------------
	//  --------------------------------------------------------------------------------
	// TODO: Comparable
	public static class Entry extends Point2d {

		public Point2d[] dTri;
		public Entry[] adjVN;
		public Object version;

		private transient double dx, dy, r, radSq = 0.;

		public Entry(Point2d e, Point2d[] n, Entry[] a) {
			this.x = e.x; this.y = e.y;
			this.dTri = n;
			this.adjVN = a;
		}    
		
		public Entry[] getEntries() {
			return this.adjVN;
		}
			
                /**Dreieck zum Entry*/
                public Point2d[] getTriangle(){
                   return dTri;
                }
                
                /**Neighbour Entrys*/
                public Entry[] getNeighbourEntry(){
                   return adjVN;
                }
                
		/**
		 * @param p
		 * @return 0, wenn p innen bzw. auf dem Rand liegt, 0 sonst
		 */
		public int perimeterContains(Point2d p) {

			if (this == Entry.INFINITY) 
				return -1;
//			throw new IllegalArgumentException("perimeterContains cannot be called on Entry.INFINITY");

			if (radSq == 0.) {
				dx = x - dTri[0].x;
				dy = y - dTri[0].y;
				radSq = dx * dx + dy * dy;
			}
			dx = x - p.x;
			dy = y - p.y;
			r = dx * dx + dy * dy;

			// radSq = radius des Umkreises^2, r = Abstand Punkt-Umkreismittelpunkt^2 
			if (radSq - r > (EPSILON * radSq))
				return 1;   // p liegt im Kreis
			else if (Math.abs(r - radSq) > (EPSILON * radSq))
				return -1;  // p liegt auÃerhalb
			else
				return 0;   // p liegt auf dem Kreis			
		}

		public int indexOf(Point2d q) {

			// um Programmierfehler zu entdecken, TODO 
			// if (this == Entry.INFINITY) return -1;
			if (this == Entry.INFINITY) 
				throw new IllegalArgumentException("perimeterContains cannot be called on Entry.INFINITY");

			for (int j=0; j < dTri.length; j++) {
				if (dTri[j] != null && dTri[j].equals(q)) {
					return j;
				}
			}
			return -1;
		}

		public int indexOf(Entry neigh) {

			if (this == Entry.INFINITY) 
				return -1;

			for (int j=0; j < adjVN.length; j++) {
				if (adjVN[j] == neigh) {
					return j;
				}
			}
			return -1;
		}

		/** Zwei Entries sind dann gleich, wenn ihre Voronoi-Knoten und die Dreiecksknoten gleich sind. */
		public boolean equals(Object e) {
			return equals((Entry)e);
		}

		public boolean equals(Entry e) {
			if (this.distanceSquared(e) < EPSILON) {
//				if (this.x == e.x && this.y == e.y) {
				if (dTri == null) return true;
				int idx;
				for (int i = 0; i < 3; i++) {
					idx = e.indexOf(this.dTri[i]);
					if (idx == -1)
						return false;
				}
				return true;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return super.hashCode() * (this.dTri != null ? this.dTri.hashCode() : 1);
		}

		public String toString() {
			StringBuffer result = new StringBuffer(" Voronoi-Knoten(" + Integer.toHexString(this.hashCode()) + "/");
			result.append("[" + this.x + ", " + this.y + "]");
			result.append(", D:");
			result.append(Arrays.toString(dTri));
			if (adjVN != null) {
				result.append(", adjVN:");
				for (int i = 0; i < adjVN.length; i++) {
					result.append(" " + i + ".: ");
					if (adjVN[i] != null) {
						result.append(Integer.toHexString(adjVN[i].hashCode()));
						result.append("/");
						result.append("[" + adjVN[i].x + ", " + adjVN[i].y + "]");
					} else {
						result.append("NULL");
					}
				}
			}
			if (adjVN != null) {
				result.append(", adjVN (dTris): ");
				for (int i = 0; i < adjVN.length; i++) {
					result.append(" " + i + ".: ");
					if (adjVN[i] != null)
						result.append(Arrays.toString(adjVN[i].dTri));
					else
						result.append("NULL");
				}
			}
			return result.toString();            
		}

		/** 
		 * Liefert -1, falls dieser Eintrag keine Randzelle beschreibt, 
		 * ansonsten den Index einer Nachbarzelle im Unendlichen. 
		 * @return int
		 */
		public int isBoundaryEntry() {
			if (this == Entry.INFINITY) 
				return -1; // throw new IllegalArgumentException("Entry.isBoundaryEntry: Entry is INFINITY");

			for (int i=0; i < 3; i++) {
				//  System.out.println(adjVN[i].equals(Entry.INFINITY) + ", " + adjVN[i]);
				if (adjVN[i] == Entry.INFINITY) {                                                           
					return i;
				}                
			}            
			return -1;
		}

		/** Ã¼berprÃ¼ft, ob einer der Nachbarn ein Randelement ist */
		public boolean isBoundaryEntrySecondOrder() {
			for (int i=0; i < 3; i++) {
				if (adjVN[i].isBoundaryEntry() > -1)
					return true;
			}
			return false;
		}

		public static final Point2d INFINITY_POINT = new Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

		public static final Entry INFINITY = new Entry(INFINITY_POINT, null, null);

		private static Point2d getPerimeterCenter(Point2d[] p) {

			double delta = 4 * (p[0].x - p[1].x) * (p[1].y - p[2].y) - 4 * (p[1].x - p[2].x) * (p[0].y - p[1].y);                  

			double delta1 = 2 * (p[0].x*p[0].x + p[0].y*p[0].y - p[1].x*p[1].x - p[1].y*p[1].y) * (p[1].y - p[2].y);
			delta1 -= 2 * (p[1].x*p[1].x + p[1].y*p[1].y - p[2].x*p[2].x - p[2].y*p[2].y) * (p[0].y - p[1].y);

			double delta2 = 2 * (p[1].x*p[1].x + p[1].y*p[1].y - p[2].x*p[2].x - p[2].y*p[2].y) * (p[0].x - p[1].x);
			delta2 -= 2 * (p[0].x*p[0].x + p[0].y*p[0].y - p[1].x*p[1].x - p[1].y*p[1].y) *  (p[1].x - p[2].x);

			Point2d m = new Point2d(delta1/delta, delta2/delta);
			return m;
		}

	}  
	/* END OF CLASS ENTRY -----------------------------------
	 * ------------------------------------------------------
	 */
	
//	class AdjVNFactory {
//		
//		Stack<Entry[]> pool; 
//		
//		public AdjVNFactory() {
//			pool = new Stack<Entry[]>();
//		}
//		
//		public Entry[] bind(Entry e0, Entry e1, Entry e2) {
//			if (!pool.isEmpty()) {
//				Entry[] adjVN = pool.pop();
//				adjVN[0] = e0;
//				adjVN[1] = e1;
//				adjVN[2] = e2;
//				return adjVN;
//			} else {
//				return new Entry[] { e0, e1, e2 };
//			}
//		}
//		
//		public void release(Entry[] adjVN) {
//			adjVN[0] = null;
//			adjVN[1] = null;
//			adjVN[2] = null;
//			pool.push(adjVN);
//		}
//	}
//	
//	class DTriFactory {
//		
//		Stack<Point2d[]> pool; 
//		
//		public DTriFactory() {
//			pool = new Stack<Point2d[]>();
//		}
//		
//		public Point2d[] bind(Point2d p0, Point2d p1, Point2d p2) {
//			
//			if (!pool.isEmpty()) {
//				Point2d[] dTri = pool.pop();
//				dTri[0] = p0;
//				dTri[1] = p1;
//				dTri[2] = p2;
//				return dTri;
//			} else {
//				return new Point2d[] { p0, p1, p2 };
//			}
//		}
//		
//		public void release(Point2d[] dTri) {
//			dTri[0] = null;
//			dTri[1] = null;
//			dTri[2] = null;
//			pool.push(dTri);
//		}
//	}
//
//	class EntryFactory {
//		
//		Stack<Entry> pool; 
//		
//		public EntryFactory() {
//			pool = new Stack<Entry>();
//		}
//		
//		public Entry bind(Point2d vNode, Point2d[] dTri, Entry[] adjVN) {
//			
//			if (!pool.isEmpty()) {
//				Entry entry = pool.pop();
//				entry.x = vNode.x;
//				entry.y = vNode.y;
//				entry.dTri = dTri;
//				entry.adjVN = adjVN;
//				return entry;
//			} else {
//				return new Entry(vNode, dTri, adjVN);
//			}
//		}
//		
//		public void release(Entry entry) {
//			DelaunayTriangulator.this.dTriFactory.release(entry.dTri);
//			DelaunayTriangulator.this.adjVNFactory.release(entry.adjVN);
//			entry.version = null;
//			pool.push(entry);
//		}
//	}
//	
//	class EdgeFactory {
//	
//		Stack<VoronoiEdge> pool; 
//		List<VoronoiEdge> boundEdges; 
//		
//		public EdgeFactory() {
//			pool = new Stack<VoronoiEdge>();
//			boundEdges = new ArrayList<VoronoiEdge>();
//		}
//		
//		public VoronoiEdge bind(Entry vn1, Entry vn2) {
//			
//			if (!pool.isEmpty()) {
//				VoronoiEdge edge = pool.pop();
//				edge.vn1 = vn1;
//				edge.vn2 = vn2;
//				return edge;
//			} else {
//				return new VoronoiEdge(vn1, vn2);
//			}
//		}
//		
//		public void release(VoronoiEdge edge) {
//			edge.vn1=null;
//			edge.vn2=null;
//			edge.marked=null;
//			edge.unmarked=null;
//			edge.dn1=null;
//			edge.dn2=null;
//			
//			pool.push(edge);
//		}
//		
//		public void release() {
//			for (VoronoiEdge edge : boundEdges) {
//				release(edge);
//			}
//			boundEdges.clear();
//		}
//		
//	}
	
	/* ------------------ VORONOIEDGE  ----------------------
	 * ------------------------------------------------------
	 */
	public static class VoronoiEdge {

		protected Entry vn1, vn2;
		protected Point2d dn1, dn2;
		protected Entry marked, unmarked;

		public VoronoiEdge (Entry vn1, Entry vn2) {
			if (vn1 == null) throw new IllegalArgumentException("vn1 is null.");
			if (vn2 == null) throw new IllegalArgumentException("vn2 is null.");
			this.vn1 = vn1;
			this.vn2 = vn2;
		}

		public void setMarked(Entry vn) {
			if (vn != vn1 && vn != vn2) throw new IllegalArgumentException("vn not element of edge " + vn);
			marked = vn;
			unmarked = (vn == vn1? vn2 : vn1);
		}

		public void setDelaunayNodes(Point2d dn1, Point2d dn2) {
			if (dn1 == null) throw new IllegalArgumentException("dn1 is null.");
			if (dn2 == null) throw new IllegalArgumentException("dn2 is null.");
			this.dn1 = dn1;
			this.dn2 = dn2;
		}

		private void flip() {
			Entry tmp = vn1;
			vn1 = vn2;
			vn2 = tmp;

			tmp = unmarked;
			unmarked = marked;
			marked = tmp;

			Point2d p;
			p = dn1;
			dn1 = dn2;
			dn2 = p;
		}
		
		public Point2d[] getDelaunayNodes() {
			return new Point2d[]{this.dn1, this.dn2};
		}
		
		public Entry[] getVoronoiNodes() {
			return new Entry[]{this.vn1,this.vn2};
		}
		
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("VN: (");
			buffer.append(Integer.toHexString(vn1.hashCode()));
			buffer.append("/");
			buffer.append("(" + vn1.x + ", " + vn1.y + ")");
			buffer.append(")-(");
			buffer.append(Integer.toHexString(vn2.hashCode()));
			buffer.append("/");
			buffer.append("(" + vn2.x + ", " + vn2.y + ")");
			buffer.append(")");
			if (dn1 != null && dn2 != null) {
				buffer.append(" DN: (" + dn1.x + ", " + dn1.y);
				buffer.append(")-(");
				buffer.append("(" + dn2.x + ", " + dn2.y);
				buffer.append(")");
			} 
			buffer.append(", marked=" + (marked != null ? "(" + marked.x + ", " + marked.y + ")" : " null"));
			buffer.append(", unmarked=" + (unmarked != null ? "(" + unmarked.x + ", " + unmarked.y + ")" : " null"));
			return buffer.toString();
		}
	}

	static class PointComparator2d<F extends Point2d> implements Comparator<F> {
		public int compare(F arg0, F arg1) {
			if (arg0.x < arg1.x) return -1;
			else if (arg0.x > arg1.x) return 1;
			else {
				if (arg0.y < arg1.y) return -1;
				else if (arg0.y > arg1.y) return 1;
				else return 0;
			}
		}
	}  // End of class VoronoiEdge

	/* ------------------ VORONOIREGION  ----------------------
	 * --------------------------------------------------------
	 */
	public static class VoronoiRegion {

		Point2d dNode;
		private List<VoronoiEdge> bisectors;

		public VoronoiRegion() {
			bisectors = new ArrayList<VoronoiEdge>(5);      
		}

		public Point2d getDelaunayNode() {
			return dNode;
		}
		
		public boolean isOpen() {
			return (bisectors.size() > 0 && bisectors.get(0).vn1 == Entry.INFINITY);
		}

		public Point2d[] getPolygon() {

			Point2d[] polyPoints = new Point2d[bisectors.size()+1];
			polyPoints[0] = bisectors.get(0).vn1;
			for (int i=0; i < bisectors.size(); i++) {
				VoronoiEdge edge = bisectors.get(i);
				polyPoints[i+1] = edge.vn2;
			}
			return polyPoints;
		}

		public VoronoiEdge getBisector(int i) {
			return bisectors.get(i);
		}
		
		public int getNumBisectors() {
			return bisectors.size();
		}
		
		protected int insertBisector(VoronoiEdge edge) {

			//System.out.println("Edges VORHER: " + bisectors);
			if (edge.vn1 == Entry.INFINITY) {
				bisectors.add(0, edge);
				//System.out.println("Edges NACHHER1: " + bisectors);
				return 0;

			} else if (edge.vn2 == Entry.INFINITY) {
				bisectors.add(edge);
				//System.out.println("Edges NACHHER2: " + bisectors);
				return bisectors.size()-1;

			} else if (bisectors.size() > 0 && edge.vn2 == bisectors.get(0).vn1) {
				//System.out.println("Edges NACHHER3: " + bisectors);
				bisectors.add(0, edge);
				return 0;

			} else {

				for (int i=0; i < bisectors.size(); i++) {
					VoronoiEdge tmp = bisectors.get(i);
					if (edge.vn1 == tmp.vn2) {
						bisectors.add(i+1, edge);
						//System.out.println("Edges NACHHER4: " + bisectors);
						return i+1;
					} 
				}
			}
			if (bisectors.size() == 0) {
				bisectors.add(edge);
			} else {
				if (edge.vn1.x == edge.vn2.x && edge.vn1.y == edge.vn2.y) {
					edge.flip();
					int res = insertBisector(edge);
					if (res > -1)
						return res;
				} else {
					for (VoronoiEdge tmp : bisectors) {
						if (tmp.vn1.x == tmp.vn2.x && tmp.vn1.y == tmp.vn2.y) {
							tmp.flip();
							break;
						}
					}
					int res = insertBisector(edge);
					if (res > -1)
						return res;
				}
				System.out.println("---- VoronoiRegion.insertBisector: SOLLTE NICHT SEIN ----- ");
				System.out.println("Edge to add:" + edge);
				System.out.println("Bisherige VR:");
				System.out.println(this.toString());
			}
			return -1; // for compiler
		}

		// TODO
//		public Iterator<VoronoiRegion<E>> neighbourIterator() {
//		}

//		public VoronoiRegion<E> getNeighbour(int i) {
//		}

		public String toString() {
			StringBuffer buffer = new StringBuffer("VR: \n");
			buffer.append("Delaunay-Node: " + dNode);
			buffer.append("\n Voronoi-Edges: ");
			for (VoronoiEdge edge : bisectors) {
				if (edge == null) {
					buffer.append("\n null");
				} else {
					buffer.append("\n " + edge);
				}
			}
			buffer.append("\n");
			return buffer.toString();
		}

	}  // END of class VoronoiRegion


	/* ------------------ UNUSED CODE  ----------------------
	 * --------------------------------------------------------
	 */
	
	/* NOT USED
	 * Erzeugt aus der Menge orientierter Kanten (veq) eine Kantenfolge und 
	 * prÃ¼ft, ob diese geschlossen ist. (einfachst implementiert TODO) 
	 */
	private boolean edgesYieldPolygon(final List<VoronoiEdge> veq) {

		if (veq.size() == 0) return false;
		List<VoronoiEdge> list = new ArrayList<VoronoiEdge>();
		list.add(veq.get(0));

		for (int i=1; i < veq.size(); i++) {
			VoronoiEdge ve = veq.get(i);
			for (int j=0; j < list.size(); j++) {
				VoronoiEdge tmp = list.get(j);
				if (ve.dn1 == tmp.dn2) {
					list.add(j+1, ve);
					break;
				} else if (ve.dn2 == tmp.dn1) {
					list.add(j, ve);
					break;
				}
			}
		}
		return list.get(0).dn1 == list.get(list.size()-1).dn2;
	}
	
	/* NOT USED
	 * Berechnet den Schnittpunkt zwischen zwei Voronoi-Kanten.
	 * FÃ¼r den Fall, dass eine Voronoi-Kante mit einem Voronoi-Knoten im Unendlichen verbunden ist,
	 * wird vor der Schnittpunktbildung anhand der in der Edge gespeicherten Delaunay-Kante (dn1,dn2) ein Pseudo-Punkt berechnet.
	 * @return null, falls e1, e2 parallel oder Ã¼bereinander
	 */
	private Point2d intersectionPoint(VoronoiEdge e1, VoronoiEdge e2) {

//		System.out.println("Intersection between e1=" + e1 + " and " + e2);

		// Um den Schnittpunkt mit einer Voronoi-Kante berechnen zu kÃ¼nnen, die mit einem unendlichen Voronoi-Knoten verbunden ist, 
		// muss Ã¼ber die mitgespeicherte Delaunay-Kante ein Pseudo-Punkt berechnet werden.
		Point2d a = e1.vn1, b = e1.vn2, c = e2.vn1, d = e2.vn2; 

		if (b == Entry.INFINITY) {
			Point2d pseudo;
			if (Math.abs(e1.dn1.y-e1.dn2.y) < EPSILON)
				pseudo = new Point2d(e1.vn1.x, e1.vn1.y+50.);
			else
				pseudo = new Point2d(e1.vn1.x+50., e1.vn1.y+((-e1.dn1.x+e1.dn2.x)*50./(e1.dn1.y-e1.dn2.y)));
			b = new Entry(pseudo, null, null);
//			System.out.println("pseudo for e1:" + b);
		} 
		if (d == Entry.INFINITY) {
			Point2d pseudo;
			if (Math.abs(e2.dn1.y-e2.dn2.y) < EPSILON) 
				pseudo = new Point2d(e2.vn1.x, e2.vn1.y+50.);
			else
				pseudo = new Point2d(e2.vn1.x+50., e2.vn1.y+((-e2.dn1.x+e2.dn2.x)*50./(e2.dn1.y-e2.dn2.y)));
			d = new Entry(pseudo, null, null);
//			System.out.println("pseudo for e2:" + d);
		}

		return intersectionPoint(a, b, c, d);
	}

	/* NOT USED
	 * a und b definieren die Gerade (a + beta*(b-a)), c und d die Gerade (c + lambda *(d-c))
	 * Die Methode gibt das Beta fÃ¼r den Schnittpunkt zurÃ¼ck */
	private double[] intersectionParameters(Point2d a, Point2d b, Point2d c, Point2d d) {

		double alpha_z = a.x*(c.y-d.y)+c.x*(d.y-a.y)+d.x*(a.y-c.y);
		double alpha_n = a.x*(c.y-d.y)+b.x*(d.y-c.y)+c.x*(b.y-a.y)+d.x*(a.y-b.y);
		double alpha = alpha_z / alpha_n;

		double beta_z = c.x*(a.y-b.y)+a.x*(b.y-c.y)+b.x*(c.y-a.y);
		double beta_n = c.x*(a.y-b.y)+d.x*(b.y-a.y)+a.x*(d.y-c.y)+b.x*(c.y-d.y);
		double beta = beta_z / beta_n;

		return new double[] { alpha, beta };
	}

	/* NOT USED */
	private Point2d intersectionPoint(Point2d a, Point2d b, Point2d c, Point2d d) {

		double beta[] = intersectionParameters(a, b, c, d);

		double x = a.x + beta[0]*(b.x-a.x);
		double y = a.y + beta[0]*(b.y-a.y);

		if (Double.isInfinite(x) || Double.isInfinite(y)) 
			return null;
		else        
			return new Point2d(x, y);
	}

}

/**
 * Datenstruktur fÃ¼r die Ablage von 2D-Punkten in einem quadratischen Gitter.
 * @author kaapke, Juni 2007
 * 
 * TODO Irgendwie mÃ¼ssen sich Quadtree und ArrayGrid gewinnbringend kombinieren lassen...
 *
 * @param <E>
 */
final class ArrayGrid<E extends Point2d> implements TSet<E> {

	public static final boolean REORGANIZE = false;  // Schalter fÃ¼r automatische Restrukturierung des Gitters bei Ãberschreiten von MAXCELLSIZE bzw. *_OUTERRIM
	public static final int DEBUG = -1;

	protected static double d(Point2d a, Point2d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx*dx+dy*dy);
	}

	protected static double d2(Point2d a, Point2d b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return dx*dx+dy*dy;
	}

	final class Cell<E> {

		public final int i, j;
		private List<E> container;

		public Cell(int i, int j) {
			this.i = i;
			this.j = j;

		}

		public void add(E e) {
			if (container == null) {
				container = new ArrayList<E>(); // (MAXCELLSIZE/4);
			}
			container.add(e);
			if (REORGANIZE && container.size() > MAXCELLSIZE) {
				if (DEBUG > 2) System.out.println("CELL (" + i + ", " + j + ") Ã¼berschreitet MAXCELLSIZE.");
				reorganize();
			}
		}

		public void remove(E e) {
			container.remove(e);
//			if (container.size() == 0) {
//			container = null;
//			}
		}
	}

	final class RefPointDistanceComparator<E extends Point2d> implements Comparator<E>{
		
		private E ref;
		
		private void setRefPoint(E ref) {
			this.ref = ref;
		}
		
		 public int compare(E arg0, E arg1) {
			 double d_p1 = ArrayGrid.d2(arg0, ref);
			 double d_p2 = ArrayGrid.d2(arg1, ref);

			 if (d_p1 < d_p2) return -1;
			 else if (d_p1 == d_p2) return 0;
			 else return 1;
		 }
	}
	
	private final int MAXCELLSIZE = 250;
	private final int MAXCELLSIZE_OUTERRIM = 500;

	private Cell<E>[][] cells;              // Gitterzellen
	private List<E> outerRim;               // FÃ¼r alles auÃerhalb der Gittergrenzen

	public double xmin, ymin, xmax, ymax;   // Bounding box des Gitters
	public double[] mins, maxs;             // Bounding box der Punkte
	private int nx, ny;                     // Anzahl der Zellen in x- bzw. y-Richtung

	private double dx, dy;                  // Gitterweiten in x- bzw. y-Richtung
	private int n;                          // Anzahl der Punkte im Gitter

	private int minIdx;                     // Erster Zellenindex, in dem Punkte sind.

	private final RefPointDistanceComparator<E> comp;
	private final PriorityQueue<E> queue;
	
	/**
	 * Legt ein neues Gitter an.
	 */
	public ArrayGrid() {
		this(0., 0., Double.MAX_VALUE-1., Double.MAX_VALUE-1., 10, 10);
	}

	/**
	 * Legt ein neues Gitter mit den Ã¼bergebenen Abmessungen an.
	 * @param xmin, ymin, xmax, ymax: bounding coordinates
	 * @param nx, ny: Anzahl Zellen in x bzw. y-Richtung
	 */
	public ArrayGrid(double xmin, double ymin, double xmax, double ymax, int nx, int ny) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		if (ymax - ymin == 0.) ymax = ymin + 1.0;
		if (xmax - xmin == 0.) xmax = xmin + 1.0;
		this.nx = nx;
		this.ny = ny;

		cells = new Cell[nx][ny];
		for (int x = 0; x < nx; x++) {
			for (int y=0; y < ny; y++) {
				cells[x][y] = new Cell<E>(x, y);
			}
		}
		dx = (xmax - xmin) / (double)nx;
		dy = (ymax - ymin) / (double)ny;

		outerRim = new ArrayList<E>(MAXCELLSIZE_OUTERRIM/4);
		
		comp = new RefPointDistanceComparator<E>();
		queue = new PriorityQueue<E>(10, comp);
	}

	/* TSet interface */
	public void add(E p) {
		addPoint(p);
	}

	/** 
	 * FÃ¼gt den Ã¼bergebenen Punkt ins Gitter ein.
	 * @param p
	 */
	public void addPoint(E p) {
		minmax(p);
		if (p.x > xmax || p.y > ymax || p.x < xmin || p.y < ymin) {
			if (DEBUG > 2) System.out.println("Point p: " + p + " is beyond the grid boundaries: xmin=" + xmin + ", ymin=" + ymin + ", xmax=" + xmax + ", ymax=" + ymax);
			outerRim.add(p);
			n++;
			if (REORGANIZE && outerRim.size() > MAXCELLSIZE_OUTERRIM)  
				reorganize();
			return;
		}
		int[] i = cellCoords(p);
//		System.out.println("Punkt " + p + "in Zelle (" + i[0] + ", " + i[1] + ").");
		cells[i[0]][i[1]].add(p);
		minIndex(i[0], i[1]);
		n++;
	}

	/**
	 *  FÃ¼gt alle Punkte der Collection ins Grid
	 */
	public void addAll(Collection<E> col) {
		for(E p : col) {
			addPoint(p);
		}
	}

	/** 
	 * Entfernt den Ã¼bergebenen Punkt aus dem Gitter.
	 * @param p
	 */
	public Object remove(E p) {
		if (!contains(p)) {
			return false;
		}
		if (outerRim.contains(p)) {
			outerRim.remove(p);
		} else {
			int[] i = cellCoords(p);
			cells[i[0]][i[1]].remove(p);

		}
		n--;
		return true;
	}

	public boolean contains(E p) {
		if (p.x > xmax || p.y > ymax || p.x < xmin || p.y < ymin) {
			return outerRim.contains(p);
		}
		int[] i = cellCoords(p);
		if (cells[i[0]][i[1]].container != null) {
			return cells[i[0]][i[1]].container.contains(p);
		} else {
			return false;
		}
	}

	public int size() {
		return n;
	}

	public List<E> getPoints() {
		List<E> result = new ArrayList<E>(n);

		for (int x=0; x < nx; x++) {
			for (int y=0; y < ny; y++) {
				List<E> container = cells[x][y].container;
				if (container != null) {
					for (E p : container) {
						result.add(p);
					}
				}
			}
		}
		for (E p : outerRim) {
			result.add(p);
		}

		return result;
	}

	/*
	 * Laufe Ã¼ber den Index:
	 *   FÃ¼r den aktuellen Index:
	 *      Wenn die Zelle einen Container enthÃ¤lt,  
	 */
	 public Iterator<E> iterator() {
		return new Iterator<E>() {

			int idx = minIdx;
			int z = 0;
			Iterator<E> inCellIt;
			boolean end = false;
			E next = getNext();
			Cell<E> c;

			public E getNext() {

				// Falls ein brauchbarer InCellIterator da ist, gib sein NÃCHSTES ELEMENT zurï¿½ck.
				if (inCellIt != null && inCellIt.hasNext()) {
					z++;
					return inCellIt.next();
				}

				while ((c = getCell(idx)) != null && c.container == null) {
					idx++;
				}

				// Falls der Index am Ende angekommen ist, gib NULL zurÃ¼ck.
				if ((c = getCell(idx)) == null) {
					if (!end) {
						inCellIt = outerRim.iterator();
						end = true;
						return getNext();
					} else {
						return null;
					}
				} 

				// Falls nicht, erhÃ¶he den Laufindex.
				idx++;

				// Falls die Zelle Punkte enthÃ¤lt, erzeuge einen neuen Iterator
				if (c.container != null) {
					inCellIt = c.container.iterator();
				}

				return getNext();
			}

			public E next() {
				if (next == null)
					throw new NoSuchElementException();
				E result = next;
				next = getNext();
				return result;
			}

			public boolean hasNext() {
				return next != null;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	 }

	 private void minIndex(int i, int j) {
		 int z = i*nx+j; 
		 if (z < minIdx) 
			 minIdx = z;
	 }

	 /** 
	  * Berechnung des laufenden Zellenindex idx
	  * @param p
	  * @return
	  */
	 public int cellIndex(E p) {
		 int[] c = cellCoords(p);
		 return c[0] * nx + c[1];        // CHECK
	 }

	 /** 
	  * Berechnung der Zellenkoordinate (i,j)
	  * @param p
	  * @return
	  */
	 public int[] cellCoords(Point2d p) {
		 return new int[] { (int)((p.x-xmin)*0.99/dx), (int)((p.y-ymin)*0.99/dy) };     // CHECK
	 }

	 /** 
	  * Berechnung der Zellenkoordinate (i,j)
	  * @param p
	  * @return
	  */
	 public int[] cellCoords(int idx) {
		 return new int[] { (idx % nx), (idx / nx) }; 
	 }

	 private Cell<E> getCell(int idx) {
		 if (idx >= (nx*ny)) 
			 return null;
		 int[] c = cellCoords(idx);
		 return getCell(c);
	 }

	 private Cell<E> getCell(int[] c) {
		 return cells[c[0]][c[1]];
	 }

	 public E getNearestElement(Point2d q) {
		 List<E> result = getNextPoints(q, 1);
		 return result.get(0);
	 }

	 public List<E> getNextPoints(final Point2d ref, int n) {

		 comp.setRefPoint((E)ref);
		 queue.clear();

		 // Berechnung des Radius, in dem der nÃ¤chste Nachbar ermittelt werden kann
		 // Lade alle Punkte innerhalb des critR in die PriorityQueue.
		 // Falls genug Punkte zusammenkommen (also n), fertig.
		 // Ansonsten: ErhÃ¶hung des Radius um einen Block

		 int s = 0, i, j, x, y;
		 double r, r2;

		 while (queue.size() < n) {

			 r = critR(ref, s);
			 r2 = r*r;

			 int[] c = cellCoords(ref);
			 i = c[0];
			 j = c[1];

			 for(x=((i-s)<0?0:(i-s)); x <= (i+s>(nx-1)?(nx-1):(i+s)); x++) {
				 for(y=((j-s)<0?0:(j-s)); y <= (j+s > (ny-1)?(ny-1):(j+s)); y++) {
					 if (cells[x][y].container != null)
						 for (E p : cells[x][y].container) {
							 if (d2(p, ref) < r2)
								 if (!queue.contains(p))
									 queue.add(p);
						 }
				 }
			 }
			 for (E p : outerRim) {
				 if (d2(p, ref) < r2)
					 if (!queue.contains(p))
						 queue.add(p);
			 }
			 s++;
		 }

		 ArrayList<E> result = new ArrayList<E>();
		 for (int k=0; k < n; k++) {
			 result.add(queue.poll());
		 }
		 return result;
	 }

	 private double critR(Point2d ref, int r) {

		 int[] c = cellCoords(ref);
		 double yo = ymin + (c[1]-r)*dy;
		 double yu = ymin + (c[1]+1+r)*dy;
		 double xl = xmin + (c[0]-r)*dx;
		 double xr = xmin + (c[0]+1+r)*dx;
		 double xd = Math.min(ref.x-xl, xr-ref.x);
		 double yd = Math.min(ref.y-yo, yu-ref.y);
		 return Math.min(xd, yd);
	 }

	 protected void minmax(E p) {
		 if ( n==0 ) {
			 mins = new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
			 maxs = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
		 }

		 if (p.x < mins[0]) {  mins[0] = p.x; }
		 if (p.x > maxs[0]) {  maxs[0] = p.x; }
		 if (p.y < mins[1]) {  mins[1] = p.y; }
		 if (p.y > maxs[1]) {  maxs[1] = p.y; }
	 }

	 /** 
	  *  Erzeugt ein neues Gitter, das die enthaltenen Punkte ohne ÃberstÃ¤nde abdeckt.
	  *  - trivialer Ansatz TODO
	  */ 
	 public void reorganize() {

		 if (DEBUG > 1) System.out.println(System.currentTimeMillis() + " - Reorganizing grid ... " + this.hashCode() + ": n=" + n + ", davon im Outer Rim: " + outerRim.size());
//		 System.out.println("  VORHER:  xmin=" + xmin + ", ymin=" + ymin + ", xmax=" + xmax + ", ymax=" + ymax);

		 ArrayGrid<E> copy;
		 if (outerRim.size() > MAXCELLSIZE_OUTERRIM) {
			 copy = new ArrayGrid<E>(mins[0]-0.1*(maxs[0]-mins[0]), mins[1]-0.1*(maxs[1]-mins[1]), maxs[0]+0.1*(maxs[0]-mins[0]), maxs[1]+0.1*(maxs[1]-mins[1]), nx, ny);
			 if (DEBUG > 1) System.out.println(" reorganize.1: Erzeuge neues Grid(: xmin=" + copy.xmin + ", ymin=" + copy.ymin + ", xmax=" + copy.xmax + ", ymax=" + copy.ymax + ", nx=" + nx + ", ny=" + ny + ", n=" + n);
		 } else {
			 if (DEBUG > 1) System.out.println(" reorganize.2: Erzeuge neues Grid: xmin=" + xmin + ", ymin=" + ymin + ", xmax=" + xmax + ", ymax=" + ymax + ", nx=" + (2*nx) + ", ny=" + (2*ny) + ", n=" + n);
			 copy = new ArrayGrid<E>(mins[0], mins[1], maxs[0], maxs[1], 2*nx, 2*ny);
		 }
		 for (E p : this) {
			 copy.addPoint(p);
		 }

		 this.cells = copy.cells;
		 this.nx = copy.nx; this.ny = copy.ny;
		 this.xmin = copy.xmin; this.xmax = copy.xmax;
		 this.ymin = copy.ymin; this.ymax = copy.ymax;
		 this.mins = copy.mins; this.maxs = copy.maxs;
		 this.dx = copy.dx; this.dy = copy.dy;
		 this.outerRim = copy.outerRim;

		 if (DEBUG > 1) System.out.println(System.currentTimeMillis() + " - Reorganizing grid completed." + this.hashCode() + ": n=" + n + ", davon im Outer Rim: " + outerRim.size());
//		 System.out.println("  NACHHER:  xmin=" + xmin + ", ymin=" + ymin + ", xmax=" + xmax + ", ymax=" + ymax);
	 }

	 public void reorganize(double xmin, double ymin, double xmax, double ymax) {
		 reorganize(xmin, ymin, xmax, ymax, this.nx, this.ny);
	 }

	 public void reorganize(double xmin, double ymin, double xmax, double ymax, int nx, int ny) {

		 ArrayGrid<E> copy = new ArrayGrid<E>(xmin, ymin, xmax, ymax, nx, ny);

		 for (E p : this) {
			 copy.addPoint(p);
		 }

		 this.cells = copy.cells;
		 this.nx = copy.nx; this.ny = copy.ny;
		 this.xmin = copy.xmin; this.xmax = copy.xmax;
		 this.ymin = copy.ymin; this.ymax = copy.ymax;
		 this.mins = copy.mins; this.maxs = copy.maxs;
		 this.dx = copy.dx; this.dy = copy.dy;
		 this.outerRim = copy.outerRim;
	 }

	 public String toString() {
		 StringBuffer buffer = new StringBuffer("ArrayGrid[\n");
		 buffer.append(" xmin=" + xmin + ", ymin=" + ymin + ", xmax=" + xmax + ", ymax=" + ymax + "\n");
		 for (E p : this) {
			 buffer.append(p.toString());
			 buffer.append("\n");
		 }
		 buffer.append("]\n");
		 return buffer.toString();
	 }
}

interface TSet<E extends Point2d> extends Iterable<E> {

	public void add(E obj);
	public Object remove(E obj);
	public boolean contains(E obj);
	public int size();

	public E getNearestElement(Point2d p);

}