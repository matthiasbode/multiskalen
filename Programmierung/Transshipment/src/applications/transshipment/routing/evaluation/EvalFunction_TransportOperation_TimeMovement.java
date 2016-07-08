package applications.transshipment.routing.evaluation;

import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.routing.TransferArea;
import java.util.HashMap;
import math.DoubleValue;
import math.FieldElement;
import org.util.Pair;

/**
 *
 * Klasse bewertet TransportOperations anhand von Dauer und longitudinal crane
 * movement und crab movement
 *
 * @author bode
 */
public class EvalFunction_TransportOperation_TimeMovement implements EvalFunction_TransportOperation {

    /**
     * Gewichtungsfaktor [0;1] der angibt, wie wichtig die Zeit bei der
     * Bewertung ist
     */
    private double timeWeight;

    private InstanceHandler rules;

    public static double correctionFactor = 1.0; // kalibriert das Verhaeltnis von Time in Sekunden und Distance in Metern

    public static double transportOperation_CrabToCraneWeight = 0.2;

    public EvalFunction_TransportOperation_TimeMovement(InstanceHandler rules, double timeWeight) {
//        this.problem = problem;
        if (timeWeight < 0 || timeWeight > 1) {
            throw new UnsupportedOperationException("timeWeight out of bounds [0;1]");
        }
        this.timeWeight = timeWeight;
        this.rules = rules;
    }

    @Override
    public double evaluate(Pair<TransferArea, TransferArea> transport, ConveyanceSystem conveyanceSystem, LoadUnit lu) {
        if (conveyanceSystem == null) {
            return 0;
        }
        // Transportdauer in Millis
        ConveyanceSystemRule scheduleRule = (ConveyanceSystemRule) rules.get(conveyanceSystem);
        if (scheduleRule == null) {
            throw new IllegalArgumentException("Keine ScheduleRule für " + conveyanceSystem + " definiert");
        }
        FieldElement time = scheduleRule.getTransportationTime(transport.getFirst(), transport.getSecond(), lu);
        double cost = time.longValue() / 1000. * TransshipmentParameter.factors.get(conveyanceSystem.getClass());
        /**
         * Lediglich Längsbewegung wichtig
         */
//        double distance = (Math.abs(transport.getSecond().getCenterOfGeneralOperatingArea().getX() - transport.getFirst().getCenterOfGeneralOperatingArea().getX()));
        double distance = 0;
        if (conveyanceSystem instanceof Crane) {
            distance += (Math.abs(transport.getSecond().getCenterOfGeneralOperatingArea().getX() - transport.getFirst().getCenterOfGeneralOperatingArea().getX()))
                    + transportOperation_CrabToCraneWeight * ((Math.abs(transport.getSecond().getCenterOfGeneralOperatingArea().getX() - transport.getFirst().getCenterOfGeneralOperatingArea().getX())));
        }

        double value = timeWeight * cost / 1000. + (1 - timeWeight) * distance * correctionFactor;
        return value;
    }
}
