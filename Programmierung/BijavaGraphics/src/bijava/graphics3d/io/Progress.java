package bijava.graphics3d.io;

//------------------------------------------------------------------------------//
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 *
 * @author RedEye
 */
//------------------------------------------------------------------------------//
public class Progress extends JFrame {

    private JProgressBar pb;

//------------------------------------------------------------------------------//
    /**
     *
     * @param name
     */
//,,,,,,,,,,,,,,,,,,,,,,------------------------------------------------------------------------------//
    public Progress(String name) {
        super(name);
        setSize(640, 75);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        pb = new JProgressBar(0, 100);
        pb.setStringPainted(true);
        add(pb);
        setVisible(true);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @return
     */
//------------------------------------------------------------------------------//
    public JProgressBar getPb() {
        return pb;
    }
}