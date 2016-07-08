package project;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

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
    }

    public String getPath() {
        return path;
    }

    public File getFolder(){
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
