package bijava.geometry.dim2;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author kaapke, 15.12.2004
 * @deprecated
 */
public class VoronoiTriangulation extends Triangulation2d {

    private PointSet nodes;    
    private VoronoiDirectory dir;
    
    private DTView view;
    
    public VoronoiTriangulation(PointSet p) {
    	this(p.toArray(new IdPoint[p.size()]));    	  
    }
    
    public VoronoiTriangulation(Point2d[] p) {
        if (p.length < 3) throw new IllegalArgumentException("Mindestens drei Punkte!");        
        nodes = new PointSet();     
        
        double[] rect = rectangle2D(p);
        dir = new VoronoiDirectory(rect);
        for (int i = 0; i < p.length; i++) {
            addPoint(new IdPoint(p[i]));
        }
        dir.removeRect();
        
        triangles = new ArrayList<Triangle2d>(dir.size());
        int i=0;
        Iterator it = dir.entryIterator();
        while(it.hasNext()) {
            VoronoiDirectory.Entry entry = (VoronoiDirectory.Entry) it.next();
            Triangle t =entry.nodes;
            triangles.add(new Triangle2d(t.p[0].p, t.p[1].p, t.p[2].p));
            i++;
        }
        
    }
    
    public Triangle2d[] getTriangles(){
        Triangle2d[] triangles = new Triangle2d[dir.size()];
        int i=0;
        Iterator it = dir.entryIterator();
        while(it.hasNext()) {
            VoronoiDirectory.Entry entry = (VoronoiDirectory.Entry) it.next();
            Triangle t =entry.nodes;
            triangles[i] = new Triangle2d(t.p[0].p, t.p[1].p, t.p[2].p);
            i++;
        }
        return triangles;
    }

    /** F�ge den Punkt q in die Triangulierung ein */
    private void addPoint(IdPoint q) {    	        
                      
        /* (1) Ermittle Vq und Pq */ 
        PointSet[] pointSets = affectedNodes(q);    
        
        //System.out.println("affected VoronoiNodes:" + pointSets[0]);
        
        /* (2) Bestimme die betroffenen Voronoi-Strecken, Sq, auf denen jeweils eine neue Ecken gefunden werden muss. 
         *     Bestimme die zugeh�rigen Delaunay-Kanten, Eq. */
        Vector[] edgeSets = affectedEdges(pointSets);
        
        /* (3) �bergabe Eq (Delaunay-Kanten) */
        Vector newEdgePoints = newEdgePoints(q, edgeSets[0], edgeSets[1]);                   
        
        /* (4) �bergabe Vq (toDelete), �bergabe Sq (Voronoi-Strecken) */
        actualizeDirectory(pointSets[0], edgeSets[0], newEdgePoints);    
        
        nodes.add(q);
        
        //System.out.println("Added " + q+ ": " + dir.toString());
    }                  
    
    /**
     * Step 1: Derive affected nodes in Delaunay and Voronoi diagram
     * 
     * Die Menge Vq der Ecken, die nahestes Nachbarn von q sind, 
     * und die zugeh�rige Menge Pq der Knoten, die Nachbarn von q sind, 
     * werden durch eine Nachbarschaftssuche bestimmt.
     * 
     * Pq und Vq d�rfen aufgrund der gew�hlten Ausgangsstruktur nicht leer sein.
     * 
     * @param q
     * @return 	Vector[0]: Vq nearest voronoi points
     * 			Vector[1]: Pq associated nodes
     */
    private PointSet[] affectedNodes(IdPoint q) {
       
        /* Verbesserung: Nicht �ber alle Ecken iterieren, sondern nur �ber: 
         *  - die Nachbarn des Dreiecks, in das der Punkt q f�llt ;-)
         */
        
        PointSet vSet = new PointSet(IdPoint.IdComparator.getInstance());  // Vq
        PointSet dSet = new PointSet(IdPoint.IdComparator.getInstance());  // Pq
        
        // �ber alle Ecken
        Iterator it = dir.voronoiIterator();        
        while (it.hasNext()) {
            // f�r jede Ecke ...
            IdPoint v = (IdPoint) it.next();
            // ermittle die assoziierten Knoten 
            Triangle aufUmkreis = dir.getAssociatedNodes(v);
            
            // Liegt q in dem leeren Umkres mit der Ecke v als Mittelpunkt, 
            if (aufUmkreis.perimeter().contains(q)) {
            	// dann ist v n�chster Nachbar von q
                vSet.add(v);
                dSet.add(aufUmkreis.p);
            }                          
        }
        return new PointSet[] { vSet, dSet};
    }        
    
    /** 
     * Step 2: Derive affected edges in Delaunay and Voronoi diagram 
     * 
     * Jeder zu l�schenden Ecke in Vq sind drei assoziierte Knotenpaare, 
     * die jeweils ein Kante des Delaunay-Diagramms darstellen, und 
     * drei zugeh�rige Eckenpaare, die jeweils eine Strecke des Voronoi-Diagramms darstellen und
     * senkrecht auf einer Kante stehen, zugeordnet.
     * 
     * Diejenigen Strecken (Voronoikanten), die genau eine zu l�schende Ecke besitzen, 
     * werden in der Folge Sq zusammengefasst. 
     * Due zugeh�rigen (Delaunay-)Kanten werden in der Folge Eq zusammengefasst.  
     * 
     * @param PointSet[0] Vq, PointSet[1] Pq  
     * @return 	Vector[0] - Voronoi-Edges Sq
     * 			Vector[1] - Delaunay-Edges Eq  			
     */
    private Vector[] affectedEdges(PointSet[] set) {        
                
        Vector vEdges = new Vector();  // Sq
        Vector dEdges = new Vector();  // Eq
        
        // f�r jede Ecke in set[0], Vq - affected voronoi nodes
        Iterator toDelete = set[0].iterator();
        while(toDelete.hasNext()) {
            
            IdPoint edge = (IdPoint) toDelete.next(); // zu l�schende Ecke
            IdPoint[] nodes = dir.getAssociatedNodes(edge).p;
            IdPoint[] edgePoints = dir.getNeighbours(edge);
            
            // Bilde die Knotenpaare
            for (int i=0; i < edgePoints.length; i++) {
                // Diese assoziierte Ecke edgePoints[i] darf nicht in Vq enthalten sein,
                // da Sq nur Voronoikanten mit genau einer zu l�schenden Ecke enthalten darf.
                if (!set[0].contains(edgePoints[i])) {
	                IdPoint.Pair vPair = new IdPoint.Pair(edge, edgePoints[i]);
	                IdPoint.Pair dPair = new IdPoint.Pair(nodes[(i+1)%3], nodes[((i+2)%3)]);
	                // F�ge die Kanten den Mengen hinzu, sofern die Delaunaykante nicht schon enthalten ist.
	                if (!dEdges.contains(dPair)) {	                
	                    // vPair (x,INF) muss mehrfach in vEdges enthalten sein k�nnen.
	                    // markiere den Knoten, der gel�scht wird.
	                    vPair.mark(edge);
	                    vEdges.add(vPair);	                    
	                	dEdges.add(dPair);
	                }
                }
            }   
            
            // Entferne den Eintrag zur Ecke aus der Datenstruktur
            dir.removeEntry(edge); 
            
        }
        return new Vector[] { vEdges, dEdges };
    }
    
    /** 
     * Step 3: Calculate the new voronoi edges and add them accordingly to the directory
     * 
     * Der einzuf�gende Knoten q bildet mit jeder Kante (pi,pj) aus Eq ein Dreieck (q, pi, pj).
     * Die Umkreismittelpunkte dieser Dreicke bilden die neuen Ecken im Voronoi-Diagramm.
     * 
     * Besitzen zwei Dreicke einen gemeinsamen Knoten p, dann besitzen sie eine gemeinsame Kante (p,q)
     * und folglich sind die zu den beiden Dreicken geh�renden neuen Ecken benachbart.
     * 
     * @param q - der einzuf�gende Delaunay-Punkt
     * @param dpSet - die Menge der beeinflussten Delaunay-Kanten Eq
     * @return Points - die neuen Voronoi-Ecken
     */
    private Vector newEdgePoints(IdPoint q, Vector vpSet, Vector dpSet) {
        
        Vector newEdgePoints = new Vector();        
        Vector triangles = new Vector();
                
        // Berechne Position der neuen Voronoiecken        
        for (int i=0; i < dpSet.size(); i++) {   // f�r jedes Delaunayknoten-Paar in Eq     
            IdPoint.Pair pair = (IdPoint.Pair) dpSet.get(i);
            // Bilde ein Dreieck mit dem Knoten q und der "Nachbar"-kante.            
            try {
                Triangle t = new Triangle(q, pair.p1, pair.p2);
                triangles.add(t);            
                // Berechne sein Umkreismittelpunkt und merke den Knoten vor
                newEdgePoints.add(t.perimeter().m);
            } catch (IllegalArgumentException ex) {
                dpSet.remove(i);
                vpSet.remove(i--);
            }            
        }
        
        // F�ge die neuen Ecken in die Datenstruktur ein.
        // Ermittle dazu f�r jede neue Voronoiecke ihre drei Nachbarecken.
        for (int i=0; i < dpSet.size(); i++) {            
            IdPoint.Pair a = (IdPoint.Pair) dpSet.get(i);                                   
            IdPoint[] neighs =  new IdPoint[] { IdPoint.INFINITY, IdPoint.INFINITY, IdPoint.INFINITY };            
            
            // Ermittle, ob das Dreieck (q, a.p1, a.p2) einen gemeinsamen Knoten mit einem anderen Dreieck (q, b.p1, b.p2) hat            
            for (int j=0; j < dpSet.size(); j++) {
                if (i == j) continue;
                IdPoint.Pair b = (IdPoint.Pair) dpSet.get(j);
                IdPoint tmp = IdPoint.Pair.section(a, b);                                
                if (tmp != null) {
                    // Der Index des gemeinsamen Knotens im Vergleichsdreick t (q, b.p1, b.p2) 
                    // entspricht dem Index der Voronoiecke in 'neighs'.
                    Triangle t = (Triangle) triangles.get(j);
                    int index = t.getIndex(tmp);               
                    neighs[index] = (IdPoint) newEdgePoints.get(j);                                                           
                }
            }
            dir.addEntry((IdPoint)newEdgePoints.get(i), (Triangle) triangles.get(i), neighs);
        }        
        return newEdgePoints;
        
    }
    
    /**
     * Step 4: Exchange removed edge partners by the new edges in the corresponding entries in the directory
     * 
     * Jeder Kante (pi, pj) aus Eq ist eine neue Ecke zugeorndet. Zu jeder dieser Kanten geh�rt eine Strecke (vr, vs) aus Sq 
     * mit der gel�schten Ecke vr. Die gel�schte Ecke wird durch die der Kante zugeordneten, neuen Ecke ersetzt. 
     * 
     * @param toDelete Vq - affected Voronoi points 
     * @param voronoiEdges Sq - affected Voronoi edges
     * @param newEdgePoints calculated new Voronoi points
     */
    private void actualizeDirectory(PointSet toDelete, Vector voronoiEdges, Vector newEdgePoints) {               
        
        // Ersetze bei allen Strecken in Sq (voronoiEdges), die zu l�schende Ecke 
        // durch die �ber den Laufindex korrespondierende Ecke. 
        for (int i = 0; i < voronoiEdges.size(); i++) {              
            
            // Ermittle den Partner der gel�schten Ecke
            IdPoint.Pair pair = (IdPoint.Pair) voronoiEdges.get(i);            
            IdPoint tmp = pair.getUnmarkedPoint();
            if (tmp.equals(IdPoint.INFINITY)) continue;                       
            IdPoint tmp_del = pair.getMarkedPoint();
            
            // Setze die fehlende Nachbarecke in den neuen Eckeintr�gen.            
            IdPoint newEdgePoint = (IdPoint) newEdgePoints.get(i);          
            IdPoint[] neighs = dir.getNeighbours(newEdgePoint);
            //System.out.println("new:" + newEdgePoint);            
            neighs[0] = tmp;                    
            //System.out.println("tmp:" + tmp);
            
            // Ersetze im Eintrag f�r die Ecke 'tmp' (im Voronoi-Diagramm verbliebene Ecke f�r eine Strecke auf der ein Knoten gel�scht wurde.), 
            // die zu l�schende Ecke mit der �ber den Index passenden, neuen Ecke.            
            neighs = dir.getNeighbours(tmp);
                        
            for (int j=0; j < neighs.length; j++) {
                if (neighs[j].equals(tmp_del))
                    neighs[j] = newEdgePoint;                                              
            }            
        }               
    }       
    
    // -------------------- Punkt entfernen -------------------------------------------
    
    /** Entfernt den �bergebenen Punkt q aus der Triangulierung */
    public void removePoint(IdPoint q) {
                  
        PointSet[] nodeSets = affectedNodesForRemoval(q);      
        
        // trianguliere die Zelle, die durch das Entfernen von q entsteht. 
        VoronoiTriangulation vt = new VoronoiTriangulation(nodeSets[1]);
        IdPoint[] test = nodeSets[1].toArray();
        for (int i = 0; i < test.length; i++)  
            vt.addPoint(test[i]);                    
        vt.dir.removeRect();
        
        // f�r den Fall, dass die Zelle nicht konvex ist, werden "ueberzaehlige" Dreiecke entfernt
        ensureConvexTriangulation(q, vt);        
        
        actualizeDirectoryForRemoval(nodeSets[0], vt);
        
        // Knoten aus redundanter Knotenmenge entfernen
        nodes.remove(q);        
        
        System.out.println(dir);
    }           
    
    private void ensureConvexTriangulation(IdPoint q, VoronoiTriangulation vt) {           
        
        Vector toDelete = new Vector();
        
        Iterator it = vt.dir.voronoiIterator();
        while (it.hasNext()) {
            IdPoint vp = (IdPoint) it.next();
            Triangle newT = vt.dir.getAssociatedNodes(vp);
           
            Iterator it2 = nodes.iterator();
            while (it2.hasNext()) {
                IdPoint dp = (IdPoint) it2.next();
                if (dp.equals(q) || vt.nodes.contains(dp)) continue;
                
                if (newT.perimeter().contains(dp)) {
                    toDelete.add(vp);
                    
                }
            }
        }   
        
        it = toDelete.iterator();
        while(it.hasNext()) {
            vt.dir.removeEntry((IdPoint) it.next());            
        }
        
    }
    
    /** Step Del 1: Ermittelt die vom L�schen betroffenen Knoten */
    private PointSet[] affectedNodesForRemoval(IdPoint q) {
        
        PointSet vSet = new PointSet(IdPoint.IdComparator.getInstance());
        PointSet dSet = new PointSet(IdPoint.IdComparator.getInstance());
        
        // �ber alle Ecken
        Iterator it = dir.voronoiIterator();        
        while (it.hasNext()) {
            // f�r jede Ecke ...
            IdPoint v = (IdPoint) it.next();            
            // ermittle die assoziierten Knoten 
            Triangle aufUmkreis = dir.getAssociatedNodes(v);
            for (int i=0; i < 3; i++) {
                if (aufUmkreis.p[i].equals(q)) { 
                    vSet.add(v);
                    dSet.add(aufUmkreis.p[(i+1)%3]);
                    dSet.add(aufUmkreis.p[(i+2)%3]);
                }
            }                                 
        }
        return new PointSet[] { vSet, dSet };
    }
    
    /** Diese Methode ermittelt die Voronoiknoten, die zu den Zellen benachbart sind, die vom Entfernen betroffen sind. */
    private PointSet neighboursOfAffectedCells(PointSet affectedVoronoiNodes) {
        
        PointSet result = new PointSet();
        // Iteriere �ber die Voronoiknoten, deren Zellen vom Entfernen betroffen sind
        Iterator it = affectedVoronoiNodes.iterator();
        while(it.hasNext()) {
            IdPoint tmp = (IdPoint) it.next();
            IdPoint[] neighs =  dir.getNeighbours(tmp);
            // ermittle f�r jeden Voronoiknoten seine Nachbarn, die nicht betroffen sind
            for (int i = 0; i < neighs.length; i++)
                if (!neighs[i].equals(IdPoint.INFINITY) && !affectedVoronoiNodes.contains(neighs[i]))
                    result.add(neighs[i]);
        }        
        // ... das sind die Voronoiknoten, die neue Nachbarn bekommen
        return result;
    }
    
    /** Step Del 2 - remove */
    private void actualizeDirectoryForRemoval(PointSet affectedVoronois, VoronoiTriangulation vt) {        		    	    
        
        // Die Teiltriangulierung vt enth�lt die neuen Voronoizellen samt inneren Nachbarschaften.
        // F�r diese Zellen werden nun zunaechst die gemeinsamen Kanten mit der bestehenden Triangulierung (this) 
        // und damit die (aeusseren) Nachbarschaften ermittelt.
        PointSet neighbouredCells = neighboursOfAffectedCells(affectedVoronois);
        //System.out.println("neighbouredCells: " + neighbouredCells);
        
        // Laufe �ber alle neuen Dreiecke...
        Iterator it = vt.dir.entryIterator();
        while(it.hasNext()) {
            VoronoiDirectory.Entry e = (VoronoiDirectory.Entry) it.next();
            Triangle newT = e.nodes;
            // Hole ein relevantes Dreieck aus der bestehenden Triangulierung, um auf Nachbarschaft zu testen.
            Iterator it2 = neighbouredCells.iterator();
            while (it2.hasNext()) {
                IdPoint voronoi = (IdPoint) it2.next();
                Triangle oldT = dir.getAssociatedNodes(voronoi);
                // ermittle die gemeinsame Kante von neuem und alten Dreieck
                IdPoint.Pair sharedEdge = newT.section(oldT);              
                if(sharedEdge != null) {
                    // die Nachbarschaft ist ermittelt, 
                    // jetzt muss jeweils der korrekte Index des neuen/alten Nachbarn ermittelt werden
                    // - f�r das neue Dreieck:                   
                    int idx1 = newT.getIndex(sharedEdge.p1);
                    int idx2 = newT.getIndex(sharedEdge.p2);                    
                    IdPoint[] neighs = vt.dir.getNeighbours(newT.perimeter().m);
                    neighs[3-idx1-idx2] = oldT.perimeter().m;
                    
                    // - f�r das alte Dreieck                    
                    idx1 = oldT.getIndex(sharedEdge.p1);
                    idx2 = oldT.getIndex(sharedEdge.p2);                    
                    neighs = dir.getNeighbours(oldT.perimeter().m);
                    neighs[3-idx1-idx2] = newT.perimeter().m;
                }
            }
            dir.addEntry(newT.perimeter().m, newT, vt.dir.getNeighbours(newT.perimeter().m)); 
        }
        
        // Entferne die betroffenen Ecken aus dem Directory
        it = affectedVoronois.iterator();
        while (it.hasNext()) {
           dir.removeEntry((IdPoint) it.next());
        }
    }
    
    /**
     * Berechnet das die Punktmenge umschlie�ende Rechteck
     * @param p
     * @return xmin = [0]; xmax = [1]; ymin = [2]; ymax =[3]
     */
    private double[] rectangle2D(Point2d[] p) {
        if (p.length < 2) throw new IllegalArgumentException("at least 2 points are necessary.");
        double[] v = new double[4];
        v[0] = v[1]= p[0].x;
        v[2] = v[3]= p[0].y;
        for (int i = 1; i < p.length; i++) {
            if (p[i].x < v[0]) v[0] = p[i].x;
            else if (p[i].x > v[1]) v[1] = p[i].x;
            if (p[i].y < v[2]) v[2] = p[i].y;
            else if (p[i].y > v[3]) v[3] = p[i].y;
        }
        return v;
    }
    
    /* Test */
    public static void main(String[] args) {
        
        /*
        int n = 2;
              
        IdPoint[] test = new IdPoint[n * n];
        for (int i = 0; i < n; i++) 
            for (int j = 0; j < n; j++) 
                test[i*n+j] = new IdPoint(50 + i * 50.0, 50+ j * 50.0);                            
        */
        
/*        IdPoint p1 = new IdPoint(50.,50.);
        IdPoint p2 = new IdPoint(200.,50.);
        IdPoint p3 = new IdPoint(200.,200.);
        IdPoint p4 = new IdPoint(50.,200.);
        IdPoint p5 = new IdPoint(125.0,125.0);
        IdPoint[] test = new IdPoint[] {p1, p2, p3, p4, p5};
*/        

        Point2d p1 = new Point2d(50.,50.);
        Point2d p2 = new Point2d(200.,50.);
        Point2d p3 = new Point2d(200.,200.);
        Point2d p4 = new Point2d(50.,200.);
        Point2d p5 = new Point2d(125.0,125.0);
        Point2d[] test = new Point2d[] {p1, p2, p3, p4, p5};

        
        
        // Erzeuge eine Feld mit zuf�lligen Punkten
        /*
        IdPoint[] test = new IdPoint[n];
        for (int i = 0; i < test.length; i++) {
            double x = Math.random() * 800;
            double y = Math.random() * 600;
            test[i] = new IdPoint(x, y);
        }
        */   


        VoronoiTriangulation triang = new VoronoiTriangulation(test);                
        
        System.out.println("Finished:" + new java.util.Date().getTime()/1000);
        System.out.println(triang.dir);
        
        JFrame frame = new JFrame("Voronoi-Triangulierung");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(triang.getView());
        frame.setSize(1024, 768);        
        frame.setVisible(true);         
    }
    
    public JComponent getView() {
        if (view == null) view = new DTView();
        return view;
    }
    
    class DTView extends JComponent {
        
        // double s = 0.15, dx = 500., dy = 300.;
        double s = 1.0, dx = 50., dy =50.;
        boolean text = true;
        
        public DTView() { 
            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    if (me.getButton()==MouseEvent.BUTTON1)
                        addPoint(new IdPoint((double) me.getPoint().x-50, (double) me.getPoint().y-50));
                    else {
                        removePoint(dir.searchDelaunayPoint(new IdPoint((double) me.getPoint().x-50, (double) me.getPoint().y-50)));                        
                    }
                    System.out.println("<------\n" + dir);
                    dir.checkConsistency();
                    System.out.println("------->");
                    repaint();
                }
            });
        }
        
        public void paintComponent(Graphics g) {                                                            
                        
            g.setColor(Color.red);
            IdPoint[] ps = nodes.toArray();
            for (int i = 0; i < ps.length; i++) {
                g.drawRect((int)((ps[i].x-1)*s+dx), (int)((ps[i].y-1)*s+dy), 3, 3);
                if (text) g.drawString("" + ps[i].id, (int)(ps[i].x*s+dx+5), (int)(ps[i].y*s+dy+5));                
            }
            g.setColor(Color.black);
            
            Iterator it = dir.voronoiIterator();
            while (it.hasNext()) {
                IdPoint edge = (IdPoint) it.next();
                g.drawRect((int)(edge.x*s+dx), (int)(edge.y*s+dy), 1, 1);
                if (text) g.drawString("" + edge.id, (int)(edge.x*s+5+dx), (int)(edge.y*s+5+dy));                
                
                IdPoint[] nodes = dir.getAssociatedNodes(edge).p;
                
                g.drawLine((int)(nodes[0].x*s+dx), (int)(nodes[0].y*s+dy), (int)(nodes[1].x*s+dx), (int)(nodes[1].y*s+dy));
                g.drawLine((int)(nodes[1].x*s+dx), (int)(nodes[1].y*s+dy), (int)(nodes[2].x*s+dx), (int)(nodes[2].y*s+dy));
                g.drawLine((int)(nodes[2].x*s+dx), (int)(nodes[2].y*s+dy), (int)(nodes[0].x*s+dx), (int)(nodes[0].y*s+dy));              
                                
            }            
        }        
    }    
}



/* -------- VoronoiDirectory ------------------- */
class VoronoiDirectory {

    static class Entry {
        
        IdPoint edge;
        Triangle nodes;
        IdPoint[] adjEdges;
        
        Entry(IdPoint e, Triangle n, IdPoint[] a) {
            this.edge = e;
            this.nodes = n;
            this.adjEdges = a;
        }    
        
        public String toString() {
            String result = edge + ": [" + nodes + "], [";
            for (int i = 0; i < adjEdges.length; i++) 
                result += adjEdges[i] + (i < adjEdges.length-1 ? "," : "]");
                //result += adjEdges[i].id + (i < adjEdges.length-1 ? "," : "]");
            return result;            
        }
    }
        
    private HashMap map;
    
    private IdPoint[] rect;    
    private Triangle[] triangles;
    private double L;

    /**
     * 
     * @param rect: xmin = [0]; xmax = [1]; ymin = [2]; ymax =[3]
     */
    public VoronoiDirectory(double[] r) {
        map = new HashMap();

        // Rechteck als Ausgangsstruktur
        L = 2 * Math.sqrt(Math.pow(r[1]-r[0], 2.) + Math.pow(r[3]-r[2], 2.));
        rect = new IdPoint[4];        
        rect[0] = new IdPoint(r[0]-L, r[2]-L);
        rect[1] = new IdPoint(r[1]+L, r[2]-L);
        rect[2] = new IdPoint(r[0]-L, r[3]+L);
        rect[3] = new IdPoint(r[1]+L, r[3]+L);
        
        triangles = new Triangle[2];
        triangles[0] = new Triangle(new IdPoint[] { rect[0], rect[1], rect[3] });        
        triangles[1] = new Triangle(new IdPoint[] { rect[0], rect[3], rect[2] });        
                
        addEntry(triangles[0].perimeter().m, triangles[0], new IdPoint[] {IdPoint.INFINITY, triangles[1].perimeter().m, IdPoint.INFINITY});
        addEntry(triangles[1].perimeter().m, triangles[1], new IdPoint[] {IdPoint.INFINITY, IdPoint.INFINITY, triangles[0].perimeter().m});
        
    }
        
    void addEntry(IdPoint edge, Triangle nodes, IdPoint[] vertices) {        
        if (edge == null) throw new IllegalArgumentException("edge is null");
        if (nodes == null) throw new IllegalArgumentException("nodes is null");
        if (vertices == null) throw new IllegalArgumentException("vertices is null");
                      
        map.put(edge, new Entry(edge, nodes, vertices));        
    }      
    
    Entry getEntry(IdPoint edge) {
        if (!map.containsKey(edge)) throw new IllegalArgumentException("no entry for " + edge);
        return (Entry) map.get(edge);
    }
    
    Entry removeEntry(IdPoint edge) {
        if (!map.containsKey(edge)) throw new IllegalArgumentException("no entry for " + edge);
        return (Entry) map.remove(edge);
    }
    
    Triangle getAssociatedNodes(IdPoint edge) {
        if (edge == null) throw new IllegalArgumentException("null");
        return ((Entry)map.get(edge)).nodes;
    }
    
    IdPoint[] getNeighbours(IdPoint edge) {
        //System.out.println("edge: " + edge + ", map: " + map.get(edge));
        if (edge == null) throw new IllegalArgumentException("null");
        return ((Entry)map.get(edge)).adjEdges;
    }       
    
    Iterator voronoiIterator() {
        return map.keySet().iterator();       
    }
        
    Iterator entryIterator() {
        return map.values().iterator();       
    }
    
    int size() {
        return map.size();
    }
     
    public String toString() {
        String result = "VoronoiDirectory, " + map.size()+ " Eintr�ge:\n";
        Iterator it = voronoiIterator();
        while(it.hasNext()) {
            IdPoint key = (IdPoint) it.next();
            result += getEntry(key).toString() + "\n";
        }
        return result;
    }
    
    
    IdPoint searchDelaunayPoint(IdPoint q) {
        
        IdPoint result = null;
        double min_dist = Double.POSITIVE_INFINITY;
        Iterator it = map.values().iterator();
        while (it.hasNext()) {
            IdPoint[] dps = ((Entry)it.next()).nodes.p;
            for (int i = 0; i < dps.length; i++) {
                double dist = q.distance(dps[i]);
                if (dist < min_dist) {
                    min_dist = dist;
                    result = dps[i];
                } 
            }
        }
        return result;
    }
    
    /** Sucht den Voronoiknoten, mit dem kleinsten Abstand zu q. */
    IdPoint searchVoronoiPoint(IdPoint q) {        
        
        IdPoint result = null;
        double min_dist = Double.POSITIVE_INFINITY;
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            IdPoint p = (IdPoint) it.next();
            double dist = q.distance(p);
            if (dist < min_dist) {
                min_dist = dist;
                result = p;
            }            
        }
        return result;
    }
    
    PointSet getNodesNeighbouredTo(IdPoint q) {
        PointSet result = new PointSet();        
        Iterator it = entryIterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            for (int i=0; i < entry.adjEdges.length; i++) {
                if (entry.adjEdges[i].equals(q))
                    result.add(entry.edge);                
            }
        }        
        return result;
    }
    
    
    /** Einfacher Konsistenztest: �berpr�ft, ob alle Ecken vorhanden sind, die referenziert werden. */
    void checkConsistency() {
        
        Iterator it = entryIterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            IdPoint[] neighs = entry.adjEdges;
            for (int i = 0; i < neighs.length; i++)                
                if (!neighs[i].equals(IdPoint.INFINITY) && !map.containsKey(neighs[i])) {
                    System.out.println("Inkonsistenz: " + entry);
                    java.awt.Toolkit.getDefaultToolkit().beep();
                }
        }                
    }
    
    /**  
     *  Diese Methode entfernt die Ausgangsstruktur.
     *  Zu jeder Voronoiecke wird gepr�ft, ob das assoziierte Dreieck einen Knoten des Ausgangsrechtecks referenziert.
     *  Ist das der Dall, dann muss diese Ecke aus dem Verzeichnis entfernt und die Nachbarecken aktualisiert werden. 
     */
    void removeRect() {  
        
        System.out.println(toString());
        
        PointSet initSet = new PointSet(rect);      
        PointSet tmpSet = new PointSet(IdPoint.IdComparator.getInstance());       
        
        Iterator it = voronoiIterator();
        while(it.hasNext()) {
            IdPoint vp = (IdPoint) it.next();
            Entry entry = (Entry) map.get(vp);
            
            // triangles sind die beiden Dreiecke der Ausgangsstruktur.
            if (vp == triangles[0].perimeter().m || vp == triangles[1].perimeter().m) continue;
            
            Triangle t = entry.nodes;
            for (int i=0; i < 3; i++) {                
                if (initSet.contains(t.p[i])) {                                        
                    // tmpSet wird die Ecken enthalten, die gel�scht werden sollen                                       
                    tmpSet.add(vp);                     
                }
            }            
        }        
        
        // Entferne die Ecken, die einen Punkt aus initSet referenzieren.
        PointSet pSet = new PointSet();
        it = tmpSet.iterator();
        while(it.hasNext()) {
            IdPoint vp = (IdPoint) it.next();
            
            // deren Nachbarn merken, um diese spaeter zu aktualisieren.
            Entry entry = (Entry) map.get(vp);
            for (int i = 0; i < entry.adjEdges.length; i++)                 
                if (!tmpSet.contains(entry.adjEdges[i])) 
                    if (!entry.adjEdges[i].equals(IdPoint.INFINITY))
                        pSet.add(entry.adjEdges[i]);
                
            
            removeEntry(vp);
        }
        
        // Aktualisiere die Nachbarecken
        it = pSet.iterator();
        while(it.hasNext()) {
            IdPoint vp = (IdPoint) it.next();
            Entry entry = (Entry) map.get(vp);
            
            // suche nach der gel�schten Ecke in den anderen Eintr�gen und ersetze sie dort durch IdPoint.INFINITY
            IdPoint[] neighs = entry.adjEdges;
            for (int i=0; i < 3; i++) {
                if (tmpSet.contains(neighs[i])) {
                    neighs[i] = IdPoint.INFINITY;
                }
            }    
        }
        
    }
}


/* ---- PointSet -----*/

class PointSet {

    private TreeSet set;        
    
    PointSet() {
        set = new TreeSet(IdPoint.IdComparator.getInstance());        
    }
    
    PointSet(IdPoint[] p) {
        this();
        add(p);        
    }

    PointSet(IdPoint[] p, Comparator comp) {
        this(comp);
        add(p);
    }
    
    PointSet(Comparator comp) {
        set = new TreeSet(comp);
    }
    
    boolean add(IdPoint p) {                
        return set.add(p);
    }

    void add(IdPoint[] p) {
        for (int i=0; i < p.length; i++)
            set.add(p[i]);
    }    
    
    boolean remove(IdPoint p) {
        return set.remove(p);
    }
    
    void removeAll() {
        set.clear();
    }
    
    boolean contains(IdPoint p) {
        return set.contains(p);
    }
    
    Iterator iterator() {
        return set.iterator();
    }
    
    int size() {
        return set.size();
    }
   
    IdPoint[] toArray() {
        IdPoint[] r = new IdPoint[set.size()];        
        set.toArray(r);
        return r;
    }
    
    IdPoint[] toArray(IdPoint[] p) {
        set.toArray(p);
        return p;
    }
    
    Vector toVector() {
        Vector result = new Vector();
        Iterator it = set.iterator();
        while (it.hasNext())
            result.add(it.next());
        return result;        
    }

    public String toString() {
        String result = "";
        Iterator it = iterator();
        while (it.hasNext()) {
            result += it.next() + ";  ";
        }
        return result;
    }
    
    static PointSet union(PointSet a, PointSet b) {
        PointSet result = new PointSet();
        Iterator it = a.iterator();
        while (it.hasNext()) result.add((IdPoint)it.next());
        it = b.iterator();
        while (it.hasNext()) result.add((IdPoint)it.next());
        return result;
    }
    
    static boolean compare(PointSet a, PointSet b) {
        PointSet result = new PointSet();
        Iterator it = a.iterator();
        while (it.hasNext()) 
            b.contains((IdPoint) it.next());        
        return true;
    }     
}

/* -----  ----- */

class Triangle {
    
    IdPoint[] p = null;   
    private IdPoint.Pair[] edges = null;    
    private Circle perimeter = null;
    private double area = -1.;
    
    static double EPSILON = 1.E-6;  // Epsilontik f�r Kollinearit�t
        
    Triangle(IdPoint a, IdPoint b, IdPoint c) {
        this(new IdPoint[] { a, b, c});        
    }
    
    Triangle(IdPoint[] p) {
        if (p[0] == null || p[1] == null || p[2] == null) throw new IllegalArgumentException("a, b or c is null");
        if (p[0] == IdPoint.INFINITY || p[1] == IdPoint.INFINITY || p[2] == IdPoint.INFINITY) throw new IllegalArgumentException("a, b or c is INFINITY");                                  
        if (IdPoint.collinear(p[0], p[1], p[2], EPSILON)) throw new IllegalArgumentException("Triangle: Die drei Punkte in p " + java.util.Arrays.toString(p) + " sind collinear. ");
        this.p = p;        
    }
       
    IdPoint[] getPoints() { 
        return p; 
    }
    
    IdPoint.Pair[] getEdges() {        
        if (edges != null) return edges;
        
        edges = new IdPoint.Pair[3];
        for (int i=0; i < 3; i++)
            edges[i] = new IdPoint.Pair(p[i], p[(i+1)%3]);        
        return edges;        
    }
    
    double area() {
        if (area == -1.) {
            double a = p[0].distance(p[1]);
            double b = p[1].distance(p[2]);
            double c = p[2].distance(p[0]);
            double s = 0.5 * (a + b +c);
            area = Math.sqrt(s*(s-a)*(s-b)*(s-c));
            if (Double.isNaN(area)) area = 0.0;
        }
        return area;
    }
    
    Circle perimeter() {
      
        if (perimeter != null)
            return perimeter;
        
        double delta = 4 * (p[0].x - p[1].x) * (p[1].y - p[2].y) - 4 * (p[1].x - p[2].x) * (p[0].y - p[1].y);                  
        
        double delta1 = 2 * (p[0].x*p[0].x + p[0].y*p[0].y - p[1].x*p[1].x - p[1].y*p[1].y) *  (p[1].y - p[2].y);
        delta1 -= 2 * (p[1].x*p[1].x + p[1].y*p[1].y - p[2].x*p[2].x - p[2].y*p[2].y) *  (p[0].y - p[1].y);
        
        double delta2 = 2 * (p[1].x*p[1].x + p[1].y*p[1].y - p[2].x*p[2].x - p[2].y*p[2].y) *  (p[0].x - p[1].x);
        delta2 -= 2 * (p[0].x*p[0].x + p[0].y*p[0].y - p[1].x*p[1].x - p[1].y*p[1].y) *  (p[1].x - p[2].x);
        
        IdPoint m = new IdPoint(delta1/delta, delta2/delta);
        double r = m.distance(p[0]);
        
        perimeter = new Circle(m, r);
        return perimeter;        
    }
    
    /** Gibt true zur�ck, wenn die Koordinaten im Dreieck oder auf seinem Rand liegen */
    boolean contains(double x, double y, boolean boundaryIsInside) {
        
        IdPoint tmp = new IdPoint(x, y);
        double area = 0.0;
        try {
            for (int i=0; i < 3; i++) {                
                Triangle tri = new Triangle(tmp, p[(i+1)%3], p[(i+2)%3]);
                area += tri.area();
            }
        } catch (IllegalArgumentException ex) {
            // Die drei Punkte sind kollinear, daher liegt der Punkt auf dem Dreiek
            if (boundaryIsInside) return true;
            else return false;
        }
        if ((area - this.area()) < EPSILON) return true;
        return false;
    }
    
    /** Gibt true zur�ck, wenn die Koordinaten im Dreieck oder auf seinem Rand liegen */
    boolean contains(IdPoint p, boolean boundaryIsInside) {
        return contains(p.x, p.y, boundaryIsInside);
    }
        
    boolean isNeighboured(Triangle t) {
        
        // at least two points must be equal
        TreeSet set = new TreeSet(IdPoint.PointComparator.getInstance());
        set.add(p); 
        set.add(t.p);
        
        if (set.size() == 4) return true;
        return false;       
    }
    
    /** Berechnet die gemeinsame Kante */
    IdPoint.Pair section(Triangle t) {
        
        if (t == null) throw new IllegalArgumentException("t is null");
        
        IdPoint[] edge = new IdPoint[2];        
        int z = 0;        
        for (int i = 0; i < 3; i++) {
            IdPoint tmp = p[i];
            for (int j = 0; j < 3; j++) {
                if (tmp.equals(t.p[j]) && z < 2) {
                     edge[z++]= tmp;
                     break;
                }              
            }
        }
        if (edge[0] != null && edge[1] != null)
            return new IdPoint.Pair(edge[0], edge[1]);
        else 
            return null;
    }
    
    /** index aus 0,1,2 bzw. -1 wenn nicht gefunden */
    int getIndex(IdPoint p) {
        for (int i = 0; i < 3; i++)
            if (this.p[i].equals(p)) return i;
        return -1;
    }
    
    /** Berechnet den Winkel (in Radiant) zwischen p1p2 und p2p3 */
    double angle(IdPoint p1, IdPoint p2, IdPoint p3) {
        double p1p2 = Math.hypot(p1.x - p2.x, p1.y - p2.y);
        double p2p3 = Math.hypot(p2.x - p3.x, p2.y - p3.y);
        double p3p1 = Math.hypot(p1.x - p3.x, p1.y - p3.y);        
        return Math.acos((p1p2*p1p2 + p2p3*p2p3 - p3p1*p3p1)/(2*p1p2*p2p3));
    }
    
    /** Berechnet den kleinsten Winkel des Dreicks und gibt die Ecke an dem Winkel zur�ck. */
    IdPoint smallestAnglePoint() {
        double minA = Double.MAX_VALUE; int minI = 0;
        for (int i=0; i < 3; i++) {
            double a = angle(p[(i+1)%3], p[i%3], p[(i+2)%3]);
            if (a < minA) { minA = a; minI = i; }            
        }
        return p[minI];        
    }
    
    /** Berechnet den kleinsten Winkel des Dreicks. */
    double smallestAngle() {
        double minA = Double.MAX_VALUE;
        for (int i=0; i < 3; i++) {
            double a = angle(p[(i+1)%3], p[i%3], p[(i+2)%3]);
            if (a < minA) { minA = a; }            
        }
        return minA;        
    }

    /** Berechnet den Index der k�rzesten Kante */
    int shortestEdge() {
        double min = Double.MAX_VALUE;
        int idx = -1;
        for (int i=0; i < p.length; i++) {
            double len = Math.hypot(p[(i+1)%3].x - p[i].x, p[(i+1)%3].y - p[i].y);
            if (len < min) { 
                min = len;
                idx = (i+2)%3;
            }
        }
        return idx;  
    }
    
    /** Berechnet den Index der l�ngsten Kante */
    int longestEdge() {
        double max = 0;
        int idx = -1;
        for (int i=0; i < p.length; i++) {
            double len = Math.hypot(p[(i+1)%3].x - p[i].x, p[(i+1)%3].y - p[i].y);
            if (len > max) { 
                max = len;
                idx = (i+2)%3;
            }
        }
        return idx;  
    }
    
    /** Gibt die L�nge der Kante zur�ck, die dem Punkt i gegen�ber liegt. */
    double lengthOfEdge(int i) {
        int idx = longestEdge();
        return Math.hypot(p[(idx+2)%3].x - p[(idx+1)%3].x, p[(idx+2)%3].y - p[(idx+1)%3].y);
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Triangle)) return false;
        Triangle t = (Triangle) o;
        int z=0;
        for (int i=0; i < p.length; i++)
            for (int j=0; j < t.p.length; j++) 
                if ((t.p[i].equals(this.p[j]))) z++; 
        return z==3;
    }
    
    public boolean equalsByCoordinates(Object o, double eps) {
        if (!(o instanceof Triangle)) return false;
        Triangle t = (Triangle) o;
        int z=0;
        for (int i=0; i < p.length; i++)
            for (int j=0; j < t.p.length; j++)
                if (t.p[i].equalsByCoordinates(this.p[j], eps)) z++;
        return z==3;
    }
    
    public String toString() {
        return new String("Tri[" + p[0] + ", " + p[1] + ", " + p[2]);        
    }
    
    public static class TriangleComparator implements Comparator {
        
        IdPoint.PointComparator pComp = IdPoint.PointComparator.getInstance();
        
        public int compare(Object arg0, Object arg1) {
            if (!(arg0 instanceof Triangle && arg1 instanceof Triangle)) 
                throw new IllegalArgumentException("TriangeComparator cannot compare " + arg0);
            Triangle t1 = (Triangle) arg0;
            Triangle t2 = (Triangle) arg1;
            IdPoint p1 = t1.perimeter().m;
            IdPoint p2 = t2.perimeter().m;
            return pComp.compare(p1, p2);
        }
    }
}

/* ----  ---- */
class IdPoint extends Point2d {

    public static final double EPSILON =  1.E-10;  // Double: 14 Nachkommastellen    
    public static final IdPoint INFINITY = new IdPoint(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    
    // Masken f�r flag
    public static final int BOUNDARY_POINT = (1<<0);
    public static final int NOREMOVE_POINT = (1<<1);
    
    public static long idCounter;  
    
    /* ID */
    long id;            
    
    /* Flags */
    int flag; 
    
    Point2d p;
    
    public IdPoint(double x, double y) {
        super(x, y);
        this.p = (Point2d) this;
        id = ++idCounter;
    }    
    
    public IdPoint(Point2d p) {
        super(p);
        this.p = p;
        id = ++idCounter;
    }
    
    public void setFlag(int mask, boolean val) {        
        flag = (val ? flag | mask : flag & ~mask);        
    }
    
    public boolean getFlag(int mask) {
        return (flag & mask) == mask;
    }  
    
    public boolean equals(Object o) {
        if (!(o instanceof IdPoint)) return false;
        IdPoint p = (IdPoint) o;      
        if (p.id == id) return true;
        return false;
    }
    
     public boolean equalsByCoordinates(Object o, double eps) {
        if (!(o instanceof IdPoint)) return false;
        IdPoint p = (IdPoint) o;      
        if ((Math.abs(p.x - this.x) > eps ) || (Math.abs(p.y - this.y) > eps )) return false;    
        return true;
    }
    
    /** Pr�ft, ob der Punkt p3 auf der Geraden p1p2 liegt. */
    public static boolean collinear(IdPoint p1, IdPoint p2, IdPoint p3) {        
        return collinear(p1, p2, p3, IdPoint.EPSILON);
    }
    
    public static boolean collinear(IdPoint p1, IdPoint p2, IdPoint p3, double epsilon) {        
               
        double alpha = (p3.x-p1.x)/(p2.x-p1.x);
        if (Math.abs(p3.x-p1.x) < epsilon && Math.abs(p2.x-p1.x) < epsilon) return true;
        if (Math.abs(p1.y + alpha * (p2.y-p1.y) - p3.y) < epsilon) return true;
        else return false;
    }
    
    /**
     * Berechnet das die Punktmenge umschlie�ende Rechteck 
     * @param p
     * @return xmin = [0]; xmax = [1]; ymin = [2]; ymax =[3]
     */
    public static double[] rectangle2D(IdPoint[] p) {        
        if (p.length < 2) throw new IllegalArgumentException("at least 2 points are necessary.");
        double[] v = new double[4];
        v[0] = v[1]= p[0].x;
        v[2] = v[3]= p[0].y;
        for (int i = 1; i < p.length; i++) {
            if (p[i].x < v[0]) v[0] = p[i].x;
            else if (p[i].x > v[1]) v[1] = p[i].x;
            if (p[i].y < v[2]) v[2] = p[i].y;
            else if (p[i].y > v[3]) v[3] = p[i].y;            
        }              
        return v;
    }
    
       
    /** Sucht nach dem Punkt im Feld, der dem �bergebenen am nahesten liegt. */
    public static IdPoint nearPoint(IdPoint[] field, IdPoint p) {
        
        double dist, min_dist = Double.POSITIVE_INFINITY;
        IdPoint result = null;        
        for (int i=0; i < field.length; i++) {
            dist = field[i].distance(p);
            if (dist < min_dist) {
                min_dist = dist;
                result = field[i];
            }
        }
        return result;        
    }
    
    public static boolean isInSameOrder(IdPoint[] p1, IdPoint[] p2) {
        
        // find the smaller list;
        IdPoint[] a, b;
        if (p2.length > p1.length) { a = p1; b = p2; }
        else { a = p2; b = p1; }
        // find the first point of A in B
        int index = -1;
        for (int i=0; i < b.length; i++) {
            if (b[i].equals(a[0])) {
                index = i;
                break;
            }
        }
        // from there, the points in B must be in the same order as in A
        for (int i=0; i < a.length; i++) {
            if (!(a[i].equals(b[(index+i)%b.length]))) { return false; }
        }
        return true;
    }
    
    public static class Pair {
        
        public IdPoint p1;
        public IdPoint p2;
        private int mark = -1;
        
        public Pair(IdPoint p1, IdPoint p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair p = (Pair) o;
            if ((p.p1.equals(p1) && p.p2.equals(p2)) || (p.p1.equals(p2) && p.p2.equals(p1)))
                return true;
            return false;
        } 
        
        public void mark(IdPoint p) {
            if (p.equals(p1)) mark = 1;
            else if (p.equals(p2)) mark = 2;
            else throw new IllegalArgumentException("IdPoint " + p + " unknown.");
        }
        
        public IdPoint getMarkedPoint() {
            if (mark == -1) throw new IllegalArgumentException("no point marked");
            if (mark == 1) return p1;
            else return p2;             
        }
        
        public IdPoint getUnmarkedPoint() {
            if (mark == -1) throw new IllegalArgumentException("no point marked");
            if (mark == 1) return p2;
            else return p1;             
        }
        
        public String toString() {
            return new String("Pair: <" + p1 + ", " + p2 + ">");
        }
        
        public IdPoint section(Pair b) {
            return section(this, b);
        }
        
        public static IdPoint section(Pair a, Pair b) {           
            if (a.p1.equals(b.p1) || a.p1.equals(b.p2))
                return a.p1;
            else if(a.p2.equals(b.p1) || a.p2.equals(b.p2))
                return a.p2;
            else
                return null;                        
        }
        
        public boolean contains(IdPoint p) {
            if (p1.equals(p) || p2.equals(p))
                return true;
            return false;            
        }
    }       
    
    public static class PointComparator implements Comparator {
        
        private static PointComparator instance = new PointComparator();
        public static PointComparator getInstance() { return instance; }        
        private PointComparator() {};
        
        public int compare(Object arg0, Object arg1) {
            if (!(arg0 instanceof IdPoint && arg1 instanceof IdPoint)) 
                throw new IllegalArgumentException("PointComparator cannot compare " + arg0);
            IdPoint p1 = (IdPoint) arg0;
            IdPoint p2 = (IdPoint) arg1;            
            if (p1.x < p2.x) return -1;
            else if (p1.x > p2.x) return 1;
            else {
                if (p1.y < p2.y) return -1;
                else if (p1.y > p2.y) return 1;
                else return 0;
            }
        }
    }
    
    public static class IdComparator implements Comparator {
        
        private static IdComparator instance = new IdComparator();
        public static IdComparator getInstance() { return instance; }        
        private IdComparator() {};
        
        public int compare(Object arg0, Object arg1) {
            if (!(arg0 instanceof IdPoint && arg1 instanceof IdPoint)) 
                throw new IllegalArgumentException("PointComparator cannot compare " + arg0);
            IdPoint p1 = (IdPoint) arg0;
            IdPoint p2 = (IdPoint) arg1;
            if (p1.id < p2.id) return -1;
            else if (p1.id > p2.id) return 1;
            else return 0;            
        }
    }        
}


/* --- --- */
class Circle {
    
    public IdPoint m;
    public double r;
            
    public Circle(IdPoint m, double r) {
        if (r < 0) throw new IllegalArgumentException("radius < 0");
        this.m = m;
        this.r = r;
    }
    
    public void paint(java.awt.Graphics g) {
        g.drawOval((int)m.x, (int)m.y, (int)(2*r), (int)(2*r));        
    }
    
    /** 
     * Der Kreis enth�lt p, wenn die Kreisungleich mit einer Genauigkeit von eps erf�llt ist.
     * @param p
     * @return True, falls der Kreis den Punkt p enth�lt.
     */
    public boolean contains(IdPoint p) {
        
        if  ( ((p.x-m.x)*(p.x-m.x) + (p.y-m.y)*(p.y-m.y)) <= r*r) return true;        
        return false;
    }
    
    public String toString() {
        return new String("Circle: MP=" + m + ", r=" + r);
    }
}


