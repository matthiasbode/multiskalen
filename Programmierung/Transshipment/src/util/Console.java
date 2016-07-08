/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 *
 * @author Matthias
 */
public class Console extends OutputStream {

    private TextArea output;

    public Console(TextArea ta) {
        this.output = ta;
    }

    @Override
    public void write(final int i) throws IOException {
    Platform.runLater(new Runnable() {
        public void run() {
            output.appendText(String.valueOf((char) i));
        }
    });
    }
}
