/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartmaker;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML
 * Controller
 * class
 *
 * @author
 * philippthiele
 */
public class PopupController implements Initializable {
    @FXML 
    private javafx.scene.control.Button okButton;
    public static Text popupText;
    /**
     * Initializes
     * the
     * controller
     * class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    @FXML
    private void handleOKButtonAction(Event event){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
    public void setPopupText(String text){
        popupText.setText(text);
    }
}
