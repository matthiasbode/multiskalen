package applications.timetable.gui;

 
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 *
 * To add a tab, use the method addTab(String, Component)
 *
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file type) use
 * the method addTab(String, Component, Icon). Only clicking the 'X' closes the tab.
 */
public class JTabbedPaneWithCloseIcons extends JTabbedPane implements MouseListener, PropertyChangeListener {
    public JTabbedPaneWithCloseIcons() {
        super();
        this.setUI(new TabbedPaneUI(SwingUtilities.LEFT));
        addMouseListener(this);
    }

    @Override
    public void addTab(String title, Component component) {
        this.addTab(title, component, null);
    }

    public void addTab(String title, Component component, Icon extraIcon) {
        super.addTab(title, new CloseTabIcon(extraIcon), component);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
        if (tabNumber < 0) {
            return;
        }
        Rectangle rect = ((CloseTabIcon) getIconAt(tabNumber)).getBounds();
        if (rect.contains(e.getX(), e.getY())) {
            Component component = getComponentAt(tabNumber);
            firePropertyChange("TAB_CLOSED", component, null);
            this.removeTabAt(tabNumber);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("modified")) {
            //Es muss der momentan aktive Tab gewesen sein, der das
            //Event "modified" gesendet hat.

            if ((Boolean) (evt.getNewValue())) {
                this.setForegroundAt(this.getSelectedIndex(), Color.blue);
            }
            if (!(Boolean) (evt.getNewValue())) {
                this.setForegroundAt(this.getSelectedIndex(), Color.black);
            }
            this.invalidate();
            this.repaint();
        }
    }

    class TabbedPaneUI extends BasicTabbedPaneUI {

        private Rectangle selIconRect;
        private int horizontalTextPosition = SwingUtilities.LEFT;

        public TabbedPaneUI() {
        }

        public TabbedPaneUI(int horTextPosition) {
            horizontalTextPosition = horTextPosition;
        }

        public Rectangle getSelectedIconRect() {
            return selIconRect;
        }

        @Override
        protected void layoutLabel(int tabPlacement, FontMetrics metrics,
                int tabIndex, String title, Icon icon, Rectangle tabRect,
                Rectangle iconRect, Rectangle textRect, boolean isSelected) {

            textRect.x = 0;
            textRect.y = 0;
            iconRect.x = 0;
            iconRect.y = 0;
            SwingUtilities.layoutCompoundLabel((JComponent) tabPane, metrics,
                    title, icon, SwingUtilities.CENTER, SwingUtilities.CENTER,
                    SwingUtilities.CENTER, horizontalTextPosition, tabRect, iconRect,
                    textRect, textIconGap + 2);

            selIconRect = iconRect;
        }
    }

    /**
     * The class which generates the 'X' icon for the tabs. The constructor
     * accepts an icon which is extra to the 'X' icon, so you can have tabs
     * like in JBuilder. This value is null if no extra icon is required.
     */
    class CloseTabIcon implements Icon {

        private int x_pos;
        private int y_pos;
        private int width;
        private int height;
        private Icon fileIcon;

        public CloseTabIcon(Icon fileIcon) {
            this.fileIcon = fileIcon;
            width = 16;
            height = 16;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            this.x_pos = x;
            this.y_pos = y;

            Color col = g.getColor();

            g.setColor(Color.black);
            int y_p = y + 2;
            g.drawLine(x + 1, y_p, x + 12, y_p);
            g.drawLine(x + 1, y_p + 13, x + 12, y_p + 13);
            g.drawLine(x, y_p + 1, x, y_p + 12);
            g.drawLine(x + 13, y_p + 1, x + 13, y_p + 12);
            g.drawLine(x + 3, y_p + 3, x + 10, y_p + 10);
            g.drawLine(x + 3, y_p + 4, x + 9, y_p + 10);
            g.drawLine(x + 4, y_p + 3, x + 10, y_p + 9);
            g.drawLine(x + 10, y_p + 3, x + 3, y_p + 10);
            g.drawLine(x + 10, y_p + 4, x + 4, y_p + 10);
            g.drawLine(x + 9, y_p + 3, x + 3, y_p + 9);
            g.setColor(col);
            if (fileIcon != null) {
                fileIcon.paintIcon(c, g, x + width, y_p);
            }
        }

        @Override
        public int getIconWidth() {
            return width + (fileIcon != null ? fileIcon.getIconWidth() : 0);
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        public Rectangle getBounds() {
            return new Rectangle(x_pos, y_pos, width, height);
        }
    }
 
}


