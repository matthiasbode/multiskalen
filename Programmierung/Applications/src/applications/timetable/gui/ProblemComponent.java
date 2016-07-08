/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.Grade;
import applications.timetable.model.Lecturer;
import applications.timetable.model.Period;
import applications.timetable.model.ProblemDefinition;
import applications.timetable.model.Room;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
public class ProblemComponent extends JPanel {

    public ProblemDefinition problemDefinition;

    
    private GUI gui;
    private JList lecturerList;
    private JList gradeList;
    private JList roomList;
    
    private JTextField newLecturerField;
    private JTextField newGradeField;
    private JTextField newGradeFieldAmount;
    private JTextField newRoomFieldName;
    private JTextField newRoomFieldAmount;
    
    private JButton openFreeRoomPlaner;

    
    public ProblemComponent(final GUI gui, final ProblemDefinition problemDefinition) {
        super(new BorderLayout());
        this.gui = gui;
        this.problemDefinition = problemDefinition;
        
        ArrayList<Grade> classes = new ArrayList<Grade>(problemDefinition.getModules().keySet());

        JPanel lecturerPanel = new JPanel(new BorderLayout());
        Collections.<Lecturer>sort(problemDefinition.getLecturers());
        final GenericListModel<Lecturer> lectureListModel = new GenericListModel<Lecturer>(problemDefinition.getLecturers());
        lecturerList = new JList(lectureListModel);
        JScrollPane spLecturer = new JScrollPane(lecturerList);
        lecturerPanel.add(spLecturer, BorderLayout.CENTER);
        newLecturerField = new JTextField();
        JButton addLecturer = new JButton("+");
        addLecturer.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String name = newLecturerField.getText();
                        Lecturer l = new Lecturer(name);
                        lectureListModel.add(l);
                        ProblemComponent.this.invalidate();
                        ProblemComponent.this.repaint();
                    }
                });
        lecturerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.getButton() == 3) {
                    int index = lecturerList.locationToIndex(evt.getPoint());
                    lecturerList.setSelectedIndex(index);
                    showMenu(lecturerList, evt);
                }
            }
        });
        JPanel addNewLecturer = new JPanel();
        addNewLecturer.setLayout(new BoxLayout(addNewLecturer, BoxLayout.X_AXIS));
        addNewLecturer.add(newLecturerField);
        addNewLecturer.add(addLecturer);
        lecturerPanel.add(addNewLecturer, BorderLayout.SOUTH);


        
        JPanel roomPanel = new JPanel(new BorderLayout());
        
        openFreeRoomPlaner = new JButton("Raumverfügungsplan");
        openFreeRoomPlaner.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                gui.addTab("Raumplaner", new FreeRoomsComponent(new PeriodJTableModel(problemDefinition)));
            }
        });
        roomPanel.add(openFreeRoomPlaner, BorderLayout.NORTH);
        
        Collections.<Room>sort(problemDefinition.getRooms());
        final GenericListModel<Room> roomListModel = new GenericListModel<Room>(problemDefinition.getRooms());
        roomList = new JList(roomListModel);
        JScrollPane spRoom = new JScrollPane(roomList);
        roomPanel.add(spRoom, BorderLayout.CENTER);
        newRoomFieldName = new JTextField();
        newRoomFieldAmount = new JTextField();
        JButton addRoom = new JButton("+");
        addRoom.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String name = newRoomFieldName.getText();
                        int anz = Integer.parseInt(newRoomFieldAmount.getText());
                        Room r = new Room(name, anz);
                        roomListModel.add(r);
                        ProblemComponent.this.invalidate();
                        ProblemComponent.this.repaint();
                    }
                });
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.getButton() == 3) {
                    int index = roomList.locationToIndex(evt.getPoint());
                    roomList.setSelectedIndex(index);
                    showMenu(roomList, evt);
                }
            }
        });
         JPanel addNewRoom = new JPanel();
        addNewRoom.setLayout(new BoxLayout(addNewRoom, BoxLayout.X_AXIS));
        addNewRoom.add(newRoomFieldName);
        addNewRoom.add(newRoomFieldAmount);
        addNewRoom.add(addRoom);
        roomPanel.add(addNewRoom, BorderLayout.SOUTH);
//        
        
        
        JPanel gradePanel = new JPanel(new BorderLayout());
        Collections.<Grade>sort(classes);
        final GenericListModel<Grade> gradeListModel = new GenericListModel<Grade>(classes);
        gradeList = new JList(gradeListModel);
        JScrollPane spGrade = new JScrollPane(gradeList);
        gradePanel.add(spGrade, BorderLayout.CENTER);
        newGradeField = new JTextField(8);
        newGradeFieldAmount = new JTextField(3);
        JButton newGradeAdd = new JButton("+");
        newGradeAdd.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        String name = newGradeField.getText();
                        int amount = Integer.parseInt(newGradeFieldAmount.getText());
                        Grade g = new Grade(name, amount);
                        gradeListModel.add(g);
                        ProblemComponent.this.invalidate();
                        ProblemComponent.this.repaint();
                    }
                });
        
        gradeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Grade g = (Grade)gradeList.getSelectedValue();
                    gui.addTab(g.getId(), new GradeComponent(g, problemDefinition.getModules().get(g), problemDefinition.getLecturers()));
                }
            }
        });
        JPanel addNewGrade = new JPanel();
        addNewGrade.setLayout(new BoxLayout(addNewGrade, BoxLayout.X_AXIS));
        addNewGrade.add(newGradeField);
        addNewGrade.add(newGradeFieldAmount);
        addNewGrade.add(newGradeAdd);
        gradePanel.add(addNewGrade, BorderLayout.SOUTH);
        
        
        
        this.add(lecturerPanel, BorderLayout.WEST);
        this.add(roomPanel, BorderLayout.CENTER);
        this.add(gradePanel, BorderLayout.EAST);
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

    

   
}
