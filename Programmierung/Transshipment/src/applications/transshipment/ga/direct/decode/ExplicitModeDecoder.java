/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.decode;

import ga.individuals.IntegerIndividual;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.ga.direct.routeDetermination.ListenerEmbeddedEALOSAEBuilder;
import applications.transshipment.ga.direct.routeDetermination.EALOSAEViolationListener;
import applications.transshipment.ga.direct.routeDetermination.ViolationEvent;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import ga.Parameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public final class ExplicitModeDecoder implements EALOSAEViolationListener {

    private final IntegerIndividual indModes;
    private final MultiJobTerminalProblem problem;

    private final List<RoutingTransportOperation> chosenOperations;
    private final ListenerEmbeddedEALOSAEBuilder builder;
    public HashMap<LoadUnitJob, JobOperationList<RoutingTransportOperation>> routingsPerJob = new HashMap<LoadUnitJob, JobOperationList<RoutingTransportOperation>>();
    private final Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes;

    public ExplicitModeDecoder(IntegerIndividual indMode, MultiJobTerminalProblem problem) {

        this.indModes = indMode.clone();
        this.problem = problem;

        /**
         *
         * #####################################################################
         * GRAPH UND EALOSAEBuilder initialiseren
         * #####################################################################
         */
        /**
         * Auswahl der Routen berücksichtigen.
         */
        this.chosenOperations = new ArrayList<>();
        List<LoadUnitJob> jobs = problem.getJobs();
        /**
         * Schleife über alle Jobs, indem über alle Gene des Tails gegangen
         * werden.
         */
        for (int i = 0; i < this.indModes.size(); i++) {
            /**
             * Bestimme die ausgewählte Route
             */
            Integer routingNumber = this.indModes.get(i);
            List<JobOperationList<RoutingTransportOperation>> routings = jobs.get(i).getRoutings();
            if (routingNumber > routings.size() - 1) {
                throw new IllegalArgumentException("Zu großer Index bei der Routenwahl: " + jobs.get(i) + " angefragt:" + routingNumber + "/" + routings.size());
            }
            JobOperationList<RoutingTransportOperation> routing = routings.get(routingNumber);
            if (this.routingsPerJob != null) {
                routingsPerJob.put(jobs.get(i), routing);
            }
            this.chosenOperations.addAll(routing);
        }
        /**
         * Activity - On - Node - Diagramm erstellen. EALOSAES für die aktiven
         * Routen/Moden bestimmen. Schedule erzeugen.
         */
        this.builder = new ListenerEmbeddedEALOSAEBuilder(chosenOperations, problem);
        this.builder.addListener(this);
        this.ealosaes = builder.getEALOSAES();

        this.chosenOperations.clear();
        for (int i = 0; i < this.indModes.size(); i++) {
            /**
             * Bestimme die ausgewählte Route
             */
            Integer routingNumber = this.indModes.get(i);
            JobOperationList<RoutingTransportOperation> routing = jobs.get(i).getRoutings().get(routingNumber);
            this.chosenOperations.addAll(routing);
        }

    }

    /**
     * Erzeugt die Activitätsliste und die benötigten detailierten Operationen.
     *
     * @param event
     */
    @Override
    public void react(ViolationEvent event) {
        //Bestimme Jobs der Zusammenhangskomponente
        List<LoadUnitJob> jobsOfComponent = new ArrayList<>();
        for (RoutingTransportOperation routingTransportOperation : event.connectionComponent.vertexSet()) {
            if (!jobsOfComponent.contains(routingTransportOperation.getJob())) {
                jobsOfComponent.add(routingTransportOperation.getJob());
            }
        }
        /**
         * Wähle andere Route
         */
        List<RoutingTransportOperation> newOperations = new ArrayList<>();
        List<LoadUnitJob> candidates = new ArrayList<>(jobsOfComponent);

        while (!candidates.isEmpty()) {
            int randomValue = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, candidates.size() - 1);
            LoadUnitJob jobToModify = candidates.remove(randomValue);

            int indexOfJobToModify = problem.getJobs().indexOf(jobToModify);
            Integer oldMode = indModes.get(indexOfJobToModify);
            if (oldMode < jobToModify.getRoutings().size() - 1) {
                indModes.set(indexOfJobToModify, oldMode + 1);
                /**
                 * Operationen auswählen für alle Jobs
                 */
                for (LoadUnitJob currentJob : jobsOfComponent) {
                    /**
                     * Bestimme die ausgewählte Route
                     */
                    int indexOfCurrentJob = problem.getJobs().indexOf(currentJob);
                    Integer routingNumber = indModes.get(indexOfCurrentJob);
                    JobOperationList<RoutingTransportOperation> routing = problem.getJobs().get(indexOfCurrentJob).getRoutings().get(routingNumber);
                    newOperations.addAll(routing);
                }

                /**
                 * Entferne alte Zusammenhangskomponente, die nicht passte.
                 */
                builder.removeComponentAndEALOSAE(event.connectionComponent);
                /**
                 * Füge neue Zusammenhangskomponente hinzu.
                 */
                ActivityOnNodeGraph<RoutingTransportOperation> newSubComponent = builder.addNewComponentAndEALOSAE(newOperations);
                /**
                 * Push diese ganz nach oben im Stack.
                 */
                event.componentsToVerify.push(newSubComponent);
                return;
            }
        }
        throw new UnsupportedOperationException("Zeitliches Problem bei Routenwahl kann nicht aufgelöst werden");
    }

    public IntegerIndividual getNewIndModes() {
        return indModes;
    }

    public MultiJobTerminalProblem getProblem() {
        return problem;
    }

    public List<RoutingTransportOperation> getChosenOperations() {
        return chosenOperations;
    }

    public ActivityOnNodeGraph<RoutingTransportOperation> getGraph() {
        ActivityOnNodeGraph<RoutingTransportOperation> subGraph = problem.getActivityOnNodeDiagramm().getSubGraph(chosenOperations);
        return subGraph;
    }

    public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> getEalosaes() {
        return this.ealosaes;
    }

}
