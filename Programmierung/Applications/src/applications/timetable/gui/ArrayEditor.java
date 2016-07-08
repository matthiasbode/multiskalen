/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author bode
 */
public class ArrayEditor<E> extends AbstractCellEditor
        implements TableCellEditor,
        ActionListener {

    ArrayList<E> choosenObjects;
    ArrayList<E> allObjects;
    JButton cell;

    public ArrayEditor(ArrayList<E> allEs) {
        this.allObjects = allEs;
    }

    public Object getCellEditorValue() {
        return choosenObjects;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        choosenObjects = (ArrayList<E>) table.getValueAt(row, column);
        if (choosenObjects == null) {
            cell = new JButton();
        } else {
            cell = new JButton(value.toString());
        }
        cell.addActionListener(this);
        return cell;
    }

    public void actionPerformed(ActionEvent e) {
        final JFrame f = new JFrame();
        f.setSize(300, 200);
        final GenericListModel<E> allLecturesModel = new GenericListModel<E>(allObjects);
        final JList jList = new JList(allLecturesModel);

        if (choosenObjects != null) {
            int[] selected = new int[choosenObjects.size()];
            for (int i = 0; i < choosenObjects.size(); i++) {
                E lecturer = choosenObjects.get(i);
                selected[i] = allObjects.indexOf(lecturer);
            }
            jList.setSelectedIndices(selected);
        }
        JScrollPane sp = new JScrollPane(jList);
        f.add(sp, BorderLayout.CENTER);
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                List<E> selectedValuesList = jList.getSelectedValuesList();
                choosenObjects.clear();
                choosenObjects.addAll(selectedValuesList);
                f.setVisible(false);
                cell.setText(choosenObjects.toString());
                cell.getParent().invalidate();
                cell.getParent().repaint();
            }
        });
        f.add(ok, BorderLayout.SOUTH);
        f.setVisible(true);
    }
}
