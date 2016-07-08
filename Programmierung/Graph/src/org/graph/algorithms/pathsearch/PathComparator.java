/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms.pathsearch;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedPath;
import org.util.Pair;

/**
 *
 * @author Nils Rinke
 */
public class PathComparator<E, B extends EdgeWeight<B>> implements Comparator<WeightedPath<E, B>> {
//    /**
//     *
//     */
//    ArrayList<WeightedPath<E, B>> shortestPaths;
//
//    
//    public PathComparator(ArrayList<WeightedPath<E, B>> shortestPaths) {
//        this.shortestPaths = shortestPaths;
//    }
//
//    
//    @Override
//    public int compare(WeightedPath<E, B> p1, WeightedPath<E, B> p2) {
//        if (shortestPaths.size() == 0) {
//            return p1.getWeight().compareTo(p2.getWeight());
//        }
//        if (p1.getWeight().equals(p1.getWeight().getNullElement())) {
//            if (p2.getWeight().equals(p2.getWeight().getNullElement())) {
//                return 0;
//            }
//            return 1;
//        } else if (p2.getWeight().equals(p2.getWeight().getNullElement())) {
//            return -1;
//        }
//
//        double edgeOverlapping = getProcentualOverlap(p1);
//        double weight = shortestPaths.get(0).getWeight().weightToDouble()
//                / p1.getWeight().weightToDouble();
//        double c1 = edgeOverlapping * weight;
//
//        edgeOverlapping = getProcentualOverlap(p2);
//        weight = shortestPaths.get(0).getWeight().weightToDouble()
//                / p2.getWeight().weightToDouble();
//        double c2 = edgeOverlapping * weight;
//        if (c1 > c2) {
//            return -1;
//        }
//        if (c1 == c2) {
//            return 0;
//        } else {
//            return +1;
//        }
//    }
//
//    private double getProcentualOverlap(WeightedPath<E, B> path) {
//        ArrayList<Double> overlapping = new ArrayList<Double>();
//        double edgeOverlapping = 0;
//        for (WeightedPath<E, B> weightedPath : shortestPaths) {
//            int overlap = 0;
//            for (Edge<E, E> edge : weightedPath.getPathEdges()) {
//                if (path.getPathEdges().contains(edge)) {
//                    overlap++;
//                }
//            }
//            edgeOverlapping += 1. * overlap / weightedPath.getNumberOfEdges();
//            overlapping.add(1. * overlap / weightedPath.getNumberOfEdges());
//        }
//        return 1 - (edgeOverlapping / shortestPaths.size());
//    }
    
    Collection<WeightedPath<E, B>> shortestPaths;

        public PathComparator(Collection<WeightedPath<E, B>> shortestPaths) {
            this.shortestPaths = shortestPaths;
        }


        public int compare(WeightedPath<E, B> p1, WeightedPath<E, B> p2) {
            if(shortestPaths.isEmpty())
                return p1.getWeight().compareTo(p2.getWeight());
            if(p1.getWeight().equals(p1.getWeight().getNullElement())) {
                if(p2.getWeight().equals(p2.getWeight().getNullElement()))
                    return 0;
                return 1;
            } else if(p2.getWeight().equals(p2.getWeight().getNullElement())) {
                return -1;
            }

            double edgeOverlapping = getProcentualOverlap(p1);
//            System.out.println("overlap: " + edgeOverlapping);
            double weight = shortestPaths.iterator().next().getWeight().doubleValue()/
                            p1.getWeight().doubleValue();
//            System.out.println("weight: " + weight);
            double c1 = edgeOverlapping*weight;

            edgeOverlapping = getProcentualOverlap(p2);
            weight = shortestPaths.iterator().next().getWeight().doubleValue()/
                     p2.getWeight().doubleValue();
            double c2 = edgeOverlapping*weight;
            if(c1 > c2)
                return -1;
            if(c1 == c2)
                return 0;
            else
                return +1;
        }


        private double getProcentualOverlap(WeightedPath<E, B> path) {
            ArrayList<Double> overlapping = new ArrayList<Double>();
            double edgeOverlapping = 0;
            for (WeightedPath<E, B> weightedPath : shortestPaths) {
                int overlap=0;
                for (Pair<E, E> edge : weightedPath.getPathEdges()) {
                    if(path.getPathEdges().contains(edge))
                        overlap++;
                }
                edgeOverlapping += 1.*overlap/weightedPath.getNumberOfEdges();
                overlapping.add(1.*overlap/weightedPath.getNumberOfEdges());
            }
            return 1-(edgeOverlapping/shortestPaths.size());
//            return 1-Collections.max(overlapping);
        }
        
}
