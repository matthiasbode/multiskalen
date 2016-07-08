/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.DefaultSharedResource;
import applications.transshipment.model.resources.conveyanceSystems.MultipleAgentConveyanceSystem;
import bijava.geometry.dim2.Point2d;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class LCSystem extends DefaultSharedResource<Agent> implements MultipleAgentConveyanceSystem<Agent> {

    public static final String PREFIX = "LCSystem";
    /**
     * Die ID wird bei Erstellung eines Objektes dieser Klasse aus dem
     * {@link #PREFIX} und der {@link #number} gebildet.
     */
    protected final String ID;

    private RackGroup handoverPoints;

    private final HashMap<Agent, LCSHandover> startingAgentPositions = new HashMap<>();

    public static long rendezvousTime = 15 * 1000L;


    /*
     Graph zur Routensuche für die einzelnen AGVs.
     */
    private WeightedDirectedGraph<Point2d, Double> graph;

    public LCSystem(Rectangle2D rec, int numberOfAgents) {
        super(rec);
        this.ID = PREFIX + "-" + getNumber();
        this.setArea(new Area(rec));
        for (int i = 0; i < numberOfAgents; i++) {
            this.addSharingResource(new Agent(this, this.getGeneralOperatingArea()));
        }
    }

    public HashMap<Agent, LCSHandover> getStartingAgentPositions() {
        return startingAgentPositions;
    }

    public RackGroup getHandoverPoints() {
        return handoverPoints;
    }

    public void setHandoverPoints(RackGroup handoverPoints) {
        this.handoverPoints = handoverPoints;
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        return true;
    }

    @Override
    public String toString() {
        return "LCSystem{" + getNumber() + '}';
    }

    @Override
    public String getID() {
        return ID;
    }

    public WeightedDirectedGraph<Point2d, Double> getGraph() {
        return graph;
    }

    public void setGraph(WeightedDirectedGraph<Point2d, Double> graph) {
        this.graph = graph;
    }
}
