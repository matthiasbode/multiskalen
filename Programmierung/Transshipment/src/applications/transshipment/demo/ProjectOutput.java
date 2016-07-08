/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.WorkingSpace;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 *
 * @author bode
 */
public class ProjectOutput {

    public static File create() {
        WorkingSpace ws = new WorkingSpace();
        return create(ws.getFolder(), null);
    }

    public static File create(String prefix) {
        WorkingSpace ws = new WorkingSpace();
        return create(ws.getFolder(), prefix);
    }

    public static File create(File folder, String prefix) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String longToFormattedDateString = ((prefix != null) ? prefix : "") + format.format(new Date(System.currentTimeMillis()));
        File f = new File(folder, longToFormattedDateString);
        f.mkdirs();
        if (!f.exists()) {
            throw new NoSuchElementException("Pfad nicht erstellbar: " + f.getAbsolutePath());
        }
        return f;
    }
}
