/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator;
import applications.transshipment.multiscale.model.Scale;
import com.google.common.collect.Iterators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import math.geometry.ParametricLinearCurve3d;

/**
 *
 * @author bode
 */
public class CraneAnalysis implements Analysis {

    public static int getNumberOfTansportOperations(Crane c, LoadUnitJobSchedule schedule) {
        int numberOfOperations = 0;

        Collection<Operation> operationsForResource = schedule.getOperationsForResource(c);
        for (Operation operation : operationsForResource) {
            if (operation instanceof TransportOperation) {
                numberOfOperations++;
            }
        }
        return numberOfOperations;
    }

    public static long getMeanTransportDuration(Crane c, TerminalProblem problem, LoadUnitJobSchedule schedule) {
        /**
         * Längsbewegung
         */
        long meanDuration = 0;
        int numberOfOperations = 0;

        Collection<Operation> operationsForResource = schedule.getOperationsForResource(c);
        for (Operation operation : operationsForResource) {
            if (operation instanceof TransportOperation) {
                numberOfOperations++;
                meanDuration += operation.getDuration().longValue();
            }
        }

        return meanDuration / numberOfOperations;
    }

    public static long getMeanTransportDuration(TerminalProblem problem, LoadUnitJobSchedule schedule) {
        /**
         * Längsbewegung
         */
        long meanDuration = 0;
        int numberOfOperations = 0;
        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (!(conveyanceSystem instanceof Crane)) {
                continue;
            }
            Collection<Operation> operationsForResource = schedule.getOperationsForResource(conveyanceSystem);
            for (Operation operation : operationsForResource) {
                if (operation instanceof TransportOperation) {
                    numberOfOperations++;
                    meanDuration += operation.getDuration().longValue();
                }
            }
        }
        if (numberOfOperations == 0) {
            return 0;
        }

        return meanDuration / numberOfOperations;
    }

    public static double getMacroCraneIdleDistance(TerminalProblem problem, LoadUnitJobSchedule schedule) {
        /**
         * Längsbewegung
         */
        double distance = 0;
        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (!(conveyanceSystem instanceof Crane)) {
                continue;
            }
            Collection<Operation> operationsForResource = schedule.getOperationsForResource(conveyanceSystem);
            for (Operation operation : operationsForResource) {
                if (operation instanceof IdleSettingUpOperation) {
                    IdleSettingUpOperation idleOp = (IdleSettingUpOperation) operation;
                    distance += idleOp.getStart().getCenterOfGeneralOperatingArea().distance(idleOp.getEnd().getCenterOfGeneralOperatingArea());
                }
            }
        }
        return distance;
    }

    public static double getCraneLongitudinalDistance(TerminalProblem problem, LoadUnitJobSchedule schedule, boolean includeTransport) {
        /**
         * Längsbewegung
         */
        double distance = 0;
        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (!(conveyanceSystem instanceof Crane)) {
                continue;
            }
            Crane c = (Crane) conveyanceSystem;
            distance += getMacroCraneLongitudinalDistance(c, problem, schedule, includeTransport);
        }
        return distance;
    }

    public static long getMittlereRuestZeit(Crane c, Schedule s, TerminalProblem problem) {
        long summe = 0;
        int anzahl = 0;
        Collection<Operation> operationsForResource = s.getOperationsForResource(c);
        for (Iterator<Operation> iterator = operationsForResource.iterator(); iterator.hasNext();) {

            Operation o = iterator.next();
            if (o instanceof TransportOperation) {

                TransportOperation first = (TransportOperation) o;

                TransportOperation second = null;
                if (problem.getScale().equals(Scale.micro)) {
                    Operation higher = (TransportOperation) Iterators.getNext(iterator, first);
                    if (higher != null) {
                        second = (TransportOperation) higher;
                    }
                } else {
                    Operation higher = Iterators.getNext(iterator, first);
                    higher = Iterators.getNext(iterator, higher);
                    if (higher != null) {
                        second = (TransportOperation) higher;
                    }
                }

                if (second != null) {
                    ParametricLinearCurve3d transportMotion = CraneMotionCalculator.getSettingUpMotion(c, first.getDestination(), second.getOrigin());
                    if (transportMotion != null) {
                        summe += transportMotion.getDuration();
                    }
                    anzahl++;
                }
            }
        }
        if (anzahl == 0) {
            return 0;
        }
        return summe / anzahl;
    }

    public ArrayList<Long> getRuestZeiten(Crane c, Schedule s, TerminalProblem problem) {
        ArrayList<Long> dauern = new ArrayList<>();
        Collection<Operation> operationsForResource = s.getOperationsForResource(c);
        for (Iterator<Operation> iterator = operationsForResource.iterator(); iterator.hasNext();) {
            Operation o = iterator.next();
            if (o instanceof TransportOperation) {

                TransportOperation first = (TransportOperation) o;

                TransportOperation second = null;
                if (problem.getScale().equals(Scale.micro)) {
                    Operation higher = (TransportOperation) Iterators.getNext(iterator, first);
                    if (higher != null) {
                        second = (TransportOperation) higher;
                    }
                } else {
                    Operation higher = Iterators.getNext(iterator, first);
                    higher = Iterators.getNext(iterator, higher);
                    if (higher != null) {
                        second = (TransportOperation) higher;
                    }
                }
                if (second != null) {
                    ParametricLinearCurve3d transportMotion = CraneMotionCalculator.getSettingUpMotion(c, first.getDestination(), second.getOrigin());
                    if (transportMotion != null) {
                        dauern.add(transportMotion.getDuration() / 1000);
                    }
                }
            }
        }
        Collections.sort(dauern);
        return dauern;

    }

    public static long getMittlereLaengsRuestFahrtCrane(Schedule s, TerminalProblem p) {
        long summe = 0;
        int anzahl = 0;
        for (ConveyanceSystem conveyanceSystem : p.getTerminal().getConveyanceSystems()) {
            if (!(conveyanceSystem instanceof Crane)) {
                continue;
            }
            Crane c = (Crane) conveyanceSystem;

            Collection<Operation> operationsForResource = s.getOperationsForResource(c);
            if (p.getScale().equals(Scale.micro)) {
                for (Iterator<Operation> iterator = operationsForResource.iterator(); iterator.hasNext();) {
                    TransportOperation first = (TransportOperation) iterator.next();
                    TransportOperation second = (TransportOperation) Iterators.getNext(iterator, first);
                    if (second != null) {
                        summe += Math.abs(first.getDestination().getCenterOfGeneralOperatingArea().x - second.getOrigin().getCenterOfGeneralOperatingArea().x);
                        anzahl++;
                    }
                }
            }

            if (p.getScale().equals(Scale.macro)) {
                for (Operation operation : operationsForResource) {
                    if (operation instanceof IdleSettingUpOperation) {
                        IdleSettingUpOperation idleOp = (IdleSettingUpOperation) operation;
                        summe += Math.abs(idleOp.getStart().getCenterOfGeneralOperatingArea().x - idleOp.getEnd().getCenterOfGeneralOperatingArea().x);
                        anzahl++;
                    }
                }
            }

        }
        if (anzahl == 0) {
            return 0;
        }
        return summe / anzahl;
    }

    public static double getMittlereRuestFahrtDistanceCrane(Schedule s, TerminalProblem p) {
        double summe = 0;
        int anzahl = 0;
        for (ConveyanceSystem conveyanceSystem : p.getTerminal().getConveyanceSystems()) {
            if (!(conveyanceSystem instanceof Crane)) {
                continue;
            }
            Crane c = (Crane) conveyanceSystem;

            Collection<Operation> operationsForResource = s.getOperationsForResource(c);
            if (p.getScale().equals(Scale.micro)) {
                for (Iterator<Operation> iterator = operationsForResource.iterator(); iterator.hasNext();) {
                    TransportOperation first = (TransportOperation) iterator.next();
                    TransportOperation second = (TransportOperation) Iterators.getNext(iterator, first);
                    if (second != null) {

                        summe += first.getDestination().getCenterOfGeneralOperatingArea().distance(second.getOrigin().getCenterOfGeneralOperatingArea());
                        anzahl++;
                    }
                }

            }

        }
        if (anzahl == 0) {
            return 0;
        }
        return summe / anzahl;

    }

    public static long getVarianzRuestZeit(Crane c, Schedule s, TerminalProblem problem) {
        double xMean = getMittlereRuestZeit(c, s, problem);

        long s2 = 0;
        int anzahl = 0;
        Collection<Operation> operationsForResource = s.getOperationsForResource(c);
        for (Iterator<Operation> iterator = operationsForResource.iterator(); iterator.hasNext();) {
            Operation o = iterator.next();
            if (o instanceof TransportOperation) {

                TransportOperation first = (TransportOperation) o;

                TransportOperation second = null;
                if (problem.getScale().equals(Scale.micro)) {
                    Operation higher = (TransportOperation) Iterators.getNext(iterator, first);
                    if (higher != null) {
                        second = (TransportOperation) higher;
                    }
                } else {
                    Operation higher = Iterators.getNext(iterator, first);
                    higher = Iterators.getNext(iterator, higher);
                    if (higher != null) {
                        second = (TransportOperation) higher;
                    }
                }

                if (second != null) {
                    ParametricLinearCurve3d transportMotion = CraneMotionCalculator.getSettingUpMotion(c, first.getDestination(), second.getOrigin());
                    if (transportMotion != null) {
                        s2 += (transportMotion.getDuration() - xMean) * (transportMotion.getDuration() - xMean);
                    }
                    anzahl++;
                }
            }
        }
        if (anzahl == 0) {
            return 0;
        }
        return s2 / (anzahl - 1);
    }

    public static double getMacroCraneLongitudinalDistance(Crane c, TerminalProblem problem, LoadUnitJobSchedule schedule, boolean includeTransport) {
        /**
         * Längsbewegung
         */
        double distance = 0;

        Collection<Operation> operationsForResource = schedule.getOperationsForResource(c);
        for (Operation operation : operationsForResource) {
            if (operation instanceof IdleSettingUpOperation) {
                IdleSettingUpOperation idleOp = (IdleSettingUpOperation) operation;
                distance += idleOp.getStart().getCenterOfGeneralOperatingArea().distance(idleOp.getEnd().getCenterOfGeneralOperatingArea());
            }
            if (includeTransport) {
                if (operation instanceof TransportOperation) {
                    TransportOperation top = (TransportOperation) operation;
                    distance += top.getOrigin().getCenterOfGeneralOperatingArea().distance(top.getDestination().getCenterOfGeneralOperatingArea());
                }
            }
        }

        return distance;
    }

    @Override
    public void analysis(LoadUnitJobSchedule s, MultiJobTerminalProblem problem, File folder) {
        /**
         * Ausgabe in Datei
         */
        if (!(s instanceof LoadUnitJobSchedule)) {
            return;
        }

        try {
            File f = new File(folder, "CraneData.txt");
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("Krandaten");
            bw.newLine();
            bw.write("#########################");
            bw.newLine();
            List<Resource> resources = new ArrayList<>(s.getResources());
            Collections.sort(resources, new Comparator<Resource>() {

                @Override
                public int compare(Resource o1, Resource o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

            double sumTransportOperations = 0;
            long sumMittlereRuestZeit = 0;
            long sumTransportDuration = 0;

            for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
                if (conveyanceSystem instanceof Crane) {
                    Crane c = (Crane) conveyanceSystem;

                    double numberofOperations = getNumberOfTansportOperations(c, s);
                    long mittlereRuestZeit = 0;

                    mittlereRuestZeit = getMittlereRuestZeit(c, s, problem);

                    long meanTransportDuration = getMeanTransportDuration(c, problem, s);
                    long varianz = 0;

                    varianz = getVarianzRuestZeit(c, s, problem);

                    bw.write(c.toString());

                    bw.newLine();

                    sumTransportOperations += numberofOperations;
                    sumMittlereRuestZeit += (numberofOperations * mittlereRuestZeit);
                    sumTransportDuration += (numberofOperations * meanTransportDuration);

                    bw.write("Anzahl an Transportoperationen: " + numberofOperations);
                    bw.newLine();
                    bw.write("Mittlere TransportDauer: " + (meanTransportDuration) / 1000);
                    bw.newLine();
                    bw.write("Mittlere RüstDauer: " + (mittlereRuestZeit) / 1000);
                    bw.newLine();
                    bw.write("Standardabweichung RüstDauer: " + Math.sqrt(varianz) / 1000);
                    bw.newLine();
                    bw.write("----------------------------");
                    bw.newLine();
                }
            }

            bw.write("#################################");
            bw.newLine();
            bw.write("Mittlere RüstDauer: " + (sumMittlereRuestZeit / sumTransportOperations) + "\t" + TimeSlot.longToFormattedDurationMillis((long) (sumMittlereRuestZeit / sumTransportOperations)));
            bw.newLine();
            bw.write("Mittlere TransportDauer: " + (sumTransportDuration / sumTransportOperations) + "\t" + TimeSlot.longToFormattedDurationMillis((long) (sumTransportDuration / sumTransportOperations)));
            bw.newLine();

            int numberOfLoadCycles = 0;
            for (LoadUnitJob job : s.getScheduledJobs()) {
                LoadUnitPositions operationsForLoadUnit = s.getOperationsForLoadUnit(job.getLoadUnit());
                for (LoadUnitOperation loadUnitOperation : operationsForLoadUnit) {
                    if (loadUnitOperation.getResource() instanceof Crane) {
                        numberOfLoadCycles++;
                    }
                }
            }
            bw.write("mittlere Kranspiele pro LoadUnit: " + (numberOfLoadCycles / (double) s.getScheduledJobs().size()));
            bw.newLine();
            bw.newLine();
            bw.newLine();
            for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
                if (conveyanceSystem instanceof Crane) {
                    Crane c = (Crane) conveyanceSystem;
                    bw.write(c.toString());
                    bw.newLine();
                    ArrayList<Long> dauern = getRuestZeiten(c, s, problem);
                    for (Long long1 : dauern) {
                        bw.write(long1.toString());
                        bw.newLine();
                    }
                }
            }
            bw.close();
            fw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

    }

}
