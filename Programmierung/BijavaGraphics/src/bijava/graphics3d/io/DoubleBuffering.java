package bijava.graphics3d.io;

import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

//------------------------------------------------------------------------------//
/**
 *
 * @author RedEye
 */
//------------------------------------------------------------------------------//
public class DoubleBuffering extends JFrame implements ActionListener {

    private JPanel jp_center;
    private JLabel jl;
    private ArrayList<BufferedImage> images;
    private JFrame videoFrame;
    private JButton okBu;
    private JTextField delayField;
    private long delay = 200l;
    Graphics bufferGraphics;
    Image offscreen;

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    public DoubleBuffering() {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File("C:/"));
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        File[] files = null;
        int returnVal = jfc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = jfc.getSelectedFiles();
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF Images", "jpg", "gif", "ser", "jpeg");
        jfc.setFileFilter(filter);

        String[] dateinamen = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            dateinamen[i] = files[i].getAbsolutePath();
        }

        String dateiname = "";
        String absolutpath = "";
        String en = "";
        if (files.length == 1) {
            dateiname = files[0].getName();
            absolutpath = files[0].getParent() + "\\";
            en = jfc.getTypeDescription(files[0]);
        }
        initView();
        this.images = new ArrayList<BufferedImage>(1);

        int in_pun = 0;
        int[] in_zahl = new int[10];
        if (!en.equals("SER-Datei") && files.length == 1) {
            in_pun = dateiname.indexOf(".");

            in_zahl[0] = dateiname.indexOf("0");
            in_zahl[1] = dateiname.indexOf("1");
            in_zahl[2] = dateiname.indexOf("2");
            in_zahl[3] = dateiname.indexOf("3");
            in_zahl[4] = dateiname.indexOf("4");
            in_zahl[5] = dateiname.indexOf("5");
            in_zahl[6] = dateiname.indexOf("6");
            in_zahl[7] = dateiname.indexOf("7");
            in_zahl[8] = dateiname.indexOf("8");
            in_zahl[9] = dateiname.indexOf("9");

            int min_index = 0;
            for (int i = 0; i < in_zahl.length; i++) {
                if (in_zahl[i] != -1) {
                    min_index = i;
                    break;
                }
            }
            for (int i = 1; i < in_zahl.length; i++) {
                if (in_zahl[i] != -1) {
                    //TODO   nochmal alles ueberpruefen
                    if (Math.sqrt(in_zahl[min_index] * in_zahl[min_index]) > in_zahl[i] || in_zahl[i] == 0) {
                        min_index = i;
                    }
                }
            }
            String zahl = dateiname.substring(in_zahl[min_index], in_pun);
            int l = zahl.getBytes().length;
            char[] c = new char[l];
            zahl.getChars(0, l, c, 0);

            int z = Character.getNumericValue(c[0]);
            int start_zahl = Integer.parseInt(zahl);

            String name = "";
            String ende = "";

            if (z == 0) {
                name = absolutpath + dateiname.substring(0, in_zahl[min_index] + 1);
                ende = dateiname.substring(in_pun);
            } else {
                name = absolutpath + dateiname.substring(0, in_zahl[min_index]);
                ende = dateiname.substring(in_pun);
            }
            readImage(name, ende, start_zahl);
        } else if (en.equals("SER-Datei")) {
            readSer(dateiname);
        }

        if (files.length > 1) {
            readImages(dateinamen);
        }
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param dateiname
     * @param delay
     */
//------------------------------------------------------------------------------//
    public DoubleBuffering(String dateiname, long delay) {
        this.images = new ArrayList<BufferedImage>();
        this.delay = delay;
        initView();
        readSer(dateiname);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param images
     */
//------------------------------------------------------------------------------//
    public DoubleBuffering(ArrayList<BufferedImage> images) {
        this.images = images;
        initView();
        Progress f = new Progress("Bilder werden vorbereitet");
        double ii = 0;
        for (int i = 0; i < images.size(); i++) {
            jp_center.prepareImage(images.get(i), this);
            int a = (int) ii / images.size() * 100;
            f.getPb().setValue(a);
            f.repaint();
        }
        setLoca();
        f.dispose();
        setVisible(true);
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    public void setLoca() {
        setSize(images.get(0).getWidth() + 150, images.get(0).getHeight() + 37);
        setLocationRelativeTo(null);
    }

//------------------------------------------------------------------------------//
    /**
     * 
     */
//------------------------------------------------------------------------------//
    public void initView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = new Container();
        c.setLayout(new BorderLayout());
        add(c);
        jp_center = new JPanel();

        JPanel jp_west = new JPanel();
        jp_west.setLayout(new GridLayout(5, 1));

        JButton jb_start = new JButton("Start der Animation");
        jb_start.setActionCommand("start");
        jb_start.addActionListener(this);

        JButton jb_add = new JButton("Bilder Hinzufuegen");
        jb_add.setActionCommand("add");
        jb_add.addActionListener(this);

        JButton jb_option = new JButton("Optionen");
        jb_option.setActionCommand("option");
        jb_option.addActionListener(this);

        JLabel[] la = new JLabel[2];
        for (int i = 0; i < la.length; i++) {
            la[i] = new JLabel();
        }

        jp_west.add(jb_add);
        jp_west.add(la[0]);
        jp_west.add(jb_start);
        jp_west.add(la[1]);
        jp_west.add(jb_option);

        c.add(jp_west, BorderLayout.WEST);
        c.add(jp_center, BorderLayout.CENTER);
        jl = new JLabel();
        jp_center.add(jl);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param i
     */
//------------------------------------------------------------------------------//
    public void paintImage(int i) {
        ImageIcon ico = new ImageIcon(images.get(i).getScaledInstance(jp_center.getWidth(), jp_center.getHeight(), Image.SCALE_DEFAULT));

        jl.setIcon(ico);
        jp_center.revalidate();
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param dateiname
     */
//------------------------------------------------------------------------------//
    public void readSer(String dateiname) {
        ArrayList<SerializableImages> images2 = ArrayIO.collectionAusDateiLesen(dateiname);
//        Progress f = new Progress("Bilder werden vorbereitet");
//        double ii = 0;
        int anz = images2.size();
        for (int i = 0; i < anz; i++) {
            addImage(images2.get(i).getImage());
//            int a = (int) ii / anz * 100;
//            f.getPb().setValue(a);
//            f.repaint();
        }
        setLoca();
//        f.dispose();
        System.out.println("fertig!!!");
        setVisible(true);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param dateiname
     * @param endung
     * @param startzahl
     */
//------------------------------------------------------------------------------//
    public void readImage(String dateiname, String endung, int startzahl) {
        ArrayList<BufferedImage> list = ArrayIO.collectionAusImage(dateiname, endung, startzahl);

        Progress f = new Progress("Bilder werden vorbereitet");
        double ii = 0;
        int anz = list.size();
        for (int i = 0; i < anz; i++) {
            addImage(list.get(i));
            int a = (int) ii / anz * 100;
            f.getPb().setValue(a);
            f.repaint();
        }
        setLoca();
        f.dispose();
        System.out.println("fertig!!!");
        setVisible(true);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param dateiname
     */
//------------------------------------------------------------------------------//
    public void readImages(String[] dateiname) {
        ArrayList<BufferedImage> list = ArrayIO.collectionAusImages(dateiname);
        Progress f = new Progress("Bilder werden vorbereitet");
        double ii = 0;
        for (int i = 0; i < list.size(); i++) {
            addImage(list.get(i));
            int a = (int) ii / list.size() * 100;
            f.getPb().setValue(a);
            f.repaint();
        }
        setLoca();
        f.dispose();
        System.out.println("fertig!!!");
        setVisible(true);
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param bi
     */
//------------------------------------------------------------------------------//
    public void addImage(BufferedImage bi) {
        this.images.add(bi);
//        jp_center.prepareImage(bi, this);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param delay
     * @return
     */
//------------------------------------------------------------------------------//
    public SwingWorker<Void, Void> getSwingWorker(final long delay) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                for (int i = 0; i < images.size(); i++) {
                    paintImage(i);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DoubleBuffering.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        };
        return worker;
    }

//------------------------------------------------------------------------------//
    /**
     * 
     * @param delay
     */
//------------------------------------------------------------------------------//
    public void setDelay(long delay) {
        this.delay = delay;
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    public void initOptionFrame() {
        videoFrame = new JFrame("Video Optionen");
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel panel_2 = new JPanel(new GridLayout(1, 2));

        videoFrame.setSize(200, 100);
        videoFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        videoFrame.setLocationRelativeTo(null);

        okBu = new JButton("Ok");
        okBu.addActionListener(this);

        JLabel label = new JLabel("Delay-Time");

        delayField = new JTextField();
        delayField.setText("" + delay);
        delayField.addActionListener(this);

        panel_2.add(label);
        panel_2.add(delayField);
        panel.add(panel_2);
        panel.add(okBu);

        videoFrame.add(panel);
        videoFrame.setVisible(true);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    public void getBuffer() {
        offscreen = jp_center.createImage(jp_center.getWidth(), jp_center.getHeight());
        bufferGraphics = offscreen.getGraphics();
    }

    @Override
    public void paint(Graphics g) {
        getBuffer();
        g.drawImage(offscreen, 0, 0, this);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param e
     */
//------------------------------------------------------------------------------//
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("start")) {
            SwingWorker<Void, Void> worker = getSwingWorker(delay);
            worker.execute();
            return;
        }
        if (cmd.equals("add")) {

            return;
        }
        if (cmd.equals("option")) {
            initOptionFrame();
            return;
        }
        Object ob = e.getSource();

        if (ob == okBu) {
            String m = delayField.getText();
            delay = Long.parseLong(m);
            videoFrame.dispose();
            return;
        }

        if (ob == delayField) {
            return;
        }
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param args
     */
//------------------------------------------------------------------------------//
    public static void main(String[] args) {
//        DoubleBuffering db = new DoubleBuffering("C:/bilder0.jpg", 200);
//        db.readImage("C:/bilder", ".jpg", 0);
    }
}
