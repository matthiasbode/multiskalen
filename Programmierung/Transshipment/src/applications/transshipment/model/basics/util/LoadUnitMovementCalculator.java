/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations.CraneMicroTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.vecmath.Point3d;
import math.FieldElement;
import math.geometry.ParametricLinearCurve3d;
import math.geometry.PositionAndOrientation3DInTime;

/**
 * Klasse zum Bestimmen der
 *
 * @author bode
 */
public class LoadUnitMovementCalculator {

    public LoadUnitMovementCalculator() {

    }

    public static HashMap<LoadUnit, List<LoadUnitPositionAndOrientation3DInTime>> getKeyPoints(LoadUnitJobSchedule schedule) {
        HashMap<LoadUnit, List<LoadUnitPositionAndOrientation3DInTime>> positions = new HashMap<>();
        for (LoadUnit loadUnit : schedule.getLoadUnits()) {
            ArrayList<LoadUnitPositionAndOrientation3DInTime> listPerLU = new ArrayList<>();

            LoadUnitPositions operationsForLoadUnit = schedule.getOperationsForLoadUnit(loadUnit);
            for (LoadUnitOperation loadUnitOperation : operationsForLoadUnit) {
                List<LoadUnitPositionAndOrientation3DInTime> keyPoints = loadUnitOperation.getKeyPoints();
                FieldElement startTime = schedule.get(loadUnitOperation);
                if (startTime == null) {
                    continue;
                }
                for (LoadUnitPositionAndOrientation3DInTime pos : keyPoints) {
                    LoadUnitPositionAndOrientation3DInTime posnew = new LoadUnitPositionAndOrientation3DInTime(pos.getOperation(), pos.getTime(), pos.getPosition(), pos.getOrientation());
                    posnew.moveInTime(startTime.longValue());
                    listPerLU.add(posnew);
                }

            }
            positions.put(loadUnit, listPerLU);
        }
        return positions;
    }

    public static HashMap<Crane, List<PositionAndOrientation3DInTime>> getKeyPointsCrane(LoadUnitJobSchedule schedule, TerminalProblem problem) {

        HashMap<Crane, List<PositionAndOrientation3DInTime>> positions = new HashMap<>();

        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (conveyanceSystem instanceof Crane) {
                Crane c = (Crane) conveyanceSystem;
                ArrayList<PositionAndOrientation3DInTime> listForCrane = new ArrayList<>();
                positions.put(c, listForCrane);
                Collection<Operation> operationsForResource = schedule.getOperationsForResource(c);
                for (Operation operation : operationsForResource) {
                    if (operation instanceof CraneMicroTransportOperation) {
                        CraneMicroTransportOperation cmt = (CraneMicroTransportOperation) operation;

                        List<LoadUnitPositionAndOrientation3DInTime> keyPoints = cmt.getKeyPoints();
                        FieldElement startTime = schedule.get(cmt);
                        if (startTime == null) {
                            continue;
                        }
                        for (PositionAndOrientation3DInTime pos : keyPoints) {
                            PositionAndOrientation3DInTime posnew = new PositionAndOrientation3DInTime(pos.getTime(), pos.getPosition(), pos.getOrientation());
                            posnew.moveInTime(startTime.longValue());
                            listForCrane.add(posnew);
                        }

                    }
//                        ParametricLinearCurve3d transportMotion = CraneMotionCalculator.getTransportMotion(c, cmt.getOrigin(), cmt.getDestination(), cmt.getLoadUnit());
//                        long startZeit = schedule.get(operation).longValue();
//
//                        for (int i = 0; i < transportMotion.ptsInTime.length; i++) {
//                            long ptsInTime = transportMotion.ptsInTime[i] + startZeit;
//                            Point3d p = transportMotion.ptsInSpace[i];
//                            listForCrane.add(new PositionAndOrientation3DInTime(ptsInTime, p, null));
//                        }

                }
            }
        }

        return positions;
    }

}
