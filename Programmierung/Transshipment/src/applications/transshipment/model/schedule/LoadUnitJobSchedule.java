/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.operations.SubOperation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.model.LoadUnitJob;

import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.resources.conveyanceSystems.SubConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.scheduleSchemes.StorageToolMethods;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import math.FieldElement;

/**
 * Ein spezieller Schedule für LoadUnitJobs, der noch weitere Methoden zum
 * Handling von speziellen LoadUnitJobs bereitstellt.
 *
 * @author bode
 */
public class LoadUnitJobSchedule extends Schedule {

    public ActivityOnNodeGraph<RoutingTransportOperation> aon;
    public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae;
    public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> originalEalosae;

    /**
     * Verwaltung von zusätzlichen Informationen
     */
    private LinkedHashSet<LoadUnitJob> dnfJobs = new LinkedHashSet<>();
    private LinkedHashSet<LoadUnitJob> scheduledJobs = new LinkedHashSet<>();
    private HashMap<LoadUnit, LoadUnitPositions> operationsPerJobMap = new HashMap<LoadUnit, LoadUnitPositions>();
    private LinkedHashMap<RoutingTransportOperation, MultiScaleTransportOperation> detailOperations = new LinkedHashMap<RoutingTransportOperation, MultiScaleTransportOperation>();
    public TreeMultimap<FieldElement, RoutingTransportOperation> einplanzeitpunkte = TreeMultimap.create(Ordering.natural(), Ordering.usingToString());
    public FieldElement t;

    public LoadUnitJobSchedule(LoadUnitJobSchedule s, InstanceHandler handler) {
        super(handler);
        /**
         * Werden gesetzt aus altem
         */
        this.dnfJobs = new LinkedHashSet<>(s.dnfJobs);
        this.didNotFinish = new LinkedHashSet<>(s.didNotFinish);

        /**
         * Werden über das Einplanen gefüllt.
         */
        this.scheduledJobs = new LinkedHashSet<>();
        this.operationsPerJobMap = new HashMap<>();
        this.detailOperations = new LinkedHashMap<>();

        TreeMultimap<FieldElement, Operation> operationToAdd = TreeMultimap.create(Ordering.natural(), Ordering.arbitrary());
        for (Resource ma : s.mapTimeToOperations.keySet()) {
            TreeMultimap<FieldElement, Operation> resMap = s.mapTimeToOperations.get(ma);
            operationToAdd.putAll(resMap);
        }

        for (Operation o : operationToAdd.values()) {
            if (!(o instanceof SubOperation)) {
                if (!this.mapOperationToStartTime.containsKey(o)) {
                    this.schedule(o.clone(), s.mapOperationToStartTime.get(o));
//                    if(o instanceof CraneMicroTransportOperation){
//                        System.out.println(o);
//                    }
                }
            }
        }

    }

    public LoadUnitJobSchedule(InstanceHandler handler) {
        this(handler, handler.getResources());
    }

    public LoadUnitJobSchedule(InstanceHandler handler, Collection<Resource> resources) {
        super(handler);

    }

    
    
    public Collection<RoutingTransportOperation> getActiveSet(TimeSlot timeSlot) {
        Set<RoutingTransportOperation> ops = new LinkedHashSet<>();
        for (Resource resource : getResources()) {
            if ((resource instanceof ConveyanceSystem)) {
                TreeMultimap<FieldElement, Operation> timeToOperationMap = getTimeToOperationMap(resource);
                for (Collection<Operation> set : timeToOperationMap.asMap().subMap(timeSlot.getFromWhen(), true, timeSlot.getUntilWhen(), true).values()) {
                    for (Operation operation : set) {
                        if (operation instanceof MultiScaleTransportOperation) {
                            MultiScaleTransportOperation mtop = (MultiScaleTransportOperation) operation;
                            ops.add(mtop.getRoutingTransportOperation());
                        }
                    }
                }
            }
        }
        return ops;
    }

    public LinkedHashSet<LoadUnitJob> getDnfJobs() {
        return dnfJobs;
    }

    @Override
    public void schedule(Operation o, FieldElement executionStart) throws NoSuchElementException {
        if (mapOperationToStartTime.containsKey(o) && mapOperationToStartTime.get(o).equals(executionStart)) {
            return;
        }

        super.schedule(o, executionStart);

        if (o instanceof MultiScaleTransportOperation) {

            MultiScaleTransportOperation luOp = (MultiScaleTransportOperation) o;
            if (luOp.getRoutingTransportOperation().getRouting() == null) {
                return;
            }
            this.detailOperations.put(luOp.getRoutingTransportOperation(), luOp);

            LoadUnitJob job = luOp.getRoutingTransportOperation().getJob();
            if (luOp.getRoutingTransportOperation().getRouting().isLast(luOp.getRoutingTransportOperation())) {
                if (!getDnfJobs().contains(job)) {
                    this.scheduledJobs.add(job);
                }
            }

//            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(luOp.getRoutingTransportOperation());
//            if (ealosae.getEarliestStart().isGreaterThan(executionStart)) {
//                throw new IllegalArgumentException("Operation zu früh eingeplant!");
//            }
        }
        if (o instanceof LoadUnitOperation) {
            LoadUnitOperation luop = (LoadUnitOperation) o;
            LoadUnitPositions operationsPerJob = getOperationsForLoadUnit(luop.getLoadUnit());
            operationsPerJob.put(executionStart, luop);
        }

    }

    public Collection<RoutingTransportOperation> getOperationsScheduledInInterval(TimeSlot t) {
        SortedMap<FieldElement, Collection<RoutingTransportOperation>> subMap = einplanzeitpunkte.asMap().subMap(t.getFromWhen(), t.getUntilWhen());
        LinkedHashSet<RoutingTransportOperation> result = new LinkedHashSet<>();
        for (Collection<RoutingTransportOperation> va : subMap.values()) {
            result.addAll(va);
        }
        return result;

    }

    public void scheduleBundle(TransportBundle bundle, FieldElement einplanZeitpunkt) {
        einplanzeitpunkte.put(einplanZeitpunkt, bundle.getJ().getRoutingTransportOperation());
        StorageToolMethods.adaptPreviousStorage(this, bundle, bundle.getJ());
        if (bundle.getResource() instanceof SubConveyanceSystem) {
            SubConveyanceSystem subCS = (SubConveyanceSystem) bundle.getResource();
            FieldElement demandOfSuperSystem = bundle.getJ().getDemand(subCS.getSuperConveyanceSystem());
            bundle.getJ().setDemand(subCS, demandOfSuperSystem);
        }
        if (bundle.getSqj() != null) {
            this.schedule(bundle.getSqj(), bundle.getStartTime(bundle.getSqj()));
        }
        this.schedule(bundle.getJ(), bundle.getStartTime(bundle.getJ()));
        MultiScaleTransportOperation j = (MultiScaleTransportOperation) bundle.getJ();
        if (bundle.getSjq1_new() != null) {
            if (bundle.getSqq1_old() != null) {
                this.unschedule(bundle.getSqq1_old());
            }
            this.schedule(bundle.getSjq1_new(), bundle.getStartTime(bundle.getSjq1_new()));
        }
        if (bundle.getStoreAtDestination() != null) {
            this.schedule(bundle.getStoreAtDestination(), bundle.getStartTime(bundle.getStoreAtDestination()));
        }
    }

    public LoadUnitPositions getOperationsForLoadUnit(LoadUnit lu) {
        LoadUnitPositions list = operationsPerJobMap.get(lu);
        if (list == null) {
            list = new LoadUnitPositions(lu);
            operationsPerJobMap.put(lu, list);
        }
        return list;
    }

    public LinkedHashSet<LoadUnitJob> getScheduledJobs() {
        return scheduledJobs;
    }

    @Override
    public FieldElement get(Operation o) {
        FieldElement elem = super.get(o);
        if (elem == null && o instanceof RoutingTransportOperation) {
            MultiScaleTransportOperation mTop = this.detailOperations.get((RoutingTransportOperation) o);
            return super.get(mTop);
        }
        return elem;
    }

    public MultiScaleTransportOperation getScheduledOperation(RoutingTransportOperation top) {
        return this.detailOperations.get(top);
    }

    public Collection<RoutingTransportOperation> getScheduledRoutingTransportOperations() {
        return detailOperations.keySet();
    }

    public Collection<LoadUnit> getLoadUnits() {
        return this.operationsPerJobMap.keySet();
    }

    /**
     * Setzt den zu der Transportoperation gehörigen Job DNF.
     *
     * @param job
     * @param rtop Die fehlgeschlagene RoutingOperation, anhand derere der Job
     * bestimmt wird, der DNF gesetzt wird.
     */
    public void addDNFJob(LoadUnitJob job) {
        dnfJobs.add(job);
    }

    public void addDNFJob(RoutingTransportOperation rtop) {
        LoadUnitJob j = null;
        for (RoutingTransportOperation macroscopicTransportOperation : rtop.getRouting()) {
            this.addNotFinish(macroscopicTransportOperation);
            if (j == null) {
                j = macroscopicTransportOperation.getJob();
            }
        }
        this.addNotFinish(rtop);
        dnfJobs.add(j);
    }

    public LoadUnitStorage getOrigin(RoutingTransportOperation operation) {
        JobOperationList<RoutingTransportOperation> routing = operation.getRouting();
        int indexOf = routing.indexOf(operation);
        if (indexOf > 0) {
            RoutingTransportOperation predRTop = routing.get(indexOf - 1);
            MultiScaleTransportOperation mTop = this.detailOperations.get(predRTop);
            if (mTop == null) {
                System.err.println("Fehlerhafte Implementierung. Es ist für die vorangegangen RoutingTransportoperation keine MultiScaleOperation eingeplant" + predRTop);
            }
            return mTop.getDestination();
        } else {
            return operation.getOrigin();
        }
    }

    @Override
    public FieldElement getLastScheduleEventTime() {
        ArrayList<FieldElement> res = new ArrayList<>();
        for (Resource r : mapTimeToOperations.keySet()) {
            if (r instanceof ConveyanceSystem) {
                TreeMultimap<FieldElement, Operation> times = mapTimeToOperations.get(r);
                res.add(times.asMap().lastKey());
            }
        }
        Collections.sort(res);
        return res.get(res.size() - 1);
    }

}
