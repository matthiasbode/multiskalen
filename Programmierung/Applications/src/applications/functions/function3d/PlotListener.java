/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions.function3d;

import applications.functions.graphics3d.SimpleCanvas3D;
import ga.individuals.Individual;
import ga.listeners.IndividualEvent;
import ga.listeners.IndividualListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author bode
 */
public class PlotListener<E extends Individual> implements IndividualListener<E>, ActionListener {

    public TreeMap<Integer, ArrayList<E>> recombinationIndividuals = new TreeMap<>();
    public TreeMap<Integer, ArrayList<E>> localSearchIndividuals = new TreeMap<>();
    int numberOfGeneration = 0;
    IndividualPlotter plotter;
    JButton forward;
    JButton backward;
    IndividualEvent.StatusIndividualEvent currentStatus = IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL;
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentStatus == IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL) {
            currentStatus = IndividualEvent.StatusIndividualEvent.NEW_LS_INDIVIDUAL;
        } else {
            currentStatus = IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL;
        }
        if (e.getSource().equals(forward)) {
            plotter.addIndividuals(++numberOfGeneration, currentStatus);
        }
        if (e.getSource().equals(backward)) {
            plotter.addIndividuals(--numberOfGeneration, currentStatus);
        }
    }

    

    @Override
    public void finished() {
        JFrame f = new JFrame("Funktion");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plotter = new IndividualPlotter(this);
        SimpleCanvas3D canvas = plotter.createCanvas();
        f.add(canvas, BorderLayout.CENTER);

        JPanel control = new JPanel();
        forward = new JButton("->");
        forward.addActionListener(this);

        backward = new JButton("<-");
        backward.addActionListener(this);

        control.add(backward);
        control.add(forward);
        f.add(control, BorderLayout.SOUTH);
        f.setVisible(true);

        int i = 0;
        for (Integer pop : recombinationIndividuals.keySet()) {
            ArrayList<E> arrayList = recombinationIndividuals.get(pop);
            System.out.println("Recombination Generation: " + i++);
            for (Individual<E> individual : arrayList) {
                System.out.println(individual);
            }
        }

        i = 0;
        for (Integer pop : localSearchIndividuals.keySet()) {
            ArrayList<E> arrayList = localSearchIndividuals.get(pop);
            System.out.println("Recombination Generation: " + i++);
            for (Individual<E> individual : arrayList) {
                System.out.println(individual);
            }
        }
    }

    @Override
    public void newIndividual(IndividualEvent<E> event) {
        if (event.status == IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL) {
            if (recombinationIndividuals.get(event.populationNumber) == null) {
                recombinationIndividuals.put(event.populationNumber, new ArrayList<E>());
            }
            recombinationIndividuals.get(event.populationNumber).add(event.individual);
        }
        if (event.status == IndividualEvent.StatusIndividualEvent.NEW_LS_INDIVIDUAL) {
            if (localSearchIndividuals.get(event.populationNumber) == null) {
                localSearchIndividuals.put(event.populationNumber, new ArrayList<E>());
            }
            localSearchIndividuals.get(event.populationNumber).add(event.individual);
        }
    }
}
