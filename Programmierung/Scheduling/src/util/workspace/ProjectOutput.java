/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.workspace;

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
        return create(ws.getFolder());
    }

    public static File create(String newname) {
        WorkingSpace ws = new WorkingSpace();
        File f = new File(ws.getFolder(), newname);
        f.mkdirs();
        if (!f.exists()) {
            throw new NoSuchElementException("Pfad nicht erstellbar: " + f.getAbsolutePath());
        }
        return f;
    }

    public static File create(File folder) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String longToFormattedDateString = format.format(new Date(System.currentTimeMillis()));
        File f = new File(folder, longToFormattedDateString);
        f.mkdirs();
        if (!f.exists()) {
            throw new NoSuchElementException("Pfad nicht erstellbar: " + f.getAbsolutePath());
        }
        return f;
    }
}
