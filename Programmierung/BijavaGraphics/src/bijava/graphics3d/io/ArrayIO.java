package bijava.graphics3d.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Eine Klasse die IO-Methoden f&uuml;r das Einlesen von Images aus
 * Bilddateien (JPEG,...) oder ArrayLists aus .SER-Dateien
 * 
 * @author RedEye
 */
public abstract class ArrayIO {

//------------------------------------------------------------------------------//
    /**
     * Die Methode speichert die Objekte einer ArrayList in einer Datei...
     * Die Elemente der ArrayList m&uuml;ssen Serializable sein
     * @param <E>
     * @param list
     * @param datei
     */
//------------------------------------------------------------------------------//
    public static <E> void collectionInDateiSchreiben(Collection<E> list, String datei) {
        try {
            ObjectOutputStream aus = new ObjectOutputStream(new FileOutputStream(datei));
            aus.writeObject(list);
            aus.flush();
            aus.close();
        } catch (IOException ex) {
            System.out.println("ArrayIO : " + ex);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * Die Methode liest eine Datei .ser aus und speichert die Objekte in einer ArrayList
     * @param <E>
     * @param datei
     * @return
     */
//------------------------------------------------------------------------------//
    public static <E> ArrayList<E> collectionAusDateiLesen(String datei) {
        ArrayList<E> ar = new ArrayList<E>();

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(datei));
            ar = (ArrayList) in.readObject();
            in.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Speichersdatei (noch) nicht vorhanden!");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return ar;
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param dateiname
     * @param endung
     * @param start
     * @return
     */
//------------------------------------------------------------------------------//
    public static ArrayList<BufferedImage> collectionAusImage(String dateiname, String endung, int start) {
        ArrayList<BufferedImage> ar = new ArrayList<BufferedImage>();
        int i = start;
        try {
            while (ImageIO.read(new File(dateiname + i + endung)) != null) {
                i++;
            }
        } catch (IOException ex) {
            System.out.println("kein bild mehr");
        }
        Progress f = new Progress("Bilder werden geladen");

        double anz = i - start;
        double ii = 0;
        i = start;
        try {
            while (ImageIO.read(new File(dateiname + i + endung)) != null) {
                BufferedImage bi = ImageIO.read(new File(dateiname + i + endung));
                ar.add(bi);
                int a = (int) (ii / anz * 100);
                f.getPb().setValue(a);
                f.repaint();
                ii++;
                i++;
            }
        } catch (IOException ex) {
            System.out.println("kein bild mehr");
        }
        f.dispose();
        return ar;
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param dateinamen
     * @return
     */
//------------------------------------------------------------------------------//
    public static ArrayList<BufferedImage> collectionAusImages(String[] dateinamen) {
        ArrayList<BufferedImage> ar = new ArrayList<BufferedImage>();
        Progress f = new Progress("Bilder werden geladen");
        int anz = dateinamen.length;
        double ii = 0.;
        for (int i = 0; i < anz; i++) {
            try {
                BufferedImage bi = ImageIO.read(new File(dateinamen[i]));
                ar.add(bi);
                int a = (int) (ii / anz * 100);
                f.getPb().setValue(a);
                f.repaint();
                ii++;
            } catch (IOException ex) {
                System.out.println("kein bild mehr");
            }
        }
        f.dispose();
        return ar;
    }

    public static BufferedImage readImage(String dateiname) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File(dateiname));
        } catch (IOException ex) {
            System.out.println("ArrayIO getImage : " + ex);
        }
        return bi;
    }

    public static void writeImage(BufferedImage bi, String dateiname) {
        try {
            ImageIO.write(bi, "GIF", new File(dateiname));
        } catch (IOException ex) {
            System.out.println("ArrayIO getImage : " + ex);
        }
    }

    public static ImageIcon getImageIcon(String datei) {
        ImageIcon im = new ImageIcon(datei);
        return im;
    }
}
