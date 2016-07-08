/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartmaker;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import util.chart.heatmap.HeatMapDataSet;

/**
 *
 * @author
 * Phil
 */
public class SampleController implements Initializable {

    HeatMapDataSet<String, String> BarData;
    private Label label;
    private Stage popupStage;
    private Stage DataTableStage;
    private Popup popup;
    private Text popupText;
    @FXML
    private ListView listView;
    @FXML
    private BarChart<String, Double> barChart;
    @FXML
    private CheckBox checkTrans;

    /**
     * Handles
     * the
     * Action
     * for
     * the
     * Main
     * Frame
     * LoadData
     * Button
     *
     *
     */
    @FXML
    private void handleLoadDataButtonAction(ActionEvent event) throws FileNotFoundException, IOException {
        Stage chooserStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(chooserStage);
        System.out.println(file.getName());
        if (file != null) {
            if (file.getName().toLowerCase().endsWith(".csv")) {
                BarData = HeatMapDataSet.readFromCSV(file);
                showDataInList();
                showPopup("Data sucessfully Loaded");
            } else {
                showPopup("Data must be CSV");
            }
        }
    }

    @FXML
    private void showDataInList() {
        if (checkTrans.isSelected()) {
            ObservableList<String> columns = FXCollections.observableArrayList(BarData.getCategories());
            listView.setItems(columns);
        } else {
            ObservableList<String> columns = FXCollections.observableArrayList(BarData.getIdentifiers());
            listView.setItems(columns);
        }
    }

    @FXML
    private void listClicked(MouseEvent event) {
        int index = listView.getSelectionModel().getSelectedIndex();
        System.out.println("index nr. " + index + " is selected");
        System.out.println(listView.getItems().get(index).toString());
        String caption = listView.getItems().get(index).toString();
        setChart(caption);
    }

    private void setChart(String caption) {
        barChart.setTitle(caption);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName(caption);
        if (checkTrans.isSelected()) {
            for (String ide : BarData.getIdentifiers()) {
                series1.getData().add(new XYChart.Data(ide, BarData.getValue(ide, caption)));
            }
        } else {
            for (String cat : BarData.getCategories()) {
                series1.getData().add(new XYChart.Data(cat, BarData.getValue(caption, cat)));
            }
        }
        barChart.getData().clear();
        barChart.getData().add(series1);
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) throws IOException {
        Stage chooserStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(barChart.getTitle() + ".png");
        File file = fileChooser.showSaveDialog(chooserStage);
        saveBarChart(file);
    }

    private void saveBarChart(File file) {
        file = checkFileForSpecialChars(file);
        WritableImage result = barChart.snapshot(null, null);
        while (result.getProgress() != 1.) {
            //Wait for it...
        }
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(result, null);

        if (file != null) {
            File imageFile;
            if (file.getName().toLowerCase().endsWith(".png")) {
                imageFile = file;
            } else {
                imageFile = new File(file.getAbsolutePath() + ".png");
                System.out.println(file.getParent());
            }
            try {
                ImageIO.write(
                        renderedImage,
                        "png",
                        imageFile);
                showPopup("Image saved");
            } catch (IOException ex) {
                Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void handleSaveAllButtonAction(ActionEvent event) {
        Stage chooserStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(";TITLE;.png");
        File file = fileChooser.showSaveDialog(chooserStage);
        String initial = barChart.getTitle();
        int i = 0;
        if (checkTrans.isSelected()) {
            for (String cat : BarData.getCategories()) {
                File newFile = changeFileForSaveAll(file, cat);
                setChart(cat);
                saveBarChart(newFile);
            }
        } else {
            for (String ide : BarData.getIdentifiers()) {
                if (i == 0) {
                    File newFile = changeFileForSaveAll(file, ide);
                    setChart(ide);
                    saveBarChart(newFile);
                    i++;
                }
                else{
                    
                }
            }
        }
        setChart(initial);
    }

    private File changeFileForSaveAll(File file, String title) {
        String test = file.getName();
        String[] testo = test.split(";TITLE;");
        String newName = "";
        for (int i = 0; i < testo.length - 1; i++) {
            newName += testo[i];
            newName += title;
        }
        newName += testo[testo.length - 1];
        File result = new File(file.getParent() + "/" + newName);
        System.out.println(file.getAbsolutePath());
        return result;

    }

    private File checkFileForSpecialChars(File file) {
        String newName = "";
        String oldName = file.getName();
        String[] div = oldName.split(":");
        if (div.length != 1) {
            for (int i = 0; i < div.length - 1; i++) {
                newName += div[i] + "_";
            }
        }
        newName += div[div.length - 1];
        System.out.println(newName);
        return new File(file.getParent(), newName);
    }

    @FXML
    private void handleShowDataButtonAction(ActionEvent event) {
        System.out.println("ShowDataButtonClicked");
        initDataTable();
        DataTableStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BarData = new HeatMapDataSet<>();
        popupStage = new Stage();
        DataTableStage = new Stage();
        try {
            initPopup();
            // TODO
        } catch (IOException ex) {
            Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initPopup() throws IOException {
        Parent popupParent = FXMLLoader.load(getClass().getResource("Popup.fxml"));
        Scene popupScene = new Scene(popupParent);
        popupStage.setScene(popupScene);
    }

    private void showPopup(String text) {
        PopupController.popupText.setText(text);
        popupStage.show();
    }

    private void initDataTable() {
        ObservableList<String> columns = FXCollections.observableArrayList();
        TableView view = new TableView(columns);
        view.getColumns().add(new TableColumn("Identifiers"));
        for (String cat : BarData.getCategories()) {
            view.getColumns().add(new TableColumn(cat));
            for (String ide : BarData.getIdentifiers()) {
                System.out.println(ide);
                columns.add(ide);
                columns.add("" + BarData.getValue(ide, cat));
            }
        }
        view.setItems(columns);
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        Button ok = new Button("ok");
        hbox.getChildren().add(ok);
        vbox.getChildren().addAll(view, hbox);
        Scene dataTableScene = new Scene(vbox);
        DataTableStage.setScene(dataTableScene);
    }
}
