/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartmaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Phil
 */
public class ChartMaker extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent ChartMakerMain = FXMLLoader.load(getClass().getResource("ChartMakerMain1.fxml"));
        Scene scene = new Scene(ChartMakerMain);
        scene.getStylesheets().add(ChartMaker.class.getResource("ChartMakerMain.css").toExternalForm());
        stage.setScene(scene);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.show();     
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}