/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.problem;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.problem.multiMode.MultiModeJobProblem;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.model.LoadUnitJob;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.DefaultConveyanceInteraction;
import applications.transshipment.model.resources.storage.StorageInteraction;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceInteraction;
import applications.transshipment.model.resources.storage.DefaultStorageInteraction;
import applications.transshipment.model.structs.Train;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.routing.RouteFinder;
import applications.transshipment.routing.TransportGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class MultiJobTerminalProblem extends MultiModeJobProblem<RoutingTransportOperation, LoadUnitJob> implements TerminalProblem, Cloneable {

    private Scale scale;
    private Terminal terminal;
    private List<Train> trains;
    private ArrayList<LoadUnitJob> stammrelation;

    private DefaultStorageInteraction defStorage = new DefaultStorageInteraction();
    private DefaultConveyanceInteraction defTransport = new DefaultConveyanceInteraction();
    private final InteractionMapper interactionRuleMapper;

    private HashMap<String, LoadUnitJob> luIDToJobMap;
    private RouteFinder finder;
    private final List<LoadUnit> loadUnits;

    @Deprecated
    public List<LoadUnitJob> notDirectlyTransportable;

    Map<LoadUnitJob, TimeSlot> jobTimeWindows;

//    public LoadUnitJobSchedule startSchedule;
    public MultiJobTerminalProblem(Terminal terminal, List<Train> trains, List<LoadUnitJob> jobs, InteractionMapper interactionRuleMapper, ScheduleManagerBuilder builder, TimeSlot optimizationTimeSlot) {
        super(optimizationTimeSlot, jobs, terminal.getAllResources(), builder);

        this.interactionRuleMapper = interactionRuleMapper;
        this.trains = trains;
        this.terminal = terminal;
        this.loadUnits = new ArrayList<>();
        for (LoadUnitJob loadUnitJob : getJobs()) {
            this.loadUnits.add(loadUnitJob.getLoadUnit());
        }

    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    @Override
    public MultiJobTerminalProblem clone() {
        try {
            return (MultiJobTerminalProblem) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MultiJobTerminalProblem.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public StorageInteraction getStorageInteractionRule(LoadUnitStorage storage) {
        return interactionRuleMapper.getStorageRules().get(storage) != null ? interactionRuleMapper.getStorageRules().get(storage) : defStorage;
    }

    @Override
    public ConveyanceInteraction getConveyanceSystemInteractionRule(ConveyanceSystem cs) {
        return interactionRuleMapper.getTransportationRules().get(cs) != null ? interactionRuleMapper.getTransportationRules().get(cs) : defTransport;
    }

    public InteractionMapper getInteractionRuleMapper() {
        return interactionRuleMapper;
    }

    @Override
    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public List<Train> getTrains() {
        return this.trains;
    }

    @Override
    public void setJobs(List<LoadUnitJob> jobs) {
        this.stammrelation = new ArrayList<>();

        this.luIDToJobMap = new HashMap<>();
        for (MultiModeJob job : jobs) {
            if (job instanceof LoadUnitJob) {
                LoadUnitJob luJob = (LoadUnitJob) job;
                LoadUnit loadUnit = luJob.getLoadUnit();
                this.luIDToJobMap.put(loadUnit.getID(), luJob);
                if (luJob.getOrigin().equals(luJob.getDestination())) {
                    stammrelation.add(luJob);
                }
            }
        }
        List<LoadUnitJob> jobsToAdd = jobs;
        jobsToAdd.removeAll(stammrelation);
        super.setJobs(jobsToAdd);

    }

    public LoadUnitJob getJob(String luName) {
        return this.luIDToJobMap.get(luName);
    }

    public RouteFinder getRouteFinder() {
        return finder;
    }

    public void setRouteFinder(RouteFinder finder) {
        this.finder = finder;
        finder.setProblem(this);
    }

    public List<LoadUnit> getLoadUnits() {
        return loadUnits;
    }

    /**
     *
     * @return
     */
    @Override
    public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> getEalosaes() {
        throw new UnsupportedOperationException("Nicht implementiert! Dafür bitte getJobEarliestStart benutzen");
    }

    public Map<LoadUnitJob, TimeSlot> getJobTimeWindows() {
        return jobTimeWindows;
    }

    public void setJobTimeWindows(Map<LoadUnitJob, TimeSlot> jobTimewindows) {
        this.jobTimeWindows = jobTimewindows;
        for (LoadUnitJob loadUnitJob : jobTimewindows.keySet()) {
            if (jobTimewindows.get(loadUnitJob).equals(TimeSlot.nullTimeSlot)) {
                this.remove(loadUnitJob);
                System.err.println("Lösche Job:" + loadUnitJob);
            }
        }
    }

    @Override
    public JobOnNodeDiagramm<LoadUnitJob> getJobOnNodeDiagramm() {
        return (JobOnNodeDiagramm<LoadUnitJob>) super.getJobOnNodeDiagramm(); //To change body of generated methods, choose Tools | Templates.
    }
 
    @Override
    public Collection<LoadUnitJob> getStammRelation() {
        return stammrelation;
    }

    @Override
    public TransportGraph getStaticTransportGraph() {
        return finder.getStaticGraph();
    }
}
