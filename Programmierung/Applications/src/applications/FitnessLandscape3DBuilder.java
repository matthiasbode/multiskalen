/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications;

import applications.functions.graphics3d.utils.IsoPalette;
import ga.basics.FitnessEvalationFunction;
import ga.fittnessLandscapeAnalysis.Kreis;
import ga.individuals.Individual;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import ga.listeners.IndividualEvent;
import ga.listeners.IndividualListener;
import ga.metric.Metric;
import java.awt.BorderLayout;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.JFrame;


/**
 *
 * @author bode
 */
public class FitnessLandscape3DBuilder<C extends Individual> implements IndividualListener<C> {

    LinkedHashMap<C, Point2D> pos = new LinkedHashMap<>();
    Metric<C> metric;
    FitnessEvalationFunction<C> env;

    public FitnessLandscape3DBuilder(Metric<C> metric, FitnessEvalationFunction<C> env) {
        this.metric = metric;
        this.env = env;
    }

    public void addPoint(C individual) {
        if (pos.size() == 0) {
            pos.put(individual, new Point2D.Double(0, 0));
            return;
        } else if (pos.size() == 1) {
            C a = pos.keySet().iterator().next();
            double distance = metric.distance(a, individual);
            pos.put(individual, new Point2D.Double(distance, 0));
            return;
        } else if (pos.size() == 2) {
            Iterator<C> iterator = pos.keySet().iterator();
            C a = iterator.next();
            C b = iterator.next();
            double distanceA = metric.distance(a, individual);
            double distanceB = metric.distance(b, individual);
            Point2D pA = pos.get(a);
            Point2D pB = pos.get(b);

            Kreis k1 = new Kreis(new Point2D.Double(pA.getX(), pA.getY()), distanceA);
            Kreis k2 = new Kreis(new Point2D.Double(pB.getX(), pB.getY()), distanceB);

            pos.put(individual, k1.getSchnittPunkte(k2)[0]);
            return;
        }

        ArrayList<C> others = new ArrayList<C>(pos.keySet());
        final HashMap<C, Double> distanceToIndividual = new HashMap<>();
        for (C other : others) {
            double distance1 = metric.distance(other, individual);
            distanceToIndividual.put(other, distance1);
        }

        Collections.sort(others, new Comparator<C>() {

            public int compare(C o1, C o2) {
                return Double.compare(distanceToIndividual.get(o1), distanceToIndividual.get(o1));
            }
        });
        C a = others.get(0);
        C b = others.get(1);
        C c = others.get(2);

        ArrayList<Point2D> possiblePoints = new ArrayList<Point2D>(6);
        possiblePoints.addAll(Arrays.asList(getSchnittPunkt(a, b, individual)));
        possiblePoints.addAll(Arrays.asList(getSchnittPunkt(a, c, individual)));
        possiblePoints.addAll(Arrays.asList(getSchnittPunkt(b, c, individual)));

        ArrayList<Point2D> minPoints = new ArrayList<Point2D>();
        double min = Double.POSITIVE_INFINITY;

        for (Point2D point2d0 : possiblePoints) {
            for (Point2D point2d1 : possiblePoints) {
                for (Point2D point2d2 : possiblePoints) {
                    double dist = 0;
                    dist += point2d0.distance(point2d1);
                    dist += point2d1.distance(point2d2);
                    dist += point2d2.distance(point2d0);
                    if (dist < min) {
                        minPoints.clear();
                        minPoints.add(point2d0);
                        minPoints.add(point2d1);
                        minPoints.add(point2d2);
                    }
                }
            }
        }

        Point2D p = new Point2D.Double((minPoints.get(0).getX() + minPoints.get(1).getX() + minPoints.get(2).getX()) / 3., (minPoints.get(0).getY() + minPoints.get(1).getY() + minPoints.get(2).getY()) / 3.);
        pos.put(individual, p);

    }

    Point2D[] getSchnittPunkt(C first, C second, C toAdd) {
        double r1 = metric.distance(first, toAdd);
        double r2 = metric.distance(second, toAdd);
        Point2D p1 = pos.get(first);
        Point2D p2 = pos.get(second);
        if (p1 == null || p2 == null) {
            throw new NoSuchElementException("Keine berechnete Position gefunden");
        }
        Kreis k1 = new Kreis(p1, r1);
        Kreis k2 = new Kreis(p2, r2);
        return k1.getSchnittPunkte(k2);
    }

     

    @Override
    public void finished() {
        JFrame f = new JFrame("FitnessLandscape");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Individual<C> individual : pos.keySet()) {
            if (individual.getFitness() < min) {
                min = individual.getFitness();
            }
            if (individual.getFitness() > max) {
                max = individual.getFitness();
            }
        }

        IsoPalette pal = new IsoPalette(min, max);
        pal.setFarbverlauf(IsoPalette.Palette.Rainbow);

//        FitnessLandscapePlotter<C> plotter = new FitnessLandscapePlotter<C>(pal, 1., pos, env);
        TriangulatedFitnessLandscapePlotter plotter = new TriangulatedFitnessLandscapePlotter(pal, 1., pos, env);

        f.add(plotter.getSimpleCanvas(), BorderLayout.CENTER);
        f.setVisible(true);
    }

    public void newIndividual(IndividualEvent<C> event) {
        addPoint(event.individual);
    }
}
