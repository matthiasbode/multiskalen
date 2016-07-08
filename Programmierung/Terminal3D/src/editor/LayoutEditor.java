package editor;

import applications.transshipment.generator.json.JsonTerminal;
import applications.transshipment.generator.json.JsonTerminalResource;
import gui.CenterPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.GUI;
import project.WorkingSpace;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author hagedorn
 */
public final class LayoutEditor extends CenterPane {

    public final static String KEY_NONESELECTED = "<none>";
    protected boolean start = true;
    public Camera cameraGroup;

    private JsonTerminal jsonTerminal;
    public boolean selectedPointer = true;
    public LayoutElement selectedElement;
    public ArrayList<LayoutElement> elements = new ArrayList<>();
    public Group terminal = new Group();

    private MouseListenerDraw listenerDraw;
    private MouseListenerPointer listenerPointer;

    protected ToggleGroup toggleGroup;
    protected ToggleButton tbPointer, tbDraw;
    protected Button btnDelete, btnEdit;
    protected ComboBox<String> boxDrawType, boxElementType;
    protected CheckBox checkCraneRunway, checkLCS, checkStorageRow, checkRailroadTrack, checkHandover, checkCoordinates;
    protected TextField txtElementHeight, txtElementWidth, txtElementX, txtElementY;

    public LayoutEditor(GUI gui) {

        super(gui, new Camera());

        this.cameraGroup = (Camera) this.getRoot();
        this.cameraGroup.setScene(this);
        this.initializeControls();
        this.resetElementProperties();
        this.setMouseInteraction();
        this.terminal = getTerminalLayoutFromJSon();
        refreshView();
        this.set2DCamera(terminal.getBoundsInParent());

    }

    public void initializeControls() {
        checkCoordinates = new CheckBox("Koordinatensystem");
        tbPointer = new ToggleButton();
        tbDraw = new ToggleButton();
        checkCoordinates.setSelected(true);
        checkCraneRunway = new CheckBox();
        checkHandover = new CheckBox();
        checkLCS = new CheckBox();
        checkRailroadTrack = new CheckBox();
        checkStorageRow = new CheckBox();
        boxDrawType = new ComboBox<>();
        listenerDraw = new MouseListenerDraw(this);
        listenerPointer = new MouseListenerPointer(this);
        toggleGroup = new ToggleGroup();
        txtElementWidth = new TextField();
        txtElementWidth.setDisable(false);
        txtElementHeight = new TextField();
        txtElementHeight.setDisable(false);
        txtElementX = new TextField();
        txtElementX.setDisable(false);
        txtElementY = new TextField();
        txtElementY.setDisable(false);
        boxElementType = new ComboBox<>();
        btnDelete = new Button("Löschen");
        btnDelete.setVisible(false);
        btnDelete.setPrefWidth(100);
        btnEdit = new Button("Ändern");
        btnEdit.setVisible(false);
        btnEdit.setPrefWidth(100);
        enableEditElementProperties(false);

    }

    public void setMouseInteraction() {
        this.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent me) {
                double newScale = cameraGroup.s.getX() * (1 + (me.getDeltaY() / 800));
                cameraGroup.zoomOnPoint(newScale, new Point3D(me.getX(), me.getY(), 0));

            }
        });
        System.out.println("MouseInteraction hinzugefügt");
        if (selectedPointer) {
            System.out.println("Zeigemodus");
            this.setOnMouseClicked(listenerPointer);
            this.setOnMousePressed(listenerPointer);
            this.setOnMouseDragged(listenerPointer);

            this.removeEventHandler(MouseEvent.MOUSE_CLICKED, listenerDraw);
            this.removeEventHandler(MouseEvent.MOUSE_PRESSED, listenerDraw);
            this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, listenerDraw);

        } else {
            System.out.println("Zeichenmodus");
            this.setOnMouseClicked(listenerDraw);
            this.setOnMousePressed(listenerDraw);
            this.setOnMouseDragged(listenerDraw);

            this.removeEventHandler(MouseEvent.MOUSE_CLICKED, listenerPointer);
            this.removeEventHandler(MouseEvent.MOUSE_PRESSED, listenerPointer);
            this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, listenerPointer);

        }
    }

    public void refreshView() {
        cameraGroup.getChildren().clear();
        cameraGroup.getChildren().add(terminal);

        if (!start) {
            terminal.getChildren().clear();

            for (LayoutElement element : elements) {
                element.addTypeColors();
                switch (element.getResource().getBezeichnung()) {
                    case JsonTerminalResource.KEY_CRANERUNWAY:
                        if (checkCraneRunway.isSelected()) {
                            terminal.getChildren().add(element);
                        }
                        break;
                    case JsonTerminalResource.KEY_HANDOVER:
                        if (checkHandover.isSelected()) {
                            terminal.getChildren().add(element);
                        }
                        break;
                    case JsonTerminalResource.KEY_LCS:
                        if (checkLCS.isSelected()) {
                            terminal.getChildren().add(element);
                        }
                        break;
                    case JsonTerminalResource.KEY_STORAGEROW:
                        if (checkStorageRow.isSelected()) {
                            terminal.getChildren().add(element);
                        }
                        break;
                    case JsonTerminalResource.KEY_RAILROADTRACK:
                        if (checkRailroadTrack.isSelected()) {
                            terminal.getChildren().add(element);
                        }
                        break;
                    default:
                        System.err.println("DEFAULT");
                        break;
                }
            }
        }
        if (checkCoordinates.isSelected()) {
            cameraGroup.getChildren().add(getAxisGroup());
        }
        cameraGroup.getChildren().add(getLight());
        
        if (start) {
            this.set2DCamera(terminal.getBoundsInParent());
        }
        
        start = false;
    }

    public void set2DCamera(Bounds b) {
        LayoutEditor.this.setCamera(new ParallelCamera());
        cameraGroup.resetCam2D(b, this.getWidth(), this.getHeight());

    }

    public Group getTerminalLayoutFromJSon() {
        Group result = new Group();
        System.err.println("Folgende Objekte wurden eingelesen:");
        InputStream resourceAsStream = GUI.class.getResourceAsStream("DuisburgTerminal.json");
        jsonTerminal = JSONSerialisierung.importJSON(resourceAsStream, JsonTerminal.class);
        for (JsonTerminalResource resource : jsonTerminal.getResources()) {

            LayoutElement e = new LayoutElement(resource);
            elements.add(e);
            result.getChildren().add(e);
            System.err.println(e);
        }
        System.err.println("Insgesamt wurden " + elements.size() + " Elemente eingelesen.");
        showMessageBox("Datei wurde eingelesen: DuisburgTerminal.json/n", null, false);
        return result;
    }

    public void load() {
        elements.clear();
        terminal.getChildren().clear();

        FileChooser fileChooser = new FileChooser();
        WorkingSpace workSpace = new WorkingSpace();
        fileChooser.setTitle("Datei zum Öffnen auswählen");

        fileChooser.setInitialDirectory(workSpace.getFolder());
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.JSON");
        fileChooser.getExtensionFilters().add(extFilterJPG);

        File file = fileChooser.showOpenDialog(null);
        System.out.println(file);
        if (file != null) {

            String path = file.getAbsolutePath();

            InputStream inputStream = null;

            try {
                inputStream = new FileInputStream(path);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(LayoutEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (inputStream != null) {

                System.err.println("Folgende Objekte wurden eingelesen:");

                InputStream resourceAsStream = inputStream;
                jsonTerminal = JSONSerialisierung.importJSON(resourceAsStream, JsonTerminal.class);
                for (JsonTerminalResource resource : jsonTerminal.getResources()) {

                    LayoutElement e = new LayoutElement(resource);
                    terminal.getChildren().add(e);
                    elements.add(e);
                    System.err.println(e);
                }
                System.err.println("Insgesamt wurden " + elements.size() + " Elemente eingelesen.");

                showMessageBox("Datei erfolgreich geöffnet:\n", file, true);
                refreshView();
            }
        } else {

            showMessageBox("Keine Datei ausgewählt!\n", null, false);

        }

    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public void save() {
        List<JsonTerminalResource> res = jsonTerminal.getResources();
        res.clear();
        for (LayoutElement e : this.elements) {
            res.remove(e.getResource());
            res.add(e.getResource());
        }

        WorkingSpace workSpace = new WorkingSpace();

        File file = new File(workSpace.getPath() + "/JsonTerminal" + getCurrentTimeStamp() + ".json");
        JSONSerialisierung.exportJSON(file, jsonTerminal, true);
        System.err.println("Datei erzeugt: " + file);

        showMessageBox("Datei erfolgreich gespeichert unter:\n", file, true);

    }

    public static void showMessageBox(String message, File f, boolean isFileMessage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Button btnDialogOkay = new Button("Okay");
        btnDialogOkay.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                dialogStage.close();
            }
        });
        dialogStage.setWidth(500);

        if (isFileMessage) {

            dialogStage.setScene(new Scene(VBoxBuilder.create().
                    children(new Text(message + " " + f), btnDialogOkay).
                    alignment(Pos.CENTER).padding(new Insets(20)).build()));
        } else {
            dialogStage.setScene(new Scene(VBoxBuilder.create().
                    children(new Text(message), btnDialogOkay).
                    alignment(Pos.CENTER).padding(new Insets(20)).build()));
        }
        dialogStage.show();
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

    public Group getLight() {
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        Group lightGroup = new Group(ambientLight);
        return lightGroup;
    }

    @Override
    public void updateTransform() {
        this.set2DCamera(terminal.getBoundsInParent());
    }

    @Override
    public Node bottomElements() {
        HBox hbox = new HBox(20);
        hbox.setPrefWidth(600);
        return hbox;
    }

    @Override
    public HBox leftElements() {
        HBox controls = new HBox();
        controls.setPrefWidth(250);
        controls.setSpacing(20);
        controls.setPadding(new Insets(15, 12, 15, 12));

        GridPane gridDataOperations = new GridPane();
        Text txtLoadSave = new Text("Dateioperation");
        txtLoadSave.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));

        Button btnSave = new Button("Speichern");
        btnSave.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.save();
            }
        });
        btnSave.setPrefWidth(100);
        Button btnLoad = new Button("Öffnen");
        btnLoad.setPrefWidth(100);
        btnLoad.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.load();
            }
        });

        gridDataOperations.add(txtLoadSave, 0, 0);
        gridDataOperations.add(btnSave, 1, 1);
        gridDataOperations.add(btnLoad, 0, 1);
        gridDataOperations.setVgap(10);

        GridPane gridViewOperations = new GridPane();
        Text txtMouseFunction = new Text("Mausfunktion");
        txtMouseFunction.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        gridViewOperations.add(txtMouseFunction, 0, 0, 2, 1);

        boxDrawType.getItems().addAll(
                JsonTerminalResource.KEY_CRANERUNWAY,
                JsonTerminalResource.KEY_HANDOVER,
                JsonTerminalResource.KEY_LCS,
                JsonTerminalResource.KEY_RAILROADTRACK,
                JsonTerminalResource.KEY_STORAGEROW);

        boxDrawType.setPrefWidth(200);

        tbPointer = new ToggleButton("Zeiger");
        tbPointer.setToggleGroup(toggleGroup);
        tbPointer.setSelected(true);
        tbPointer.setPrefWidth(100);

        tbDraw = new ToggleButton("Stift");
        tbDraw.setToggleGroup(toggleGroup);
        tbDraw.setPrefWidth(100);
        tbDraw.setSelected(false);

        toggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) -> {
            selectedPointer = tbPointer.isSelected();
            setMouseInteraction();
            refreshView();
        });
        Text txtDrawLayer = new Text("Zeichenebenen");
        txtDrawLayer.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));

        Text txtDrawMode = new Text("Zeichenmodus");
        txtDrawMode.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                LayoutEditor.this.elements.remove(LayoutEditor.this.selectedElement);
                LayoutEditor.this.refreshView();
                LayoutEditor.this.resetElementProperties();
            }
        });
        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!txtElementWidth.isDisable()) {
                    selectedElement.updateLayoutElement(Double.parseDouble(txtElementWidth.getText()),
                            Double.parseDouble(txtElementHeight.getText()),
                            Double.parseDouble(txtElementX.getText()),
                            Double.parseDouble(txtElementY.getText()),
                            boxElementType.getValue());

                    refreshView();
                    cameraGroup.getChildren().add(selectedElement.getTransformationRectangles());
                }

                enableEditElementProperties(txtElementWidth.isDisable());

            }
        });
        checkCraneRunway = new CheckBox(JsonTerminalResource.KEY_CRANERUNWAY);
        checkCraneRunway.setSelected(true);
        checkCraneRunway.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.refreshView();
                System.out.println("Hier gehts noch Checkbox");
            }
        });
        checkHandover = new CheckBox(JsonTerminalResource.KEY_HANDOVER);
        checkHandover.setSelected(true);
        checkHandover.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.refreshView();
            }
        });
        checkLCS = new CheckBox(JsonTerminalResource.KEY_LCS);
        checkLCS.setSelected(true);
        checkLCS.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.refreshView();
            }
        });
        checkRailroadTrack = new CheckBox(JsonTerminalResource.KEY_RAILROADTRACK);
        checkRailroadTrack.setSelected(true);
        checkRailroadTrack.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.refreshView();
            }
        });
        checkStorageRow = new CheckBox(JsonTerminalResource.KEY_STORAGEROW);
        checkStorageRow.setSelected(true);
        checkStorageRow.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.refreshView();
            }
        });
        boxDrawType.getSelectionModel().selectFirst();
        checkCoordinates.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor.this.refreshView();
            }
        });
        boxElementType.getItems().addAll(
                JsonTerminalResource.KEY_CRANERUNWAY,
                JsonTerminalResource.KEY_HANDOVER,
                JsonTerminalResource.KEY_LCS,
                JsonTerminalResource.KEY_RAILROADTRACK,
                JsonTerminalResource.KEY_STORAGEROW);
        boxElementType.setEditable(false);
        boxElementType.setPrefWidth(100);
        boxElementType.getSelectionModel().select(KEY_NONESELECTED);

        gridViewOperations.setVgap(10);
        gridViewOperations.add(txtDrawMode, 0, 2, 2, 1);
        gridViewOperations.add(boxDrawType, 0, 3, 2, 1);
        gridViewOperations.add(tbPointer, 0, 1);
        gridViewOperations.add(tbDraw, 1, 1);
        gridViewOperations.add(txtDrawLayer, 0, 4, 2, 1);
        gridViewOperations.add(checkCraneRunway, 0, 5, 2, 1);
        gridViewOperations.add(checkHandover, 0, 6, 2, 1);
        gridViewOperations.add(checkLCS, 0, 7, 2, 1);
        gridViewOperations.add(checkRailroadTrack, 0, 8, 2, 1);
        gridViewOperations.add(checkStorageRow, 0, 9, 2, 1);
        gridViewOperations.add(checkCoordinates, 0, 10, 2, 1);

        //Elementeigenschaften
        GridPane gridElementOperations = new GridPane();
        gridElementOperations.setVgap(10);
        Text titleElements = new Text("Elementeigenschaften");
        titleElements.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        gridElementOperations.add(titleElements, 0, 0, 2, 1);

        Text lblElementType = new Text("Typ");
        gridElementOperations.add(lblElementType, 0, 1, 1, 1);
        gridElementOperations.add(boxElementType, 1, 1, 1, 1);

        Text lblElementWidth = new Text("Länge [m]:");
        gridElementOperations.add(lblElementWidth, 0, 2, 1, 1);
        gridElementOperations.add(txtElementWidth, 1, 2, 1, 1);

        Text lblElementHeight = new Text("Breite [m]:");
        gridElementOperations.add(lblElementHeight, 0, 3, 1, 1);
        gridElementOperations.add(txtElementHeight, 1, 3, 1, 1);

        Text lblElementX = new Text("PositionX [m]:");
        gridElementOperations.add(lblElementX, 0, 4, 1, 1);
        gridElementOperations.add(txtElementX, 1, 4, 1, 1);

        Text lblElementY = new Text("PositionY [m]:");
        gridElementOperations.add(lblElementY, 0, 5, 1, 1);
        gridElementOperations.add(txtElementY, 1, 5, 1, 1);

        gridElementOperations.add(btnEdit, 1, 6);
        gridElementOperations.add(btnDelete, 0, 6);

        GridPane gridLayout = new GridPane();

        gridLayout.add(gridDataOperations, 0, 0);
        gridLayout.add(gridViewOperations, 0, 1);
        gridLayout.add(gridElementOperations, 0, 2);
        gridLayout.setVgap(10);
        controls.getChildren().add(gridLayout);

        return controls;
    }

    public void resetElementProperties() {
        boxElementType.getSelectionModel().select(KEY_NONESELECTED);
        txtElementWidth.setText("");
        txtElementHeight.setText("");
        txtElementX.setText("");
        txtElementY.setText("");
        btnDelete.setVisible(false);
        btnEdit.setVisible(false);
    }

    public void enableEditElementProperties(boolean enableEdit) {
        if (enableEdit) {
            //enable to edit LayoutElements's values
            boxElementType.setDisable(false);
            txtElementWidth.setDisable(false);
            txtElementHeight.setDisable(false);
            txtElementX.setDisable(false);
            txtElementY.setDisable(false);
            btnDelete.setDisable(false);

            //Loading exact values
            txtElementWidth.setText(String.valueOf(selectedElement.getWidth()));
            txtElementHeight.setText(String.valueOf(selectedElement.getHeight()));
            txtElementX.setText(String.valueOf(selectedElement.getX()));
            txtElementY.setText(String.valueOf(selectedElement.getY()));

            //edit button has confirm function
            btnEdit.setText("Bestätigen");
            btnDelete.setVisible(false);
        } else {
            boxElementType.setDisable(true);
            txtElementWidth.setDisable(true);
            txtElementHeight.setDisable(true);
            txtElementX.setDisable(true);
            txtElementY.setDisable(true);

            // edit button gets edit function back
            btnDelete.setVisible(true);
            btnEdit.setText("Ändern");
        }
    }
}
