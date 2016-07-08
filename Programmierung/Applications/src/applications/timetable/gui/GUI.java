/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import ga.individuals.Individual;
import applications.timetable.xml.XMLImport;
import applications.timetable.model.Initialization;
import applications.timetable.model.ProblemDefinition;
import applications.timetable.model.TimeTableMatrix;
import applications.timetable.model.TimeTableMatrixCrossover;
import applications.timetable.model.TimeTableMatrixMutation;
import applications.timetable.model.TimeTableMetricPermutationBased;
import applications.timetable.model.TimeTableMetricEuclidian;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author bode
 */
public final class GUI {

    public static File file = new File("/home/bode/TimeTableProject/problemDefinition.xml");
    JSplitPane jsp;
    ProblemDefinition problemDefintion;
    JFrame f;
    JTabbedPaneWithCloseIcons tabbedPane;
    JButton startSimulation;

    public GUI(ProblemDefinition problemDefintion) {
        this.problemDefintion = problemDefintion;
        problemDefintion.test();
        init();
    }

    public void init() {
        f = new JFrame("TimeTableOptimization");
        f.setSize(900, 700);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        tabbedPane = new JTabbedPaneWithCloseIcons();
        tabbedPane.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("TAB_CLOSED".equals(evt.getPropertyName())) {
                    Component component = (Component) evt.getOldValue();
                    if (component instanceof ProblemComponent) {
                        ProblemComponent pc = (ProblemComponent) component;
                        XMLImport.exportXML(file, pc.problemDefinition, false);
                    }
                }
            }
        });
        jsp.setLeftComponent(buildStartLeftComponent());
        jsp.setRightComponent(tabbedPane);
        jsp.setDividerLocation(0.3);
        f.add(jsp);
        f.setVisible(true);
    }
    
    public JPanel buildStartLeftComponent(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        startSimulation = new JButton("Simulate");
        jPanel.add(startSimulation);
        startSimulation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<TimeTableMatrix> buildInitialPopulation = Initialization.buildInitialPopulation(problemDefintion, 100);
//                for (Individual<TimeTableMatrix> individual : buildInitialPopulation) {
//                    addTab("Individual", new TimeTableComponent(individual.chrom.getTimeTable(), problemDefintion));
//                }
//                TimeTableMatrixCrossover ttm = new TimeTableMatrixCrossover();
//                TimeTableMatrix recombine = ttm.recombine(buildInitialPopulation.get(0).chrom, buildInitialPopulation.get(1).chrom, 1.0);
//                addTab("Recombined", new TimeTableComponent(recombine.getTimeTable(), problemDefintion));
                
                addTab("Individual", new TimeTableComponent(buildInitialPopulation.get(0).getTimeTable(), problemDefintion));
                TimeTableMatrixMutation ttmm = new TimeTableMatrixMutation(4, 8);
                TimeTableMatrix mutate = ttmm.mutate(buildInitialPopulation.get(0), 1.0);
                TimeTableMetricPermutationBased metric = new TimeTableMetricPermutationBased(problemDefintion);
                double distance = metric.distance(buildInitialPopulation.get(0), mutate);
                addTab("Mutated: " +distance, new TimeTableComponent(mutate.getTimeTable(), problemDefintion));

            }
        });
        return jPanel;
    }


    public void addTab(String title, Component component) {
        tabbedPane.addTab(title, component);
        tabbedPane.setSelectedComponent(component);
        f.invalidate();
        f.repaint();
    }

    public void setLeft(Component component) {
        jsp.setLeftComponent(component);
        f.invalidate();
        f.repaint();
    }

    public ProblemDefinition getP() {
        return problemDefintion;
    }

    public void setP(ProblemDefinition p) {
        this.problemDefintion = p;
    }

    public static void main(String[] args) {
        ProblemDefinition problemDefintion = XMLImport.<ProblemDefinition>importXML(file, ProblemDefinition.class, false);
        GUI gui = new GUI(problemDefintion);
        ProblemComponent pc = new ProblemComponent(gui, problemDefintion);
//        ArrayList<Individual<TimeTableMatrix>> buildInitialPopulation = Initialization.buildInitialPopulation(problemDefintion, 100);
        gui.addTab("ProblemGenerator", pc);
    }
//    public static void main(String[] args) {
//        GUI gUI = new GUI();
//        ProblemDimensionsOfFreedom problem = Test.getProblem();
//        gUI.p = problem;
//        TimeTableMatrix timeTableMatrix = Test.getTimeTableMatrix(problem);
//        gUI.addTimeTable(problem.getClasses().get(0), timeTableMatrix.getTimeTable());
//    }
}
