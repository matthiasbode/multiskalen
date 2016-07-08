package applications.transshipment;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;

/**
 *
 * @author hofmann
 */
public class WorkingSpace {

    private String path;
    private Preferences prefs;
    private static String Key_PATH = "PATH";

    public WorkingSpace(String projectPath) {
        this();
        this.setPath(projectPath);
    }

    public WorkingSpace() {
        prefs = Preferences.userNodeForPackage(WorkingSpace.class);
        this.path = this.prefs.get(Key_PATH, "");

        if (this.getPath() == null || this.getPath().equals("")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Workingspace setzen");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File workingspace = chooser.getSelectedFile();
                try {
                    this.setPath(workingspace.getCanonicalPath());
                } catch (IOException ex) {
                    Logger.getLogger(GA_GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
       }
    }

    public String getPath() {
        return path;
    }

    public File getFolder() {
        return new File(path);
    }

    public void setPath(String path) {
        // Pfad normalisieren
        File f = new File(path);
        try {
            path = f.getCanonicalPath();
        } catch (IOException e) {
        }

        this.prefs.put(Key_PATH, path);
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

}
