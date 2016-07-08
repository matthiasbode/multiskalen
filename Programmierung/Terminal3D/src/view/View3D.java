package view;

import applications.mmrcsp.model.basics.TimeSlotList;
import applications.transshipment.generator.json.JsonTerminal;
import applications.transshipment.generator.json.JsonTerminalResource;
import applications.transshipment.model.basics.util.LoadUnitMovementCalculator;
import applications.transshipment.model.basics.util.LoadUnitPositionAndOrientation3DInTime;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.structs.Train;
import applications.transshipment.model.structs.Wagon;
import gui.CenterPane;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Shear;
import javafx.util.Duration;
import jfxtras.scene.control.LocalDateTimeTextField;
import project.GUI;
import java.io.InputStream;
import java.time.ZonedDateTime;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import math.geometry.PositionAndOrientation3DInTime;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author hagedorn
 */
public final class View3D extends CenterPane {

    //mouse positions
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    boolean viewMode3D = true;
    final Camera cameraGroup;
    final Shear shear = new Shear();

    Group craneGroup = new Group();
    CheckBox checkCraneVisible;
    Timeline timeline;

    public View3D(GUI gui) {

        super(gui, new Camera());

        cameraGroup = (Camera) this.getRoot();
        cameraGroup.setScene(this);
        timeline = new Timeline();

        setViewMode2D();
        addMouseInteraction();

        cameraGroup.getChildren().add(getTerminalLayoutFromJSon());
        //cameraGroup.getChildren().add(getTrains());
        cameraGroup.getChildren().add(getAxisGroup());
        cameraGroup.getChildren().add(getLight());
        updateTransform();

    }

    public void setSchedule(LoadUnitJobSchedule s) {
        cameraGroup.getChildren().add(getContainerGroup(s));
        craneGroup.getChildren().add(getCraneGroup(s));

    }

    public void addMouseInteraction() {
        this.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent me) {
                double newScale = cameraGroup.s.getX() * (1 + (me.getDeltaY() / 800));
                cameraGroup.zoomOnPoint(newScale, new Point3D(me.getX(), me.getY(), 0));

            }
        });

        MouseListener me = new MouseListener(this);
        this.setOnMouseClicked(me);
        this.setOnMousePressed(me);
        this.setOnMouseDragged(me);
    }

    public Group getLight() {
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        Group lightGroup = new Group(ambientLight);
        return lightGroup;
    }

    @Override
    public HBox leftElements() {
        HBox controls = new HBox();
        controls.setPrefWidth(250);
        controls.setSpacing(20);
        controls.setPadding(new Insets(15, 12, 15, 12));

        GridPane gridViewOperations = new GridPane();
        Text txtMouseFunction = new Text("Darstellungsoptionen");
        txtMouseFunction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        gridViewOperations.add(txtMouseFunction, 0, 0, 2, 1);

        Button btnChangeViewMode = new Button("3D-Modus");
        btnChangeViewMode.setPrefWidth(100);
        btnChangeViewMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (viewMode3D) {
                    btnChangeViewMode.setText("View Mode 3D");
                    setViewMode2D();
                } else {
                    btnChangeViewMode.setText("View Mode 2D");
                    setViewMode3D();
                }
            }
        });
        gridViewOperations.add(btnChangeViewMode, 0, 1);

        Button btnReset = new Button("Zurücksetzen");
        btnReset.setPrefWidth(100);
        btnReset.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (viewMode3D) {
                    setViewMode3D();
                } else {
                    setViewMode2D();
                }
                //camera1.setCamScale(Terminal3D.this);
            }

        });
        checkCraneVisible = new CheckBox("Kräne sichtbar");
        checkCraneVisible.setPrefWidth(200);
        checkCraneVisible.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (checkCraneVisible.isSelected()) {
                    cameraGroup.getChildren().add(craneGroup);
                } else {
                    cameraGroup.getChildren().remove(craneGroup);
                }

            }

        });

        gridViewOperations.add(btnReset, 1, 1);
        gridViewOperations.add(checkCraneVisible, 0, 2, 2, 1);

        controls.getChildren().addAll(gridViewOperations);

        return controls;
    }

    @Override
    public void updateTransform() {
        if (viewMode3D) {
            setViewMode3D();
        } else {
            setViewMode2D();
        }
    }

    public void setViewMode2D() {
        View3D.this.setCamera(new ParallelCamera());
        viewMode3D = false;
        cameraGroup.convert(getTerminalLayoutFromJSon().getBoundsInLocal(), View3D.this.getWidth(), View3D.this.getHeight());
    }

    public void setViewMode3D() {
        View3D.this.setCamera(new PerspectiveCamera(false));
        cameraGroup.convert(getTerminalLayoutFromJSon().getBoundsInLocal(), View3D.this.getWidth(), View3D.this.getHeight());
        viewMode3D = true;
    }

    public Group getTerminalLayoutFromJSon() {
        Group result = new Group();
        System.err.println("Folgende Objekte wurden eingelesen:");
        InputStream resourceAsStream = GUI.class.getResourceAsStream("DuisburgTerminal.json");
        JsonTerminal jsonTerminal = JSONSerialisierung.importJSON(resourceAsStream, JsonTerminal.class);
        for (JsonTerminalResource resource : jsonTerminal.getResources()) {
            LayoutElement3D e = new LayoutElement3D(resource);
            result.getChildren().add(e);
            System.err.println(e);
        }
        System.err.println("Insgesamt wurden " + result.getChildren().size() + " Elemente eingelesen.");
        return result;
    }

    public Group getCraneGroup(LoadUnitJobSchedule s) {
        Group craneCollection = new Group();

        HashMap<Crane, List<PositionAndOrientation3DInTime>> keyPointsCrane = LoadUnitMovementCalculator.getKeyPointsCrane(s, gui.getProblem());
        ArrayList<Crane> cranes = new ArrayList<>();
        for (ConveyanceSystem conveyanceSystem : gui.getProblem().getTerminal().getConveyanceSystems()) {
            if (conveyanceSystem instanceof Crane) {
                cranes.add((Crane) conveyanceSystem);
            }
        }

        int i = 0;
        for (ConveyanceSystem conveyanceSystem : cranes) {
            if (conveyanceSystem instanceof Crane) {
                Crane c = (Crane) conveyanceSystem; 
                

                List<PositionAndOrientation3DInTime> positionsForCrane = keyPointsCrane.get(c);
                System.out.println("Kranorte hinzufügen");
                System.out.println(c + ":\t" + positionsForCrane.size());
                ArrayList<KeyFrame> frames = new ArrayList<>();
                
                //Crane3D crane3D = new Crane3D(new Color(1.0f, 0.54901963f, 0.0f, 0.4f),positionsForCrane);
                Crane3D crane3D = new Crane3D(Color.DARKGRAY,positionsForCrane);
                craneCollection.getChildren().add(crane3D); 
                
                for (PositionAndOrientation3DInTime pos : positionsForCrane) {
                                   
                    KeyValue kBewegenX = new KeyValue(crane3D.translateXProperty(), pos.getPosition().x, Interpolator.LINEAR);
                    KeyValue kBewegenY = new KeyValue(crane3D.translateYProperty(), pos.getPosition().y, Interpolator.LINEAR);
                    KeyValue kBewegenZ = new KeyValue(crane3D.translateZProperty(), pos.getPosition().z, Interpolator.LINEAR);
                    KeyFrame FrameBewegenGes = new KeyFrame(new Duration(pos.getTime()), kBewegenX);
                    frames.add(FrameBewegenGes);
                }
                timeline.getKeyFrames().addAll(frames);

                i++;
            }
        }
        return craneCollection;
    }

    public Group getTrains() {
        Group trainGroup = new Group();
        for (Train train : gui.getProblem().getTrains()) {
            for (Wagon wagon : train.getWagons()) {
                Area area = wagon.getGeneralOperatingArea();
                Rectangle2D bounds2D = area.getBounds2D();
                Box wBox = new Box(bounds2D.getWidth(), bounds2D.getHeight(), wagon.wagonLoadingHeight / 2.);
                PhongMaterial material = new PhongMaterial();
                material.setSpecularColor(Color.BEIGE);
                material.setDiffuseColor(Color.BEIGE);
                wBox.setMaterial(material);
                wBox.setTranslateX(bounds2D.getCenterX());
                wBox.setTranslateY(bounds2D.getCenterY());
                wBox.setTranslateZ(wagon.getPosition().z);
                wBox.setVisible(false);

                long startSichtbar = wagon.getTemporalAvailability().getFromWhen().longValue();
                KeyValue sichtbar = new KeyValue(wBox.visibleProperty(), true, Interpolator.DISCRETE);
                KeyFrame FrameSichtbar = new KeyFrame(new Duration(startSichtbar), sichtbar);
                timeline.getKeyFrames().add(FrameSichtbar);

                double endSichtbar = wagon.getTemporalAvailability().getUntilWhen().doubleValue();
                KeyValue sichtbarEnde = new KeyValue(wBox.visibleProperty(), false, Interpolator.DISCRETE);
                KeyFrame FrameSichtbarEnde = new KeyFrame(new Duration(endSichtbar), sichtbarEnde);
                timeline.getKeyFrames().add(FrameSichtbarEnde);

                trainGroup.getChildren().add(wBox);
            }

        }
//        if (!(craneGroup == null) && !(craneGroup.getChildren().size() == 0)) {
//            for (Node crane : craneGroup.getChildren()) {
//                Crane3D crane3d = (Crane3D) crane;
//                timeline.getKeyFrames().add(crane3d.getAnimation());
//            }
//
//        }

        return trainGroup;
    }

    public Group getContainerGroup(LoadUnitJobSchedule s) {

        Group containerGroup = new Group();

        TimeSlotList t = gui.getTerminal().getTemporalAvailability();
        System.out.println(t.getAllOverTimeSlot());
        Map<LoadUnit, List<LoadUnitPositionAndOrientation3DInTime>> keyPoints = LoadUnitMovementCalculator.getKeyPoints(s);

        HashMap<Container3D, List<KeyFrame>> keyFrames = new HashMap<Container3D, List<KeyFrame>>();

        /**
         * Stammrelation
         */
        gui.getProblem().getStammRelation().parallelStream().forEach((e) -> {
            Container3D container = new Container3D(e.getLoadUnit());
            container.setGreen();
            //containerGroup.getChildren().add(container);
            ArrayList<KeyFrame> frames = new ArrayList<>();

            double startSichtbar = e.getOrigin().getTemporalAvailability().getFromWhen().doubleValue();
            KeyValue sichtbar = new KeyValue(container.visibleProperty(), true, Interpolator.DISCRETE);
            KeyValue keyValueX = new KeyValue(container.translateXProperty(), e.getOrigin().getPosition().x, Interpolator.DISCRETE);
            KeyValue keyValueY = new KeyValue(container.translateYProperty(), e.getOrigin().getPosition().y, Interpolator.DISCRETE);
            KeyValue keyValueZ = new KeyValue(container.translateZProperty(), e.getOrigin().getPosition().z + e.getLoadUnit().getHeight() / 2., Interpolator.DISCRETE);
            KeyFrame FrameSichtbar = new KeyFrame(new Duration(startSichtbar), sichtbar, keyValueX, keyValueY, keyValueZ);
//            timeline.getKeyFrames().add(FrameSichtbar);
            frames.add(FrameSichtbar);
            double endSichtbar = e.getDestination().getTemporalAvailability().getUntilWhen().doubleValue();
            KeyValue sichtbarEnde = new KeyValue(container.visibleProperty(), false, Interpolator.DISCRETE);
            KeyFrame FrameSichtbarEnde = new KeyFrame(new Duration(endSichtbar), sichtbarEnde);
//            timeline.getKeyFrames().add(FrameSichtbarEnde);
            frames.add(FrameSichtbarEnde);
            keyFrames.put(container, frames);
        });

        /**
         * KeyPoints für alle Bewegungen.
         */
        keyPoints.keySet().parallelStream().forEach((e) -> {
            Container3D container = new Container3D(e);
//            containerGroup.getChildren().add(container);
            ArrayList<KeyFrame> frames = new ArrayList<>();
            double startSichtbar = e.getOrigin().getTemporalAvailability().getFromWhen().doubleValue();

            KeyValue sichtbar = new KeyValue(container.visibleProperty(), true, Interpolator.DISCRETE);
            KeyValue keyValueX = new KeyValue(container.translateXProperty(), e.getOrigin().getPosition().x, Interpolator.DISCRETE);
            KeyValue keyValueY = new KeyValue(container.translateYProperty(), e.getOrigin().getPosition().y, Interpolator.DISCRETE);
            KeyValue keyValueZ = new KeyValue(container.translateZProperty(), e.getOrigin().getPosition().z + e.getHeight() / 2., Interpolator.DISCRETE);
            KeyFrame FrameSichtbar = new KeyFrame(new Duration(startSichtbar), sichtbar, keyValueX, keyValueY, keyValueZ);
//            timeline.getKeyFrames().add(FrameSichtbar);
            frames.add(FrameSichtbar);

            for (LoadUnitPositionAndOrientation3DInTime pos : keyPoints.get(e)) {
                KeyValue kBewegenX = new KeyValue(container.translateXProperty(), pos.getPosition().x, Interpolator.LINEAR);// pos.getInterpolator());
                KeyValue kBewegenY = new KeyValue(container.translateYProperty(), pos.getPosition().y, Interpolator.LINEAR);//, pos.getInterpolator());
                KeyValue kBewegenZ = new KeyValue(container.translateZProperty(), pos.getPosition().z + e.getHeight() / 2., Interpolator.LINEAR);//, pos.getInterpolator());
                KeyFrame FrameBewegenGes = new KeyFrame(new Duration(pos.getTime()), kBewegenX, kBewegenY, kBewegenZ);
                if (pos.getOperation() instanceof StoreOperation) {
                    StoreOperation o = (StoreOperation) pos.getOperation();
                    if (o.getResource().equals(e.getDestination())) {
                        KeyValue farbe = new KeyValue(container.material.diffuseColorProperty(), Color.GREEN, Interpolator.DISCRETE);
                        KeyValue farbe2 = new KeyValue(container.material.specularColorProperty(), Color.DARKGREEN, Interpolator.DISCRETE);
                        FrameBewegenGes = new KeyFrame(new Duration(pos.getTime()), kBewegenX, kBewegenY, kBewegenZ, farbe, farbe2);

                    }
                }
//                timeline.getKeyFrames().add(FrameBewegenGes);
                frames.add(FrameBewegenGes);
            }

            double endSichtbar = e.getDestination().getTemporalAvailability().getUntilWhen().doubleValue();
            KeyValue sichtbarEnde = new KeyValue(container.visibleProperty(), false, Interpolator.DISCRETE);
            KeyFrame FrameSichtbarEnde = new KeyFrame(new Duration(endSichtbar), sichtbarEnde);
//            timeline.getKeyFrames().add(FrameSichtbarEnde);
            frames.add(FrameSichtbarEnde);
            keyFrames.put(container, frames);
        });

        for (Container3D container3D : keyFrames.keySet()) {
            containerGroup.getChildren().add(container3D);
            timeline.getKeyFrames().addAll(keyFrames.get(container3D));
        }

        return containerGroup;

    }

    public Group getAxisGroup() {
        final Group axisGroup = new Group();
        final Box xAxis = new Box(240.0, 1, 1);
        final Box yAxis = new Box(1, 240.0, 1);
        final Box zAxis = new Box(1, 1, 240.0);

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);

        return axisGroup;
    }

    @Override
    public Node bottomElements() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(gui.getTerminal().getTemporalAvailability().getFromWhen().longValue());

        LocalDateTimeTextField dateTimePicker = new LocalDateTimeTextField(LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault()));
        dateTimePicker.setPrefWidth(300);

        dateTimePicker.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    LocalDateTime localDateTime = dateTimePicker.getLocalDateTime();
                    long newValue = localDateTime.toEpochSecond(ZoneOffset.ofHours(2));
                    timeline.jumpTo(new Duration(newValue * 1000));
                }
            }
        });

        timeline.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable ov) {
                long time = (long) timeline.getCurrentTime().toMillis() / 1000;
                ZoneId zoneID = ZoneId.of("Europe/Berlin");
                LocalDateTime dt = LocalDateTime.now();
                dateTimePicker.setLocalDateTime(LocalDateTime.ofEpochSecond(time, 0, dt.atZone(zoneID).getOffset()));
            }
        });
        DoubleProperty rateProperty = timeline.rateProperty();
        final Label valueLabel = new Label(rateProperty.getValue().toString());

        final Slider slider = new Slider();
        slider.setMin(-100);
        slider.setMax(100);
        slider.setValue(1);
        slider.setPrefWidth(400);

        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ov) {
                rateProperty.set(slider.getValue());
                valueLabel.setText(Double.toString(slider.getValue()));
            }
        });

        Button btnStartStop = new Button("Start");
        btnStartStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (btnStartStop.getText().equals("Start")) {
                    btnStartStop.setText("Stop");

                    timeline.playFrom(new Duration(gui.getTerminal().getTemporalAvailability().getFromWhen().doubleValue()));
                } else {
                    btnStartStop.setText("Start");
                    timeline.pause();
                }
            }

        });
        HBox hbox = new HBox(20, dateTimePicker, slider, valueLabel, btnStartStop);
        hbox.setPrefWidth(600);
        return hbox;
    }
}
