package gantt;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.operations.OperationImplementation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Train;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsView;
import com.flexganttfx.view.graphics.renderer.ActivityBarRenderer;
import com.flexganttfx.view.timeline.Timeline;
import com.flexganttfx.view.util.GanttChartStatusBar;
import com.flexganttfx.view.util.GanttChartToolBar;
import gui.CenterPane;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.TreeSet;
import javafx.scene.AmbientLight;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import project.GUI;

/**
 *
 * @author hagedorn
 */
public final class Gantt extends CenterPane {

    AmbientLight ambient;
    PointLight pointLight;

    public Gantt(GUI gui) {
        super(gui, new BorderPane());

        BorderPane borderPane = (BorderPane) this.getRoot();

        // Create the root row.
        GanttResource root = new GanttResource("Terminal");
        root.setExpanded(true);

        // Create the Gantt chart
        GanttChart<GanttResource> gantt = new GanttChart<>(root);

        Layer layer = new Layer("Ressourcen");
        Layer trainLayer = new Layer("Züge");
        gantt.getLayers().add(layer);
        gantt.getLayers().add(trainLayer);

        LoadUnitJobSchedule schedule = gui.getSchedule();
        Collection<ConveyanceSystem> conveyanceSystems = gui.getTerminal().getConveyanceSystems();
        LCSystem lcsystem = null;
        for (ConveyanceSystem conveyanceSystem : conveyanceSystems) {
            GanttResource resource = new GanttResource(conveyanceSystem.toString());
            if (conveyanceSystem instanceof LCSystem) {
                lcsystem = (LCSystem) conveyanceSystem;
            }
            Collection<Operation> operationsForResource = schedule.getOperationsForResource(conveyanceSystem);
            for (Operation operation : operationsForResource) {
                if (operation instanceof TransportOperation) {
                    GanttOperation gantOp = new GanttOperation(operation, schedule.get(operation).longValue());
                    resource.addActivity(layer, gantOp);
                }

            }
            root.getChildren().add(resource);
        }

        if (lcsystem != null) {

            GanttResource resourceParentLC = new GanttResource("Parent:" + lcsystem.toString());

            root.getChildren().add(resourceParentLC);

            for (Agent agent : lcsystem.getSharingResources()) {
                GanttResource resource = new GanttResource(agent.toString());
                Collection<Operation> operationsForResource = schedule.getOperationsForResource(agent);
                System.out.println("Anzahl für AGV" + operationsForResource.size());
                for (Operation operation : operationsForResource) {
                    GanttOperation gantOp = new GanttOperation(operation, schedule.get(operation).longValue());
                    resource.addActivity(layer, gantOp);

                }
                resourceParentLC.getChildren().add(resource);
            }
        }

        for (LoadUnitStorage loadUnitStorage : gui.getTerminal().getStorages()) {
            GanttResource resourceParentLC = new GanttResource("Parent:" + loadUnitStorage.toString());
            root.getChildren().add(resourceParentLC);
            if (loadUnitStorage instanceof LocationBasedStorage) {
                LocationBasedStorage stor = (LocationBasedStorage) loadUnitStorage;
                for (StorageLocation storageLocation : stor.getStorageLocations()) {

                    GanttResource resource = new GanttResource(storageLocation.toString());
                    Collection<Operation> operationsForResource = schedule.getOperationsForResource(storageLocation);
                    for (Operation operation : operationsForResource) {
                        GanttOperation gantOp = new GanttOperation(operation, schedule.get(operation).longValue());
                        resource.addActivity(layer, gantOp);
                    }
                    resourceParentLC.getChildren().add(resource);
                }
            }
        }

        GanttResource trains = new GanttResource("Züge");

        root.getChildren().add(trains);
        for (Train train : gui.getProblem().getTrains()) {
            GanttResource resource = new GanttResource("Zug " + train.getNumber());
            GanttOperation ganttOp = new GanttOperation(new OperationImplementation(train.getTemporalAvailability().getAllOverTimeSlot().getDuration().longValue()), train.getTemporalAvailability().getFromWhen().longValue());
            resource.addActivity(trainLayer, ganttOp);
            trains.getChildren().add(resource);
            for (Slot slot : train.getStorageLocations()) {

                GanttResource slotResource = new GanttResource(slot.toString());
                Collection<Operation> operationsForResource = schedule.getOperationsForResource(slot);
                for (Operation operation : operationsForResource) {
                    GanttOperation gantOp = new GanttOperation(operation, schedule.get(operation).longValue());
                    slotResource.addActivity(layer, gantOp);
                }
                resource.getChildren().add(slotResource);
            }
        }

        com.flexganttfx.view.timeline.Timeline timeline = gantt.getTimeline();

        timeline.showTemporalUnit(ChronoUnit.HOURS,
                10);
        timeline.setZoomMode(Timeline.ZoomMode.CENTER);

        GraphicsView<GanttResource> graphics = gantt.getGraphics();
        ActivityBarRenderer<Activity> activityBarRenderer = new ActivityBarRenderer<>(graphics, "Operation Renderer");

        graphics.setActivityRenderer(GanttOperation.class, GanttLayout.class,
                activityBarRenderer);
        graphics.showAllActivities();

        borderPane.setTop(
                new GanttChartToolBar(gantt));
        borderPane.setCenter(gantt);

        borderPane.setBottom(
                new GanttChartStatusBar(gantt));

    }

    @Override
    public HBox leftElements() {
        HBox controls = new HBox();
        controls.setPrefWidth(250);
        return controls;
    }

    @Override
    public void updateTransform() {
    }

    @Override
    public Node bottomElements() {
        return null;
    }

}
