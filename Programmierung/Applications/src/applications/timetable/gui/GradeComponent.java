/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.Grade;
import applications.timetable.model.Lecturer;
import applications.timetable.model.Module;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author bode
 */
public class GradeComponent extends JPanel {
    
    private Grade g;
    private ArrayList<Module> modules;
    private JList moduleList;
    private JPanel center;
    final GenericListModel<Module> moduleModel;
    private ArrayList<Lecturer> allLecturers;
    
    public GradeComponent(Grade g, final ArrayList<Module> modules,   final ArrayList<Lecturer> allLecturers) {
        super(new BorderLayout());
        this.g = g;
        this.modules = modules;
        this.allLecturers = allLecturers;
        center = new JPanel();
        center.setLayout(new BorderLayout());
        
        JPanel modulePanel = new JPanel(new BorderLayout());
        modulePanel.setPreferredSize(new Dimension(300, 500));
        Collections.<Module>sort(modules);
        moduleModel = new GenericListModel<Module>(modules);
        moduleList = new JList(moduleModel);
        JScrollPane spModules = new JScrollPane(moduleList);
        modulePanel.add(spModules, BorderLayout.CENTER);
        
        moduleList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.getButton() == 3) {
                    int index = moduleList.locationToIndex(evt.getPoint());
                    moduleList.setSelectedIndex(index);
                    showMenu(moduleList, evt);
                }
                if (evt.getClickCount() == 2) {
                    Module selectedValue = (Module) moduleList.getSelectedValue();
                    ModuleComponent mc = new ModuleComponent(selectedValue, allLecturers);
                    center.removeAll();
                    buildNewModulePanel();
                    center.add(mc, BorderLayout.CENTER);
                    
                    GradeComponent.this.getParent().invalidate();
                    GradeComponent.this.getParent().repaint();
                }
            }
        });
        
        
        buildNewModulePanel();
        
        
        this.add(modulePanel, BorderLayout.WEST);
        this.add(center, BorderLayout.CENTER);
    }
    
    public <E> void showMenu(JList list, MouseEvent evt) {
        final GenericListModel model = (GenericListModel) list.getModel();
        final E selectedItem = (E) list.getSelectedValue();
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem item = new JMenuItem("Löschen");
        item.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                model.remove(selectedItem);
            }
        });
        menu.add(item);
        menu.show(list, evt.getX(), evt.getY());
    }

    private void buildNewModulePanel() {
        JPanel newModule = new JPanel();
        newModule.setLayout(new BoxLayout(newModule, BoxLayout.Y_AXIS));
        newModule.add(new JLabel("<html><h1>Neues Modul</h1></html>"));
        final JTextField moduleName = new JTextField(10);
        final JTextField numberOfStudents = new JTextField(3);
        final JTextField numberOfLessons = new JTextField(3);
        final JButton selectLectureres = new JButton("Bitte Dozenten wählen");
        JButton ok = new JButton("+");
        JPanel insertLine = new JPanel();
        insertLine.setLayout(new BoxLayout(insertLine, BoxLayout.X_AXIS));
        insertLine.add(moduleName);
        insertLine.add(numberOfStudents);
        insertLine.add(numberOfLessons);
        insertLine.add(selectLectureres);
        insertLine.add(ok);
        newModule.add(insertLine);
        final ArrayList<Lecturer> lecturers = new  ArrayList<Lecturer>();
        
        selectLectureres.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                final JFrame f = new JFrame();
                f.setSize(300, 200);
                final GenericListModel<Lecturer> allLecturesModel = new GenericListModel<Lecturer>(allLecturers);
                final JList jList = new JList(allLecturesModel);
                int[] selected = new int[lecturers.size()];
                for (int i = 0; i < lecturers.size(); i++) {
                    Lecturer lecturer = lecturers.get(i);
                    selected[i] = allLecturers.indexOf(lecturer);
                }
                jList.setSelectedIndices(selected);


                JScrollPane sp = new JScrollPane(jList);
                f.add(sp, BorderLayout.CENTER);
                JButton ok = new JButton("OK");
                ok.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        List<Lecturer> selectedValuesList = jList.getSelectedValuesList();
                        lecturers.clear();
                        lecturers.addAll(selectedValuesList);
                        f.setVisible(false);
                        selectLectureres.setText(lecturers.toString());
                        selectLectureres.getParent().invalidate();
                        selectLectureres.getParent().repaint();
                    }
                });
                f.add(ok, BorderLayout.SOUTH);
                f.setVisible(true);
            }
        });
        
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String name = moduleName.getText();
                int numS = Integer.parseInt(numberOfStudents.getText());
                int numL = Integer.parseInt(numberOfLessons.getText());
                Module m = new Module(name, lecturers, numL, numS);
                moduleModel.add(m);
                m.addGrade(g);
                GradeComponent.this.getParent().invalidate();
                GradeComponent.this.getParent().repaint();
            }
        });
                
        center.add(newModule, BorderLayout.NORTH);
    }
}
